import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

// i followed the outline but the location still looked dog for me 
/*runAction( This is to help fedhil see the different arguments to pass and beware to run this when a card a played if its onplay
 * I left between turns because it requires gamepanel event triggering to see the different actions that another player does i hope 
 * maybe carter takes care of that idk
	    					     id,  
	    					   	 spot,  
	    					   	 giving,  
	    					   	 trading,  
	    					   	 getFood1, 
	    			    		 getFood2,  
	    			    		 gridX,  
	    			    		 gridY,  
	    			    		 gridX2,  
	    			    		 gridY2,  
	    			    		 playerNum,  
	    			    		 drawCard,
	    			    		 discardIndex,  
	    			    		 removeBonus1,  
	    			    		 changeHabitat,  
	    			    		 targetHabitatIndex,
	    			    		 tuckCard,  
	    			    		 birdIndexFromHand,  
	    			    		 chooseWheat,  
	    			    		 discardFoodIndex, uiChoices)
	    			   ;*/

// PLS HAVE UICHOICES AS A PARMETER BECAUSE CASE RECURSIVELY CALLS THE METHOD
public class GamePanel extends JPanel implements MouseListener {
    private Game game;
	private JButton[] actionButtons;
	private JButton left;
	private JButton right;
	public CardDraw drawer;
	public BirdFeeder feeder;
	private BufferedImage mat;
	private SpotPicker pick;
	public int playerDisplaying;
	public JLabel error;
	private JButton viewWinner;
	
    public GamePanel(Game game) {
    	this.game = game;
		setLayout(null); // Use absolute positioning
		drawer = new CardDraw(game);
		feeder = new BirdFeeder();
		pick = new SpotPicker();
		playerDisplaying = game.playerTurn;

		try {
			mat = ImageIO.read(getClass().getResourceAsStream("Images/Backgrounds/WingspanPlayerMat.jpg"));
			left = new JButton();
			left.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("Images/LeftArrow.png"))));
			left.setBackground(new Color(0, 0, 0, 0));
			left.setBorderPainted(false);
			left.setFocusable(false);
			left.addActionListener((e) -> {
				if (playerDisplaying == game.playerTurn) removeActions();
				playerDisplaying = (playerDisplaying - 1) % game.getPlayerCount();
				if (playerDisplaying < 0) playerDisplaying = game.getPlayerCount() - 1;
				if (playerDisplaying == game.playerTurn) addActions();
				layoutComponents();
				repaint();
			});

			right = new JButton();
			right.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("Images/RightArrow.png"))));
			right.setBackground(new Color(0, 0, 0, 0));
			right.setBorderPainted(false);
			right.setFocusable(false);
			right.addActionListener((e) -> {
				if (playerDisplaying == game.playerTurn) removeActions();
				playerDisplaying = (playerDisplaying + 1) % game.getPlayerCount();
				if (playerDisplaying == game.playerTurn) addActions();
				layoutComponents();
				repaint();
			});
			add(left);
			add(right);

			viewWinner = new JButton("View Scores");
			viewWinner.setFont(new Font("SansSerif", Font.BOLD, 24));
			viewWinner.setBackground(new Color(255, 87, 87));
			viewWinner.setForeground(Color.WHITE);
		} catch (Exception e) {e.printStackTrace();}

		actionButtons = new JButton[4];
		for (int i = 0; i < 4; i++) {
			JButton action = new JButton();
			action.setFont(new Font("SansSerif", Font.BOLD, 18));
			action.setBackground(new Color(255, 87, 87));
			action.setForeground(Color.WHITE);
			switch(i) {
				case 0 -> {action.setText("Gain Food"); action.addActionListener((e) -> action(0));}
				case 1 -> {action.setText("Lay Eggs"); action.addActionListener((e) -> action(1));}
				case 2 -> {action.setText("Draw Cards"); action.addActionListener((e) -> action(2));}
				default -> {action.setText("Play Card (See Hand)"); action.addActionListener((e) -> playCard());}
			}
			actionButtons[i] = action;
			add(action);
		}

		error = new JLabel();
		error.setFont(new Font("SansSerif", Font.BOLD, 18));
		error.setBackground(new Color(255, 87, 87));
		error.setForeground(Color.WHITE);
		error.setVisible(false);
		add(error);

		addMouseListener(this);
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent evt) {
				layoutComponents();
			}
		});
		layoutComponents();
    }
	
	private void layoutComponents() {
		if (getWidth() == 0 || getHeight() == 0) return; // Panel not sized yet
		
		int matWidth = getWidth() * 4 / 5;
		int startX = matWidth + 20; // 20 pixels from right edge of picture
		int yPos = 20; // 20 pixels from top
		
		// Calculate text height for spacing
		FontMetrics fm = getFontMetrics(new Font("Verdana", Font.BOLD, 35));
		int textHeight = fm.getHeight();
		int lineSpacing = 5;
		
		// Position arrows beneath text (text takes 3 lines)
		int arrowY = yPos + 3 * (textHeight + lineSpacing) + 15;
		int arrowSize = 50; // Approximate arrow button size
		
		if (left != null && right != null) {
			left.setBounds(startX, arrowY, arrowSize, arrowSize);
			right.setBounds(startX + arrowSize + 10, arrowY, arrowSize, arrowSize);
		}
		
		// Position buttons beneath arrows, stacked vertically
		int buttonY = arrowY + arrowSize + 20;
		int buttonWidth = 250;
		int buttonHeight = 40;
		int buttonSpacing = 10;
		
		for (int i = 0; i < actionButtons.length; i++) {
			if (actionButtons[i] != null) {
				actionButtons[i].setBounds(startX, buttonY + i * (buttonHeight + buttonSpacing), buttonWidth, buttonHeight);
			}
		}
		viewWinner.setBounds(startX, buttonY, buttonWidth, buttonHeight);
		
		// Position error label below buttons
		if (error != null) {
			error.setBounds(startX, buttonY + actionButtons.length * (buttonHeight + buttonSpacing), buttonWidth, buttonHeight);
		}
	}

	public void action(int habitat) {
		error.setVisible(false);
		game.getCurrentPlayer().placeAction(habitat);
	}

	public void addActions() {
		for (JButton action : actionButtons) add(action);
		add(error);
		layoutComponents();
	}

	public void removeActions() {
		for (JButton action : actionButtons) remove(action);
		remove(error);
		error.setVisible(false);
		layoutComponents();
	}

	public void showError(String msg) {
		error.setText(msg);
		error.setVisible(true);
	}

    private void playCardShenanigans(CardPicker playing) {
        game.setPanel(playing);
        revalidate();
        playing.setCallback((card, habitat) -> {
            if (habitat == null) {game.setPanel(this); revalidate(); return;}
            int flag = game.getCurrentPlayer().playCard(card, habitat);
            if (flag == 0) {
                playing.sayNo("Not enough eggs or room in habitat");
            } else if (flag == 1) {
                // Only return to the game board if no prompt is currently displayed
                if (!(game.getCurrentPanel() instanceof Prompt)) {
                    game.setPanel(this);
                }
            }
        });
    }
	public void playCard() {
		CardPicker playing = new CardPicker(game, game.getCurrentPlayer());
		playCardShenanigans(playing);
	}
	public void playCard(BirdCard habitatRestriction) {
		CardPicker playing = new CardPicker(game, game.getCurrentPlayer(), habitatRestriction);
		playCardShenanigans(playing);
	}

	public void promptFood(int n) {
		feeder.setCount(n);
		feeder.setCallback(f -> {
			if (f == -2) {
				game.setPanel(this);
				repaint();
				revalidate();
			} else if (f == -1) {
				game.setPanel(this);
				game.getCurrentPlayer().getBoard().moveAction();
				repaint();
				revalidate();
			}
			else {
				game.getCurrentPlayer().addFood(f, 1);
			}
		});
		game.setPanel(feeder);
	}

	public void selectCards(int n) {
		drawer.setCount(n);
		drawer.setCallback((c) -> {
			if (c == null && drawer.getCount() == -1) {
				game.setPanel(this);
			} else if (c == null) {
				game.setPanel(this);
				game.getCurrentPlayer().getBoard().moveAction();
			} else game.getCurrentPlayer().addBird(c);
		});
		game.setPanel(drawer);
	}

	public void selectSpots(int n, int m) {
		pick.run(n, m);
	}

	public void markEnd(WinnerPanel win) {
		for (JButton action : actionButtons) remove(action);
		left.removeActionListener(left.getActionListeners()[0]);
		left.addActionListener((e) -> {
			playerDisplaying = (playerDisplaying - 1) % game.getPlayerCount();
			if (playerDisplaying < 0) playerDisplaying = game.getPlayerCount() - 1;
			layoutComponents();
			repaint();
		});
		right.removeActionListener(right.getActionListeners()[0]);
		right.addActionListener((e) -> {
			playerDisplaying = (playerDisplaying + 1) % game.getPlayerCount();
			layoutComponents();
			repaint();
		});


        viewWinner.addActionListener(e -> {
            game.setPanel(win);
        });
        add(viewWinner);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Spot[][] grid = game.getPlayer(playerDisplaying).getBoard().grid;
		try {
			// Calculate scale factors based on original image size (1950 × 1356)
			final int originalWidth = 1950;
			final int originalHeight = 1356;
			int scaledMatWidth = getWidth() * 4 / 5;
			int scaledMatHeight = getHeight();
			double scaleX = (double) scaledMatWidth / originalWidth;
			double scaleY = (double) scaledMatHeight / originalHeight;
			
			// Base card positions from original image (1950 × 1356)
			// Forest (row 0): starts at (377, 61)
			// Prairie (row 1): starts at (377, 497)
			// Wetlands (row 2): starts at (377, 932)
			// Card spacing: 301 pixels horizontally (678 - 377 = 301)
			// Card dimensions: 285 × 416 pixels
			final int baseCardStartX = 377;
			final int[] baseCardStartY = {61, 497, 932};
			final int cardSpacingX = 301;
			final int cardWidth = 285; // Card width: 285 pixels
			final int cardHeight = 416; // Card height: 416 pixels
			
			for (int i = 0; i < grid.length; i++) {
				int cardY = (int)(baseCardStartY[i] * scaleY);
				for (int j = 0; j < grid[i].length - 1; j++) {
					if (grid[i][j].occupied()) {
						// Calculate scaled position
						int cardX = (int)((baseCardStartX + j * cardSpacingX) * scaleX);
						int scaledCardWidth = (int)(cardWidth * scaleX);
						int scaledCardHeight = (int)(cardHeight * scaleY);
						g.setFont(new Font("Verdana", Font.BOLD, 15)); // Smaller font for card text
						
						g.drawImage(
							ImageIO.read(getClass().getResourceAsStream("Images/Birds/" + grid[i][j].getName() + ".png")),
							cardX, cardY,
							scaledCardWidth, scaledCardHeight,
							null
						);
						g.drawString(grid[i][j].getEggCount() + " eggs", cardX, cardY + scaledCardHeight);
						g.drawString(grid[i][j].getCachedFoodCount() + " food cached", cardX, cardY + g.getFontMetrics().getHeight());
						g.drawString(grid[i][j].getTuckedCount() + " cards tucked", cardX + scaledCardWidth - g.getFontMetrics().stringWidth("X cards tucked"), 
							cardY + scaledCardHeight);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Draw text labels to the right of the board
		int matWidth = getWidth() * 4 / 5;
		int startX = matWidth + 20; // 20 pixels from right edge of picture
		int yPos = 30; // 20 pixels from top
		g.setFont(new Font("Verdana", Font.BOLD, 35)); // Smaller font for info text
		FontMetrics fm = g.getFontMetrics();
		int lineHeight = fm.getHeight() + 5;
		g.drawString("Round: " + (game.round), startX, yPos);
		g.drawString("Turn: " + (game.playerTurn + 1), startX, yPos + lineHeight);
		g.drawString("Cubes: " + game.getCurrentPlayer().getActionCubes(), startX, yPos + 2 * lineHeight);
		g.drawString("Player: " + (playerDisplaying + 1), startX, yPos + 3 * lineHeight);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(mat, 0, 0, getWidth() * 4 / 5, getHeight(), null);
	}

    public void mouseClicked(MouseEvent e) {
		pick.process(DetermineSpot.r(e.getX(), e.getY(), getWidth(), getHeight()));
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

	class SpotPicker {
		JButton cancel;
		JLabel pickLabel;
		int picksLeft;
		int mode;

		public SpotPicker() {
			pickLabel = new JLabel();
			pickLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
			pickLabel.setBackground(new Color(0, 0, 0, 255));
			pickLabel.setForeground(Color.WHITE);

			cancel = new JButton("Cancel");
			cancel.setBackground(new Color(255, 87, 87));
			cancel.setForeground(Color.WHITE);
			cancel.setFont(new Font("SansSerif", Font.BOLD, 22));
			cancel.addActionListener((e) -> {
				picksLeft = 0;
				removeAll();
				add(left);
				add(right);
				for (JButton b : actionButtons) add(b);
				layoutComponents();
				repaint();
			});
		}

		public void refreshLabel() {
			if (mode > 0) pickLabel.setText("Pick Spots: " + picksLeft + " eggs left to place");
			else pickLabel.setText("Pick Spots: " + picksLeft + " eggs left to remove");
		}

		public void run(int n, int m) {
			picksLeft = n;
			mode = m;
			refreshLabel();
			removeAll();
			add(pickLabel);
			if (mode > 0) add(cancel);
			// Center the pickLabel and cancel button when picking spots
			int centerX = getWidth() / 2 - 200;
			int centerY = getHeight() / 2;
			pickLabel.setBounds(centerX, centerY - 30, 400, 30);
			if (mode > 0) cancel.setBounds(centerX, centerY + 10, 150, 40);
			repaint();
		}

		public void process(int[] c) {
			if (picksLeft == 0) return;
			Spot mySpot = game.getCurrentPlayer().getBoard().getSpot(c[1], c[0]);
			if (mySpot == null || !mySpot.occupied()) return;
			boolean flag;
			if (mode > 0) flag = mySpot.addEgg();
			else flag = mySpot.removeEgg();
			if (!flag) return;

			picksLeft--;
			refreshLabel();
			remove(cancel);
			if (picksLeft == 0) {
				removeAll();
				add(left);
				add(right);
				for (JButton b : actionButtons) add(b);
				layoutComponents();
				if (mode != -2) game.getCurrentPlayer().getBoard().moveAction();
				else {
					game.getCurrentPlayer().drawBirdChoice(
						game.getCurrentPlayer().getBoard().gainInColumn(
							game.getCurrentPlayer().getBoard().getActionCube()[1]
						) + 1
					);
				}
			}
			repaint();
		}

	}
}

class DetermineSpot {
	// Original image dimensions
	private final static int originalWidth = 1950;
	private final static int originalHeight = 1356;
	
	// Base card positions from original image (1950 × 1356)
	private final static int baseCardStartX = 377;
	private final static int[] baseCardStartY = {61, 497, 932};
	private final static int cardSpacingX = 301;
	private final static int cardWidth = 285; // Card width: 285 pixels
	private final static int cardHeight = 416; // Card height: 416 pixels

	public static int[] r(int x, int y, int panelWidth, int panelHeight) {
		// Calculate scale factors
		int scaledMatWidth = panelWidth * 4 / 5;
		int scaledMatHeight = panelHeight;
		double scaleX = (double) scaledMatWidth / originalWidth;
		double scaleY = (double) scaledMatHeight / originalHeight;
		
		// Check which column and row the click is in
		int col = -1, row = -1;
		
		// Check columns (5 cards)
		for (int j = 0; j < 5; j++) {
			int cardX = (int)((baseCardStartX + j * cardSpacingX) * scaleX);
			int scaledCardWidth = (int)(cardWidth * scaleX);
			if (x >= cardX && x <= cardX + scaledCardWidth) {
				col = j;
				break;
			}
		}
		
		// Check rows (3 biomes)
		for (int i = 0; i < 3; i++) {
			int cardY = (int)(baseCardStartY[i] * scaleY);
			int scaledCardHeight = (int)(cardHeight * scaleY);
			if (y >= cardY && y <= cardY + scaledCardHeight) {
				row = i;
				break;
			}
		}
		
		if (col == -1 || row == -1) return null;
		return new int[]{col, row};
	}
}
