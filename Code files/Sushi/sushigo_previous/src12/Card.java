import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class Card {
    public String type;
    // single copies of each image — loaded once
    public static BufferedImage chop, dumplings, egg, m1, m2, m3, pudding, salmon, sashimi, squid, tempura, wasabi, back;

    // constructor: cheap, just assigns type
    public Card(String type) {
        this.type = type;
    }

    // static initializer: load images once
    static {
        try {
            chop     = loadImg("/Image/chopsticks.jpg");
            dumplings = loadImg("/Image/dumplings.jpg");
            egg      = loadImg("/Image/eggnigiri.jpg");
            m1       = loadImg("/Image/makiroll1.jpg");
            m2       = loadImg("/Image/makiroll2.jpg");
            m3       = loadImg("/Image/makiroll3.jpg");
            pudding  = loadImg("/Image/pudding.jpg");
            salmon   = loadImg("/Image/salmonnigiri.jpg");
            sashimi  = loadImg("/Image/sashimi.jpg");
            squid    = loadImg("/Image/squidnigiri.jpg");
            tempura  = loadImg("/Image/tempura.jpg");
            wasabi   = loadImg("/Image/wasabi.jpg");
            back     = loadImg("/Image/back.jpg");
        } catch (Exception e) {
            // If resources fail to load, print trace but continue — drawing code will check for null.
            e.printStackTrace();
        }
    }

    private static BufferedImage loadImg(String resourcePath) {
        try (InputStream is = Card.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Resource not found: " + resourcePath);
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Draws a card back (for face-down) or card front in given bounds.
     * drawFront: if true, draws the specific image for this card's type; otherwise draws back.
     */
    public void draw(Graphics2D g, int x, int y, int w, int h, boolean drawFront) {
        if (!drawFront) {
            // back
            if (back != null) g.drawImage(back, x, y, w, h, null);
            else {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(x, y, w, h);
            }
            return;
        }

        BufferedImage img = null;
        if (type == null) {
            img = null;
        } else switch (type) {
            case "chopsticks": img = chop; break;
            case "dumpling": img = dumplings; break;
            case "eggnigiri": img = egg; break;
            case "maki1": img = m1; break;
            case "maki2": img = m2; break;
            case "maki3": img = m3; break;
            case "pudding": img = pudding; break;
            case "salmonnigiri": img = salmon; break;
            case "sashimi": img = sashimi; break;
            case "squidnigiri": img = squid; break;
            case "tempura": img = tempura; break;
            case "wasabi": img = wasabi; break;
            default: img = null; break;
        }

        if (img != null) {
            g.drawImage(img, x, y, w, h, null);
        } else {
            // fallback drawing
            g.setColor(Color.LIGHT_GRAY);
            g.fillRoundRect(x, y, w, h, 12, 12);
            g.setColor(Color.GRAY);
            g.drawRoundRect(x, y, w, h, 12, 12);
            g.setColor(Color.BLACK);
            g.drawString(type == null ? "?" : type, x + 6, y + 16);
        }
    }
}
