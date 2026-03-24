package PowerGrid;

import java.util.List;

/**
 * Player order: first player is determined by most cities, then largest power plant number (tiebreak).
 * Round 1 after auctions: order by largest plant only (per user rules).
 */
public final class TurnOrder {

    private TurnOrder() {}

    /** After round 1 power plant purchases: highest plant number first. */
    public static void sortRoundOneByLargestPlant(List<Player> order) {
        order.sort((a, b) -> Integer.compare(b.getLargestPlantNumber(), a.getLargestPlantNumber()));
    }

    /** Start of each round (round 2+): most cities first, then largest plant. */
    public static void sortByCitiesThenLargestPlant(List<Player> order, Game game) {
        order.sort((a, b) -> {
            int ca = game.getConnectedCityCount(a);
            int cb = game.getConnectedCityCount(b);
            if (cb != ca) {
                return Integer.compare(cb, ca);
            }
            return Integer.compare(b.getLargestPlantNumber(), a.getLargestPlantNumber());
        });
    }
}
