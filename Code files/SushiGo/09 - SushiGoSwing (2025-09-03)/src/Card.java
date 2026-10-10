import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class Card {
    public final CardType type;

    public Card(CardType type) {
        this.type = type;
    }

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

    public void paint(Graphics2D g, int x, int y, int w, int h, boolean selected) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 16, 16);
        g.setColor(Color.GRAY);
        g.drawRoundRect(x, y, w, h, 16, 16);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        g.setColor(Color.BLACK);
        String text = type.displayName();
        g.drawString(text, x+10, y+24);
        if (selected) {
            g.setColor(new Color(0, 120, 215));
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(x+2, y+2, w-4, h-4, 16, 16);
        }
    }
}
