package PowerGrid;

public class Edge {
    private final City cityA;
    private final City cityB;
    private final int weight;

    public Edge(City cityA, City cityB, int weight) {
        this.cityA = cityA;
        this.cityB = cityB;
        this.weight = weight;
    }

    public City getCityA() {
        return cityA;
    }

    public City getCityB() {
        return cityB;
    }

    public City getOther(City city) {
        if (city == cityA) {
            return cityB;
        } else if (city == cityB) {
            return cityA;
        } else {
            return null;
        }
    }

    public int getWeight() {
        return weight;
    }
}
