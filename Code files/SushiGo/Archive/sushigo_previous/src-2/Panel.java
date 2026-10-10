import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel.java  — replacement
 *
 * Key features:
 * - Centered hands per side
 * - Counter-clockwise rotation of hands (bottom -> left -> top -> right)
 * - Selected card(s) visually offset until Done, then moved to player's table
 * - Larger center scoring area with per-player round scores
 * - Played cards in center displayed in a small "hand-like" layout and rotated so the top of the card faces the center
 * - Side cards rotated (left/right) so their top edge faces the center
 *
 * Minimal assumptions about your Game/Player API; see comments below where to adapt names.
 */
public class Panel extends JPanel implements MouseListener {
    private final Game game;
    private final int CARD_W = 80;
    private final int CARD_H = 120;
    private final int GAP = 6;
    private final int SMALL_GAP = 6;
    private final int SIDE_MARGIN = 24;

    // selection buffer while current player chooses
    private final List<Card> selected = new ArrayList<>();

    // seatOffset controls which seat each player occupies.
    // seating: 0 = bottom, 1 = right, 2 = top, 3 = left
    // For player at index i in game.players, seat = (i - seatOffset + 4) % 4
    // Increment seatOffset after each endTurn to rotate counter-clockwise (hands move left).
    private int seatOffset = 0;

    private final JButton doneBtn;
    private final JButton chopBtn;

    public Panel(Game game) {
        this.game = game;
        setLayout(null);
        setBackground(new Color(0xE9EFEF));
        setPreferredSize(new Dimension(1000, 750));

        doneBtn = new JButton("Done");
        doneBtn.setBounds(420, 680, 100, 36);
        doneBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDone();
            }
        });
        add(doneBtn);

        chopBtn = new JButton("Use Chopsticks");
        chopBtn.setBounds(540, 680, 160, 36);
        chopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // placeholder: you can implement chopsticks action here if desired
                // for now this button simply repaints (selection logic already allows 2 selections
                // if the player has a chopsticks card on their table).
                repaint();
            }
        });
        add(chopBtn);

        addMouseListener(this);
    }

    // Called when the player presses Done
    private void onDone() {
        Player cur = game.getCurrentPlayer();
        if (cur == null) return;

        if (selected.isEmpty()) return;

        // call game.playCard for each selected card; if playCard doesn't remove from hand,
        // we'll remove it here to avoid drawing duplicates.
        for (Card c : new ArrayList<>(selected)) {
            try {
                game.playCard(cur, c);
            } catch (Exception ex) {
                // if playCard signature differs, try reflection or just remove ourselves
                try {
                    Method m = game.getClass().getMethod("playCard", Player.class, Card.class);
                    m.invoke(game, cur, c);
                } catch (Exception e) {
                    // give up on reflective call
                }
            }
            // defend against duplicate-draw glitch: remove from hand if still present
            if (cur.hand.contains(c)) cur.hand.remove(c);
        }

        selected.clear();

        // End the turn in the game (advance current player). This should be present.
        try {
            game.endTurn();
        } catch (Exception ex) {
            // try reflection (best-effort)
            try {
                Method m = game.getClass().getMethod("endTurn");
                m.invoke(game);
            } catch (Exception e) { /* ignore */ }
        }

        // Rotate seatOffset to simulate passing hands counter-clockwise
        seatOffset = (seatOffset + 1) % 4;

        repaint();
    }

    // Helper: seat positions (center points) for each seat index (0..3)
    // seat 0 = bottom, 1 = right, 2 = top, 3 = left
    private Point seatCenter(int seat, int w, int h) {
        int cx = w / 2, cy = h / 2;
        int ex = Math.min(300, w / 4);
        int ey = Math.min(220, h / 4);

        switch (seat) {
            case 0: return new Point(cx, h - SIDE_MARGIN - CARD_H/2 - 80);
            case 1: return new Point(w - SIDE_MARGIN - CARD_W/2 - 80, cy);
            case 2: return new Point(cx, SIDE_MARGIN + CARD_H/2 + 20);
            case 3:
            default: return new Point(SIDE_MARGIN + CARD_W/2 + 20, cy);
        }
    }

    @Override
    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Draw background
        g.setColor(new Color(0xF7FBFA));
        g.fillRect(0, 0, w, h);

        // Draw center large scoring rectangle
        int centerW = Math.min(640, w - 160);
        int centerH = Math.min(520, h - 160);
        int cx = (w - centerW) / 2;
        int cy = (h - centerH) / 2;
        g.setColor(Color.WHITE);
        g.fillRoundRect(cx, cy, centerW, centerH, 14, 14);
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(cx, cy, centerW, centerH, 14, 14);

        // Draw title and round
        g.setFont(g.getFont().deriveFont(Font.BOLD, 16f));
        g.drawString("Round: " + safeGetGameRound(), cx + 12, cy + 22);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 13f));
        g.drawString("Round Scores:", cx + 12, cy + 44);

        // Draw per-player round scoring in center area
        int scoreY = cy + SMALL_GAP6;
        int lineH = 20;
        for (int i = 0; i < game.players.size(); i++) {
            Player p = game.players.get(i);
            int rscore = safeGetPlayerRoundScore(p);
            g.drawString("P" + p.id + ": " + rscore, cx + 12, scoreY + i * lineH);
        }

        // Draw played cards in center: for each player, show their table near the center,
        // arranged in a "fan" around the center so they are inside the big rectangle.
        drawCenterPlayedArea(g, cx, cy, centerW, centerH);

        // Draw each player's hand at their side — centered along that side
        for (int idx = 0; idx < game.players.size(); idx++) {
            Player p = game.players.get(idx);
            int seat = (idx - seatOffset + 4) % 4; // seat assignment
            drawPlayerSide(g, p, seat, w, h);
        }
    }

    // Draw each player's table (their played cards) in the center area.
    
private void drawCenterPlayedArea(Graphics2D g, int cx, int cy, int w, int h) {
        // Arrange each player's played cards in a horizontal row across the center rectangle.
        int centerY = cy + h/2;
        int n = Math.max(1, game.players.size());
        for (int idx = 0; idx < n; idx++) {
            Player p = game.players.get(idx);
            int seat = (idx - seatOffset + 4) % 4;
            // distribute across width with padding at ends
            int x = cx + (w * (idx + 1)) / (n + 1);
            drawCardsInHandStyle(g, p.table, x, centerY, seat, /*maxPerRow*/ 6, /*cardScale*/1.1);
        }
    }


    // Draw a list of cards arranged similarly to a hand, but centered at (centerX, centerY)
    // seat controls rotation so top of card faces center
    private void drawCardsInHandStyle(Graphics2D g, List<Card> cards, int centerX, int centerY, int seat, int maxPerRow, double scale) {
        
if (cards == null || cards.isEmpty()) {
            // draw empty placeholder; rotate for side seats so the top faces the center
            int w = (int) (CARD_W * scale), h = (int) (CARD_H * scale);
            double angle = seatToAngleForTopFacingCenter(seat);
            if (Math.abs(angle) > 1e-6) {
                AffineTransform old = g.getTransform();
                double cxp = centerX;
                double cyp = centerY;
                g.rotate(angle, cxp, cyp);
                g.setColor(new Color(0xF0F0F0));
                g.fillRoundRect((int)(centerX - w/2), (int)(centerY - h/2), w, h, 8, 8);
                g.setColor(Color.GRAY);
                g.drawRoundRect((int)(centerX - w/2), (int)(centerY - h/2), w, h, 8, 8);
                g.setTransform(old);
            } else {
                g.setColor(new Color(0xF0F0F0));
                g.fillRoundRect(centerX - w/2, centerY - h/2, w, h, 8, 8);
                g.setColor(Color.GRAY);
                g.drawRoundRect(centerX - w/2, centerY - h/2, w, h, 8, 8);
            }
            return;
        }


        // compute rows
        int perRow = Math.min(maxPerRow, cards.size());
        int rows = (cards.size() + perRow - 1) / perRow;
        int totalW = (int) (perRow * (CARD_W * scale) + (perRow - 1) * SMALL_GAP);
        int totalH = (int) (rows * (CARD_H * scale) + (rows - 1) * SMALL_GAP);

        // top-left of block
        int startX = centerX - totalW/2;
        int startY = centerY - totalH/2;

        // rotation angle so top of card faces center:
        double angle = seatToAngleForTopFacingCenter(seat);

        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < perRow; c++) {
                if (idx >= cards.size()) break;
                Card card = cards.get(idx++);
                int x = startX + c * ((int)(CARD_W*scale) + SMALL_GAP);
                int y = startY + r * ((int)(CARD_H*scale) + SMALL_GAP);
                drawCardRotated(g, card, x, y, (int)(CARD_W*scale), (int)(CARD_H*scale), angle);
            }
        }
    }

    // Draw the player's side hand, centered on the side
    private void drawPlayerSide(Graphics2D g, Player p, int seat, int w, int h) {
        Point center = seatCenter(seat, w, h);

        boolean vertical = (seat == 1 || seat == 3);

        List<Card> hand = p.hand;

        int count = hand.size();
        if (count == 0) return;

        // compute total width/height of hand and starting point so it's centered along side
        int totalW = vertical ? CARD_W : count * CARD_W + (count - 1) * GAP;
        int totalH = vertical ? (count * CARD_H + (count - 1) * GAP) : CARD_H;

        int startX = center.x - totalW / 2;
        int startY = center.y - totalH / 2;

        double angle = seatToAngleForTopFacingCenter(seat);

        // draw each card — if it's selected (current player's selected list) we draw it slightly offset outward
        for (int i = 0; i < count; i++) {
            Card card = hand.get(i);
            int x = startX + (vertical ? 0 : i * (CARD_W + GAP));
            int y = startY + (vertical ? i * (CARD_H + GAP) : 0);

            // If this card is currently selected by the active player, draw it offset outward a bit
            boolean isCurrentPlayer = (game.getCurrentPlayer() == p);
            boolean isSelected = isCurrentPlayer && selected.contains(card);

            int drawX = x;
            int drawY = y;

            if (isSelected) {
                // push the selected card slightly toward the center to show it's chosen
                int outward = 18;
                switch (seat) {
                    case 0: drawY -= outward; break; // bottom -> move up
                    case 1: drawX -= outward; break; // right -> move left
                    case 2: drawY += outward; break; // top -> move down
                    case 3: drawX += outward; break; // left -> move right
                }
            }

            // when it's NOT the current player's turn, draw backs (face-down)
            boolean faceUp = (game.getCurrentPlayer() == p);

            if (!faceUp) {
                // draw back image rotated accordingly
                Card.back = Card.back; // just to reference; Card.draw handles back when drawFront==false
                drawCardRotated(g, card, drawX, drawY, CARD_W, CARD_H, angle, false);
            } else {
                // draw front
                drawCardRotated(g, card, drawX, drawY, CARD_W, CARD_H, angle, true);
            }

            // draw player label near first card
            if (i == 0) {
                g.setColor(Color.BLACK);
                g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
                int labelX = startX - 8, labelY = startY - 8;
                if (seat == 0) { labelX = center.x - 20; labelY = startY - 12; }
                if (seat == 1) { labelX = startX + CARD_W + 8; labelY = center.y - 8; }
                if (seat == 2) { labelX = center.x - 20; labelY = startY - 18; }
                if (seat == 3) { labelX = startX - 60; labelY = center.y - 8; }
                g.drawString("P" + p.id + " (" + safeGetPlayerRoundScore(p) + ")", labelX, labelY);
            }
        }
    }

    // Draw card with rotation so the top of the card points toward the center (angle in radians).
    // Overloaded: drawFront true draws the card front, false draws back image.
    private void drawCardRotated(Graphics2D g, Card card, int x, int y, int w, int h, double angle) {
        drawCardRotated(g, card, x, y, w, h, angle, true);
    }

    private void drawCardRotated(Graphics2D g, Card card, int x, int y, int w, int h, double angle, boolean drawFront) {
        AffineTransform old = g.getTransform();

        // rotate around card center
        double cx = x + w / 2.0;
        double cy = y + h / 2.0;
        g.rotate(angle, cx, cy);

        // Draw: use Card.draw which expects top-of-card up image. Our rotation makes "top" face center.
        card.draw(g, x, y, w, h, drawFront);

        g.setTransform(old);
    }

    // Determine rotation angle (radians) so top of card faces center for given seat
    // seat 0 bottom: angle = 0 (top points up toward center)
    // seat 1 right: top should face left (angle = -90deg)
    // seat 2 top: top should face down (angle = 180deg)
    // seat 3 left: top should face right (angle = +90deg)
    private double seatToAngleForTopFacingCenter(int seat) {
        switch (seat) {
            case 0: return 0.0;
            case 1: return -Math.PI / 2.0;
            case 2: return Math.PI;
            case 3:
            default: return Math.PI / 2.0;
        }
    }

    // Utility: try to fetch game.round safely
    private int safeGetGameRound() {
        try {
            // try field
            java.lang.reflect.Field f = game.getClass().getField("round");
            return f.getInt(game);
        } catch (Exception ex) {
            // try method
            try {
                Method m = game.getClass().getMethod("getRound");
                Object o = m.invoke(game);
                if (o instanceof Number) return ((Number)o).intValue();
            } catch (Exception e) { }
        }
        return 0;
    }

    // Utility: try to get per-player round score. fallback to score field.
    private int safeGetPlayerRoundScore(Player p) {
        try {
            java.lang.reflect.Field f = p.getClass().getField("roundScore");
            return f.getInt(p);
        } catch (Exception ex) {
            try {
                Method m = p.getClass().getMethod("getRoundScore");
                Object o = m.invoke(p);
                if (o instanceof Number) return ((Number)o).intValue();
            } catch (Exception e) { }
        }

        try {
            java.lang.reflect.Field f2 = p.getClass().getField("score");
            return f2.getInt(p);
        } catch (Exception e) {
            // fallback
        }
        return 0;
    }

    // ---------- Mouse handling: select/deselect cards in current player's visible hand ----------
    @Override
    public void mouseClicked(MouseEvent e) {
        Player cur = game.getCurrentPlayer();
        if (cur == null) return;

        // map each player index to seat; find the seat of current player
        int currentIndex = -1;
        for (int i = 0; i < game.players.size(); i++) {
            if (game.players.get(i) == cur) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1) return;

        int seat = (currentIndex - seatOffset + 4) % 4;
        // compute where the current player's hand is drawn (same as drawPlayerSide)
        int w = getWidth(), h = getHeight();
        Point center = seatCenter(seat, w, h);
        boolean vertical = (seat == 1 || seat == 3);

        List<Card> hand = cur.hand;
        int count = hand.size();
        if (count == 0) return;

        int totalW = vertical ? CARD_W : count * CARD_W + (count - 1) * GAP;
        int totalH = vertical ? (count * CARD_H + (count - 1) * GAP) : CARD_H;

        int startX = center.x - totalW / 2;
        int startY = center.y - totalH / 2;

        int mx = e.getX(), my = e.getY();

        for (int i = 0; i < count; i++) {
            int x = startX + (vertical ? 0 : i * (CARD_W + GAP));
            int y = startY + (vertical ? i * (CARD_H + GAP) : 0);
            Rectangle r = new Rectangle(x, y, CARD_W, CARD_H);
            if (r.contains(mx, my)) {
                Card clicked = hand.get(i);

                // selection rules:
                // default: only 1 card. If player has chopsticks on their table, allow 2 selections.
                boolean hasChopsticks = false;
                for (Card c : cur.table) {
                    if ("chopsticks".equalsIgnoreCase(c.type)) { hasChopsticks = true; break; }
                }

                if (!selected.contains(clicked)) {
                    if (selected.isEmpty()) selected.add(clicked);
                    else {
                        if (hasChopsticks && selected.size() < 2) selected.add(clicked);
                        else {
                            // replace existing selection to enforce single selection
                            selected.clear();
                            selected.add(clicked);
                        }
                    }
                } else {
                    selected.remove(clicked);
                }
                repaint();
                return;
            }
        }
    }

    // unused mouse events
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
