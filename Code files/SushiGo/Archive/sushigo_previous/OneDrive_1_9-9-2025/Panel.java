import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Panel extends JPanel implements MouseListener {
    private Game game;
    private int cardWidth = 80, cardHeight = 120;
    private List<Card> selectedCards = new ArrayList<>();
    private JButton doneButton, chopButton;

    public Panel(Game g) {
        this.game = g;

        doneButton = new JButton("Done");
        
        doneButton.setBounds(400, 600, 100, 40);
        doneButton.addActionListener(e -> {
            Player current = game.getCurrentPlayer();
            if (!selectedCards.isEmpty()) {
                for (Card c : selectedCards) {
                    if (c.type.equals("wasabi")) current.wasabi = true;
                    game.playCard(current, c);
                }
                selectedCards.clear();
                game.endTurn();
                repaint();
            }
        });
        add(doneButton);

        chopButton = new JButton("Use Chopsticks");
        chopButton.setBounds(550, 600, 150, 40);
        chopButton.addActionListener(e -> {
            Player current = game.getCurrentPlayer();
            if (current.hand.stream().anyMatch(c -> c.type.equals("chopsticks")) && selectedCards.size() == 1) {
                selectedCards.clear(); // clear first pick, allow second pick
                current.hand.removeIf(c -> c.type.equals("chopsticks"));
                repaint();
            }
        });
        add(chopButton);

        addMouseListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(0, 128, 0));
        drawPlayers(g);
    }

    private void drawPlayers(Graphics g) {
        int w = getWidth(), h = getHeight();
        List<Player> ps = game.players;

        for (int i = 0; i < ps.size(); i++) {
            Player p = ps.get(i);
            int x = 0, y = 0;
            double rotation = 0;
            switch (i) {
                case 0: x = w / 2 - (p.hand.size() * cardWidth) / 2; y = h - cardHeight - 50; rotation = 0; break;
                case 1: x = w - cardHeight - 50; y = h / 2 - (p.hand.size() * cardWidth) / 2; rotation = Math.PI / 2; break;
                case 2: x = w / 2 - (p.hand.size() * cardWidth) / 2; y = 50; rotation = Math.PI; break;
                case 3: x = 50; y = h / 2 - (p.hand.size() * cardWidth) / 2; rotation = -Math.PI / 2; break;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.rotate(rotation, x + cardWidth / 2, y + cardHeight / 2);

            // Draw table cards
            int tx = w / 2 - (p.table.size() * cardWidth) / 2;
            int ty = (i == 0) ? y - cardHeight - 20 : (i == 2) ? y + cardHeight + 20 : y;
            for (Card c : p.table) {
                c.drawFace(g2, tx, ty, cardWidth, cardHeight, false);
                if (i == 0 || i == 2) tx += cardWidth + 5;
                else ty += cardHeight + 5;
            }

            // Draw hand
            int hx = x, hy = y;
            for (Card c : p.hand) {
                boolean faceUp = (i == game.currentPlayerIndex);
                if (faceUp) c.drawFace(g2, hx, hy, cardWidth, cardHeight, selectedCards.contains(c));
                else Card.drawBack(g2, hx, hy, cardWidth, cardHeight);
                if (i == 0 || i == 2) hx += cardWidth + 5;
                else hy += cardHeight + 5;
            }
            g2.dispose();
        }
    }

    public void mouseClicked(MouseEvent e) {
        Player current = game.getCurrentPlayer();
        int w = getWidth(), h = getHeight();
        int x = w / 2 - (current.hand.size() * cardWidth) / 2;
        int y = h - cardHeight - 50;

        for (Card c : current.hand) {
            Rectangle bounds = new Rectangle(x, y, cardWidth, cardHeight);
            if (bounds.contains(e.getPoint())) {
                if (!selectedCards.contains(c)) {
                    if (selectedCards.size() < 2) selectedCards.add(c);
                } else selectedCards.remove(c);
                repaint();
                break;
            }
            x += cardWidth + 5;
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
