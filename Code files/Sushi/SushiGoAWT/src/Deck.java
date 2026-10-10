import java.util.*;
public class Deck {
    public static List<Card> fullDeck() {
        List<Card> d = new ArrayList<>();
        add(d, CardType.TEMPURA, 14);
        add(d, CardType.SASHIMI, 14);
        add(d, CardType.DUMPLING, 14);
        add(d, CardType.MAKI_2, 12);
        add(d, CardType.MAKI_3, 8);
        add(d, CardType.MAKI_1, 6);
        add(d, CardType.NIGIRI_SALMON, 10);
        add(d, CardType.NIGIRI_SQUID, 5);
        add(d, CardType.NIGIRI_EGG, 5);
        add(d, CardType.PUDDING, 10);
        add(d, CardType.WASABI, 6);
        add(d, CardType.CHOPSTICKS, 4);
        Collections.shuffle(d, new Random());
        return d;
    }
    private static void add(List<Card> d, CardType t, int n) {
        for (int i=0;i<n;i++) d.add(new Card(t));
    }
}
