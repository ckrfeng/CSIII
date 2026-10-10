import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;

public class Panel extends JPanel implements MouseListener, KeyListener {
    private Game state;
    private final int WIDTH = 1200, HEIGHT = 800;
    public Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        state = new Game();
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
    }

    // main entry
    public static void main(String[] args) {
        JFrame f = new JFrame("Sushi Go - Simple Pass & Play");
        Panel p = new Panel();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(p);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        // draw header
        g.setColor(new Color(30,30,30));
        g.drawString("Round: " + state.round + (state.isGameOver() ? "  (Game Over)" : ""), 10, 20);
        g.drawString("Current Player: " + (state.currentPlayer().id+1), 200, 20);
        // draw players' tableau top area
        int areaW = getWidth()/4;
        for (int i=0;i<state.players.size();i++) {
            Player p = state.players.get(i);
            int x0 = i*areaW + 10;
            int areaTop = 40;
            g.setColor(new Color(200,200,200));
            g.fillRect(x0, areaTop, areaW-20, 140);
            g.setColor(Color.BLACK);
            g.drawRect(x0, areaTop, areaW-20, 140);
            g.drawString("Player " + (p.id+1) + "  Score:" + p.score, x0+8, areaTop+16);
            // draw tableau cards as small backs
            int cx = x0 + 8, cy = areaTop+28;
            int cw = 50, ch = 70;
            int pad = 6;
            for (int j=0;j<p.table.size();j++) {
                Card.drawBack(g, cx + (j%5)*(cw+pad), cy + (j/5)*(ch+pad), cw, ch);
            }
        }

        if (state.isGameOver()) {
            g.setFont(new Font("SansSerif", Font.BOLD, 28));
            g.drawString("Game Over!", getWidth()/2 - 80, getHeight()/2 - 40);
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
            g.drawString(state.winnerText(), getWidth()/2 - 160, getHeight()/2);
            return;
        }

        // draw current player's hand large at bottom
        List<Card> hand = state.currentHand();
        int cardW = 120, cardH = 160, gap = 12;
        int totalW = hand.size()*cardW + Math.max(0, hand.size()-1)*gap;
        int startX = (getWidth()-totalW)/2;
        int y = getHeight() - cardH - 60;
        // draw each card (face up)
        for (int i=0;i<hand.size();i++) {
            Card c = hand.get(i);
            boolean sel = state.stagedCardsFor(state.currentPlayer().id).contains(c);
            c.draw((Graphics2D)g, startX + i*(cardW+gap), y, cardW, cardH, sel);
        }

        // draw confirm button
        int bx = getWidth()-240, by = getHeight()-120, bw=200, bh=40;
        g.setColor(new Color(240,240,240));
        g.fillRoundRect(bx, by, bw, bh, 12,12);
        g.setColor(Color.GRAY);
        g.drawRoundRect(bx, by, bw, bh, 12,12);
        g.setColor(Color.BLACK);
        g.drawString("Confirm (Enter) - End Turn", bx+20, by+26);

        // draw pass indicator / instructions
        g.drawString("Click a card to select/deselect. Then press Enter to confirm.", 10, getHeight()-20);
    }

    // map click to hand card index
    private int handCardAt(int mx, int my) {
        List<Card> hand = state.currentHand();
        int cardW = 120, gap = 12, cardH=160;
        int totalW = hand.size()*cardW + Math.max(0, hand.size()-1)*gap;
        int startX = (getWidth()-totalW)/2;
        int y = getHeight() - cardH - 60;
        for (int i=0;i<hand.size();i++) {
            int x = startX + i*(cardW+gap);
            if (mx >= x && mx <= x+cardW && my >= y && my <= y+cardH) return i;
        }
        return -1;
    }

    @Override public void mouseClicked(MouseEvent e) {
        int idx = handCardAt(e.getX(), e.getY());
        if (idx >= 0) {
            state.toggleSelect(idx);
            repaint();
        } else {
            // check confirm button click
            int bx = getWidth()-240, by = getHeight()-120, bw=200, bh=40;
            if (e.getX() >= bx && e.getX() <= bx+bw && e.getY() >= by && e.getY() <= by+bh) {
                if (state.canConfirm()) state.confirmSelection();
                repaint();
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (state.canConfirm()) {
                state.confirmSelection();
                repaint();
            }
        }
    }
}