package Lab3;

import javax.swing.JFrame;

public class Lab3 extends JFrame {
    public Lab3(String title) {
        super(title);
        setSize(1600, 960);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new MenuPanel());
        setVisible(true);
    }
}