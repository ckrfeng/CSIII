import java.awt.*;

public class ScalableGUI {
    private static final int BASE_WIDTH = 1920;
    private static final int BASE_HEIGHT = 1080;
    
    public static int scaleWidth(int baseWidth, int currentWidth) {
        return (int) (baseWidth * ((double) currentWidth / BASE_WIDTH));
    }

    public static int scaleHeight(int baseHeight, int currentHeight) {
        return (int) (baseHeight * ((double) currentHeight / BASE_HEIGHT));
    }

    public static Font scaleFont(String name, int style, int baseSize, int currentWidth, int currentHeight) {
        double scale = Math.min((double) currentWidth / BASE_WIDTH, (double) currentHeight / BASE_HEIGHT);
        int scaledSize = (int) (baseSize * scale);
        return new Font(name, style, Math.max(scaledSize, 12));
    }

    public static int percentX(double percent, int width) {
        return (int) (width * percent / 100.0);
    }

    public static int percentY(double percent, int height) {
        return (int) (height * percent / 100.0);
    }

    public static int percentWidth(double percent, int width) {
        return (int) (width * percent / 100.0);
    }

    public static int percentHeight(double percent, int height) {
        return (int) (height * percent / 100.0);
    }
}