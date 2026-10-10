import java.util.ArrayList;
import java.util.Random;

public class BirdFeeder {
    public ArrayList<Integer> dice;

    public BirdFeeder() {
        dice = new ArrayList<>();
        roll();
    }

    public void roll() {
        dice.clear();
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            // dice values 1..6 inclusive
            dice.add(r.nextInt(6) + 1);
        }
    }

    public boolean allSame() {
        if (dice.isEmpty()) return true;
        int d = dice.get(0);
        for (int i = 1; i < dice.size(); i++) {
            if (dice.get(i) != d) return false;
        }
        return true;
    }
}


/*
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
*/