package PowerGrid;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class City {
    private String name;
    private boolean isInteractable;
    private double x, y;
    private Group visualNode; // The UI element on the map

    public City(String name, double x, double y, boolean isInteractable) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.isInteractable = isInteractable;
        createVisual();
    }

    private void createVisual() {
        visualNode = new Group();

        // Circle centered at (0,0) so layoutX/layoutY place the center at (x,y)
        Circle circle = new Circle(40, Color.RED);

        if (isInteractable) {
            circle.setStroke(Color.YELLOW);
            circle.setStrokeWidth(3);
            visualNode.setOnMouseClicked(e -> System.out.println("Clicked City: " + name));
            visualNode.setStyle("-fx-cursor: hand;");
        } else {
            circle.setOpacity(0.5);
            visualNode.setMouseTransparent(true);
        }

        Text label = new Text(name);
        label.setFill(Color.WHITE);
        label.setTranslateY(20); // Put text below circle center

        visualNode.getChildren().addAll(circle, label);
        visualNode.setLayoutX(x);
        visualNode.setLayoutY(y);
    }

    public Group getVisualNode() { return visualNode; }
}
