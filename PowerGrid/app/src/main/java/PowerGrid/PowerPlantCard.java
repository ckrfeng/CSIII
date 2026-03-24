package PowerGrid;

public enum PowerPlantCard {
    P03(3, PowerPlantMaterial.OIL, 2, 1),
    P04(4, PowerPlantMaterial.COAL, 2, 1),
    P05(5, PowerPlantMaterial.HYBRID, 2, 1),
    P06(6, PowerPlantMaterial.TRASH, 1, 1),
    P07(7, PowerPlantMaterial.OIL, 3, 2),
    P08(8, PowerPlantMaterial.COAL, 3, 2),
    P09(9, PowerPlantMaterial.OIL, 1, 1),
    P10(10, PowerPlantMaterial.COAL, 2, 2),
    P11(11, PowerPlantMaterial.URANIUM, 1, 2),
    P12(12, PowerPlantMaterial.HYBRID, 2, 2),
    P13(13, PowerPlantMaterial.ECO, 0, 1),
    P14(14, PowerPlantMaterial.TRASH, 2, 2),
    P15(15, PowerPlantMaterial.COAL, 2, 3),
    P16(16, PowerPlantMaterial.OIL, 2, 3),
    P17(17, PowerPlantMaterial.URANIUM, 1, 2),
    P18(18, PowerPlantMaterial.ECO, 0, 2),
    P19(19, PowerPlantMaterial.TRASH, 2, 3),
    P20(20, PowerPlantMaterial.COAL, 3, 5),
    P21(21, PowerPlantMaterial.HYBRID, 2, 4),
    P22(22, PowerPlantMaterial.ECO, 0, 2),
    P23(23, PowerPlantMaterial.URANIUM, 1, 3),
    P24(24, PowerPlantMaterial.TRASH, 2, 4),
    P25(25, PowerPlantMaterial.COAL, 2, 5),
    P26(26, PowerPlantMaterial.OIL, 2, 5),
    P27(27, PowerPlantMaterial.ECO, 0, 3),
    P28(28, PowerPlantMaterial.URANIUM, 1, 4),
    P29(29, PowerPlantMaterial.HYBRID, 1, 4),
    P30(30, PowerPlantMaterial.TRASH, 3, 6),
    P31(31, PowerPlantMaterial.COAL, 3, 6),
    P32(32, PowerPlantMaterial.OIL, 3, 6),
    P33(33, PowerPlantMaterial.ECO, 0, 4),
    P34(34, PowerPlantMaterial.URANIUM, 1, 5),
    P35(35, PowerPlantMaterial.OIL, 1, 5),
    P36(36, PowerPlantMaterial.COAL, 3, 7),
    P37(37, PowerPlantMaterial.ECO, 0, 4),
    P38(38, PowerPlantMaterial.TRASH, 3, 7),
    P39(39, PowerPlantMaterial.URANIUM, 1, 6),
    P40(40, PowerPlantMaterial.OIL, 2, 6),
    P42(42, PowerPlantMaterial.COAL, 2, 6),
    P44(44, PowerPlantMaterial.ECO, 0, 5),
    P46(46, PowerPlantMaterial.HYBRID, 3, 7),
    P50(50, PowerPlantMaterial.ECO, 0, 6);

    private final int cost;
    private final PowerPlantMaterial materialType;
    private final int materialRequired;
    private final int citiesPowered;

    PowerPlantCard(int cost, PowerPlantMaterial materialType, int materialRequired, int citiesPowered) {
        this.cost = cost;
        this.materialType = materialType;
        this.materialRequired = materialRequired;
        this.citiesPowered = citiesPowered;
    }

    public int getCost() { return cost; }
    public PowerPlantMaterial getMaterialType() { return materialType; }
    public int getMaterialRequired() { return materialRequired; }
    public int getCitiesPowered() { return citiesPowered; }
    public int getProductionCapacity() { return citiesPowered; }
}
