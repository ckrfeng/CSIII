
import java.util.*;

public class Spot {
    BirdCard card; 
    private int eggCount = 0; 
    private int[] foodPoints; 
    ArrayList<BirdCard> tuckedCards;

    public void assignCard(BirdCard card) {
        this.card = card;
        foodPoints = new int[5];
        tuckedCards = new ArrayList<>();
    }

    public boolean addEgg() {
        if (!occupied()) return false;
        if (eggCount == card.eggMax) return false;
        eggCount++;
        return true;
    }

    public void addFood(int[] food) {
        for (int i = 0; i < foodPoints.length; i++) {
            foodPoints[i] += food[i];
        }
    }

    public boolean occupied() {
        return card != null;
    }

    public void removeEgg() {
        eggCount--;
    }
   
    public void tuckCard(BirdCard card) {
        tuckedCards.add(card);
    }

    public int getEggCount() {
        return eggCount;
    }
}