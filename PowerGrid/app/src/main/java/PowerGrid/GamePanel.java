package PowerGrid;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

public class GamePanel extends Application {
    private double mouseAnchorX, mouseAnchorY;
    private final Affine transform = new Affine();
    private ImageView imageView;
    private Pane mapContainer;
    private StackPane rootStack; // The manager that swaps screens

    @Override
    public void start(Stage stage) {
        //Creates the stackPane that will hold either the Menu or the Game
        rootStack = new StackPane();

        // 2. Load the MenuPanel and tell it to run 'startGame' when a button is clicked
        MenuPanel menu = new MenuPanel(() -> initGameView(stage));
        rootStack.getChildren().add(menu);

        Scene scene = new Scene(rootStack, 1200, 800);
        stage.setTitle("Power Grid");
        stage.setScene(scene);
        stage.show();
    }

    private void initGameView(Stage stage) {
        // 3. This is your original Map & Sidebar logic
        
        mapContainer = new Pane();
        mapContainer.setStyle("-fx-background-color: #111;");
        HBox.setHgrow(mapContainer, Priority.ALWAYS);

        String path = getClass().getResource("/map.png").toExternalForm();
        Image img = new Image(path);
        imageView = new ImageView(img);
        
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);
        
        Group mapGroup = new Group(imageView);
        mapGroup.getTransforms().add(transform);
        mapContainer.getChildren().add(mapGroup);

        Game game = new Game(img.getWidth(), img.getHeight());
        for (City city : game.getCities()) {
            mapGroup.getChildren().add(city.getVisualNode());
        }

        Rectangle clip = new Rectangle();
        mapContainer.setClip(clip);
        mapContainer.widthProperty().addListener((o, old, val) -> clip.setWidth(val.doubleValue()));
        mapContainer.heightProperty().addListener((o, old, val) -> clip.setHeight(val.doubleValue()));

        // Home button
        Button homeBtn = new Button("⌂");
        homeBtn.setShape(new Circle(20));
        homeBtn.setMinSize(40, 40);
        homeBtn.setMaxSize(40, 40);
        homeBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;");
        homeBtn.setLayoutX(15);
        homeBtn.setLayoutY(15);
        homeBtn.setOnAction(e -> resetToCenter());
        homeBtn.setOnMouseEntered(e -> homeBtn.setStyle("-fx-background-color: #777777; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;"));
        homeBtn.setOnMouseExited(e -> homeBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;"));

        mapContainer.getChildren().add(homeBtn);

        VBox sidebar = new VBox();
        sidebar.setMinWidth(500);
        sidebar.setStyle("-fx-background-color: #222; -fx-border-color: black; -fx-border-width: 0 0 0 2;");

        // Pan/Zoom Logic
        mapContainer.setOnMousePressed(event -> {
            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();
        });

        mapContainer.setOnMouseDragged(event -> {
            double dx = event.getX() - mouseAnchorX;
            double dy = event.getY() - mouseAnchorY;
            transform.prependTranslation(dx, dy);
            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();
        });

        mapContainer.addEventFilter(ScrollEvent.ANY, event -> {
            double zoomFactor = (event.getDeltaY() > 0) ? 1.05 : 0.95;
            double scale = transform.getMxx();
            if (scale * zoomFactor < 0.05 || scale * zoomFactor > 20) return;
            try {
                Point2D pivotInMap = transform.inverseTransform(event.getX(), event.getY());
                transform.appendScale(zoomFactor, zoomFactor, pivotInMap.getX(), pivotInMap.getY());
            } catch (NonInvertibleTransformException e) { }
            event.consume();
        });

        // 4. Swap the Menu for the Game Layout
        HBox gameLayout = new HBox(mapContainer, sidebar);
        rootStack.getChildren().clear();
        rootStack.getChildren().add(gameLayout);
        
        // --- EDIT STARTS HERE ---
        // Use Platform.runLater to ensure the Pane has a width/height before centering
        javafx.application.Platform.runLater(() -> {
            resetToCenter();
        });
    }

    private void resetToCenter() {
        if (imageView == null || mapContainer == null) return;
        double containerW = mapContainer.getWidth();
        double containerH = mapContainer.getHeight();
        double imgW = imageView.getImage().getWidth();
        double imgH = imageView.getImage().getHeight();

        transform.setToIdentity();
        transform.appendTranslation((containerW - imgW) / 2, (containerH - imgH) / 2);
    }

    public String getGreeting() {
        return "Welcome to Power Grid!";
    }
    public static void main(String[] args) { launch(args); }
}
