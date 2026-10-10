// Panel.java with layout fix for overflowing cards
// (adds second row/column if too many cards)
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Panel extends JPanel implements MouseListener {
    // unchanged variables

    // in drawRowCenteredRotated and drawColumnCenteredRotated methods, adjust for overflow:
    private void drawRowCenteredRotated(Graphics2D g, List<Card> cards, int cx, int cy, int w, int h, int gap, int angle) {
        if (cards == null || cards.isEmpty()) return;
        int rowSize = cards.size() > 6 ? (int)Math.ceil(cards.size()/2.0) : cards.size();
        int rows = cards.size() > 6 ? 2 : 1;
        int x = cx - (rowSize * w + (rowSize-1)*gap)/2;
        int y = cy - (rows*h + (rows-1)*gap)/2;
        int idx = 0;
        for (Card c : cards) {
            int row = idx / rowSize;
            int col = idx % rowSize;
            int drawX = x + col*(w+gap);
            int drawY = y + row*(h+gap);
            c.drawRotated(g, drawX, drawY, w, h, true, angle);
            idx++;
        }
    }

    private void drawColumnCenteredRotated(Graphics2D g, List<Card> cards, int cx, int cy, int w, int h, int gap, int angle) {
        if (cards == null || cards.isEmpty()) return;
        int colSize = cards.size() > 5 ? (int)Math.ceil(cards.size()/2.0) : cards.size();
        int cols = cards.size() > 5 ? 2 : 1;
        int y = cy - (colSize*h + (colSize-1)*gap)/2;
        int x = cx - (cols*w + (cols-1)*gap)/2;
        int idx = 0;
        for (Card c : cards) {
            int col = idx / colSize;
            int row = idx % colSize;
            int drawX = x + col*(w+gap);
            int drawY = y + row*(h+gap);
            c.drawRotated(g, drawX, drawY, w, h, true, angle);
            idx++;
        }
    }
}
