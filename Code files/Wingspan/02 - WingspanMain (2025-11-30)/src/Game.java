
import java.util.*;
import javax.swing.*;

public class Game {
    HashSet<BirdCard> birdDeck;
    HashSet<BonusCard> bonusDeck;
    private Wingspan frame;
    private GamePanel panel;
    private int playerPoints[];
    private Player[] players;
    private int playerTurn = 0;
    private int round = 1;

    public Game(Wingspan frame, int playerCount) {
        this.frame = frame;
        players = new Player[playerCount];
        playerPoints = new int[playerCount];
        for (int i = 0; i < playerCount; i++)
            players[i] = new Player(this);
        
        birdDeck = new HashSet<>();
        birdDeck.addAll(Arrays.asList(BirdCard.values()));
        bonusDeck = new HashSet<>();
        bonusDeck.addAll(Arrays.asList(BonusCard.values()));

        handOutCards();
    }
    
    public int getPlayerCount() {
    		return players.length;
    }

    public BirdCard drawBird() {
        int randomCard = (int)(Math.random() * birdDeck.size());
        Iterator<BirdCard> birdIter = birdDeck.iterator();
        BirdCard drawn = null;
        while (randomCard >= 0) {
            randomCard--;
            drawn = birdIter.next();
        }
        return drawn;
    }

    public BonusCard drawBonus() {
        int randomCard = (int)(Math.random() * bonusDeck.size());
        Iterator<BonusCard> birdIter = bonusDeck.iterator();
        BonusCard drawn = null;
        while (randomCard >= 0) {
            randomCard--;
            drawn = birdIter.next();
        }
        return drawn;
    }

    public void end() {
        // ALGORITHM TO END AND SCORE THE GAME
        // need the number of birds in each habitat, bonus cards, end of round goals, eggs, cached food, tucked birds
    }

    public Player getPlayer(int i) {
        return players[i];
    }

    public void handOutCards() {
        SetupPanel setup = new SetupPanel(this, players.length);
        frame.setPanel(setup);
    }

    public void nextTurn() {
        // Run all abilities in between turns
        playerTurn = (playerTurn + 1) % players.length;
        if (players[playerTurn].getActionCubes() == 0) {
            nextRound();
        }
    }

    public void nextRound() {
        if (round == 4) {
            end();
            return;
        }
        round++;
        for (Player p : players) {
            p.setActionCubes(9 - round);
        }
    }

    public void setPanel(JPanel p) {
        frame.setPanel(p);
    }

    public void start() {
        panel = new GamePanel(this);
        frame.setPanel(panel);
    }
}
