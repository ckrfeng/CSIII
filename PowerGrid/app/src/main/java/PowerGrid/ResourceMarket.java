package PowerGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Resource market: priced slots per resource type and a supply.
 * Players buy from slots (pay slot price); used tokens return to supply; refill places supply onto slots (highest price first).
 */
public class ResourceMarket {

    public static final int TOTAL_COAL = 24;
    public static final int TOTAL_OIL = 24;
    public static final int TOTAL_GARBAGE = 24;
    public static final int TOTAL_URANIUM = 12;

    /** A single market slot: price per token and current count. */
    public static class Slot {
        private final int price;
        private int count;
        private final int maxCount;

        public Slot(int price, int maxCount) {
            this.price = price;
            this.maxCount = maxCount;
        }

        public int getPrice() { return price; }
        public int getCount() { return count; }
        public int getMaxCount() { return maxCount; }
        public boolean hasToken() { return count > 0; }
        public boolean canAdd() { return count < maxCount; }

        void setCount(int count) { this.count = Math.max(0, Math.min(count, maxCount)); }
        void addOne() { if (canAdd()) count++; }
        void removeOne() { if (hasToken()) count--; }
    }

    private final Map<ResourceType, List<Slot>> slots = new EnumMap<>(ResourceType.class);
    private final Map<ResourceType, Integer> supply = new EnumMap<>(ResourceType.class);

    public ResourceMarket() {
        // Coal: spaces 1–8 (prices 1,1,1,2,2,3,3,4), max 3 per slot
        slots.put(ResourceType.COAL, buildSlots(new int[] { 1, 1, 1, 2, 2, 3, 3, 4 }, 3));
        // Oil: spaces 3–8 (prices 3,3,4,5,5,6)
        slots.put(ResourceType.OIL, buildSlots(new int[] { 3, 3, 4, 5, 5, 6 }, 3));
        // Garbage: spaces 6–8 (prices 6,7,8)
        slots.put(ResourceType.GARBAGE, buildSlots(new int[] { 6, 7, 8 }, 3));
        // Uranium: spaces 14–16 (prices 14,14,16), max 1 per slot
        slots.put(ResourceType.URANIUM, buildSlots(new int[] { 14, 14, 16 }, 1));

        // Initial supply = total - what we put on market at start
        supply.put(ResourceType.COAL, TOTAL_COAL - countOnMarket(ResourceType.COAL));
        supply.put(ResourceType.OIL, TOTAL_OIL - countOnMarket(ResourceType.OIL));
        supply.put(ResourceType.GARBAGE, TOTAL_GARBAGE - countOnMarket(ResourceType.GARBAGE));
        supply.put(ResourceType.URANIUM, TOTAL_URANIUM - countOnMarket(ResourceType.URANIUM));
    }

    private List<Slot> buildSlots(int[] prices, int maxPerSlot) {
        List<Slot> list = new ArrayList<>();
        for (int p : prices) {
            list.add(new Slot(p, maxPerSlot));
        }
        return list;
    }

    private int countOnMarket(ResourceType type) {
        int n = 0;
        for (Slot s : slots.get(type)) n += s.getCount();
        return n;
    }

    /** Place initial tokens on market (cheapest slots first). Call once at game start. */
    public void fillInitialMarket() {
        // Requested starting setup:
        // Coal: 24 tokens on prices 1..8 (3 each)
        // Oil: 18 tokens on prices 3..8 (3 each)
        // Garbage: 6 tokens on prices 7..8 (3 each)
        // Uranium: 2 tokens on 14 and 16 (1 each)
        putInitial(ResourceType.COAL, 24);
        putInitial(ResourceType.OIL, 18);
        putInitial(ResourceType.GARBAGE, 6);
        putInitial(ResourceType.URANIUM, 2);
        // Recompute supply
        supply.put(ResourceType.COAL, TOTAL_COAL - countOnMarket(ResourceType.COAL));
        supply.put(ResourceType.OIL, TOTAL_OIL - countOnMarket(ResourceType.OIL));
        supply.put(ResourceType.GARBAGE, TOTAL_GARBAGE - countOnMarket(ResourceType.GARBAGE));
        supply.put(ResourceType.URANIUM, TOTAL_URANIUM - countOnMarket(ResourceType.URANIUM));
    }

    private void putInitial(ResourceType type, int amount) {
        List<Slot> list = slots.get(type);
        int left = amount;
        for (Slot s : list) {
            while (left > 0 && s.canAdd()) {
                s.addOne();
                left--;
            }
        }
    }

    /** Read-only view of slots for a resource type (price and count per slot). */
    public List<Slot> getSlots(ResourceType type) {
        return Collections.unmodifiableList(slots.get(type));
    }

    public int getSupply(ResourceType type) {
        return supply.getOrDefault(type, 0);
    }

    /** Total tokens of this type currently on the market. */
    public int getTotalOnMarket(ResourceType type) {
        return countOnMarket(type);
    }

    /**
     * Buy one token from the given slot. Returns price paid, or -1 if slot invalid or empty.
     * Caller must add the token to the player's power plant and charge the player.
     */
    public int buy(ResourceType type, int slotIndex) {
        List<Slot> list = slots.get(type);
        if (list == null || slotIndex < 0 || slotIndex >= list.size()) return -1;
        Slot slot = list.get(slotIndex);
        if (!slot.hasToken()) return -1;
        int price = slot.getPrice();
        slot.removeOne();
        return price;
    }

    /** Return tokens to supply (e.g. after producing electricity). */
    public void returnToSupply(ResourceType type, int amount) {
        if (amount <= 0) return;
        supply.merge(type, amount, Integer::sum);
    }

    /**
     * Refill market from supply (Phase 5). Places tokens on highest-price empty slots first.
     * Uses ResourceRefill amounts for the given step and player count.
     */
    public void refill(int step, int numPlayers) {
        int[] amounts = ResourceRefill.getRefillAmounts(step, numPlayers);
        refillType(ResourceType.COAL, amounts[0]);
        refillType(ResourceType.OIL, amounts[1]);
        refillType(ResourceType.GARBAGE, amounts[2]);
        refillType(ResourceType.URANIUM, amounts[3]);
    }

    private void refillType(ResourceType type, int amount) {
        if (amount <= 0) return;
        List<Slot> list = slots.get(type);
        int inSupply = supply.getOrDefault(type, 0);
        int toPlace = Math.min(amount, inSupply);
        if (toPlace <= 0) return;

        // Place on highest-price slots first (fill empty/higher price first)
        List<Slot> byPrice = new ArrayList<>(list);
        byPrice.sort((a, b) -> Integer.compare(b.getPrice(), a.getPrice()));

        int placed = 0;
        for (Slot s : byPrice) {
            while (placed < toPlace && s.canAdd()) {
                s.addOne();
                placed++;
            }
        }
        supply.put(type, inSupply - placed);
    }
}
