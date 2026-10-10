import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*; 
/*

Columns: 350, 570, 795, 1015, 1230
Rows: 90, 380, 680

*/

// i followed the outline but the location still looked dog for me 
public class GamePanel extends JPanel implements MouseListener {
    private Game game;
    private BufferedImage playerMat;
    BirdFeeder feed;
    Color transparentColor = new Color(255, 255, 255, 0);
	private JButton[] foodTokenButton = new JButton[6];
	private JButton[] playerChooseToken = new JButton[5];
	Player[] players; 
	Player current; 
	private int playerDie; 
	
	
    public GamePanel(Game game) {
    	this.game = game;
    		 feed = new BirdFeeder(); 
    		System.out.println(feed.dice);
    		players = new Player[game.getPlayerCount()];
    		for(int i = 0 ; i < players.length; i++) {
    			players[i] = new Player(game);
    		}
    		current = players[0];
    		setLayout(null);
        
        try {
            playerMat = ImageIO.read(GamePanel.class.getResource("Images/Backgrounds/WingspanPlayerMat.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        int boo = 0;  
        for(int i = 0; i < feed.dice.size(); i++ ) {
        	 int index = i;
        	 playerChooseToken[i] = makeMeAButton("dice " + (i+1)); 
        	 playerChooseToken[i].setBounds(393*(i+1) - boo, 20, 34, 34);
        		playerChooseToken[i].addActionListener(e -> buttonChosen(index));
        		playerChooseToken[i].setVisible(false);
        		add(playerChooseToken[i]);
        		boo += 245; 
        }
        
        int coo = 0; 
        for(int i = 0; i < 6; i++ ) {
       	 int index = i;
       		foodTokenButton[i] = makeMeAButton("Spot " + (i+1)); 
       		foodTokenButton[i].setBounds(393*(i+1) - coo, 97, 34, 34);
       		add(foodTokenButton[i]);
       		foodTokenButton[i].addActionListener(e -> foodActions(index));
       		coo += 245; 
       }
        
        
        addMouseListener(this);	
    }
    
    public void foodActions(int num) { 
    	if(num == 0) {
    		diceThere(); 
    	}	else {
    		System.out.println("mb didnt do the other spots");    	
    	}
    	
    }
    
    public void buttonChosen(int num) {
    		System.out.println(num);
    		playerDie = feed.getDie(num);
    		feed.removeDie(num);
    		System.out.println(feed.dice.size());
    		
    		System.out.println("selected food "+ playerDie);
    		refreshDiceButtons(); 
    		//current.addFood(playerDie);
    		//System.out.println(current.getFoodTokens());
    		//addFodd token causes error when clicking a certain dice button, idk why i spent to long figuring that out
    		// which made me not work on the other spots mb
    		
    }

    public void diceThere() {
    	for(int i = 0 ; i < feed.dice.size(); i++) {
    		playerChooseToken[i].setVisible(true);
    	}
    	
    }
    
    public void refreshDiceButtons() {
    	for(JButton b: playerChooseToken) {
			b.setVisible(false);
		}
    	        
        revalidate(); 
        repaint(); 
    }
    
  
    

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(103, 190, 217));
        g.fillRect(0, 0, getWidth() * 4 / 5, getHeight());
        g.drawImage(playerMat, 0, 0, getWidth() * 4 / 5, getHeight(), null);
    }

	public JButton makeMeAButton(String text) {
		JButton button = new JButton(text);
		return button;
	}
    
    	
    public void mouseClicked(MouseEvent e) {
        System.out.printf("x: %d, y: %d\n", e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
