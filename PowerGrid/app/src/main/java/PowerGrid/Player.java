package PowerGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A player in Power Grid.
 * Tracks: player id (1–6), Elektro (money), owned power plants (max 3).
 * Each player has 22 houses; cities in network are tracked via City occupancy (game state).
 */
public class Player {
    public static final int STARTING_ELEKTRO = 50;
    public static final int MAX_POWER_PLANTS = 3;
    public static final int HOUSES_PER_PLAYER = 22;

    private final int playerId;
    private int elektro;
    private final List<PowerPlant> powerPlants = new ArrayList<>();

    public Player(int playerId) {
        if (playerId < 1 || playerId > 6) {
            throw new IllegalArgumentException("playerId must be 1–6");
        }
        this.playerId = playerId;
        this.elektro = STARTING_ELEKTRO;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getElektro() {
        return elektro;
    }

    /** Pay Elektro (e.g. for plants, resources, building). Returns false if insufficient. */
    public boolean pay(int amount) {
        if (amount < 0 || amount > elektro) return false;
        elektro -= amount;
        return true;
    }

    /** Receive Elektro (e.g. from bureaucracy phase). */
    public void receive(int amount) {
        if (amount > 0) elektro += amount;
    }

    public List<PowerPlant> getPowerPlants() {
        return Collections.unmodifiableList(powerPlants);
    }

    /** Largest plant number (for player-order tiebreaker: most cities, then biggest plant). */
    public int getLargestPlantNumber() {
        if (powerPlants.isEmpty()) return 0;
        int max = 0;
        for (PowerPlant p : powerPlants) {
            if (p.getNumber() > max) max = p.getNumber();
        }
        return max;
    }

    /** Add a power plant. If already at max, caller must scrap one first (game handles that). */
    public void addPowerPlant(PowerPlant plant) {
        if (plant == null) return;
        if (powerPlants.size() >= MAX_POWER_PLANTS) {
            throw new IllegalStateException("Player can own at most " + MAX_POWER_PLANTS + " power plants");
        }
        powerPlants.add(plant);
    }

    /** Remove (scrap) a power plant. Resources on it are handled by the game. */
    public boolean removePowerPlant(PowerPlant plant) {
        return powerPlants.remove(plant);
    }

    /** Whether this player can participate in an auction (has not bought a plant this round). */
    public boolean canBidThisRound(boolean boughtThisRound) {
        return !boughtThisRound;
    }

    /** Total cities this player can supply with current plants and stored resources. */
    public int getMaxProducibleCities() {
        int total = 0;
        for (PowerPlant p : powerPlants) {
            total += p.getProducibleCities();
        }
        return total;
    }
}
