package PowerGrid;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.Cursor;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GamePanel extends Application {

    // ── map / scene ──────────────────────────────────────────────────────────
    private double mouseAnchorX, mouseAnchorY;
    private final Affine transform = new Affine();
    private ImageView imageView;
    private Pane mapContainer;
    private StackPane rootStack;
    private Game game;
    private Group mapGroup;
    private Group connectionPathGroup;
    private Group cityPopupGroup;
    private Group turnOrderSlotsGroup;

    // ── persistent HUD ───────────────────────────────────────────────────────
    private StackPane hudOverlay;
    private Label roundHudLabel;
    private Label viewingHudLabel;
    private VBox topRightHudBox;
    private HBox bottomLeftHudBox;
    private StackPane elektroBadge;
    private Label elektroAmountLabel;
    private HBox ownedPlantsRow;
    private HBox buildHudButtonsBox;
    private Pane dragOverlayPane;
    private int  dragSourcePlantIdx = -1;
    private ResourceType dragResourceType = null;
    private Node dragGhostNode = null;

    // ── phase tabs (right edge) ───────────────────────────────────────────────
    private final Map<String, Label> phaseTabs = new LinkedHashMap<>();
    private final Map<String, Timeline> bounceTimelines = new HashMap<>();
    private static final String[] PHASE_KEYS   = {"Power Plant Auction","Purchase Resources","Build Cities","Power Cities"};
    private static final String[] PHASE_LABELS = {"Auction","Resources","Build","Power"};
    private static final String[] TAB_COLORS   = {"#67944A","#67944A","#67944A","#67944A"};

    // ── phase overlays ────────────────────────────────────────────────────────
    private StackPane auctionOverlayPane;
    private Label auctionPlayerTitle;
    private VBox auctionBidPanelBox;
    private Label auctionBidCardLabel;
    private final Label bidStatus = new Label();

    private StackPane resourceOverlayPane;
    private StackPane powerWizardOverlay;

    // ── game state ────────────────────────────────────────────────────────────
    private final Random random = new Random();
    private List<Player> turnOrder = new ArrayList<>();
    private int currentTurnIdx = 0;
    private int viewingPlayerIdx = 0;
    private String phase = "Power Plant Auction";

    private int resourceTurnIdx = 0;
    private int buildTurnIdx = -1;
    private boolean buildPhaseActive = false;
    private final Deque<BuildUndoStep> buildUndoStack = new ArrayDeque<>();

    private int powerTurnIdx = -1;
    private boolean powerPhaseActive = false;

    private enum PowerWizardPane { IDLE, SELECT_PLANTS, SELECT_CITIES, EARNINGS }
    private PowerWizardPane powerWizardPane = PowerWizardPane.IDLE;
    private final LinkedHashSet<City> powerCityPickSet = new LinkedHashSet<>();
    private final Set<Integer> powerPlantPickIndices = new LinkedHashSet<>();
    private Player powerWizardPlayer;

    // ── sizes ─────────────────────────────────────────────────────────────────
    private static final double OWNED_PLANT_SLOT  = 132 * 1.3;
    private static final double OWNED_PLANT_IMAGE = 120 * 1.3;
    private static final double AUCTION_OVERLAY_SLOT  = 220;
    private static final double AUCTION_OVERLAY_IMAGE = 205;

    private static final class BuildUndoStep {
        final City city; final int totalPaid; final List<Line> pathLines;
        BuildUndoStep(City c, int p, List<Line> l) { city=c; totalPaid=p; pathLines=l; }
    }

    // ── auction state ─────────────────────────────────────────────────────────
    private PowerPlantDeck deck;
    private String activeAuctionCardId;
    private TextField auctionBidField;
    private HBox auctionRow1;
    private HBox auctionRow2;
    private boolean auctionInProgress = false;
    private int auctionStarterIdx = -1;
    private int highestBid = 0;
    private int highestBidderIdx = -1;
    private List<Boolean> boughtThisAuctionRound   = new ArrayList<>();
    private List<Boolean> optedOutThisAuctionRound = new ArrayList<>();
    private List<Boolean> passedThisAuction        = new ArrayList<>();

    private final Map<String, Image> cardImages = new HashMap<>();

    // ═════════════════════════════════════════════════════════════════════════
    //  LIFECYCLE
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void start(Stage stage) {
        rootStack = new StackPane();
        rootStack.getChildren().add(new MenuPanel(this::showPlayerSetup));
        stage.setScene(new Scene(rootStack, 1400, 900));
        stage.setTitle("Power Grid");
        stage.show();
    }

    private void showPlayerSetup(int playerCount) {
        rootStack.getChildren().setAll(PlayerSetupPanel.create(playerCount, this::showReveal));
    }

    private void showReveal(List<String> names) {
        List<String> colors = new ArrayList<>(List.of("#D32F2F","#1976D2","#2E7D32","#7B1FA2","#FBC02D"));
        Collections.shuffle(names, random);
        List<String> chosen = new ArrayList<>(colors.subList(0, names.size()));
        HBox row = new HBox(16); row.setAlignment(Pos.CENTER);
        for (int i = 0; i < names.size(); i++) {
            VBox col = new VBox(8); col.setAlignment(Pos.CENTER);
            Label name = new Label(names.get(i)); name.setTextFill(Color.WHITE);
            col.getChildren().addAll(name, new Rectangle(30, 30, Color.web(chosen.get(i))));
            row.getChildren().add(col);
        }
        Button start = new Button("Start Game");
        start.setOnAction(e -> showRegionSelection(names, chosen));
        Label t = new Label("Randomized Player Order"); t.setTextFill(Color.WHITE);
        VBox w = new VBox(18, t, row, start); w.setAlignment(Pos.CENTER);
        w.setStyle("-fx-background-color:#121212;");
        rootStack.getChildren().setAll(w);
    }

    private void showRegionSelection(List<String> orderedNames, List<String> colorHexes) {
        game = new Game(1654, 2287, orderedNames.size());
        game.applyRandomizedPlayerOrder(orderedNames, colorHexes);
        turnOrder = new ArrayList<>(game.getPlayers());
        initAuctionRoundState();
        StackPane panel = RegionSelectionPanel.create(turnOrder, selected -> {
            game.setRegionsInteractable(selected);
            initGameView(orderedNames, colorHexes);
        });
        rootStack.getChildren().setAll(panel);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  MAP + GAME VIEW INIT
    // ═════════════════════════════════════════════════════════════════════════

    private void initGameView(List<String> orderedNames, List<String> colorHexes) {
        mapContainer = new Pane();
        mapContainer.setStyle("-fx-background-color:#111;");

        Image img = new Image(getClass().getResource("/map.png").toExternalForm());
        imageView = new ImageView(img);
        imageView.setCache(true); imageView.setCacheHint(CacheHint.SPEED);

        connectionPathGroup = new Group();
        cityPopupGroup      = new Group();
        turnOrderSlotsGroup = new Group();
        mapGroup = new Group(imageView, connectionPathGroup, turnOrderSlotsGroup);
        mapGroup.getTransforms().add(transform);
        mapContainer.getChildren().add(mapGroup);

        if (game == null) {
            game = new Game(img.getWidth(), img.getHeight(), orderedNames.size());
            game.applyRandomizedPlayerOrder(orderedNames, colorHexes);
        }
        turnOrder = new ArrayList<>(game.getPlayers());
        deck = new PowerPlantDeck();

        for (City city : game.getCities()) {
            mapGroup.getChildren().add(city.getVisualNode());
            city.getVisualNode().setOnMouseClicked(e -> {
                if (!turnOrder.isEmpty()) handleCityClick(city);
                e.consume();
            });
            updateCityStroke(city);
        }
        mapGroup.getChildren().add(cityPopupGroup);

        Rectangle clip = new Rectangle();
        mapContainer.setClip(clip);
        mapContainer.widthProperty().addListener((o,ov,nv)  -> clip.setWidth(nv.doubleValue()));
        mapContainer.heightProperty().addListener((o,ov,nv) -> clip.setHeight(nv.doubleValue()));

        mapContainer.setOnMousePressed(ev -> { mouseAnchorX=ev.getX(); mouseAnchorY=ev.getY(); });
        mapContainer.setOnMouseDragged(ev -> {
            transform.prependTranslation(ev.getX()-mouseAnchorX, ev.getY()-mouseAnchorY);
            mouseAnchorX=ev.getX(); mouseAnchorY=ev.getY();
        });
        mapContainer.addEventFilter(ScrollEvent.ANY, ev -> {
            double f = ev.getDeltaY()>0?1.05:0.95, s = transform.getMxx();
            if (s*f<0.05||s*f>20) return;
            try { Point2D p=transform.inverseTransform(ev.getX(),ev.getY()); transform.appendScale(f,f,p.getX(),p.getY()); }
            catch (NonInvertibleTransformException ignored) {}
            ev.consume();
        });

        refreshTurnOrderSlots();
        hudOverlay = buildHudOverlay();
        rootStack.getChildren().setAll(new StackPane(mapContainer, hudOverlay));
        Platform.runLater(() -> { resetToCenter(); startBounce("Power Plant Auction"); });
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HUD OVERLAY
    // ═════════════════════════════════════════════════════════════════════════

    private StackPane buildHudOverlay() {
        StackPane overlay = new StackPane();
        overlay.setPickOnBounds(false);

        // Home button
        Button homeBtn = new Button("⌂");
        homeBtn.setShape(new Circle(20)); homeBtn.setMinSize(40,40); homeBtn.setMaxSize(40,40);
        homeBtn.setStyle("-fx-background-color:rgba(80,80,80,0.85);-fx-text-fill:white;-fx-font-size:18px;-fx-cursor:hand;");
        homeBtn.setOnAction(e -> resetToCenter());
        StackPane.setAlignment(homeBtn, Pos.TOP_LEFT);
        StackPane.setMargin(homeBtn, new Insets(15));

        // Round label
        roundHudLabel = new Label("Round: 1");
        roundHudLabel.setStyle("-fx-text-fill:white;-fx-font-size:18px;-fx-font-weight:bold;"
            +"-fx-background-color:rgba(0,0,0,0.55);-fx-padding:6 18 6 18;-fx-background-radius:10;");
        roundHudLabel.setMouseTransparent(true);
        StackPane.setAlignment(roundHudLabel, Pos.TOP_CENTER);
        StackPane.setMargin(roundHudLabel, new Insets(12,0,0,0));

        // Viewing label
        viewingHudLabel = new Label();
        viewingHudLabel.setStyle("-fx-text-fill:white;-fx-font-size:15px;-fx-font-weight:bold;"
            +"-fx-background-color:rgba(20,20,100,0.8);-fx-padding:4 14 4 14;-fx-background-radius:8;");
        viewingHudLabel.setVisible(false); viewingHudLabel.setMouseTransparent(true);
        StackPane.setAlignment(viewingHudLabel, Pos.TOP_CENTER);
        StackPane.setMargin(viewingHudLabel, new Insets(48,0,0,0));

        // Top-right turn order + cities
        topRightHudBox = new VBox(5);
        topRightHudBox.setAlignment(Pos.TOP_RIGHT); topRightHudBox.setPickOnBounds(false);
        StackPane.setAlignment(topRightHudBox, Pos.TOP_RIGHT);
        StackPane.setMargin(topRightHudBox, new Insets(12));
        refreshTopRightHud();

        // Bottom-left: owned plants + nav
        ownedPlantsRow = buildOwnedPlantsRow();
        String navStyle = "-fx-background-color:rgba(60,60,60,0.9);-fx-text-fill:white;-fx-background-radius:8;-fx-padding:6 12;";
        Button prev = new Button("◀ Prev"), next = new Button("Next ▶"), ret = new Button("My Board");
        prev.setStyle(navStyle); next.setStyle(navStyle); ret.setStyle(navStyle);
        prev.setOnAction(e -> { viewingPlayerIdx=(viewingPlayerIdx-1+turnOrder.size())%turnOrder.size(); refreshOwnedAndMaterials(); });
        next.setOnAction(e -> { viewingPlayerIdx=(viewingPlayerIdx+1)%turnOrder.size(); refreshOwnedAndMaterials(); });
        ret.setOnAction(e -> { viewingPlayerIdx=currentTurnIdx; refreshOwnedAndMaterials(); });
        VBox navBox = new VBox(6, prev, next, ret); navBox.setAlignment(Pos.CENTER_LEFT);

        bottomLeftHudBox = new HBox(12, ownedPlantsRow, navBox);
        bottomLeftHudBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomLeftHudBox.setStyle("-fx-background-color:rgba(0,0,0,0.55);-fx-padding:10;-fx-background-radius:12;");
        bottomLeftHudBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        bottomLeftHudBox.setPickOnBounds(false);

        Label dragHint = new Label("Drag symbols around to move resources to other power plants");
        dragHint.setTextFill(Color.WHITE);
        dragHint.setStyle("-fx-font-size:12px;");
        dragHint.setMouseTransparent(true);

        VBox bottomLeftWrapper = new VBox(4, dragHint, bottomLeftHudBox);
        bottomLeftWrapper.setAlignment(Pos.BOTTOM_LEFT);
        bottomLeftWrapper.setPickOnBounds(false);
        bottomLeftWrapper.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(bottomLeftWrapper, Pos.BOTTOM_LEFT);
        StackPane.setMargin(bottomLeftWrapper, new Insets(0,0,15,15));

        // Elektro badge bottom-right
        elektroBadge = new StackPane();
        elektroBadge.setMinSize(66,66); elektroBadge.setMaxSize(66,66);
        elektroAmountLabel = new Label("50");
        elektroAmountLabel.setTextFill(Color.WHITE);
        elektroAmountLabel.setStyle("-fx-font-weight:bold;-fx-font-size:16px;");
        elektroBadge.getChildren().addAll(new Circle(30, Color.rgb(40,160,60)), elektroAmountLabel);
        StackPane.setAlignment(elektroBadge, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(elektroBadge, new Insets(0,15,15,0));

        // Phase tabs (right edge)
        VBox tabs = buildPhaseTabs();
        StackPane.setAlignment(tabs, Pos.CENTER_RIGHT);

        dragOverlayPane = new Pane();
        dragOverlayPane.setPickOnBounds(false);
        dragOverlayPane.setMouseTransparent(true);

        overlay.getChildren().addAll(homeBtn, roundHudLabel, viewingHudLabel,
                topRightHudBox, bottomLeftWrapper, elektroBadge, tabs, dragOverlayPane);
        refreshOwnedAndMaterials();
        return overlay;
    }

    private VBox buildPhaseTabs() {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPickOnBounds(false);

        for (int i = 0; i < PHASE_KEYS.length; i++) {
            String key = PHASE_KEYS[i];
            Label tab = new Label(PHASE_LABELS[i]);
            tab.setPrefSize(115, 56);
            tab.setAlignment(Pos.CENTER);
            tab.setStyle(inactiveTabStyle());
            tab.setMouseTransparent(false);
            tab.setOnMouseClicked(e -> handlePhaseTabClick(key));
            phaseTabs.put(key, tab);
            box.getChildren().add(tab);
        }
        return box;
    }

    private static String activeTabStyle(String color) {
        return "-fx-background-color:" + color + ";-fx-text-fill:white;"
            + "-fx-background-radius:12 0 0 12;-fx-font-weight:bold;-fx-font-size:14px;-fx-cursor:hand;"
            + "-fx-border-color:#3D542F;-fx-border-width:2;-fx-border-radius:12 0 0 12;";
    }

    private static String inactiveTabStyle() {
        return "-fx-background-color:rgba(40,40,40,0.75);-fx-text-fill:#666;"
            + "-fx-background-radius:12 0 0 12;-fx-font-size:13px;"
            + "-fx-border-color:#3D542F;-fx-border-width:2;-fx-border-radius:12 0 0 12;";
    }

    // ─── bounce / tab state ──────────────────────────────────────────────────

    private void startBounce(String activePhase) {
        // Stop all running timelines and reset positions
        for (int i = 0; i < PHASE_KEYS.length; i++) {
            String key = PHASE_KEYS[i];
            Timeline old = bounceTimelines.remove(key);
            if (old != null) old.stop();
            Label tab = phaseTabs.get(key);
            if (tab == null) continue;
            tab.setTranslateX(0);
            tab.setStyle(key.equals(activePhase) ? activeTabStyle(TAB_COLORS[i]) : inactiveTabStyle());
        }

        Label activeTab = phaseTabs.get(activePhase);
        if (activeTab == null) return;

        Timeline tl = new Timeline(
            new KeyFrame(Duration.ZERO,         new KeyValue(activeTab.translateXProperty(), 0, Interpolator.LINEAR)),
            new KeyFrame(Duration.millis(160),  new KeyValue(activeTab.translateXProperty(), -24, Interpolator.EASE_OUT)),
            new KeyFrame(Duration.millis(320),  new KeyValue(activeTab.translateXProperty(), 0, Interpolator.EASE_IN)),
            new KeyFrame(Duration.millis(2400), new KeyValue(activeTab.translateXProperty(), 0, Interpolator.LINEAR))
        );
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
        bounceTimelines.put(activePhase, tl);
    }

    private void handlePhaseTabClick(String key) {
        if (!key.equals(phase)) return; // only current phase is interactive
        switch (key) {
            case "Power Plant Auction" -> toggleOrCreate(auctionOverlayPane, () -> createAndShowAuctionOverlay());
            case "Purchase Resources"  -> toggleOrCreate(resourceOverlayPane, () -> createAndShowResourceOverlay());
            case "Build Cities"        -> { /* map IS the build UI; Done button is on HUD */ }
            case "Power Cities"        -> {
                if (powerWizardOverlay != null && rootStack.getChildren().contains(powerWizardOverlay))
                    powerWizardOverlay.setVisible(!powerWizardOverlay.isVisible());
                else if (powerWizardPane == PowerWizardPane.IDLE)
                    openPowerCitiesWizardAuto();
            }
        }
    }

    private void toggleOrCreate(StackPane pane, Runnable creator) {
        if (pane != null && rootStack.getChildren().contains(pane))
            pane.setVisible(!pane.isVisible());
        else
            creator.run();
    }

    // ─── HUD refresh ─────────────────────────────────────────────────────────

    private HBox buildOwnedPlantsRow() {
        HBox row = new HBox(8);
        for (int i = 0; i < 3; i++) {
            StackPane slot = new StackPane();
            slot.setPrefSize(OWNED_PLANT_SLOT,OWNED_PLANT_SLOT);
            slot.setMinSize(OWNED_PLANT_SLOT,OWNED_PLANT_SLOT);
            slot.setMaxSize(OWNED_PLANT_SLOT,OWNED_PLANT_SLOT);
            slot.setStyle("-fx-background-color:#333;-fx-padding:4;-fx-background-radius:12;");
            Label e = new Label("Empty"); e.setTextFill(Color.WHITE);
            slot.getChildren().add(e);
            row.getChildren().add(slot);
        }
        return row;
    }

    private void refreshTurnOrderSlots() {
        if (turnOrderSlotsGroup == null) return;
        turnOrderSlotsGroup.getChildren().clear();
        double[][] bounds = {{119,60,166,108},{179,60,227,107},{239,60,287,107},{299,60,348,107},{360,60,408,107}};
        for (int i = 0; i < Math.min(turnOrder.size(),5); i++) {
            double x1=bounds[i][0],y1=bounds[i][1],x2=bounds[i][2],y2=bounds[i][3];
            double cx=(x1+x2)/2, cy=(y1+y2)/2, w=x2-x1-6, h=y2-y1-6;
            Rectangle r = new Rectangle(cx-w/2, cy-h/2, w, h);
            r.setFill(Color.web(turnOrder.get(i).getColorHex()));
            r.setMouseTransparent(true);
            turnOrderSlotsGroup.getChildren().add(r);
        }
    }

    private void refreshTopRightHud() {
        if (topRightHudBox == null || game == null) return;
        topRightHudBox.getChildren().clear();

        Label toTitle = new Label("Turn Order");
        toTitle.setStyle("-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;"
            +"-fx-background-color:rgba(0,0,0,0.55);-fx-padding:4 10;-fx-background-radius:6;");
        topRightHudBox.getChildren().add(toTitle);
        for (int i = 0; i < turnOrder.size(); i++) {
            Player p = turnOrder.get(i); boolean cur = i==currentTurnIdx;
            Label box = new Label((i+1)+". "+p.getDisplayName());
            box.setStyle("-fx-background-color:"+p.getColorHex()+";-fx-padding:5 10;-fx-text-fill:white;"
                +(cur?"-fx-border-color:white;-fx-border-width:2;-fx-border-radius:3;":""));
            topRightHudBox.getChildren().add(box);
        }

        Label cTitle = new Label("Cities");
        cTitle.setStyle("-fx-text-fill:white;-fx-font-size:12px;"
            +"-fx-background-color:rgba(0,0,0,0.55);-fx-padding:3 10;-fx-background-radius:6;");
        topRightHudBox.getChildren().add(cTitle);

        HBox cr = new HBox(4); cr.setAlignment(Pos.CENTER_RIGHT);
        for (Player p : turnOrder) {
            Label box = new Label(String.valueOf(game.getConnectedCityCount(p)));
            box.setStyle("-fx-background-color:"+p.getColorHex()+";-fx-padding:4 8;-fx-text-fill:white;");
            cr.getChildren().add(box);
        }
        topRightHudBox.getChildren().add(cr);
    }

    private void refreshOwnedAndMaterials() {
        if (ownedPlantsRow == null || turnOrder.isEmpty()) return;
        Player shown = turnOrder.get(viewingPlayerIdx);
        if (elektroAmountLabel != null) elektroAmountLabel.setText(String.valueOf(shown.getElektro()));

        for (int i = 0; i < 3; i++) {
            StackPane slot = (StackPane) ownedPlantsRow.getChildren().get(i);
            slot.setStyle("-fx-background-color:#333;-fx-padding:4;-fx-background-radius:12;");
            slot.getChildren().clear();
            if (i < shown.getPowerPlants().size()) {
                PowerPlant plant = shown.getPowerPlants().get(i);
                ImageView iv = new ImageView(loadCardImage(String.valueOf(plant.getNumber())));
                iv.setPreserveRatio(true); iv.setSmooth(true);
                iv.setFitWidth(OWNED_PLANT_IMAGE); iv.setFitHeight(OWNED_PLANT_IMAGE);
                iv.setMouseTransparent(true);

                HBox symbolsRow = new HBox(5);
                symbolsRow.setAlignment(Pos.CENTER);
                symbolsRow.setPickOnBounds(false);
                final int plantIdx = i;
                boolean isOwnBoard = (viewingPlayerIdx == currentTurnIdx);
                for (ResourceType rt : ResourceType.values()) {
                    int cnt = plant.getStoredResources().getOrDefault(rt, 0);
                    for (int t = 0; t < cnt; t++) {
                        Node sym = makeResourceSymbol(rt);
                        if (isOwnBoard) addDragHandlers(sym, plantIdx, rt);
                        symbolsRow.getChildren().add(sym);
                    }
                }

                slot.getChildren().addAll(iv, symbolsRow);
            } else {
                Label empty = new Label("Empty"); empty.setTextFill(Color.WHITE);
                slot.getChildren().add(empty);
            }
        }

        boolean viewing = viewingPlayerIdx != currentTurnIdx;
        if (viewingHudLabel != null) {
            viewingHudLabel.setVisible(viewing);
            if (viewing) viewingHudLabel.setText("Viewing: " + shown.getDisplayName());
        }
        if (roundHudLabel != null && game != null) roundHudLabel.setText("Round: " + game.getRound());
        refreshTopRightHud();
        if (auctionPlayerTitle != null && !turnOrder.isEmpty())
            auctionPlayerTitle.setText(turnOrder.get(currentTurnIdx).getDisplayName() + " — choose a power plant");
    }

    // ─── resource symbol helpers ─────────────────────────────────────────────

    private Node makeResourceSymbol(ResourceType type) {
        if (type == ResourceType.COAL) {
            Polygon tri = new Polygon(0.0, 16.0, 9.0, 0.0, 18.0, 16.0);
            tri.setFill(Color.BLACK); tri.setStroke(Color.WHITE); tri.setStrokeWidth(1.2);
            return tri;
        }
        Circle c = new Circle(9);
        if      (type == ResourceType.OIL)     { c.setFill(Color.BLACK);  c.setStroke(Color.WHITE); }
        else if (type == ResourceType.GARBAGE)  { c.setFill(Color.YELLOW); c.setStroke(Color.web("#B8860B")); }
        else                                    { c.setFill(Color.RED);    c.setStroke(Color.web("#8B0000")); } // URANIUM
        c.setStrokeWidth(1.2);
        return c;
    }

    private void addDragHandlers(Node symbol, int sourcePlantIdx, ResourceType type) {
        symbol.setOnMousePressed(e -> {
            if (dragOverlayPane == null) return;
            dragSourcePlantIdx = sourcePlantIdx;
            dragResourceType   = type;
            dragGhostNode = makeResourceSymbol(type);
            dragGhostNode.setMouseTransparent(true);
            Point2D pos = dragOverlayPane.sceneToLocal(e.getSceneX(), e.getSceneY());
            dragGhostNode.setLayoutX(pos.getX() - 9);
            dragGhostNode.setLayoutY(pos.getY() - 9);
            dragOverlayPane.getChildren().add(dragGhostNode);
            e.consume();
        });
        symbol.setOnMouseDragged(e -> {
            if (dragGhostNode == null || dragOverlayPane == null) return;
            Point2D pos = dragOverlayPane.sceneToLocal(e.getSceneX(), e.getSceneY());
            dragGhostNode.setLayoutX(pos.getX() - 9);
            dragGhostNode.setLayoutY(pos.getY() - 9);
            e.consume();
        });
        symbol.setOnMouseReleased(e -> {
            try {
                if (dragOverlayPane != null) dragOverlayPane.getChildren().remove(dragGhostNode);
                dragGhostNode = null;
                int targetIdx = getSlotIndexAt(e.getSceneX(), e.getSceneY());
                if (targetIdx >= 0 && targetIdx != dragSourcePlantIdx && !turnOrder.isEmpty()
                        && viewingPlayerIdx == currentTurnIdx) {
                    Player shown = turnOrder.get(viewingPlayerIdx);
                    int plantCount = shown.getPowerPlants().size();
                    if (targetIdx < plantCount
                            && dragSourcePlantIdx >= 0 && dragSourcePlantIdx < plantCount) {
                        PowerPlant src = shown.getPowerPlants().get(dragSourcePlantIdx);
                        PowerPlant tgt = shown.getPowerPlants().get(targetIdx);
                        if (tgt.canStore(dragResourceType)
                                && tgt.getTotalStored() < tgt.getStorageCapacity()
                                && src.getStoredResources().getOrDefault(dragResourceType, 0) > 0) {
                            src.removeStored(dragResourceType, 1);
                            tgt.addStored(dragResourceType, 1);
                        }
                    }
                }
            } finally {
                dragSourcePlantIdx = -1;
                dragResourceType   = null;
                refreshOwnedAndMaterials();
            }
            e.consume();
        });
    }

    private int getSlotIndexAt(double sceneX, double sceneY) {
        if (ownedPlantsRow == null) return -1;
        for (int i = 0; i < ownedPlantsRow.getChildren().size(); i++) {
            Node n = ownedPlantsRow.getChildren().get(i);
            Bounds b = n.localToScene(n.getBoundsInLocal());
            if (b.contains(sceneX, sceneY)) return i;
        }
        return -1;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  AUCTION PHASE OVERLAY
    // ═════════════════════════════════════════════════════════════════════════

    private void createAndShowAuctionOverlay() {
        if (auctionOverlayPane != null) rootStack.getChildren().remove(auctionOverlayPane);

        auctionOverlayPane = new StackPane();

        Rectangle dim = new Rectangle();
        dim.setFill(Color.rgb(0,0,0,0.78));
        dim.widthProperty().bind(auctionOverlayPane.widthProperty());
        dim.heightProperty().bind(auctionOverlayPane.heightProperty());

        // X close button
        Button xBtn = xButton(() -> auctionOverlayPane.setVisible(false));

        auctionPlayerTitle = new Label();
        auctionPlayerTitle.setStyle("-fx-text-fill:white;-fx-font-size:20px;-fx-font-weight:bold;");
        if (!turnOrder.isEmpty())
            auctionPlayerTitle.setText(turnOrder.get(currentTurnIdx).getDisplayName()+" — choose a power plant");

        auctionRow1 = new HBox(12); auctionRow1.setAlignment(Pos.CENTER);
        auctionRow2 = new HBox(12); auctionRow2.setAlignment(Pos.CENTER);

        Label futureLabel = new Label("▼  Future Market  ▼");
        futureLabel.setStyle("-fx-text-fill:#aaa;-fx-font-size:13px;");

        bidStatus.setText(""); bidStatus.setStyle("-fx-text-fill:white;-fx-font-size:13px;");

        // Bid panel
        auctionBidCardLabel = new Label();
        auctionBidCardLabel.setStyle("-fx-text-fill:white;-fx-font-size:16px;-fx-font-weight:bold;");
        auctionBidField = new TextField();
        auctionBidField.setPrefWidth(90);
        auctionBidField.setStyle("-fx-font-size:16px;-fx-alignment:center;");

        String bidBtnStyle = "-fx-background-color:#555;-fx-text-fill:white;-fx-font-size:18px;"
            +"-fx-font-weight:bold;-fx-background-radius:8;-fx-min-width:44;-fx-min-height:44;";
        Button minusBtn = new Button("−"); minusBtn.setStyle(bidBtnStyle);
        Button plusBtn  = new Button("+"); plusBtn.setStyle(bidBtnStyle);
        minusBtn.setOnAction(e -> adjustBid(-1));
        plusBtn.setOnAction(e ->  adjustBid(+1));

        Button confirmBtn = new Button("Confirm Bid");
        confirmBtn.setStyle("-fx-background-color:#2E7D32;-fx-text-fill:white;-fx-font-weight:bold;"
            +"-fx-background-radius:8;-fx-padding:8 22;-fx-font-size:14px;");
        confirmBtn.setOnAction(e -> handleAuctionConfirm());

        Button passBtn = new Button("Pass");
        passBtn.setStyle("-fx-background-color:#CC2929;-fx-text-fill:white;-fx-font-weight:bold;"
            +"-fx-background-radius:8;-fx-padding:8 22;-fx-font-size:14px;");
        passBtn.setOnAction(e -> handleAuctionSkip());

        HBox bidRow = new HBox(10, minusBtn, auctionBidField, plusBtn, confirmBtn, passBtn);
        bidRow.setAlignment(Pos.CENTER);

        auctionBidPanelBox = new VBox(10, auctionBidCardLabel, bidRow);
        auctionBidPanelBox.setAlignment(Pos.CENTER);
        auctionBidPanelBox.setStyle("-fx-padding:18;");
        auctionBidPanelBox.setVisible(false); auctionBidPanelBox.setManaged(false);

        VBox content = new VBox(14, auctionPlayerTitle, auctionRow1, futureLabel, auctionRow2,
                auctionBidPanelBox, bidStatus);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(28));
        content.setMaxWidth(1160);
        content.setPickOnBounds(false);

        StackPane.setAlignment(xBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(xBtn, new Insets(12));

        auctionOverlayPane.getChildren().addAll(dim, content, xBtn);
        refreshCardButtons();
        rootStack.getChildren().add(auctionOverlayPane);
        skipBoughtPlayersInAuction();
    }

    private Button xButton(Runnable onClose) {
        Button b = new Button("✕");
        b.setStyle("-fx-background-color:#C62828;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;"
            +"-fx-background-radius:50%;-fx-min-width:34;-fx-min-height:34;-fx-max-width:34;-fx-max-height:34;-fx-cursor:hand;");
        b.setOnAction(e -> onClose.run());
        return b;
    }

    private void adjustBid(int delta) {
        if (auctionBidField == null) return;
        try { auctionBidField.setText(String.valueOf(Math.max(0, Integer.parseInt(auctionBidField.getText().trim())+delta))); }
        catch (NumberFormatException ignored) {}
    }

    private void refreshCardButtons() {
        if (auctionRow1==null||auctionRow2==null||deck==null) return;
        auctionRow1.getChildren().clear(); auctionRow2.getChildren().clear();
        List<String> market = deck.getMarket();
        for (int i = 0; i < market.size(); i++) {
            String id = market.get(i);
            Button b = new Button();
            b.setPrefSize(AUCTION_OVERLAY_SLOT,AUCTION_OVERLAY_SLOT);
            b.setMinSize(AUCTION_OVERLAY_SLOT,AUCTION_OVERLAY_SLOT);
            b.setMaxSize(AUCTION_OVERLAY_SLOT,AUCTION_OVERLAY_SLOT);
            b.setStyle("-fx-background-color:#2a2a2a;-fx-background-radius:10;-fx-padding:4;");
            if ("STEP3".equals(id)) { b.setText("STEP 3"); b.setStyle(b.getStyle()+"-fx-text-fill:white;"); }
            else { ImageView iv = new ImageView(loadCardImage(id)); iv.setFitWidth(AUCTION_OVERLAY_IMAGE); iv.setFitHeight(AUCTION_OVERLAY_IMAGE); b.setGraphic(iv); }
            final boolean top = (i < 4);
            b.setOnAction(e -> {
                if (auctionInProgress) { bidStatus.setText("Finish current auction first."); return; }
                if (!top && game.getStep()<3) { bidStatus.setText("Only top-row plants can be auctioned."); return; }
                if (boughtThisAuctionRound.get(currentTurnIdx)||optedOutThisAuctionRound.get(currentTurnIdx)) {
                    bidStatus.setText("This player already acted this round."); return;
                }
                activeAuctionCardId = id;
                int retail = Integer.parseInt(id);
                auctionBidField.setText(String.valueOf(retail));
                auctionBidCardLabel.setText("Bidding on Plant #"+id+"  (min "+retail+")");
                auctionBidPanelBox.setVisible(true); auctionBidPanelBox.setManaged(true);
                bidStatus.setText("");
            });
            if (i<4) auctionRow1.getChildren().add(b); else auctionRow2.getChildren().add(b);
        }
    }

    private void handleAuctionConfirm() {
        if (activeAuctionCardId == null) return;
        try {
            int bid = Integer.parseInt(auctionBidField.getText().trim());
            Player current = turnOrder.get(currentTurnIdx);
            int minBid = Integer.parseInt(activeAuctionCardId);
            if (!auctionInProgress) {
                if (bid < minBid || bid > current.getElektro()) { bidStatus.setText("Invalid bid."); return; }
                auctionInProgress=true; auctionStarterIdx=currentTurnIdx; highestBid=bid; highestBidderIdx=currentTurnIdx;
                bidStatus.setText(current.getDisplayName()+" starts at "+bid);
                int next = nextBidderFrom(currentTurnIdx);
                if (next<0) finalizeCurrentAuction();
                else { currentTurnIdx=next; viewingPlayerIdx=next; applySuggestedBidForCurrentTurn(); refreshOwnedAndMaterials(); }
            } else {
                if (bid<=highestBid||bid>current.getElektro()) { bidStatus.setText("Bid must be > "+highestBid+"."); return; }
                highestBid=bid; highestBidderIdx=currentTurnIdx;
                bidStatus.setText(current.getDisplayName()+" bids "+bid);
                int next = nextBidderFrom(currentTurnIdx);
                if (next<0) finalizeCurrentAuction();
                else { currentTurnIdx=next; viewingPlayerIdx=next; applySuggestedBidForCurrentTurn(); refreshOwnedAndMaterials(); }
            }
        } catch (NumberFormatException ignored) {}
    }

    private void handleAuctionSkip() {
        if (auctionInProgress) {
            passedThisAuction.set(currentTurnIdx, true);
            bidStatus.setText(turnOrder.get(currentTurnIdx).getDisplayName()+" passes.");
            int next = nextBidderFrom(currentTurnIdx);
            if (next<0) finalizeCurrentAuction();
            else { currentTurnIdx=next; viewingPlayerIdx=next; applySuggestedBidForCurrentTurn(); refreshOwnedAndMaterials(); }
            return;
        }
        optedOutThisAuctionRound.set(currentTurnIdx, true);
        bidStatus.setText(turnOrder.get(currentTurnIdx).getDisplayName()+" opts out.");
        activeAuctionCardId = null;
        if (auctionBidPanelBox!=null) { auctionBidPanelBox.setVisible(false); auctionBidPanelBox.setManaged(false); }
        int next = nextChooserFrom(currentTurnIdx);
        if (next<0) { enterResourcePurchasePhase(); return; }
        currentTurnIdx=next; viewingPlayerIdx=next;
        skipBoughtPlayersInAuction();
        refreshOwnedAndMaterials();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  RESOURCE PHASE OVERLAY
    // ═════════════════════════════════════════════════════════════════════════

    private void enterResourcePurchasePhase() {
        initAuctionRoundState();
        if (game.getRound()==1) {
            TurnOrder.sortRoundOneByLargestPlant(turnOrder);
            refreshTurnOrderSlots();
        }
        phase = "Purchase Resources";
        resourceTurnIdx = turnOrder.isEmpty() ? 0 : turnOrder.size()-1;
        currentTurnIdx = resourceTurnIdx; viewingPlayerIdx = resourceTurnIdx;
        if (auctionOverlayPane!=null) { rootStack.getChildren().remove(auctionOverlayPane); auctionOverlayPane=null; }
        refreshTopRightHud();
        refreshOwnedAndMaterials();
        startBounce("Purchase Resources");
    }

    private void createAndShowResourceOverlay() {
        if (resourceOverlayPane!=null) rootStack.getChildren().remove(resourceOverlayPane);
        if (turnOrder.isEmpty()) return;
        Player player = turnOrder.get(resourceTurnIdx);

        resourceOverlayPane = new StackPane();
        Rectangle dim = new Rectangle();
        dim.setFill(Color.rgb(0,0,0,0.82));
        dim.widthProperty().bind(resourceOverlayPane.widthProperty());
        dim.heightProperty().bind(resourceOverlayPane.heightProperty());

        Button xBtn = xButton(() -> resourceOverlayPane.setVisible(false));
        StackPane.setAlignment(xBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(xBtn, new Insets(12));

        Label title = new Label("Purchase Resources — "+player.getDisplayName());
        title.setStyle("-fx-text-fill:white;-fx-font-size:20px;-fx-font-weight:bold;");

        EnumMap<ResourceType,List<int[]>> cart = new EnumMap<>(ResourceType.class);
        for (ResourceType t : ResourceType.values()) cart.put(t, new ArrayList<>());
        int[] totalCost = {0};

        Label totalLabel = new Label("0");
        totalLabel.setStyle("-fx-text-fill:white;-fx-font-size:16px;-fx-font-weight:bold;"
            +"-fx-background-color:#1a4d1a;-fx-padding:6 16;-fx-background-radius:10;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;"
            +"-fx-background-color:#8B2222;-fx-padding:8 16;-fx-background-radius:8;");
        errorLabel.setVisible(false); errorLabel.setManaged(false);

        ResourceType[] types = {ResourceType.COAL,ResourceType.OIL,ResourceType.GARBAGE,ResourceType.URANIUM};
        String[]       names = {"Coal","Oil","Trash","Uranium"};

        HBox cardsRow = new HBox(16); cardsRow.setAlignment(Pos.CENTER);

        for (int ti = 0; ti < types.length; ti++) {
            ResourceType type = types[ti];
            String typeName = names[ti];
            boolean isUranium = (type == ResourceType.URANIUM);

            // Token symbols (3 per slot for coal/oil/trash)
            List<Shape> symbols = new ArrayList<>();
            HBox symbolBox = new HBox(5);
            symbolBox.setAlignment(Pos.CENTER_RIGHT);
            if (!isUranium) {
                for (int s = 0; s < 3; s++) {
                    Shape shape;
                    if (type == ResourceType.COAL) {
                        Polygon tri = new Polygon(0.0,14.0, 8.0,0.0, 16.0,14.0);
                        tri.setFill(Color.WHITE); tri.setStroke(Color.WHITE); tri.setStrokeWidth(1.5);
                        shape = tri;
                    } else {
                        Circle c = new Circle(8);
                        c.setFill(Color.WHITE);
                        c.setStroke(type == ResourceType.GARBAGE ? Color.BLACK : Color.WHITE);
                        c.setStrokeWidth(1.5);
                        shape = c;
                    }
                    symbols.add(shape);
                    symbolBox.getChildren().add(shape);
                }
            }

            List<ResourceMarket.Slot> sl = game.getResourceMarket().getSlots(type);

            Label countLbl = new Label("0");
            countLbl.setStyle("-fx-background-color:rgba(255,255,255,0.15);-fx-background-radius:6;"
                +"-fx-min-width:36;-fx-min-height:36;-fx-alignment:center;"
                +"-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;");

            Label totalAvailLbl = new Label("Total Available: 0");
            totalAvailLbl.setStyle("-fx-text-fill:#ccc;-fx-font-size:13px;");

            Label currentPriceLbl = new Label("Current Price: —");
            currentPriceLbl.setStyle("-fx-text-fill:#ccc;-fx-font-size:13px;");

            Runnable updateUI = () -> {
                countLbl.setText(String.valueOf(cart.get(type).size()));
                int fi = firstNonEmptySlotIndex(sl);
                int total = 0; for (ResourceMarket.Slot slot : sl) total += slot.getCount();
                totalAvailLbl.setText("Total Available: " + total);
                if (fi >= 0) {
                    currentPriceLbl.setText("Current Price: " + sl.get(fi).getPrice());
                    if (!isUranium) {
                        int count = sl.get(fi).getCount();
                        for (int s = 0; s < 3; s++) {
                            boolean filled = s >= (3 - count);
                            Shape shape = symbols.get(s);
                            if (type == ResourceType.GARBAGE) {
                                shape.setFill(filled ? Color.YELLOW : Color.WHITE);
                                shape.setStroke(filled ? Color.web("#B8860B") : Color.BLACK);
                            } else {
                                shape.setFill(filled ? Color.BLACK : Color.WHITE);
                                shape.setStroke(Color.WHITE);
                            }
                        }
                    }
                } else {
                    currentPriceLbl.setText("Current Price: —");
                    Color emptyStroke = (type == ResourceType.GARBAGE) ? Color.BLACK : Color.WHITE;
                    for (Shape shape : symbols) { shape.setFill(Color.WHITE); shape.setStroke(emptyStroke); }
                }
                totalLabel.setText(String.valueOf(totalCost[0]));
            };
            updateUI.run();

            String btnStyle = "-fx-background-color:#444;-fx-text-fill:white;-fx-font-size:22px;"
                +"-fx-font-weight:bold;-fx-background-radius:8;-fx-min-width:48;-fx-min-height:48;";
            Button minusBtn = new Button("−"); minusBtn.setStyle(btnStyle);
            Button plusBtn  = new Button("+"); plusBtn.setStyle(btnStyle);

            plusBtn.setOnAction(e -> {
                errorLabel.setVisible(false); errorLabel.setManaged(false);
                if (!canStoreResource(player,type)) {
                    errorLabel.setText("You do not have space on your power plants.");
                    errorLabel.setVisible(true); errorLabel.setManaged(true); return;
                }
                int fi = firstNonEmptySlotIndex(sl); if (fi<0) return;
                int price = game.getResourceMarket().buy(type,fi); if (price<0) return;
                storeResource(player,type);
                cart.get(type).add(new int[]{fi,price});
                totalCost[0]+=price; updateUI.run();
            });
            minusBtn.setOnAction(e -> {
                errorLabel.setVisible(false); errorLabel.setManaged(false);
                List<int[]> tc = cart.get(type); if (tc.isEmpty()) return;
                int[] last = tc.remove(tc.size()-1);
                sl.get(last[0]).addOne();
                unstoreResource(player,type);
                totalCost[0]-=last[1]; updateUI.run();
            });

            Label nameLabel = new Label(typeName);
            nameLabel.setStyle("-fx-text-fill:white;-fx-font-size:22px;-fx-font-weight:bold;");

            Region nameSpacer = new Region();
            HBox.setHgrow(nameSpacer, Priority.ALWAYS);
            HBox topRow = new HBox(8);
            topRow.setAlignment(Pos.CENTER_LEFT);
            topRow.getChildren().addAll(nameLabel, nameSpacer);
            if (!isUranium) topRow.getChildren().add(symbolBox);

            HBox btnRow = new HBox(8, countLbl, minusBtn, plusBtn);
            btnRow.setAlignment(Pos.CENTER_LEFT);

            VBox card = new VBox(10, topRow, totalAvailLbl, currentPriceLbl, btnRow);
            card.setAlignment(Pos.TOP_LEFT);
            card.setPrefSize(230, 160);
            card.setStyle("-fx-background-color:black;-fx-background-radius:16;-fx-padding:16;");
            cardsRow.getChildren().add(card);
        }

        Button confirmBtn = new Button("Confirm Purchase");
        confirmBtn.setStyle("-fx-background-color:#2E7D32;-fx-text-fill:white;-fx-font-weight:bold;"
            +"-fx-background-radius:8;-fx-padding:10 30;-fx-font-size:15px;");
        confirmBtn.setOnAction(e -> {
            if (!player.pay(totalCost[0])) {
                errorLabel.setText("Not enough Elektro!"); errorLabel.setVisible(true); errorLabel.setManaged(true); return;
            }
            rootStack.getChildren().remove(resourceOverlayPane); resourceOverlayPane=null;
            advanceResourceTurn();
        });

        HBox totalRow = new HBox(12, new Label("Total Cost:"){{setStyle("-fx-text-fill:white;-fx-font-size:15px;");}}, totalLabel);
        totalRow.setAlignment(Pos.CENTER);

        VBox content = new VBox(16, title, cardsRow, errorLabel, totalRow, confirmBtn);
        content.setAlignment(Pos.CENTER); content.setPadding(new Insets(30)); content.setMaxWidth(950);

        resourceOverlayPane.getChildren().addAll(dim, content, xBtn);
        rootStack.getChildren().add(resourceOverlayPane);
        refreshOwnedAndMaterials();
    }

    private void advanceResourceTurn() {
        resourceTurnIdx--;
        if (resourceTurnIdx<0) { enterBuildPhase(); return; }
        currentTurnIdx=resourceTurnIdx; viewingPlayerIdx=resourceTurnIdx;
        refreshOwnedAndMaterials();
        // Resources tab still bouncing — next player must click it
    }

    private void unstoreResource(Player player, ResourceType type) {
        List<PowerPlant> plants = player.getPowerPlants();
        for (int i = plants.size()-1; i>=0; i--) {
            PowerPlant plant = plants.get(i);
            if (plant.getStoredResources().getOrDefault(type,0)>0) { plant.removeStored(type,1); return; }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  BUILD PHASE
    // ═════════════════════════════════════════════════════════════════════════

    private void enterBuildPhase() {
        phase = "Build Cities";
        buildPhaseActive=true; buildUndoStack.clear();
        clearConnectionPath(); cityPopupGroup.getChildren().clear();
        buildTurnIdx=turnOrder.isEmpty()?0:turnOrder.size()-1;
        currentTurnIdx=buildTurnIdx; viewingPlayerIdx=buildTurnIdx;

        if (buildHudButtonsBox!=null&&hudOverlay!=null) hudOverlay.getChildren().remove(buildHudButtonsBox);

        Button undoBtn = new Button("Undo");
        undoBtn.setStyle("-fx-background-color:#CC2929;-fx-text-fill:white;-fx-background-radius:8;-fx-padding:8 20;-fx-font-size:14px;");
        undoBtn.setOnAction(e -> undoLastBuild());

        Button doneBtn = new Button("Done Building");
        doneBtn.setStyle("-fx-background-color:#2E7D32;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:8;-fx-padding:10 28;-fx-font-size:15px;");
        doneBtn.setOnAction(e -> finishBuildTurn());

        buildHudButtonsBox = new HBox(12, undoBtn, doneBtn);
        buildHudButtonsBox.setAlignment(Pos.CENTER);
        buildHudButtonsBox.setStyle("-fx-background-color:rgba(0,0,0,0.55);-fx-padding:8 16;-fx-background-radius:10;");
        buildHudButtonsBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        buildHudButtonsBox.setPickOnBounds(false);
        StackPane.setAlignment(buildHudButtonsBox, Pos.TOP_CENTER);
        StackPane.setMargin(buildHudButtonsBox, new Insets(55,0,0,0));
        if (hudOverlay!=null) hudOverlay.getChildren().add(buildHudButtonsBox);

        refreshOwnedAndMaterials();
        startBounce("Build Cities");
    }

    private void handleCityClick(City city) {
        if ("Build Cities".equals(phase)&&buildPhaseActive) {
            if (currentTurnIdx>=0&&currentTurnIdx<turnOrder.size()) showCityBuildPopup(city);
        }
    }

    private void showCityBuildPopup(City city) {
        Player player = turnOrder.get(currentTurnIdx);
        clearCityPopup();
        int total = game.getTotalElektroToEnter(player,city); if (total<0) return;
        PathResult pr = game.getBestPathForBuild(player,city);
        List<Integer> weights = game.getEdgeWeightsAlongPath(pr.getPath());
        int house = game.getCurrentEntryCost(city);
        String pathExpr = weights.isEmpty()?"0":weights.stream().map(String::valueOf).collect(Collectors.joining(" + "));
        VBox box = new VBox(6);
        box.setStyle("-fx-background-color:#2a2a2a;-fx-padding:10;-fx-background-radius:8;");
        Label nm=new Label(city.getName()); nm.setTextFill(Color.WHITE);
        Label oc=new Label("Occupants: "+occupyingText(city)); oc.setTextFill(Color.WHITE);
        Label pp=new Label("Path: "+pathExpr); pp.setTextFill(Color.WHITE);
        Label hp=new Label("City slot: "+(house<0?"—":house)); hp.setTextFill(Color.WHITE);
        Label tot=new Label("Total: "+total); tot.setTextFill(Color.LIGHTGREEN);
        Button confirm=new Button("Confirm"); confirm.setDisable(!game.canPlayerEnterCity(player,city));
        confirm.setOnAction(ev -> {
            if (!game.placeHouse(player,city)) { showOutOfMoneyPopup(); return; }
            List<Line> lines=new ArrayList<>();
            if (pr.getPath().size()>=2) for (int i=0;i<pr.getPath().size()-1;i++) {
                City a=pr.getPath().get(i),b=pr.getPath().get(i+1);
                Line ln=new Line(a.getMapX(),a.getMapY(),b.getMapX(),b.getMapY());
                ln.setStroke(Color.RED); ln.setStrokeWidth(3);
                connectionPathGroup.getChildren().add(ln); lines.add(ln);
            }
            buildUndoStack.push(new BuildUndoStep(city,total,lines));
            updateCityStroke(city); refreshTopRightHud(); refreshOwnedAndMaterials(); clearCityPopup();
        });
        box.getChildren().addAll(nm,oc,pp,hp,tot,confirm);
        box.setLayoutX(city.getMapX()+55); box.setLayoutY(city.getMapY()-70);
        cityPopupGroup.getChildren().add(box);
    }

    private void undoLastBuild() {
        if (!buildPhaseActive||buildUndoStack.isEmpty()) return;
        BuildUndoStep step=buildUndoStack.pop();
        game.undoHouse(turnOrder.get(currentTurnIdx),step.city,step.totalPaid);
        for (Line ln:step.pathLines) connectionPathGroup.getChildren().remove(ln);
        updateCityStroke(step.city); refreshTopRightHud(); refreshOwnedAndMaterials();
    }

    private void finishBuildTurn() {
        if (!buildPhaseActive) return;
        clearCityPopup(); buildTurnIdx--;
        if (buildTurnIdx<0) {
            buildPhaseActive=false;
            if (buildHudButtonsBox!=null&&hudOverlay!=null) hudOverlay.getChildren().remove(buildHudButtonsBox);
            enterPowerCitiesPhase(); return;
        }
        currentTurnIdx=buildTurnIdx; viewingPlayerIdx=buildTurnIdx;
        refreshOwnedAndMaterials();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  POWER CITIES PHASE
    // ═════════════════════════════════════════════════════════════════════════

    private void enterPowerCitiesPhase() {
        phase="Power Cities"; powerPhaseActive=true;
        powerTurnIdx=0; currentTurnIdx=0; viewingPlayerIdx=0;
        refreshOwnedAndMaterials();
        startBounce("Power Cities");
    }

    private void openPowerCitiesWizardAuto() {
        if (!powerPhaseActive||powerTurnIdx<0||powerTurnIdx>=turnOrder.size()) return;
        if (powerWizardPane!=PowerWizardPane.IDLE) return;
        currentTurnIdx=powerTurnIdx; viewingPlayerIdx=powerTurnIdx;
        refreshOwnedAndMaterials();
        powerWizardPlayer=turnOrder.get(powerTurnIdx);
        showPowerCitiesWizardOverlay();
    }

    private void showPowerCitiesWizardOverlay() {
        powerCityPickSet.clear(); powerPlantPickIndices.clear();

        powerWizardOverlay = new StackPane();
        powerWizardOverlay.setPickOnBounds(true);
        powerWizardOverlay.setStyle("-fx-background-color:rgba(0,0,0,0.88);");

        Button xBtn = xButton(() -> powerWizardOverlay.setVisible(false));
        StackPane.setAlignment(xBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(xBtn, new Insets(12));

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));
        root.setMaxWidth(720);
        StackPane.setAlignment(root, Pos.CENTER);

        powerWizardOverlay.getChildren().addAll(root, xBtn);
        rootStack.getChildren().add(powerWizardOverlay);
        buildSelectPlantsPane(root, powerWizardOverlay);
    }

    private void buildSelectPlantsPane(VBox root, StackPane overlay) {
        powerWizardPane = PowerWizardPane.SELECT_PLANTS;
        powerPlantPickIndices.clear();

        Label title = new Label("Select power plants to use");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size:20px;-fx-font-weight:bold;");

        List<PowerPlant> plants = powerWizardPlayer.getPowerPlants();
        List<StackPane> cards = new ArrayList<>();

        String selStyle  = "-fx-background-color:#1a3d1a;-fx-border-color:limegreen;-fx-border-width:3;"
                         + "-fx-border-radius:12;-fx-background-radius:12;-fx-padding:6;";
        String normStyle = "-fx-background-color:#444;-fx-border-color:transparent;-fx-border-width:3;"
                         + "-fx-border-radius:12;-fx-background-radius:12;-fx-padding:6;";

        Label err = new Label();
        err.setTextFill(Color.RED);
        err.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");

        Runnable refreshCards = () -> {
            for (int i = 0; i < cards.size(); i++) {
                boolean on = powerPlantPickIndices.contains(i);
                cards.get(i).setStyle(on ? selStyle : normStyle);
            }
        };

        HBox plantsRow = new HBox(18);
        plantsRow.setAlignment(Pos.CENTER);

        for (int i = 0; i < plants.size(); i++) {
            final int idx = i;
            PowerPlant pl = plants.get(i);
            boolean hasEnough = pl.isEcological() || pl.getProducibleCities() > 0;

            StackPane card = new StackPane();
            card.setPrefSize(OWNED_PLANT_SLOT, OWNED_PLANT_SLOT + 28);
            card.setMinSize(OWNED_PLANT_SLOT, OWNED_PLANT_SLOT + 28);
            card.setMaxSize(OWNED_PLANT_SLOT, OWNED_PLANT_SLOT + 28);
            card.setStyle(normStyle);

            ImageView iv = new ImageView(loadCardImage(String.valueOf(pl.getNumber())));
            iv.setFitWidth(OWNED_PLANT_IMAGE); iv.setFitHeight(OWNED_PLANT_IMAGE);
            iv.setPreserveRatio(true); iv.setSmooth(true); iv.setMouseTransparent(true);

            card.getChildren().add(iv);

            if (!pl.isEcological()) {
                int stored = pl.getTotalStored(), req = pl.getResourcesRequired();
                Label res = new Label(stored + " / " + req + " fuel");
                res.setTextFill(hasEnough ? Color.LIGHTGREEN : Color.SALMON);
                res.setStyle("-fx-font-size:11px;-fx-font-weight:bold;");
                res.setMouseTransparent(true);
                StackPane.setAlignment(res, Pos.BOTTOM_CENTER);
                StackPane.setMargin(res, new Insets(0, 0, 3, 0));
                card.getChildren().add(res);
            }

            if (!hasEnough) {
                card.setOpacity(0.4);
            } else {
                card.setCursor(Cursor.HAND);
                card.setOnMouseClicked(ev -> {
                    if (powerPlantPickIndices.contains(idx)) powerPlantPickIndices.remove(idx);
                    else powerPlantPickIndices.add(idx);
                    err.setText("");
                    refreshCards.run();
                });
            }

            cards.add(card);
            plantsRow.getChildren().add(card);
        }

        String greenBtn = "-fx-background-color:#2e7d32;-fx-text-fill:white;-fx-font-weight:bold;"
                        + "-fx-font-size:14px;-fx-padding:10 32;-fx-background-radius:8;";
        Button confirm = new Button("Confirm Selection");
        confirm.setStyle(greenBtn);
        confirm.setCursor(Cursor.HAND);
        confirm.setOnAction(e -> {
            err.setText("");
            if (!selectedPlantsHaveEnoughFuel(powerWizardPlayer, powerPlantPickIndices)) {
                err.setText("Not enough resources!"); return;
            }
            powerWizardPane = PowerWizardPane.SELECT_CITIES;
            root.getChildren().clear();
            buildSelectCitiesPane(root, overlay);
        });

        VBox box = new VBox(20, title, plantsRow, confirm, err);
        box.setAlignment(Pos.CENTER);
        root.getChildren().setAll(box);
    }

    private void buildSelectCitiesPane(VBox root, StackPane overlay) {
        powerWizardPane = PowerWizardPane.SELECT_CITIES;
        powerCityPickSet.clear();

        int cap = maxCitiesSelectableForPower(powerWizardPlayer, powerPlantPickIndices);

        Label title = new Label("Select cities to power");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size:20px;-fx-font-weight:bold;");

        Label capHint = new Label("Can power up to " + cap + (cap == 1 ? " city" : " cities"));
        capHint.setTextFill(Color.LIGHTGRAY);
        capHint.setStyle("-fx-font-size:13px;");

        List<City> ownedCities = new ArrayList<>(game.getCitiesForPlayer(powerWizardPlayer));
        ownedCities.sort(Comparator.comparing(City::getName, String.CASE_INSENSITIVE_ORDER));

        String cityNorm = "-fx-background-color:#444;-fx-text-fill:white;-fx-background-radius:8;"
                        + "-fx-border-color:transparent;-fx-border-width:2;-fx-border-radius:8;"
                        + "-fx-padding:8 18;-fx-font-size:13px;";
        String citySel  = "-fx-background-color:#1a3d1a;-fx-text-fill:white;-fx-background-radius:8;"
                        + "-fx-border-color:limegreen;-fx-border-width:2;-fx-border-radius:8;"
                        + "-fx-padding:8 18;-fx-font-size:13px;";

        Map<City, Button> cityToBtn = new LinkedHashMap<>();

        Label tooMany = new Label("Too many cities selected!");
        tooMany.setTextFill(Color.RED);
        tooMany.setStyle("-fx-font-weight:bold;-fx-font-size:14px;");
        tooMany.setVisible(false);

        Runnable refreshPicks = () -> {
            for (Map.Entry<City, Button> e : cityToBtn.entrySet()) {
                e.getValue().setStyle(powerCityPickSet.contains(e.getKey()) ? citySel : cityNorm);
            }
            tooMany.setVisible(powerCityPickSet.size() > cap);
        };

        FlowPane cityFlow = new FlowPane(10, 10);
        cityFlow.setAlignment(Pos.CENTER);
        cityFlow.setPrefWrapLength(620);

        for (City city : ownedCities) {
            Button b = new Button(city.getName());
            b.setStyle(cityNorm);
            b.setCursor(Cursor.HAND);
            b.setOnAction(ev -> {
                if (powerCityPickSet.contains(city)) powerCityPickSet.remove(city);
                else powerCityPickSet.add(city);
                refreshPicks.run();
            });
            cityToBtn.put(city, b);
            cityFlow.getChildren().add(b);
        }

        if (ownedCities.isEmpty()) {
            Label none = new Label("No cities owned.");
            none.setTextFill(Color.LIGHTGRAY);
            cityFlow.getChildren().add(none);
        }

        ScrollPane cs = new ScrollPane(cityFlow);
        cs.setFitToWidth(true); cs.setPrefViewportHeight(220);
        cs.setStyle("-fx-background:transparent;-fx-background-color:transparent;");

        String greenBtn = "-fx-background-color:#2e7d32;-fx-text-fill:white;-fx-font-weight:bold;"
                        + "-fx-font-size:14px;-fx-padding:10 32;-fx-background-radius:8;";
        Button confirm = new Button("Confirm Selection");
        confirm.setStyle(greenBtn);
        confirm.setCursor(Cursor.HAND);
        confirm.setOnAction(e -> {
            if (powerCityPickSet.size() > cap) return;
            int pay = game.getElektroForCitiesPowered(powerCityPickSet.size());
            applyPowerConsumption(powerWizardPlayer, powerPlantPickIndices, powerCityPickSet.size());
            powerWizardPlayer.receive(pay); powerCityPickSet.clear();
            powerWizardPane = PowerWizardPane.EARNINGS;
            root.getChildren().clear();
            Label earn = new Label("You earned " + pay + " Elektros!");
            earn.setTextFill(Color.WHITE); earn.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");
            Button ok = new Button("Confirm");
            ok.setStyle(greenBtn);
            ok.setCursor(Cursor.HAND);
            VBox green = new VBox(20, earn, ok); green.setAlignment(Pos.CENTER);
            green.setStyle("-fx-background-color:#1a4d1a;-fx-padding:36;-fx-background-radius:12;"
                         + "-fx-border-color:#2e7d32;-fx-border-width:2;-fx-border-radius:12;");
            green.setMaxWidth(420);
            root.getChildren().add(green);
            ok.setOnAction(ev -> { rootStack.getChildren().remove(overlay); powerWizardOverlay = null; endPowerWizardAndAdvance(); });
        });

        VBox box = new VBox(14, title, capHint, cs, tooMany, confirm);
        box.setAlignment(Pos.CENTER);
        root.getChildren().setAll(box);
    }

    private void endPowerWizardAndAdvance() {
        powerWizardPane=PowerWizardPane.IDLE; powerWizardPlayer=null;
        powerPlantPickIndices.clear(); powerCityPickSet.clear();
        refreshOwnedAndMaterials();
        powerTurnIdx++;
        if (powerTurnIdx>=turnOrder.size()) {
            powerPhaseActive=false;
            game.endOfRound();
            TurnOrder.sortByCitiesThenLargestPlant(turnOrder,game);
            refreshTurnOrderSlots(); refreshTopRightHud(); refreshOwnedAndMaterials();
            showRoundInterstitial(); return;
        }
        currentTurnIdx=powerTurnIdx; viewingPlayerIdx=powerTurnIdx;
        refreshOwnedAndMaterials();
        // Power tab still bouncing — next player clicks it
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ROUND INTERSTITIAL
    // ═════════════════════════════════════════════════════════════════════════

    private void showRoundInterstitial() {
        StackPane overlay=new StackPane(); overlay.setStyle("-fx-background-color:rgba(0,0,0,0.92);");
        VBox box=new VBox(28); box.setAlignment(Pos.CENTER);
        Label title=new Label("Round "+game.getRound()); title.setTextFill(Color.LIMEGREEN);
        try { var url=getClass().getResource("/universcondensed_medium.ttf");
              title.setFont(url!=null?Font.loadFont(url.toExternalForm(),56):Font.font(56));
        } catch(Exception ignored){title.setStyle("-fx-font-size:56px;");}
        Label head=new Label("Turn order"); head.setTextFill(Color.WHITE); head.setStyle("-fx-font-size:16px;");
        HBox orderRow=new HBox(12); orderRow.setAlignment(Pos.CENTER);
        for(int i=0;i<turnOrder.size();i++){Player pl=turnOrder.get(i); VBox col=new VBox(6); col.setAlignment(Pos.CENTER);
            Label nm=new Label((i+1)+". "+pl.getDisplayName()); nm.setTextFill(Color.WHITE);
            col.getChildren().addAll(new Rectangle(36,36,Color.web(pl.getColorHex())),nm); orderRow.getChildren().add(col);}
        Label hint=new Label("Click anywhere to proceed"); hint.setTextFill(Color.GRAY);
        box.getChildren().addAll(title,head,orderRow,hint); overlay.getChildren().add(box);
        overlay.setOnMouseClicked(e->{
            rootStack.getChildren().remove(overlay);
            phase="Power Plant Auction"; currentTurnIdx=0; viewingPlayerIdx=0;
            initAuctionRoundState(); refreshOwnedAndMaterials();
            startBounce("Power Plant Auction");
        });
        rootStack.getChildren().add(overlay);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  AUCTION LOGIC (unchanged)
    // ═════════════════════════════════════════════════════════════════════════

    private void finalizeCurrentAuction() {
        if (highestBidderIdx<0||activeAuctionCardId==null) return;
        Player winner=turnOrder.get(highestBidderIdx);
        if (!winner.pay(highestBid)) { bidStatus.setText("Auction failed: winner cannot pay."); auctionInProgress=false; activeAuctionCardId=null; return; }
        addPurchasedPlant(winner,activeAuctionCardId);
        boughtThisAuctionRound.set(highestBidderIdx,true);
        String wonCard=activeAuctionCardId;
        String drawn=deck.takeFromMarket(activeAuctionCardId);
        if ("STEP3".equals(drawn)) game.enterStep3Immediately();
        bidStatus.setText(winner.getDisplayName()+" wins "+wonCard+" for "+highestBid);
        if (auctionBidPanelBox!=null){auctionBidPanelBox.setVisible(false); auctionBidPanelBox.setManaged(false);}
        activeAuctionCardId=null;

        int nextChooser;
        if (auctionStarterIdx>=0&&!boughtThisAuctionRound.get(auctionStarterIdx)&&!optedOutThisAuctionRound.get(auctionStarterIdx))
            nextChooser=auctionStarterIdx;
        else nextChooser=nextChooserFrom(auctionStarterIdx>=0?auctionStarterIdx:highestBidderIdx);

        if (nextChooser<0) { enterResourcePurchasePhase(); return; }
        currentTurnIdx=nextChooser; viewingPlayerIdx=nextChooser;
        auctionInProgress=false; highestBid=0; highestBidderIdx=-1;
        for(int i=0;i<passedThisAuction.size();i++) passedThisAuction.set(i,false);
        skipBoughtPlayersInAuction();
        refreshCardButtons(); refreshOwnedAndMaterials();
    }

    private void skipBoughtPlayersInAuction() {
        if (turnOrder.isEmpty()||!"Power Plant Auction".equals(phase)) return;
        int guard=0;
        while(guard<turnOrder.size()&&boughtThisAuctionRound.get(currentTurnIdx)){currentTurnIdx=(currentTurnIdx+1)%turnOrder.size(); guard++;}
        viewingPlayerIdx=currentTurnIdx;
        if (auctionPlayerTitle!=null&&!turnOrder.isEmpty())
            auctionPlayerTitle.setText(turnOrder.get(currentTurnIdx).getDisplayName()+" — choose a power plant");
        refreshOwnedAndMaterials();
    }

    private void initAuctionRoundState() {
        boughtThisAuctionRound.clear(); optedOutThisAuctionRound.clear(); passedThisAuction.clear();
        for(int i=0;i<turnOrder.size();i++){boughtThisAuctionRound.add(false); optedOutThisAuctionRound.add(false); passedThisAuction.add(false);}
        auctionInProgress=false; auctionStarterIdx=-1; highestBid=0; highestBidderIdx=-1; activeAuctionCardId=null;
    }

    private int nextChooserFrom(int from) {
        int n=turnOrder.size(); if(n==0) return -1;
        for(int s=1;s<=n;s++){int idx=(from+s)%n; if(!boughtThisAuctionRound.get(idx)&&!optedOutThisAuctionRound.get(idx)) return idx;}
        return -1;
    }

    private int nextBidderFrom(int from) {
        if(turnOrder.isEmpty()) return -1;
        int activeCount=0, lastActive=-1;
        for(int i=0;i<turnOrder.size();i++) if(!passedThisAuction.get(i)&&canParticipateInAuction(i)){activeCount++; lastActive=i;}
        if(activeCount<=1) return -1;
        int n=turnOrder.size();
        for(int s=1;s<=n;s++){int idx=(from+s)%n; if(!passedThisAuction.get(idx)&&canParticipateInAuction(idx)) return idx;}
        return lastActive;
    }

    private boolean canParticipateInAuction(int idx) {
        if(idx<0||idx>=turnOrder.size()) return false;
        return !boughtThisAuctionRound.get(idx)&&!optedOutThisAuctionRound.get(idx);
    }

    private void applySuggestedBidForCurrentTurn() {
        if(auctionBidField==null||activeAuctionCardId==null) return;
        auctionBidField.setText(String.valueOf(Math.max(highestBid+1,Integer.parseInt(activeAuctionCardId))));
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  UTILITIES
    // ═════════════════════════════════════════════════════════════════════════

    private void addPurchasedPlant(Player player, String cardId) {
        PowerPlantCard card=PowerPlantCard.byNumber(Integer.parseInt(cardId)); if(card==null) return;
        if(player.getPowerPlants().size()>=Player.MAX_POWER_PLANTS) player.removePowerPlant(player.getPowerPlants().get(0));
        player.addPowerPlant(card.toPowerPlant());
    }

    private boolean canStoreResource(Player player, ResourceType type) {
        for(PowerPlant p:player.getPowerPlants()) if(p.canStore(type)&&p.getTotalStored()<p.getStorageCapacity()) return true;
        return false;
    }

    private void storeResource(Player player, ResourceType type) {
        for(PowerPlant p:player.getPowerPlants()) if(p.canStore(type)&&p.getTotalStored()<p.getStorageCapacity()){p.addStored(type,1); return;}
    }

    private Image loadCardImage(String id) {
        return cardImages.computeIfAbsent(id, k->new Image(getClass().getResource("/PowerPlants/"+k+".png").toExternalForm()));
    }

    private String occupyingText(City city) {
        List<String> names=new ArrayList<>();
        for(int pid:city.getOccupyingPlayers()){Player p=game.getPlayer(pid); if(p!=null) names.add(p.getDisplayName());}
        return names.isEmpty()?"None":String.join(", ",names);
    }

    private void updateCityStroke(City city) {
        List<Color> colors=new ArrayList<>();
        for(int pid:city.getOccupyingPlayers()){Player p=game.getPlayer(pid); if(p!=null) colors.add(Color.web(p.getColorHex()));}
        city.updateOccupancyStroke(colors);
        if(!city.isInteractable()&&colors.isEmpty())
            city.getVisualNode().getChildren().filtered(n->n instanceof Circle).forEach(n->((Circle)n).setStroke(Color.TRANSPARENT));
    }

    private void showOutOfMoneyPopup() {
        StackPane overlay=new StackPane(); overlay.setStyle("-fx-background-color:rgba(0,0,0,0.5);");
        VBox box=new VBox(12); box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color:#8B2222;-fx-padding:24;-fx-background-radius:12;");
        Label msg=new Label("You have ran out of money!"); msg.setTextFill(Color.WHITE);
        Button ok=new Button("Confirm"); ok.setOnAction(e->rootStack.getChildren().remove(overlay));
        box.getChildren().addAll(msg,ok); overlay.getChildren().add(box); rootStack.getChildren().add(overlay);
    }

    private void clearConnectionPath() { if(connectionPathGroup!=null) connectionPathGroup.getChildren().clear(); }
    private void clearCityPopup()      { if(cityPopupGroup!=null)      cityPopupGroup.getChildren().clear(); }

    private void resetToCenter() {
        if(imageView==null||mapContainer==null) return;
        double iw=imageView.getImage().getWidth(), ih=imageView.getImage().getHeight();
        transform.setToIdentity();
        transform.appendTranslation((mapContainer.getWidth()-iw)/2,(mapContainer.getHeight()-ih)/2);
    }

    private int firstNonEmptySlotIndex(List<ResourceMarket.Slot> slots) {
        for(int i=0;i<slots.size();i++) if(slots.get(i).getCount()>0) return i;
        return -1;
    }

    private int maxCitiesSelectableForPower(Player player, Set<Integer> plantIndices) {
        int sum=0; List<PowerPlant> plants=player.getPowerPlants();
        for(int i:plantIndices) if(i>=0&&i<plants.size()) sum+=plants.get(i).getProducibleCities();
        return Math.min(sum,game.getConnectedCityCount(player));
    }

    private boolean selectedPlantsHaveEnoughFuel(Player player, Set<Integer> plantIndices) {
        List<PowerPlant> plants=player.getPowerPlants();
        for(int i:plantIndices){if(i<0||i>=plants.size()) continue; PowerPlant pl=plants.get(i); if(!pl.isEcological()&&pl.getProducibleCities()<=0) return false;}
        return true;
    }

    private void applyPowerConsumption(Player player, Set<Integer> plantIndices, int cities) {
        if(cities<=0||plantIndices.isEmpty()) return;
        List<Integer> idxList=new ArrayList<>(plantIndices); Collections.sort(idxList);
        int remaining=cities;
        for(int i:idxList){if(remaining<=0) break; if(i<0||i>=player.getPowerPlants().size()) continue;
            PowerPlant pl=player.getPowerPlants().get(i); int take=Math.min(remaining,pl.getProducibleCities());
            if(take<=0) continue; remaining-=take; consumeOnePlantActivation(pl);}
    }

    private void consumeOnePlantActivation(PowerPlant plant) {
        if(plant.isEcological()) return;
        int need=plant.getResourcesRequired();
        ResourceMarket market=game.getResourceMarket();
        if(plant.isHybrid()){
            int coal=plant.getStoredResources().getOrDefault(ResourceType.COAL,0), take=Math.min(need,coal);
            plant.removeStored(ResourceType.COAL,take);
            if(take>0) market.returnToSupply(ResourceType.COAL,take);
            int rest=need-take;
            if(rest>0){
                int oil=Math.min(rest,plant.getStoredResources().getOrDefault(ResourceType.OIL,0));
                plant.removeStored(ResourceType.OIL,oil);
                if(oil>0) market.returnToSupply(ResourceType.OIL,oil);
            }
        } else {
            ResourceType rt=plant.getPrimaryResource();
            if(rt!=null){
                plant.removeStored(rt,need);
                market.returnToSupply(rt,need);
            }
        }
    }

    private static String powerPlantSelectionLine(PowerPlant pl) {
        int r=pl.getResourcesRequired(), c=pl.getCitiesSupplied(), s=pl.getTotalStored();
        if(pl.isEcological()) return String.format("#%d (eco) — %d/run — up to %d cities — %d stored",pl.getNumber(),r,c,s);
        if(pl.isHybrid())     return String.format("#%d (hybrid) — %d/run — up to %d cities — %d stored",pl.getNumber(),r,c,s);
        String fuel=pl.getPrimaryResource()!=null?pl.getPrimaryResource().name().toLowerCase():"";
        return String.format("#%d (%s) — %d/run — up to %d cities — %d stored",pl.getNumber(),fuel,r,c,s);
    }

    private static String formatResourcePoolDescription(String prefix, Map<ResourceType,Integer> pool) {
        StringBuilder sb=new StringBuilder(prefix); boolean any=false;
        for(ResourceType t:ResourceType.values()){int c=pool.getOrDefault(t,0); if(c>0){if(any) sb.append(", "); sb.append(t.name().toLowerCase()).append(" ").append(c); any=true;}}
        if(!any) sb.append("none"); return sb.toString();
    }

    private static boolean committedAllocationsMatchPool(Map<ResourceType,Integer> pool, List<EnumMap<ResourceType,Integer>> committed) {
        EnumMap<ResourceType,Integer> sum=new EnumMap<>(ResourceType.class);
        for(EnumMap<ResourceType,Integer> m:committed) for(Map.Entry<ResourceType,Integer> e:m.entrySet()) sum.merge(e.getKey(),e.getValue(),Integer::sum);
        for(ResourceType t:ResourceType.values()) if(sum.getOrDefault(t,0)!=pool.getOrDefault(t,0)) return false;
        return true;
    }

    public String getGreeting() { return "Welcome to Power Grid!"; }
    public static void main(String[] args) { launch(args); }
}
