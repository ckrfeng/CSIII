
import java.util.*;

public class Player {

    private int actionCubes;
    private Board board;
    private ArrayList<BirdCard> birdCards;
    private ArrayList<BonusCard> bonusCards;
    private int[] foodTokens;
    public Game game;
    private boolean marker;

    public Player(Game game) {
        board = new Board(this);
        birdCards = new ArrayList<>();
        bonusCards = new ArrayList<>();
        foodTokens = new int[5];
        this.game = game;
        marker = false;
    }

    public void addBird(BirdCard c) {
        birdCards.add(c);
    }
    public void addBonus(BonusCard c) {
        bonusCards.add(c);
    }
    public void addFood(int f) {
        foodTokens[f]++;
    }
    
    public String getFoodTokens() {
    	return Arrays.toString(foodTokens);
    }

    public int bonusAction(BonusCard bonus) {
        return -1;
    }

    public void drawBird() {
        addBird(game.drawBird());
    }

    public void drawBonus() {
        addBonus(game.drawBonus());
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

    public int getActionCubes() {
        return actionCubes;
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

    public void runAction(int id, Spot spot) {
        switch (id) {
            // BIRDFEEDER FOOD
            case 0: // On-activate: gain one [food] from the bird feeder
            case 1: // On-play: gain all tokens of [food] in the bird feeder
            case 2: // On-activate: each player gains 1 food from the bird feeder starting with the player of choice
            case 3: // On-activate: players with the fewest forest cards gain one food from the bird feeder
            case 4: // Between turns: if another player's death card succeeds, gain one food from the bird feeder

            // SUPPLY FOOD
            case 5: // On-activate: gain 1 [food] from the supply
            case 6: // On-play: gain 3 [food] from the supply
            case 7: // On-activate: all players gain 1 [food] from the supply
            case 8: // On-activate: trade 1 food for another
            case 9: // On-activate: discard 1 egg from any bird to gain [1/2] food from the supply
            case 10: // Between turns: when another player plays a card in [habitat], gain 1 [food] from the supply

            // CACHING FOOD
            case 11: // On-activate: gain 1 wheat from the supply and cache it
            case 12: // On-activate: 
            case 13: // Between turns: 

            // LAYING EGGS
            case 14: // Between turns: 
            case 15: // On-play: 
            case 16: // On-activate:
            case 17: // On-activate:
            case 18: // On-activate:
            
            // DRAWING CARDS
            case 19: // On-activate:
            case 20: // On-play: 
            case 21: // On-activate:
            case 22: // On-activate:
            case 23: // On-activate:
            case 24: // On-activate:
            case 25: // On-activate:
            case 26: // On-play: 

            // FLOCKING/TUCKING CARDS
            case 27: // Between turns: 
            case 28: // On-activate:
            case 29: // On-activate:
            case 30: // On-activate:
            case 31: // On-activate:

            // HUNTING/FISHING
            case 32: // On-activate:
            case 33: // On-activate:
            case 34: // On-activate:

            // OTHER
            case 35: // On-activate:
            case 36: // On-activate:
            case 37: // On-activate:
            case 38: // On-play: 
            case 39: // On-play: 
        }
    }

}
