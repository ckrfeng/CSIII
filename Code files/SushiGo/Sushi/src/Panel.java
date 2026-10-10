import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel: draws the table and hands.
 * - Uses pixel-rotation helpers in Card (rotate90/180/270 + drawRotated)
 * - No AffineTransform usage
 * - Middle area enlarged; center cards in rows (one row per player)
 * - Left/right cards rotated so top faces center
 * - Tighter spacing
 */
public class Panel extends JPanel implements MouseListener {
    private final Game game;
    private final int CARD_W = 80;
    private final int CARD_H = 120;
    private final int GAP = 6;
    private final int SMALL_GAP = 6;
    private final int SIDE_MARGIN = 28;

    private final JButton doneBtn;
    private final List<Card> selected = new ArrayList<>();

    public Panel(Game game) {
        this.game = game;
        setBackground(new Color(0xF7FBFA));
        setLayout(null);
        addMouseListener(this);

        doneBtn = new JButton("Done");
        doneBtn.setBounds(20, 20, 80, 30);
        doneBtn.addActionListener(e -> onDone());
        add(doneBtn);
    }

    private void onDone() {
        Player cur = game.getCurrentPlayer();
        if (cur == null) return;

        // play selected cards for current player
        for (Card c : new ArrayList<>(selected)) {
            game.playCard(cur, c);
            if (cur.hand != null) cur.hand.remove(c);
        }
        selected.clear();

        // advance game
        game.endTurn();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Draw center area (bigger)
        int centerW = Math.min(700, w - 160);
        int centerH = Math.min(520, h - 160);
        int cx = (w - centerW) / 2;
        int cy = (h - centerH) / 2;

        g.setColor(Color.WHITE);
        g.fillRoundRect(cx, cy, centerW, centerH, 14, 14);
        g.setColor(Color.GRAY);
        g.drawRoundRect(cx, cy, centerW, centerH, 14, 14);

        // Draw center table rows (one row per player), cards in a row (not stacked)
        drawCenterPlayedArea(g, cx, cy, centerW, centerH);

        // Draw players' hands (player index -> seat: 0 bottom, 1 right, 2 top, 3 left)
        for (int i = 0; i < game.players.size(); i++) {
            drawPlayerHand(g, i);
        }
    }

    private void drawCenterPlayedArea(Graphics2D g, int cx, int cy, int w, int h) {
        int rows = game.players.size();
        int rowHeight = CARD_H + SMALL_GAP;
        for (int i = 0; i < rows; i++) {
            List<Card> table = game.players.get(i).table;
            if (table == null || table.isEmpty()) {
                // draw placeholder box for empty slot (rotated for side seats)
                int centerX = cx + w * (i + 1) / (rows + 1);
                int centerY = cy + h / 2;
                int tw = CARD_W, th = CARD_H;
                int deg = seatToAngle(i);
                // use a temporary Card to draw a rotated back placeholder, avoid AffineTransform
                Card tmp = new Card("back");
                tmp.drawRotated((Graphics2D) g, centerX - tw / 2, centerY - th / 2, tw, th, false, deg);
                continue;
            }
            int totalW = table.size() * (CARD_W + SMALL_GAP) - SMALL_GAP;
            int startX = cx + (w - totalW) / 2;
            int rowY = cy + SMALL_GAP + i * rowHeight;
            int deg = seatToAngle(i);
            for (int j = 0; j < table.size(); j++) {
                Card c = table.get(j);
                c.drawRotated(g, startX + j * (CARD_W + SMALL_GAP), rowY, CARD_W, CARD_H, true, deg);
            }
        }
    }

    /** Map seat index to rotation degrees so top of the card faces center:
     * seat 0 (bottom) -> 0 deg
     * seat 1 (right) -> 270 deg (top faces left)
     * seat 2 (top) -> 180 deg
     * seat 3 (left) -> 90 deg
     */
    private int seatToAngle(int seat) {
        switch (seat) {
            case 0: return 0;
            case 1: return 270;
            case 2: return 180;
            case 3: return 90;
            default: return 0;
        }
    }

    private void drawPlayerHand(Graphics2D g, int playerIndex) {
        int w = getWidth(), h = getHeight();
        Player p = game.players.get(playerIndex);
        List<Card> hand = p.hand;
        if (hand == null) return;
        int seat = playerIndex;

        if (seat == 0) {
            // bottom, centered
            int totalW = Math.max(0, hand.size() * (CARD_W + GAP) - GAP);
            int startX = (w - totalW) / 2;
            int y = h - SIDE_MARGIN - CARD_H;
            for (int i = 0; i < hand.size(); i++) {
                Card c = hand.get(i);
                int x = startX + i * (CARD_W + GAP);
                c.drawRotated(g, x, y, CARD_W, CARD_H, true, seatToAngle(seat));
            }
        } else if (seat == 2) {
            // top, centered
            int totalW = Math.max(0, hand.size() * (CARD_W + GAP) - GAP);
            int startX = (w - totalW) / 2;
            int y = SIDE_MARGIN;
            for (int i = 0; i < hand.size(); i++) {
                Card c = hand.get(i);
                int x = startX + i * (CARD_W + GAP);
                c.drawRotated(g, x, y, CARD_W, CARD_H, true, seatToAngle(seat));
            }
        } else if (seat == 3) {
            // left, vertical, rotated 90 deg
            int totalH = Math.max(0, hand.size() * (CARD_H + GAP) - GAP);
            int startY = (h - totalH) / 2;
            int x = SIDE_MARGIN;
            for (int i = 0; i < hand.size(); i++) {
                Card c = hand.get(i);
                int y = startY + i * (CARD_H + GAP);
                c.drawRotated(g, x, y, CARD_W, CARD_H, true, seatToAngle(seat));
            }
        } else if (seat == 1) {
            // right, vertical, rotated 270 deg
            int totalH = Math.max(0, hand.size() * (CARD_H + GAP) - GAP);
            int startY = (h - totalH) / 2;
            int x = getWidth() - SIDE_MARGIN - CARD_W;
            for (int i = 0; i < hand.size(); i++) {
                Card c = hand.get(i);
                int y = startY + i * (CARD_H + GAP);
                c.drawRotated(g, x, y, CARD_W, CARD_H, true, seatToAngle(seat));
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // allow selection only from the current player's hand
        Player cur = game.getCurrentPlayer();
        if (cur == null || cur.hand == null) return;
        int cp = game.currentPlayerIndex;
        int mx = e.getX(), my = e.getY();
        int seat = cp;
        int w = getWidth(), h = getHeight();

        if (seat == 0) {
            int totalW = Math.max(0, cur.hand.size() * (CARD_W + GAP) - GAP);
            int startX = (w - totalW) / 2;
            int y = h - SIDE_MARGIN - CARD_H;
            for (int i = 0; i < cur.hand.size(); i++) {
                int x = startX + i * (CARD_W + GAP);
                Rectangle r = new Rectangle(x, y, CARD_W, CARD_H);
                if (r.contains(mx, my)) {
                    Card clicked = cur.hand.get(i);
                    if (selected.contains(clicked)) selected.remove(clicked);
                    else selected.add(clicked);
                    repaint();
                    return;
                }
            }
        } else if (seat == 2) {
            int totalW = Math.max(0, cur.hand.size() * (CARD_W + GAP) - GAP);
            int startX = (w - totalW) / 2;
            int y = SIDE_MARGIN;
            for (int i = 0; i < cur.hand.size(); i++) {
                int x = startX + i * (CARD_W + GAP);
                Rectangle r = new Rectangle(x, y, CARD_W, CARD_H);
                if (r.contains(mx, my)) {
                    Card clicked = cur.hand.get(i);
                    if (selected.contains(clicked)) selected.remove(clicked);
                    else selected.add(clicked);
                    repaint();
                    return;
                }
            }
        } else if (seat == 3) {
            int totalH = Math.max(0, cur.hand.size() * (CARD_H + GAP) - GAP);
            int startY = (h - totalH) / 2;
            int x = SIDE_MARGIN;
            for (int i = 0; i < cur.hand.size(); i++) {
                int y = startY + i * (CARD_H + GAP);
                Rectangle r = new Rectangle(x, y, CARD_W, CARD_H);
                if (r.contains(mx, my)) {
                    Card clicked = cur.hand.get(i);
                    if (selected.contains(clicked)) selected.remove(clicked);
                    else selected.add(clicked);
                    repaint();
                    return;
                }
            }
        } else if (seat == 1) {
            int totalH = Math.max(0, cur.hand.size() * (CARD_H + GAP) - GAP);
            int startY = (h - totalH) / 2;
            int x = getWidth() - SIDE_MARGIN - CARD_W;
            for (int i = 0; i < cur.hand.size(); i++) {
                int y = startY + i * (CARD_H + GAP);
                Rectangle r = new Rectangle(x, y, CARD_W, CARD_H);
                if (r.contains(mx, my)) {
                    Card clicked = cur.hand.get(i);
                    if (selected.contains(clicked)) selected.remove(clicked);
                    else selected.add(clicked);
                    repaint();
                    return;
                }
            }
        }
    }

    // Unused mouse events
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
