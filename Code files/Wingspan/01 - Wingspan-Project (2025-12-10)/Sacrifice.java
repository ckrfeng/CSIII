import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Sacrifice extends JPanel {
    private Player player;
    private BufferedImage background;
    private Consumer<Object> callback;

    public Sacrifice(Player player, boolean forFood) {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
            if (forFood) {
                for (BirdCard card : player.getHand()) {
                    JButton cardButton = new JButton();
                    cardButton.setIcon(new ImageIcon(
                        ImageIO.read(getClass().getResourceAsStream("Images/Birds/" + card.name + ".png")).getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
                    cardButton.addActionListener((e) -> callback.accept(card));
                    add(cardButton);
                }
            } else {
                int[] food = player.getFood();
                for (int i = 0; i < food.length; i++) {
                    if (food[i] == 0) continue;
                    JButton foodButton = new JButton();
                    foodButton.setIcon(new ImageIcon(
                        ImageIO.read(getClass().getResourceAsStream("Images/Food/" + i + ".png")).getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
                    int j = i;
                    foodButton.addActionListener((e) -> callback.accept(j));
                    add(foodButton);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void setCallback(Consumer<Object> callback) {
        this.callback = callback;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }
}