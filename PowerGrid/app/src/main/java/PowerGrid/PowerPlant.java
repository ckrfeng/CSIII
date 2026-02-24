package PowerGrid;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * A power plant card in Power Grid.
 * Has a number (min bid, used for tiebreaker), fuel type(s), capacity to supply cities,
 * and resource storage. Hybrid plants use coal and/or oil; ecological plants need no resources.
 */
public class PowerPlant {
    /** Plant number (03–50); minimum bid and tiebreaker. */
    private final int number;

    /** Resource type(s) needed. Empty = ecological (green/blue). Two types = hybrid. */
    private final ResourceType primaryResource;
    private final ResourceType secondaryResource; // null unless hybrid

    /** How many resource tokens are needed to produce once (e.g. 2 coal). */
    private final int resourcesRequired;

    /** How many cities this plant can supply with electricity (1–6). */
    private final int citiesSupplied;

    /** Max tokens storable on this plant (2 * resourcesRequired; 0 for ecological). */
    private final int storageCapacity;

    /** Current tokens on this plant (for owned plants). */
    private final Map<ResourceType, Integer> storedResources = new EnumMap<>(ResourceType.class);

    public PowerPlant(int number, ResourceType primaryResource, ResourceType secondaryResource,
                      int resourcesRequired, int citiesSupplied) {
        if (number < 3 || number > 50) {
            throw new IllegalArgumentException("Power plant number must be 3–50");
        }
        this.number = number;
        this.primaryResource = primaryResource;
        this.secondaryResource = secondaryResource;
        this.resourcesRequired = Math.max(0, resourcesRequired);
        this.citiesSupplied = Math.max(1, Math.min(6, citiesSupplied));
        this.storageCapacity = (primaryResource == null) ? 0 : (2 * this.resourcesRequired);
    }

    /** Single-resource plant (coal, oil, garbage, uranium). */
    public static PowerPlant fossil(int number, ResourceType resource, int resourcesRequired, int citiesSupplied) {
        return new PowerPlant(number, resource, null, resourcesRequired, citiesSupplied);
    }

    /** Hybrid coal/oil plant. */
    public static PowerPlant hybrid(int number, int resourcesRequired, int citiesSupplied) {
        return new PowerPlant(number, ResourceType.COAL, ResourceType.OIL, resourcesRequired, citiesSupplied);
    }

    /** Ecological plant (no resources). */
    public static PowerPlant ecological(int number, int citiesSupplied) {
        return new PowerPlant(number, null, null, 0, citiesSupplied);
    }

    public int getNumber() {
        return number;
    }

    public ResourceType getPrimaryResource() {
        return primaryResource;
    }

    public ResourceType getSecondaryResource() {
        return secondaryResource;
    }

    public boolean isHybrid() {
        return secondaryResource != null;
    }

    public boolean isEcological() {
        return primaryResource == null;
    }

    public int getResourcesRequired() {
        return resourcesRequired;
    }

    public int getCitiesSupplied() {
        return citiesSupplied;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    /** Total tokens currently stored (for hybrid, coal + oil). */
    public int getTotalStored() {
        int total = 0;
        for (Integer n : storedResources.values()) {
            total += n;
        }
        return total;
    }

    public Map<ResourceType, Integer> getStoredResources() {
        return Collections.unmodifiableMap(storedResources);
    }

    /** Whether this plant can store the given resource type. */
    public boolean canStore(ResourceType type) {
        if (isEcological()) return false;
        if (type == primaryResource || type == secondaryResource) return true;
        return false;
    }

    /** Add tokens (e.g. when buying resources). Caller must enforce capacity and type. */
    public void addStored(ResourceType type, int amount) {
        if (!canStore(type) || amount <= 0) return;
        int space = storageCapacity - getTotalStored();
        if (space <= 0) return;
        amount = Math.min(amount, space);
        storedResources.merge(type, amount, Integer::sum);
    }

    /** Remove tokens (e.g. when producing). Caller must enforce required amounts. */
    public void removeStored(ResourceType type, int amount) {
        int current = storedResources.getOrDefault(type, 0);
        int remove = Math.min(amount, current);
        if (remove <= 0) return;
        storedResources.merge(type, -remove, Integer::sum);
        if (storedResources.getOrDefault(type, 0) <= 0) {
            storedResources.remove(type);
        }
    }

    /** Max cities this plant can power this round given current stored resources. */
    public int getProducibleCities() {
        if (isEcological()) return citiesSupplied;
        int total = getTotalStored();
        int batches = total / resourcesRequired;
        return Math.min(batches, citiesSupplied);
    }
}
