public class Card {
    public enum Type {
        TEMPURA, SASHIMI, DUMPLINGS, MAKI1, MAKI2, MAKI3, SALMON_NIGIRI, SQUID_NIGIRI, EGG_NIGIRI, WASABI, PUDDING, CHOPSTICKS
    }

    public final Type type;
    public final String imagePath;

    public Card(Type type, String imagePath) {
        this.type = type;
        this.imagePath = imagePath;
    }

    public void drawRotated(Graphics2D g, int x, int y, int w, int h, boolean faceUp, int angle) {
        // drawing code unchanged
    }
}
