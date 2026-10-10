import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class Card {
    public String type;

    public static BufferedImage chop, dumplings, egg, m1, m2, m3,
            pudding, salmon, sashimi, squid, tempura, wasabi, back;

    private static BufferedImage[] chopRotated, dumplingsRotated, eggRotated, m1Rotated, m2Rotated, m3Rotated,
            puddingRotated, salmonRotated, sashimiRotated, squidRotated, tempuraRotated, wasabiRotated, backRotated;

    public Card(String type) {
        this.type = type;
    }

    static {
        try {
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
	        back = ImageIO.read(Card.class.getResource("/Image/back.jpg"));         

		} catch (IOException e) {
			e.printStackTrace();
		}   
        chopRotated = makeRotations(chop);
        dumplingsRotated = makeRotations(dumplings);
        eggRotated = makeRotations(egg);
        m1Rotated = makeRotations(m1);
        m2Rotated = makeRotations(m2);
        m3Rotated = makeRotations(m3);
        puddingRotated = makeRotations(pudding);
        salmonRotated = makeRotations(salmon);
        sashimiRotated = makeRotations(sashimi);
        squidRotated = makeRotations(squid);
        tempuraRotated = makeRotations(tempura);
        wasabiRotated = makeRotations(wasabi);
        backRotated = makeRotations(back);
    }

    private static BufferedImage[] makeRotations(BufferedImage img) {
        BufferedImage r0 = img;
        BufferedImage r90 = rotate90(r0);
        BufferedImage r180 = rotate90(r90);
        BufferedImage r270 = rotate90(r180);
        
        BufferedImage[] rotations = new BufferedImage[4];
        rotations[0] = r0;
        rotations[1] = r90;
        rotations[2] = r180;
        rotations[3] = r270;
        
        return rotations;
    }

    private static BufferedImage rotate90(BufferedImage source) {
        BufferedImage image = new BufferedImage(source.getHeight(), source.getWidth(), source.getType());
        for(int i = 0; i < image.getWidth(); i++) {
        	for(int j = 0; j < image.getHeight(); j++) {
        		image.setRGB(i, j, source.getRGB(j, image.getWidth() - 1 - i));
        	}
        }
        return image;
    }

    private static BufferedImage getImage(String type) {
        switch (type) {
            case "chopsticks":
            	return chop;
            case "dumpling":
            	return dumplings;
            case "eggnigiri":
            	return egg;
            case "maki1":
            	return m1;
            case "maki2":
            	return m2;
            case "maki3":
            	return m3;
            case "pudding":
            	return pudding;
            case "salmonnigiri":
            	return salmon;
            case "sashimi":
            	return sashimi;
            case "squidnigiri":
            	return squid;
            case "tempura":
            	return tempura;
            case "wasabi":
            	return wasabi;
            default:
            	return null;
        }
    }

    private static BufferedImage[] getRotations(String type, boolean front) {
        if (!front) return backRotated;
        switch (type) {
            case "chopsticks": 
            	return chopRotated;
            case "dumpling":   
            	return dumplingsRotated;
            case "eggnigiri":  
            	return eggRotated;
            case "maki1":      
            	return m1Rotated;
            case "maki2":      
            	return m2Rotated;
            case "maki3":      
            	return m3Rotated;
            case "pudding":    
            	return puddingRotated;
            case "salmonnigiri": 
            	return salmonRotated;
            case "sashimi":    
            	return sashimiRotated;
            case "squidnigiri": 
            	return squidRotated;
            case "tempura":    
            	return tempuraRotated;
            case "wasabi":     
            	return wasabiRotated;
            default: 
            	return null;
        }
    }

    public void draw(Graphics g, int x, int y, int width, int height, boolean front) {
    	BufferedImage img;

        if (front) {
            img = getImage(type);
        } else {
            img = back;
        }
        
        g.drawImage(img, x, y, width, height, null);
    }

    public void drawRotated(Graphics g, int x, int y, int width, int height, boolean front, int angle) {
        BufferedImage[] rotated = getRotations(type, front);
        
        int ang = ((angle % 360) + 360) % 360 / 90;
        
        BufferedImage toDraw = rotated[ang];

        g.drawImage(toDraw, x, y, width, height, null);
    }
}