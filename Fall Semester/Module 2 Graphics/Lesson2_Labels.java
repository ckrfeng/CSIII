import java.*;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Lesson2_Labels extends JFrame
{
	private Container win;
	private JLabel input, output, heading;

	public Lesson2_Labels()
	{
		super ("Using Labels on a Frame");
		win = getContentPane();
		win.setLayout(null);

		//Set up your labels in your constructor
		input = new JLabel("Input");
		input.setSize(50, 20);
		input.setLocation(20,200);
		win.add(input);

		output = new JLabel("Output");
		output.setSize(50, 20);
		output.setLocation(450,200);
		win.add(output);

		heading = new JLabel("Binary and Hexadecimal Conversion");
		heading.setSize(225, 20);
		heading.setLocation(200,20);
		win.add(heading);

		setSize(600,300);
		setVisible(true);
       	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               new Lesson2_Labels();
            }
        });
    }



}








