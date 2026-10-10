import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class Deck {
    public static List<Card> fullDeck() {
        List<Card> deck = new ArrayList<>();
        add(deck, "tempura", 14);
        add(deck, "sashimi", 14);
        add(deck, "dumpling", 14);
        add(deck, "maki2", 12);
        add(deck, "maki3", 8);
        add(deck, "maki1", 6);
        add(deck, "salmonnigiri", 10);
        add(deck, "squidnigiri", 5);
        add(deck, "eggnigiri", 5);
        add(deck, "pudding", 10);
        add(deck, "wasabi", 6);
        add(deck, "chopsticks", 4);
        Collections.shuffle(deck, new Random());
        return deck;
    }
    private static void add(List<Card> deck, String t, int n) {
        for (int i=0;i<n;i++) {
        	deck.add(new Card(t));
        }
    }
}




