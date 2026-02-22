//./gradlew run --no-configuration-cache

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
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

public class App extends Application {
    private double mouseAnchorX, mouseAnchorY;
    private final Affine transform = new Affine();
    private ImageView imageView;
    private Pane mapContainer;

    @Override
    public void start(Stage stage) {
        mapContainer = new Pane();
        mapContainer.setStyle("-fx-background-color: #111;");
        HBox.setHgrow(mapContainer, Priority.ALWAYS);

        String path = getClass().getResource("/map.png").toExternalForm();
        Image img = new Image(path);
        imageView = new ImageView(img);
        
        // Boost performance for panning
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);
        
        Group mapGroup = new Group(imageView);
        mapGroup.getTransforms().add(transform);
        mapContainer.getChildren().add(mapGroup);

        Game game = new Game(img.getWidth(), img.getHeight());
        
        // Add all cities to the same group as the image
        for (City city : game.getCities()) {
            mapGroup.getChildren().add(city.getVisualNode());
        }

        Rectangle clip = new Rectangle();
        mapContainer.setClip(clip);
        mapContainer.widthProperty().addListener((o, old, val) -> clip.setWidth(val.doubleValue()));
        mapContainer.heightProperty().addListener((o, old, val) -> clip.setHeight(val.doubleValue()));

        //home button
        Button homeBtn = new Button("⌂");
        homeBtn.setShape(new Circle(20));
        homeBtn.setMinSize(40, 40);
        homeBtn.setMaxSize(40, 40);
        // Grey color styling
        homeBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;");
        homeBtn.setLayoutX(15);
        homeBtn.setLayoutY(15);
        homeBtn.setOnAction(e -> resetToCenter());
        
        // Hover effect for better UI feel
        homeBtn.setOnMouseEntered(e -> homeBtn.setStyle("-fx-background-color: #777777; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;"));
        homeBtn.setOnMouseExited(e -> homeBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;"));

        mapContainer.getChildren().add(homeBtn);

        VBox sidebar = new VBox();
        sidebar.setMinWidth(250);
        sidebar.setStyle("-fx-background-color: #222; -fx-border-color: black; -fx-border-width: 0 0 0 2;");

        // --- PANNING ---
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

        // --- FIXED: PERFECT ZOOM-TO-CURSOR ---
        mapContainer.addEventFilter(ScrollEvent.ANY, event -> {
            double zoomFactor = (event.getDeltaY() > 0) ? 1.05 : 0.95;
            double scale = transform.getMxx();
            if (scale * zoomFactor < 0.05 || scale * zoomFactor > 20) return;

            try {
                // Correctly find the pivot point in the map's coordinate system
                Point2D pivotInMap = transform.inverseTransform(event.getX(), event.getY());
                
                // Scale around that specific point
                transform.appendScale(zoomFactor, zoomFactor, pivotInMap.getX(), pivotInMap.getY());
            } catch (NonInvertibleTransformException e) {
                // Ignore if math fails
            }
            event.consume();
        });

        stage.setScene(new Scene(new HBox(mapContainer, sidebar), 1200, 800));
        stage.show();
        resetToCenter();
    }

    private void resetToCenter() {
        double containerW = mapContainer.getWidth();
        double containerH = mapContainer.getHeight();
        double imgW = imageView.getImage().getWidth();
        double imgH = imageView.getImage().getHeight();

        transform.setToIdentity();
        transform.appendTranslation((containerW - imgW) / 2, (containerH - imgH) / 2);
    }

    public static void main(String[] args) { launch(args); }
}
