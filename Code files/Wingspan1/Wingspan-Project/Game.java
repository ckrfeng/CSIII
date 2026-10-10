import java.util.*;
import javax.swing.*;

public class Game {
    ArrayList<BirdCard> birdDeck;
    ArrayList<BonusCard> bonusDeck;
    private Wingspan frame;
    private GamePanel panel;
    private int playerPoints[];
    private Player[] players;
    public int playerTurn = 0;
    public int round = 1;
    private int[] selectedScoreTiles;

    public Game(Wingspan frame, int playerCount) {
        this.frame = frame;
        players = new Player[playerCount];
        playerPoints = new int[playerCount];
        for (int i = 0; i < playerCount; i++) {
            players[i] = new Player(this);
            players[i].setActionCubes(8);
        }
        
        selectScoreTiles();

        birdDeck = new ArrayList<>();
        birdDeck.addAll(Arrays.asList(BirdCard.values()));
        Collections.shuffle(birdDeck);
        bonusDeck = new ArrayList<>();
        bonusDeck.addAll(Arrays.asList(BonusCard.values()));
        Collections.shuffle(bonusDeck);

        handOutCards();
    }
    
    public int getPlayerCount() {
    		return players.length;
    }

    public BirdCard drawBird() {
        if(birdDeck.isEmpty()) {return null;}
        return birdDeck.remove(0);
    }

    public BonusCard drawBonus() {
        if(bonusDeck.isEmpty()) {return null;}
        return bonusDeck.remove(0);
    }

    public void requestFood(int n) {
        panel.promptFood(n);
    }

    public void selectSpots(int n, int m) {
        panel.selectSpots(n, m);
    }

    private void selectScoreTiles() {
        ArrayList<Integer> availableTiles = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            availableTiles.add(i);
        }
        Collections.shuffle(availableTiles);
        
        selectedScoreTiles = new int[4];
        for (int i = 0; i < 4; i++) {
            selectedScoreTiles[i] = availableTiles.get(i);
        }
    }

    public void drawBirdChoice(int n) {
        panel.selectCards(n);
    }

    public void showError(String msg) {
        panel.showError(msg);
    }

    public void end() {
        // Score final round goals
        scoreEndOfRoundGoals();
        
        int n = players.length;

        int[] birdPoints = new int[n];
        int[] bonusPoints = new int[n];
        int[] roundGoalPoints = new int[n];
        int[] eggPoints = new int[n];
        int[] cachedFoodPoints = new int[n];
        int[] tuckedPoints = new int[n];
        int[] foodTokens = new int[n];
        int[] totalPoints = new int[n];

        for (int i = 0; i < n; i++) {
            Player p = players[i];
            Board b = p.getBoard();

            birdPoints[i] = b.getBirdPointTotal();
            eggPoints[i] = b.getEggCount();
            cachedFoodPoints[i] = b.getCachedFoodCount();
            tuckedPoints[i] = b.getTuckedCardCount();
            
            // Count total food tokens for tie-breaking
            int totalFood = 0;
            for (int food : p.getFood()) totalFood += food;
            foodTokens[i] = totalFood;

            int bp = 0;
            for (BonusCard bc : p.getBonusCards()) {
                bp += p.bonusAction(bc);
            }
            bonusPoints[i] = bp;

            roundGoalPoints[i] = playerPoints[i];

            totalPoints[i] = birdPoints[i] + bonusPoints[i] + roundGoalPoints[i]
                    + eggPoints[i] + cachedFoodPoints[i] + tuckedPoints[i];

            playerPoints[i] = totalPoints[i];
        }

        // Determine winners with tie-breaking
        ArrayList<Integer> winners = determineWinners(totalPoints, foodTokens);

        // Create and show winner panel
        WinnerPanel winnerPanel = new WinnerPanel(this, birdPoints, bonusPoints, roundGoalPoints,
                eggPoints, cachedFoodPoints, tuckedPoints, totalPoints, foodTokens, winners);
        frame.setPanel(winnerPanel);
        panel.markEnd(winnerPanel);
    }
    
    private ArrayList<Integer> determineWinners(int[] totalPoints, int[] foodTokens) {
        int highest = totalPoints[0];
        for (int i = 1; i < players.length; i++) {
            if (totalPoints[i] > highest) highest = totalPoints[i];
        }

        ArrayList<Integer> tiedWinners = new ArrayList<>();
        for (int i = 0; i < players.length; i++) {
            if (totalPoints[i] == highest) tiedWinners.add(i);
        }

        if (tiedWinners.size() == 1) {
            return tiedWinners;
        }

        // Tie-breaking: most food tokens wins
        int maxFood = -1;
        ArrayList<Integer> foodWinners = new ArrayList<>();
        for (int i : tiedWinners) {
            if (foodTokens[i] > maxFood) {
                maxFood = foodTokens[i];
                foodWinners.clear();
                foodWinners.add(i);
            } else if (foodTokens[i] == maxFood) {
                foodWinners.add(i);
            }
        }

        // If still tied after food, victory is shared
        return foodWinners;
    }
    

    public GamePanel getGamePanel() {
        return panel;
    }

    public JPanel getCurrentPanel() {
        return frame.getPanel();
    }

    public Player getPlayer(int i) {
        return players[i];
    }

    public int getPlayerIndex(Player p) {
        for (int i = 0; i < players.length; i++) if (players[i] == p) return i;
        return -1;
    }
    
    public Player[] getPlayers() {
    		return players; 
    }

    public Player getCurrentPlayer() {
        return getPlayer(playerTurn);
    }

    public BirdFeeder getBirdFeeder() {
        return panel.feeder;
    }

    public int[] getSelectedScoreTiles() {
        return selectedScoreTiles;
    }

    public void handOutCards() {
        ScoringSheetPanel scoringSheet = new ScoringSheetPanel(this);
        frame.setPanel(scoringSheet);
    }

    public void nextTurn() {
        // Run all abilities in between turns
        playerTurn = (playerTurn + 1) % players.length;
        if (players[playerTurn].getActionCubes() == 0) {
            nextRound();
        }
        panel.playerDisplaying = playerTurn;
        panel.repaint();
    }

    public void nextRound() {
        // Score end-of-round goals before moving to next round
        scoreEndOfRoundGoals();
        
        if (round == 4) {
            end();
            return;
        }
        
        // Round end processing
        round++;
        
        // Remove all action cubes from player mats
        for (Player p : players) {
            p.getBoard().clearActionCubes();
        }
        
        // Replenish bird tray (3 face-up cards)
        if (panel != null && panel.drawer != null) {
            panel.drawer.replenishTray();
        }
        
        // Rotate first player token clockwise (move to next player)
        // This is handled by the turn system, but we set new action cubes
        for (Player p : players) {
            p.setActionCubes(9 - round);
        }
    }
    
    private void scoreEndOfRoundGoals() {
        // Score each of the 4 selected goals
        for (int goalIndex = 0; goalIndex < 4; goalIndex++) {
            int goalType = selectedScoreTiles[goalIndex];
            scoreGoal(goalType, goalIndex);
        }
    }
    
    private void scoreGoal(int goalType, int goalIndex) {
        // Goal types: 0-7 (8 different goal types)
        // Each goal has values 0-5 (or 4th place = 0)
        int[] scores = calculateGoalScores(goalType);
        
        // Assign points based on rankings (majority goals: tied players split points)
        int[] points = new int[players.length];
        boolean isPointsPerItem = isPointsPerItemGoal(goalType);
        
        if (isPointsPerItem) {
            // Points-per-item goals: players get points directly (0-5)
            for (int i = 0; i < players.length; i++) {
                points[i] = Math.min(5, scores[i]); // Cap at 5
            }
        } else {
            // Majority goals: rank players and assign points
            points = calculateMajorityPoints(scores);
        }
        
        // Add points to playerPoints array
        for (int i = 0; i < players.length; i++) {
            playerPoints[i] += points[i];
        }
    }
    
    private int[] calculateGoalScores(int goalType) {
        int[] scores = new int[players.length];
        
        // Goal types based on rulebook:
        // 0-2: Birds in habitat (Forest, Prairie, Wetlands)
        // 3-5: Birds with nest type with eggs (Bowl, Platform, Cavity, Ground)
        // 6: Eggs in habitat (all 3 habitats combined or separately)
        // 7: Sets of eggs OR Total birds
        // This is simplified - actual implementation depends on exact goal tile mappings
        
        switch (goalType) {
            case 0 -> { // Birds in Forest
                for (int i = 0; i < players.length; i++) {
                    scores[i] = players[i].getBoard().countBirdsInHabitat(0);
                }
            }
            case 1 -> { // Birds in Prairie
                for (int i = 0; i < players.length; i++) {
                    scores[i] = players[i].getBoard().countBirdsInHabitat(1);
                }
            }
            case 2 -> { // Birds in Wetlands
                for (int i = 0; i < players.length; i++) {
                    scores[i] = players[i].getBoard().countBirdsInHabitat(2);
                }
            }
            case 3 -> { // Birds with Bowl nest with eggs
                for (int i = 0; i < players.length; i++) {
                    scores[i] = players[i].getBoard().countBirdsWithNestAndEgg(Nest.BOWL);
                }
            }
            case 4 -> { // Birds with Platform nest with eggs
                for (int i = 0; i < players.length; i++) {
                    scores[i] = players[i].getBoard().countBirdsWithNestAndEgg(Nest.PLATFORM);
                }
            }
            case 5 -> { // Birds with Cavity nest with eggs
                for (int i = 0; i < players.length; i++) {
                    scores[i] = players[i].getBoard().countBirdsWithNestAndEgg(Nest.CAVITY);
                }
            }
            case 6 -> { // Sets of eggs (1 in each habitat)
                for (int i = 0; i < players.length; i++) {
                    scores[i] = players[i].getBoard().countSetsOfEggs();
                }
            }
            case 7 -> { // Total birds
                for (int i = 0; i < players.length; i++) {
                    scores[i] = players[i].getBoard().getTotalBirdCount();
                }
            }
        }
        return scores;
    }
    
    private boolean isPointsPerItemGoal(int goalType) {
        // Goals 6 and 7 are points-per-item (sets of eggs, total birds)
        return goalType == 6 || goalType == 7;
    }
    
    private int[] calculateMajorityPoints(int[] scores) {
        // Rank players: 1st = 5, 2nd = 3, 3rd = 2, 4th = 0 (or split if tied)
        int[] points = new int[players.length];
        
        // Create list of players with their scores for sorting
        ArrayList<int[]> playerScores = new ArrayList<>();
        for (int i = 0; i < players.length; i++) {
            playerScores.add(new int[]{i, scores[i]});
        }
        
        // Sort by score (descending)
        playerScores.sort((a, b) -> Integer.compare(b[1], a[1]));
        
        int rank = 0;
        while (rank < playerScores.size()) {
            int currentScore = playerScores.get(rank)[1];
            ArrayList<Integer> tiedPlayers = new ArrayList<>();
            
            // Find all players tied at this score
            for (int i = rank; i < playerScores.size(); i++) {
                if (playerScores.get(i)[1] == currentScore) {
                    tiedPlayers.add(playerScores.get(i)[0]);
                } else {
                    break;
                }
            }
            
            // Calculate points for this rank
            int rankValue = getRankValue(rank);
            int nextRankValue = rank + tiedPlayers.size() < playerScores.size() ? 
                getRankValue(rank + tiedPlayers.size()) : 0;
            
            // Split points if tied
            int totalPoints = 0;
            for (int r = rank; r < rank + tiedPlayers.size(); r++) {
                totalPoints += getRankValue(r);
            }
            if (tiedPlayers.size() > 1) {
                totalPoints += nextRankValue; // Include next place
                int splitPoints = totalPoints / tiedPlayers.size();
                for (int playerIdx : tiedPlayers) {
                    points[playerIdx] = splitPoints;
                }
            } else {
                points[tiedPlayers.get(0)] = rankValue;
            }
            
            rank += tiedPlayers.size();
        }
        
        return points;
    }
    
    private int getRankValue(int rank) {
        // 1st = 5, 2nd = 3, 3rd = 2, 4th+ = 0
        return switch (rank) {
            case 0 -> 5;
            case 1 -> 3;
            case 2 -> 2;
            default -> 0;
        };
    }

    public void setPanel(JPanel p) {
        frame.setPanel(p);
    }

    public void start() {
        panel = new GamePanel(this);
        frame.setPanel(panel);
    }
}
