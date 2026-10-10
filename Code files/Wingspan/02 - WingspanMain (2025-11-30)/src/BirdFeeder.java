
import java.util.*;

public class BirdFeeder {
    public ArrayList<Integer> dice;

    public BirdFeeder() {
    	dice = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dice.add((int)(Math.random() * 6)+1);
        }
    }

    public void roll() {
        dice.clear();
        for (int i = 0; i < 5; i++) {
            dice.add((int)(Math.random() * 6)+1);
        }
    }
    
    public int getDie(int i) {
    		return dice.get(i);
    }

    public void removeDie(int ind) {
    	dice.remove(ind);
    }
    	
    public boolean allSame() {
        if (dice.isEmpty()) return true;
        int d = dice.get(0);
        for (int i = 0; i < dice.size(); i++) {
            if (dice.get(i) != d) return false;
        }
        return true;
    }
}
