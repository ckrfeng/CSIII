package PowerGrid;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class MenuPanel extends StackPane {
    private Font customFont;
    private final java.util.function.IntConsumer onStartGame;
    private final VBox buttonColumn;
    
    // Original image dimensions
    private final double ORIG_W = 651.0;
    private final double ORIG_H = 353.0;

    public MenuPanel(java.util.function.IntConsumer onStartGame) {
        this.onStartGame = onStartGame;

        // 1. BACKGROUND IMAGE (Stretches to fill window)
        String imagePath = getClass().getResource("/menu.jpg").toExternalForm();
        ImageView backgroundView = new ImageView(new Image(imagePath));
        
        // Bind image size to the MenuPanel size (which fills the window)
        backgroundView.fitWidthProperty().bind(this.widthProperty());
        backgroundView.fitHeightProperty().bind(this.heightProperty());
        backgroundView.setSmooth(true);

        // 2. SCALING BUTTON LAYER
        Pane scalingLayer = new Pane();
        
        try {
            customFont = Font.loadFont(getClass().getResourceAsStream("/universcondensed_medium.ttf"), 16);
        } catch (Exception e) {
            customFont = Font.font("Arial", 16);
        }

        buttonColumn = new VBox(10);
        buttonColumn.setAlignment(Pos.CENTER);

        Button btn3 = createStyledButton("3 PLAYERS");
        Button btn4 = createStyledButton("4 PLAYERS");
        Button btn5 = createStyledButton("5 PLAYERS");
        Button btnTutorial = createStyledButton("TUTORIAL");

        btn3.setOnAction(e -> this.onStartGame.accept(3));
        btn4.setOnAction(e -> this.onStartGame.accept(4));
        btn5.setOnAction(e -> this.onStartGame.accept(5));
        btnTutorial.setOnAction(e -> openRulesPDF());

        buttonColumn.getChildren().addAll(btn3, btn4, btn5, btnTutorial);
        scalingLayer.getChildren().add(buttonColumn);

        // 3. RESPONSIVE POSITIONING & SCALING
        // This listener recalculates button position and size whenever the window resizes
        this.widthProperty().addListener((obs, oldVal, newVal) -> updateLayout());
        this.heightProperty().addListener((obs, oldVal, newVal) -> updateLayout());

        this.getChildren().addAll(backgroundView, scalingLayer);
    }

    private void updateLayout() {
        double currentW = getWidth();
        double currentH = getHeight();
        if (currentW == 0 || currentH == 0) return;

        // Calculate scale ratios
        double scaleX = currentW / ORIG_W;
        double scaleY = currentH / ORIG_H;

        // 1. Position the column relative to the image (500, 200 center point)
        // We subtract half the button width so 500 is the CENTER
        double relX = 500 * scaleX;
        double relY = 200 * scaleY;
        
        buttonColumn.setLayoutX(relX - (buttonColumn.getWidth() / 2));
        buttonColumn.setLayoutY(relY);

        // 2. Scale the buttons themselves
        // We use the smaller of the two scales to prevent buttons from getting weirdly stretched
        double fontScale = Math.min(scaleX, scaleY);
        buttonColumn.setScaleX(fontScale);
        buttonColumn.setScaleY(fontScale);
    }

    private void openRulesPDF() {
        try {
            InputStream is = getClass().getResourceAsStream("/Power Grid Rules.pdf");
            if (is == null) return;
            File tempFile = File.createTempFile("PowerGridRules", ".pdf");
            tempFile.deleteOnExit();
            Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private Button createStyledButton(String text) {
        Button b = new Button(text);
        if (customFont != null) b.setFont(customFont);
        b.setPrefWidth(120);
        b.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #777777; -fx-text-fill: white; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-cursor: hand;"));
        return b;
    }
}
