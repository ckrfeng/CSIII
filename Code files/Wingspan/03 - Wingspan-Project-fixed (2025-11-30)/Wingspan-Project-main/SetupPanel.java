import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/*DISCLAIMER THIS SHOWS ONLY TWO SCREENS AND IT DOES NOT DIFFERENTIATE BETWEEN THE NUMBER 
 * OF PLAYERS THAT ARE CHOSEN IN THE GAME IT IS THE BASIC OUTLOOK PLS MAKE ANY CHANGS NECESSARY
 * WHOEVER WORKS ON THIS, IT IS AWFUL CODE OVERALL I WAS JS GETTING USED TO USING JBUTTON*/

// bro this code is aagaggagagag

public class SetupPanel extends JPanel {
    private Game game;
    private BufferedImage selectionBonus; 
    private BufferedImage selectionStuff;
	private BufferedImage which;
	private JButton doneButton;
	private JButton[] birdButtons;
	private BirdCard[] birds;
    private JButton[] bonusButtons;
	private BonusCard[] bonuses;
	private JButton[] foodButtons;

	ArrayList<Integer> selected = new ArrayList<>();
	Color selectedColor = new Color(125, 125, 125, 50);
	Color transparentColor = new Color(255, 255, 255, 0);
	private int playerCount;
	private int n;

    public SetupPanel(Game game, int playerCount) {
    	//setLayout(null); no layout bc of convenience and not setbounds for that reason 
        this.game = game;
		this.playerCount = playerCount;
		n = playerCount * 2;
        try {
           selectionStuff = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/ChooseBird.png"));
           selectionBonus = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/ChooseBonus.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
		doneButton = new JButton("Continue");
		doneButton.addActionListener(e -> moveOn());
		doneButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        doneButton.setBackground(new Color(255, 87, 87));
		doneButton.setForeground(Color.WHITE);
		birdButtons = new JButton[5];
		foodButtons = new JButton[5];
		bonusButtons = new JButton[2];
		for (int i = 0; i < 5; i++) {
			birdButtons[i] = makeMeAButton("Bird " + (i + 1));
			int j = i;
			birdButtons[i].addActionListener(e -> zoomBird(j));
		}
		for (int i = 0; i < 5; i++) {
			foodButtons[i] = makeMeAButton("Food " + (i + 1));
			int j = i;
			ImageIcon pic = new ImageIcon(new ImageIcon("Images/Food/" + i + ".png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
			foodButtons[i].setIcon(pic);
			foodButtons[i].addActionListener(e -> choose(j, 1));
		}
		for (int i = 0; i < 2; i++) {
			bonusButtons[i] = makeMeAButton("Select Bonus " + (i + 1));
			int j = i;
			bonusButtons[i].addActionListener(e -> zoomBonus(j));
		}
		kabow();
   	}

	public JButton makeMeAButton(String text) {
		JButton button = new JButton(text);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setRolloverEnabled(false);
		button.setBackground(transparentColor);
		return button;
	}

	public void zoomBonus(int i) {
		CardZoom z = new CardZoom(bonuses[i]);
		game.setPanel(z);
		revalidate();
		z.choice((b) -> {
			if (b) choose(i, 2);
			if (n != 0) game.setPanel(this);
			repaint();
			revalidate();
		});
	}
	public void zoomBird(int i) {
		if (selected.contains(i) || selected.size() >= 5) {
			choose(i, 0);
			return;
		}

		CardZoom z = new CardZoom(birds[i]);
		game.setPanel(z);
		revalidate();
		z.choice((b) -> {
			if (b) choose(i, 0);
			game.setPanel(this);
			repaint();
			revalidate();
		});
	}

   	public void kabow() {
		if(n == 0) {
			game.start();
			return;
		}
		removeAll();
		if (n % 2 == 1) {
			which = selectionBonus;
			bonuses = new BonusCard[5];
			for (int i = 0; i < 2; i++) {
				bonuses[i] = game.drawBonus();
				ImageIcon pic = new ImageIcon(new ImageIcon("Images/Birds/Placeholder.jpg").getImage().getScaledInstance(100, 150, Image.SCALE_DEFAULT)); // placeholder image
				bonusButtons[i].setIcon(pic);
				add(bonusButtons[i]);
			}
			for (JButton b : birdButtons) b.setBackground(transparentColor);
			for (JButton b : foodButtons) b.setBackground(transparentColor);
		} else {
			which = selectionStuff;
			birds = new BirdCard[5];
			for (int i = 0; i < 5; i++) {
				birds[i] = game.drawBird();
				ImageIcon pic = new ImageIcon(new ImageIcon("Images/Birds/Placeholder.jpg").getImage().getScaledInstance(100, 150, Image.SCALE_DEFAULT)); // placeholder image
				birdButtons[i].setIcon(pic);
				add(birdButtons[i]);
			}
			for (int i = 0; i < 5; i++) {
				add(foodButtons[i]);
			}
			add(doneButton);
		}
		revalidate();
		repaint();
   	}
    
	public void moveOn() {
		if (selected.size() != 5) return;
		n--;
		kabow();
	}

    public void choose(int i, int type) { // 0: bird; 1: food; 2: bonus
		switch (type) {
			case 2:
			Player p = game.getPlayer(playerCount - 1 - n / 2);
			for (int s : selected) {
				if (s < 0) p.addFood(-s - 1);
				else p.addBird(birds[s]);
			}
			p.addBonus(bonuses[i]);
			selected.clear();
			n--;
			kabow();
			return;
			case 0:
			if (birdButtons[i].getBackground() != selectedColor && selected.size() < 5) {
				selected.add(i);
				birdButtons[i].setBackground(selectedColor);
			} else if (birdButtons[i].getBackground() == selectedColor) {
				selected.remove(Integer.valueOf(i));
				birdButtons[i].setBackground(transparentColor);
			}
			break;
			case 1:
			if (foodButtons[i].getBackground() != selectedColor && selected.size() < 5) {
				selected.add(-i - 1);
				foodButtons[i].setBackground(selectedColor);
			} else if (foodButtons[i].getBackground() == selectedColor) {
				selected.remove(Integer.valueOf(-i - 1));
				foodButtons[i].setBackground(transparentColor);
			}
		}
	}
   
    public void paintComponent(Graphics g){
        super.paintComponent(g);
		g.drawImage(which, 0, 0, getWidth(), getHeight(), this);
    }
}