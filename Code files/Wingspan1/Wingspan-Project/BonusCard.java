public enum BonusCard {
    PHOTOGRAPHER("Photographer"),
    ANATOMIST("Anatomist"),
    CARTOGRAPHER("Cartographer"),
    HISTORIAN("Historian"),
    WILDLIFE_GARDENER("Wildlife Gardener"),
    PLATFORM_BUILDER("Platform Builder"),
    ENCLOSURE_BUILDER("Enclosure Builder"),
    NEST_BOX_BUILDER("Nest Box Builder"),
    BREEDING_MANAGER("Breeding Manager"),
    OOLOGIST("Oologist"),
    FORESTER("Forester"),
    PRAIRIE_MANAGER("Prairie Manager"),
    WETLAND_SCIENTIST("Wetland Scientist"),
    ECOLOGIST("Ecologist"),
    BIRD_COUNTER("Bird Counter"),
    FALCONER("Falconer"),
    LARGE_BIRD_SPECIALIST("Large Bird Specialist"),
    PASSERINE_SPECIALIST("Passerine Specialist"),
    BACKYARD_BIRDER("Backyard Birder"),
    VISIONARY_LEADER("Visionary Leader");

    public final String name;

    BonusCard(String name) {
        this.name = name;
    }
}