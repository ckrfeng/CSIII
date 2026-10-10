
import java.util.*;

public class Spot {
    BirdCard card; 
    private int eggCount = 0; 
    private int[] foodPoints; 
    ArrayList<BirdCard> tuckedCards;

    public void assignCard(BirdCard card) {
        this.card = card;
        if (foodPoints == null) foodPoints = new int[6];
        else foodPoints = new int[foodPoints.length];
        tuckedCards = new ArrayList<>();
    }
    public BirdCard getCard() {
    return card;
}

public int getEggCount() {
    return eggCount;
}

public int getCachedFoodCount() {
    int sum = 0;
    if (foodPoints != null)
        for (int f : foodPoints) sum += f;
    return sum;
}

public int getTuckedCount() {
    return tuckedCards == null ? 0 : tuckedCards.size();
}

    public boolean addEgg() {
        if (!occupied()) return false;
        if (eggCount == card.eggMax) return false;
        eggCount++;
        return true;
    }
    
    public void removeCard() {
        card = null; 
        eggCount = 0; 
        foodPoints = new int[6]; 
        if (tuckedCards != null) tuckedCards.clear();
    } 
    
    public int getHabitat() {
    	return card == null ? 0 : card.habitats; 
    }
    
    public void addFood(int[] food) {
        if (foodPoints == null) foodPoints = new int[6];
        for (int i = 0; i < foodPoints.length && i < food.length; i++) {
            foodPoints[i] += food[i];
        }
    }

    public boolean occupied() {
        return card != null;
    }

    public boolean removeEgg() {
        if (!occupied() || eggCount == 0) return false;
        eggCount--;
        return true;
    }
   
    public void tuckCard(BirdCard card) {
        tuckedCards.add(card);
    }

    public int[] getFood() {
        return foodPoints;
    }

    public String getName() {
        return card == null ? "" : card.name;
    }
}
