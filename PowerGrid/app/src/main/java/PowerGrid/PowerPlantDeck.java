package PowerGrid;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class PowerPlantDeck {
    private final Deque<String> drawPile = new ArrayDeque<>();
    private final List<String> market = new ArrayList<>();

    public PowerPlantDeck() {
        initialize();
    }

    private void initialize() {
        drawPile.clear();
        market.clear();
        List<Integer> plugs = new ArrayList<>();
        List<Integer> sockets = new ArrayList<>();
        for (PowerPlantCard card : PowerPlantCard.values()) {
            if (card.getNumber() <= 15) plugs.add(card.getNumber());
            else sockets.add(card.getNumber());
        }
        Collections.shuffle(plugs);
        Collections.shuffle(sockets);

        // Per rules: first 8 drawn cards shown in market are from plug (3-15) set only.
        for (int i = 0; i < 8 && !plugs.isEmpty(); i++) {
            market.add(String.valueOf(plugs.remove(0)));
        }
        market.sort((a, b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)));

        // One additional plug card is placed on top of supply stack.
        if (!plugs.isEmpty()) {
            drawPile.addLast(String.valueOf(plugs.remove(0)));
        }

        // Remaining supply stack is shuffled sockets + remaining plugs + STEP3 at bottom.
        List<String> rest = new ArrayList<>();
        for (Integer n : plugs) rest.add(String.valueOf(n));
        for (Integer n : sockets) rest.add(String.valueOf(n));
        Collections.shuffle(rest);
        drawPile.addAll(rest);
        drawPile.addLast("STEP3");
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
        market.sort((a, b) -> {
            if ("STEP3".equals(a)) return 1;
            if ("STEP3".equals(b)) return -1;
            return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
        });
        return next;
    }
}

