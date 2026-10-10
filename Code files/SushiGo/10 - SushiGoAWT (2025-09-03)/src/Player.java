import java.util.*;
public class Player {
    public final int id;
    public List<Card> hand = new ArrayList<>();
    public List<Card> tableau = new ArrayList<>();
    public int score = 0;
    public int pudding = 0;
    public boolean hasPendingWasabi = false;
    public Player(int id){ this.id = id; }
    public void newRoundReset() {
        tableau.clear();
        hasPendingWasabi = false;
    }
}
