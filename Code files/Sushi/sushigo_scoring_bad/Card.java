import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class Card {
    public String type;

    // images
    public static BufferedImage chop, dumplings, egg, m1, m2, m3,
            pudding, salmon, sashimi, squid, tempura, wasabi, back;

    // rotations (0,90,180,270)
    private static BufferedImage[] chopRotated, dumplingsRotated, eggRotated, m1Rotated, m2Rotated, m3Rotated,
            puddingRotated, salmonRotated, sashimiRotated, squidRotated, tempuraRotated, wasabiRotated, backRotated;

    public Card(String type) {
        this.type = type;
    }

    // load images + prepare rotations
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

    /** Precompute 0°, 90°, 180°, 270° rotations */
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

    /** Rotate image 90° clockwise */
    private static BufferedImage rotate90(BufferedImage source) {
        /*
    	if (src == null) return null;
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < h; x++) {
            for (int y = 0; y < w; y++) {
                out.setRGB(x, y, src.getRGB(y, h - 1 - x));
            }
        }
        return out;
        */
        BufferedImage image = new BufferedImage(source.getHeight(), source.getWidth(), source.getType());
        for(int i = 0; i < image.getWidth(); i++) {
        	for(int j = 0; j < image.getHeight(); j++) {
        		image.setRGB(i, j, source.getRGB(j, image.getWidth() - 1 - i));
        	}
        }
        return image;
    }

    /** Get base image for card type */
    private static BufferedImage getImage(String type) {
        //if (type == null) return null;
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

    /** Get rotation set for card type */
    private static BufferedImage[] getRotations(String type, boolean front) {
        if (!front) return backRotated;
        //if (type == null) return null;
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

    /** Normal draw */
    public void draw(Graphics g, int x, int y, int width, int height, boolean front) {
    	BufferedImage img;

        if (front) {
            img = getImage(type);
        } else {
            img = back;
        }
        
        g.drawImage(img, x, y, width, height, null);
        
        /*
        if (img != null) {
            g.drawImage(img, x, y, width, height, null);
        } else {
            // fallback: gray box with text
            g.setColor(Color.LIGHT_GRAY);
            g.fillRoundRect(x, y, width, height, 12, 12);
            g.setColor(Color.GRAY);
            g.drawRoundRect(x, y, width, height, 12, 12);
            g.setColor(Color.BLACK);
            g.drawString(type == null ? "?" : type, x + 6, y + 16);
        }
        */
    }

    /** Rotated draw (by 0/90/180/270 only) */
    public void drawRotated(Graphics g, int x, int y, int width, int height, boolean front, int angle) {
        BufferedImage[] rotated = getRotations(type, front);
        
        int ang = ((angle % 360) + 360) % 360 / 90;
        
        BufferedImage toDraw = rotated[ang];

        g.drawImage(toDraw, x, y, width, height, null);
        
        
        /*
        BufferedImage toDraw = null;
        if (rotated != null && rotated.length == 4) {
            toDraw = rotated[ang];
        }
        */

        /*
        if (toDraw == null) {
            draw(g, x, y, width, height, front); // fallback
        } else {
            g.drawImage(toDraw, x, y, width, height, null);
        }
        */
    }
}

/*
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class Card {
    public String type;
    public static BufferedImage chop, dumplings, egg, m1, m2, m3,
            pudding, salmon, sashimi, squid, tempura, wasabi, back;

    // rotation arrays for each card type
    private static BufferedImage[] chopRot, dumplingsRot, eggRot, m1Rot, m2Rot, m3Rot,
            puddingRot, salmonRot, sashimiRot, squidRot, tempuraRot, wasabiRot, backRot;

    public Card(String type) {
        this.type = type;
    }

    // load images once, cache rotations
    static {
        try {
            chop      = loadImg("/Image/chopsticks.jpg");
            dumplings = loadImg("/Image/dumplings.jpg");
            egg       = loadImg("/Image/eggnigiri.jpg");
            m1        = loadImg("/Image/makiroll1.jpg");
            m2        = loadImg("/Image/makiroll2.jpg");
            m3        = loadImg("/Image/makiroll3.jpg");
            pudding   = loadImg("/Image/pudding.jpg");
            salmon    = loadImg("/Image/salmonnigiri.jpg");
            sashimi   = loadImg("/Image/sashimi.jpg");
            squid     = loadImg("/Image/squidnigiri.jpg");
            tempura   = loadImg("/Image/tempura.jpg");
            wasabi    = loadImg("/Image/wasabi.jpg");
            back      = loadImg("/Image/back.jpg");
        } catch (Throwable t) {
            // never throw from static init; print and continue so program still runs if some images missing
            t.printStackTrace();
        }

        // create rotation arrays
        chopRot      = makeRotations(chop);
        dumplingsRot = makeRotations(dumplings);
        eggRot       = makeRotations(egg);
        m1Rot        = makeRotations(m1);
        m2Rot        = makeRotations(m2);
        m3Rot        = makeRotations(m3);
        puddingRot   = makeRotations(pudding);
        salmonRot    = makeRotations(salmon);
        sashimiRot   = makeRotations(sashimi);
        squidRot     = makeRotations(squid);
        tempuraRot   = makeRotations(tempura);
        wasabiRot    = makeRotations(wasabi);
        backRot      = makeRotations(back);
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

    private static BufferedImage[] makeRotations(BufferedImage img) {
        if (img == null) return null;
        BufferedImage r0 = img;
        BufferedImage r90 = rotate90Static(img);
        BufferedImage r180 = rotate90Static(r90);
        BufferedImage r270 = rotate90Static(r180);
        return new BufferedImage[]{ r0, r90, r180, r270 };
    }

    // static rotation helpers used for caching
    private static BufferedImage rotate90Static(BufferedImage first) {
        if (first == null) return null;
        int fw = first.getWidth();
        int fh = first.getHeight();
        int newW = fh;
        int newH = fw;
        int type = first.getType();
        if (type == BufferedImage.TYPE_CUSTOM || type == 0) type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage image = new BufferedImage(newW, newH, type);
        for (int i = 0; i < newW; i++) {
            for (int j = 0; j < newH; j++) {
                // new(i,j) = first(j, fh - 1 - i)
                image.setRGB(i, j, first.getRGB(j, fh - 1 - i));
            }
        }
        return image;
    }

    private static BufferedImage rotate180Static(BufferedImage first) {
        return rotate90Static(rotate90Static(first));
    }

    private static BufferedImage rotate270Static(BufferedImage first) {
        return rotate90Static(rotate180Static(first));
    }

    private static String keyForType(String t) {
        if (t == null) return null;
        switch (t.toLowerCase()) {
            case "chopsticks": return "chop";
            case "dumpling": return "dumplings";
            case "eggnigiri": return "egg";
            case "maki1": return "m1";
            case "maki2": return "m2";
            case "maki3": return "m3";
            case "pudding": return "pudding";
            case "salmonnigiri": return "salmon";
            case "sashimi": return "sashimi";
            case "squidnigiri": return "squid";
            case "tempura": return "tempura";
            case "wasabi": return "wasabi";
            default: return null;
        }
    }

    private static BufferedImage getImageForType(String t) {
        String k = keyForType(t);
        if (k == null) return null;
        switch (k) {
            case "chop": return chop;
            case "dumplings": return dumplings;
            case "egg": return egg;
            case "m1": return m1;
            case "m2": return m2;
            case "m3": return m3;
            case "pudding": return pudding;
            case "salmon": return salmon;
            case "sashimi": return sashimi;
            case "squid": return squid;
            case "tempura": return tempura;
            case "wasabi": return wasabi;
            default: return null;
        }
    }

    private static BufferedImage[] getRotationsForType(String t, boolean drawFront) {
        if (!drawFront) return backRot;
        String k = keyForType(t);
        if (k == null) return null;
        switch (k) {
            case "chop": return chopRot;
            case "dumplings": return dumplingsRot;
            case "egg": return eggRot;
            case "m1": return m1Rot;
            case "m2": return m2Rot;
            case "m3": return m3Rot;
            case "pudding": return puddingRot;
            case "salmon": return salmonRot;
            case "sashimi": return sashimiRot;
            case "squid": return squidRot;
            case "tempura": return tempuraRot;
            case "wasabi": return wasabiRot;
            default: return null;
        }
    }

    public void draw(Graphics2D g, int x, int y, int w, int h, boolean drawFront) {
        BufferedImage img = drawFront ? getImageForType(type) : back;
        if (img != null) {
            g.drawImage(img, x, y, w, h, null);
            return;
        }
        if (!drawFront && back != null) {
            g.drawImage(back, x, y, w, h, null);
            return;
        }
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(Color.GRAY);
        g.drawRoundRect(x, y, w, h, 12, 12);
        g.setColor(Color.BLACK);
        g.drawString(type == null ? "?" : type, x + 6, y + 16);
    }

    public void drawRotated(Graphics2D g, int x, int y, int w, int h, boolean drawFront, int angle) {
        BufferedImage[] arr = getRotationsForType(type, drawFront);

        int normalized = ((angle % 360) + 360) % 360;
        int idx = (normalized / 90) % 4;

        BufferedImage toDraw = null;
        if (arr != null && arr.length == 4) {
            toDraw = arr[idx];
        } else {
            // fallback: perform rotation on-demand
            BufferedImage src = drawFront ? getImageForType(type) : back;
            if (src != null) {
                if (idx == 0) toDraw = src;
                else if (idx == 1) toDraw = rotate90Static(src);
                else if (idx == 2) toDraw = rotate180Static(src);
                else toDraw = rotate270Static(src);
            }
        }

        if (toDraw != null) {
            g.drawImage(toDraw, x, y, w, h, null);
        } else {
            draw(g, x, y, w, h, drawFront);
        }
    }
}
*/