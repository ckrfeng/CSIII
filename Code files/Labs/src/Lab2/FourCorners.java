package Lab2;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
public class FourCorners extends JPanel implements MouseListener{
	BufferedImage img1, img2, img3, img4, img5, img6, img7, img8;
	BufferedImage choice1, choice2, choice3, choice4;
	public FourCorners() {
		try {
			img1 = ImageIO.read(FourCorners.class.getResource("/Image/chopsticks.jpg"));
			img2 = ImageIO.read(FourCorners.class.getResource("/Image/dumplings.jpg"));
			img3 = ImageIO.read(FourCorners.class.getResource("/Image/eggnigiri.jpg"));
			img4 = ImageIO.read(FourCorners.class.getResource("/Image/makiroll1.jpg"));
			img5 = ImageIO.read(FourCorners.class.getResource("/Image/makiroll2.jpg"));
			img6 = ImageIO.read(FourCorners.class.getResource("/Image/makiroll3.jpg")); 
			img7 = ImageIO.read(FourCorners.class.getResource("/Image/pudding.jpg")); 
			img8 = ImageIO.read(FourCorners.class.getResource("/Image/salmonnigiri.jpg"));
		}
		catch(Exception E) {
			System.out.println("Exception Error");
			return;
		}
		choice1 = img1;
		choice2 = img2;
		choice3 = img3;
		choice4 = img4;
		addMouseListener(this);
	}
	public void paint(Graphics g) {
	super.paint(g);
		g.drawImage(choice1, 0, 0, getWidth()/2, getHeight()/2, null);
		g.drawImage(choice2, getWidth()/2, 0, getWidth()/2, getHeight()/2, null);
		g.drawImage(choice3, 0, getHeight()/2, getWidth()/2, getHeight()/2, null);
		g.drawImage(choice4, getWidth()/2, getHeight()/2, getWidth()/2, getHeight()/2, null);
	}
	public void mousePressed (MouseEvent e) {
	}
	public void mouseReleased (MouseEvent e) {
	}
	public void mouseClicked (MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(x<getWidth()/2 && y<getHeight()/2) {
			if(choice1 == img1) {
				choice1 = img5;
			}
			else {
				choice1 = img1;
			}
		}
		if (x>getWidth()/2 && y<getHeight()/2) {
			if(choice2 == img2) {
				choice2 = img6;
			}
			else {
				choice2 = img2;
			}
		}
		if(x<getWidth()/2 && y>getHeight()/2) {
			if(choice3 == img3) {
				choice3 = img7;
			}
			else {
				choice3 = img3;
			}
		}
		if(x>getWidth()/2 && y>getHeight()/2) {
			if(choice4 == img4) {
				choice4 = img8;
			}
			else {
				choice4 = img4;
			}
		}
		repaint();
	}
	public void mouseEntered (MouseEvent e) {
	}
	public void mouseExited (MouseEvent e) {
	}
	public void addNotify() {
		super.addNotify();
		requestFocus();
	}
}