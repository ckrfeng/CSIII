package PowerGrid;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class PowerPlantDeck {
    private final Deque<String> drawPile = new ArrayDeque<>();
    private final List<String> market = new ArrayList<>();

    // Plants in use for this build (exclude 1,2,41,43,45,47,48,49 as requested)
    private static final List<String> BASE_CARDS = Arrays.asList(
            "3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",
            "21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
            "42","44","46","50"
    );

    public PowerPlantDeck() {
        initialize();
    }

    public void initialize() {
        drawPile.clear();
        market.clear();

        List<String> cards = new ArrayList<>(BASE_CARDS);
        Collections.shuffle(cards);

        // STEP3 card goes in deck. When drawn, game goes to Step 3 immediately.
        cards.add("STEP3");
        Collections.shuffle(cards);
        drawPile.addAll(cards);

        while (market.size() < 8 && !drawPile.isEmpty()) {
            market.add(drawPile.removeFirst());
        }
    }

    public List<String> getMarket() {
        return Collections.unmodifiableList(market);
    }

    public boolean isStep3Card(String cardId) {
        return "STEP3".equals(cardId);
    }

    /** Removes selected market card and refills one. Returns drawn replacement or null. */
    public String takeFromMarket(String cardId) {
        if (!market.remove(cardId)) return null;
        if (drawPile.isEmpty()) return null;
        String next = drawPile.removeFirst();
        market.add(next);
        return next;
    }
}

