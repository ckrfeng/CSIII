
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

    public void act() {
        Spot action = grid[actionCube[0]][actionCube[1]];
        if (!action.occupied()) {
            switch (actionCube[0]) {
                case 0 -> { // Forest action: add food

                }
                case 1 -> { // Prarie action: add eggs

                }
                case 2 -> { // Wetlands action: add cards

                }
            }
        } else {
            // Insert ability on-activate
        }
        moveAction();
    }

    private int columnEggCost(int column) {
        return switch (column) {
            case 0 ->
                0;
            case 1, 2 ->
                1;
            case 3, 4 ->
                2;
            default ->
                -1;
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

    public void moveAction() {
        if (actionCube[0] == 0) {
            finishedActions[actionCube[0]]++;
            actionCube[0] = -1;
            actionCube[1] = -1;
            // Insert some algorithm to run all abilities in between turns
            player.game.nextTurn();
            return;
        }
        actionCube[0]--;
        // Some input system to verify if activating card
        act();
    }

    public void placeAction(int habitat) {
        Spot[] row = grid[habitat];
        for (int i = 0; i < row.length; i++) {
            if (!row[i].occupied()) {
                actionCube[0] = habitat;
                actionCube[1] = i;
            }
        }
        act();
    }

    public boolean playCard(BirdCard card, int habitat) {
        Spot[] row = grid[habitat];
        for (int i = 0; i < row.length; i++) {
            if (!row[i].occupied()) {
                actionCube[1] = i;
            }
        }
        if (columnEggCost(actionCube[1]) > getEggCount()) {
            return false;
        }
        // Insert egg subtraction (require player selection of which eggs)
        grid[habitat][actionCube[1]].assignCard(card);
        // Insert on-play abilities
        return true;
    }

    public boolean placeEgg(int gridX, int gridY) {
        return grid[gridX][gridY].addEgg();
    }

}
