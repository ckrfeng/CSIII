import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Panel extends JPanel implements MouseListener {
    private Game game;
    private int PAD = 24;
    private int sideCardW = 110, sideCardH = 150, gap = 12;
    private Rectangle doneButton;

    public Panel() {
        setFocusable(true);
        addMouseListener(this);
        game = new Game();
        doneButton = new Rectangle();
        setPreferredSize(new Dimension(1400, 900));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        drawHands(g);
        drawTable(g);
        drawDoneButton(g);
    }

    private void drawHands(Graphics2D g) {
        int w = getWidth(), h = getHeight();
        List<List<Card>> hands = game.hands;
        int cp = game.currentPlayerIndex;
        for (int i = 0; i < 4; i++) {
            List<Card> hand = (i == cp) ? hands.get(i) : hands.get(i); // all hands exist, only bottom player visible
            int cx = 0, cy = 0, rot = 0;
            switch (i) {
                case 0: cx = w/2 - hand.size()*sideCardW/2; cy = h - sideCardH - PAD; rot = 0; break; // bottom
                case 1: cx = w - sideCardH - PAD; cy = h/2 - hand.size()*sideCardW/2; rot = 90; break; // right
                case 2: cx = w/2 + hand.size()*sideCardW/2; cy = PAD; rot = 180; break; // top
                case 3: cx = PAD; cy = h/2 + hand.size()*sideCardW/2; rot = -90; break; // left
            }
            for (int j = 0; j < hand.size(); j++) {
                Card c = hand.get(j);
                int tx = cx, ty = cy;
                if (i == 0) tx += j * (sideCardW + gap);
                else if (i == 1) ty += j * (sideCardW + gap);
                else if (i == 2) tx -= j * (sideCardW + gap);
                else ty -= j * (sideCardW + gap);

                if (i == cp || i == 0) { // only show current player's hand
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(tx, ty, sideCardW, sideCardH);
                    g.setColor(Color.BLACK);
                    g.drawRect(tx, ty, sideCardW, sideCardH);
                    g.drawString(c.type, tx + 8, ty + 20);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(tx, ty, sideCardW, sideCardH);
                    g.setColor(Color.BLACK);
                    g.drawRect(tx, ty, sideCardW, sideCardH);
                }
            }
        }
    }

    private void drawTable(Graphics2D g) {
        int w = getWidth(), h = getHeight();
        List<List<Card>> table = game.placedDown;
        int tableW = sideCardW + 8;
        int tableH = sideCardH + 8;
        for (int i = 0; i < 4; i++) {
            List<Card> cards = table.get(i);
            int cx = 0, cy = 0;
            switch (i) {
                case 0: cx = w/2 - tableW/2; cy = h - sideCardH - PAD - tableH - PAD; break;
                case 1: cx = w - sideCardH - PAD - tableW - PAD; cy = h/2 - tableH/2; break;
                case 2: cx = w/2 - tableW/2; cy = PAD + tableH + PAD; break;
                case 3: cx = PAD + tableW + PAD; cy = h/2 - tableH/2; break;
            }
            for (int j = 0; j < cards.size(); j++) {
                Card c = cards.get(j);
                g.setColor(Color.ORANGE);
                g.fillRect(cx, cy, sideCardW, sideCardH);
                g.setColor(Color.BLACK);
                g.drawRect(cx, cy, sideCardW, sideCardH);
                g.drawString(c.type, cx + 8, cy + 20);
                if (i == 0) cx += sideCardW + gap;
                else if (i == 1) cy += sideCardH + gap;
                else if (i == 2) cx += sideCardW + gap;
                else cy += sideCardH + gap;
            }
        }
    }

    private void drawDoneButton(Graphics2D g) {
        int w = 120, h = 40;
        int x = getWidth()/2 - w/2, y = getHeight() - PAD - h;
        doneButton.setBounds(x, y, w, h);
        g.setColor(Color.CYAN);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
        g.drawString("Done", x + 30, y + 25);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        // check if click on card
        int cp = game.currentPlayerIndex;
        List<Card> hand = game.hands.get(cp);
        int cx = getWidth()/2 - hand.size()*sideCardW/2;
        int cy = getHeight() - sideCardH - PAD;
        for (int i = 0; i < hand.size(); i++) {
            Rectangle r = new Rectangle(cx + i*(sideCardW+gap), cy, sideCardW, sideCardH);
            if (r.contains(p)) {
                game.playCard(i);
                repaint();
                return;
            }
        }
        // check done button
        if (doneButton.contains(p)) {
            game.nextPlayer();
            repaint();
        }
    }

    // unused mouse methods
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
