import java.awt.*;
import java.awt.image.*;
import java.util.function.*;
import javax.imageio.*;
import javax.swing.*;

public class CardZoom extends JPanel {
    private BufferedImage background;
    private BufferedImage cardImage;
    private Consumer<Boolean> callback;
    private JButton forward;
    private JButton back;

    public CardZoom(BirdCard card) {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
            cardImage = ImageIO.read(getClass().getResourceAsStream("Images/Birds/" + card.name + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        restOfConstructor();
    }
    public CardZoom(BonusCard card) {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
            cardImage = ImageIO.read(getClass().getResourceAsStream("Images/BonusCards/" + card.name + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        restOfConstructor();
    }
    private void restOfConstructor() {
        forward = new JButton("Select");
        forward.setBackground(Color.GREEN);
        forward.setForeground(Color.WHITE);
		forward.setFont(new Font("SansSerif", Font.BOLD, 22));
        forward.addActionListener((e) -> callback.accept(true));
        back = new JButton("Back");
        back.setBackground(new Color(255, 87, 87));
        back.setForeground(Color.WHITE);
		back.setFont(new Font("SansSerif", Font.BOLD, 22));
        back.addActionListener((e) -> callback.accept(false));

        add(forward);
        add(back);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        g.drawImage(cardImage, (getWidth() - 677) / 2, (getHeight() - 1027) / 2, 677, 1027, null);
    }

    public void setCallback(Consumer<Boolean> a) {
        callback = a;
    }
}
