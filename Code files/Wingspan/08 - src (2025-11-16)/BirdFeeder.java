
import java.util.*;

public class BirdFeeder {
    public ArrayList<Integer> dice;

    public BirdFeeder() {
        for (int i = 0; i < 6; i++) {
            dice.add((int)(Math.random() * 6));
        }
    }

    public void roll() {
        dice.clear();
        for (int i = 0; i < 6; i++) {
            dice.add((int)(Math.random() * 6));
        }
    }

    public boolean allSame() {
        if (dice.isEmpty()) return true;
        int d = dice.getFirst();
        for (int i = 0; i < dice.size(); i++) {
            if (dice.get(i) != d) return false;
        }
        return true;
    }
}