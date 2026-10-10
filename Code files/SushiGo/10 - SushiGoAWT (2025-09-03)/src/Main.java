import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        Frame frame = new Frame("Sushi Go! - AWT Pass & Play");
        frame.setSize(1200, 800);
        frame.setLayout(null);
        GameCanvas canvas = new GameCanvas();
        canvas.setBounds(0,0,1200,800);
        frame.add(canvas);
        frame.addWindowListener(new WindowAdapter(){ public void windowClosing(WindowEvent e){ System.exit(0);} });
        frame.setVisible(true);
        canvas.requestFocus();
    }
}
