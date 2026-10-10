import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class Deck {
    public static List<Card> fullDeck() {
        List<Card> d = new ArrayList<>();
        add(d, "tempura", 14);
        add(d, "sashimi", 14);
        add(d, "dumpling", 14);
        add(d, "maki2", 12);
        add(d, "maki3", 8);
        add(d, "maki1", 6);
        add(d, "salmonnigiri", 10);
        add(d, "squidnigiri", 5);
        add(d, "eggnigiri", 5);
        add(d, "pudding", 10);
        add(d, "wasabi", 6);
        add(d, "chopsticks", 4);
        Collections.shuffle(d, new Random());
        return d;
    }
    private static void add(List<Card> d, String t, int n) {
        for (int i=0;i<n;i++) d.add(new Card(t));
    }
}




