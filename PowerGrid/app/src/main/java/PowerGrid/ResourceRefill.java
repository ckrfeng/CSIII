package PowerGrid;

/**
 * Refill amounts for the resource market per round (Phase 5 Bureaucracy).
 * Indexed by step (1–3) and number of players (2–6). Values: { coal, oil, garbage, uranium }.
 */
public final class ResourceRefill {
    // [step 1-3][players 2-6] -> { coal, oil, garbage, uranium }
    // Players 3–5 per project spec; 2 and 6 use prior board-game style amounts.
    private static final int[][][] REFILL = {
        // Step 1
        {
                { 3, 3, 2, 1 }, // 2 players
                { 4, 2, 1, 1 }, // 3
                { 5, 3, 2, 1 }, // 4
                { 5, 4, 3, 2 }, // 5
                { 6, 5, 4, 3 }  // 6
        },
        // Step 2
        {
                { 4, 3, 2, 1 },
                { 5, 3, 2, 1 },
                { 6, 4, 3, 2 },
                { 7, 5, 3, 3 },
                { 6, 5, 4, 3 }
        },
        // Step 3
        {
                { 3, 4, 3, 1 },
                { 3, 4, 3, 1 },
                { 4, 5, 4, 2 },
                { 5, 6, 5, 2 },
                { 7, 6, 5, 4 }
        }
    };

    /** Refill amounts for given step and player count. Returns { coal, oil, garbage, uranium }. */
    public static int[] getRefillAmounts(int step, int numPlayers) {
        if (step < 1 || step > 3 || numPlayers < 2 || numPlayers > 6) {
            return new int[] { 0, 0, 0, 0 };
        }
        int[] row = REFILL[step - 1][numPlayers - 2];
        return new int[] { row[0], row[1], row[2], row[3] };
    }

    public static int getCoalRefill(int step, int numPlayers) {
        return getRefillAmounts(step, numPlayers)[0];
    }

    public static int getOilRefill(int step, int numPlayers) {
        return getRefillAmounts(step, numPlayers)[1];
    }

    public static int getGarbageRefill(int step, int numPlayers) {
        return getRefillAmounts(step, numPlayers)[2];
    }

    public static int getUraniumRefill(int step, int numPlayers) {
        return getRefillAmounts(step, numPlayers)[3];
    }
}
