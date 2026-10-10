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
    
    public final int x = 300;
    public final int y = 80;

    public MenuPanel(Wingspan frame) {
        this.frame = frame;
        setLayout(null);

        try {
        	cover = ImageIO.read(MenuPanel.class.getResource("/images/WingspanCover.jpg"));
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        startButton = new JButton("Click to Play");
        startButton.addActionListener(this);
        
        startButton.setBounds((frame.getWidth() - x) / 2, (frame.getHeight() - y) / 2, x, y);
        
        startButton.setFont(new Font ("SansSerif", Font.PLAIN, 22));
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
        JButton play2 = new JButton("2 Players");
        JButton play3 = new JButton("3 Players");
        JButton play4 = new JButton("4 Players");
        JButton play5 = new JButton("5 Players");

        play2.setBounds((frame.getWidth() - x) / 3, (frame.getHeight() - y) / 3, x, y);
        play3.setBounds(2 * (frame.getWidth() - x) / 3, (frame.getHeight() - y) / 3, x, y);
        play4.setBounds((frame.getWidth() - x) / 3, 2 * (frame.getHeight() - y) / 3, x, y);
        play5.setBounds(2 * (frame.getWidth() - x) / 3, 2 * (frame.getHeight() - y) / 3, x, y);

        play2.addActionListener(e -> frame.startGame(2));
        play3.addActionListener(e -> frame.startGame(3));
        play4.addActionListener(e -> frame.startGame(4));
        play5.addActionListener(e -> frame.startGame(5));
        
        play2.setFont(new Font ("SansSerif", Font.PLAIN, 22));
        play3.setFont(new Font ("SansSerif", Font.PLAIN, 22));
        play4.setFont(new Font ("SansSerif", Font.PLAIN, 22));
        play5.setFont(new Font ("SansSerif", Font.PLAIN, 22));

        add(play2);
        add(play3);
        add(play4);
        add(play5);
        
        revalidate();
        repaint();
    }
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	if (!started) {
    		startButton.setBounds((frame.getWidth() - x) / 2, (frame.getHeight() - y) / 2 + 240, x, y);
    		
    		
    		//g.drawImage(cover, 0, 0, frame.getWidth(), frame.getHeight(), null);
    		/*
    		if (cover == null) {
    		    System.out.println("Failed to load image!");
    		} else {
    		    g.drawImage(cover, 0, 0, getWidth(), getHeight(), this);
    		}
    		*/
    	}
    }
}