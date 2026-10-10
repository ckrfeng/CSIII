import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Prompt extends JPanel {
    private JLabel prompt;
    private JButton accept;
    private JButton deny;
    private Consumer<Object> callback;
    private BufferedImage background;

    public Prompt(String msg) {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        prompt = new JLabel(msg);
        prompt.setFont(new Font("Verdana", Font.BOLD, 50));
        add(prompt);

        accept = new JButton("Accept");
		accept.setFont(new Font("SansSerif", Font.BOLD, 22));
        accept.setBackground(Color.GREEN);
        accept.addActionListener((e) -> callback.accept(true));
        add(accept);

        deny = new JButton("Deny");
		deny.setFont(new Font("SansSerif", Font.BOLD, 22));
        deny.setBackground(Color.RED);
        deny.addActionListener((e) -> callback.accept(false));
        add(deny);
    }
    public Prompt(String msg, Object[] options) {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        prompt = new JLabel(msg);
        prompt.setFont(new Font("Verdana", Font.BOLD, 50));
        add(prompt);

        for (Object o : options) {
            JButton option = new JButton();
            option.setBackground(new Color(255, 87, 87));
            option.setFont(new Font("SansSerif", Font.BOLD, 22));

            if (o instanceof BirdCard bc) {
                try {
                    option.setIcon(new ImageIcon(
                        ImageIO.read(getClass().getResourceAsStream("Images/Birds/" + bc.name + ".png")).getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
                    option.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (o instanceof BonusCard bc) {
                try {
                    option.setIcon(new ImageIcon(
                        ImageIO.read(getClass().getResourceAsStream("Images/BonusCards/" + bc.name + ".png")).getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
                    option.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (o instanceof Integer val) {
                try {
                    option.setIcon(new ImageIcon(
                        ImageIO.read(getClass().getResourceAsStream("Images/Food/" + val + ".png")).getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
                    option.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (o instanceof Player p) {
                int idx = p.game.getPlayerIndex(p);
                option.setText("Player " + (idx + 1));
            } else {
                option.setText(String.valueOf(o));
            }

            option.addActionListener((e) -> callback.accept(o));
            add(option);
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
