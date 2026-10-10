
import java.util.*;

public class Player {

    private int actionCubes;
    private Board board;
    private ArrayList<BirdCard> birdCards;
    private ArrayList<BonusCard> bonusCards;
    private int[] foodTokens;
    private Game game;
    private boolean marker;

    public Player(Game game) {
        board = new Board(this);
        birdCards = new ArrayList<>();
        bonusCards = new ArrayList<>();
        foodTokens = new int[5];
        this.game = game;
        marker = false;
    }

    public int bonusAction(BonusCard bonus) {
        return -1;
    }

    public void drawBird() {
        birdCards.add(game.drawBird());
    }

    public void drawBonus() {
        bonusCards.add(game.drawBonus());
    }

    public boolean enoughFor(BirdCard card) {
        // Add all other values up
        int sum = 0;
        for (int i = 0; i < foodTokens.length; i++) {
            sum += foodTokens[i];
        }
        for (int i = 0; i < card.foodCost.length - 1; i++) {
            if (foodTokens[i] < card.foodCost[i]) {
                return false;
            } else {
                sum -= card.foodCost[i]; // Subtract the necessary card of each item to be left with the sum of the other food types

            }
        }
        return sum >= foodTokens[foodTokens.length - 1];
    }

    public Board getBoard() {
        return board;
    }

    public void placeAction(int habitat) {
        board.placeAction(habitat);
        actionCubes--;
    }

    public boolean playCard(BirdCard card, int habitat) {
        if (!enoughFor(card)) {
            return false;
        }
        for (int i = 0; i < foodTokens.length; i++) {
            foodTokens[i] -= card.foodCost[i];
        }
        return board.playCard(card, habitat);
    }

    public void setActionCubes(int a) {
        actionCubes = a;
    }

    public void runAction(int id) {
    
    }
}