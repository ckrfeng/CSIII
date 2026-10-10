import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.BiConsumer;
import javax.imageio.ImageIO;
import javax.swing.*;

public class CardPicker extends JPanel {
    BufferedImage background;
    BiConsumer<BirdCard, ArrayList<Integer>> callback;
    ArrayList<BirdCard> hand;
    ArrayList<JButton> birdButtons;
    ArrayList<JButton> bonusButtons;
    ArrayList<JButton> habitatButtons;
    ArrayList<JButton> foodButtons;
    int[] foodTokens;
    int[] foodChosen;
    int[] foodRequired;
    Game game;
    Player player;
    int habitatChosen = -1;
    BirdCard restrict = null;
    BirdCard birdChosen;
    JButton cancel;
    JButton finish;
    JLabel no;

    public CardPicker(Game game, Player player) {
        construct(game, player);
    }
    public CardPicker(Game game, Player player, BirdCard habitatRestriction) {
        restrict = habitatRestriction;
        construct(game, player);
    }

    public void construct(Game game, Player player) {
        this.player = player;
        this.game = game;
        this.hand = player.getHand();
        this.foodTokens = player.getFood();
        this.foodChosen = new int[6];
        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
            bonusButtons = new ArrayList<>();
            for (int i = 0; i < player.getBonusCards().size(); i++) {
                JButton bonus = makeButton();
                bonus.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("Images/BonusCards/" + player.getBonusCards().get(i).name + ".png"))
                    .getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
                int j = i; // only exists because of the stupid lambda thing
                bonus.setBackground(Color.GRAY);
                add(bonus);
            }
            birdButtons = new ArrayList<>();
            for (int i = 0; i < hand.size(); i++) {
                JButton bird = makeButton();
                bird.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("Images/Birds/" + hand.get(i).name + ".png"))
                    .getScaledInstance(200, 280, Image.SCALE_DEFAULT)));
                int j = i; // only exists because of the stupid lambda thing
                birdButtons.add(bird);
                if (player.foodEnoughFor(hand.get(i))) bird.addActionListener(e -> {
                    game.setPanel(cardZoom(j));
                }); else bird.setBackground(Color.GRAY);
                add(bird);
            }
            foodButtons = new ArrayList<>();
            for (int i = 0; i < foodTokens.length; i++) {
            ImageIcon icon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("Images/Food/" + i + ".png")).getScaledInstance(50, 50, Image.SCALE_DEFAULT));
                for (int j = 0; j < foodTokens[i]; j++) {
                    JButton food = makeButton();
                    foodButtons.add(food);
                    food.setIcon(icon);
                    int ii = i; // only exists because of the stupid lambda thing
                    food.addActionListener((e) -> {
                        if (birdChosen == null) return;
                        if (food.getBackground() == Color.GREEN) {
                            foodChosen[ii]--;
                            food.setBackground(new Color(255, 87, 87));
                            refreshFood();
                            if (!player.foodEnoughFor(birdChosen, foodChosen)) birdButtons.get(hand.indexOf(birdChosen)).setBackground(Color.BLUE);
                        } else {
                            if (birdButtons.get(hand.indexOf(birdChosen)).getBackground() == Color.GREEN) return;
                            if (foodRequired[ii] > 0) {
                                foodRequired[ii]--;
                            } else if (foodRequired[5] > 0) {
                                foodRequired[ii]--;
                            } else return;

                            foodChosen[ii]++;
                            food.setBackground(Color.GREEN);
                            if (player.foodEnoughFor(birdChosen, foodChosen)) birdButtons.get(hand.indexOf(birdChosen)).setBackground(Color.GREEN);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cancel = makeButton();
        cancel.setText("Cancel");
        cancel.addActionListener((e) -> callback.accept(null, null));
        add(cancel);

        habitatButtons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
			JButton action = makeButton();
			add(action);
            habitatButtons.add(action);
            int j = i;
            if (restrict != null && !restrict.canInhabit(j)) {
                habitatButtons.get(i).setBackground(Color.GRAY);
            } else action.addActionListener(e -> {
                if (birdChosen == null) {sayNo("Select a card"); return;}
                if(!birdChosen.canInhabit(j)) {sayNo("Invalid habitat"); return;}
                habitatChosen = j;
                makeButtonsRed(habitatButtons);
                action.setBackground(Color.GREEN);
            });
		}
        habitatButtons.get(0).setText("Forest");
        habitatButtons.get(1).setText("Prairie");
        habitatButtons.get(2).setText("Wetlands");

        finish = makeButton();
        finish.setText("Finish");
        finish.setBackground(Color.BLUE);
        finish.addActionListener((e) -> {
            if (birdChosen == null) sayNo("Select a card");
            else if (!player.foodEnoughFor(birdChosen, foodChosen)) sayNo("Not enough food");
            else if (habitatChosen == -1) sayNo("Select a habitat");
            else {
                ArrayList<Integer> foodAndHabitat = new ArrayList<>();
                for (int food : foodChosen) foodAndHabitat.add(food);
                foodAndHabitat.add(habitatChosen);
                callback.accept(birdChosen, foodAndHabitat);
            }
        });
        add(finish);

        no = new JLabel("Unable to play card");
        no.setFont(new Font("SansSerif", Font.BOLD, 22));
        no.setBackground(new Color(0, 0, 0, 255));
        no.setForeground(Color.WHITE);
        add(no);
        no.setVisible(false);

        for (JButton food : foodButtons) add(food);
    }

    public CardZoom cardZoom(int index) {
        CardZoom zoom = new CardZoom(hand.get(index));
        zoom.setCallback((b) -> {
            if (b) {
                birdChosen = this.hand.get(index);
                makeButtonsRed(birdButtons);
                foodChosen = new int[6];
                makeButtonsRed(foodButtons);
                habitatChosen = -1;
                makeButtonsRed(habitatButtons);
                birdButtons.get(index).setBackground(Color.BLUE);
                if (player.foodEnoughFor(hand.get(index), new int[6])) birdButtons.get(index).setBackground(Color.GREEN);
                foodRequired = hand.get(index).foodCost.clone();
            }
            game.setPanel(CardPicker.this);
        });
        return zoom;
    }
    
    public void refreshFood() {
        foodRequired = birdChosen.foodCost.clone();
        for (int i = 0; i < foodChosen.length; i++) {
            for (int j = 0; j < foodChosen[i]; j++) {
                if (foodRequired[i] > 0) foodRequired[i]--;
                else if (foodRequired[5] > 0) foodRequired[5]--;
                else foodRequired[i]--;
            }
        }
    }

    public void setCallback(BiConsumer<BirdCard, ArrayList<Integer>> callback) {
        this.callback = callback;
    }

    public JButton makeButton() {
        JButton button = new JButton();
        button.setFont(new Font("SansSerif", Font.BOLD, 22));
        button.setBackground(new Color(255, 87, 87));
        button.setForeground(Color.WHITE);
        return button;
    }

    public void makeButtonsRed(ArrayList<JButton> l) {
        for (JButton b : l) b.setBackground(new Color(255, 87, 87));
    }

    public void sayNo(String msg) {
        no.setText(msg);
        no.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }
}
