import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {
    CardLayout layout = new CardLayout();
    JPanel root = new JPanel(layout);
    MenuPanel menu;
    GamePanel game;

    public AppFrame() {
        super("Sushi Go! - Pass & Play");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        menu = new MenuPanel(this::startGame);
        root.add(menu, "menu");
        add(root);
    }

    private void startGame(int players) {
        game = new GamePanel(players, this::goToMenu);
        root.add(game, "game");
        layout.show(root, "game");
    }

    private void goToMenu() {
        layout.show(root, "menu");
        if (game != null) {
            root.remove(game);
            game = null;
        }
    }
}
