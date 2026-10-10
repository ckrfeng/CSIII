
import javax.swing.*;

public class Wingspan extends JFrame {

    private JPanel panel;

    public static void main(String[] args) {
        Wingspan w = new Wingspan();
    }

    public Wingspan() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        System.out.println(this.getWidth() + " + " + this.getHeight());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        panel = new MenuPanel(this);
        add(panel);
        revalidate();
        repaint();
    }

    public void setPanel(JPanel panel) {
        remove(this.panel);
        this.panel = panel;
        add(panel);
        revalidate();
    }

    public void startGame(int playerCount) {
        Game g = new Game(this, playerCount);
        g.start();
    }
}
