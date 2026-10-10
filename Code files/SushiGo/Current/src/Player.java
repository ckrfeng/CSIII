import java.util.ArrayList;
import java.util.List;
public class Player {
    public final int id;
    public List<Card> hand = new ArrayList<>();
    public List<Card> table = new ArrayList<>();
    public int score = 0;
    public int pudding = 0;
    public boolean wasabi = false;
    public int makiCount;
    
    public Player(int id){ 
        this.id = id; 
    }
    
    public void newRoundReset() {
        table.clear();
        wasabi = false;
    }
    
    public int scoreTable() {
        int total = 0;
        int tempuraCount = 0;
        int sashimiCount = 0;
        int dumplingCount = 0;
        int availableWasabi = 0;

        for (Card c : table) {
            switch (c.type) {
                case "tempura":
                    tempuraCount++;
                    break;
                case "sashimi":
                    sashimiCount++;
                    break;
                case "dumpling":
                    dumplingCount++;
                    break;
                case "wasabi":
                    availableWasabi++;
                    break;
                case "eggnigiri":
                    if (availableWasabi > 0) {
                        total += 3;
                        availableWasabi--;
                    } else {
                        total += 1;
                    }
                    break;
                case "salmonnigiri":
                    if (availableWasabi > 0) {
                        total += 6;
                        availableWasabi--;
                    } else {
                        total += 2;
                    }
                    break;
                case "squidnigiri":
                    if (availableWasabi > 0) {
                        total += 9;
                        availableWasabi--;
                    } else {
                        total += 3;
                    }
                    break;
                case "maki1":
                    makiCount += 1;
                    break;
                case "maki2":
                    makiCount += 2;
                    break;
                case "maki3":
                    makiCount += 3;
                    break;
            }
        }

        total += (tempuraCount / 2) * 5;
        total += (sashimiCount / 3) * 10;

        if (dumplingCount > 0) {
            int dumplingPoints = 0;
            if (dumplingCount == 1) {
                dumplingPoints = 1;
            }
            else if (dumplingCount == 2) {
                dumplingPoints = 3;
            }
            else if (dumplingCount == 3) {
                dumplingPoints = 6;
            }
            else if (dumplingCount == 4) {
                dumplingPoints = 10;
            }
            else {
                dumplingPoints = 15;
            }
            total += dumplingPoints;
        }

        return total;
    }

}




