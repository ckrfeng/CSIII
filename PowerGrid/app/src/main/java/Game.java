package PowerGrid;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<City> cities = new ArrayList<>();

    public Game(double imgW, double imgH) {
        // Create 5 cities based on image dimensions
        cities.add(new City("Center City", imgW/2, imgH/2, true));
        cities.add(new City("NW Outpost", 100, 100, true));
        cities.add(new City("NE Port", imgW - 100, 100, false));
        cities.add(new City("SW Mine", 100, imgH - 100, true));
        cities.add(new City("SE Capital", imgW - 100, imgH - 100, false));
    }

    public List<City> getCities() { return cities; }
}

