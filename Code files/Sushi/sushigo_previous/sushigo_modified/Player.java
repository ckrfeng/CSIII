import java.util.ArrayList;
import java.util.List;
public class Player {
    public final int id;
    public List<Card> hand = new ArrayList<>();
    public List<Card> table = new ArrayList<>();
    public List<Card> faceDownPile = new ArrayList<>();
    public int score = 0;
    public int pudding = 0;
    public boolean hasPendingWasabi = false;
    public Player(int id){ this.id = id; }
    public void newRoundReset() {
        faceDownPile.clear();
        table.clear();
        hasPendingWasabi = false;
    }
}


