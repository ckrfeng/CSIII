package Lab1;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class SushiGoPicture extends JPanel{
private BufferedImage image;
	public SushiGoPicture() {
		try {
	        image = ImageIO.read(SushiGoPicture.class.getResource("/Image/dumplings.jpg"));    
		}
		catch(Exception E) {
			System.out.println("Exception error");
			return;
		}
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		int x = getWidth()/2-40;
		int y = getHeight()/2-75;
		int w = 80;
		int h = 150;

		g2d.drawImage(image, x, y-150, w, h, null);
		g2d.rotate(Math.toRadians (90), x + w/2.0, y + h/2.0);
		g2d.drawImage(image, x, y-150, w, h, null);
		g2d.rotate(Math.toRadians (90), x + w/2.0, y + h/2.0);
		g2d.drawImage(image, x, y-150, w, h, null);
		g2d.rotate(Math.toRadians (90), x + w/2.0, y + h/2.0);
		g2d.drawImage(image, x, y-150, w, h, null);
	}
}