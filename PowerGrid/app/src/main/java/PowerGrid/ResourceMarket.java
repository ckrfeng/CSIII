package PowerGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ResourceMarket {

    public static final int TOTAL_COAL = 24;
    public static final int TOTAL_OIL = 24;
    public static final int TOTAL_GARBAGE = 24;
    public static final int TOTAL_URANIUM = 12;

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
        // Coal: 8 columns priced 1–8 Elektro, max 3 tokens per column (UI row of 8 buttons).
        slots.put(ResourceType.COAL, buildSlots(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 }, 3));
        // Oil: same 8 columns; game starts with stock on prices 3–8 only.
        slots.put(ResourceType.OIL, buildSlots(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 }, 3));
        // Garbage (trash): 8 columns; stock starts on 6–8 only.
        slots.put(ResourceType.GARBAGE, buildSlots(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 }, 3));
        // Uranium: 12 distinct price columns, max 1 token per column.
        slots.put(ResourceType.URANIUM, buildSlots(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16 }, 1));

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

    /** Place initial tokens on market. Call once at game start. */
    public void fillInitialMarket() {
        // Coal: 1(3) … 8(3) — full cheap-to-expensive row.
        for (Slot s : slots.get(ResourceType.COAL)) {
            s.setCount(3);
        }
        // Oil: 1(0) 2(0) 3(3) … 8(3)
        List<Slot> oil = slots.get(ResourceType.OIL);
        for (int i = 0; i < oil.size(); i++) {
            oil.get(i).setCount(i >= 2 ? 3 : 0);
        }
        // Garbage: 1(0)…5(0) 6(3) 7(3) 8(3)
        List<Slot> gar = slots.get(ResourceType.GARBAGE);
        for (int i = 0; i < gar.size(); i++) {
            gar.get(i).setCount(i >= 5 ? 3 : 0);
        }
        // Uranium: 1(0)…12(0) 14(1) 16(1) — indices 10,11 in 12-slot row
        List<Slot> u = slots.get(ResourceType.URANIUM);
        for (int i = 0; i < u.size(); i++) {
            u.get(i).setCount((i == 10 || i == 11) ? 1 : 0);
        }
        supply.put(ResourceType.COAL, TOTAL_COAL - countOnMarket(ResourceType.COAL));
        supply.put(ResourceType.OIL, TOTAL_OIL - countOnMarket(ResourceType.OIL));
        supply.put(ResourceType.GARBAGE, TOTAL_GARBAGE - countOnMarket(ResourceType.GARBAGE));
        supply.put(ResourceType.URANIUM, TOTAL_URANIUM - countOnMarket(ResourceType.URANIUM));
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
