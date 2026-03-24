package PowerGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Shortest paths on an undirected weighted graph (connection costs between cities).
 */
public final class DijkstraPathfinder {

    private DijkstraPathfinder() {}

    /**
     * @return total connection cost along edges and ordered path of cities from {@code from} to {@code to} (inclusive).
     *         Empty if unreachable.
     */
    public static PathResult shortestPath(City from, City to, Map<City, List<Edge>> adjacency) {
        if (from == null || to == null || adjacency == null) {
            return PathResult.unreachable();
        }
        if (from.equals(to)) {
            List<City> single = new ArrayList<>();
            single.add(from);
            return new PathResult(0, single);
        }

        Map<City, Integer> dist = new HashMap<>();
        Map<City, City> prev = new HashMap<>();
        Set<City> settled = new HashSet<>();
        PriorityQueue<City> pq = new PriorityQueue<>(Comparator.comparingInt(c -> dist.getOrDefault(c, Integer.MAX_VALUE)));

        dist.put(from, 0);
        pq.add(from);

        while (!pq.isEmpty()) {
            City u = pq.poll();
            if (settled.contains(u)) continue;
            settled.add(u);
            int du = dist.getOrDefault(u, Integer.MAX_VALUE);
            if (du == Integer.MAX_VALUE) continue;

            List<Edge> outs = adjacency.get(u);
            if (outs == null) continue;
            for (Edge e : outs) {
                City v = e.getOther(u);
                if (v == null) continue;
                int nd = du + e.getWeight();
                if (nd < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                    dist.put(v, nd);
                    prev.put(v, u);
                    pq.add(v);
                }
            }
        }

        if (!dist.containsKey(to)) {
            return PathResult.unreachable();
        }

        List<City> path = new ArrayList<>();
        City cur = to;
        while (cur != null) {
            path.add(cur);
            cur = prev.get(cur);
        }
        Collections.reverse(path);
        return new PathResult(dist.get(to), path);
    }

    /** Connection costs for each consecutive pair along the path (empty if length &lt; 2). */
    public static List<Integer> edgeWeightsAlongPath(List<City> path, Map<City, List<Edge>> adjacency) {
        if (path == null || path.size() < 2) return List.of();
        List<Integer> weights = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            City a = path.get(i);
            City b = path.get(i + 1);
            int w = weightBetween(a, b, adjacency);
            weights.add(w);
        }
        return weights;
    }

    private static int weightBetween(City a, City b, Map<City, List<Edge>> adjacency) {
        List<Edge> list = adjacency.get(a);
        if (list == null) return Integer.MAX_VALUE;
        for (Edge e : list) {
            if (e.getOther(a) == b) return e.getWeight();
        }
        return Integer.MAX_VALUE;
    }
}
