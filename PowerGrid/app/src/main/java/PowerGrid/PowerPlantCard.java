package PowerGrid;

import java.util.HashMap;
import java.util.Map;

public enum PowerPlantCard {
    P03(3, ResourceType.OIL, null, 2, 1),
    P04(4, ResourceType.COAL, null, 2, 1),
    P05(5, ResourceType.COAL, ResourceType.OIL, 2, 1),
    P06(6, ResourceType.GARBAGE, null, 1, 1),
    P07(7, ResourceType.OIL, null, 3, 2),
    P08(8, ResourceType.COAL, null, 3, 2),
    P09(9, ResourceType.OIL, null, 1, 1),
    P10(10, ResourceType.COAL, null, 2, 2),
    P11(11, ResourceType.URANIUM, null, 1, 2),
    P12(12, ResourceType.COAL, ResourceType.OIL, 2, 2),
    P13(13, null, null, 0, 1),
    P14(14, ResourceType.GARBAGE, null, 2, 2),
    P15(15, ResourceType.COAL, null, 2, 3),
    P16(16, ResourceType.OIL, null, 2, 3),
    P17(17, ResourceType.URANIUM, null, 1, 2),
    P18(18, null, null, 0, 2),
    P19(19, ResourceType.GARBAGE, null, 2, 3),
    P20(20, ResourceType.COAL, null, 3, 5),
    P21(21, ResourceType.COAL, ResourceType.OIL, 2, 4),
    P22(22, null, null, 0, 2),
    P23(23, ResourceType.URANIUM, null, 1, 3),
    P24(24, ResourceType.GARBAGE, null, 2, 4),
    P25(25, ResourceType.COAL, null, 2, 5),
    P26(26, ResourceType.OIL, null, 2, 5),
    P27(27, null, null, 0, 3),
    P28(28, ResourceType.URANIUM, null, 1, 4),
    P29(29, ResourceType.COAL, ResourceType.OIL, 1, 4),
    P30(30, ResourceType.GARBAGE, null, 3, 6),
    P31(31, ResourceType.COAL, null, 3, 6),
    P32(32, ResourceType.OIL, null, 3, 6),
    P33(33, null, null, 0, 4),
    P34(34, ResourceType.URANIUM, null, 1, 5),
    P35(35, ResourceType.OIL, null, 1, 5),
    P36(36, ResourceType.COAL, null, 3, 7),
    P37(37, null, null, 0, 4),
    P38(38, ResourceType.GARBAGE, null, 3, 7),
    P39(39, ResourceType.URANIUM, null, 1, 6),
    P40(40, ResourceType.OIL, null, 2, 6),
    P42(42, ResourceType.COAL, null, 2, 6),
    P44(44, null, null, 0, 5),
    P46(46, ResourceType.COAL, ResourceType.OIL, 3, 7),
    P50(50, null, null, 0, 6);

    private static final Map<Integer, PowerPlantCard> BY_NUMBER = new HashMap<>();
    static {
        for (PowerPlantCard c : values()) {
            BY_NUMBER.put(c.number, c);
        }
    }

    private final int number;
    private final ResourceType primary;
    private final ResourceType secondary;
    private final int materialRequired;
    private final int citiesPowered;

    PowerPlantCard(int number, ResourceType primary, ResourceType secondary, int materialRequired, int citiesPowered) {
        this.number = number;
        this.primary = primary;
        this.secondary = secondary;
        this.materialRequired = materialRequired;
        this.citiesPowered = citiesPowered;
    }

    public int getCost() { return number; }
    public int getNumber() { return number; }
    public ResourceType getPrimary() { return primary; }
    public ResourceType getSecondary() { return secondary; }
    public int getMaterialRequired() { return materialRequired; }
    public int getCitiesPowered() { return citiesPowered; }
    public int getProductionCapacity() { return citiesPowered; }

    public boolean isEcological() { return primary == null; }
    public boolean isHybrid() { return primary != null && secondary != null; }

    public PowerPlant toPowerPlant() {
        return new PowerPlant(number, primary, secondary, materialRequired, citiesPowered);
    }

    public static PowerPlantCard byNumber(int number) {
        return BY_NUMBER.get(number);
    }
}
