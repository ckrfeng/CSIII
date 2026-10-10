import java.*;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Lesson3_TextFields extends JFrame
{
	private Container win;
	private JLabel input, output, heading;
	private JTextField inputField,outputField;

	public Lesson3_TextFields()
	{
		super ("Using Text Fields on a Frame");
		win = getContentPane();
		win.setLayout(null);

		/**************************************  L A B E L S 	**************************************/
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

		/**************************************  T E X T   F I E L D S 	**************************************/
		//Input field
			inputField = new JTextField("", 10);
			inputField.setSize(100, 20);
			inputField.setLocation(20, 150);
			win.add(inputField);

		//Output field
			outputField = new JTextField("", 10);
			outputField.setSize(100, 20);
			outputField.setLocation(450, 150);
			win.add(outputField);

		setSize(600,300);
		setVisible(true);
       	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               new Lesson3_TextFields();
            }
        });
    }



}








