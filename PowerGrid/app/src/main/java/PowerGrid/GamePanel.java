package PowerGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    private StackPane rootStack;
    private Game game;
    private Group mapGroup;

    private final Random random = new Random();
    private List<Player> turnOrder = new ArrayList<>();
    private int currentTurnIdx = 0;
    private String phase = "Power Plant Auction";

    private Label phaseLabel = new Label();
    private Label roundLabel = new Label();
    private Label playerLabel = new Label();
    private Label bidStatus = new Label("No auction active.");

    private PowerPlantDeck deck;
    private String activeAuctionCardId;
    private int highestBid;
    private Player highestBidder;

    private final Map<String, Image> cardImages = new java.util.HashMap<>();

    @Override
    public void start(Stage stage) {
        rootStack = new StackPane();
        MenuPanel menu = new MenuPanel(count -> showPlayerSetup(stage, count));
        rootStack.getChildren().add(menu);

        Scene scene = new Scene(rootStack, 1400, 900);
        stage.setTitle("Power Grid");
        stage.setScene(scene);
        stage.show();
    }

    private void showPlayerSetup(Stage stage, int playerCount) {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #161616;");
        List<TextField> nameFields = new ArrayList<>();
        List<Button> readyButtons = new ArrayList<>();
        Button continueButton = new Button("Continue");
        continueButton.setDisable(true);

        for (int i = 0; i < playerCount; i++) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER);
            TextField tf = new TextField("Player " + (i + 1));
            tf.setPrefWidth(220);
            Button ready = new Button("Ready");
            ready.setOnAction(e -> {
                boolean toReady = "Ready".equals(ready.getText());
                ready.setText(toReady ? "Unready" : "Ready");
                tf.setDisable(toReady);
                boolean allReady = readyButtons.stream().allMatch(b -> "Unready".equals(b.getText()));
                continueButton.setDisable(!allReady);
            });
            readyButtons.add(ready);
            nameFields.add(tf);
            row.getChildren().addAll(tf, ready);
            content.getChildren().add(row);
        }

        continueButton.setOnAction(e -> {
            List<String> names = new ArrayList<>();
            for (TextField tf : nameFields) names.add(tf.getText().isBlank() ? "Player" : tf.getText().trim());
            showReveal(stage, names);
        });
        content.getChildren().add(continueButton);
        rootStack.getChildren().setAll(content);
    }

    private void showReveal(Stage stage, List<String> names) {
        List<String> colors = new ArrayList<>(List.of("#D32F2F", "#1976D2", "#2E7D32", "#7B1FA2", "#FBC02D"));
        Collections.shuffle(names, random);
        List<String> chosen = new ArrayList<>(colors.subList(0, names.size()));

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER);
        for (int i = 0; i < names.size(); i++) {
            VBox col = new VBox(8);
            col.setAlignment(Pos.CENTER);
            Label name = new Label(names.get(i));
            name.setTextFill(Color.WHITE);
            Rectangle square = new Rectangle(30, 30, Color.web(chosen.get(i)));
            col.getChildren().addAll(name, square);
            row.getChildren().add(col);
        }

        Button startGame = new Button("Start Game");
        startGame.setOnAction(e -> showRegionSelection(stage, names, chosen));
        Label t = new Label("Randomized Player Order");
        t.setTextFill(Color.WHITE);
        VBox wrapper = new VBox(18, t, row, startGame);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: #121212;");
        rootStack.getChildren().setAll(wrapper);
    }

    private void showRegionSelection(Stage stage, List<String> orderedNames, List<String> colorHexes) {
        game = new Game(1654, 2287, orderedNames.size());
        game.applyRandomizedPlayerOrder(orderedNames, colorHexes);
        turnOrder = new ArrayList<>(game.getPlayers());

        StackPane panel = new StackPane();
        panel.setStyle("-fx-background-color: black;");

        Image mapImg = new Image(getClass().getResource("/map.png").toExternalForm());
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
        orderBox.setAlignment(Pos.TOP_RIGHT);
        StackPane.setAlignment(orderBox, javafx.geometry.Pos.TOP_RIGHT);
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

        List<RegionColor> selected = new ArrayList<>();
        int[] selecting = {0};

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
                selected.add(region);
                b.setDisable(true);
                b.setStyle("-fx-background-color: #666; -fx-text-fill:white; -fx-font-weight:bold;");
                if (selected.size() >= turnOrder.size()) {
                    game.setRegionsInteractable(selected);
                    initGameView(stage, orderedNames, colorHexes);
                    return;
                }
                selecting[0] = (selecting[0] + 1) % turnOrder.size();
                redrawOrder.run();
            });
            overlay.getChildren().add(b);
        }

        Pane mapPane = new Pane();
        mapPane.setPickOnBounds(true);
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
        VBox wrapper = new VBox(8, current, mapStack);
        wrapper.setAlignment(Pos.CENTER);
        current.setTextFill(Color.WHITE);
        panel.getChildren().addAll(wrapper, orderBox);
        rootStack.getChildren().setAll(panel);
    }

    private void initGameView(Stage stage, List<String> orderedNames, List<String> colorHexes) {
        mapContainer = new Pane();
        mapContainer.setStyle("-fx-background-color: #111;");
        HBox.setHgrow(mapContainer, Priority.ALWAYS);

        String path = getClass().getResource("/map.png").toExternalForm();
        Image img = new Image(path);
        imageView = new ImageView(img);
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);

        mapGroup = new Group(imageView);
        mapGroup.getTransforms().add(transform);
        mapContainer.getChildren().add(mapGroup);

        if (game == null) {
            game = new Game(img.getWidth(), img.getHeight(), orderedNames.size());
            game.applyRandomizedPlayerOrder(orderedNames, colorHexes);
        }
        turnOrder = new ArrayList<>(game.getPlayers());

        for (City city : game.getCities()) {
            mapGroup.getChildren().add(city.getVisualNode());
            city.getVisualNode().setOnMouseClicked(e -> {
                if (city.isInteractable() && !turnOrder.isEmpty()) showCityPopup(city);
                e.consume();
            });
            updateCityStroke(city);
        }

        Rectangle clip = new Rectangle();
        mapContainer.setClip(clip);
        mapContainer.widthProperty().addListener((o, old, val) -> clip.setWidth(val.doubleValue()));
        mapContainer.heightProperty().addListener((o, old, val) -> clip.setHeight(val.doubleValue()));

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

        VBox rightDashboard = buildDashboard();

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
            } catch (NonInvertibleTransformException e) {
            }
            event.consume();
        });

        HBox gameLayout = new HBox(mapContainer, rightDashboard);
        rootStack.getChildren().setAll(gameLayout);
        javafx.application.Platform.runLater(this::resetToCenter);
    }

    private VBox buildDashboard() {
        VBox root = new VBox(10);
        root.setMinWidth(520);
        root.setPrefWidth(560);
        root.setStyle("-fx-background-color: #222; -fx-padding: 12; -fx-border-color: black; -fx-border-width: 0 0 0 2;");

        HBox turnHeader = new HBox(8);
        Label topTitle = new Label("Turn Order");
        topTitle.setTextFill(Color.WHITE);
        turnHeader.getChildren().add(topTitle);
        for (Player p : turnOrder) {
            Label box = new Label(p.getDisplayName());
            box.setStyle("-fx-background-color:" + p.getColorHex() + "; -fx-padding: 6 9 6 9; -fx-text-fill: white;");
            turnHeader.getChildren().add(box);
        }

        phaseLabel.setTextFill(Color.WHITE);
        roundLabel.setTextFill(Color.WHITE);
        playerLabel.setTextFill(Color.WHITE);
        refreshPhaseIndicators();

        HBox phaseBox = new HBox(12, phaseLabel, roundLabel, playerLabel);

        Button auctionBtn = new Button("Power Plant Auction");
        Button resourceBtn = new Button("Purchase Resources");
        HBox actionBtns = new HBox(8, auctionBtn, resourceBtn);

        VBox auctionPanel = buildAuctionPanel();
        VBox resourcesPanel = buildResourcesPanel();
        auctionPanel.setVisible(false); auctionPanel.setManaged(false);
        resourcesPanel.setVisible(false); resourcesPanel.setManaged(false);

        auctionBtn.setOnAction(e -> togglePanel(auctionPanel, resourcesPanel, "Power Plant Auction"));
        resourceBtn.setOnAction(e -> togglePanel(resourcesPanel, auctionPanel, "Purchase Resources"));

        Label bottomTitle = new Label("Owned Power Plants");
        bottomTitle.setTextFill(Color.WHITE);
        HBox owned = buildOwnedPlantsRow();

        root.getChildren().addAll(turnHeader, phaseBox, actionBtns, auctionPanel, resourcesPanel, bottomTitle, owned);
        return root;
    }

    private void togglePanel(VBox show, VBox hide, String newPhase) {
        show.setVisible(!show.isVisible());
        show.setManaged(show.isVisible());
        hide.setVisible(false);
        hide.setManaged(false);
        phase = newPhase;
        refreshPhaseIndicators();
    }

    private void refreshPhaseIndicators() {
        phaseLabel.setText("Phase: " + phase);
        roundLabel.setText("Round: " + game.getRound());
        playerLabel.setText("Current: " + turnOrder.get(currentTurnIdx).getDisplayName());
    }

    private VBox buildAuctionPanel() {
        deck = new PowerPlantDeck();
        VBox box = new VBox(8);
        HBox cardsRow = new HBox(6);

        refreshCardButtons(cardsRow);

        TextField bidField = new TextField();
        bidField.setPromptText("Bid amount");
        Button confirm = new Button("Confirm");
        Button skip = new Button("Skip");
        HBox actions = new HBox(6, bidField, confirm, skip);

        confirm.setOnAction(e -> {
            if (activeAuctionCardId == null) return;
            try {
                int bid = Integer.parseInt(bidField.getText().trim());
                Player current = turnOrder.get(currentTurnIdx);
                int minBid = Integer.parseInt(activeAuctionCardId);
                if (bid > highestBid && bid >= minBid && bid <= current.getElektro()) {
                    highestBid = bid;
                    highestBidder = current;
                    bidStatus.setText(current.getDisplayName() + " bids " + bid);
                    currentTurnIdx = (currentTurnIdx + 1) % turnOrder.size();
                    refreshPhaseIndicators();
                }
            } catch (Exception ignored) {}
        });

        skip.setOnAction(e -> {
            if (activeAuctionCardId == null) return;
            if (highestBidder != null) {
                highestBidder.pay(highestBid);
                if (highestBidder.getPowerPlants().size() < Player.MAX_POWER_PLANTS) {
                    int n = Integer.parseInt(activeAuctionCardId);
                    highestBidder.addPowerPlant(PowerPlant.fossil(n, ResourceType.COAL, 1, 1));
                }
                String drawn = deck.takeFromMarket(activeAuctionCardId);
                if ("STEP3".equals(drawn)) game.enterStep3Immediately();
                bidStatus.setText("Winner: " + highestBidder.getDisplayName() + " at " + highestBid);
                refreshCardButtons(cardsRow);
            }
            activeAuctionCardId = null;
            highestBid = 0;
            highestBidder = null;
        });

        bidStatus.setTextFill(Color.WHITE);
        box.getChildren().addAll(cardsRow, actions, bidStatus);
        return box;
    }

    private void refreshCardButtons(HBox row) {
        row.getChildren().clear();
        for (String id : deck.getMarket()) {
            Button b = new Button();
            b.setPrefSize(84, 84);
            b.setMinSize(84, 84);
            b.setMaxSize(84, 84);
            if ("STEP3".equals(id)) {
                b.setText("STEP3");
            } else {
                ImageView iv = new ImageView(loadCardImage(id));
                iv.setFitWidth(78);
                iv.setFitHeight(78);
                b.setGraphic(iv);
            }
            b.setOnAction(e -> {
                activeAuctionCardId = id;
                highestBid = 0;
                highestBidder = null;
                bidStatus.setText("Auction started for " + id);
            });
            row.getChildren().add(b);
        }
    }

    private Image loadCardImage(String id) {
        return cardImages.computeIfAbsent(id, k -> new Image(getClass().getResource("/PowerPlants/" + k + ".png").toExternalForm()));
    }

    private VBox buildResourcesPanel() {
        VBox box = new VBox(4);
        box.getChildren().add(new Label("Coal"));
        box.getChildren().add(materialRow(ResourceType.COAL));
        box.getChildren().add(new Label("Oil"));
        box.getChildren().add(materialRow(ResourceType.OIL));
        box.getChildren().add(new Label("Trash"));
        box.getChildren().add(materialRow(ResourceType.GARBAGE));
        box.getChildren().add(new Label("Uranium"));
        box.getChildren().add(materialRow(ResourceType.URANIUM));
        for (var n : box.getChildren()) if (n instanceof Label l) l.setTextFill(Color.WHITE);
        return box;
    }

    private HBox materialRow(ResourceType type) {
        HBox row = new HBox(4);
        for (ResourceMarket.Slot slot : game.getResourceMarket().getSlots(type)) {
            Button b = new Button(slot.getPrice() + " (" + slot.getCount() + ")");
            b.setOnAction(e -> {
                Player p = turnOrder.get(currentTurnIdx);
                int idx = game.getResourceMarket().getSlots(type).indexOf(slot);
                int cost = game.getResourceMarket().buy(type, idx);
                if (cost > 0 && p.pay(cost)) {
                    phase = "Purchase Resources";
                    refreshPhaseIndicators();
                    b.setText(slot.getPrice() + " (" + slot.getCount() + ")");
                }
            });
            row.getChildren().add(b);
        }
        return row;
    }

    private HBox buildOwnedPlantsRow() {
        HBox row = new HBox(8);
        Player current = turnOrder.get(currentTurnIdx);
        for (int i = 0; i < 3; i++) {
            Label slot = new Label(i < current.getPowerPlants().size() ? "Plant " + current.getPowerPlants().get(i).getNumber() : "Empty");
            slot.setStyle("-fx-background-color:#333; -fx-text-fill:white; -fx-padding:8;");
            slot.setPrefWidth(140);
            row.getChildren().add(slot);
        }
        return row;
    }

    private void showCityPopup(City city) {
        Player current = turnOrder.get(currentTurnIdx);
        int cost = game.getCurrentEntryCost(city);
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        VBox popup = new VBox(10);
        popup.setAlignment(Pos.CENTER);
        popup.setPrefSize(270, 180);
        popup.setStyle("-fx-background-color:#2A2A2A; -fx-background-radius:16; -fx-padding:12;");

        Label name = new Label(city.getName());
        Label c = new Label("Elektro Cost: " + (cost < 0 ? "Unavailable" : cost));
        Label occ = new Label("Occupied by: " + occupyingText(city));
        name.setTextFill(Color.WHITE); c.setTextFill(Color.WHITE); occ.setTextFill(Color.WHITE);

        Button confirm = new Button("Confirm");
        confirm.setDisable(cost < 0 || !game.canPlayerEnterCity(current, city));
        confirm.setOnAction(e -> {
            if (game.placeHouse(current, city)) {
                updateCityStroke(city);
            }
            rootStack.getChildren().remove(overlay);
        });

        popup.getChildren().addAll(name, c, occ, confirm);
        overlay.getChildren().add(popup);
        overlay.setOnMouseClicked(e -> rootStack.getChildren().remove(overlay));
        rootStack.getChildren().add(overlay);
    }

    private String occupyingText(City city) {
        List<String> names = new ArrayList<>();
        for (int pid : city.getOccupyingPlayers()) {
            Player p = game.getPlayer(pid);
            if (p != null) names.add(p.getDisplayName());
        }
        return names.isEmpty() ? "None" : String.join(", ", names);
    }

    private void updateCityStroke(City city) {
        List<Color> colors = new ArrayList<>();
        for (int pid : city.getOccupyingPlayers()) {
            Player p = game.getPlayer(pid);
            if (p != null) colors.add(Color.web(p.getColorHex()));
        }
        city.updateOccupancyStroke(colors);
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
