import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel {
    private final AppFrame parentRef;
    private final Runnable goMenu;
    private final GameState state;
    private int cardW = 120, cardH = 160, gap = 12;
    private Rectangle btnConfirm = new Rectangle(980, 620, 180, 40);
    private Rectangle btnChopsticks = new Rectangle(980, 670, 180, 40);
    private Rectangle btnMenu = new Rectangle(20, 20, 160, 36);

    public GamePanel(int players, Runnable goMenu) {
        this.goMenu = goMenu;
        this.parentRef = null;
        this.state = new GameState(players);
        setFocusable(true);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            Point p = e.getPoint();
            if (btnMenu.contains(p)) {
                int opt = JOptionPane.showConfirmDialog(this, "Quit to menu? Current game will be lost.", "Confirm", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) goMenu.run();
                return;
            }
            if (state.awaitingPassPrompt) {
                state.awaitingPassPrompt = false;
                repaint();
                return;
            }
            if (btnConfirm.contains(p)) {
                if (state.canConfirm()) {
                    state.confirmSelection();
                    repaint();
                }
                return;
            }
            if (btnChopsticks.contains(p)) {
                state.toggleUseChopsticks();
                repaint();
                return;
            }
            // detect click on hand cards
            List<Card> hand = state.currentHand();
            for (int i=0;i<hand.size();i++) {
                Rectangle r = handCardBounds(i, hand.size());
                if (r.contains(p)) {
                    state.selectCard(i);
                    repaint();
                    return;
                }
            }
        }
        super.processMouseEvent(e);
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode()==KeyEvent.VK_ENTER) {
                if (state.awaitingPassPrompt) {
                    state.awaitingPassPrompt = false;
                } else if (state.canConfirm()) {
                    state.confirmSelection();
                }
                repaint();
            }
        }
        super.processKeyEvent(e);
    }

    private Rectangle handCardBounds(int idx, int count) {
        int totalW = count * cardW + (count-1)*gap;
        int startX = (getWidth() - totalW)/2;
        int y = getHeight() - cardH - 40;
        int x = startX + idx*(cardW+gap);
        return new Rectangle(x, y, cardW, cardH);
    }

    private void drawButton(Graphics2D g, Rectangle r, String text) {
        g.setColor(new Color(240,240,240));
        g.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g.setColor(Color.GRAY);
        g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g.setColor(Color.BLACK);
        Font f = g.getFont().deriveFont(Font.BOLD, 14f);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(text))/2;
        int ty = r.y + (r.height + fm.getAscent() - fm.getDescent())/2;
        g.drawString(text, tx, ty);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(250, 248, 244));
        g.fillRect(0, 0, getWidth(), getHeight());

        // header
        drawButton(g, btnMenu, "Back to Menu");

        g.setColor(Color.DARK_GRAY);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.drawString("Round " + state.round, 200, 40);

        // Scores / puddings
        int baseY = 80;
        g.setFont(g.getFont().deriveFont(14f));
        for (Player p : state.players) {
            int y = baseY + p.id * 20;
            g.setColor(Color.BLACK);
            g.drawString("P" + (p.id+1) + ": " + p.score + " pts  |  Pudding: " + p.pudding, 20, y);
        }

        if (state.isGameOver()) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, 32f));
            g.setColor(new Color(30,30,30));
            g.drawString("Game Over", getWidth()/2 - 120, 200);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 22f));
            g.drawString(state.winnerText(), getWidth()/2 - 160, 240);
            g.setFont(g.getFont().deriveFont(16f));
            g.drawString("Click 'Back to Menu' to start again.", getWidth()/2 - 160, 280);
            return;
        }

        // Tableaus
        int tableTop = 120;
        int colW = getWidth() / state.playersCount;
        for (Player p : state.players) {
            int x0 = p.id * colW + 20;
            g.setColor(Color.GRAY);
            g.drawString("Player " + (p.id+1) + " tableau:", x0, tableTop-10);
            int x = x0, y = tableTop;
            int perRow = Math.max(1, (colW-40) / (cardW/2 + 6));
            int count = 0;
            for (Card c : p.tableau) {
                int cw = cardW/2, ch = cardH/2;
                int cx = x + (count % perRow) * (cw + 6);
                int cy = y + (count / perRow) * (ch + 6);
                c.paint(g, cx, cy, cw, ch, false);
                count++;
            }
        }

        // Hand and controls
        if (state.awaitingPassPrompt) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, 20f));
            g.setColor(new Color(40,40,40));
            g.drawString("Pass device to Player " + (state.currentPlayer().id+1) + ". Press SPACE or click to continue.", 260, 560);
            return;
        }

        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.drawString("Player " + (state.currentPlayer().id+1) + " – choose " + (state.usingChopsticksThisTurn ? "TWO" : "ONE") + " card" , 480, 560);

        List<Card> hand = state.currentHand();
        // staged
        java.util.List<Card> staged = new ArrayList<>(state.stagedSelections.getOrDefault(state.currentPlayer().id, java.util.Collections.emptyList()));

        for (int i=0;i<hand.size();i++) {
            Card c = hand.get(i);
            Rectangle r = handCardBounds(i, hand.size());
            boolean sel = staged.contains(c);
            c.paint(g, r.x, r.y, r.width, r.height, sel);
        }

        drawButton(g, btnConfirm, state.canConfirm() ? "Confirm (Enter)" : "Choose card(s)");
        drawButton(g, btnChopsticks, "Toggle Chopsticks");
    }
}
