import java.*;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Lesson6_TextArea extends JFrame implements ItemListener, ActionListener
{
	private Container win;

	// Examples of label, textfield and button
	private JLabel screenLabel, inputLabel, outputLabel;
	private JTextField inputField;
	private JTextArea outputArea;
	private JCheckBox binCheck, hexCheck, decCheck;
	private JButton printButton;

	private String binIn, hexIn;
	private int decimal;

	private boolean wantHex, wantBin;
	private int number;

	public Lesson6_TextArea()
	{
		super ("Using a TextArea");
		win = getContentPane();
		win.setLayout(null);

		wantBin = false;
		wantHex = false;

		/**************   Label  *******************/
			screenLabel = new JLabel("Binary / Hex Calculator");
			screenLabel.setSize(150,20);
			screenLabel.setLocation(20, 20);
			win.add(screenLabel);

			inputLabel = new JLabel("Input:");
			inputLabel.setSize(100,20);
			inputLabel.setLocation(20, 40);
			win.add(inputLabel);

			outputLabel = new JLabel("Check for desired bases of output:");
			outputLabel.setSize(250,20);
			outputLabel.setLocation(250, 110);
			win.add(outputLabel);

		//Input field
			inputField = new JTextField("", 10);
			inputField.setSize(100,20);
			inputField.setLocation(20, 60);
			win.add(inputField);

	//Print Button
			printButton = new JButton("PRINT");
			printButton.setSize(100, 30);
			printButton.setLocation(250, 220);
			printButton.addActionListener(this);
			win.add(printButton);

		//Checkboxes
			binCheck = new JCheckBox("Binary");
			binCheck.setSize(100,20);
			binCheck.setLocation(250, 140);
			win.add(binCheck);
			binCheck.addItemListener (this);

			hexCheck = new JCheckBox("Hexadecimal");
			hexCheck.setSize(100,20);
			hexCheck.setLocation(250, 180);
			win.add(hexCheck);
			hexCheck.addItemListener (this);


		//TextArea
		    outputArea = new JTextArea("");
			outputArea.setText("");   
			outputArea.setSize(300,250);
			outputArea.setLocation(20,270);
		    win.add(outputArea);

		//Don't forget to initialize YOUR variables
		number = 0;
		//Window size and setVisible to true (if not, you can't see your window)
			setSize(600,600);
			setVisible(true);
       		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               new Lesson6_TextArea();
            }
        });
    }


		public void itemStateChanged(ItemEvent e)
		{
			//used for checkboxes and radiobuttons

			if (binCheck.isSelected())
				wantBin = true;
			else wantBin = false;

			if (hexCheck.isSelected())
				wantHex = true;
			else wantHex = false;
		}

		public void actionPerformed (ActionEvent e)
		{
			int decimal = Integer.parseInt(inputField.getText());
			/***********  OUTPUT   **********/
			if (e.getSource() == printButton)
			{
				if (wantBin)
				{
				int bin1;
				int number = decimal;
    			String result = "";
     			for (int twobase = 128; twobase > 0; twobase /= 2)
     			{
       				bin1 = number / twobase;
       				result +=("" + bin1);
       				if (number >= twobase)
           				number -= twobase;
      			}
      			outputArea.append("\nThe binary number for " + decimal + " is " + result);
				}


				if (wantHex)
				{
				number = decimal;
				int rem = 0;
				String result = "";
    			for (int hexbase =4096; hexbase > 0; hexbase/=16)
     			{
       				int hexval = number / hexbase;
       				rem = number % hexbase;
       				if (hexval >= 10)
       				{
       				switch (hexval)
       				{
       					case 10:	result+=('A'); break;
       					case 11:	result+=('B'); break;
						case 12:	result+=('C'); break;
						case 13:	result+=('D'); break;
						case 14:	result+=('E'); break;
						case 15:	result+=('F'); break;
       				}
       				}
       				else
       					result+=hexval;
       				if ( hexval >= 0)
           				number = rem;


				}

      			outputArea.append("\nThe hexadecimal number for " + decimal + " is " + result + "\n");

				}

		}

	}

}















