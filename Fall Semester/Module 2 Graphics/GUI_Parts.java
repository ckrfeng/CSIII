import java.io.*;
import java.util.*;
import static java.lang.System.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class GUI_Parts extends JFrame implements ItemListener, ActionListener
{
	//An invisible button can be used to clear radio button at the end of an event. 
	//My "invisible" button is really off the screen. The program sets the invisible button to true so the 
	//other buttons go to false automatically.
    private Container win;
    private JLabel mainlabel, radiolabel, buttonlabel, checklabel, inputlabel;
	private JRadioButton button1, button2, button3, invisibleButton;
	private ButtonGroup allButtons;
	private JTextArea output;
	private Scanner keyboard = new Scanner(System.in);
	private static ArrayList <String> list = new ArrayList <String>();
	private JScrollPane scrollPane;
	private JButton jbutton1, jbutton2;
	private JCheckBox check1, check2, check3;
	private boolean rb1, rb2, rb3, ck1, ck2, ck3;
	private JTextField inputField;

	//Constructor
	public GUI_Parts()
	{
			win = getContentPane();
			win.setLayout(null);
		   rb1 = rb2 = rb3 = false;
		   ck1=ck2=ck3=false;

			/**************  Labels  *******************/
			mainlabel = new JLabel("Recognition of GUI Components");
			mainlabel.setSize(225, 20);
			mainlabel.setLocation(375,20);
			win.add(mainlabel);

			radiolabel = new JLabel("Radio Buttons");
			radiolabel.setSize(225, 20);
			radiolabel.setLocation(10,20);
			win.add(radiolabel);

			buttonlabel = new JLabel("JButtons");
			buttonlabel.setSize(225, 20);
			buttonlabel.setLocation(10,130);
			win.add(buttonlabel);

			checklabel = new JLabel("Check Boxes");
			checklabel.setSize(225, 20);
			checklabel.setLocation(10,300);
			win.add(checklabel);

			inputlabel = new JLabel("Input text: ");
			inputlabel.setSize(225, 20);
			inputlabel.setLocation(10,400);
			win.add(inputlabel);

			/**************  Radio Button Group  *******************/
			//ItemListener is used when a state may or may not change but the data needs to be read.
			//For this reason, ItemListener is used for checkboxes and radiobuttons.
			allButtons = new ButtonGroup();

			button1 = new JRadioButton("Button 1");
			button1.setSize(150,30);
			button1.setLocation(10, 40);
			win.add( button1);
			button1.addItemListener(this);
			allButtons.add(button1);

			button2 = new JRadioButton("Button 2");
			button2.setSize(150,30);
			button2.setLocation(10, 60);
			win.add( button2);
			button2.addItemListener(this);
			allButtons.add(button2);

			button3 = new JRadioButton("Button 3");
			button3.setSize(150,30);
			button3.setLocation(10, 80);
			win.add( button3);
			button3.addItemListener(this);
			allButtons.add(button3);

			invisibleButton = new JRadioButton("");
			invisibleButton.setSize(150,30);
			invisibleButton.setLocation(1000, 80);
			win.add( invisibleButton);
			allButtons.add(invisibleButton);

//			JButtons
			jbutton1 = new JButton("Status");
			jbutton1.setSize(150,30);
			jbutton1.setLocation(10,150);
			jbutton1.addActionListener(this);
			win.add(jbutton1);

			jbutton2 = new JButton("Clear");
			jbutton2.setSize(100,30);
			jbutton2.setLocation(10,200);
			jbutton2.addActionListener(this);
			win.add(jbutton2);

//			Checkboxes
			check1 = new JCheckBox("Check 1");
			check1.setSize(100,20);
			check1.setLocation(10, 330);
			win.add(check1);
			check1.addItemListener (this);

			check2 = new JCheckBox("Check 2");
			check2.setSize(100,20);
			check2.setLocation(10, 350);
			win.add(check2);
			check2.addItemListener (this);

			check3 = new JCheckBox("Check 3");
			check3.setSize(100,20);
			check3.setLocation(10, 370);
			win.add(check3);
			check3.addItemListener (this);


//Text Fields (can be used for input or output)
			inputField = new JTextField("", 10);
			inputField.setSize(100, 20);
			inputField.setLocation (10, 430);
			win.add(inputField);

			/**************  Text Area for Output  and Scroll Pane  *******************/
		    output = new JTextArea("");

		    //Usually you will begin with an empty text area. The following command is for learning only.
		    output.append("This is the text area. You can display data here.");
		    scrollPane = new JScrollPane(output, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setSize(500,300);
			scrollPane.setLocation(200, 40);
		    win.add(scrollPane);


       		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(800,800);
			setVisible(true);
	}


		public void actionPerformed (ActionEvent e)
		{
		String s = inputField.getText();
		//check buttons
			if (e.getSource() == jbutton1)
		{
			output.append("\n\nChecking Screen Status\n");

			output.append("Text input is:  " + s + "\n");
		}

			if (e.getSource() == jbutton2)
		{
			output.setText("");
		}


		//check radio buttons
		if (rb1)
		{
			output.append("Button 1 was pressed\n");
		}

		if (rb2)
		{
			output.append("Button 2 was pressed\n");
		}

		if (rb3)
		{
			output.append("Button 3 was pressed\n");
		}

		if (!rb1 && !rb2 && !rb3)
			output.append("No radio buttons are selected\n");

	  //Check check boxes
	  if (ck1)
	  	output.append("Checkbox 1 is checked\n");
	  else output.append("Checkbox 1 is off\n");
	  if (ck2)
	  	output.append("Checkbox 2 is checked\n");
	  else output.append("Checkbox 2 is off\n");
	  if (ck3)
	  	output.append("Checkbox 3 is checked\n");
	  else output.append("Checkbox 3 is off\n");
	  
	  //Clear radio buttons
	  invisibleButton.setSelected(true);
	}

	public void  itemStateChanged (ItemEvent e)
	{
		//If isSelected is not checked for the buttons, the items recognize a state change and repeat the
		//output for deselected and selected.
		if (button1.isSelected() )
 				rb1 = true;
 		else rb1 = false;

		if (button2.isSelected() &&e.getSource() == button2)
 				rb2 = true;
 		else rb2 = false;

		if (button3.isSelected() &&e.getSource() == button3)
 				rb3 = true;
 		else rb3 = false;


		if (check1.isSelected())
			ck1 = true;
		else ck1 = false;
		if (check2.isSelected())
			ck2 = true;
		else ck2 = false;
		if (check3.isSelected())
			ck3 = true;
		else ck3 = false;
	}







	public static void main(String[]arg) throws IOException
	{

		GUI_Parts g = new GUI_Parts();
	}
}