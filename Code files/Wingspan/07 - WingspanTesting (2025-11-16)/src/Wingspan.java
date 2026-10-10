/*
import javax.swing.*;

public class Wingspan extends JFrame {

    private JPanel panel;
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Wingspan());
    }

    public Wingspan() {
        panel = new MenuPanel(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.NORMAL);
        add(panel);

        setVisible(true);
    }

    public void setPanel(JPanel panel) {
        getContentPane().removeAll();
        this.panel = panel;
        add(panel);
        revalidate();
        repaint();
    }

    public void startGame(int playerCount) {
        Game g = new Game(this, playerCount);
        // Game constructor will call frame.setPanel with a GamePanel (as your original code did)
    }
}
*/


import javax.swing.*;

public class Wingspan extends JFrame {

    private JPanel panel;

    public static void main(String[] args) {
        Wingspan w = new Wingspan();
    }

    public Wingspan() {
        panel = new MenuPanel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        add(panel);

        setVisible(true);
    }

    public void setPanel(JPanel panel) {
        remove(this.panel);
        this.panel = panel;
        add(panel);
        revalidate();
    }

    public void startGame(int playerCount) {
        Game g = new Game(this, playerCount);
    }
}
