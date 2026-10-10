import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Card {
    public String type;

    // --- static shared images ---
    private static BufferedImage back, chop, dumplings, egg, m1, m2, m3, pudding,
            salmon, sashimi, squid, tempura, wasabi;

    static {
        try {
            back = ImageIO.read(Card.class.getResource("/Image/back.jpg"));
            chop = ImageIO.read(Card.class.getResource("/Image/chopsticks.jpg"));
            dumplings = ImageIO.read(Card.class.getResource("/Image/dumplings.jpg"));
            egg = ImageIO.read(Card.class.getResource("/Image/eggnigiri.jpg"));
            m1 = ImageIO.read(Card.class.getResource("/Image/makiroll1.jpg"));
            m2 = ImageIO.read(Card.class.getResource("/Image/makiroll2.jpg"));
            m3 = ImageIO.read(Card.class.getResource("/Image/makiroll3.jpg"));
            pudding = ImageIO.read(Card.class.getResource("/Image/pudding.jpg"));
            salmon = ImageIO.read(Card.class.getResource("/Image/salmonnigiri.jpg"));
            sashimi = ImageIO.read(Card.class.getResource("/Image/sashimi.jpg"));
            squid = ImageIO.read(Card.class.getResource("/Image/squidnigiri.jpg"));
            tempura = ImageIO.read(Card.class.getResource("/Image/tempura.jpg"));
            wasabi = ImageIO.read(Card.class.getResource("/Image/wasabi.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Card(String type) {
        this.type = type;
    }

    // helper to pick correct image
    private static BufferedImage getImageForType(String t) {
        if (t == null) return null;
        switch (t.toLowerCase()) {
            case "chopsticks": return chop;
            case "dumpling": return dumplings;
            case "eggnigiri": case "egg": return egg;
            case "maki1": return m1;
            case "maki2": return m2;
            case "maki3": return m3;
            case "pudding": return pudding;
            case "salmonnigiri": case "salmon": return salmon;
            case "sashimi": return sashimi;
            case "squidnigiri": case "squid": return squid;
            case "tempura": return tempura;
            case "wasabi": return wasabi;
            default: return null;
        }
    }

    public void draw(Graphics g, int x, int y, int w, int h, boolean drawFront) {
        BufferedImage img = drawFront ? getImageForType(type) : back;

        if (img != null) {
            g.drawImage(img, x, y, w, h, null);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, w, h);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, w, h);
            g.drawString(type, x + 5, y + 15);
        }
    }

    // --- Pixel rotation helpers ---
    private BufferedImage rotate90(BufferedImage first) {
        BufferedImage image = new BufferedImage(first.getHeight(), first.getWidth(), first.getType());
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                image.setRGB(i, j, first.getRGB(j, image.getWidth() - 1 - i));
            }
        }
        return image;
    }

    private BufferedImage rotate180(BufferedImage first) {
        return rotate90(rotate90(first));
    }

    private BufferedImage rotate270(BufferedImage first) {
        return rotate90(rotate180(first));
    }

    // Draw rotated card (0, 90, 180, 270 only)
    public void drawRotated(Graphics2D g, int x, int y, int w, int h, boolean drawFront, int angle) {
        BufferedImage img = drawFront ? getImageForType(type) : back;
        if (img == null) return;

        BufferedImage rotated;
        angle = ((angle % 360) + 360) % 360;
        if (angle == 90) rotated = rotate90(img);
        else if (angle == 180) rotated = rotate180(img);
        else if (angle == 270) rotated = rotate270(img);
        else rotated = img;

        g.drawImage(rotated, x, y, w, h, null);
    }
}
