import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Card {
    public final String type;
	public static BufferedImage chop, dump, egg, m1, m2, m3, pud, salmon, sashimi, squid, temp, wasa, back;
    
    public Card(String type) {
    	this.type = type;
    	try {
            chop = ImageIO.read(Card.class.getResource("/Image/chopsticks.jpg"));
            dump = ImageIO.read(Card.class.getResource("/Image/dumplings.jpg"));
            egg = ImageIO.read(Card.class.getResource("/Image/eggnigiri.jpg"));
            m1 = ImageIO.read(Card.class.getResource("/Image/makiroll1.jpg"));
            m2 = ImageIO.read(Card.class.getResource("/Image/makiroll2.jpg"));
            m3 = ImageIO.read(Card.class.getResource("/Image/makiroll3.jpg"));
            pud = ImageIO.read(Card.class.getResource("/Image/pudding.jpg"));
            salmon = ImageIO.read(Card.class.getResource("/Image/salmonnigiri.jpg"));
            sashimi = ImageIO.read(Card.class.getResource("/Image/sashimi.jpg"));
            squid = ImageIO.read(Card.class.getResource("/Image/squidnigiri.jpg"));
            temp = ImageIO.read(Card.class.getResource("/Image/tempura.jpg"));
            wasa = ImageIO.read(Card.class.getResource("/Image/wasabi.jpg"));
            back = ImageIO.read(Card.class.getResource("/Image/back.jpg"));
        } catch (Exception e) {
            System.out.println("Image load warning: " + e.getMessage());
        }
    }
    
    public int makiCount() {
        switch (type) {
            case "maki1": return 1;
            case "maki2": return 2;
            case "maki3": return 3;
            default: return 0;
        }
    }
    
    public boolean isNigiri() {
        return type=="eggnigiri" || type=="salmonnigiri" || type=="squidnigiri";
    }
    
    public int nigiriBaseScore() {
        switch (type) {
            case "eggnigiri": return 1;
            case "salmonnigiri": return 2;
            case "squidnigiri": return 3;
            default: return 0;
        }
    }
    
    public static BufferedImage imageFor(String t) {
        switch (t) {
            case "tempura": return temp;
            case "sashimi": return sashimi;
            case "dumpling": return dump;
            case "maki1": return m1;
            case "maki2": return m2;
            case "maki3": return m3;
            case "eggnigiri": return egg;
            case "salmonnigiri": return salmon;
            case "squidnigiri": return squid;
            case "wasabi": return wasa;
            case "chopsticks": return chop;
            case "pudding": return pud;
        }
        return null;
    }
    
    public void drawFace(Graphics g, int x, int y, int width, int height, boolean selected) {
        BufferedImage img = imageFor(type);
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, width, height, 12, 12);
        g.setColor(Color.GRAY);
        g.drawRoundRect(x, y, width, height, 12, 12);
        //if (img != null) 
        g.drawImage(img, x+6, y+6, width-12, height-12, null);
        if (selected) {
            g.setColor(new Color(0,120,215,160));
            g.drawRoundRect(x+2, y+2, width-4, height-4, 12, 12);
        }
    }
    
    public static void drawBack(Graphics g, int x, int y, int w, int h) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(Color.GRAY);
        g.drawRoundRect(x, y, w, h, 12, 12);
        //if (back != null) 
        g.drawImage(back, x+6, y+6, w-12, h-12, null);
    }
    
    
    
    
}
