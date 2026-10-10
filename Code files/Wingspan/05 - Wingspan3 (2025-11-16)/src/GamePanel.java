import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements MouseListener {

    private Game game;
    private Player viewingPlayer;
    private int viewingIndex = 0;

    // Coordinates for board grid
    private final int GRID_X = 120;
    private final int GRID_Y = 160;
    private final int CELL_W = 150;
    private final int CELL_H = 100;

    public GamePanel(Game game) {
        this.game = game;
        this.viewingPlayer = game.getPlayer(0);  // start with player 1’s board
        addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawHeader(g);
        drawBoardGrid(g);
        drawBoardContents(g);
        drawPlayerSwitchButtons(g);
    }

    private void drawHeader(Graphics g) {
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.setColor(Color.BLACK);

        g.drawString("Active Player: Player " + (game.playerTurn + 1), 40, 40);
        g.drawString("Viewing: Player " + (viewingIndex + 1), 40, 80);
        g.drawString("Round: " + (game.round + 1) + "/4", 40, 120);
    }

    private void drawBoardGrid(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 6; c++) {
                int x = GRID_X + c * CELL_W;
                int y = GRID_Y + r * CELL_H;
                g.drawRect(x, y, CELL_W, CELL_H);
            }
        }
    }

    private void drawBoardContents(Graphics g) {
        Board b = viewingPlayer.getBoard();
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(Color.BLACK);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 6; c++) {
                Spot s = b.grid[r][c];
                int x = GRID_X + c * CELL_W;
                int y = GRID_Y + r * CELL_H;

                if (s != null && s.occupied()) {
                    g.drawString(s.card.name, x + 10, y + 20);
                    g.drawString("Eggs: " + s.getEggCount(), x + 10, y + 40);
                }
            }
        }
    }

    private void drawPlayerSwitchButtons(Graphics g) {
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        g.setColor(Color.BLACK);
        g.drawString("<", 30, getHeight() / 2);
        g.drawString(">", getWidth() - 60, getHeight() / 2);
    }

    private void switchPlayerLeft() {
        viewingIndex = (viewingIndex - 1 + game.players.length) % game.players.length;
        viewingPlayer = game.getPlayer(viewingIndex);
        repaint();
    }

    private void switchPlayerRight() {
        viewingIndex = (viewingIndex + 1) % game.players.length;
        viewingPlayer = game.getPlayer(viewingIndex);
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        // left/right toggle click area
        if (mx < 80) {
            switchPlayerLeft();
            return;
        }
        if (mx > getWidth() - 80) {
            switchPlayerRight();
            return;
        }

        // detect grid click
        int col = (mx - GRID_X) / CELL_W;
        int row = (my - GRID_Y) / CELL_H;

        if (row >= 0 && row < 3 && col >= 0 && col < 6) {
            Spot s = viewingPlayer.getBoard().grid[row][col];
            System.out.println("Clicked board spot [" + row + "," + col + "] for player " + (viewingIndex + 1));
            // placeholder: pass further events to game logic or popups
        }
    }

    // Required unused mouse methods
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}


/*
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements MouseListener {
    private Game game;
    
    public GamePanel(Game game) {
        this.game = game;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
*/