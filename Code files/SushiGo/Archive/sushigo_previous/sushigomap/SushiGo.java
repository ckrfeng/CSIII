import javax.swing.*;
import javax.swing.JFrame;

public class SushiGo extends JFrame{
    public SushiGo(String title) {
    	super(title);
    	setSize(1000, 700);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	add(new Panel(new Game()));
    	setVisible(true);
    	
    }
}
/*
public static void main(String[] args) {
	JFrame frame = new JFrame("Sushi Go");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(1000, 700);
	
	Game game = new Game(); // 4 players
	Panel panel = new Panel(game);
	
	frame.add(panel);
	frame.setVisible(true);
}
*/