/*

HABITATS are bitmasks of the habitats that the bird can occupy; 0b100 = forest, 0b010 = prarie, 0b001 = wetlands
ACTIONID will contain basic data about the nature of the action; [0, 99] = brown; [-1, -99] = white; >= 100 = pink; <= -100 = blue

 */
public enum BirdCard {
    /*
    CAPS_SNAKE_CASE_NAME("Regular Name", actionId, eggmax, habitats, nest, points, wingspan, cost{berry, fish, rat/rodent, wheat, worm, other})
     */
    ACORN_WOODPECKER("Acorn Woodpecker", -1, 4, 0b100, Nest.CAVITY, 5, 46, new int[]{0, 0, 0, 3, 0, 0}),
    COMMON_RAVEN("Common Raven", -1, 2, 0b111, Nest.ANY, 5, 135, new int[]{0, 0, 1, 0, 0, 2}),
    CHIHUAHUAN_RAVEN("Chihuahuan Raven", -1, 3, 0b010, Nest.ANY, 4, 112, new int[]{0, 0, 1, 0, 0, 2}),
    BONELLI_EAGLE("Bonelli Eagle", -1, 1, 0b111, Nest.ANY, 8, 160, new int[]{0, 0, 3, 0, 0, 0}),
    AMERICAN_ROBIN("American Robin", -2, 3, 0b110, Nest.BOWL, 2, 30, new int[]{1, 0, 0, 1, 0, 0}),
    BLUE_JAY("Blue Jay", -1, 3, 0b100, Nest.ANY, 3, 33, new int[]{1, 0, 0, 1, 0, 0}),
    NORTHERN_CARDINAL("Northern Cardinal", -1, 3, 0b100, Nest.BOWL, 3, 37, new int[]{1, 0, 0, 1, 0, 0}),
    GREAT_HORNED_OWL("Great Horned Owl", -1, 2, 0b100, Nest.CAVITY, 6, 430, new int[]{0, 1, 0, 0, 0, 1}),
    MALLARD_DUCK("Mallard", -1, 3, 0b001, Nest.PLATFORM, 4, 60, new int[]{0, 1, 0, 0, 0, 0}),
    SANDHILL_CRANE("Sandhill Crane", -1, 2, 0b001, Nest.GROUND, 4, 150, new int[]{0, 0, 0, 1, 0, 0}),
    GOLDEN_EAGLE("Golden Eagle", -1, 2, 0b100, Nest.PLATFORM, 8, 220, new int[]{0, 0, 2, 0, 0, 1}),
    HOUSE_SPARROW("House Sparrow", -1, 4, 0b110, Nest.BOWL, 1, 16, new int[]{1, 0, 0, 0, 0, 0}),
    BARN_SWALLOW("Barn Swallow", -1, 3, 0b001, Nest.PLATFORM, 2, 32, new int[]{1, 0, 0, 0, 0, 0}),
    CANADA_GOOSE("Canada Goose", -1, 3, 0b001, Nest.GROUND, 3, 125, new int[]{0, 0, 0, 1, 0, 0}),
    FRANKLINS_GULL("Franklin Gull", -1, 2, 0b011, Nest.ANY, 3, 91, new int[]{0, 1, 0, 0, 0, 1}),
    KILL_DEER("Kill deer", -1, 3, 0b011, Nest.GROUND, 1, 46, new int[]{0, 0, 0, 1, 1, 0});

    public final String name;
    public final int actionId;
    public final int eggMax;
    public final int habitats;
    public final Nest nest;
    public final int points;
    public final int wingspan;
    public final int[] foodCost;

    BirdCard(String name, int actionId, int eggMax, int habitats, Nest nest, int points, int wingspan, int[] foodCost) {
        this.name = name;
        this.actionId = actionId;
        this.eggMax = eggMax;
        this.habitats = habitats;
        this.nest = nest;
        this.points = points;
        this.wingspan = wingspan;
        this.foodCost = foodCost;
    }
}