package PowerGrid;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
    private Group connectionPathGroup;
    private Group cityPopupGroup;

    private HBox turnOrderHeaderBox;
    private StackPane elektroBadge;
    private Label elektroAmountLabel;

    private final Random random = new Random();
    private List<Player> turnOrder = new ArrayList<>();
    private int currentTurnIdx = 0;
    private int viewingPlayerIdx = 0;
    private String phase = "Power Plant Auction";

    private Label phaseLabel = new Label();
    private Label roundLabel = new Label();
    private Label playerLabel = new Label();
    private Label bidStatus = new Label("No auction active.");
    private HBox ownedPlantsRow;
    private HBox citiesCountRow;
    private Label viewingLabel;
    private HBox playerMaterialsBox;

    private Button auctionPhaseBtn;
    private Button resourcePhaseBtn;
    private VBox auctionPanelRef;
    private VBox resourcesPanelRef;
    private Button resourcesDoneBtn;
    private Button buildCitiesBtn;
    private Button powerCitiesBtn;
    private HBox buildActionsRow;

    private int resourceTurnIdx = 0;
    private int buildTurnIdx = -1;
    private boolean buildPhaseActive = false;
    private final Deque<BuildUndoStep> buildUndoStack = new ArrayDeque<>();

    private int powerTurnIdx = -1;
    private boolean powerPhaseActive = false;

    private enum PowerWizardPane {
        IDLE, MATERIALS, SELECT_PLANTS, SELECT_CITIES, EARNINGS
    }

    private PowerWizardPane powerWizardPane = PowerWizardPane.IDLE;
    private final LinkedHashSet<City> powerCityPickSet = new LinkedHashSet<>();
    private final Set<Integer> powerPlantPickIndices = new LinkedHashSet<>();
    private Player powerWizardPlayer;

    /** Owned / auction power plant card visuals scaled up 30%. */
    private static final double OWNED_PLANT_SLOT = 132 * 1.3;
    private static final double OWNED_PLANT_IMAGE = 120 * 1.3;
    private static final double AUCTION_PLANT_BTN = 84 * 1.3;
    private static final double AUCTION_PLANT_IMAGE = 78 * 1.3;

    private static final class BuildUndoStep {
        final City city;
        final int totalPaid;
        final List<Line> pathLines;

        BuildUndoStep(City city, int totalPaid, List<Line> pathLines) {
            this.city = city;
            this.totalPaid = totalPaid;
            this.pathLines = pathLines;
        }
    }

    private PowerPlantDeck deck;
    private String activeAuctionCardId;
    private TextField auctionBidField;
    private HBox auctionRow1;
    private HBox auctionRow2;
    private boolean auctionInProgress = false;
    private int auctionStarterIdx = -1;
    private int highestBid = 0;
    private int highestBidderIdx = -1;
    private List<Boolean> boughtThisAuctionRound = new ArrayList<>();
    private List<Boolean> optedOutThisAuctionRound = new ArrayList<>();
    private List<Boolean> passedThisAuction = new ArrayList<>();

    private final Map<String, Image> cardImages = new HashMap<>();

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
        initAuctionRoundState();

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
        orderBox.setPickOnBounds(false);
        orderBox.setMouseTransparent(true);
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
        mapPane.setPickOnBounds(false);   // was true
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

        connectionPathGroup = new Group();
        cityPopupGroup = new Group();
        mapGroup = new Group(imageView, connectionPathGroup);
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
                if (!turnOrder.isEmpty()) {
                    handleCityClick(city);
                }
                e.consume();
            });
            updateCityStroke(city);
        }
        mapGroup.getChildren().add(cityPopupGroup);

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
        root.setMinWidth(670);
        root.setPrefWidth(710);
        root.setStyle("-fx-background-color: #222; -fx-padding: 12; -fx-border-color: black; -fx-border-width: 0 0 0 2;");

        turnOrderHeaderBox = new HBox(8);
        Label topTitle = new Label("Turn Order");
        topTitle.setTextFill(Color.WHITE);
        turnOrderHeaderBox.getChildren().add(topTitle);
        refreshTurnHeader();
        citiesCountRow = new HBox(8);
        refreshCitiesCountRow();

        phaseLabel.setTextFill(Color.WHITE);
        roundLabel.setTextFill(Color.WHITE);
        playerLabel.setTextFill(Color.WHITE);
        refreshPhaseIndicators();

        HBox phaseBox = new HBox(12, phaseLabel, roundLabel, playerLabel);

        auctionPhaseBtn = new Button("Power Plant Auction");
        resourcePhaseBtn = new Button("Purchase Resources");
        resourcePhaseBtn.setDisable(true);
        buildCitiesBtn = new Button("Build Cities");
        powerCitiesBtn = new Button("Power Cities");
        buildCitiesBtn.setDisable(true);
        powerCitiesBtn.setDisable(true);
        HBox actionBtns = new HBox(8, auctionPhaseBtn, resourcePhaseBtn, buildCitiesBtn, powerCitiesBtn);

        auctionPanelRef = buildAuctionPanel();
        resourcesPanelRef = buildResourcesPanel();
        auctionPanelRef.setVisible(false);
        auctionPanelRef.setManaged(false);
        resourcesPanelRef.setVisible(false);
        resourcesPanelRef.setManaged(false);

        auctionPhaseBtn.setOnAction(e -> togglePanel(auctionPanelRef, resourcesPanelRef, "Power Plant Auction"));
        resourcePhaseBtn.setOnAction(e -> togglePanel(resourcesPanelRef, auctionPanelRef, "Purchase Resources"));
        buildCitiesBtn.setOnAction(e -> {
            if (!buildPhaseActive) return;
            phase = "Build Cities";
            refreshPhaseIndicators();
        });
        powerCitiesBtn.setOnAction(e -> openPowerCitiesWizard());

        Label bottomTitle = new Label("Owned Power Plants");
        bottomTitle.setTextFill(Color.WHITE);
        viewingLabel = new Label();
        viewingLabel.setTextFill(Color.WHITE);
        ownedPlantsRow = buildOwnedPlantsRow();
        elektroBadge = new StackPane();
        elektroBadge.setMinSize(56, 56);
        elektroBadge.setMaxSize(56, 56);
        Circle eCirc = new Circle(26, Color.rgb(40, 160, 60));
        elektroAmountLabel = new Label("50");
        elektroAmountLabel.setTextFill(Color.WHITE);
        elektroAmountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        elektroBadge.getChildren().addAll(eCirc, elektroAmountLabel);
        HBox plantsAndElektro = new HBox(12, ownedPlantsRow, elektroBadge);
        buildActionsRow = new HBox(8);
        Button undoBuildBtn = new Button("Undo purchase");
        Button finishBuildBtn = new Button("Finish turn");
        undoBuildBtn.setOnAction(e -> undoLastBuild());
        finishBuildBtn.setOnAction(e -> finishBuildTurn());
        buildActionsRow.getChildren().addAll(undoBuildBtn, finishBuildBtn);
        buildActionsRow.setVisible(false);
        buildActionsRow.setManaged(false);
        refreshOwnedAndMaterials();
        playerMaterialsBox = new HBox(10);
        refreshPlayerMaterials();

        Button prev = new Button("View previous player");
        Button next = new Button("View next player");
        Button ret = new Button("Return to board");
        prev.setOnAction(e -> {
            viewingPlayerIdx = (viewingPlayerIdx - 1 + turnOrder.size()) % turnOrder.size();
            refreshOwnedAndMaterials();
        });
        next.setOnAction(e -> {
            viewingPlayerIdx = (viewingPlayerIdx + 1) % turnOrder.size();
            refreshOwnedAndMaterials();
        });
        ret.setOnAction(e -> {
            viewingPlayerIdx = currentTurnIdx;
            refreshOwnedAndMaterials();
        });
        HBox viewButtons = new HBox(8, prev, next, ret);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        root.getChildren().addAll(turnOrderHeaderBox, citiesCountRow, phaseBox, actionBtns, auctionPanelRef, resourcesPanelRef, buildActionsRow, viewingLabel, bottomTitle, plantsAndElektro, playerMaterialsBox, spacer, viewButtons);
        skipBoughtPlayersInAuction();
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
        if (!turnOrder.isEmpty() && currentTurnIdx >= 0 && currentTurnIdx < turnOrder.size()) {
            playerLabel.setText("Current: " + turnOrder.get(currentTurnIdx).getDisplayName());
        }
        if (viewingLabel != null) {
            viewingLabel.setText("Viewing board: " + turnOrder.get(viewingPlayerIdx).getDisplayName());
        }
    }

    private void refreshTurnHeader() {
        if (turnOrderHeaderBox == null) return;
        while (turnOrderHeaderBox.getChildren().size() > 1) {
            turnOrderHeaderBox.getChildren().remove(1);
        }
        for (Player p : turnOrder) {
            Label box = new Label(p.getDisplayName());
            box.setStyle("-fx-background-color:" + p.getColorHex() + "; -fx-padding: 6 9 6 9; -fx-text-fill: white;");
            turnOrderHeaderBox.getChildren().add(box);
        }
    }

    private void handleCityClick(City city) {
        if ("Build Cities".equals(phase) && buildPhaseActive) {
            if (currentTurnIdx < 0 || currentTurnIdx >= turnOrder.size()) return;
            showCityBuildPopup(city);
            return;
        }
        if ("Power Plant Auction".equals(phase) || "Purchase Resources".equals(phase)) {
            return;
        }
    }

    private VBox buildAuctionPanel() {
        deck = new PowerPlantDeck();
        VBox box = new VBox(8);
        auctionRow1 = new HBox(6);
        auctionRow2 = new HBox(6);

        refreshCardButtons();

        auctionBidField = new TextField();
        auctionBidField.setPromptText("Bid amount");
        Button confirm = new Button("Confirm");
        Button skip = new Button("Skip");
        HBox actions = new HBox(6, auctionBidField, confirm, skip);

        confirm.setOnAction(e -> {
            if (activeAuctionCardId == null) return;
            try {
                int bid = Integer.parseInt(auctionBidField.getText().trim());
                Player current = turnOrder.get(currentTurnIdx);
                int minBid = Integer.parseInt(activeAuctionCardId);
                if (!auctionInProgress) {
                    if (bid < minBid || bid > current.getElektro()) return;
                    auctionInProgress = true;
                    auctionStarterIdx = currentTurnIdx;
                    highestBid = bid;
                    highestBidderIdx = currentTurnIdx;
                    bidStatus.setText(current.getDisplayName() + " starts auction for " + activeAuctionCardId + " at " + bid);
                    int next = nextBidderFrom(currentTurnIdx);
                    if (next < 0) {
                        finalizeCurrentAuction();
                    } else {
                        currentTurnIdx = next;
                        viewingPlayerIdx = currentTurnIdx;
                        applySuggestedBidForCurrentTurn();
                        refreshPhaseIndicators();
                    }
                } else {
                    if (bid <= highestBid || bid > current.getElektro()) return;
                    highestBid = bid;
                    highestBidderIdx = currentTurnIdx;
                    bidStatus.setText(current.getDisplayName() + " bids " + bid + " for " + activeAuctionCardId);
                    int next = nextBidderFrom(currentTurnIdx);
                    if (next < 0) {
                        finalizeCurrentAuction();
                    } else {
                        currentTurnIdx = next;
                        viewingPlayerIdx = currentTurnIdx;
                        applySuggestedBidForCurrentTurn();
                        refreshPhaseIndicators();
                    }
                }
            } catch (Exception ignored) {}
        });

        skip.setOnAction(e -> {
            if (auctionInProgress) {
                passedThisAuction.set(currentTurnIdx, true);
                bidStatus.setText(turnOrder.get(currentTurnIdx).getDisplayName() + " passes on " + activeAuctionCardId);
                int next = nextBidderFrom(currentTurnIdx);
                if (next < 0) {
                    finalizeCurrentAuction();
                } else {
                    currentTurnIdx = next;
                    viewingPlayerIdx = currentTurnIdx;
                    applySuggestedBidForCurrentTurn();
                    refreshPhaseIndicators();
                }
                return;
            }

            // Skip while selecting a power plant means opt out for this auction phase round.
            optedOutThisAuctionRound.set(currentTurnIdx, true);
            bidStatus.setText(turnOrder.get(currentTurnIdx).getDisplayName() + " opts out of this auction round.");
            int nextChooser = nextChooserFrom(currentTurnIdx);
            if (nextChooser < 0) {
                enterResourcePurchasePhase();
            } else {
                currentTurnIdx = nextChooser;
                viewingPlayerIdx = currentTurnIdx;
                skipBoughtPlayersInAuction();
            }
            activeAuctionCardId = null;
            auctionBidField.clear();
            refreshPhaseIndicators();
            refreshOwnedAndMaterials();
        });

        bidStatus.setTextFill(Color.WHITE);
        box.getChildren().addAll(auctionRow1, auctionRow2, actions, bidStatus);
        return box;
    }

    private void refreshCardButtons() {
        auctionRow1.getChildren().clear();
        auctionRow2.getChildren().clear();
        List<String> market = deck.getMarket();
        for (int i = 0; i < market.size(); i++) {
            String id = market.get(i);
            Button b = new Button();
            b.setPrefSize(AUCTION_PLANT_BTN, AUCTION_PLANT_BTN);
            b.setMinSize(AUCTION_PLANT_BTN, AUCTION_PLANT_BTN);
            b.setMaxSize(AUCTION_PLANT_BTN, AUCTION_PLANT_BTN);
            if ("STEP3".equals(id)) {
                b.setText("STEP3");
            } else {
                ImageView iv = new ImageView(loadCardImage(id));
                iv.setFitWidth(AUCTION_PLANT_IMAGE);
                iv.setFitHeight(AUCTION_PLANT_IMAGE);
                b.setGraphic(iv);
            }
            b.setOnAction(e -> {
                if (auctionInProgress) {
                    bidStatus.setText("Finish current auction before choosing another plant.");
                    return;
                }
                // In Step 1/2 only current market (4 cheapest) can be auctioned.
                if (game.getStep() < 3 && deck.getMarket().indexOf(id) >= 4) {
                    bidStatus.setText("Only current market plants (top row) can be auctioned.");
                    return;
                }
                if (boughtThisAuctionRound.get(currentTurnIdx) || optedOutThisAuctionRound.get(currentTurnIdx)) {
                    bidStatus.setText("Current player cannot start a new auction this round.");
                    return;
                }
                activeAuctionCardId = id;
                int retail = Integer.parseInt(id);
                auctionBidField.setText(String.valueOf(retail));
                bidStatus.setText("Auction started for " + id);
            });
            if (i < 4) auctionRow1.getChildren().add(b);
            else auctionRow2.getChildren().add(b);
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
        resourcesDoneBtn = new Button("Confirm (finish buying resources)");
        resourcesDoneBtn.setOnAction(e -> finishCurrentResourceTurn());
        box.getChildren().add(resourcesDoneBtn);
        for (var n : box.getChildren()) if (n instanceof Label l) l.setTextFill(Color.WHITE);
        return box;
    }

    private HBox materialRow(ResourceType type) {
        HBox row = new HBox(4);
        List<ResourceMarket.Slot> slots = game.getResourceMarket().getSlots(type);
        int firstNonEmpty = firstNonEmptySlotIndex(slots);
        for (int i = 0; i < slots.size(); i++) {
            ResourceMarket.Slot slot = slots.get(i);
            Button b = new Button(slot.getPrice() + " (" + slot.getCount() + ")");
            b.getProperties().put("resourceType", type);
            b.getProperties().put("slotIndex", i);
            boolean activeSlot = firstNonEmpty >= 0 && i == firstNonEmpty && slot.getCount() > 0;
            b.setDisable(!activeSlot);
            final int slotIndex = i;
            b.setOnAction(e -> {
                Player p = turnOrder.get(resourceTurnIdx);
                if (!canStoreResource(p, type)) return;
                int cost = game.getResourceMarket().buy(type, slotIndex);
                if (cost > 0 && p.pay(cost)) {
                    storeResource(p, type);
                    phase = "Purchase Resources";
                    refreshPhaseIndicators();
                    refreshAllResourceRows();
                    refreshPlayerMaterials();
                }
            });
            row.getChildren().add(b);
        }
        return row;
    }

    /** First column (left to right) that still has stock — only that slot is purchasable. */
    private int firstNonEmptySlotIndex(List<ResourceMarket.Slot> slots) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).getCount() > 0) return i;
        }
        return -1;
    }

    private void refreshAllResourceRows() {
        if (resourcesPanelRef == null) return;
        for (var node : resourcesPanelRef.getChildren()) {
            if (node instanceof HBox row) {
                refreshMaterialRowButtons(row);
            }
        }
    }

    private void refreshMaterialRowButtons(HBox row) {
        for (var child : row.getChildren()) {
            if (!(child instanceof Button b)) continue;
            ResourceType type = (ResourceType) b.getProperties().get("resourceType");
            Integer idxObj = (Integer) b.getProperties().get("slotIndex");
            if (type == null || idxObj == null) continue;
            int idx = idxObj;
            List<ResourceMarket.Slot> slots = game.getResourceMarket().getSlots(type);
            if (idx < 0 || idx >= slots.size()) continue;
            ResourceMarket.Slot slot = slots.get(idx);
            b.setText(slot.getPrice() + " (" + slot.getCount() + ")");
            int first = firstNonEmptySlotIndex(slots);
            boolean activeSlot = first >= 0 && idx == first && slot.getCount() > 0;
            b.setDisable(!activeSlot);
        }
    }

    private HBox buildOwnedPlantsRow() {
        HBox row = new HBox(8);
        for (int i = 0; i < 3; i++) {
            StackPane slot = new StackPane();
            slot.setPrefSize(OWNED_PLANT_SLOT, OWNED_PLANT_SLOT);
            slot.setMinSize(OWNED_PLANT_SLOT, OWNED_PLANT_SLOT);
            slot.setMaxSize(OWNED_PLANT_SLOT, OWNED_PLANT_SLOT);
            slot.setStyle("-fx-background-color:#333; -fx-padding:4;");
            Label empty = new Label("Empty");
            empty.setTextFill(Color.WHITE);
            slot.getChildren().add(empty);
            row.getChildren().add(slot);
        }
        return row;
    }

    private void refreshOwnedAndMaterials() {
        if (ownedPlantsRow == null || turnOrder.isEmpty()) return;
        Player shown = turnOrder.get(viewingPlayerIdx);
        if (elektroAmountLabel != null) {
            elektroAmountLabel.setText(String.valueOf(shown.getElektro()));
        }
        for (int i = 0; i < 3; i++) {
            StackPane slot = (StackPane) ownedPlantsRow.getChildren().get(i);
            slot.setStyle("-fx-background-color:#333; -fx-padding:4;");
            slot.setOnMouseClicked(null);
            slot.getChildren().clear();
            if (i < shown.getPowerPlants().size()) {
                PowerPlant plant = shown.getPowerPlants().get(i);
                String id = String.valueOf(plant.getNumber());
                ImageView iv = new ImageView(loadCardImage(id));
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                iv.setFitWidth(OWNED_PLANT_IMAGE);
                iv.setFitHeight(OWNED_PLANT_IMAGE);
                StackPane.setAlignment(iv, Pos.CENTER);
                iv.setMouseTransparent(true);
                slot.getChildren().add(iv);
            } else {
                Label empty = new Label("Empty");
                empty.setTextFill(Color.WHITE);
                slot.getChildren().add(empty);
            }
        }
        refreshPlayerMaterials();
        refreshPhaseIndicators();
    }

    /** One-line summary for power-city plant picker (no image). */
    private static String powerPlantSelectionLine(PowerPlant pl) {
        int perRun = pl.getResourcesRequired();
        int cap = pl.getCitiesSupplied();
        int stored = pl.getTotalStored();
        if (pl.isEcological()) {
            return String.format("#%d (eco) — %d materials per run — up to %d cities — %d on plant",
                    pl.getNumber(), perRun, cap, stored);
        }
        if (pl.isHybrid()) {
            return String.format("#%d (hybrid) — %d materials per run — up to %d cities — %d on plant",
                    pl.getNumber(), perRun, cap, stored);
        }
        String fuel = pl.getPrimaryResource() != null ? pl.getPrimaryResource().name().toLowerCase() : "";
        return String.format("#%d (%s) — %d materials per run — up to %d cities — %d on plant",
                pl.getNumber(), fuel, perRun, cap, stored);
    }

    private static String formatResourcePoolDescription(String prefix, Map<ResourceType, Integer> pool) {
        StringBuilder sb = new StringBuilder(prefix);
        boolean any = false;
        for (ResourceType t : ResourceType.values()) {
            int c = pool.getOrDefault(t, 0);
            if (c > 0) {
                if (any) sb.append(", ");
                sb.append(t.name().toLowerCase()).append(" ").append(c);
                any = true;
            }
        }
        if (!any) sb.append("none");
        return sb.toString();
    }

    private static boolean committedAllocationsMatchPool(
            Map<ResourceType, Integer> initialPool, List<EnumMap<ResourceType, Integer>> committed) {
        EnumMap<ResourceType, Integer> sum = new EnumMap<>(ResourceType.class);
        for (EnumMap<ResourceType, Integer> m : committed) {
            for (Map.Entry<ResourceType, Integer> e : m.entrySet()) {
                sum.merge(e.getKey(), e.getValue(), Integer::sum);
            }
        }
        for (ResourceType t : ResourceType.values()) {
            if (sum.getOrDefault(t, 0) != initialPool.getOrDefault(t, 0)) {
                return false;
            }
        }
        return true;
    }

    private void refreshPlayerMaterials() {
        if (playerMaterialsBox == null || turnOrder.isEmpty()) return;
        playerMaterialsBox.getChildren().clear();
        Player shown = turnOrder.get(viewingPlayerIdx);
        for (PowerPlant plant : shown.getPowerPlants()) {
            VBox block = new VBox(4);
            Label title = new Label("Plant " + plant.getNumber() + " materials");
            title.setTextFill(Color.WHITE);
            HBox symbols = new HBox(4);
            for (Map.Entry<ResourceType, Integer> e : plant.getStoredResources().entrySet()) {
                for (int i = 0; i < e.getValue(); i++) {
                    symbols.getChildren().add(materialNode(e.getKey()));
                }
            }
            block.getChildren().addAll(title, symbols);
            playerMaterialsBox.getChildren().add(block);
        }
    }

    private javafx.scene.Node materialNode(ResourceType type) {
        if (type == ResourceType.COAL) {
            Rectangle r = new Rectangle(10, 10, Color.BLACK);
            r.setStroke(Color.WHITE);
            r.setStrokeWidth(1);
            return r;
        }
        Circle c = new Circle(5);
        c.setStroke(Color.WHITE);
        c.setStrokeWidth(1);
        if (type == ResourceType.OIL) c.setFill(Color.BLACK);
        else if (type == ResourceType.GARBAGE) c.setFill(Color.YELLOW);
        else c.setFill(Color.RED);
        return c;
    }

    private void refreshCitiesCountRow() {
        if (citiesCountRow == null) return;
        citiesCountRow.getChildren().clear();
        Label title = new Label("Number of Cities");
        title.setTextFill(Color.WHITE);
        citiesCountRow.getChildren().add(title);
        for (Player p : turnOrder) {
            Label box = new Label(String.valueOf(game.getConnectedCityCount(p)));
            box.setStyle("-fx-background-color:" + p.getColorHex() + "; -fx-padding: 4 10 4 10; -fx-text-fill: white;");
            citiesCountRow.getChildren().add(box);
        }
    }

    private void addPurchasedPlant(Player player, String cardId) {
        int num = Integer.parseInt(cardId);
        PowerPlantCard card = PowerPlantCard.byNumber(num);
        if (card == null) return;
        if (player.getPowerPlants().size() >= Player.MAX_POWER_PLANTS) {
            // Keep current behavior simple by scrapping the oldest plant.
            player.removePowerPlant(player.getPowerPlants().get(0));
        }
        player.addPowerPlant(card.toPowerPlant());
    }

    private void initAuctionRoundState() {
        boughtThisAuctionRound = new ArrayList<>();
        optedOutThisAuctionRound = new ArrayList<>();
        passedThisAuction = new ArrayList<>();
        for (int i = 0; i < turnOrder.size(); i++) {
            boughtThisAuctionRound.add(false);
            optedOutThisAuctionRound.add(false);
            passedThisAuction.add(false);
        }
        auctionInProgress = false;
        auctionStarterIdx = -1;
        highestBid = 0;
        highestBidderIdx = -1;
        activeAuctionCardId = null;
    }

    private int nextChooserFrom(int fromIdx) {
        if (turnOrder.isEmpty()) return -1;
        int n = turnOrder.size();
        for (int step = 1; step <= n; step++) {
            int idx = (fromIdx + step) % n;
            if (!boughtThisAuctionRound.get(idx) && !optedOutThisAuctionRound.get(idx)) {
                return idx;
            }
        }
        return -1;
    }

    private int nextBidderFrom(int fromIdx) {
        if (turnOrder.isEmpty()) return -1;
        int activeCount = 0;
        int lastActive = -1;
        for (int i = 0; i < turnOrder.size(); i++) {
            if (!passedThisAuction.get(i) && canParticipateInAuction(i)) {
                activeCount++;
                lastActive = i;
            }
        }
        if (activeCount <= 1) return -1;
        int n = turnOrder.size();
        for (int step = 1; step <= n; step++) {
            int idx = (fromIdx + step) % n;
            if (!passedThisAuction.get(idx) && canParticipateInAuction(idx)) {
                return idx;
            }
        }
        return lastActive;
    }

    /** Eligible to bid on an auction for a specific plant: has not bought this round and has not opted out of Phase 2. */
    private boolean canParticipateInAuction(int playerIndex) {
        if (playerIndex < 0 || playerIndex >= turnOrder.size()) return false;
        return !boughtThisAuctionRound.get(playerIndex) && !optedOutThisAuctionRound.get(playerIndex);
    }

    private void applySuggestedBidForCurrentTurn() {
        if (auctionBidField == null || activeAuctionCardId == null) return;
        int suggested = highestBid + 1;
        if (suggested < Integer.parseInt(activeAuctionCardId)) {
            suggested = Integer.parseInt(activeAuctionCardId);
        }
        auctionBidField.setText(String.valueOf(suggested));
    }

    private void enterResourcePurchasePhase() {
        initAuctionRoundState();
        auctionInProgress = false;
        activeAuctionCardId = null;
        if (game.getRound() == 1) {
            TurnOrder.sortRoundOneByLargestPlant(turnOrder);
            refreshTurnHeader();
        }
        phase = "Purchase Resources";

        if (auctionPhaseBtn != null) {
            auctionPhaseBtn.setDisable(true);
        }
        if (resourcePhaseBtn != null) {
            resourcePhaseBtn.setDisable(false);
        }
        if (powerCitiesBtn != null) {
            powerCitiesBtn.setDisable(true);
        }
        if (auctionPanelRef != null) {
            auctionPanelRef.setVisible(false);
            auctionPanelRef.setManaged(false);
        }
        if (resourcesPanelRef != null) {
            resourcesPanelRef.setVisible(true);
            resourcesPanelRef.setManaged(true);
        }
        resourceTurnIdx = turnOrder.isEmpty() ? 0 : turnOrder.size() - 1;
        if (resourcesDoneBtn != null) {
            resourcesDoneBtn.setDisable(false);
        }
        refreshAllResourceRows();
        currentTurnIdx = resourceTurnIdx;
        viewingPlayerIdx = resourceTurnIdx;
        refreshOwnedAndMaterials();
        refreshPhaseIndicators();
        bidStatus.setText("Auction phase complete. Purchase resources (reverse turn order).");
    }

    private void enterBuildPhase() {
        phase = "Build Cities";
        buildPhaseActive = true;
        buildUndoStack.clear();
        clearConnectionPath();
        cityPopupGroup.getChildren().clear();
        buildTurnIdx = turnOrder.isEmpty() ? 0 : turnOrder.size() - 1;
        currentTurnIdx = buildTurnIdx;
        viewingPlayerIdx = buildTurnIdx;
        if (resourcesPanelRef != null) {
            resourcesPanelRef.setVisible(false);
            resourcesPanelRef.setManaged(false);
        }
        if (resourcePhaseBtn != null) {
            resourcePhaseBtn.setDisable(true);
        }
        if (auctionPhaseBtn != null) {
            auctionPhaseBtn.setDisable(true);
        }
        if (buildCitiesBtn != null) {
            buildCitiesBtn.setDisable(false);
        }
        if (powerCitiesBtn != null) {
            powerCitiesBtn.setDisable(true);
        }
        if (buildActionsRow != null) {
            buildActionsRow.setVisible(true);
            buildActionsRow.setManaged(true);
        }
        refreshOwnedAndMaterials();
        refreshPhaseIndicators();
        bidStatus.setText("Build cities (reverse turn order). Undo / Finish turn when done.");
    }

    private void finishCurrentResourceTurn() {
        if (turnOrder.isEmpty()) return;
        resourceTurnIdx--;
        if (resourceTurnIdx < 0) {
            enterBuildPhase();
            return;
        }
        currentTurnIdx = resourceTurnIdx;
        viewingPlayerIdx = resourceTurnIdx;
        if (resourcesDoneBtn != null) {
            resourcesDoneBtn.setDisable(false);
        }
        refreshAllResourceRows();
        refreshOwnedAndMaterials();
        refreshPhaseIndicators();
    }

    private void clearConnectionPath() {
        if (connectionPathGroup != null) {
            connectionPathGroup.getChildren().clear();
        }
    }

    private void clearCityPopup() {
        if (cityPopupGroup != null) {
            cityPopupGroup.getChildren().clear();
        }
    }

    private void showCityBuildPopup(City city) {
        Player player = turnOrder.get(currentTurnIdx);
        clearCityPopup();
        int total = game.getTotalElektroToEnter(player, city);
        if (total < 0) {
            return;
        }
        PathResult pr = game.getBestPathForBuild(player, city);
        List<Integer> weights = game.getEdgeWeightsAlongPath(pr.getPath());
        int house = game.getCurrentEntryCost(city);
        String pathExpr = weights.isEmpty() ? "0" : weights.stream().map(String::valueOf).collect(Collectors.joining(" + "));
        VBox box = new VBox(6);
        box.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 10; -fx-background-radius: 8;");
        Label name = new Label(city.getName());
        name.setTextFill(Color.WHITE);
        Label occ = new Label("Occupants: " + occupyingText(city));
        occ.setTextFill(Color.WHITE);
        Label pathPrice = new Label("Path: " + pathExpr);
        pathPrice.setTextFill(Color.WHITE);
        Label housePrice = new Label("City slot: " + (house < 0 ? "—" : house));
        housePrice.setTextFill(Color.WHITE);
        Label tot = new Label("Total: " + total);
        tot.setTextFill(Color.LIGHTGREEN);
        Button confirm = new Button("Confirm");
        confirm.setDisable(!game.canPlayerEnterCity(player, city));
        confirm.setOnAction(ev -> {
            int costBefore = total;
            if (!game.placeHouse(player, city)) {
                showOutOfMoneyPopup();
                return;
            }
            List<Line> lines = new ArrayList<>();
            if (pr.getPath().size() >= 2) {
                for (int i = 0; i < pr.getPath().size() - 1; i++) {
                    City a = pr.getPath().get(i);
                    City b = pr.getPath().get(i + 1);
                    Line ln = new Line(a.getMapX(), a.getMapY(), b.getMapX(), b.getMapY());
                    ln.setStroke(Color.RED);
                    ln.setStrokeWidth(3);
                    connectionPathGroup.getChildren().add(ln);
                    lines.add(ln);
                }
            }
            buildUndoStack.push(new BuildUndoStep(city, costBefore, lines));
            updateCityStroke(city);
            refreshCitiesCountRow();
            refreshOwnedAndMaterials();
            clearCityPopup();
            if (player.getElektro() <= 0) {
                showOutOfMoneyPopup();
            }
        });
        box.getChildren().addAll(name, occ, pathPrice, housePrice, tot, confirm);
        box.setLayoutX(city.getMapX() + 55);
        box.setLayoutY(city.getMapY() - 70);
        cityPopupGroup.getChildren().add(box);
    }

    private void showOutOfMoneyPopup() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #8B2222; -fx-padding: 24; -fx-background-radius: 12;");
        Label msg = new Label("You have ran out of money!");
        msg.setTextFill(Color.WHITE);
        Button ok = new Button("Confirm");
        ok.setOnAction(e -> rootStack.getChildren().remove(overlay));
        box.getChildren().addAll(msg, ok);
        overlay.getChildren().add(box);
        rootStack.getChildren().add(overlay);
    }

    private void undoLastBuild() {
        if (!buildPhaseActive || buildUndoStack.isEmpty()) return;
        BuildUndoStep step = buildUndoStack.pop();
        Player player = turnOrder.get(currentTurnIdx);
        game.undoHouse(player, step.city, step.totalPaid);
        for (Line ln : step.pathLines) {
            connectionPathGroup.getChildren().remove(ln);
        }
        updateCityStroke(step.city);
        refreshCitiesCountRow();
        refreshOwnedAndMaterials();
    }

    private void finishBuildTurn() {
        if (!buildPhaseActive) return;
        clearCityPopup();
        buildTurnIdx--;
        if (buildTurnIdx < 0) {
            buildPhaseActive = false;
            if (buildActionsRow != null) {
                buildActionsRow.setVisible(false);
                buildActionsRow.setManaged(false);
            }
            if (buildCitiesBtn != null) {
                buildCitiesBtn.setDisable(true);
            }
            enterPowerCitiesPhase();
            return;
        }
        currentTurnIdx = buildTurnIdx;
        viewingPlayerIdx = buildTurnIdx;
        refreshOwnedAndMaterials();
        refreshPhaseIndicators();
    }

    private void enterPowerCitiesPhase() {
        phase = "Power Cities";
        powerPhaseActive = true;
        powerTurnIdx = 0;
        currentTurnIdx = 0;
        viewingPlayerIdx = 0;
        if (powerCitiesBtn != null) {
            powerCitiesBtn.setDisable(false);
        }
        refreshOwnedAndMaterials();
        refreshPhaseIndicators();
        bidStatus.setText("Power cities — each player clicks Power Cities in turn order.");
    }

    private void openPowerCitiesWizard() {
        if (!powerPhaseActive || turnOrder.isEmpty()) {
            return;
        }
        if (powerTurnIdx < 0 || powerTurnIdx >= turnOrder.size()) {
            return;
        }
        if (powerWizardPane != PowerWizardPane.IDLE) {
            return;
        }
        currentTurnIdx = powerTurnIdx;
        viewingPlayerIdx = powerTurnIdx;
        refreshOwnedAndMaterials();
        powerWizardPlayer = turnOrder.get(powerTurnIdx);
        showPowerCitiesWizardOverlay();
    }

    private void showPowerCitiesWizardOverlay() {
        powerWizardPane = PowerWizardPane.MATERIALS;
        powerCityPickSet.clear();
        powerPlantPickIndices.clear();

        StackPane overlay = new StackPane();
        overlay.setPickOnBounds(true);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.88);");
        VBox root = new VBox(16);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setMaxWidth(640);

        Label materialsTitle = new Label("Change materials on power plants");
        materialsTitle.setTextFill(Color.WHITE);
        materialsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Player p = powerWizardPlayer;
        EnumMap<ResourceType, Integer> initialPool = new EnumMap<>(ResourceType.class);
        for (PowerPlant pl : p.getPowerPlants()) {
            for (Map.Entry<ResourceType, Integer> e : pl.getStoredResources().entrySet()) {
                initialPool.merge(e.getKey(), e.getValue(), Integer::sum);
            }
            pl.clearAllStored();
        }
        String poolLine = formatResourcePoolDescription("Total resources available: ", initialPool);

        List<PowerPlant> plants = p.getPowerPlants();
        int n = plants.size();
        boolean[] plantReady = new boolean[n];
        List<EnumMap<ResourceType, Integer>> committed = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            committed.add(new EnumMap<>(ResourceType.class));
        }

        Label sumErr = new Label();
        sumErr.setTextFill(Color.RED);

        Button topConfirm = new Button("Confirm");
        topConfirm.setDisable(true);

        Runnable updateTopButton = () -> {
            for (int i = 0; i < n; i++) {
                if (!plantReady[i]) {
                    topConfirm.setDisable(true);
                    return;
                }
            }
            topConfirm.setDisable(false);
        };

        VBox plantRows = new VBox(20);
        plantRows.setFillWidth(true);

        for (int pi = 0; pi < n; pi++) {
            PowerPlant plant = plants.get(pi);
            final int idx = pi;
            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 12; -fx-background-radius: 8;");

            if (!plant.isEcological()) {
                String cid = String.valueOf(plant.getNumber());
                ImageView img = new ImageView(loadCardImage(cid));
                img.setPreserveRatio(true);
                img.setFitWidth(OWNED_PLANT_IMAGE * 0.95);
                img.setFitHeight(OWNED_PLANT_IMAGE * 0.95);
                card.getChildren().add(img);
            }

            Label q = new Label("How many materials should go on this plant?");
            q.setTextFill(Color.WHITE);
            Label tot = new Label(poolLine);
            tot.setTextFill(Color.LIGHTGRAY);
            tot.setWrapText(true);

            Button rowConfirm = new Button("Confirm");

            if (plant.isEcological()) {
                Label res = new Label("Resource: none (ecological)");
                res.setTextFill(Color.LIGHTGRAY);
                Label eco = new Label("No resources needed.");
                eco.setTextFill(Color.LIGHTGRAY);
                rowConfirm.setOnAction(e -> {
                    plantReady[idx] = !plantReady[idx];
                    committed.get(idx).clear();
                    rowConfirm.setText(plantReady[idx] ? "Unconfirm" : "Confirm");
                    sumErr.setText("");
                    updateTopButton.run();
                });
                card.getChildren().addAll(q, tot, res, eco, rowConfirm);
            } else if (plant.isHybrid()) {
                Label res = new Label("Resource: coal, oil");
                res.setTextFill(Color.LIGHTGRAY);
                TextField coalField = new TextField();
                coalField.setPromptText("Coal amount");
                TextField oilField = new TextField();
                oilField.setPromptText("Oil amount");
                rowConfirm.setOnAction(e -> {
                    if (plantReady[idx]) {
                        plantReady[idx] = false;
                        committed.get(idx).clear();
                        rowConfirm.setText("Confirm");
                        coalField.setDisable(false);
                        oilField.setDisable(false);
                    } else {
                        int wantCoal;
                        int wantOil;
                        try {
                            wantCoal = coalField.getText().isBlank() ? 0 : Integer.parseInt(coalField.getText().trim());
                            wantOil = oilField.getText().isBlank() ? 0 : Integer.parseInt(oilField.getText().trim());
                        } catch (NumberFormatException ex) {
                            return;
                        }
                        if (wantCoal < 0 || wantOil < 0) return;
                        if (wantCoal + wantOil > plant.getStorageCapacity()) return;
                        committed.get(idx).clear();
                        if (wantCoal > 0) committed.get(idx).put(ResourceType.COAL, wantCoal);
                        if (wantOil > 0) committed.get(idx).put(ResourceType.OIL, wantOil);
                        plantReady[idx] = true;
                        rowConfirm.setText("Unconfirm");
                        coalField.setDisable(true);
                        oilField.setDisable(true);
                    }
                    sumErr.setText("");
                    updateTopButton.run();
                });
                card.getChildren().addAll(q, tot, res, coalField, oilField, rowConfirm);
            } else {
                ResourceType rt = plant.getPrimaryResource();
                Label res = new Label("Resource: " + (rt != null ? rt.name().toLowerCase() : ""));
                res.setTextFill(Color.LIGHTGRAY);
                TextField amountField = new TextField();
                amountField.setPromptText("Amount");
                rowConfirm.setOnAction(e -> {
                    if (plantReady[idx]) {
                        plantReady[idx] = false;
                        committed.get(idx).clear();
                        rowConfirm.setText("Confirm");
                        amountField.setDisable(false);
                    } else {
                        int want;
                        try {
                            want = amountField.getText().isBlank() ? 0 : Integer.parseInt(amountField.getText().trim());
                        } catch (NumberFormatException ex) {
                            return;
                        }
                        if (want < 0 || want > plant.getStorageCapacity()) return;
                        committed.get(idx).clear();
                        if (rt != null && want > 0) {
                            committed.get(idx).put(rt, want);
                        }
                        plantReady[idx] = true;
                        rowConfirm.setText("Unconfirm");
                        amountField.setDisable(true);
                    }
                    sumErr.setText("");
                    updateTopButton.run();
                });
                card.getChildren().addAll(q, tot, res, amountField, rowConfirm);
            }
            plantRows.getChildren().add(card);
        }

        if (n == 0) {
            topConfirm.setDisable(false);
        }

        topConfirm.setOnAction(e -> {
            sumErr.setText("");
            if (!committedAllocationsMatchPool(initialPool, committed)) {
                sumErr.setText("Does not add up to total!");
                return;
            }
            for (int i = 0; i < n; i++) {
                PowerPlant pl = plants.get(i);
                for (Map.Entry<ResourceType, Integer> ent : committed.get(i).entrySet()) {
                    pl.addStored(ent.getKey(), ent.getValue());
                }
            }
            powerWizardPane = PowerWizardPane.SELECT_PLANTS;
            root.getChildren().clear();
            buildSelectPlantsPane(root, overlay);
        });

        ScrollPane scroll = new ScrollPane(plantRows);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(440);
        scroll.setStyle("-fx-background: rgba(0,0,0,0.3); -fx-background-color: transparent;");

        root.getChildren().addAll(materialsTitle, scroll, topConfirm, sumErr);
        StackPane.setAlignment(root, Pos.CENTER);
        overlay.getChildren().add(root);
        rootStack.getChildren().add(overlay);
    }

    private int maxCitiesSelectableForPower(Player player, Set<Integer> plantIndices) {
        if (plantIndices.isEmpty()) return 0;
        int sum = 0;
        List<PowerPlant> plants = player.getPowerPlants();
        for (int i : plantIndices) {
            if (i >= 0 && i < plants.size()) {
                sum += plants.get(i).getProducibleCities();
            }
        }
        return Math.min(sum, game.getConnectedCityCount(player));
    }

    private boolean selectedPlantsHaveEnoughFuel(Player player, Set<Integer> plantIndices) {
        if (plantIndices.isEmpty()) return true;
        List<PowerPlant> plants = player.getPowerPlants();
        for (int i : plantIndices) {
            if (i < 0 || i >= plants.size()) continue;
            PowerPlant pl = plants.get(i);
            if (pl.isEcological()) continue;
            if (pl.getProducibleCities() <= 0) {
                return false;
            }
        }
        return true;
    }

    private void buildSelectPlantsPane(VBox root, StackPane overlay) {
        powerWizardPane = PowerWizardPane.SELECT_PLANTS;
        overlay.setPickOnBounds(true);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.82);");
        powerPlantPickIndices.clear();
        root.setMaxWidth(640);

        Label t = new Label("Select power plants to use");
        t.setTextFill(Color.WHITE);
        t.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label hint = new Label("Each row shows: materials per run, max cities, current stock. Click to toggle (green = selected).");
        hint.setTextFill(Color.LIGHTGRAY);
        hint.setWrapText(true);
        hint.setMaxWidth(580);

        List<PowerPlant> plants = powerWizardPlayer.getPowerPlants();
        List<Button> plantButtons = new ArrayList<>();
        Runnable refreshPlantButtons = () -> {
            for (int i = 0; i < plantButtons.size(); i++) {
                Button b = plantButtons.get(i);
                boolean on = powerPlantPickIndices.contains(i);
                b.setStyle(on
                        ? "-fx-background-color: #1a3d1a; -fx-text-fill: white; -fx-border-color: limegreen; -fx-border-width: 2;"
                        : "-fx-background-color: #444; -fx-text-fill: white;");
            }
        };

        VBox plantList = new VBox(8);
        for (int i = 0; i < plants.size(); i++) {
            final int idx = i;
            PowerPlant pl = plants.get(idx);
            Button b = new Button(powerPlantSelectionLine(pl));
            b.setWrapText(true);
            b.setMaxWidth(600);
            b.setAlignment(Pos.CENTER_LEFT);
            b.setOnAction(ev -> {
                if (powerPlantPickIndices.contains(idx)) {
                    powerPlantPickIndices.remove(idx);
                } else {
                    powerPlantPickIndices.add(idx);
                }
                refreshPlantButtons.run();
            });
            plantButtons.add(b);
            plantList.getChildren().add(b);
        }
        refreshPlantButtons.run();

        ScrollPane plantScroll = new ScrollPane(plantList);
        plantScroll.setFitToWidth(true);
        plantScroll.setPrefViewportHeight(280);
        plantScroll.setStyle("-fx-background: transparent;");

        Label err = new Label();
        err.setTextFill(Color.RED);
        Button confirm = new Button("Confirm");
        VBox box = new VBox(12, t, hint, plantScroll, confirm, err);
        box.setAlignment(Pos.CENTER);
        box.setPickOnBounds(true);
        root.getChildren().setAll(box);
        StackPane.setAlignment(root, Pos.CENTER);

        Player pwc = powerWizardPlayer;
        refreshOwnedAndMaterials();

        confirm.setOnAction(e -> {
            err.setText("");
            if (!selectedPlantsHaveEnoughFuel(pwc, powerPlantPickIndices)) {
                err.setText("Not enough resources!");
                return;
            }
            powerWizardPane = PowerWizardPane.SELECT_CITIES;
            root.getChildren().clear();
            buildSelectCitiesPane(root, overlay);
        });
    }

    private void buildSelectCitiesPane(VBox root, StackPane overlay) {
        powerWizardPane = PowerWizardPane.SELECT_CITIES;
        powerCityPickSet.clear();
        overlay.setPickOnBounds(true);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.82);");
        root.setMaxWidth(640);

        Label t = new Label("Select cities to power");
        t.setTextFill(Color.WHITE);
        t.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        List<City> ownedCities = new ArrayList<>(game.getCitiesForPlayer(powerWizardPlayer));
        ownedCities.sort(Comparator.comparing(City::getName, String.CASE_INSENSITIVE_ORDER));

        Map<City, Button> cityToBtn = new LinkedHashMap<>();
        VBox cityListVBox = new VBox(6);
        for (City city : ownedCities) {
            Button b = new Button(city.getName());
            b.setPrefWidth(420);
            cityToBtn.put(city, b);
            cityListVBox.getChildren().add(b);
        }

        Label poweringList = new Label("Powering: (none)");
        poweringList.setTextFill(Color.WHITE);
        poweringList.setWrapText(true);

        Runnable refreshCityPicks = () -> {
            for (Map.Entry<City, Button> e : cityToBtn.entrySet()) {
                boolean on = powerCityPickSet.contains(e.getKey());
                e.getValue().setStyle(on
                        ? "-fx-background-color: #1a3d1a; -fx-text-fill: white; -fx-border-color: limegreen; -fx-border-width: 2;"
                        : "-fx-background-color: #444; -fx-text-fill: white;");
            }
            if (powerCityPickSet.isEmpty()) {
                poweringList.setText("Powering: (none)");
            } else {
                poweringList.setText("Powering: " + powerCityPickSet.stream()
                        .map(City::getName)
                        .collect(Collectors.joining(", ")));
            }
        };

        for (City city : ownedCities) {
            Button b = cityToBtn.get(city);
            b.setOnAction(ev -> {
                int max = maxCitiesSelectableForPower(powerWizardPlayer, powerPlantPickIndices);
                if (powerCityPickSet.contains(city)) {
                    powerCityPickSet.remove(city);
                } else {
                    if (powerCityPickSet.size() >= max) {
                        return;
                    }
                    powerCityPickSet.add(city);
                }
                refreshCityPicks.run();
            });
        }
        refreshCityPicks.run();

        VBox poweringBox = new VBox(6, poweringList);
        poweringBox.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 10; -fx-background-radius: 8;");
        poweringBox.setMaxWidth(480);

        ScrollPane cityScroll = new ScrollPane(cityListVBox);
        cityScroll.setFitToWidth(true);
        cityScroll.setPrefViewportHeight(240);
        cityScroll.setStyle("-fx-background: transparent;");

        Button confirm = new Button("Confirm");
        VBox box = new VBox(12, t, cityScroll, poweringBox, confirm);
        box.setAlignment(Pos.CENTER);
        box.setPickOnBounds(true);
        root.getChildren().setAll(box);
        StackPane.setAlignment(root, Pos.CENTER);

        confirm.setOnAction(e -> {
            int max = maxCitiesSelectableForPower(powerWizardPlayer, powerPlantPickIndices);
            if (powerCityPickSet.size() > max) {
                return;
            }
            int pay = game.getElektroForCitiesPowered(powerCityPickSet.size());
            applyPowerConsumption(powerWizardPlayer, powerPlantPickIndices, powerCityPickSet.size());
            powerWizardPlayer.receive(pay);
            powerCityPickSet.clear();
            powerWizardPane = PowerWizardPane.EARNINGS;
            overlay.setPickOnBounds(true);
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.88);");
            root.getChildren().clear();
            Label earn = new Label("You earned " + pay + " Elektros!");
            earn.setTextFill(Color.WHITE);
            earn.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            VBox green = new VBox(16);
            green.setAlignment(Pos.CENTER);
            green.setStyle("-fx-background-color: #1a4d1a; -fx-padding: 28; -fx-background-radius: 12; -fx-border-color: #2e7d32; -fx-border-width: 2; -fx-border-radius: 12;");
            green.setMaxWidth(400);
            Button ok = new Button("Confirm");
            green.getChildren().addAll(earn, ok);
            root.getChildren().add(green);
            ok.setOnAction(ev -> {
                rootStack.getChildren().remove(overlay);
                endPowerWizardAndAdvance();
            });
        });
    }

    private void applyPowerConsumption(Player player, Set<Integer> plantIndices, int citiesToPower) {
        if (citiesToPower <= 0 || plantIndices.isEmpty()) return;
        List<PowerPlant> order = new ArrayList<>();
        List<Integer> idxList = new ArrayList<>(plantIndices);
        Collections.sort(idxList);
        for (int i : idxList) {
            if (i >= 0 && i < player.getPowerPlants().size()) {
                order.add(player.getPowerPlants().get(i));
            }
        }
        int remaining = citiesToPower;
        for (PowerPlant plant : order) {
            if (remaining <= 0) break;
            int can = plant.getProducibleCities();
            int take = Math.min(remaining, can);
            if (take <= 0) continue;
            remaining -= take;
            consumeOnePlantActivation(plant);
        }
    }

    private void consumeOnePlantActivation(PowerPlant plant) {
        if (plant.isEcological()) return;
        int need = plant.getResourcesRequired();
        if (plant.isHybrid()) {
            int coal = plant.getStoredResources().getOrDefault(ResourceType.COAL, 0);
            int oil = plant.getStoredResources().getOrDefault(ResourceType.OIL, 0);
            int takeCoal = Math.min(need, coal);
            plant.removeStored(ResourceType.COAL, takeCoal);
            int rest = need - takeCoal;
            if (rest > 0) {
                plant.removeStored(ResourceType.OIL, Math.min(rest, oil));
            }
        } else {
            ResourceType rt = plant.getPrimaryResource();
            if (rt != null) {
                plant.removeStored(rt, need);
            }
        }
    }

    private void endPowerWizardAndAdvance() {
        powerWizardPane = PowerWizardPane.IDLE;
        powerWizardPlayer = null;
        powerPlantPickIndices.clear();
        powerCityPickSet.clear();
        refreshOwnedAndMaterials();

        powerTurnIdx++;
        if (powerTurnIdx >= turnOrder.size()) {
            powerPhaseActive = false;
            if (powerCitiesBtn != null) {
                powerCitiesBtn.setDisable(true);
            }
            game.endOfRound();
            TurnOrder.sortByCitiesThenLargestPlant(turnOrder, game);
            refreshTurnHeader();
            refreshCitiesCountRow();
            refreshOwnedAndMaterials();
            showRoundInterstitial();
            return;
        }
        currentTurnIdx = powerTurnIdx;
        viewingPlayerIdx = powerTurnIdx;
        refreshPhaseIndicators();
        bidStatus.setText("Next player — click Power Cities.");
    }

    private void showRoundInterstitial() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.92);");
        VBox box = new VBox(28);
        box.setAlignment(Pos.CENTER);
        Label title = new Label("Round " + game.getRound());
        title.setTextFill(Color.LIMEGREEN);
        try {
            var url = getClass().getResource("/universcondensed_medium.ttf");
            if (url != null) {
                title.setFont(Font.loadFont(url.toExternalForm(), 56));
            } else {
                title.setStyle("-fx-font-size: 56px;");
            }
        } catch (Exception ignored) {
            title.setStyle("-fx-font-size: 56px;");
        }

        Label orderHead = new Label("Turn order");
        orderHead.setTextFill(Color.WHITE);
        orderHead.setStyle("-fx-font-size: 16px;");

        HBox orderRow = new HBox(12);
        orderRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < turnOrder.size(); i++) {
            Player pl = turnOrder.get(i);
            VBox col = new VBox(6);
            col.setAlignment(Pos.CENTER);
            Rectangle square = new Rectangle(36, 36, Color.web(pl.getColorHex()));
            Label name = new Label((i + 1) + ". " + pl.getDisplayName());
            name.setTextFill(Color.WHITE);
            col.getChildren().addAll(square, name);
            orderRow.getChildren().add(col);
        }

        Label hint = new Label("Click anywhere to proceed");
        hint.setTextFill(Color.GRAY);
        box.getChildren().addAll(title, orderHead, orderRow, hint);
        overlay.getChildren().add(box);
        overlay.setOnMouseClicked(e -> {
            rootStack.getChildren().remove(overlay);
            phase = "Power Plant Auction";
            if (auctionPhaseBtn != null) {
                auctionPhaseBtn.setDisable(false);
            }
            if (resourcePhaseBtn != null) {
                resourcePhaseBtn.setDisable(true);
            }
            if (powerCitiesBtn != null) {
                powerCitiesBtn.setDisable(true);
            }
            currentTurnIdx = 0;
            viewingPlayerIdx = 0;
            refreshOwnedAndMaterials();
            refreshPhaseIndicators();
            bidStatus.setText("New round — power plant auction.");
        });
        rootStack.getChildren().add(overlay);
    }

    private void finalizeCurrentAuction() {
        if (highestBidderIdx < 0 || activeAuctionCardId == null) return;
        Player winner = turnOrder.get(highestBidderIdx);
        if (!winner.pay(highestBid)) {
            bidStatus.setText("Auction failed: winner cannot pay.");
            auctionInProgress = false;
            activeAuctionCardId = null;
            return;
        }
        addPurchasedPlant(winner, activeAuctionCardId);
        boughtThisAuctionRound.set(highestBidderIdx, true);
        String wonCard = activeAuctionCardId;
        String drawn = deck.takeFromMarket(activeAuctionCardId);
        if ("STEP3".equals(drawn)) game.enterStep3Immediately();
        bidStatus.setText(winner.getDisplayName() + " wins " + wonCard + " for " + highestBid);

        int nextChooser;
        if (auctionStarterIdx >= 0 && !boughtThisAuctionRound.get(auctionStarterIdx) && !optedOutThisAuctionRound.get(auctionStarterIdx)) {
            nextChooser = auctionStarterIdx;
        } else {
            nextChooser = nextChooserFrom(auctionStarterIdx >= 0 ? auctionStarterIdx : highestBidderIdx);
        }
        if (nextChooser < 0) {
            bidStatus.setText(bidStatus.getText() + " | Auction phase complete.");
            enterResourcePurchasePhase();
        } else {
            currentTurnIdx = nextChooser;
            viewingPlayerIdx = currentTurnIdx;
            auctionInProgress = false;
            activeAuctionCardId = null;
            highestBid = 0;
            highestBidderIdx = -1;
            for (int i = 0; i < passedThisAuction.size(); i++) {
                passedThisAuction.set(i, false);
            }
            skipBoughtPlayersInAuction();
        }
        auctionBidField.clear();
        refreshCardButtons();
        refreshOwnedAndMaterials();
        refreshCitiesCountRow();
        refreshPhaseIndicators();
    }

    /** If current player already owns a plant this round, they cannot be in the auction UI. */
    private void skipBoughtPlayersInAuction() {
        if (turnOrder.isEmpty()) return;
        if ("Purchase Resources".equals(phase) || "Build / Other".equals(phase)
                || "Build Cities".equals(phase) || "Power Cities".equals(phase)) return;
        int guard = 0;
        while (guard < turnOrder.size() && boughtThisAuctionRound.get(currentTurnIdx)) {
            currentTurnIdx = (currentTurnIdx + 1) % turnOrder.size();
            guard++;
        }
        viewingPlayerIdx = currentTurnIdx;
        refreshPhaseIndicators();
    }

    private boolean canStoreResource(Player player, ResourceType type) {
        for (PowerPlant plant : player.getPowerPlants()) {
            if (plant.canStore(type) && plant.getTotalStored() < plant.getStorageCapacity()) return true;
        }
        return false;
    }

    private void storeResource(Player player, ResourceType type) {
        for (PowerPlant plant : player.getPowerPlants()) {
            if (plant.canStore(type) && plant.getTotalStored() < plant.getStorageCapacity()) {
                plant.addStored(type, 1);
                return;
            }
        }
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
        if (!city.isInteractable() && colors.isEmpty()) {
            city.getVisualNode().getChildren().filtered(n -> n instanceof Circle).forEach(n -> ((Circle) n).setStroke(Color.TRANSPARENT));
        }
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
