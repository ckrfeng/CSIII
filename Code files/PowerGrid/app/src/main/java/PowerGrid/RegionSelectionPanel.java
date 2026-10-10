package PowerGrid;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class RegionSelectionPanel {
    public interface Listener {
        void onSelectionComplete(List<RegionColor> selectedRegions);
    }

    private RegionSelectionPanel() {
    }

    public static StackPane create(List<Player> turnOrder, Listener listener) {
        StackPane panel = new StackPane();
        panel.setStyle("-fx-background-color: black;");

        Image mapImg = new Image(RegionSelectionPanel.class.getResource("/map.png").toExternalForm());
        final double mapW = mapImg.getWidth();
        final double mapH = mapImg.getHeight();
        ImageView map = new ImageView(mapImg);
        map.setFitHeight(760);
        map.setPreserveRatio(true);
        final double displayH = 760;
        final double displayW = mapW * (displayH / mapH);

        Pane overlay = new Pane();
        overlay.setPickOnBounds(true);
        overlay.setMouseTransparent(false);

        Label current = new Label();
        current.setTextFill(Color.WHITE);

        VBox orderBox = new VBox(6);
        orderBox.setPickOnBounds(false);
        orderBox.setMouseTransparent(true);
        orderBox.setAlignment(Pos.TOP_RIGHT);
        StackPane.setAlignment(orderBox, Pos.TOP_RIGHT);
        orderBox.setTranslateX(-12);
        orderBox.setTranslateY(12);

        List<RegionColor> sequence = List.of(
                RegionColor.TURQUOISE,
                RegionColor.BROWN,
                RegionColor.RED,
                RegionColor.BLUE,
                RegionColor.YELLOW,
                RegionColor.PURPLE
        );
        // Centers are in original map image coordinates (1654x2287).
        List<Point2D> points = List.of(
                new Point2D(643, 488),   // Turquoise
                new Point2D(1182, 623),  // Brown
                new Point2D(430, 887),   // Red
                new Point2D(386, 1390),  // Blue
                new Point2D(1052, 1214), // Yellow
                new Point2D(858, 1847)   // Purple
        );
        Map<RegionColor, String> regionButtonColor = new EnumMap<>(RegionColor.class);
        regionButtonColor.put(RegionColor.TURQUOISE, "#1AA6A6");
        regionButtonColor.put(RegionColor.BROWN, "#8D6E63");
        regionButtonColor.put(RegionColor.RED, "#D32F2F");
        regionButtonColor.put(RegionColor.BLUE, "#1976D2");
        regionButtonColor.put(RegionColor.YELLOW, "#FBC02D");
        regionButtonColor.put(RegionColor.PURPLE, "#7B1FA2");

        // Grid layout: turquoise/brown | red/yellow | blue/purple — adjacency is 4-directional only
        Map<RegionColor, Set<RegionColor>> adjacency = new EnumMap<>(RegionColor.class);
        adjacency.put(RegionColor.TURQUOISE, EnumSet.of(RegionColor.BROWN, RegionColor.RED, RegionColor.YELLOW));
        adjacency.put(RegionColor.BROWN,     EnumSet.of(RegionColor.TURQUOISE, RegionColor.YELLOW));
        adjacency.put(RegionColor.RED,       EnumSet.of(RegionColor.TURQUOISE, RegionColor.YELLOW, RegionColor.BLUE));
        adjacency.put(RegionColor.YELLOW,    EnumSet.of(RegionColor.TURQUOISE, RegionColor.BROWN, RegionColor.RED, RegionColor.BLUE, RegionColor.PURPLE));
        adjacency.put(RegionColor.BLUE,      EnumSet.of(RegionColor.RED, RegionColor.YELLOW, RegionColor.PURPLE));
        adjacency.put(RegionColor.PURPLE,    EnumSet.of(RegionColor.YELLOW, RegionColor.BLUE));

        List<RegionColor> selected = new ArrayList<>();
        int[] selecting = {0};

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-weight:bold; -fx-font-size:14;");
        errorLabel.setVisible(false);
        errorLabel.setMouseTransparent(true);

        Runnable redrawOrder = () -> {
            orderBox.getChildren().clear();
            for (int i = 0; i < turnOrder.size(); i++) {
                Player p = turnOrder.get(i);
                Label l = new Label((i + 1) + ". " + p.getDisplayName());
                l.setTextFill(Color.WHITE);
                String border = i == selecting[0] ? "-fx-border-color:white;-fx-border-width:2;" : "";
                l.setStyle("-fx-background-color:" + p.getColorHex() + ";-fx-padding:5;" + border);
                orderBox.getChildren().add(l);
            }
            current.setText("Selecting: " + turnOrder.get(selecting[0]).getDisplayName());
        };

        redrawOrder.run();

        for (int i = 0; i < sequence.size(); i++) {
            RegionColor region = sequence.get(i);
            Button b = new Button("Select Region");
            b.setPrefSize(94, 30);
            b.setStyle("-fx-background-color: " + regionButtonColor.get(region) + "; -fx-text-fill:white; -fx-font-weight:bold;");
            Point2D p = points.get(i);
            Runnable updatePosition = () -> {
                double scaledW = displayW;
                double scaledH = displayH;
                if (scaledW <= 0 || scaledH <= 0) return;
                double x = (p.getX() / mapW) * scaledW - (b.getPrefWidth() / 2.0);
                double y = (p.getY() / mapH) * scaledH - (b.getPrefHeight() / 2.0);
                b.setLayoutX(x);
                b.setLayoutY(y);
            };
            updatePosition.run();
            b.setOnAction(e -> {
                if (selected.contains(region)) return;
                if (!selected.isEmpty()) {
                    boolean adjacent = selected.stream().anyMatch(r -> adjacency.get(r).contains(region));
                    if (!adjacent) {
                        errorLabel.setText("Region is not adjacent to other selected regions");
                        errorLabel.setVisible(true);
                        return;
                    }
                }
                errorLabel.setVisible(false);
                selected.add(region);
                b.setDisable(true);
                b.setStyle("-fx-background-color: #666; -fx-text-fill:white; -fx-font-weight:bold;");
                if (selected.size() >= turnOrder.size()) {
                    listener.onSelectionComplete(selected);
                    return;
                }
                selecting[0] = (selecting[0] + 1) % turnOrder.size();
                redrawOrder.run();
            });
            overlay.getChildren().add(b);
        }

        Pane mapPane = new Pane();
        mapPane.setPickOnBounds(false);
        map.setMouseTransparent(true);
        map.setLayoutX(0);
        map.setLayoutY(0);
        overlay.setLayoutX(0);
        overlay.setLayoutY(0);
        overlay.setMinSize(displayW, displayH);
        overlay.setPrefSize(displayW, displayH);
        overlay.setMaxSize(displayW, displayH);
        mapPane.setMinSize(displayW, displayH);
        mapPane.setPrefSize(displayW, displayH);
        mapPane.setMaxSize(displayW, displayH);
        mapPane.getChildren().addAll(map, overlay);

        StackPane mapStack = new StackPane(mapPane);
        VBox wrapper = new VBox(8, current, errorLabel, mapStack);
        wrapper.setAlignment(Pos.CENTER);
        current.setTextFill(Color.WHITE);
        panel.getChildren().addAll(wrapper, orderBox);
        return panel;
    }
}
