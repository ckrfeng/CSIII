
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    private Game game;
    private Wingspan frame;
    private JLabel infoLabel;
    private JButton nextTurnButton;
    private JButton backButton;

    public GamePanel(Game game) {
        this.game = game;
        this.frame = gameFrame();
        setLayout(null);
        infoLabel = new JLabel(getInfoText());
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        add(infoLabel);
        nextTurnButton = new JButton("Next Turn");
        nextTurnButton.addActionListener(e -> {
            game.nextTurn();
            updateInfo();
        });
        add(nextTurnButton);
        backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> {
            frame.setPanel(new MenuPanel(frame));
        });
        add(backButton);
    }
    private Wingspan gameFrame() {
        // try to obtain Wingspan via reflection from game (Game stores frame privately)
        try {
            java.lang.reflect.Field f = Game.class.getDeclaredField("frame");
            f.setAccessible(true);
            return (Wingspan) f.get(game);
        } catch (Exception ex) {
            return null;
        }
    }
    private String getInfoText() {
        int round = -1;
        int turn = -1;
        int playerCount = -1;
        try {
            java.lang.reflect.Field fr = Game.class.getDeclaredField("round");
            fr.setAccessible(true);
            round = (int) fr.get(game);
            java.lang.reflect.Field pt = Game.class.getDeclaredField("playerTurn");
            pt.setAccessible(true);
            turn = (int) pt.get(game);
            java.lang.reflect.Field pc = Game.class.getDeclaredField("players");
            pc.setAccessible(true);
            Object[] players = (Object[]) pc.get(game);
            playerCount = players.length;
        } catch (Exception ex) {
            // ignore
        }
        return "<html>Round: " + round + "<br/>Active Player: " + (turn+1) + " / " + playerCount + "</html>";
    }
    private void updateInfo() {
        infoLabel.setText(getInfoText());
        repaint();
    }
    public void doLayout() {
        int w = getWidth(), h = getHeight();
        infoLabel.setBounds(20,20,300,80);
        nextTurnButton.setBounds(20,120,140,40);
        backButton.setBounds(170,120,140,40);
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(200,230,255));
        g.fillRect(0,0,getWidth(),getHeight());
    }
}
