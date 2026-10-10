import java.*;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Lesson8_RadioButtons extends JFrame implements ItemListener, ActionListener
{
	private Container win;

	// Examples of label, textfield and button
	private JLabel screenLabel, inputLabel, outputLabel, inputBaseLabel;
	private JTextField inputField;
	private JScrollPane scrollPane;
	private JTextArea outputArea;
	private JCheckBox binCheck, hexCheck, decCheck;
	private JButton printButton;
	private JRadioButton dec_irbutton, bin_irbutton, hex_irbutton;	
	private ButtonGroup  input_radioGroup;		

	private String binIn, hexIn;
	private int decimal, baseIn;

	private boolean wantHex, wantBin, wantDec;
	private boolean hexInput, binInput, decInput;
	private int number;

	public Lesson8_RadioButtons()
	{
		super ("Using a TextArea");
		win = getContentPane();
		win.setLayout(null);

		wantBin = false;
		wantHex = false;
		wantDec = false;
		hexInput=false;
		binInput=false;
		decInput=false;

		/**************   Label  *******************/
			screenLabel = new JLabel("Binary / Hex Calculator");
			screenLabel.setSize(150,20);
			screenLabel.setLocation(20, 20);
			win.add(screenLabel);

			inputLabel = new JLabel("Input:");
			inputLabel.setSize(100,20);
			inputLabel.setLocation(20, 40);
			win.add(inputLabel);
			
			inputBaseLabel = new JLabel("Specify the base for the input:");
			inputBaseLabel.setSize(200,20);
			inputBaseLabel.setLocation(20, 100);
			win.add(inputBaseLabel);			

			outputLabel = new JLabel("Check for desired bases of output:");
			outputLabel.setSize(250,20);
			outputLabel.setLocation(250, 40);
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
			binCheck.setLocation(250, 60);
			win.add(binCheck);
			binCheck.addItemListener (this);

			hexCheck = new JCheckBox("Hexadecimal");
			hexCheck.setSize(100,20);
			hexCheck.setLocation(250, 80);
			win.add(hexCheck);
			hexCheck.addItemListener (this);

			decCheck = new JCheckBox("Decimal");
			decCheck.setSize(100,20);
			decCheck.setLocation(250, 100);
			win.add(decCheck);
			decCheck.addItemListener (this);

		//TextArea
		    outputArea = new JTextArea("");
			outputArea.setText("");   
		    scrollPane = new JScrollPane(outputArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);			    
			scrollPane.setSize(300,250);
			scrollPane.setLocation(20,270);
		    win.add(scrollPane);

	//Radio Buttons
	//input is binary / hex / or decimal
			input_radioGroup = new ButtonGroup();	

			dec_irbutton = new JRadioButton("Decimal");		//Decimal Input Radio Button
			dec_irbutton.setSize(150,30);
			dec_irbutton.setLocation(20, 120);
			win.add( dec_irbutton);
			dec_irbutton.addItemListener(this);
			input_radioGroup.add(dec_irbutton);	


			bin_irbutton = new JRadioButton("Binary");		//Decimal Input Radio Button
			bin_irbutton.setSize(150,30);
			bin_irbutton.setLocation(20, 140);
			win.add( bin_irbutton);
			bin_irbutton.addItemListener(this);
			input_radioGroup.add(bin_irbutton);	
				

			hex_irbutton = new JRadioButton("Hexadecimal");		//Decimal Input Radio Button
			hex_irbutton.setSize(150,30);
			hex_irbutton.setLocation(20, 160);
			win.add( hex_irbutton);
			hex_irbutton.addItemListener(this);
			input_radioGroup.add(hex_irbutton);					

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
               new Lesson8_RadioButtons();
            }
        });
    }


		public void itemStateChanged(ItemEvent e)
		{
			/******************  INPUT  SPECIFIED BY RADIO BUTTONS  ******************/ 
			if (dec_irbutton.isSelected())
				decInput=true;
			else decInput = false;
			
			if (hex_irbutton.isSelected())
				hexInput = true;
			else hexInput = false;
				
			if (bin_irbutton.isSelected())
				binInput=true;
			else binInput = false;
			
			/***********************************************************************************************************
			 *
			 *IF IMMEDIATE ACTION IS DONE AS A RESULT OF A RADIO BUTTON, YOU CAN DO:
			 *if (e.getSource() == hex_irbutton)....
			 *
			 *In this example, the input may or may not change with different calculations so a boolean 
			 *value is used. It could still be selected from the previous execution, but the listener only
			 *picks up changes.
			 *
			 *In Yahtzee, if you want to click a radio button and the score is immediate entered, you would
			 *want to use if (e.getSource().....). If you prefer another button for Enter Score for line (or 
			 *whatever), then you could use boolean variables like the dec, hex and binary input
			 *radio buttons.
			 *********************************************************************************************************/
	
				/******************  CHECKBOXES SPECIFY TYPE OF OUTPUT ******************/ 

			if (binCheck.isSelected())
				wantBin = true;
			else wantBin = false;

			if (hexCheck.isSelected())
				wantHex = true;
			else wantHex = false;
			
			if (decCheck.isSelected())
				wantDec = true;
			else wantDec= false;			
		}

		public void actionPerformed (ActionEvent e)
		{
			if (decInput)
			{
						decimal = Integer.parseInt(inputField.getText());
						baseIn = 10;
			}
			
			if (hexInput)
			{
				hexIn = inputField.getText().toUpperCase();	
				decimal = 0;
				int base = 1;
				for (int i = hexIn.length()-1; i>=0; i--)
				{
					int pos = 0;
					char letter = hexIn.charAt(i);
					if (Character.isDigit(letter))
						pos = letter - 48;
					else pos = letter - 55;
					decimal+= pos*base;
					base*=16;
				}
				baseIn = 16;
			}
		
			
			if (binInput)
			{
				binIn = inputField.getText();
				decimal = 0;
				int base = 1;
				for (int i = binIn.length()-1; i>=0; i--)
				{
					char letter = binIn.charAt(i);
					if (letter == '1')
						decimal += base;
					base*=2;
				}
				baseIn = 2;
			}				
			/***********  OUTPUT   **********/
			outputArea.append("\n");
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
      			switch(baseIn)
      			{
      				case 2:		 outputArea.append("\nThe binary number for " + binIn + " base 2 is " + result);  break;
      				case 10:	outputArea.append("\nThe binary number for " + decimal + " base 10  is " + result);  break;
      				case 16:	outputArea.append("\nThe binary number for " + hexIn + " base 16  is " + result);  break;
      			
      			}
				}
				
				if (wantDec)
				{
					switch (baseIn)
      				{
      				case 2:		 outputArea.append("\nThe decimal number for " + binIn + " base 2 is " + decimal);  break;
      				case 10:	outputArea.append("\nThe decimal number for " + decimal + " base 10  is " + decimal);  break;
      				case 16:	outputArea.append("\nThe decimal number for " + hexIn + " base 16  is " + decimal);  break;
      			
      				}						
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

      			switch(baseIn)
      			{
      				case 2:		 outputArea.append("\nThe hexadecimal number for " + binIn + " base 2 is " + result);  break;
      				case 10:	outputArea.append("\nThe hexadecimal number for " + decimal + " base 10  is " + result);  break;
      				case 16:	outputArea.append("\nThe hexadecimal number for " + hexIn + " base 16  is " + result);  break;
      			
      			}
				}

		}
    	if (!wantBin && !wantHex && !wantDec)
    		outputArea.append("\nNo output specified. Choose Binary, Decimal or Hex\n");
	}

}















