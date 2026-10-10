import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MenuPanel extends JPanel implements MouseListener {

    private BufferedImage[] cards = new BufferedImage[8];
    private boolean[] visible = new boolean[8];
    private Rectangle[] bounds = new Rectangle[8];
    private int cardWidth = 120;
    private int cardHeight = 180;

    public MenuPanel() {
        System.out.println("reading files");
    	try {
            cards[0] = ImageIO.read(MenuPanel.class.getResource("/images/Cover.jpg"));
            cards[1] = ImageIO.read(MenuPanel.class.getResource("/images/Cover.jpg"));
            cards[2] = ImageIO.read(MenuPanel.class.getResource("/images/Cover.jpg"));
            cards[3] = ImageIO.read(MenuPanel.class.getResource("/images/Cover.jpg"));
            cards[4] = ImageIO.read(MenuPanel.class.getResource("/images/Cover.jpg"));
            cards[5] = ImageIO.read(MenuPanel.class.getResource("/images/Cover.jpg"));
            cards[6] = ImageIO.read(MenuPanel.class.getResource("/images/Cover.jpg"));
            cards[7] = ImageIO.read(MenuPanel.class.getResource("/images/Cover.jpg"));
            System.out.println(MenuPanel.class.getResource("/images/Cover.jpg"));

            if(cards[0]==null) {
            	System.out.println("Card is null");
            }
        } catch (Exception e) {
            System.out.println("Exception Error loading images");
            return;
        }

        for (int i = 0; i < 8; i++) {
            visible[i] = true;
        }

        addMouseListener(this);
    }

    public void paint(Graphics g) {
        super.paint(g);

        int padding = 20;
        int totalWidth = 8 * (cardWidth + padding) - padding;
        int startX = getWidth() / 2 - totalWidth / 2;
        int y = getHeight() / 2 - cardHeight / 2;

        for (int i = 0; i < 8; i++) {
            int x = startX + i * (cardWidth + padding);
            bounds[i] = new Rectangle(x, y, cardWidth, cardHeight);
            if (visible[i]) {
                g.drawImage(cards[i], x, y, cardWidth, cardHeight, null);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        for (int i = 0; i < 8; i++) {
            if (visible[i] && bounds[i] != null && bounds[i].contains(mx, my)) {
                visible[i] = false;  // hide clicked card
                repaint();
                break;
            }
        }
    }

    // Empty implementations for unused MouseListener methods
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
}
