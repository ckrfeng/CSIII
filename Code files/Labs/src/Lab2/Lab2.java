package Lab2;

import javax.swing.JFrame;

public class Lab2 extends JFrame{
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 960;
    public Lab2(String title)
    {
        super(title);
        setSize (WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new FourCorners());
        setVisible(true);
    }
}