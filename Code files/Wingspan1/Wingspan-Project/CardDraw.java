import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;

public class CardDraw extends JPanel {
    BufferedImage background;
    JButton deckButton;
    JLabel drawMessage;
    Consumer<BirdCard> callback;
    JButton cancel;
    BirdCard[] faceUp;
    JButton[] faceUpButtons;
    Game game;
    int count;

    public CardDraw(Game game) {
        this.game = game;

        cancel = new JButton("Cancel");
        cancel.setBackground(new Color(255, 87, 87));
        cancel.setForeground(Color.WHITE);
        cancel.setFont(new Font("SansSerif", Font.BOLD, 22));
        cancel.addActionListener((e) -> {
            count = 0;
            fireCallback(null);
        });
        add(cancel);

        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
            deckButton = new JButton();
            deckButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("Images/Birds/FaceDown.png")).getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
            deckButton.addActionListener((e) -> {
                BirdCard drawn = game.drawBird();
                fireCallback(drawn);
                drawMessage.setText("Drew " + drawn.name);
                drawMessage.setVisible(true);
            });
            add(deckButton);

            faceUp = new BirdCard[3];
            faceUpButtons = new JButton[3];
            for (int i = 0; i < 3; i++) {
                faceUp[i] = game.drawBird();
                JButton button = new JButton();
                button.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("Images/Birds/" + faceUp[i].name + ".png")).getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
                int j = i;
                button.addActionListener((e) -> {
                    game.setPanel(cardZoom(j));
                });
                faceUpButtons[i] = button;
                add(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        drawMessage = new JLabel();
        drawMessage.setFont(new Font("SansSerif", Font.BOLD, 22));
		drawMessage.setBackground(new Color(255, 87, 87));
		drawMessage.setForeground(Color.WHITE);
		drawMessage.setVisible(false);
        add(drawMessage);
    }

    public void setCallback(Consumer<BirdCard> callback) {
        this.callback = callback;
    }

    public CardZoom cardZoom(int index) {
        CardZoom zoom = new CardZoom(faceUp[index]);
        zoom.setCallback((b) -> {
            game.setPanel(CardDraw.this);
            if (!b) return;
            fireCallback(faceUp[index]);
            drawMessage.setText("Drew " + faceUp[index].name);
            drawMessage.setVisible(true);
            faceUp[index] = game.drawBird();
            try {
                faceUpButtons[index].setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("Images/Birds/" + faceUp[index].name + ".png")).getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        });
        return zoom;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void fireCallback(BirdCard card) {
        count--;
        callback.accept(card);
        if (count == 0) {
            callback.accept(null);
            drawMessage.setVisible(false);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }
    
    public void replenishTray() {
        // Discard all face-up cards and draw 3 new ones
        for (int i = 0; i < 3; i++) {
            faceUp[i] = game.drawBird();
            try {
                if (faceUp[i] != null) {
                    faceUpButtons[i].setIcon(new ImageIcon(
                        ImageIO.read(getClass().getResourceAsStream("Images/Birds/" + faceUp[i].name + ".png"))
                            .getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        repaint();
    }
}