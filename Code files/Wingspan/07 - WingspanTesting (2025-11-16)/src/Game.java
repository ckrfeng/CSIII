
import java.util.*;

public class Game {
    HashSet<BirdCard> birdDeck;
    HashSet<BonusCard> bonusDeck;
    public Wingspan frame;
    public GamePanel panel;
    public int playerPoints[];
    public Player[] players;
    public int playerTurn = 0;
    public int round = 0;

    public Game(Wingspan frame, int playerCount) {
        this.frame = frame;
        players = new Player[playerCount];
        playerPoints = new int[playerCount];
        for (int i = 0; i < playerCount; i++)
            players[i] = new Player(this);
        panel = new GamePanel(this);
        frame.setPanel(panel);
    }

    public BirdCard drawBird() {
        if (birdDeck == null || birdDeck.isEmpty()) return null;
        int randomIndex = (int)(Math.random() * birdDeck.size());
        BirdCard picked = null;
        int i = 0;
        for (BirdCard b : birdDeck) {
            if (i == randomIndex) {
                picked = b;
                break;
            }
            i++;
        }
        if (picked != null) birdDeck.remove(picked);
        return picked;
    }

    public BonusCard drawBonus() {
        if (bonusDeck == null || bonusDeck.isEmpty()) return null;
        int randomIndex = (int)(Math.random() * bonusDeck.size());
        BonusCard picked = null;
        int i = 0;
        for (BonusCard b : bonusDeck) {
            if (i == randomIndex) {
                picked = b;
                break;
            }
            i++;
        }
        if (picked != null) bonusDeck.remove(picked);
        return picked;
    }
    /*
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
    */

    public void endGame() {
        // ALGORITHM TO END AND SCORE THE GAME
    }

    public Player getPlayer(int i) {
        return players[i];
    }

    public void handOutCards() {
        // ALGORITHM TO HAND OUT CARDS; potentially in a new panel
    }

    public void nextTurn() {
        playerTurn = (playerTurn + 1) % players.length;
    }

    public void nextRound() {
        round++;
    }
}