import java.util.*;
import javax.swing.*;

// THE MAJOR ERROR OCCURING IS BECAUSE OF ACT METHOD, MOVEACTION METHOD(SINCE IT CALLS ACT) and firecallback in gamepanel which is affecting birdfeederfedhil so pls js look into that
public class Board {

    public int[] actionCube;
    public int[] finishedActions;
    public Spot[][] grid;
    private Player player;

    public Board(Player player) {
        this.player = player;
        actionCube = new int[]{-1, -1};
        finishedActions = new int[4];
        grid = new Spot[3][6];
        for (Spot[] row : grid) {
            for (int j = 0; j < row.length; j++) {
                row[j] = new Spot();
            }
        }
    }
    public Spot getSpecificSpot(int x, int y) {
    		return grid[x][y];
    }
    public int getBirdPointTotal() {
    int sum = 0;
    for (int r = 0; r < grid.length; r++) {
        for (int c = 0; c < grid[r].length; c++) {
            Spot s = grid[r][c];
            if (s.occupied()) sum += s.getCard().points;
        }
    }
    return sum;
}

public int getCachedFoodCount() {
    int sum = 0;
    for (int r = 0; r < grid.length; r++) {
        for (int c = 0; c < grid[r].length; c++) {
            sum += grid[r][c].getCachedFoodCount();
        }
    }
    return sum;
}

    public int getTuckedCardCount() {
    int sum = 0;
    for (int r = 0; r < grid.length; r++) {
        for (int c = 0; c < grid[r].length; c++) {
            sum += grid[r][c].getTuckedCount();
        }
    }
    return sum;
}

    // Helper methods for scoring
    public int countBirdsInHabitat(int habitat) {
        int count = 0;
        for (int c = 0; c < grid[habitat].length; c++) {
            if (grid[habitat][c].occupied()) count++;
        }
        return count;
    }

    public int countBirdsWithNest(Nest nest) {
        int count = 0;
        for (Spot[] row : grid) {
            for (Spot spot : row) {
                if (spot.occupied() && spot.getCard().nest == nest) count++;
            }
        }
        return count;
    }

    public int countBirdsWithNestAndEgg(Nest nest) {
        int count = 0;
        for (Spot[] row : grid) {
            for (Spot spot : row) {
                if (spot.occupied() && spot.getEggCount() > 0) {
                    if (spot.getCard().nest == nest || nest == Nest.ANY) count++;
                }
            }
        }
        return count;
    }

    public int countEggsInHabitat(int habitat) {
        int count = 0;
        for (int c = 0; c < grid[habitat].length; c++) {
            if (grid[habitat][c].occupied()) count += grid[habitat][c].getEggCount();
        }
        return count;
    }

    public int countEggsWithNest(Nest nest) {
        int count = 0;
        for (Spot[] row : grid) {
            for (Spot spot : row) {
                if (spot.occupied()) {
                    if (spot.getCard().nest == nest || nest == Nest.ANY) {
                        count += spot.getEggCount();
                    }
                }
            }
        }
        return count;
    }

    public int countSetsOfEggs() {
        int forestEggs = countEggsInHabitat(0);
        int prairieEggs = countEggsInHabitat(1);
        int wetlandEggs = countEggsInHabitat(2);
        return Math.min(Math.min(forestEggs, prairieEggs), wetlandEggs);
    }

    public int getTotalBirdCount() {
        int count = 0;
        for (Spot[] row : grid) {
            for (Spot spot : row) {
                if (spot.occupied()) count++;
            }
        }
        return count;
    }

    public ArrayList<BirdCard> getAllPlayedBirds() {
        ArrayList<BirdCard> birds = new ArrayList<>();
        for (Spot[] row : grid) {
            for (Spot spot : row) {
                if (spot.occupied()) birds.add(spot.getCard());
            }
        }
        return birds;
    }

    public void clearActionCubes() {
        actionCube[0] = -1;
        actionCube[1] = -1;
        finishedActions = new int[4];
    }
     
    public void moveBirdHabitat(Spot fromSpot, int index) {
        int targetColumn = lastUnoccupiedColumn(index);
        if (targetColumn < 0) return;
        Spot toSpot = getSpot(index, targetColumn);
        toSpot.assignCard(fromSpot.getCard());
        toSpot.addFood(fromSpot.getFood());
        for (int i = 0; i < fromSpot.getEggCount(); i++) {
            fromSpot.removeEgg();
            toSpot.addEgg();
        }
        fromSpot.removeCard();
    }

    public int gainInColumn(int column) {
        return switch (column) {case 2, 3 -> 2; case 4, 5 -> 3; default -> 1;};
    }

    public Spot getSpot(int habitat, int column) {
        if (column < 0 || column > 6 || habitat < 0 || habitat > 3) return null;
        return grid[habitat][column];
    }

    public int lastUnoccupiedColumn(int habitat) {
        Spot[] row = grid[habitat];
        for (int i = 0; i < row.length; i++) {
            if (!row[i].occupied()) {
                return i;
            }
        }
        return -1;
    }

    public int columnEggCost(int column) {
        return switch (column) {
            case 0 ->
                0;
            case 1, 2 ->
                1;
            case 3, 4 ->
                2;
            default ->
                3;
        };
    }

    public int getEggCount() {
        int sum = 0;
        for (Spot[] row : grid) {
            for (Spot spot : row) {
                if (spot.occupied()) {
                    sum += spot.getEggCount();
                }
            }
        }
        return sum;
    }

    public int getEggMax() {
        int sum = 0;
        for (Spot[] row : grid) {
            for (Spot spot : row) {
                if (spot.occupied()) {
                    sum += spot.getCard().eggMax;
                }
            }
        }
        return sum;
    }

    public void act() {
        if (actionCube[0] < 0 || actionCube[1] < 0) return;
        Spot action = grid[actionCube[0]][actionCube[1]];
        if (!action.occupied()) {
            switch (actionCube[0]) {
                case 0 -> { // Forest action: add food
                    if (actionCube[1] % 2 == 1 && player.getHand().size() > 0) {
                        Prompt prompt = new Prompt("Exchange card for food?");
                        prompt.setCallback((b) -> {
                            if (!(Boolean)b) {player.requestFood(gainInColumn(actionCube[1])); return;}
                            Sacrifice sacrifice = new Sacrifice(player, true);
                            sacrifice.setCallback((c) -> {
                                player.getHand().remove((BirdCard)c);
                                player.game.setPanel(player.game.getGamePanel());
                                player.requestFood(gainInColumn(actionCube[1] + 1));
                            });
                            player.game.setPanel(sacrifice);
                        });
                        player.game.setPanel(prompt);
                    } else player.requestFood(gainInColumn(actionCube[1]));
                }
                case 1 -> { // Prarie action: add eggs
                    int sum = 0;
                    for (int f : player.getFood()) sum += f;
                    if (getEggMax() - getEggCount() < gainInColumn(actionCube[1]) + 1) {
                        player.showError("Not enough room for more eggs");
                    } else if (actionCube[1] % 2 == 1 && getEggMax() - getEggCount() != gainInColumn(actionCube[1]) + 1 && sum > 0) {
                        Prompt prompt = new Prompt("Exchange food for egg?");
                        prompt.setCallback((b) -> {
                            if (!(Boolean)b) {player.game.setPanel(player.game.getGamePanel()); player.selectSpots(gainInColumn(actionCube[1]) + 1, 1); return;}
                            Sacrifice sacrifice = new Sacrifice(player, false);
                            sacrifice.setCallback((c) -> {
                                player.addFood((Integer)c, -1);
                                player.game.setPanel(player.game.getGamePanel());
                                player.selectSpots(gainInColumn(actionCube[1]) + 2, 1);
                            });
                            player.game.setPanel(sacrifice);
                        });
                        player.game.setPanel(prompt);
                    } else player.selectSpots(gainInColumn(actionCube[1]) + 1, 1);
                }
                case 2 -> { // Wetlands action: add cards
                    if (actionCube[1] % 2 == 1 && getEggCount() > 0) {
                        Prompt prompt = new Prompt("Exchange egg for card?");
                        prompt.setCallback((b) -> {
                            if (!(Boolean)b) {player.drawBirdChoice(gainInColumn(actionCube[1])); return;}
                            player.game.setPanel(player.game.getGamePanel());
                            player.selectSpots(1, -2);
                        });
                        player.game.setPanel(prompt);
                    } else player.drawBirdChoice(gainInColumn(actionCube[1]));
                }
            }
        } else {
            askToActivate(action, () -> moveAction());
        }
    }

    public void moveAction() {
        if (actionCube[1] < 0) return;
        if (actionCube[1] == 0) {
            finishedActions[actionCube[0]]++;
            actionCube[0] = -1;
            actionCube[1] = -1;
            player.endTurn();
            return;
        }
        actionCube[1]--;
        // Some input system to verify if activating card
        act();
    }

    public void placeAction(int habitat) {
        actionCube[0] = habitat;
        actionCube[1] = lastUnoccupiedColumn(habitat);
        if (actionCube[1] < 0) {
            player.showError("No space left in this habitat");
            actionCube[0] = -1;
            actionCube[1] = -1;
            return;
        }
        act();
    }

    public int playCard(BirdCard card, ArrayList<Integer> stats) {
        int habitat = stats.get(stats.size() - 1);
        actionCube[0] = habitat;
        actionCube[1] = lastUnoccupiedColumn(habitat);
        if (actionCube[1] < 0 || actionCube[1] >= grid[habitat].length) {
            actionCube[0] = -1; actionCube[1] = -1; return 0;
        }
        if (columnEggCost(actionCube[1]) > getEggCount()) {actionCube[0] = -1; actionCube[1] = -1; return 0;}
        for (int i = 0; i < 6 && i < stats.size(); i++) {
            player.addFood(i, -stats.get(i));
        }
        if (columnEggCost(actionCube[1]) != 0) player.selectSpots(columnEggCost(actionCube[1]), -1);
        grid[habitat][actionCube[1]].assignCard(card);
        if (isOnPlayAbility(card.actionId)) {
            Spot mySpot = grid[actionCube[0]][actionCube[1]];
            promptForAbility(mySpot, false, null);
        }
        actionCube[0] = -1;
        actionCube[1] = -1;
        return 1;
    }

    public boolean placeEgg(int gridX, int gridY) {
        return grid[gridX][gridY].addEgg();
    }
    
    public Spot[][] getAllSpots() {
    	return grid;
    }

    public int[] getActionCube() {
        return actionCube;
    }
    
    public ArrayList<Spot> getHabitatSpots(int habitatMask) {
        ArrayList<Spot> spotsInHabitat = new ArrayList<Spot>();
        
        Spot[][] allSpots = this.getAllSpots();

        for (int i = 0; i < allSpots.length; i++) {
            for (int j = 0; j < allSpots[i].length; j++) {
                Spot s = allSpots[i][j];
                if (s.occupied() && (s.getHabitat() & habitatMask) != 0) {
                    spotsInHabitat.add(s);
                }
            }
        }

        return spotsInHabitat;
    }

    private boolean isOnPlayAbility(int actionId) {
        return switch (actionId) {
            case 1, 6, 15, 20, 26, 38, 39 -> true;
            default -> false;
        };
    }

    private void askToActivate(Spot spot, Runnable after) {
        promptForAbility(spot, true, after);
    }

    private void promptForAbility(Spot spot, boolean advanceAfter, Runnable after) {
        if (advanceAfter) {
            moveAction();
            return;
        }
        int id = spot.getCard().actionId;
        String promptMessage = advanceAfter ? 
            "Activate action for " + spot.getName() + "?" : 
            "Activate on-play action for " + spot.getName() + "?";
        Prompt prompt = new Prompt(promptMessage);
        prompt.setCallback((b) -> {
            if (Boolean.TRUE.equals(b)) {
                // Player accepted - run the action
                Runnable actionComplete = () -> {
                    if (advanceAfter && after != null) {
                        after.run();
                    } else {
                        // For on-play actions, just return to game panel
                        player.game.setPanel(player.game.getGamePanel());
                    }
                };
                boolean async = player.runAction(id, spot, actionComplete);
                if (!async) {
                    // Synchronous action - run completion immediately
                    actionComplete.run();
                }
            } else {
                // Player denied
                if (advanceAfter && after != null) {
                    // For on-activate, still advance the action cube
                    after.run();
                } else {
                    // For on-play actions, just return to game panel
                    player.game.setPanel(player.game.getGamePanel());
                }
            }
        });
        player.game.setPanel(prompt);
    }

}
