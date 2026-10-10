import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BirdFeeder extends JPanel {
    private BufferedImage background;
    private Consumer<Integer> callback;
    private JButton cancel;
    private int count;
    private ArrayList<Die> dice;
    private ImageIcon[] icons;
    private JButton rollButton;
    private JButton[] wheatWorm;

    public BirdFeeder() {
        icons = new ImageIcon[6];
        try {
            background = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/Background.jpg"));
            for (int i = 0; i < 6; i++) {
                BufferedImage image = ImageIO.read(getClass().getResourceAsStream("Images/Food/" + i + ".png"));
                icons[i] = new ImageIcon(image.getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            }
        } catch (Exception e) {e.printStackTrace();}

        cancel = classicStyle();
        cancel.setText("Cancel");
        cancel.addActionListener((e) -> callback.accept(-2));
        cancel.setVisible(false);
        add(cancel);
        
        dice = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dice.add(new Die((int)(Math.random() * 6))); // THE SIXTH TOKEN IS THE WHEAT/WORM TOKEN, NOT AN "OTHER" TOKEN
            add(dice.get(dice.size() - 1));
        }

        rollButton = classicStyle();
        rollButton.setText("Reroll Dice");
        rollButton.addActionListener((e) -> roll());
        rollButton.setVisible(false);
        add(rollButton);

        wheatWorm = new JButton[2];
        for (int i = 0; i < 2; i++) {
            wheatWorm[i] = classicStyle();
            int j = i;
            wheatWorm[i].addActionListener((e) -> answerWheatWorm(j));
        }
        wheatWorm[0].setText("Select Wheat");
        wheatWorm[1].setText("Select Worm");
    }

    private JButton classicStyle() {
        JButton b = new JButton();
		b.setFont(new Font("SansSerif", Font.BOLD, 22));
        b.setBackground(new Color(255, 87, 87));
		b.setForeground(Color.WHITE);
        return b;
    }

    public void roll() {
        dice.clear();
        removeAll();
        add(cancel);
        for (int i = 0; i < 5; i++) {
            dice.add(new Die((int)(Math.random() * 6)));
            add(dice.get(dice.size() - 1));
        }
        add(rollButton);
        if (allSame()) rollButton.setVisible(true);
        else rollButton.setVisible(false);
        revalidate();
        repaint();
    }

    public int getDiceCount() {
        return dice.size();
    }

    public int removeDie(Die d) {
        dice.remove(d);
        remove(d);
        if (dice.isEmpty()) roll();
        revalidate();
        repaint();

        return d.value();
    }
    	
    public boolean allSame() {
        if (dice.isEmpty()) return true;
        int d = dice.get(0).value();
        for (int i = 1; i < dice.size(); i++) {
            if (dice.get(i).value() != d) return false;
        }
        return true;
    }

    public void fireCallback(int a) {
        if (allSame()) rollButton.setVisible(true);
        repaint();
        revalidate();
        callback.accept(a);
        if (a == -2) {count = 0; return;}
        count--;
        if (count == 0) callback.accept(-1);
        cancel.setVisible(false);
    }

    public void setCallback(Consumer<Integer> a) {
        callback = a;
    }

    public void setCount(int n) {
        count = n;
        cancel.setVisible(true);
    }
    
    public boolean removeDieByVal(int val) {
    		for(Die d: dice) {
    			if(d.value() == val) {
    				removeDie(d); 
    				return true; 
    			}
    		}
    		return false; 
    }
    
    public boolean hasDieValue(int val) {
    		for(Die d: dice) {
    			if(d.value() == val) {
    				return true; 
    			}
    		}
    		return false;
    }
    
    public int takeAllOfType(int foodIndex) { 
        int count = 0;
        Iterator<Die> it = dice.iterator();
        while (it.hasNext()) {
            Die d = it.next();
            if (d.value() == foodIndex) { 
                count++;            
                it.remove();       
                remove(d);
            }
        }
        revalidate();
        repaint();
        return count; 
    }
    
    // Roll dice NOT in birdfeeder (for hunting/fishing powers)
    // Returns an array of the rolled values
    public int[] rollDiceNotInFeeder() {
        int diceInFeeder = dice.size();
        int diceToRoll = 5 - diceInFeeder; // Total 5 dice
        int[] results = new int[diceToRoll];
        for (int i = 0; i < diceToRoll; i++) {
            results[i] = (int)(Math.random() * 6);
        }
        return results;
    }
    
    public boolean rollContainsType(int[] rollResults, int foodType) {
        for (int val : rollResults) {
            if (val == foodType) return true;
        }
        return false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }

    public void promptWheatWorm() {
        removeAll();
        for (JButton b : wheatWorm) add(b);
        revalidate();
    }

    public void answerWheatWorm(int i) {
        for (JButton b : wheatWorm) remove(b);
        add(cancel);
        cancel.setVisible(false);
        for (Die d : dice) add(d);
        add(rollButton);
        revalidate();
        fireCallback(i == 0 ? 3 : 4);
    }

    class Die extends JButton {
        int v;

        public Die(int v) {
            super();
            setIcon(icons[v]);
            this.v = v;
            addActionListener((e) -> {
                if (v == 5) {
                    removeDie(this);
                    promptWheatWorm();
                } else if (callback != null) {
                    fireCallback(removeDie(this));
                }
            });
        }

        public int value() {
            return v;
        }
    }
}
