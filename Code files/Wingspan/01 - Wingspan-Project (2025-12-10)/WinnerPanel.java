import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class WinnerPanel extends JPanel {
    private BufferedImage background;
    private Game game;
    private int[] birdPoints;
    private int[] bonusPoints;
    private int[] roundGoalPoints;
    private int[] eggPoints;
    private int[] cachedFoodPoints;
    private int[] tuckedPoints;
    private int[] totalPoints;
    private int[] foodTokens;
    private ArrayList<Integer> winners;
    private JButton viewBoards;

    public WinnerPanel(Game game, int[] birdPoints, int[] bonusPoints, int[] roundGoalPoints,
                      int[] eggPoints, int[] cachedFoodPoints, int[] tuckedPoints,
                      int[] totalPoints, int[] foodTokens, ArrayList<Integer> winners) {
        this.game = game;
        this.birdPoints = birdPoints;
        this.bonusPoints = bonusPoints;
        this.roundGoalPoints = roundGoalPoints;
        this.eggPoints = eggPoints;
        this.cachedFoodPoints = cachedFoodPoints;
        this.tuckedPoints = tuckedPoints;
        this.totalPoints = totalPoints;
        this.foodTokens = foodTokens;
        this.winners = winners;
        
        setLayout(null);
        
        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        closeButton.setBackground(new Color(255, 87, 87));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> {
            // Close the game or return to menu
            System.exit(0);
        });
        add(closeButton);

        viewBoards = new JButton("View Boards");
        viewBoards.setFont(new Font("SansSerif", Font.BOLD, 24));
        viewBoards.setBackground(new Color(255, 87, 87));
        viewBoards.setForeground(Color.WHITE);
        viewBoards.addActionListener(e -> {
            game.setPanel(game.getGamePanel());
        });
        add(viewBoards);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                layoutComponents();
            }
        });
        layoutComponents();
    }
    
    private void layoutComponents() {
        if (getWidth() == 0 || getHeight() == 0) return;
        
        JButton closeButton = (JButton) getComponent(getComponentCount() - 1);
        int buttonWidth = 200;
        int buttonHeight = 50;
        closeButton.setBounds((getWidth() - buttonWidth) / 2, getHeight() - 100, buttonWidth, buttonHeight);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
        
        // Draw title
        g.setFont(new Font("Verdana", Font.BOLD, 48));
        FontMetrics titleFm = g.getFontMetrics();
        String title = "FINAL SCORES";
        int titleX = (getWidth() - titleFm.stringWidth(title)) / 2;
        g.setColor(Color.BLACK);
        g.drawString(title, titleX, 60);
        
        // Draw winner announcement
        g.setFont(new Font("Verdana", Font.BOLD, 36));
        FontMetrics winnerFm = g.getFontMetrics();
        String winnerText;
        if (winners.size() == 1) {
            winnerText = "WINNER: Player " + (winners.get(0) + 1) + "!";
        } else {
            winnerText = "TIE: Players ";
            for (int i = 0; i < winners.size(); i++) {
                if (i > 0) winnerText += ", ";
                winnerText += (winners.get(i) + 1);
            }
        }
        g.setColor(new Color(255, 215, 0)); // Gold color
        int winnerX = (getWidth() - winnerFm.stringWidth(winnerText)) / 2;
        g.drawString(winnerText, winnerX, 120);
        
        // Draw scoring breakdown for each player
        g.setFont(new Font("Verdana", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        int startY = 180;
        int lineHeight = 25;
        int playerCount = game.getPlayerCount();
        int playerWidth = playerCount > 0 ? getWidth() / playerCount : getWidth();
        
        String[] categories = {"Bird Cards:", "Bonus Cards:", "Round Goals:", 
                              "Eggs:", "Cached Food:", "Tucked Cards:", "TOTAL:"};
        
        for (int i = 0; i < playerCount; i++) {
            int playerX = i * playerWidth + 20;
            boolean isWinner = winners.contains(i);
            
            // Player header
            g.setFont(new Font("Verdana", Font.BOLD, 24));
            String playerName = "Player " + (i + 1);
            if (isWinner) {
                g.setColor(new Color(255, 215, 0));
            } else {
                g.setColor(Color.BLACK);
            }
            g.drawString(playerName, playerX, startY);
            
            // Scoring details
            g.setFont(new Font("Verdana", Font.PLAIN, 18));
            g.setColor(Color.BLACK);
            int[] values = {birdPoints[i], bonusPoints[i], roundGoalPoints[i],
                           eggPoints[i], cachedFoodPoints[i], tuckedPoints[i], totalPoints[i]};
            
            int yPos = startY + 35;
            for (int j = 0; j < categories.length; j++) {
                String text = categories[j] + " " + values[j];
                if (j == categories.length - 1) {
                    // Total line
                    g.setFont(new Font("Verdana", Font.BOLD, 20));
                    g.setColor(isWinner ? new Color(255, 215, 0) : Color.BLACK);
                    g.drawString("━━━━━━━━━━━━", playerX, yPos - 5);
                    yPos += 5;
                }
                g.drawString(text, playerX, yPos);
                yPos += lineHeight;
                if (j == categories.length - 2) {
                    g.setFont(new Font("Verdana", Font.PLAIN, 18));
                    g.setColor(Color.BLACK);
                }
            }
        }
    }
}

