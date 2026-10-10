import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Panel extends JPanel implements MouseListener {
    private final Game game;
    private final int CARD_W = 80;
    private final int CARD_H = 120;
    private final int SIDE_W = 120; // left/right card display width (landscape)
    private final int SIDE_H = 80;  // left/right card display height (landscape)
    private final int GAP = 4;
    private final List<Card> selected = new ArrayList<>();

    private int seatOffset = 0;

    private final JButton doneBtn;
    private final JButton chopBtn;

    public Panel(Game game) {
        this.game = game;
        setLayout(null);
        setBackground(new Color(0xE9EFEF));
        //setPreferredSize(new Dimension(1000, 700));
        
        doneBtn = new JButton("Done");
        doneBtn.setBounds(420, 792, 100, 34);
        doneBtn.addActionListener(e -> onDone());
        add(doneBtn);

        chopBtn = new JButton("Use Chopsticks");
        chopBtn.setBounds(540, 792, 140, 34);
        chopBtn.addActionListener(e -> onUseChopsticks());
        add(chopBtn);

        addMouseListener(this);
    }

    private void onUseChopsticks() {
        Player current = game.getCurrentPlayer();
        /*
        if (current == null) {
        	return;
        }
        */
        if (selected.size() != 2) {
        	return;
        }

        boolean hasChop = false;
        for (Card c : current.table) {
            if ("chopsticks".equals(c.type)) {
            	hasChop = true;
            	break;
            }
            /*
            if ("chopsticks".equalsIgnoreCase(c.type)) {
            	hasChop = true; break;
            }
            */
        }
        if (!hasChop) {
        	return;
        }

        // Use game's chopsticks helper (returns the chopsticks card to hand inside Game)
        game.useChopsticks(current, selected.get(0), selected.get(1));
        selected.clear();

        // finish turn & rotate visible seats
        game.endTurn();
        seatOffset = (seatOffset + 1) % 4;
        repaint();
    }

    private void onDone() {
        Player current = game.getCurrentPlayer();
        //if (current == null) return;

        if (selected.isEmpty()) {
            game.endTurn();
            seatOffset = (seatOffset + 1) % 4;
            repaint();
            return;
        }

        if (selected.size() == 1) {
            game.playCard(current, selected.get(0));
        } else {
            // selected.size() == 2
            boolean hasChop = false;
            for (Card c : current.table) {
            	if ("chopsticks".equals(c.type)) {
            		hasChop = true;
            		break;
            	}
            }
            if (hasChop) {
                // game.useChopsticks will return the chopsticks to hand
                game.useChopsticks(current, selected.get(0), selected.get(1));
            } else {
                // no chopsticks: play first only
                game.playCard(current, selected.get(0));
            }
        }

        selected.clear();
        game.endTurn();
        seatOffset = (seatOffset + 1) % 4;
        repaint();
    }

    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (game.isGameOver()) {
            drawScoreboard(g);
            return;
        }
        int w = getWidth(), h = getHeight();

        // big center rectangle
        /*
        int centerW = Math.min(900, w - 160);
        int centerH = Math.min(520, h - 160);
        */
        
        int centerW = 960;
        int centerH = 600;
        
        int rectX = (w - centerW) / 2;
        int rectY = (h - centerH) / 2;

        g.setColor(Color.WHITE);
        g.fillRoundRect(rectX, rectY, centerW, centerH, 16, 16);
        g.setColor(Color.DARK_GRAY);
        //g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(rectX, rectY, centerW, centerH, 16, 16);

        // draw round label
        //g.setFont(g.getFont().deriveFont(Font.BOLD, 16f));
        g.drawString("Round: " + game.round, rectX + 12, rectY + 22);

        // compute symmetric center-play areas (equidistant from rectangle)
        int pad = 40;
        Point topCenter = new Point(rectX + centerW / 2, rectY + pad + CARD_H / 2);
        Point bottomCenter = new Point(rectX + centerW / 2, rectY + centerH - pad - CARD_H / 2);
        Point leftCenter = new Point(rectX + pad + SIDE_W / 2, rectY + centerH / 2);
        Point rightCenter = new Point(rectX + centerW - pad - SIDE_W / 2, rectY + centerH / 2);

        Point[] centers = new Point[4];

        centers[0] = bottomCenter;
        centers[1] = rightCenter;
        centers[2] = topCenter;
        centers[3] = leftCenter;
        
        
        // draw each player's played cards in center areas: top/bottom -> horizontal rows, left/right -> vertical columns
        for (int i = 0; i < game.players.size(); i++) {
            Player p = game.players.get(i);
            
            int seat = (i - seatOffset + 4) % 4;
            
            Point area = centers[seat];
            
            if (seat == 1 || seat == 3) {
                // left/right -> vertical column; cards are landscape and rotated so top faces center
                int angle;
                
                if(seat == 1) {
                	angle = 270;
                } else {
                	angle = 90;
                }
                
                drawColumnCenteredRotated(g, p.table, area.x, area.y, SIDE_W, SIDE_H, GAP, angle);
            } else {
                // top/bottom -> horizontal row; portrait
                int angle;
                
                if (seat == 2) {
                	angle = 180;
                } else {
                	angle = 0;
                }
                
                drawRowCenteredRotated(g, p.table, area.x, area.y, CARD_W, CARD_H, GAP, angle);
            }
        }

        // draw each player's hand at the sides, centered
        for (int i = 0; i < game.players.size(); i++) {
            Player p = game.players.get(i);
            int seat = (i - seatOffset + 4) % 4;
            drawPlayerHandForSeat(g, p, seat, rectX, rectY, centerW, centerH);
        }
    }

    // horizontal row centered at (centerX,centerY)
    private void drawRowCenteredRotated(Graphics g, List<Card> cards, int centerX, int centerY, int drawW, int drawH, int gap, int angle) {
        //if (cards == null || cards.isEmpty()) return;
        int totalW = cards.size() * drawW + (cards.size() - 1) * gap;
        int startX = centerX - totalW / 2;
        int y = centerY - drawH / 2;
        int x = startX;
        for (Card c : cards) {
            c.drawRotated(g, x, y, drawW, drawH, true, angle);
            x += drawW + gap;
        }
    }

    // vertical column centered at (centerX,centerY)
    private void drawColumnCenteredRotated(Graphics g, List<Card> cards, int centerX, int centerY, int drawW, int drawH, int gap, int angle) {
        //if (cards == null || cards.isEmpty()) return;
        int totalH = cards.size() * drawH + (cards.size() - 1) * gap;
        int startY = centerY - totalH / 2;
        int x = centerX - drawW / 2;
        int y = startY;
        for (Card c : cards) {
            c.drawRotated(g, x, y, drawW, drawH, true, angle);
            y += drawH + gap;
        }
    }

    private void drawPlayerHandForSeat(Graphics g, Player p, int seat, int rectX, int rectY, int centerW, int centerH) {
        boolean faceUp = (game.getCurrentPlayer() == p);
        int count = p.hand.size();

        Point center;
        int margin = 32;
        int fullW = getWidth(), fullH = getHeight();
        switch (seat) {
            case 0: 
            	center = new Point(fullW/2, rectY + centerH + margin + CARD_H/2);
            	break;
            case 1:
            	center = new Point(rectX + centerW + margin + SIDE_W/2, fullH/2);
            	break;
            case 2:
            	center = new Point(fullW/2, rectY - margin - CARD_H/2);
            	break;
            default:
            	center = new Point(rectX - margin - SIDE_W/2, fullH/2);
            	break;
        }

        boolean vertical = (seat == 1 || seat == 3);
        int drawW;
        
        if (vertical) {
        	drawW = SIDE_W;
        } else {
        	drawW = CARD_W;
        }
        
        int drawH;
        
        if (vertical) {
        	drawH = SIDE_H;
        } else {
        	drawH = CARD_H;
        }
        
        int totalW;
        
        if (vertical) {
        	totalW = drawW;
        } else {
        	totalW = Math.max(0, count * drawW + (count - 1) * GAP);
        }
        
        int totalH;
        
        if (vertical) {
        	totalH = Math.max(0, count * drawH + (count - 1) * GAP);
        } else {
        	totalH = drawH;
        }

        int startX = center.x - totalW / 2;
        int startY = center.y - totalH / 2;

        int angleDeg;
        if (seat == 0) {
        	angleDeg = 0;
        } else if (seat == 1) {
        	angleDeg = 90;
        } else if (seat == 2) {
        	angleDeg = 180;
        } else {
        	angleDeg = 270;
        }

        for (int i = 0; i < count; i++) {
            Card c = p.hand.get(i);
            int drawX;
            if (vertical) {
            	drawX = startX;
            } else {
            	drawX = startX + i * (drawW + GAP);
            }
            
            int drawY;
            if (vertical) {
                drawY = startY + i * (drawH + GAP);
            } else {
                drawY = startY;
            }
            boolean current = (game.getCurrentPlayer() == p);
            boolean isSelected = current && selected.contains(c);
            
            if (isSelected) {
                int nudge = 14;
                switch (seat) {
                    case 0:
                    	drawY -= nudge;
                    	break;
                    case 1:
                    	drawX -= nudge;
                    	break;
                    case 2:
                    	drawY += nudge;
                    	break;
                    case 3:
                    	drawX += nudge;
                    	break;
                }
            }

            // draw card rotated so top faces center
            c.drawRotated(g, drawX, drawY, drawW, drawH, current, angleDeg);

            // draw player label next to first card (score + puddings)
            if (i == 0) {
                //g.setColor(Color.BLACK);
                //g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
                String label = "P" + p.id + " | Score: " + p.score + " | P:" + p.pudding;
                int labelX = drawX - 40, labelY = drawY - 8;
                if (seat == 0) {
                    labelX = drawX;
                    labelY = drawY + drawH + 20;
                } else if (seat == 1) {
                    labelX = drawX + drawW + 8;
                    labelY = drawY + drawH / 2;
                } else if (seat == 2) {
                    labelX = drawX;
                    labelY = drawY - 14;
                } else if (seat == 3) {
                    labelX = drawX - 120;
                    labelY = drawY + drawH / 2;
                }
                g.drawString(label, labelX, labelY);
            }
        }
    }
    
    private void drawScoreboard(Graphics gg) {
        Graphics2D g = (Graphics2D) gg;
        int w = getWidth();
        int h = getHeight();

        // translucent dark overlay
        //g.setColor(Color.DARK_GRAY);
        //g.fillRect(0, 0, w, h);
        
        int centerW = 960;
        int centerH = 600;
        
        int rectX = (w - centerW) / 2;
        int rectY = (h - centerH) / 2;

        g.setColor(Color.WHITE);
        g.fillRoundRect(rectX, rectY, centerW, centerH, 16, 16);
        g.setColor(Color.DARK_GRAY);
        //g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(rectX, rectY, centerW, centerH, 16, 16);

        /*
        g.setColor(new Color(255, 255, 240));
        g.fillRoundRect(rectX, rectY, centerW, centerH, 16, 16);
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(rectX, rectY, centerW, centerH, 16, 16);
        */

        // title
        //g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(Color.BLACK);
        String title = "Final Scores";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, rectX + (centerW - tw) / 2, rectY + 40);

        // per-round scores
        List<int[]> rounds = game.getRoundScores();
        int y = rectY + 80;
        //g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        /*
        if (rounds == null || rounds.isEmpty()) {
            g.drawString("No rounds recorded.", rectX + 20, y);
        } else {
        */  
        for (int r = 0; r < rounds.size(); r++) {
            int[] sc = rounds.get(r);
            String str = "Round " + (r + 1) + ": ";
            for (int i = 0; i < sc.length; i++) {
                str += "P" + (i + 1) + "=" + sc[i];
                if (i < sc.length - 1) str += "   ";
            }
            g.drawString(str, rectX + 20, y);
            y += 28;
        }

        // totals and winner
        int[] totals = game.getTotalScores();
        y += 10;
        String totStr = "Total: ";
        for (int i = 0; i < totals.length; i++) {
            totStr += "P" + (i + 1) + "=" + totals[i];
            if (i < totals.length - 1) {
            	totStr += "   ";
            }
        }
        //g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString(totStr, rectX + 20, y + 28);

        // winner
        Player winner = game.getWinner();
        String winStr = "Winner: " + "Player " + winner.id;
        g.setColor(Color.RED);
        //g.setFont(new Font("SansSerif", Font.BOLD, 24));
        int wx = rectX + (centerW - g.getFontMetrics().stringWidth(winStr)) / 2;
        g.drawString(winStr, wx, rectY + centerH - 40);

    }
    

    // ---------- mouse: select/deselect cards from current player's hand ----------
    @Override
    public void mouseClicked(MouseEvent e) {
        Player current = game.getCurrentPlayer();
        if (game.isGameOver()) {
            repaint();
            return;
        }
        //if (cur == null) return;

        // find current player's index in list
        int currentID = -1;
        for (int i = 0; i < game.players.size(); i++) {
            if (game.players.get(i) == current) {
            	currentID = i;
            	break;
            }
        }
        if (currentID == -1) return;

        int seat = (currentID - seatOffset + 4) % 4;
        boolean vertical = (seat == 1 || seat == 3);

        // compute center location used earlier
        int fullW = getWidth(), fullH = getHeight();
        int centerRectH = Math.min(520, fullH - 160);
        int rectY = (fullH - centerRectH) / 2;
        int centerRectW = Math.min(900, fullW - 160);
        int rectX = (fullW - centerRectW) / 2;

        Point center;
        int margin = 32;
        switch (seat) {
            case 0:
            	center = new Point(fullW/2, rectY + centerRectH + margin + CARD_H/2);
            	break;
            case 1:
            	center = new Point(rectX + centerRectW + margin + SIDE_W/2, fullH/2);
            	break;
            case 2:
            	center = new Point(fullW/2, rectY - margin - CARD_H/2);
            	break;
            default:
            	center = new Point(rectX - margin - SIDE_W/2, fullH/2);
            	break;
        }

        int drawW;
        
        if (vertical) {
        	drawW = SIDE_W;
        } else {
        	drawW = CARD_W;
        }
        
        int drawH;
        
        if (vertical) {
        	drawH = SIDE_H;
        } else {
        	drawH = CARD_H;
        }
        
        int count = current.hand.size();
        if (count == 0) {
        	return;
        }

        int totalW;
        
        if (vertical) {
        	totalW = drawW;
        } else {
        	totalW = Math.max(0, count * drawW + (count - 1) * GAP);
        }
        
        int totalH;
        
        if (vertical) {
        	totalH = Math.max(0, count * drawH + (count - 1) * GAP);
        } else {
        	totalH = drawH;
        }
        
        int startX = center.x - totalW / 2;
        int startY = center.y - totalH / 2;

        int mx = e.getX(), my = e.getY();
        for (int i = 0; i < count; i++) {
            int x = startX + (vertical ? 0 : i * (drawW + GAP));
            int y = startY + (vertical ? i * (drawH + GAP) : 0);
            Rectangle r = new Rectangle(x, y, drawW, drawH);
            if (r.contains(mx, my)) {
                Card clicked = current.hand.get(i);

                // selection rules
                boolean hasChop = false;
                for (Card c : current.table) if ("chopsticks".equalsIgnoreCase(c.type)) { hasChop = true; break; }

                if (!selected.contains(clicked)) {
                    if (selected.isEmpty()) selected.add(clicked);
                    else {
                        if (hasChop && selected.size() < 2) selected.add(clicked);
                        else {
                            selected.clear();
                            selected.add(clicked);
                        }
                    }
                } else {
                    selected.remove(clicked);
                }
                repaint();
                return;
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
