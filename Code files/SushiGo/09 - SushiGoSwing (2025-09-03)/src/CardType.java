public enum CardType {
    TEMPURA, SASHIMI, DUMPLING,
    MAKI_1, MAKI_2, MAKI_3,
    NIGIRI_EGG, NIGIRI_SALMON, NIGIRI_SQUID,
    WASABI, CHOPSTICKS, PUDDING;

    public String displayName() {
        switch (this) {
            case TEMPURA: return "Tempura";
            case SASHIMI: return "Sashimi";
            case DUMPLING: return "Dumpling";
            case MAKI_1: return "Maki (1)";
            case MAKI_2: return "Maki (2)";
            case MAKI_3: return "Maki (3)";
            case NIGIRI_EGG: return "Egg Nigiri";
            case NIGIRI_SALMON: return "Salmon Nigiri";
            case NIGIRI_SQUID: return "Squid Nigiri";
            case WASABI: return "Wasabi";
            case CHOPSTICKS: return "Chopsticks";
            case PUDDING: return "Pudding";
        }
        return name();
    }
}
