/*

HABITATS are bitmasks of the habitats that the bird can occupy; 0b100 = forest, 0b010 = prarie, 0b001 = wetlands
ACTIONID will contain basic data about the nature of the action; [0, 99] = brown; [-1, -99] = white; >= 100 = pink; <= -100 = blue

 */
public enum BirdCard {
    /*
    CAPS_SNAKE_CASE_NAME("Regular Name", actionId, eggmax, habitats, nest, points, wingspan, cost{berry, fish, rat/rodent, wheat, worm, other})
     */
    // ACORN_WOODPECKER("Acorn Woodpecker", 0, 4, 0b100, Nest.CAVITY, 5, 46, new int[]{0, 0, 0, 3, 0, 0}),
    // AMERICAN_BITTERN("American Bittern", 0, 2, 0b001, Nest.PLATFORM, 7, 107, new int[]{0, 1, 1, 0, 1, 0}),
    // AMERICAN_COOT("American Coot", 0, 5, 0b001, Nest.PLATFORM, 3, 61, new int[]{0, 0, 0, 1, 0, 1}),
    // AMERICAN_CROW("American Crow", 0, 2, 0b111, Nest.PLATFORM, 4, 99, new int[]{0, 0, 0, 0, 0, 1}),
    // AMERICAN_GOLDFINCH("American Goldfinch", -1, 3, 0b010, Nest.BOWL, 3, 23, new int[]{0, 0, 0, 2, 0, 0}),
    // AMERICAN_KESTREL("American Kestrel", 0, 3, 0b010, Nest.CAVITY, 5, 56, new int[]{0, 0, 1, 0, 1, 0}),
    // AMERICAN_OYSTERCATCHER("American Oystercatcher", -1, 2, 0b001, Nest.GROUND, 5, 81, new int[]{0, 0, 0, 0, 2, 0}),
    // AMERICAN_WHITE_PELICAN("American White Pelican", 0, 1, 0b001, Nest.GROUND, 5, 274, new int[]{0, 2, 0, 0, 0, 0}),
    // AMERICAN_WOODCOCK("American Woodcock", 0, 2, 0b110, Nest.GROUND, 9, 46, new int[]{0, 0, 0, 1, 2, 0}),
    // ANHINGA("Anhinga", 0, 2, 0b001, Nest.PLATFORM, 6, 114, new int[]{0, 2, 0, 0, 0, 0}),
    // ANNA_S_HUMMINGBIRD("Anna's Hummingbird", 0, 2, 0b111, Nest.BOWL, 4, 13, new int[]{0, 0, 0, 0, 0, 1}),
    // ASH_THROATED_FLYCATCHER("Ash-Throated Flycatcher", -1, 4, 0b010, Nest.CAVITY, 4, 30, new int[]{1, 0, 0, 0, 2, 0}),
    // ATLANTIC_PUFFIN("Atlantic Puffin", -1, 1, 0b001, Nest.ANY, 8, 53, new int[]{0, 3, 0, 0, 0, 0}),
    // BAIRD_S_SPARROW("Baird's Sparrow", 0, 2, 0b010, Nest.GROUND, 3, 23, new int[]{0, 0, 0, 1, 1, 0}),
    // BALD_EAGLE("Bald Eagle", -1, 1, 0b001, Nest.PLATFORM, 9, 203, new int[]{0, 2, 1, 0, 0, 0}),
    // BALTIMORE_ORIOLE("Baltimore Oriole", 0, 2, 0b100, Nest.ANY, 9, 30, new int[]{2, 0, 0, 0, 1, 0}),
    // BARN_OWL("Barn Owl", 0, 4, 0b111, Nest.CAVITY, 5, 107, new int[]{0, 0, 2, 0, 0, 0}),
    // BARN_SWALLOW("Barn Swallow", 0, 3, 0b011, Nest.ANY, 1, 38, new int[]{0, 0, 0, 0, 1, 0}),
    // BARRED_OWL("Barred Owl", 0, 2, 0b100, Nest.CAVITY, 3, 107, new int[]{0, 0, 1, 0, 0, 0}),
    // BEWICK_S_WREN("Bewick's Wren", 0, 3, 0b111, Nest.CAVITY, 4, 18, new int[]{0, 0, 0, 1, 2, 0}),
    // BLACK_SKIMMER("Black Skimmer", 0, 2, 0b001, Nest.GROUND, 6, 112, new int[]{0, 2, 0, 0, 0, 0}),
    // BLACK_CROWNED_NIGHT_HERON("Black-Crowned Night-Heron", 0, 2, 0b001, Nest.PLATFORM, 9, 112, new int[]{0, 1, 1, 0, 1, 0}),
    // BLACK_NECKED_STILT("Black-Necked Stilt", -1, 2, 0b001, Nest.GROUND, 4, 74, new int[]{0, 0, 0, 0, 2, 0}),
    // BLUE_GROSBEAK("Blue Grosbeak", 0, 3, 0b111, Nest.BOWL, 4, 28, new int[]{0, 0, 0, 2, 1, 0}),
    // BLUE_JAY("Blue Jay", 0, 2, 0b100, Nest.BOWL, 3, 41, new int[]{0, 0, 0, 1, 0, 1}),
    // BLUE_GRAY_GNATCATCHER("Blue-Gray Gnatcatcher", 0, 3, 0b100, Nest.BOWL, 1, 15, new int[]{0, 0, 0, 0, 1, 0}),
    // BLUE_WINGED_WARBLER("Blue-Winged Warbler", 0, 2, 0b110, Nest.BOWL, 8, 20, new int[]{0, 0, 0, 0, 2, 0}),
    // BOBOLINK("Bobolink", -1, 3, 0b010, Nest.GROUND, 4, 30, new int[]{0, 0, 0, 2, 1, 0}),
    // BREWER_S_BLACKBIRD("Brewer's Blackbird", 0, 3, 0b010, Nest.BOWL, 3, 41, new int[]{0, 0, 0, 1, 0, 1}),
    // BROWN_PELICAN("Brown Pelican", -1, 2, 0b001, Nest.PLATFORM, 4, 201, new int[]{0, 2, 0, 0, 0, 0}),
    // BURROWING_OWL("Burrowing Owl", 0, 4, 0b010, Nest.ANY, 5, 53, new int[]{0, 0, 1, 0, 1, 0}),
    // BUSHTIT("Bushtit", 0, 5, 0b111, Nest.ANY, 2, 15, new int[]{0, 0, 0, 1, 1, 0}),
    // CALIFORNIA_CONDOR("California Condor", -1, 1, 0b111, Nest.GROUND, 1, 277, new int[]{0, 0, 0, 0, 0, 0}),
    // CALIFORNIA_QUAIL("California Quail", 0, 6, 0b110, Nest.GROUND, 3, 36, new int[]{0, 0, 0, 2, 1, 0}),
    // CANADA_GOOSE("Canada Goose", 0, 3, 0b011, Nest.GROUND, 3, 132, new int[]{0, 0, 0, 2, 0, 0}),
    // CASSIN_S_FINCH("Cassin's Finch", -1, 3, 0b100, Nest.BOWL, 4, 30, new int[]{1, 0, 0, 1, 0, 0}),
    // CASSIN_S_SPARROW("Cassin's Sparrow", 0, 2, 0b010, Nest.GROUND, 3, 20, new int[]{0, 0, 0, 1, 1, 0}),
    // CEDAR_WAXWING("Cedar Waxwing", 0, 3, 0b110, Nest.BOWL, 3, 25, new int[]{2, 0, 0, 0, 0, 0}),
    // CERULEAN_WARBLER("Cerulean Warbler", -1, 2, 0b100, Nest.BOWL, 4, 20, new int[]{0, 0, 0, 1, 1, 0}),
    // CHESTNUT_COLLARED_LONGSPUR("Chestnut-Collared Longspur", -1, 4, 0b010, Nest.GROUND, 5, 25, new int[]{0, 0, 0, 2, 1, 0}),
    // CHIHUAHUAN_RAVEN("Chihuahuan Raven", 0, 3, 0b010, Nest.PLATFORM, 4, 112, new int[]{0, 0, 1, 0, 0, 2}),
    // CHIMNEY_SWIFT("Chimney Swift", 0, 2, 0b111, Nest.ANY, 3, 36, new int[]{0, 0, 0, 0, 2, 0}),
    // CLARK_S_GREBE("Clark's Grebe", 0, 2, 0b001, Nest.ANY, 5, 61, new int[]{0, 1, 0, 0, 0, 0}),
    // CLARK_S_NUTCRACKER("Clark's Nutcracker", 0, 2, 0b100, Nest.PLATFORM, 5, 61, new int[]{0, 0, 0, 2, 0, 1}),
    // COMMON_GRACKLE("Common Grackle", 0, 3, 0b111, Nest.BOWL, 3, 43, new int[]{0, 0, 0, 1, 0, 1}),
    // COMMON_LOON("Common Loon", 0, 1, 0b001, Nest.GROUND, 6, 117, new int[]{0, 1, 0, 0, 0, 1}),
    // COMMON_MERGANSER("Common Merganser", 0, 4, 0b001, Nest.CAVITY, 5, 86, new int[]{0, 1, 0, 0, 0, 1}),
    // COMMON_NIGHTHAWK("Common Nighthawk", 0, 2, 0b111, Nest.GROUND, 3, 56, new int[]{0, 0, 0, 0, 2, 0}),
    // COMMON_RAVEN("Common Raven", 0, 2, 0b111, Nest.PLATFORM, 5, 135, new int[]{0, 0, 1, 0, 0, 2}),
    // COMMON_YELLOWTHROAT("Common Yellowthroat", 0, 4, 0b001, Nest.BOWL, 1, 18, new int[]{0, 0, 0, 0, 1, 0}),
    // COOPER_S_HAWK("Cooper's Hawk", 0, 2, 0b100, Nest.PLATFORM, 3, 79, new int[]{0, 0, 1, 0, 0, 0}),
    // DARK_EYED_JUNCO("Dark-Eyed Junco", 0, 3, 0b110, Nest.GROUND, 3, 23, new int[]{0, 0, 0, 1, 1, 0}),
    // DICKCISSEL("Dickcissel", 0, 3, 0b010, Nest.GROUND, 4, 25, new int[]{0, 0, 0, 2, 1, 0}),
    // DOUBLE_CRESTED_CORMORANT("Double-Crested Cormorant", 0, 3, 0b001, Nest.PLATFORM, 3, 132, new int[]{0, 1, 0, 0, 0, 1}),
    // EASTERN_BLUEBIRD("Eastern Bluebird", -1, 5, 0b010, Nest.CAVITY, 4, 33, new int[]{1, 0, 0, 0, 1, 0}),
    // FERRUGINOUS_HAWK("Ferruginous Hawk", 0, 2, 0b010, Nest.PLATFORM, 6, 142, new int[]{0, 0, 2, 0, 0, 0}),
    // FISH_CROW("Fish Crow", 0, 2, 0b111, Nest.PLATFORM, 6, 91, new int[]{0, 1, 0, 0, 0, 1}),
    // FRANKLIN_S_GULL("Franklin's Gull", 0, 2, 0b011, Nest.ANY, 3, 91, new int[]{0, 1, 0, 0, 0, 1}),
    // GOLDEN_EAGLE("Golden Eagle", 0, 1, 0b011, Nest.PLATFORM, 8, 201, new int[]{0, 0, 3, 0, 0, 0}),
    // GREAT_BLUE_HERON("Great Blue Heron", -1, 2, 0b001, Nest.PLATFORM, 5, 183, new int[]{0, 1, 0, 0, 1, 0}),
    // GREAT_CRESTED_FLYCATCHER("Great Crested Flycatcher", 0, 3, 0b100, Nest.CAVITY, 5, 33, new int[]{1, 0, 0, 0, 1, 0}),
    // GREAT_EGRET("Great Egret", -1, 3, 0b001, Nest.PLATFORM, 7, 130, new int[]{0, 2, 1, 0, 0, 0}),
    // GREAT_HORNED_OWL("Great Horned Owl", 0, 2, 0b100, Nest.PLATFORM, 8, 112, new int[]{0, 0, 3, 0, 0, 0}),
    // GREATER_ROADRUNNER("Greater Roadrunner", 0, 2, 0b010, Nest.PLATFORM, 7, 56, new int[]{0, 0, 1, 0, 1, 1}),
    // HERMIT_THRUSH("Hermit Thrush", 0, 2, 0b100, Nest.ANY, 7, 30, new int[]{2, 0, 0, 0, 1, 0}),
    // HOODED_WARBLER("Hooded Warbler", 0, 3, 0b100, Nest.BOWL, 7, 18, new int[]{0, 0, 0, 0, 2, 0}),
    // HOUSE_FINCH("House Finch", 0, 6, 0b111, Nest.BOWL, 3, 25, new int[]{1, 0, 0, 1, 0, 0}),
    // HOUSE_WREN("House Wren", -1, 5, 0b110, Nest.CAVITY, 1, 15, new int[]{0, 0, 0, 0, 1, 0}),
    // INCA_DOVE("Inca Dove", -1, 4, 0b010, Nest.PLATFORM, 2, 28, new int[]{0, 0, 0, 2, 0, 0}),
    // INDIGO_BUNTING("Indigo Bunting", 0, 3, 0b110, Nest.BOWL, 5, 20, new int[]{1, 0, 0, 1, 1, 0}),
    // JUNIPER_TITMOUSE("Juniper Titmouse", 0, 3, 0b100, Nest.CAVITY, 4, 23, new int[]{0, 0, 0, 1, 1, 0}),
    // KING_RAIL("King Rail", -1, 6, 0b001, Nest.PLATFORM, 4, 51, new int[]{0, 1, 0, 0, 1, 1}),
    // LAZULI_BUNTING("Lazuli Bunting", 0, 4, 0b010, Nest.BOWL, 4, 23, new int[]{1, 0, 0, 1, 1, 0}),
    // LINCOLN_S_SPARROW("Lincoln's Sparrow", 0, 2, 0b111, Nest.GROUND, 3, 20, new int[]{0, 0, 0, 1, 1, 0}),
    // MOUNTAIN_BLUEBIRD("Mountain Bluebird", -1, 5, 0b010, Nest.CAVITY, 4, 36, new int[]{1, 0, 0, 0, 1, 0}),
    // MOURNING_DOVE("Mourning Dove", 0, 5, 0b111, Nest.PLATFORM, 0, 46, new int[]{0, 0, 0, 1, 0, 0}),
    // NORTHERN_BOBWHITE("Northern Bobwhite", 0, 6, 0b010, Nest.GROUND, 5, 33, new int[]{0, 0, 0, 3, 0, 0}),
    // NORTHERN_CARDINAL("Northern Cardinal", 0, 5, 0b100, Nest.BOWL, 3, 30, new int[]{1, 0, 0, 1, 0, 0}),
    // NORTHERN_HARRIER("Northern Harrier", 0, 2, 0b011, Nest.PLATFORM, 3, 109, new int[]{0, 0, 1, 0, 0, 0}),
    // NORTHERN_SHOVELER("Northern Shoveler", 0, 4, 0b001, Nest.GROUND, 7, 76, new int[]{0, 0, 0, 2, 1, 0}),
    // OSPREY("Osprey", 0, 2, 0b001, Nest.PLATFORM, 5, 160, new int[]{0, 1, 0, 0, 0, 0}),
    // PAINTED_BUNTING("Painted Bunting", -1, 4, 0b010, Nest.BOWL, 5, 23, new int[]{0, 0, 0, 2, 1, 0}),
    // PEREGRINE_FALCON("Peregrine Falcon", 0, 2, 0b011, Nest.PLATFORM, 5, 104, new int[]{0, 0, 2, 0, 0, 0}),
    // PILEATED_WOODPECKER("Pileated Woodpecker", 0, 2, 0b100, Nest.CAVITY, 4, 74, new int[]{1, 0, 0, 0, 1, 0}),
    // PINE_SISKIN("Pine Siskin", 0, 2, 0b100, Nest.BOWL, 3, 23, new int[]{0, 0, 0, 2, 0, 0}),
    // PROTHONOTARY_WARBLER("Prothonotary Warbler", 0, 4, 0b101, Nest.CAVITY, 8, 23, new int[]{0, 0, 0, 1, 2, 0}),
    // PURPLE_GALLINULE("Purple Gallinule", 0, 4, 0b001, Nest.PLATFORM, 7, 56, new int[]{1, 0, 0, 1, 0, 1}),
    // PURPLE_MARTIN("Purple Martin", 0, 3, 0b011, Nest.CAVITY, 2, 46, new int[]{0, 0, 0, 0, 1, 0}),
    // PYGMY_NUTHATCH("Pygmy Nuthatch", 0, 4, 0b100, Nest.CAVITY, 2, 20, new int[]{0, 0, 0, 1, 1, 0}),
    // RED_CROSSBILL("Red Crossbill", 0, 2, 0b100, Nest.BOWL, 6, 28, new int[]{0, 0, 0, 2, 0, 0}),
    // RED_COCKADED_WOODPECKER("Red-Cockaded Woodpecker", -1, 2, 0b100, Nest.CAVITY, 4, 36, new int[]{1, 0, 0, 0, 1, 0}),
    // RED_HEADED_WOODPECKER("Red-Headed Woodpecker", 0, 3, 0b111, Nest.CAVITY, 4, 43, new int[]{0, 0, 0, 1, 1, 1}),
    // RED_SHOULDERED_HAWK("Red-Shouldered Hawk", 0, 2, 0b100, Nest.PLATFORM, 3, 102, new int[]{0, 0, 1, 0, 0, 0}),
    // RED_TAILED_HAWK("Red-Tailed Hawk", 0, 2, 0b111, Nest.PLATFORM, 5, 124, new int[]{0, 0, 2, 0, 0, 0}),
    // RED_WINGED_BLACKBIRD("Red-Winged Blackbird", 0, 3, 0b011, Nest.BOWL, 2, 33, new int[]{0, 0, 0, 1, 0, 0}),
    // RING_BILLED_GULL("Ring-Billed Gull", 0, 2, 0b001, Nest.GROUND, 4, 122, new int[]{0, 0, 0, 0, 0, 2}),
    // ROSE_BREASTED_GROSBEAK("Rose-Breasted Grosbeak", 0, 3, 0b100, Nest.BOWL, 6, 33, new int[]{1, 0, 0, 1, 1, 0}),
    // ROSEATE_SPOONBILL("Roseate Spoonbill", -1, 2, 0b001, Nest.PLATFORM, 6, 127, new int[]{0, 1, 0, 1, 1, 0}),
    // RUBY_THROATED_HUMMINGBIRD("Ruby-Throated Hummingbird", 0, 2, 0b111, Nest.BOWL, 4, 10, new int[]{0, 0, 0, 0, 0, 1}),
    // SANDHILL_CRANE("Sandhill Crane", 0, 1, 0b011, Nest.GROUND, 5, 196, new int[]{0, 0, 0, 2, 0, 1}),
    // SAY_S_PHOEBE("Say's Phoebe", -1, 3, 0b010, Nest.BOWL, 5, 33, new int[]{0, 0, 0, 0, 3, 0}),
    // SCISSOR_TAILED_FLYCATCHER("Scissor-Tailed Flycatcher", 0, 2, 0b010, Nest.BOWL, 8, 38, new int[]{1, 0, 0, 0, 2, 0}),
    // SPOTTED_OWL("Spotted Owl", -1, 1, 0b100, Nest.CAVITY, 5, 102, new int[]{0, 0, 2, 0, 0, 0}),
    // SPOTTED_SANDPIPER("Spotted Sandpiper", 0, 2, 0b001, Nest.GROUND, 5, 38, new int[]{0, 0, 0, 0, 1, 0}),
    // SPRAGUE_S_PIPIT("Sprague's Pipit", -1, 3, 0b010, Nest.GROUND, 3, 25, new int[]{0, 0, 0, 1, 1, 0}),
    // STELLER_S_JAY("Steller's Jay", 0, 2, 0b100, Nest.BOWL, 5, 48, new int[]{0, 0, 0, 2, 0, 1}),
    // SWAINSON_S_HAWK("Swainson's Hawk", 0, 2, 0b010, Nest.PLATFORM, 5, 130, new int[]{0, 0, 1, 0, 1, 0}),
    // TREE_SWALLOW("Tree Swallow", 0, 4, 0b001, Nest.CAVITY, 3, 38, new int[]{1, 0, 0, 0, 1, 0}),
    // TRUMPETER_SWAN("Trumpeter Swan", 0, 2, 0b001, Nest.GROUND, 9, 203, new int[]{0, 0, 0, 2, 0, 1}),
    // VIOLET_GREEN_SWALLOW("Violet-Green Swallow", 0, 3, 0b111, Nest.CAVITY, 3, 36, new int[]{0, 0, 0, 0, 2, 0}),
    // WESTERN_MEADOWLARK("Western Meadowlark", 0, 4, 0b010, Nest.GROUND, 2, 38, new int[]{0, 0, 0, 1, 1, 0}),
    // WESTERN_TANAGER("Western Tanager", 0, 2, 0b100, Nest.BOWL, 6, 30, new int[]{1, 0, 0, 0, 2, 0}),
    // WHITE_CROWNED_SPARROW("White-Crowned Sparrow", 0, 5, 0b111, Nest.GROUND, 2, 25, new int[]{0, 0, 0, 1, 1, 0}),
    // WHITE_FACED_IBIS("White-Faced Ibis", 0, 2, 0b001, Nest.PLATFORM, 8, 91, new int[]{0, 1, 0, 0, 2, 0}),
    // WHOOPING_CRANE("Whooping Crane", -1, 1, 0b001, Nest.GROUND, 6, 221, new int[]{0, 0, 0, 0, 0, 3}),
    // WILD_TURKEY("Wild Turkey", 0, 5, 0b110, Nest.GROUND, 8, 135, new int[]{1, 0, 0, 2, 0, 0}),
    // WILSON_S_SNIPE("Wilson's Snipe", 0, 2, 0b001, Nest.GROUND, 5, 41, new int[]{0, 0, 0, 0, 1, 0}),
    // WOOD_DUCK("Wood Duck", 0, 4, 0b101, Nest.CAVITY, 4, 76, new int[]{1, 0, 0, 2, 0, 0}),
    // WOOD_STORK("Wood Stork", -1, 2, 0b001, Nest.PLATFORM, 6, 155, new int[]{0, 1, 1, 0, 0, 1}),
    // YELLOW_BELLIED_SAPSUCKER("Yellow-Bellied Sapsucker", 0, 3, 0b100, Nest.CAVITY, 3, 41, new int[]{1, 0, 0, 0, 1, 0}),
    // YELLOW_BREASTED_CHAT("Yellow-Breasted Chat", 0, 3, 0b111, Nest.BOWL, 5, 25, new int[]{2, 0, 0, 0, 1, 0}),
    // YELLOW_HEADED_BLACKBIRD("Yellow-Headed Blackbird", 0, 3, 0b001, Nest.BOWL, 4, 38, new int[]{0, 0, 0, 1, 1, 0});
    
    GREAT_CRESTED_FLYCATCHER("Great Crested Flycatcher", 0, 3, 0b100, Nest.CAVITY, 5, 33, new int[]{1, 0, 0, 0, 1, 0}),
    INDIGO_BUNTING("Indigo Bunting", 0, 3, 0b110, Nest.BOWL, 5, 20, new int[]{1, 0, 0, 1, 1, 0}),
    ROSE_BREASTED_GROSBEAK("Rose-Breasted Grosbeak", 0, 3, 0b100, Nest.BOWL, 6, 33, new int[]{1, 0, 0, 1, 1, 0}),
    WESTERN_TANAGER("Western Tanager", 0, 2, 0b100, Nest.BOWL, 6, 30, new int[]{1, 0, 0, 0, 2, 0}),

    BALD_EAGLE("Bald Eagle", 1, 1, 0b001, Nest.PLATFORM, 9, 203, new int[]{0, 2, 1, 0, 0, 0}),

    ANNA_S_HUMMINGBIRD("Anna's Hummingbird", 2, 2, 0b111, Nest.BOWL, 4, 13, new int[]{0, 0, 0, 0, 0, 1}),
    RUBY_THROATED_HUMMINGBIRD("Ruby-Throated Hummingbird", 2, 2, 0b111, Nest.BOWL, 4, 10, new int[]{0, 0, 0, 0, 0, 1}),

    HERMIT_THRUSH("Hermit Thrush", 3, 2, 0b100, Nest.ANY, 7, 30, new int[]{2, 0, 0, 0, 1, 0}),

    BLUE_GRAY_GNATCATCHER("Blue-Gray Gnatcatcher", 5, 3, 0b100, Nest.BOWL, 1, 15, new int[]{0, 0, 0, 0, 1, 0}),
    NORTHERN_CARDINAL("Northern Cardinal", 5, 5, 0b100, Nest.BOWL, 3, 30, new int[]{1, 0, 0, 1, 0, 0}),

    AMERICAN_GOLDFINCH("American Goldfinch", 6, 3, 0b010, Nest.BOWL, 3, 23, new int[]{0, 0, 0, 2, 0, 0}),
    BROWN_PELICAN("Brown Pelican", 6, 2, 0b001, Nest.PLATFORM, 4, 201, new int[]{0, 2, 0, 0, 0, 0}),

    SCISSOR_TAILED_FLYCATCHER("Scissor-Tailed Flycatcher", 7, 2, 0b010, Nest.BOWL, 8, 38, new int[]{1, 0, 0, 0, 2, 0}),
    OSPREY("Osprey", 7, 2, 0b001, Nest.PLATFORM, 5, 160, new int[]{0, 1, 0, 0, 0, 0}),
    BALTIMORE_ORIOLE("Baltimore Oriole", 7, 2, 0b100, Nest.ANY, 9, 30, new int[]{2, 0, 0, 0, 1, 0}),
    RED_CROSSBILL("Red Crossbill", 7, 2, 0b100, Nest.BOWL, 6, 28, new int[]{0, 0, 0, 2, 0, 0}),

    AMERICAN_CROW("American Crow", 9, 2, 0b111, Nest.PLATFORM, 4, 99, new int[]{0, 0, 0, 0, 0, 1}),
    BLACK_CROWNED_NIGHT_HERON("Black-Crowned Night-Heron", 9, 2, 0b001, Nest.PLATFORM, 9, 112, new int[]{0, 1, 1, 0, 1, 0}),
    FISH_CROW("Fish Crow", 9, 2, 0b111, Nest.PLATFORM, 6, 91, new int[]{0, 1, 0, 0, 0, 1}),
    CHIHUAHUAN_RAVEN("Chihuahuan Raven", 9, 3, 0b010, Nest.PLATFORM, 4, 112, new int[]{0, 0, 1, 0, 0, 2}),
    COMMON_RAVEN("Common Raven", 9, 2, 0b111, Nest.PLATFORM, 5, 135, new int[]{0, 0, 1, 0, 0, 2}),

    JUNIPER_TITMOUSE("Juniper Titmouse", 11, 3, 0b100, Nest.CAVITY, 4, 23, new int[]{0, 0, 0, 1, 1, 0}),

    BLUE_JAY("Blue Jay", 12, 2, 0b100, Nest.BOWL, 3, 41, new int[]{0, 0, 0, 1, 0, 1}),
    CLARK_S_NUTCRACKER("Clark's Nutcracker", 12, 2, 0b100, Nest.PLATFORM, 5, 61, new int[]{0, 0, 0, 2, 0, 1}),
    RED_HEADED_WOODPECKER("Red-Headed Woodpecker", 12, 3, 0b111, Nest.CAVITY, 4, 43, new int[]{0, 0, 0, 1, 1, 1}),
    STELLER_S_JAY("Steller's Jay", 12, 2, 0b100, Nest.BOWL, 5, 48, new int[]{0, 0, 0, 2, 0, 1}),
    ACORN_WOODPECKER("Acorn Woodpecker", 12, 4, 0b100, Nest.CAVITY, 5, 46, new int[]{0, 0, 0, 3, 0, 0}),

    ASH_THROATED_FLYCATCHER("Ash-Throated Flycatcher", 15, 4, 0b010, Nest.CAVITY, 4, 30, new int[]{1, 0, 0, 0, 2, 0}),
    BOBOLINK("Bobolink", 15, 3, 0b010, Nest.GROUND, 4, 30, new int[]{0, 0, 0, 2, 1, 0}),
    INCA_DOVE("Inca Dove", 15, 4, 0b010, Nest.PLATFORM, 2, 28, new int[]{0, 0, 0, 2, 0, 0}),
    SAY_S_PHOEBE("Say's Phoebe", 15, 3, 0b010, Nest.BOWL, 5, 33, new int[]{0, 0, 0, 0, 3, 0}),

    PILEATED_WOODPECKER("Pileated Woodpecker", 16, 2, 0b100, Nest.CAVITY, 4, 74, new int[]{1, 0, 0, 0, 1, 0}),
    LAZULI_BUNTING("Lazuli Bunting", 16, 4, 0b010, Nest.BOWL, 4, 23, new int[]{1, 0, 0, 1, 1, 0}),
    WESTERN_MEADOWLARK("Western Meadowlark", 16, 4, 0b010, Nest.GROUND, 2, 38, new int[]{0, 0, 0, 1, 1, 0}),

    BAIRD_S_SPARROW("Baird's Sparrow", 17, 2, 0b010, Nest.GROUND, 3, 23, new int[]{0, 0, 0, 1, 1, 0}),
    CASSIN_S_SPARROW("Cassin's Sparrow", 17, 2, 0b010, Nest.GROUND, 3, 20, new int[]{0, 0, 0, 1, 1, 0}),

    CALIFORNIA_QUAIL("California Quail", 18, 6, 0b110, Nest.GROUND, 3, 36, new int[]{0, 0, 0, 2, 1, 0}),
    MOURNING_DOVE("Mourning Dove", 18, 5, 0b111, Nest.PLATFORM, 0, 46, new int[]{0, 0, 0, 1, 0, 0}),
    NORTHERN_BOBWHITE("Northern Bobwhite", 18, 6, 0b010, Nest.GROUND, 5, 33, new int[]{0, 0, 0, 3, 0, 0}),

    BLACK_NECKED_STILT("Black-Necked Stilt", 20, 2, 0b001, Nest.GROUND, 4, 74, new int[]{0, 0, 0, 0, 2, 0}),

    CLARK_S_GREBE("Clark's Grebe", 21, 2, 0b001, Nest.ANY, 5, 61, new int[]{0, 1, 0, 0, 0, 0}),

    COMMON_YELLOWTHROAT("Common Yellowthroat", 22, 4, 0b001, Nest.BOWL, 1, 18, new int[]{0, 0, 0, 0, 1, 0}),
    WOOD_DUCK("Wood Duck", 22, 4, 0b101, Nest.CAVITY, 4, 76, new int[]{1, 0, 0, 2, 0, 0}),

    FRANKLIN_S_GULL("Franklin's Gull", 23, 2, 0b011, Nest.ANY, 3, 91, new int[]{0, 1, 0, 0, 0, 1}),

    NORTHERN_SHOVELER("Northern Shoveler", 24, 4, 0b001, Nest.GROUND, 7, 76, new int[]{0, 0, 0, 2, 1, 0}),
    PURPLE_GALLINULE("Purple Gallinule", 24, 4, 0b001, Nest.PLATFORM, 7, 56, new int[]{1, 0, 0, 1, 0, 1}),
    SPOTTED_SANDPIPER("Spotted Sandpiper", 24, 2, 0b001, Nest.GROUND, 5, 38, new int[]{0, 0, 0, 0, 1, 0}),
    WILSON_S_SNIPE("Wilson's Snipe", 24, 2, 0b001, Nest.GROUND, 5, 41, new int[]{0, 0, 0, 0, 1, 0}),

    AMERICAN_BITTERN("American Bittern", 25, 2, 0b001, Nest.PLATFORM, 7, 107, new int[]{0, 1, 1, 0, 1, 0}),
    COMMON_LOON("Common Loon", 25, 1, 0b001, Nest.GROUND, 6, 117, new int[]{0, 1, 0, 0, 0, 1}),

    AMERICAN_OYSTERCATCHER("American Oystercatcher", 26, 2, 0b001, Nest.GROUND, 5, 81, new int[]{0, 0, 0, 0, 2, 0}),

    RED_WINGED_BLACKBIRD("Red-Winged Blackbird", 28, 3, 0b011, Nest.BOWL, 2, 33, new int[]{0, 0, 0, 1, 0, 0}),
    COMMON_GRACKLE("Common Grackle", 28, 3, 0b111, Nest.BOWL, 3, 43, new int[]{0, 0, 0, 1, 0, 1}),
    BREWER_S_BLACKBIRD("Brewer's Blackbird", 28, 3, 0b010, Nest.BOWL, 3, 41, new int[]{0, 0, 0, 1, 0, 1}),
    YELLOW_HEADED_BLACKBIRD("Yellow-Headed Blackbird", 28, 3, 0b001, Nest.BOWL, 4, 38, new int[]{0, 0, 0, 1, 1, 0}),
    BUSHTIT("Bushtit", 28, 5, 0b111, Nest.ANY, 2, 15, new int[]{0, 0, 0, 1, 1, 0}),
    DICKCISSEL("Dickcissel", 28, 3, 0b010, Nest.GROUND, 4, 25, new int[]{0, 0, 0, 2, 1, 0}),

    AMERICAN_COOT("American Coot", 29, 5, 0b001, Nest.PLATFORM, 3, 61, new int[]{0, 0, 0, 1, 0, 1}),
    BARN_SWALLOW("Barn Swallow", 29, 3, 0b011, Nest.ANY, 1, 38, new int[]{0, 0, 0, 0, 1, 0}),
    HOUSE_FINCH("House Finch", 29, 6, 0b111, Nest.BOWL, 3, 25, new int[]{1, 0, 0, 1, 0, 0}),
    PURPLE_MARTIN("Purple Martin", 29, 3, 0b011, Nest.CAVITY, 2, 46, new int[]{0, 0, 0, 0, 1, 0}),
    RING_BILLED_GULL("Ring-Billed Gull", 29, 2, 0b001, Nest.GROUND, 4, 122, new int[]{0, 0, 0, 0, 0, 2}),
    TREE_SWALLOW("Tree Swallow", 29, 4, 0b001, Nest.CAVITY, 3, 38, new int[]{1, 0, 0, 0, 1, 0}),
    VIOLET_GREEN_SWALLOW("Violet-Green Swallow", 29, 3, 0b111, Nest.CAVITY, 3, 36, new int[]{0, 0, 0, 0, 2, 0}),

    CEDAR_WAXWING("Cedar Waxwing", 30, 3, 0b110, Nest.BOWL, 3, 25, new int[]{2, 0, 0, 0, 0, 0}),
    DARK_EYED_JUNCO("Dark-Eyed Junco", 30, 3, 0b110, Nest.GROUND, 3, 23, new int[]{0, 0, 0, 1, 1, 0}),
    PINE_SISKIN("Pine Siskin", 30, 2, 0b100, Nest.BOWL, 3, 23, new int[]{0, 0, 0, 2, 0, 0}),
    PYGMY_NUTHATCH("Pygmy Nuthatch", 30, 4, 0b100, Nest.CAVITY, 2, 20, new int[]{0, 0, 0, 1, 1, 0}),

    AMERICAN_WHITE_PELICAN("American White Pelican", 31, 1, 0b001, Nest.GROUND, 5, 274, new int[]{0, 2, 0, 0, 0, 0}),
    DOUBLE_CRESTED_CORMORANT("Double-Crested Cormorant", 31, 3, 0b001, Nest.PLATFORM, 3, 132, new int[]{0, 1, 0, 0, 0, 1}),
    CANADA_GOOSE("Canada Goose", 31, 3, 0b011, Nest.GROUND, 3, 132, new int[]{0, 0, 0, 2, 0, 0}),
    SANDHILL_CRANE("Sandhill Crane", 31, 1, 0b011, Nest.GROUND, 5, 196, new int[]{0, 0, 0, 2, 0, 1}),

    BARRED_OWL("Barred Owl", 32, 2, 0b100, Nest.CAVITY, 3, 107, new int[]{0, 0, 1, 0, 0, 0}),
    COOPER_S_HAWK("Cooper's Hawk", 32, 2, 0b100, Nest.PLATFORM, 3, 79, new int[]{0, 0, 1, 0, 0, 0}),
    NORTHERN_HARRIER("Northern Harrier", 32, 2, 0b011, Nest.PLATFORM, 3, 109, new int[]{0, 0, 1, 0, 0, 0}),
    RED_SHOULDERED_HAWK("Red-Shouldered Hawk", 32, 2, 0b100, Nest.PLATFORM, 3, 102, new int[]{0, 0, 1, 0, 0, 0}),
    RED_TAILED_HAWK("Red-Tailed Hawk", 32, 2, 0b111, Nest.PLATFORM, 5, 124, new int[]{0, 0, 2, 0, 0, 0}),
    SWAINSON_S_HAWK("Swainson's Hawk", 32, 2, 0b010, Nest.PLATFORM, 5, 130, new int[]{0, 0, 1, 0, 1, 0}),
    GOLDEN_EAGLE("Golden Eagle", 32, 1, 0b011, Nest.PLATFORM, 8, 201, new int[]{0, 0, 3, 0, 0, 0}),
    GREAT_HORNED_OWL("Great Horned Owl", 32, 2, 0b100, Nest.PLATFORM, 8, 112, new int[]{0, 0, 3, 0, 0, 0}),
    PEREGRINE_FALCON("Peregrine Falcon", 32, 2, 0b011, Nest.PLATFORM, 5, 104, new int[]{0, 0, 2, 0, 0, 0}),
    GREATER_ROADRUNNER("Greater Roadrunner", 32, 2, 0b010, Nest.PLATFORM, 7, 56, new int[]{0, 0, 1, 0, 1, 1}),

    ANHINGA("Anhinga", 33, 2, 0b001, Nest.PLATFORM, 6, 114, new int[]{0, 2, 0, 0, 0, 0}),
    BLACK_SKIMMER("Black Skimmer", 33, 2, 0b001, Nest.GROUND, 6, 112, new int[]{0, 2, 0, 0, 0, 0}),
    COMMON_MERGANSER("Common Merganser", 33, 4, 0b001, Nest.CAVITY, 5, 86, new int[]{0, 1, 0, 0, 0, 1}),
    WHITE_FACED_IBIS("White-Faced Ibis", 33, 2, 0b001, Nest.PLATFORM, 8, 91, new int[]{0, 1, 0, 0, 2, 0}),
    WHITE_CROWNED_SPARROW("White-Crowned Sparrow", 33, 5, 0b111, Nest.GROUND, 2, 25, new int[]{0, 0, 0, 1, 1, 0}),

    AMERICAN_KESTREL("American Kestrel", 34, 3, 0b010, Nest.CAVITY, 5, 56, new int[]{0, 0, 1, 0, 1, 0}),
    BARN_OWL("Barn Owl", 34, 4, 0b111, Nest.CAVITY, 5, 107, new int[]{0, 0, 2, 0, 0, 0}),
    BURROWING_OWL("Burrowing Owl", 34, 4, 0b010, Nest.ANY, 5, 53, new int[]{0, 0, 1, 0, 1, 0}),
    FERRUGINOUS_HAWK("Ferruginous Hawk", 34, 2, 0b010, Nest.PLATFORM, 6, 142, new int[]{0, 0, 2, 0, 0, 0}),
    MISSISSIPPI_KITE("Mississippi Kite", 34, 2, 0b100, Nest.PLATFORM, 4, 110, new int[]{0, 0, 1, 0, 0, 0}),

    BEWICK_S_WREN("Bewick's Wren", 35, 3, 0b111, Nest.CAVITY, 4, 18, new int[]{0, 0, 0, 1, 2, 0}),
    BLUE_GROSBEAK("Blue Grosbeak", 35, 3, 0b111, Nest.BOWL, 4, 28, new int[]{0, 0, 0, 2, 1, 0}),
    CHIMNEY_SWIFT("Chimney Swift", 35, 2, 0b111, Nest.ANY, 3, 36, new int[]{0, 0, 0, 0, 2, 0}),
    COMMON_NIGHTHAWK("Common Nighthawk", 35, 2, 0b111, Nest.GROUND, 3, 56, new int[]{0, 0, 0, 0, 2, 0}),
    LINCOLN_S_SPARROW("Lincoln's Sparrow", 35, 2, 0b111, Nest.GROUND, 3, 20, new int[]{0, 0, 0, 1, 1, 0}),
    YELLOW_BREASTED_CHAT("Yellow-Breasted Chat", 35, 3, 0b111, Nest.BOWL, 5, 25, new int[]{2, 0, 0, 0, 1, 0}),

    ATLANTIC_PUFFIN("Atlantic Puffin", 38, 1, 0b001, Nest.ANY, 8, 53, new int[]{0, 3, 0, 0, 0, 0}),
    CALIFORNIA_CONDOR("California Condor", 38, 1, 0b111, Nest.GROUND, 1, 277, new int[]{0, 0, 0, 0, 0, 0}),
    CASSIN_S_FINCH("Cassin's Finch", 38, 3, 0b100, Nest.BOWL, 4, 30, new int[]{1, 0, 0, 1, 0, 0}),
    CERULEAN_WARBLER("Cerulean Warbler", 38, 2, 0b100, Nest.BOWL, 4, 20, new int[]{0, 0, 0, 1, 1, 0}),
    CHESTNUT_COLLARED_LONGSPUR("Chestnut-Collared Longspur", 38, 4, 0b010, Nest.GROUND, 5, 25, new int[]{0, 0, 0, 2, 1, 0}),
    KING_RAIL("King Rail", 38, 6, 0b001, Nest.PLATFORM, 4, 51, new int[]{0, 1, 0, 0, 1, 1}),
    PAINTED_BUNTING("Painted Bunting", 38, 4, 0b010, Nest.BOWL, 5, 23, new int[]{0, 0, 0, 2, 1, 0}),
    RED_COCKADED_WOODPECKER("Red-Cockaded Woodpecker", 38, 2, 0b100, Nest.CAVITY, 4, 36, new int[]{1, 0, 0, 0, 1, 0}),
    ROSEATE_SPOONBILL("Roseate Spoonbill", 38, 2, 0b001, Nest.PLATFORM, 6, 127, new int[]{0, 1, 0, 1, 1, 0}),
    SPOTTED_OWL("Spotted Owl", 38, 1, 0b100, Nest.CAVITY, 5, 102, new int[]{0, 0, 2, 0, 0, 0}),
    SPRAGUE_S_PIPIT("Sprague's Pipit", 38, 3, 0b010, Nest.GROUND, 3, 25, new int[]{0, 0, 0, 1, 1, 0}),
    WHOOPING_CRANE("Whooping Crane", 38, 1, 0b001, Nest.GROUND, 6, 221, new int[]{0, 0, 0, 0, 0, 3}),
    WOOD_STORK("Wood Stork", 38, 2, 0b001, Nest.PLATFORM, 6, 155, new int[]{0, 1, 1, 0, 0, 1}),

    EASTERN_BLUEBIRD("Eastern Bluebird", 39, 5, 0b010, Nest.CAVITY, 4, 33, new int[]{1, 0, 0, 0, 1, 0}),
    HOUSE_WREN("House Wren", 39, 5, 0b110, Nest.CAVITY, 1, 15, new int[]{0, 0, 0, 0, 1, 0}),
    MOUNTAIN_BLUEBIRD("Mountain Bluebird", 39, 5, 0b010, Nest.CAVITY, 4, 36, new int[]{1, 0, 0, 0, 1, 0}),
    GREAT_BLUE_HERON("Great Blue Heron", 39, 2, 0b001, Nest.PLATFORM, 5, 183, new int[]{0, 1, 0, 0, 1, 0}),
    GREAT_EGRET("Great Egret", 39, 3, 0b001, Nest.PLATFORM, 7, 130, new int[]{0, 2, 1, 0, 0, 0});
    
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

    public boolean canInhabit(int habitat) { // 0 = forest, 1 = prairie, 2 = wetlands
        return (habitats & (int)(Math.pow(2, 2 - habitat))) > 0;
    }
}
