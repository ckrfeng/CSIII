import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ScoringSheetPanel extends JPanel implements ComponentListener, MouseListener {
    private Game game;
    private BufferedImage background;
    private BufferedImage goalsImage;
    private BufferedImage[] scoreTiles;
    private JButton continueButton;
    private double[][] scoreTilePositions = {{0.05, 0.09}, {0.28, 0.09}, {0.51, 0.09}, {0.74, 0.09}};
    private double tileWidthRatio = 0.22;
    private double tileHeightRatio = 0.17;

    public ScoringSheetPanel(Game game) {
        this.game = game;
        setLayout(null);
        addComponentListener(this);
        addMouseListener(this);
        
        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
            goalsImage = ImageIO.read(getClass().getResourceAsStream("Images/Scores/Goals.png"));
            
            int[] selectedTiles = game.getSelectedScoreTiles();
            scoreTiles = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                scoreTiles[i] = ImageIO.read(getClass().getResourceAsStream("Images/Scores/SCORE" + selectedTiles[i] + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        continueButton = new JButton("Continue");
        continueButton.addActionListener(e -> {
            SetupPanel setup = new SetupPanel(game, game.getPlayerCount());
            game.setPanel(setup);
        });
        add(continueButton);
        
        layoutComponents();
    }
    
    private void layoutComponents() {
        int width = getWidth();
        int height = getHeight();
        
        if (width == 0 || height == 0) return;
        
        continueButton.setFont(ScalableGUI.scaleFont("SansSerif", Font.BOLD, 22, width, height));
        continueButton.setBackground(new Color(255, 87, 87));
        
        int buttonWidth = ScalableGUI.percentWidth(10, width);
        int buttonHeight = ScalableGUI.percentHeight(5, height);
        int buttonX = ScalableGUI.percentX(45, width);
        int buttonY = ScalableGUI.percentY(90, height);
        
        continueButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
        
        if (goalsImage != null) {
            int imgWidth = goalsImage.getWidth();
            int imgHeight = goalsImage.getHeight();

            double scale = Math.min(
                (double)(getWidth() * 0.8) / imgWidth,
                (double)(getHeight() * 0.7) / imgHeight
            );
            
            int scaledWidth = (int)(imgWidth * scale);
            int scaledHeight = (int)(imgHeight * scale);
            int goalsX = (getWidth() - scaledWidth) / 2;
            int goalsY = (getHeight() - scaledHeight) / 2 - ScalableGUI.percentHeight(5, getHeight());
            
            g.drawImage(goalsImage, goalsX, goalsY, scaledWidth, scaledHeight, this);

            if (scoreTiles != null) {
                int tileWidth = (int)(scaledWidth * tileWidthRatio);
                int tileHeight = (int)(scaledHeight * tileHeightRatio);
                
                for (int i = 0; i < 4 && i < scoreTiles.length; i++) {
                    if (scoreTiles[i] != null) {
                        int tileX = goalsX + (int)(scaledWidth * scoreTilePositions[i][0]);
                        int tileY = goalsY + (int)(scaledHeight * scoreTilePositions[i][1]);
                        
                        g.drawImage(scoreTiles[i], tileX, tileY, tileWidth, tileHeight, this);
                    }
                }
            }
        }
    }

    public void componentResized(ComponentEvent e) {
        layoutComponents();
        repaint();
    }
    
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}

    public void mouseClicked(MouseEvent e) {
        int clickX = e.getX();
        int clickY = e.getY();
        
        System.out.println("Click position - Panel coordinates: (" + clickX + ", " + clickY + ")");
        
        if (goalsImage != null) {
            int imgWidth = goalsImage.getWidth();
            int imgHeight = goalsImage.getHeight();
            
            double scale = Math.min(
                (double)(getWidth() * 0.8) / imgWidth,
                (double)(getHeight() * 0.7) / imgHeight
            );
            
            int scaledWidth = (int)(imgWidth * scale);
            int scaledHeight = (int)(imgHeight * scale);
            int goalsX = (getWidth() - scaledWidth) / 2;
            int goalsY = (getHeight() - scaledHeight) / 2 - ScalableGUI.percentHeight(5, getHeight());
            
            if (clickX >= goalsX && clickX <= goalsX + scaledWidth &&
                clickY >= goalsY && clickY <= goalsY + scaledHeight) {
                
                double relativeX = (double)(clickX - goalsX) / scaledWidth;
                double relativeY = (double)(clickY - goalsY) / scaledHeight;
                
                System.out.println("  Relative to Goals.png: (" + 
                    String.format("%.3f", relativeX) + ", " + 
                    String.format("%.3f", relativeY) + ")");
                System.out.println("  Use in scoreTilePositions: {" + 
                    String.format("%.2f", relativeX) + ", " + 
                    String.format("%.2f", relativeY) + "}");
            } else {
                System.out.println("  Click is outside Goals.png area");
            }
        }
    }
    
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}