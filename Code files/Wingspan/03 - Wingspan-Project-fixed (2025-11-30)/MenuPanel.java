
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MenuPanel extends JPanel implements ActionListener {
    private Wingspan frame;
    private JButton startButton;
    private JButton play2, play3, play4, play5;
    private JPanel chooser;
    public MenuPanel(Wingspan frame) {
        this.frame = frame;
        setLayout(null);
        startButton = new JButton("Click to Play");
        startButton.addActionListener(this);
        add(startButton);
        chooser = new JPanel();
        chooser.setLayout(new GridLayout(2,2,10,10));
        play2 = new JButton("2 Players"); play3 = new JButton("3 Players");
        play4 = new JButton("4 Players"); play5 = new JButton("5 Players");
        play2.addActionListener(this); play3.addActionListener(this);
        play4.addActionListener(this); play5.addActionListener(this);
        chooser.add(play2); chooser.add(play3); chooser.add(play4); chooser.add(play5);
        chooser.setVisible(false);
        add(chooser);
        JButton exit = new JButton("Exit");
        exit.addActionListener(e -> System.exit(0));
        add(exit);
    }
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == startButton) {
            chooser.setVisible(true);
            repaint();
        } else if (src == play2) {
            frame.startGame(2);
        } else if (src == play3) {
            frame.startGame(3);
        } else if (src == play4) {
            frame.startGame(4);
        } else if (src == play5) {
            frame.startGame(5);
        }
    }
    public void doLayout() {
        int w = getWidth(), h = getHeight();
        startButton.setBounds(w/2 - 100, h/2 - 40, 200, 60);
        chooser.setBounds(w/2 - 220, h/2 + 30, 440, 120);
        // place exit bottom right
        Component exit = getComponent(getComponentCount()-1);
        exit.setBounds(w-110, h-50, 100, 40);
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(30,120,30));
        g.fillRect(0,0,getWidth(),getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        g.drawString("Wingspan - Menu", 30, 70);
    }
}
