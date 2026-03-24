package PowerGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

public class City {
    private String name; //Name of the city
    private boolean isInteractable; //Determines whether or not a player can click a city to build in it during a turn
    private double x, y; //Location of the City
    private Group visualNode; // VisualNode is the UI Node on the pannable map
    private Circle ring;

    // stores the players that are occupying the city, integers 1-5 where 1= player 1, 2=player 2, etc
    private final List<Integer> occupyingPlayers = new ArrayList<>();

    public City(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        isInteractable = true;
        createVisual();
    }

    private void createVisual() {
        visualNode = new Group();

        // Circle centered at (0,0) so layoutX/layoutY place the center at (x,y)
        Circle circle = new Circle(45, Color.TRANSPARENT);
        this.ring = circle;

        if (isInteractable) {
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(3);
            visualNode.setOnMouseClicked(e -> System.out.println("Clicked City: " + name));
            visualNode.setStyle("-fx-cursor: hand;");
        } else {
            circle.setOpacity(0.5);
            visualNode.setMouseTransparent(true);
        }

        visualNode.getChildren().add(circle);
        visualNode.setLayoutX(x);
        visualNode.setLayoutY(y);
    }

    public Group getVisualNode() {
        return visualNode;
    }

    public String getName() {
        return name;
    }

    public void addOccupyingPlayer(int playerId) {
        if (playerId < 1 || playerId > 5) {
            throw new IllegalArgumentException("playerId must be between 1 and 5");
        }
        if (!occupyingPlayers.contains(playerId)) {
            occupyingPlayers.add(playerId);
        }
    }

    public List<Integer> getOccupyingPlayers() {
        return Collections.unmodifiableList(occupyingPlayers);
    }

    public boolean hasAnyPlayer() {
        return !occupyingPlayers.isEmpty();
    }

    public boolean isInteractable() {
        return isInteractable;
    }

    public void setInteractable(boolean interactable) {
        this.isInteractable = interactable;
        visualNode.setMouseTransparent(!interactable);
        visualNode.setStyle(interactable ? "-fx-cursor: hand;" : "");
    }

    public void updateOccupancyStroke(List<Color> playerColors) {
        if (ring == null) return;
        if (playerColors == null || playerColors.isEmpty()) {
            ring.setStroke(Color.WHITE);
            ring.setStrokeWidth(3);
            return;
        }

        ring.setStrokeWidth(13);
        if (playerColors.size() == 1) {
            ring.setStroke(playerColors.get(0));
            return;
        }

        // Two+ players: split/gradient effect
        Color c1 = playerColors.get(0);
        Color c2 = playerColors.get(1);
        ring.setStroke(new LinearGradient(
                -1, 0, 1, 0, true, null,
                new Stop(0, c1),
                new Stop(0.5, c1),
                new Stop(0.5, c2),
                new Stop(1, c2)
        ));
    }
}
