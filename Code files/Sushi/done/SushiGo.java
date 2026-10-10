import javax.swing.JFrame;

public class SushiGo extends JFrame{
    public SushiGo(String title) {
    	super(title);
    	setExtendedState(JFrame.MAXIMIZED_BOTH);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	add(new Panel(new Game()));
    	setVisible(true);
    	
    }
}
