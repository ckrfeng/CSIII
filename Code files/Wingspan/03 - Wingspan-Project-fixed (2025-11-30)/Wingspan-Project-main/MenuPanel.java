import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MenuPanel extends JPanel implements ActionListener {
    private Wingspan frame;
    private boolean started = false;
    private int numPlayers;
    
    private JButton startButton;
    private JButton play2, play3, play4, play5;
    
    public static BufferedImage cover;
    
    public int x;
    public int y;
    //x=300
    //y=80

    public MenuPanel(Wingspan frame) {
        this.frame = frame;
        setLayout(null);

        try {
        	cover = ImageIO.read(MenuPanel.class.getResource("/Images/Backgrounds/WingspanCover.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        /*
        x = (int)(0.15625*frame.getWidth());
        y = (int)(0.0740740741*frame.getHeight());
        System.out.println("x: " + x + " y: " + y);
        */
        startButton = new JButton("Click to Play");
        startButton.addActionListener(this);
        
        //startButton.setBounds((frame.getWidth() - x) / 2, (frame.getHeight() - y) / 2, x, y);
        
        startButton.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(startButton);
        
    }

    public void actionPerformed(ActionEvent e) {
        started = true;
        showSelection();
    }

    public void numberPlayers(ActionEvent e, int num) {
        frame.startGame(num);
    }

    public void showSelection() {
        removeAll();
        JButton play2 = createPlayerButton("2 Players", 2);
        JButton play3 = createPlayerButton("3 Players", 3);
        JButton play4 = createPlayerButton("4 Players", 4);
        JButton play5 = createPlayerButton("5 Players", 5);

        play2.setBounds((frame.getWidth() - x) / 3, (frame.getHeight() - y) / 3, x, y);
        play3.setBounds(2 * (frame.getWidth() - x) / 3, (frame.getHeight() - y) / 3, x, y);
        play4.setBounds((frame.getWidth() - x) / 3, 2 * (frame.getHeight() - y) / 3, x, y);
        play5.setBounds(2 * (frame.getWidth() - x) / 3, 2 * (frame.getHeight() - y) / 3, x, y);
        
        add(play2);
        add(play3);
        add(play4);
        add(play5);
        
        revalidate();
        repaint();
    }
    
    public JButton createPlayerButton(String text, int number) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 22));
        b.addActionListener(e -> frame.startGame(number));
        b.setBackground(new Color(255, 255, 255, 128));
        return b;
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
    		startButton.setBounds((frame.getWidth() - x) / 2, (frame.getHeight() - y) / 2 + (int)(0.167*getHeight()), x, y);
    		
    	}
    	g.drawImage(cover, 0, 0, frame.getWidth(), frame.getHeight(), null);
    }

    public void addNotify() {
        super.addNotify();
        x = (int) (0.15625 * frame.getWidth());
        y = (int) (0.0740740741 * frame.getHeight());
        System.out.println("x: " + x + " y: " + y);
        startButton.setBounds((frame.getWidth() - x) / 2, (frame.getHeight() - y) / 2, x, y);
    }
}
