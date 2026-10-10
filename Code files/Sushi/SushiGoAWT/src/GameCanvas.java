import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GameCanvas extends Canvas implements MouseListener, KeyListener, Runnable {
    private GameState state;
    private int players = 3;
    private Thread loop;
    private Font big = new Font("SansSerif", Font.BOLD, 18);
    public GameCanvas() {
        state = new GameState(players);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        loop = new Thread(this);
        loop.setDaemon(true);
        loop.start();
    }
    @Override public Dimension getPreferredSize(){ return new Dimension(1200,800); }
    public void paint(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        g.setColor(new Color(250,248,244));
        g.fillRect(0,0,getWidth(),getHeight());
        g.setFont(big);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Round " + state.round, 20, 40);
        // draw player areas (left-aligned columns). Put score and pudding above area to avoid overlap.
        int areaW = getWidth() / state.playersCount;
        int areaTop = 80;
        for (Player p : state.players) {
            int x0 = p.id * areaW + 10;
            // header: player name, score, puddings - draw inside header box above tableau
            g.setColor(new Color(230,230,230));
            g.fillRect(x0, areaTop-40, areaW-20, 36);
            g.setColor(Color.BLACK);
            g.drawString("Player " + (p.id+1), x0+8, areaTop-16);
            g.drawString("Score: " + p.score, x0+150, areaTop-16);
            g.drawString("Pud: " + p.pudding, x0+280, areaTop-16);
            // draw tableau cards below header, grid layout to avoid overlap
            int cx = x0, cy = areaTop;
            int cw = 80, ch = 110;
            int perRow = Math.max(1, (areaW-40)/(cw+8));
            int count=0;
            for (Card c : p.tableau) {
                int dx = cx + (count%perRow) * (cw+8);
                int dy = cy + 28 + (count/perRow) * (ch+8);
                c.draw(g, dx, dy, cw, ch, false);
                count++;
            }
        }
        // if game over, show winner panel center
        if (state.isGameOver()) {
            g.setFont(new Font("SansSerif", Font.BOLD, 32));
            g.setColor(new Color(30,30,30));
            g.drawString("Game Over", getWidth()/2 - 120, getHeight()/2 - 40);
            g.setFont(new Font("SansSerif", Font.PLAIN, 20));
            g.drawString(state.winnerText(), getWidth()/2 - 120, getHeight()/2);
            g.drawString("Restart the program to play again.", getWidth()/2 - 120, getHeight()/2 + 40);
            return;
        }
        // pass prompt
        if (state.awaitingPassPrompt) {
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            g.setColor(Color.BLACK);
            g.drawString("Pass device to Player " + (state.currentPlayer().id+1) + ". Click to continue.", 300, 560);
            return;
        }
        // draw current player's hand at bottom, centered
        List<Card> hand = state.currentHand();
        int cardW = 120, cardH = 160, gap = 12;
        int totalW = hand.size() * cardW + (hand.size()-1)*gap;
        int startX = (getWidth()-totalW)/2;
        int y = getHeight() - cardH - 40;
        // staged
        List<Card> staged = state.stagedSelections.getOrDefault(state.currentPlayer().id, Collections.emptyList());
        for (int i=0;i<hand.size();i++) {
            Card c = hand.get(i);
            int x = startX + i*(cardW+gap);
            boolean sel = staged.contains(c);
            c.draw((Graphics2D)g, x, y, cardW, cardH, sel);
        }
        // controls (draw simple rectangles)
        g.setColor(new Color(240,240,240));
        g.fillRoundRect(getWidth()-220, getHeight()-160, 200, 40, 12, 12);
        g.setColor(Color.GRAY);
        g.drawRoundRect(getWidth()-220, getHeight()-160, 200, 40, 12, 12);
        g.setColor(Color.BLACK);
        g.drawString("Confirm (Enter)", getWidth()-160, getHeight()-135);
        g.setColor(new Color(240,240,240));
        g.fillRoundRect(getWidth()-220, getHeight()-100, 200, 40, 12, 12);
        g.setColor(Color.GRAY);
        g.drawRoundRect(getWidth()-220, getHeight()-100, 200, 40, 12, 12);
        g.setColor(Color.BLACK);
        g.drawString("Toggle Chopsticks", getWidth()-170, getHeight()-75);
    }

    // utility to find which hand card was clicked
    private int handCardAt(int mx, int my) {
        if (state.awaitingPassPrompt) return -1;
        List<Card> hand = state.currentHand();
        int cardW = 120, gap = 12, cardH=160;
        int totalW = hand.size()*cardW + (hand.size()-1)*gap;
        int startX = (getWidth()-totalW)/2;
        int y = getHeight() - cardH - 40;
        for (int i=0;i<hand.size();i++) {
            int x = startX + i*(cardW+gap);
            if (mx >= x && mx <= x+cardW && my >= y && my <= y+cardH) return i;
        }
        // confirm button
        if (mx >= getWidth()-220 && mx <= getWidth()-20 && my >= getHeight()-160 && my <= getHeight()-120) {
            return -2;
        }
        // chopsticks
        if (mx >= getWidth()-220 && mx <= getWidth()-20 && my >= getHeight()-100 && my <= getHeight()-60) {
            return -3;
        }
        return -1;
    }

    @Override public void mouseClicked(MouseEvent e) {
        if (state.awaitingPassPrompt) {
            state.awaitingPassPrompt = false;
            repaint(); return;
        }
        int idx = handCardAt(e.getX(), e.getY());
        if (idx >= 0) {
            state.selectCard(idx); repaint(); return;
        } else if (idx == -2) {
            if (state.canConfirm()) { state.confirmSelection(); repaint(); }
            return;
        } else if (idx == -3) {
            state.toggleUseChopsticks(); repaint(); return;
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (state.awaitingPassPrompt) state.awaitingPassPrompt = false;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (state.canConfirm()) { state.confirmSelection(); repaint(); }
        }
    }
    @Override public void keyReleased(KeyEvent e) {}

    @Override public void run() {
        while (true) {
            try { Thread.sleep(30); } catch (InterruptedException ex) {}
            repaint();
        }
    }
}
