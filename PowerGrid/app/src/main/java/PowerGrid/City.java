package PowerGrid;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class City {
    private String name;
    private boolean isInteractable;
    private double x, y;
    private StackPane visualNode; // The UI element on the map

    public City(String name, double x, double y, boolean isInteractable) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.isInteractable = isInteractable;
        createVisual();
    }

    private void createVisual() {
        visualNode = new StackPane();
        
        // Base Circle
        Circle circle = new Circle(40, Color.RED);
        
        // Yellow border if interactable
        if (isInteractable) {
            circle.setStroke(Color.YELLOW);
            circle.setStrokeWidth(3);
            visualNode.setOnMouseClicked(e -> System.out.println("Clicked City: " + name));
            visualNode.setStyle("-fx-cursor: hand;");
        } else {
            circle.setOpacity(0.5); // Dim if not interactable
            visualNode.setMouseTransparent(true); // Cannot click
        }

        Text label = new Text(name);
        label.setFill(Color.WHITE);
        label.setTranslateY(20); // Put text below circle

        visualNode.getChildren().addAll(circle, label);
        visualNode.setLayoutX(x);
        visualNode.setLayoutY(y);
    }

    public StackPane getVisualNode() { return visualNode; }
}
