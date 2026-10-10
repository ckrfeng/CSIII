import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

public class Card {
    public final CardType type;
    public static BufferedImage chop, dump, egg, m1, m2, m3, pud, salmon, sashimi, squid, temp, wasa;
    static {
        try {
            Class<?> c = Card.class;
            chop = ImageIO.read(c.getResource("/Image/chopsticks.jpg"));
            dump = ImageIO.read(c.getResource("/Image/dumplings.jpg"));
            egg = ImageIO.read(c.getResource("/Image/eggnigiri.jpg"));
            m1 = ImageIO.read(c.getResource("/Image/makiroll1.jpg"));
            m2 = ImageIO.read(c.getResource("/Image/makiroll2.jpg"));
            m3 = ImageIO.read(c.getResource("/Image/makiroll3.jpg"));
            pud = ImageIO.read(c.getResource("/Image/pudding.jpg"));
            salmon = ImageIO.read(c.getResource("/Image/salmonnigiri.jpg"));
            sashimi = ImageIO.read(c.getResource("/Image/sashimi.jpg"));
            squid = ImageIO.read(c.getResource("/Image/squidnigiri.jpg"));
            temp = ImageIO.read(c.getResource("/Image/tempura.jpg"));
            wasa = ImageIO.read(c.getResource("/Image/wasabi.jpg"));
        } catch (Exception e) {
            // Images may be missing; that's okay — we'll draw placeholders
            System.out.println("Image load warning: " + e.getMessage());
        }
    }

    public Card(CardType t) { this.type = t; }

    public int makiCount() {
        switch (type) {
            case MAKI_1: return 1;
            case MAKI_2: return 2;
            case MAKI_3: return 3;
            default: return 0;
        }
    }
    public boolean isNigiri() {
        return type==CardType.NIGIRI_EGG || type==CardType.NIGIRI_SALMON || type==CardType.NIGIRI_SQUID;
    }
    public int nigiriBaseScore() {
        switch (type) {
            case NIGIRI_EGG: return 1;
            case NIGIRI_SALMON: return 2;
            case NIGIRI_SQUID: return 3;
            default: return 0;
        }
    }

    public void draw(Graphics2D g, int x, int y, int w, int h, boolean selected) {
        // Draw background
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(Color.GRAY);
        g.drawRoundRect(x, y, w, h, 12, 12);
        // Image if available
        BufferedImage img = null;
        switch (type) {
            case TEMPURA: img = temp; break;
            case SASHIMI: img = sashimi; break;
            case DUMPLING: img = dump; break;
            case MAKI_1: img = m1; break;
            case MAKI_2: img = m2; break;
            case MAKI_3: img = m3; break;
            case NIGIRI_EGG: img = egg; break;
            case NIGIRI_SALMON: img = salmon; break;
            case NIGIRI_SQUID: img = squid; break;
            case WASABI: img = wasa; break;
            case CHOPSTICKS: img = chop; break;
            case PUDDING: img = pud; break;
        }
        if (img != null) {
            g.drawImage(img, x+6, y+28, w-12, h-36, null);
        } else {
            g.setColor(new Color(220,220,220));
            g.fillRect(x+6, y+28, w-12, h-36);
            g.setColor(Color.BLACK);
            g.drawString(type.displayName(), x+10, y+22);
        }
        if (selected) {
            g.setColor(new Color(0,120,215,160));
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(x+2, y+2, w-4, h-4, 12, 12);
        }
    }
}
