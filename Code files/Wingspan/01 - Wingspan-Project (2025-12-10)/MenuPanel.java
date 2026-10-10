import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MenuPanel extends JPanel implements ActionListener, ComponentListener {
    private Wingspan frame;
    private boolean started = false;
    private int numPlayers;
    
    private JButton startButton;
    private JButton play2, play3, play4, play5;
    
    public static BufferedImage cover;
    
    public int x;
    public int y;

    public MenuPanel(Wingspan frame) {
        this.frame = frame;
        setLayout(null);
        addComponentListener(this);

        try {
        	cover = ImageIO.read(MenuPanel.class.getResource("/Images/Backgrounds/WingspanCover.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startButton = new JButton("Click to Play");
        startButton.addActionListener(this);
                
        startButton.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(startButton);
    }

    public void actionPerformed(ActionEvent e) {
        // started = true;
        // showSelection();
        frame.startGame(2);
    }
    
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	g.setColor(new Color(240, 245, 255));
        g.fillRect(0, 0, getWidth(), getHeight());
        
    	if (!started) {
    		String screen = "Wingspan";
    		g.setFont(new Font("SansSerif", Font.BOLD, 56));

    		FontMetrics metrics = g.getFontMetrics();
    		
    		int width = (getWidth() - metrics.stringWidth(screen))/2;
    		int height = (getHeight() - metrics.getHeight())/2;
    		
    		g.setColor(Color.BLACK);
    		g.drawString(screen, width, height);
            
    		startButton.setBackground(new Color(255, 255, 255, 128));
    	}
        layoutComponents();
    	g.drawImage(cover, 0, 0, frame.getWidth(), frame.getHeight(), null);
    }

    public void addNotify() {
        super.addNotify();
        layoutComponents();
    }

    private void layoutComponents() {
        int width = getWidth();
        int height = getHeight();
        
        if (width == 0 || height == 0) return;
        
        int buttonWidth = ScalableGUI.percentWidth(15.625, width);
        int buttonHeight = ScalableGUI.percentHeight(7.4, height);
        
        if (startButton != null) {
            startButton.setFont(ScalableGUI.scaleFont("SansSerif", Font.BOLD, 28, width, height));
            startButton.setBounds(ScalableGUI.percentX(42.2, width), ScalableGUI.percentY(67, height), buttonWidth, buttonHeight);
        }
    }

    public void componentResized(ComponentEvent e) {
        layoutComponents();
        repaint();
    }
    
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {
        layoutComponents();
    }
    public void componentHidden(ComponentEvent e) {}
}
