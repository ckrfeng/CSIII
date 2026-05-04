package PowerGrid;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class GermanyEdgeInitializer {

    private GermanyEdgeInitializer() {}

    public static void initialize(Game game) {
        List<City> cities = game.getCities();
        Map<String, City> byNorm = new HashMap<>();
        for (City c : cities) {
            byNorm.put(normalize(c.getName()), c);
        }

        String[][] pairs = {
                { "Flensburg", "Kiel", "4" },
                { "Kiel", "Lubeck", "4" },
                { "Kiel", "Hamburg", "8" },
                { "Hamburg", "Lubeck", "6" },
                { "Cuxhaven", "Hamburg", "11" },
                { "Hamburg", "Hannover", "17" },
                { "Bremen", "Hannover", "10" },
                { "Hamburg", "Bremen", "11" },
                { "Cuxhaven", "Bremen", "8" },
                { "Wilhelmshaven", "Bremen", "11" },
                { "Wilhelmshaven", "Osnabruck", "14" },
                { "Bremen", "Osnabruck", "11" },
                { "Hannover", "Osnabruck", "16" },
                { "Osnabruck", "Munster", "7" },
                { "Osnabruck", "Kassel", "20" },
                { "Hannover", "Kassel", "15" },
                { "Duisburg", "Essen", "0" },
                { "Munster", "Essen", "6" },
                { "Munster", "Dortmund", "2" },
                { "Essen", "Dortmund", "4" },
                { "Dortmund", "Kassel", "18" },
                { "Essen", "Dusseldorf", "2" },
                { "Aachen", "Dusseldorf", "9" },
                { "Koln", "Dusseldorf", "4" },
                { "Aachen", "Koln", "7" },
                { "Dortmund", "Koln", "10" },
                { "Trier", "Aachen", "19" },
                { "Trier", "Koln", "20" },
                { "Trier", "Saarbrucken", "11" },
                { "Koln", "Wiesbaden", "21" },
                { "Trier", "Wiesbaden", "18" },
                { "Saarbrucken", "Wiesbaden", "10" },
                { "Frankfurt-m", "Dortmund", "20" },
                { "Wiesbaden", "Frankfurt-m", "0" },
                { "Mannheim", "Wiesbaden", "11" },
                { "Saarbrucken", "Mannheim", "11" },
                { "Kassel", "Frankfurt-m", "13" },
                { "Stuttgart", "Mannheim", "6" },
                { "Saarbrucken", "Stuttgart", "17" },
                { "Freiburg", "Stuttgart", "16" },
                { "Konstanz", "Freiburg", "14" },
                { "Konstanz", "Stuttgart", "16" },
                { "Augsburg", "Konstanz", "17" },
                { "Augsburg", "Stuttgart", "15" },
                { "Wurzburg", "Frankfurt-m", "13" },
                { "Mannheim", "Wurzburg", "10" },
                { "Stuttgart", "Wurzburg", "12" },
                { "Fulda", "Kassel", "8" },
                { "Fulda", "Frankfurt-m", "8" },
                { "Fulda", "Wurzburg", "11" },
                { "Wurzburg", "Augsburg", "19" },
                { "Munchen", "Augsburg", "6" },
                { "Passau", "Munchen", "14" },
                { "Regensburg", "Passau", "12" },
                { "Regensburg", "Munchen", "10" },
                { "Augsburg", "Regensburg", "13" },
                { "Nurnberg", "Augsburg", "18" },
                { "Nurnberg", "Regensburg", "12" },
                { "Wurzburg", "Nurnberg", "8" },
                { "Erfurt", "Nurnberg", "21" },
                { "Erfurt", "Fulda", "13" },
                { "Erfurt", "Kassel", "15" },
                { "Hannover", "Erfurt", "19" },
                { "Dresden", "Erfurt", "19" },
                { "Leipzig", "Dresden", "13" },
                { "Leipzig", "Halle", "0" },
                { "Erfurt", "Halle", "6" },
                { "Frankfurt-O", "Dresden", "16" },
                { "Frankfurt-O", "Leipzig", "21" },
                { "Halle", "Berlin", "17" },
                { "Frankfurt-O", "Berlin", "6" },
                { "Magdeburg", "Berlin", "10" },
                { "Halle", "Magdeburg", "11" },
                { "Hannover", "Magdeburg", "15" },
                { "Magdeburg", "Schwerin", "16" },
                { "Hannover", "Schwerin", "19" },
                { "Hamburg", "Schwerin", "8" },
                { "Lubeck", "Schwerin", "6" },
                { "Rostock", "Schwerin", "6" },
                { "Torgelow", "Berlin", "15" },
                { "Torgelow", "Schwerin", "19" },
                { "Rostock", "Torgelow", "19" },
                { "Schwerin", "Berlin", "18" },
        };

        for (String[] row : pairs) {
            City a = city(byNorm, row[0]);
            City b = city(byNorm, row[1]);
            int w = Integer.parseInt(row[2]);
            if (a != null && b != null) {
                game.connect(a, b, w);
            }
        }
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    private static City city(Map<String, City> byNorm, String name) {
        City c = byNorm.get(normalize(name));
        if (c != null) return c;
        String n = normalize(name);
        if ("frankfurt-d".equals(n)) return byNorm.get(normalize("Frankfurt-O"));
        if ("mannhelm".equals(n)) return byNorm.get(normalize("Mannheim"));
        return null;
    }
}
