package PowerGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    /** Building cost for 1st house in a city (Step 1–3). */
    public static final int BUILD_COST_FIRST = 10;
    /** Building cost for 2nd house in a city (Step 2–3). */
    public static final int BUILD_COST_SECOND = 15;
    /** Building cost for 3rd house in a city (Step 3). */
    public static final int BUILD_COST_THIRD = 20;

    private final List<City> cities = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final Map<City, List<Edge>> adjacency = new HashMap<>();
    private final List<Player> players = new ArrayList<>();
    private final ResourceMarket resourceMarket = new ResourceMarket();

    /** Current round (1-based). Advances at end of Phase 5 (Bureaucracy). */
    private int round = 1;

    /** Step of the game (1, 2, or 3). */
    private int step = 1;

    public Game(double imgW, double imgH) {
        this(imgW, imgH, 0);
    }

    /** Creates map and optionally a set of players (2–6). */
    public Game(double imgW, double imgH, int numberOfPlayers) {
        // Create 5 cities based on image dimensions
        cities.add(new City("Center City", imgW / 2, imgH / 2));
        cities.add(new City("NW Outpost", 100, 100));
        cities.add(new City("NE Port", imgW - 100, 100));
        cities.add(new City("SW Mine", 100, imgH - 100));
        cities.add(new City("SE Capital", imgW - 100, imgH - 100));

        // Example connections (undirected, weighted)
        City center = cities.get(0);
        City nw = cities.get(1);
        City ne = cities.get(2);
        City sw = cities.get(3);
        City se = cities.get(4);

        connect(center, nw, 5);
        connect(center, ne, 5);
        connect(center, sw, 6);
        connect(center, se, 6);

        if (numberOfPlayers >= 2 && numberOfPlayers <= 6) {
            for (int i = 1; i <= numberOfPlayers; i++) {
                players.add(new Player(i));
            }
        }

        resourceMarket.fillInitialMarket();
        updateAllCityInteractability();
    }

    public List<City> getCities() {
        return Collections.unmodifiableList(cities);
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Edge> getEdgesFrom(City city) {
        List<Edge> list = adjacency.get(city);
        if (list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Connect two cities with an undirected edge and integer weight.
     */
    public Edge connect(City a, City b, int weight) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Cities must not be null");
        }
        Edge edge = new Edge(a, b, weight);
        edges.add(edge);

        adjacency.computeIfAbsent(a, k -> new ArrayList<>()).add(edge);
        adjacency.computeIfAbsent(b, k -> new ArrayList<>()).add(edge);

        return edge;
    }

    // --- Step handling ---

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step < 1 || step > 3) {
            throw new IllegalArgumentException("step must be 1, 2, or 3");
        }
        this.step = step;
        updateAllCityInteractability();
    }

    // --- Player occupancy methods (Game-level API) ---

    /**
     * Add a player (1–5) occupying the given city.
     */
    public void addPlayerToCity(City city, int playerId) {
        if (city == null) {
            throw new IllegalArgumentException("city must not be null");
        }
        city.addOccupyingPlayer(playerId);
        updateCityInteractability(city);
    }

    /**
     * Get players occupying the given city.
     */
    public List<Integer> getPlayersAtCity(City city) {
        if (city == null) {
            return Collections.emptyList();
        }
        return city.getOccupyingPlayers();
    }

    // --- Players ---

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Player getPlayer(int playerId) {
        for (Player p : players) {
            if (p.getPlayerId() == playerId) return p;
        }
        return null;
    }

    /** Number of cities in this player's network (houses placed). */
    public int getConnectedCityCount(Player player) {
        if (player == null) return 0;
        int id = player.getPlayerId();
        int count = 0;
        for (City c : cities) {
            if (c.getOccupyingPlayers().contains(id)) count++;
        }
        return count;
    }

    /** Elektro earned in Phase 5 (Bureaucracy) for the number of cities powered. */
    public int getElektroForCitiesPowered(int citiesPowered) {
        return PaymentSummary.getElektroForCitiesPowered(citiesPowered, Math.max(2, players.size()));
    }

    /** Set up 2–6 players (only if no players exist yet). */
    public void setNumberOfPlayers(int n) {
        if (n < 2 || n > 6) {
            throw new IllegalArgumentException("numberOfPlayers must be 2–6");
        }
        if (!players.isEmpty()) {
            throw new IllegalStateException("Players already initialized");
        }
        for (int i = 1; i <= n; i++) {
            players.add(new Player(i));
        }
    }

    // --- Resource market & round ---

    public ResourceMarket getResourceMarket() {
        return resourceMarket;
    }

    public int getRound() {
        return round;
    }

    /**
     * Refill the resource market (Phase 5 Bureaucracy, step 2).
     * Uses current step and player count.
     */
    public void refillMarket() {
        if (players.size() < 2) return;
        resourceMarket.refill(step, players.size());
    }

    /**
     * Call at end of Phase 5 (Bureaucracy). Refills the market and advances the round.
     */
    public void endOfRound() {
        refillMarket();
        round++;
    }

    // --- Internal helpers for interactability rules ---

    private void updateAllCityInteractability() {
        for (City city : cities) {
            updateCityInteractability(city);
        }
    }

    private void updateCityInteractability(City city) {
        // Rule:
        // - Step 1: a city that already has a player is NOT interactable
        // - Step 2 and 3: occupancy does not affect interactability
        boolean interactable;
        if (step == 1 && city.hasAnyPlayer()) {
            interactable = false;
        } else {
            interactable = true;
        }
        city.setInteractable(interactable);
    }
}

