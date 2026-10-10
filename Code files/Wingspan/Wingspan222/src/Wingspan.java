
import javax.swing.*;

public class Wingspan extends JFrame {

    private JPanel panel;

    public static void main(String[] args) {
        Wingspan w = new Wingspan();
    }

    public Wingspan() {
        panel = new MenuPanel(this);

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