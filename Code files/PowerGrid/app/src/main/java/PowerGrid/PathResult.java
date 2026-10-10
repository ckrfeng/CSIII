package PowerGrid;

import java.util.Collections;
import java.util.List;

/** Result of a shortest-path query between two cities. */
public final class PathResult {
    private final int totalCost;
    private final List<City> path;

    public PathResult(int totalCost, List<City> path) {
        this.totalCost = totalCost;
        this.path = path != null ? path : Collections.emptyList();
    }

    public static PathResult unreachable() {
        return new PathResult(Integer.MAX_VALUE, Collections.emptyList());
    }

    public boolean isReachable() {
        return totalCost < Integer.MAX_VALUE;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public List<City> getPath() {
        return path;
    }
}
