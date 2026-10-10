package PowerGrid;

public final class PaymentSummary {
    private static final int[] INCOME = {
        10,  22, 33, 44, 54, 64, 73, 82, 90, 98,
        105, 112, 118, 124, 129, 134, 138, 142, 145, 148, 150
    };
    private static final int[] INCOME_6_PLAYERS = {
        10,  20, 31, 42, 51, 60, 68, 75, 82, 88,
        93, 98, 102, 106, 109, 112, 115, 118, 120, 122, 124
    };

    public static int getElektroForCitiesPowered(int citiesPowered, int numberOfPlayers) {
        int index = Math.min(Math.max(0, citiesPowered), 20);
        if (numberOfPlayers == 6) {
            return INCOME_6_PLAYERS[index];
        }
        return INCOME[index];
    }
}
