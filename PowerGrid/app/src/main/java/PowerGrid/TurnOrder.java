package PowerGrid;

import java.util.List;

public final class TurnOrder {

    private TurnOrder() {}

    public static void sortRoundOneByLargestPlant(List<Player> order) {
        order.sort((a, b) -> Integer.compare(b.getLargestPlantNumber(), a.getLargestPlantNumber()));
    }

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
