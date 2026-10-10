import java.util.*;

public class Player {
    private int actionCubes;
    private final Board board;
    private final ArrayList<BirdCard> birdCards;
    private final ArrayList<BonusCard> bonusCards;
    private final int[] foodTokens;
    public final Game game;

    public Player(Game game) {
        this.game = game;
        this.board = new Board(this);
        this.birdCards = new ArrayList<>();
        this.bonusCards = new ArrayList<>();
        this.foodTokens = new int[6]; // berry, fish, rodent, wheat, worm, wildcard
    }

    public Board getBoard() {
        return board;
    }

    public java.util.List<BonusCard> getBonusCards() {
        return bonusCards;
    }

    public ArrayList<BirdCard> getHand() {
        return birdCards;
    }

    public int[] getFood() {
        return foodTokens;
    }

    public BirdCard getBirdFromHand(int index) {
        return birdCards.get(index);
    }

    public void addBird(BirdCard c) {
        if (c != null) birdCards.add(c);
    }

    public void addBonus(BonusCard c) {
        if (c != null) bonusCards.add(c);
    }

    public void addFood(int type, int delta) {
        if (type < 0 || type >= foodTokens.length) return;
        foodTokens[type] = Math.max(0, foodTokens[type] + delta);
    }

    public void discardFood(int type, int amount) {
        addFood(type, -amount);
    }

    public void discardCard(int index) {
        if (index < 0 || index >= birdCards.size()) return;
        birdCards.remove(index);
    }

    public int bonusAction(BonusCard bonus) {
        ArrayList<BirdCard> playedBirds = board.getAllPlayedBirds();
        
        return switch (bonus.name) {
            case "Photographer" -> scorePhotographer(playedBirds);
            case "Anatomist" -> scoreAnatomist(playedBirds);
            case "Cartographer" -> scoreCartographer(playedBirds);
            case "Historian" -> scoreHistorian(playedBirds);
            case "Wildlife Gardener" -> scoreWildlifeGardener(playedBirds);
            case "Platform Builder" -> scorePlatformBuilder();
            case "Enclosure Builder" -> scoreEnclosureBuilder();
            case "Nest Box Builder" -> scoreNestBoxBuilder();
            case "Breeding Manager" -> scoreBreedingManager();
            case "Oologist" -> scoreOologist();
            case "Forester" -> board.countBirdsInHabitat(0);
            case "Prairie Manager" -> board.countBirdsInHabitat(1);
            case "Wetland Scientist" -> board.countBirdsInHabitat(2);
            case "Ecologist" -> scoreEcologist();
            case "Bird Counter" -> board.getTotalBirdCount();
            case "Falconer" -> scoreFalconer(playedBirds);
            case "Large Bird Specialist" -> scoreLargeBirdSpecialist(playedBirds);
            case "Passerine Specialist" -> scorePasserineSpecialist(playedBirds);
            case "Backyard Birder" -> scoreBackyardBirder(playedBirds);
            case "Visionary Leader" -> scoreVisionaryLeader(playedBirds);
            default -> 0;
        };
    }

    private int scorePhotographer(ArrayList<BirdCard> birds) {
        // Birds with colors in their names
        String[] colors = {"ash", "chestnut", "lazuli", "ruddy", "black", "ferruginous", "purple", "rufous",
                          "blue", "gold", "red", "snowy", "bronze", "gray", "rose", "white", "brown", "green",
                          "roseate", "yellow", "cerulean", "indigo", "ruby"};
        int count = 0;
        for (BirdCard bird : birds) {
            String name = bird.name.toLowerCase();
            for (String color : colors) {
                if (name.contains(color)) {
                    count++;
                    break; // Count each bird only once
                }
            }
        }
        return count;
    }

    private int scoreAnatomist(ArrayList<BirdCard> birds) {
        // Birds with body parts in their names
        String[] parts = {"beak", "bill", "chin", "face", "head", "shoulder", "belly", "breast", "collar",
                         "crest", "crown", "eye", "neck", "rump", "tail", "throat", "wing", "cap"};
        int count = 0;
        for (BirdCard bird : birds) {
            String name = bird.name.toLowerCase();
            for (String part : parts) {
                if (name.contains(part)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private int scoreCartographer(ArrayList<BirdCard> birds) {
        // Birds with geography terms in their names
        String[] geos = {"american", "atlantic", "baltimore", "california", "canada", "carolina", "chihuahua",
                        "mountain", "eastern", "northern", "inca", "kentucky", "mississippi", "sandhill",
                        "savannah", "western"};
        int count = 0;
        for (BirdCard bird : birds) {
            String name = bird.name.toLowerCase();
            for (String geo : geos) {
                if (name.contains(geo)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private int scoreHistorian(ArrayList<BirdCard> birds) {
        // Birds with 's in their name
        int count = 0;
        for (BirdCard bird : birds) {
            if (bird.name.contains("'s") || bird.name.contains("'")) count++;
        }
        return count;
    }

    private int scoreWildlifeGardener(ArrayList<BirdCard> birds) {
        // Birds with flowers/plants in name - simplified check
        String[] plants = {"rose", "roseate"};
        int count = 0;
        for (BirdCard bird : birds) {
            String name = bird.name.toLowerCase();
            for (String plant : plants) {
                if (name.contains(plant)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private int scorePlatformBuilder() {
        return board.countBirdsWithNestAndEgg(Nest.PLATFORM);
    }

    private int scoreEnclosureBuilder() {
        return board.countBirdsWithNestAndEgg(Nest.GROUND);
    }

    private int scoreNestBoxBuilder() {
        return board.countBirdsWithNestAndEgg(Nest.CAVITY);
    }

    private int scoreBreedingManager() {
        return board.countBirdsWithNestAndEgg(Nest.BOWL);
    }

    private int scoreOologist() {
        // Count sets of eggs (1 in forest, 1 in prairie, 1 in wetlands)
        return board.countSetsOfEggs();
    }

    private int scoreEcologist() {
        // Count birds with different nest types
        boolean hasBowl = board.countBirdsWithNest(Nest.BOWL) > 0;
        boolean hasPlatform = board.countBirdsWithNest(Nest.PLATFORM) > 0;
        boolean hasCavity = board.countBirdsWithNest(Nest.CAVITY) > 0;
        boolean hasGround = board.countBirdsWithNest(Nest.GROUND) > 0;
        int nestTypes = 0;
        if (hasBowl) nestTypes++;
        if (hasPlatform) nestTypes++;
        if (hasCavity) nestTypes++;
        if (hasGround) nestTypes++;
        return nestTypes * 3;
    }

    private int scoreFalconer(ArrayList<BirdCard> birds) {
        // Birds with wingspan 65cm or more
        int count = 0;
        for (BirdCard bird : birds) {
            if (bird.wingspan >= 65) count++;
        }
        return count;
    }

    private int scoreLargeBirdSpecialist(ArrayList<BirdCard> birds) {
        // Birds with wingspan 100cm or more
        int count = 0;
        for (BirdCard bird : birds) {
            if (bird.wingspan >= 100) count++;
        }
        return count;
    }

    private int scorePasserineSpecialist(ArrayList<BirdCard> birds) {
        // Passerines typically have wingspan under 30cm - simplified
        int count = 0;
        for (BirdCard bird : birds) {
            if (bird.wingspan < 30) count++;
        }
        return count;
    }

    private int scoreBackyardBirder(ArrayList<BirdCard> birds) {
        // Birds with wingspan under 30cm
        int count = 0;
        for (BirdCard bird : birds) {
            if (bird.wingspan < 30) count++;
        }
        return count;
    }

    private int scoreVisionaryLeader(ArrayList<BirdCard> birds) {
        // Birds with wingspan 65cm or more
        int count = 0;
        for (BirdCard bird : birds) {
            if (bird.wingspan >= 65) count++;
        }
        return count;
    }

    public void drawBird() {
        addBird(game.drawBird());
    }

    public void drawBonus() {
        addBonus(game.drawBonus());
    }

    public void endTurn() {
        actionCubes--;
        game.nextTurn();
    }

    public void requestFood(int n) {
        game.requestFood(n);
    }

    public void selectSpots(int n, int m) {
        game.selectSpots(n, m);
    }

    public void drawBirdChoice(int n) {
        game.drawBirdChoice(n);
    }

    public void showError(String msg) {
        game.showError(msg);
    }

    public boolean foodEnoughFor(BirdCard card, int[] food) {
        if (card == null) return false;
        int sum = 0;
        for (int value : food) sum += value;
        for (int i = 0; i < card.foodCost.length - 1; i++) {
            if (i >= food.length) return false;
            if (food[i] < card.foodCost[i]) {
                return false;
            }
            sum -= card.foodCost[i];
        }
        return sum >= card.foodCost[card.foodCost.length - 1];
    }

    public boolean foodEnoughFor(BirdCard card) {
        return foodEnoughFor(card, foodTokens);
    }

    public int getActionCubes() {
        return actionCubes;
    }

    public void placeAction(int habitat) {
        board.placeAction(habitat);
    }

    public int playCard(BirdCard card, ArrayList<Integer> stats) {
        int flag = board.playCard(card, stats);
        if (flag == 1) {
            birdCards.remove(card);
            // If no eggs were required (first column), end turn immediately.
            int habitat = stats.get(stats.size() - 1);
            if (board.columnEggCost(Math.max(0, board.lastUnoccupiedColumn(habitat) - 1)) == 0) {
                endTurn();
            }
        }
        return flag;
    }

    public void setActionCubes(int a) {
        actionCubes = a;
    }

    public void tradeFood(int give, int get) {
        if (give < 0 || give >= foodTokens.length || get < 0 || get >= foodTokens.length) return;
        if (foodTokens[give] == 0) return;
        foodTokens[give]--;
        foodTokens[get]++;
    }

    private int preferredFoodType(BirdCard card) {
        for (int i = 0; i < card.foodCost.length - 1; i++) {
            if (card.foodCost[i] > 0) return i;
        }
        return 0;
    }

    private Spot findSpotWithSpace(int habitatMask) {
        for (Spot[] row : board.grid) {
            for (Spot s : row) {
                if (s.occupied() && (s.getHabitat() & habitatMask) != 0 && s.getEggCount() < s.getCard().eggMax) {
                    return s;
                }
            }
        }
        return null;
    }

    private int mostAbundantFoodIndex() {
        int best = -1;
        int qty = 0;
        for (int i = 0; i < foodTokens.length; i++) {
            if (foodTokens[i] > qty) {
                qty = foodTokens[i];
                best = i;
            }
        }
        return best;
    }

    private int leastFoodIndex() {
        int best = 0;
        int qty = Integer.MAX_VALUE;
        for (int i = 0; i < foodTokens.length; i++) {
            if (foodTokens[i] < qty) {
                qty = foodTokens[i];
                best = i;
            }
        }
        return best;
    }

    private void tuckFromHand(Spot spot) {
        if (birdCards.isEmpty()) return;
        spot.tuckCard(birdCards.remove(0));
    }

    private int habitatRowFor(BirdCard card) {
        if (card.canInhabit(0)) return 0;
        if (card.canInhabit(1)) return 1;
        return 2;
    }

    // Find the first occupied spot with a matching nest that can take another egg.
    public Spot findFirstSpotWithNest(Nest nest) {
        for (Spot[] row : board.grid) {
            for (Spot s : row) {
                if (!s.occupied()) continue;
                if (nest == Nest.ANY || s.getCard().nest == nest || s.getCard().nest == Nest.ANY) {
                    if (s.getEggCount() < s.getCard().eggMax) return s;
                }
            }
        }
        return null;
    }

    private int predatorThreshold(BirdCard card) {
        // Rough approximation: larger predators want bigger prey thresholds.
        return switch (card.name) {
            case "Golden Eagle", "Great Horned Owl", "Peregrine Falcon" -> 100;
            default -> 75;
        };
    }

    private boolean promptSelectCardFromHand(String message, java.util.function.Consumer<BirdCard> onSelect) {
        if (birdCards.isEmpty()) return false;
        Prompt p = new Prompt(message, birdCards.toArray());
        game.setPanel(p);
        p.setCallback((obj) -> {
            BirdCard chosen = (BirdCard)obj;
            game.setPanel(game.getGamePanel());
            onSelect.accept(chosen);
        });
        return true;
    }

    private boolean promptSelectFood(String message, java.util.List<Integer> choices, java.util.function.Consumer<Integer> onSelect) {
        if (choices.isEmpty()) return false;
        Prompt p = new Prompt(message, choices.toArray());
        game.setPanel(p);
        p.setCallback((obj) -> {
            int idx = (Integer)obj;
            game.setPanel(game.getGamePanel());
            onSelect.accept(idx);
        });
        return true;
    }

    // Returns true if action flows asynchronously (UI); calls continueAction when it finishes.
    public boolean runAction(int id, Spot spot, Runnable continueAction) {
        BirdFeeder feeder = game.getBirdFeeder();
        BirdCard card = spot.getCard();
        switch (id) {
            case 0 -> { // Gain 1 food from feeder if possible
                for (int type = 0; type < 6; type++) {
                    if (feeder.hasDieValue(type)) {
                        feeder.removeDieByVal(type);
                        addFood(Math.min(type, foodTokens.length - 1), 1);
                        break;
                    }
                }
                if (continueAction != null) continueAction.run();
                return false;
            }
            case 1 -> { // Gain all dice of one type from feeder - user chooses type
                java.util.List<Integer> availableTypes = new ArrayList<>();
                for (int type = 0; type < 6; type++) {
                    if (feeder.hasDieValue(type)) {
                        availableTypes.add(type);
                    }
                }
                if (availableTypes.isEmpty()) {
                    if (continueAction != null) continueAction.run();
                    return false;
                }
                if (promptSelectFood("Choose food type to gain all dice of", availableTypes, (type) -> {
                    int gained = feeder.takeAllOfType(type);
                    addFood(Math.min(type, foodTokens.length - 1), gained);
                    game.setPanel(game.getGamePanel());
                    if (continueAction != null) continueAction.run();
                })) return true; // Async - continueAction will be called in callback
                // Should not reach here
                if (continueAction != null) continueAction.run();
            }
            case 2 -> { // Each player gains 1 food from feeder starting with this player
                Player[] players = game.getPlayers();
                Prompt chooseStart = new Prompt("Choose starting player", players);
                chooseStart.setCallback((p) -> {
                    int startIdx = game.getPlayerIndex((Player)p);
                    for (int i = 0; i < players.length; i++) {
                        int idx = (startIdx + i) % players.length;
                        for (int type = 0; type < 6; type++) {
                            if (feeder.hasDieValue(type)) {
                                feeder.removeDieByVal(type);
                                players[idx].addFood(Math.min(type, foodTokens.length - 1), 1);
                                break;
                            }
                        }
                    }
                    game.setPanel(game.getGamePanel());
                    if (continueAction != null) continueAction.run();
                });
                game.setPanel(chooseStart);
                return true;
            }
            case 3 -> { // Players with fewest forest birds gain 1 food
                int min = Integer.MAX_VALUE;
                int[] counts = new int[game.getPlayerCount()];
                for (int i = 0; i < counts.length; i++) {
                    int c = 0;
                    for (Spot s : game.getPlayer(i).getBoard().grid[0]) {
                        if (s.occupied()) c++;
                    }
                    counts[i] = c;
                    min = Math.min(min, c);
                }
                for (int i = 0; i < counts.length; i++) {
                    if (counts[i] != min) continue;
                    for (int type = 0; type < 6; type++) {
                        if (feeder.hasDieValue(type)) {
                            feeder.removeDieByVal(type);
                            game.getPlayer(i).addFood(type, 1);
                            break;
                        }
                    }
                }
                if (continueAction != null) continueAction.run();
                return false;
            }
            case 5 -> { // Gain 1 from supply
                addFood(preferredFoodType(card), 1);
                if (continueAction != null) continueAction.run();
            }
            case 6 -> { // Gain 3 from supply on play
                addFood(preferredFoodType(card), 3);
                if (continueAction != null) continueAction.run();
            }
            case 7 -> { // All players gain 1 from supply
                int type = preferredFoodType(card);
                for (Player p : game.getPlayers()) p.addFood(type, 1);
                if (continueAction != null) continueAction.run();
            }
            case 8 -> { // Trade 1 food for another
                // Prompt for which food to give and get
                java.util.List<Integer> available = new ArrayList<>();
                for (int i = 0; i < foodTokens.length; i++) if (foodTokens[i] > 0) available.add(i);
                if (available.isEmpty()) {
                    if (continueAction != null) continueAction.run();
                    return false;
                }
                Prompt chooseGive = new Prompt("Choose food to trade away", available.toArray());
                game.setPanel(chooseGive);
                chooseGive.setCallback((o) -> {
                    int give = (Integer)o;
                    Integer[] options = {0, 1, 2, 3, 4, 5};
                    Prompt chooseGet = new Prompt("Choose food to gain", options);
                    chooseGet.setCallback((oget) -> {
                        int get = (Integer)oget;
                        tradeFood(give, get);
                        game.setPanel(game.getGamePanel());
                        if (continueAction != null) continueAction.run();
                    });
                    game.setPanel(chooseGet);
                });
                return true;
            }
            case 9 -> { // Discard 1 egg to gain food
                if (spot.removeEgg()) {
                    int type = preferredFoodType(card);
                    addFood(type, 1);
                }
                if (continueAction != null) continueAction.run();
            }
            case 11 -> { // Cache wheat
                spot.addFood(new int[]{0, 0, 0, 1, 0, 0});
                if (continueAction != null) continueAction.run();
            }
            case 12 -> { // Cache from feeder if wheat available
                if (feeder.removeDieByVal(3)) {
                    spot.addFood(new int[]{0, 0, 0, 1, 0, 0});
                }
                if (continueAction != null) continueAction.run();
            }
            case 15 -> { // Add an egg to all birds with matching nest
                Nest nest = card.nest;
                for (Spot[] row : board.grid) {
                    for (Spot s : row) {
                        if (!s.occupied()) continue;
                        if (s.getCard().nest == nest || s.getCard().nest == Nest.ANY || nest == Nest.ANY) {
                            s.addEgg();
                        }
                    }
                }
                if (continueAction != null) continueAction.run();
            }
            case 16 -> { // Lay an egg on this bird and another matching habitat if possible
                // All players lay 1 egg on a bird with the same nest; current player may lay an extra.
                Nest nest = card.nest;
                for (Player p : game.getPlayers()) {
                    Spot target = p.findFirstSpotWithNest(nest);
                    if (target != null) target.addEgg();
                }
                Spot extra = findSpotWithSpace(card.habitats);
                if (extra != null && extra != spot) extra.addEgg();
                if (continueAction != null) continueAction.run();
            }
            case 17, 18 -> { // Lay 1 egg
                spot.addEgg();
                if (continueAction != null) continueAction.run();
            }
            case 20 -> { // Draw 2 birds
                drawBird();
                drawBird();
                if (continueAction != null) continueAction.run();
            }
            case 21 -> { // Draw 1 and discard 1 if possible
                drawBird();
                if (birdCards.size() > 1) {
                    if (promptSelectCardFromHand("Discard a card", (bc) -> {
                        birdCards.remove(bc);
                        if (continueAction != null) continueAction.run();
                    })) {
                        return true; // Async - continueAction will be called in callback
                    }
                }
                // If no prompt needed, run continueAction now
                if (continueAction != null) continueAction.run();
            }
            case 22 -> { // Draw 2 and discard 1
                drawBird();
                drawBird();
                if (!birdCards.isEmpty()) {
                    if (promptSelectCardFromHand("Discard a card", (bc) -> {
                        birdCards.remove(bc);
                        if (continueAction != null) continueAction.run();
                    })) {
                        return true; // Async - continueAction will be called in callback
                    }
                }
                // If no prompt needed, run continueAction now
                if (continueAction != null) continueAction.run();
            }
            case 23 -> { // Spend egg to draw 2
                if (spot.removeEgg()) {
                    drawBird();
                    drawBird();
                }
                if (continueAction != null) continueAction.run();
            }
            case 24 -> { // Each player draws 1
                for (Player p : game.getPlayers()) p.drawBird();
                if (continueAction != null) continueAction.run();
            }
            case 25 -> { // Fewest wetland birds draw 1
                int min = Integer.MAX_VALUE;
                int[] counts = new int[game.getPlayerCount()];
                for (int i = 0; i < counts.length; i++) {
                    int c = 0;
                    for (Spot s : game.getPlayer(i).getBoard().grid[2]) {
                        if (s.occupied()) c++;
                    }
                    counts[i] = c;
                    min = Math.min(min, c);
                }
                for (int i = 0; i < counts.length; i++) {
                    if (counts[i] == min) game.getPlayer(i).drawBird();
                }
                if (continueAction != null) continueAction.run();
            }
            case 26 -> { // Give everyone a new bird
                for (Player p : game.getPlayers()) p.drawBird();
                if (continueAction != null) continueAction.run();
            }
            case 28 -> { // Tuck a card and lay an egg
                if (!birdCards.isEmpty()) {
                    if (promptSelectCardFromHand("Choose a card to tuck", (bc) -> {
                        birdCards.remove(bc);
                        spot.tuckCard(bc);
                        spot.addEgg();
                        if (continueAction != null) continueAction.run();
                    })) return true; // Async - continueAction will be called in callback
                }
                // If no cards to tuck, run continueAction now
                if (continueAction != null) continueAction.run();
            }
            case 29, 30 -> { // Tuck and gain minor bonus
                if (!birdCards.isEmpty()) {
                    if (promptSelectCardFromHand("Choose a card to tuck", (bc) -> {
                        birdCards.remove(bc);
                        spot.tuckCard(bc);
                        addFood(preferredFoodType(card), 1);
                        if (continueAction != null) continueAction.run();
                    })) return true; // Async - continueAction will be called in callback
                }
                // If no cards to tuck, run continueAction now
                if (continueAction != null) continueAction.run();
            }
            case 31 -> { // Spend food to tuck 2 from deck
                java.util.List<Integer> avail = new ArrayList<>();
                for (int i = 0; i < foodTokens.length; i++) if (foodTokens[i] > 0) avail.add(i);
                if (avail.isEmpty()) {
                    // No food available, can't perform action
                    if (continueAction != null) continueAction.run();
                    break;
                }
                if (promptSelectFood("Choose food to discard", avail, (idx) -> {
                    discardFood(idx, 1);
                    BirdCard c1 = game.drawBird();
                    BirdCard c2 = game.drawBird();
                    if (c1 != null) spot.tuckCard(c1);
                    if (c2 != null) spot.tuckCard(c2);
                    if (continueAction != null) continueAction.run();
                })) return true; // Async - continueAction will be called in callback
                // Should not reach here, but just in case
                if (continueAction != null) continueAction.run();
            }
            case 32 -> { // Predator check
                BirdCard drawn = game.drawBird();
                if (drawn != null) {
                    if (drawn.wingspan < predatorThreshold(card)) spot.tuckCard(drawn);
                    // if larger, discard to graveyard (no effect)
                }
                if (continueAction != null) continueAction.run();
            }
            case 33 -> { // Cache fish if available - roll dice NOT in birdfeeder
                int[] rollResults = feeder.rollDiceNotInFeeder();
                if (feeder.rollContainsType(rollResults, 1)) {
                    // Gain 1 fish from supply and cache it on this card
                    spot.addFood(new int[]{0, 1, 0, 0, 0, 0});
                }
                if (continueAction != null) continueAction.run();
            }
            case 34 -> { // Cache rodent if available - roll dice NOT in birdfeeder
                int[] rollResults = feeder.rollDiceNotInFeeder();
                if (feeder.rollContainsType(rollResults, 2)) {
                    // Gain 1 rodent from supply and cache it on this card
                    spot.addFood(new int[]{0, 0, 1, 0, 0, 0});
                }
                if (continueAction != null) continueAction.run();
            }
            case 35 -> { // Move to another habitat - user chooses which habitat
                // Bird must be to the right of all other birds in its current habitat
                // Find current habitat
                int currentHabitat = -1;
                for (int h = 0; h < 3; h++) {
                    for (int c = 0; c < board.grid[h].length; c++) {
                        if (board.grid[h][c] == spot) {
                            currentHabitat = h;
                            break;
                        }
                    }
                    if (currentHabitat >= 0) break;
                }
                // Check if bird is rightmost in its habitat
                if (currentHabitat >= 0) {
                    boolean isRightmost = true;
                    for (int c = 0; c < board.grid[currentHabitat].length; c++) {
                        if (board.grid[currentHabitat][c] == spot) {
                            // Check if there are any birds to the right
                            for (int c2 = c + 1; c2 < board.grid[currentHabitat].length; c2++) {
                                if (board.grid[currentHabitat][c2].occupied()) {
                                    isRightmost = false;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (isRightmost) {
                        // Find available habitats (excluding current)
                        java.util.List<Integer> availableHabitats = new ArrayList<>();
                        for (int h = 0; h < 3; h++) {
                            if (h != currentHabitat && card.canInhabit(h)) {
                                if (board.lastUnoccupiedColumn(h) >= 0) {
                                    availableHabitats.add(h);
                                }
                            }
                        }
                        if (!availableHabitats.isEmpty()) {
                            // Prompt user to choose habitat
                            Integer[] habitatOptions = availableHabitats.toArray(new Integer[0]);
                            Prompt chooseHabitat = new Prompt("Choose habitat to move bird to", habitatOptions);
                            game.setPanel(chooseHabitat);
                            chooseHabitat.setCallback((obj) -> {
                                int targetHabitat = (Integer)obj;
                                board.moveBirdHabitat(spot, targetHabitat);
                                game.setPanel(game.getGamePanel());
                                if (continueAction != null) continueAction.run();
                            });
                            return true; // Async
                        }
                    }
                }
                // No valid move possible
                if (continueAction != null) continueAction.run();
            }
            case 38 -> { // Draw 2 bonus cards, keep 1
                BonusCard card1 = game.drawBonus();
                BonusCard card2 = game.drawBonus();
                if (card1 != null && card2 != null) {
                    Prompt chooseBonus = new Prompt("Choose bonus card to keep", new Object[]{card1, card2});
                    game.setPanel(chooseBonus);
                    chooseBonus.setCallback((obj) -> {
                        BonusCard chosen = (BonusCard)obj;
                        addBonus(chosen);
                        // Other card is discarded (not added to hand)
                        game.setPanel(game.getGamePanel());
                        if (continueAction != null) continueAction.run();
                    });
                    return true; // Async
                } else if (card1 != null) {
                    addBonus(card1);
                } else if (card2 != null) {
                    addBonus(card2);
                }
                if (continueAction != null) continueAction.run();
            }
            case 39 -> { // Play another bird from hand - user chooses which bird
                // Find affordable birds that can be played in the same habitat as the triggering card
                java.util.List<BirdCard> playableBirds = new ArrayList<>();
                final int[] habitatToMatch = {-1};
                // Find which habitat the current card is in
                for (int h = 0; h < 3; h++) {
                    for (int c = 0; c < board.grid[h].length; c++) {
                        if (board.grid[h][c] == spot) {
                            habitatToMatch[0] = h;
                            break;
                        }
                    }
                    if (habitatToMatch[0] >= 0) break;
                }
                
                for (BirdCard b : new ArrayList<>(birdCards)) {
                    if (b == card) continue; // Can't play the same card
                    if (!foodEnoughFor(b)) continue;
                    if (habitatToMatch[0] >= 0 && !b.canInhabit(habitatToMatch[0])) continue;
                    playableBirds.add(b);
                }
                
                if (!playableBirds.isEmpty()) {
                    Prompt chooseBird = new Prompt("Choose bird to play", playableBirds.toArray());
                    game.setPanel(chooseBird);
                    chooseBird.setCallback((obj) -> {
                        BirdCard chosen = (BirdCard)obj;
                        ArrayList<Integer> stats = new ArrayList<>();
                        for (int cost : chosen.foodCost) stats.add(cost);
                        // Use the same habitat as the triggering card
                        stats.add(habitatToMatch[0] >= 0 ? habitatToMatch[0] : habitatRowFor(chosen));
                        // playCard will handle its own on-play prompts if needed
                        int result = playCard(chosen, stats);
                        game.setPanel(game.getGamePanel());
                        if (continueAction != null) continueAction.run();
                    });
                    return true; // Async
                }
                // No playable birds
                if (continueAction != null) continueAction.run();
            }
            default -> {}
        }
        if (continueAction != null) continueAction.run();
        return false;
    }
}
