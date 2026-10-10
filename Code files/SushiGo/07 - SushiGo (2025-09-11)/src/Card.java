import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Card {
    public String type;

    // cached images
    public static BufferedImage chop, dumplings, egg, m1, m2, m3,
            pudding, salmon, sashimi, squid, tempura, wasabi, back;

    // rotation cache: key -> [0deg,90deg,180deg,270deg]
    private static final Map<String, BufferedImage[]> cache = new HashMap<>();

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

        // cache rotations for each loaded image (if null, skip)
        cacheRot("chop", chop);
        cacheRot("dumplings", dumplings);
        cacheRot("egg", egg);
        cacheRot("m1", m1);
        cacheRot("m2", m2);
        cacheRot("m3", m3);
        cacheRot("pudding", pudding);
        cacheRot("salmon", salmon);
        cacheRot("sashimi", sashimi);
        cacheRot("squid", squid);
        cacheRot("tempura", tempura);
        cacheRot("wasabi", wasabi);
        cacheRot("back", back);
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

    private static void cacheRot(String key, BufferedImage img) {
        if (img == null) return;
        BufferedImage r0 = img;
        BufferedImage r90 = rotate90Static(img);
        BufferedImage r180 = rotate90Static(r90);
        BufferedImage r270 = rotate90Static(r180);
        cache.put(key, new BufferedImage[] { r0, r90, r180, r270 });
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

    /**
     * Draw front/back non-rotated (fallback).
     */
    public void draw(Graphics2D g, int x, int y, int w, int h, boolean drawFront) {
        BufferedImage img = drawFront ? getImageForType(type) : back;
        if (img != null) {
            g.drawImage(img, x, y, w, h, null);
            return;
        }
        // fallback drawing if image missing
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

    /**
     * Draw rotated card (0,90,180,270) using cached rotated images when available.
     * drawFront: if true, draws this card's front image; otherwise draws the back.
     * angle: degrees (0/90/180/270).
     */
    public void drawRotated(Graphics2D g, int x, int y, int w, int h, boolean drawFront, int angle) {
        BufferedImage src = drawFront ? getImageForType(type) : back;
        if (src == null) {
            // fallback to normal draw (no image)
            draw(g, x, y, w, h, drawFront);
            return;
        }

        String key = drawFront ? keyForType(type) : "back";
        BufferedImage[] arr = (key == null) ? null : cache.get(key);
        int normalized = ((angle % 360) + 360) % 360;
        int idx = (normalized / 90) % 4; // 0->0deg,1->90,2->180,3->270

        BufferedImage toDraw = null;
        if (arr != null && arr.length == 4) {
            toDraw = arr[idx];
        } else {
            // fallback: perform rotation on-demand (rare path)
            if (idx == 0) toDraw = src;
            else if (idx == 1) toDraw = rotate90Static(src);
            else if (idx == 2) toDraw = rotate180Static(src);
            else toDraw = rotate270Static(src);
        }

        if (toDraw != null) {
            g.drawImage(toDraw, x, y, w, h, null);
        } else {
            // final fallback
            draw(g, x, y, w, h, drawFront);
        }
    }
}
