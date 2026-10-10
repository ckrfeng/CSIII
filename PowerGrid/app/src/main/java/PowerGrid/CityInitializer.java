package PowerGrid;

import java.util.ArrayList;
import java.util.List;

public final class CityInitializer {
    private CityInitializer() {}

    public static List<City> createGermanyCities() {
        List<City> cities = new ArrayList<>();

        // Turquoise
        cities.add(new City("Flensburg", 660, 91));
        cities.add(new City("Kiel", 747, 232));
        cities.add(new City("Hamburg", 738, 441));
        cities.add(new City("Cuxhaven", 545, 358));
        cities.add(new City("Wilhelmshaven", 434, 441));
        cities.add(new City("Bremen", 576, 574));
        cities.add(new City("Hannover", 744, 780));

        // Red
        cities.add(new City("Osnabruck", 449, 748));
        cities.add(new City("Munster", 357, 880));
        cities.add(new City("Kassel", 662, 1067));
        cities.add(new City("Dortmund", 384, 1035));
        cities.add(new City("Essen", 237, 972));
        cities.add(new City("Duisburg", 132, 935));
        cities.add(new City("Dusseldorf", 158, 1096));

        // Blue
        cities.add(new City("Aachen", 110, 1249));
        cities.add(new City("Koln", 273, 1200));
        cities.add(new City("Trier", 177, 1499));
        cities.add(new City("Saarbrucken", 323, 1657));
        cities.add(new City("Mannheim", 542, 1610));
        cities.add(new City("Wiesbaden", 459, 1412));
        cities.add(new City("Frankfurt-m", 561, 1363));

        // Purple
        cities.add(new City("Stuttgart", 595, 1781));
        cities.add(new City("Freiburg", 397, 1958));
        cities.add(new City("Konstanz", 589, 2050));
        cities.add(new City("Augsburg", 868, 1843));
        cities.add(new City("Munchen", 1034, 1958));
        cities.add(new City("Passau", 1337, 1824));
        cities.add(new City("Regensburg", 1084, 1705));

        // Brown
        cities.add(new City("Lubeck", 872, 327));
        cities.add(new City("Rostock", 1083, 275));
        cities.add(new City("Schwerin", 980, 457));
        cities.add(new City("Magdeburg", 1045, 801));
        cities.add(new City("Berlin", 1289, 734));
        cities.add(new City("Frankfurt-O", 1463, 783));
        cities.add(new City("Torgelow", 1398, 440));

        // Yellow
        cities.add(new City("Halle", 1072, 1014));
        cities.add(new City("Leipzig", 1162, 1074));
        cities.add(new City("Erfurt", 953, 1152));
        cities.add(new City("Fulda", 741, 1273));
        cities.add(new City("Nurnberg", 954, 1572));
        cities.add(new City("Wurzburg", 769, 1486));
        cities.add(new City("Dresden", 1395, 1163));

        return cities;
    }

    public static RegionColor regionForIndex(int index) {
        if (index < 7) return RegionColor.TURQUOISE;
        if (index < 14) return RegionColor.RED;
        if (index < 21) return RegionColor.BLUE;
        if (index < 28) return RegionColor.PURPLE;
        if (index < 35) return RegionColor.BROWN;
        return RegionColor.YELLOW;
    }
}

