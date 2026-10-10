import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    public final int playersCount = 4;
    public List<Player> players = new ArrayList<>();
    public List<List<Card>> hands;
    public List<List<Card>> placedDown;
    public List<Integer> wasabiCount;
    public List<Integer> chopsticksCount;
    public List<Boolean> pendingUseWasabi;
    public List<Boolean> pendingUseChopsticks;
    public List<Card> deck;
    public int currentPlayerIndex = 0;
    public int round = 1; // 1..3

    public Game() {
        for (int i = 0; i < playersCount; i++) players.add(new Player(i));
        wasabiCount = new ArrayList<>();
        chopsticksCount = new ArrayList<>();
        pendingUseWasabi = new ArrayList<>();
        pendingUseChopsticks = new ArrayList<>();
        for (int i = 0; i < playersCount; i++) {
            wasabiCount.add(0);
            chopsticksCount.add(0);
            pendingUseWasabi.add(false);
            pendingUseChopsticks.add(false);
        }
        deck = Deck.fullDeck();
        startNewRound();
    }

    public void startNewRound() {
        placedDown = new ArrayList<>();
        for (int i = 0; i < playersCount; i++) {
            placedDown.add(new ArrayList<>());
            wasabiCount.set(i, 0);
            chopsticksCount.set(i, 0);
            pendingUseWasabi.set(i, false);
            pendingUseChopsticks.set(i, false);
        }
        hands = new ArrayList<>();
        for (int i = 0; i < playersCount; i++) hands.add(new ArrayList<>());
        int handSize = Math.min(10, deck.size() / playersCount);
        for (int k = 0; k < handSize; k++) {
            for (int p = 0; p < playersCount; p++) {
                if (!deck.isEmpty()) hands.get(p).add(deck.remove(0));
            }
        }
        currentPlayerIndex = 0;
    }

    public void playCard(int cardIndex) {
        if (cardIndex < 0) return;
        List<Card> hand = hands.get(currentPlayerIndex);
        if (cardIndex >= hand.size()) return;
        Card chosen = hand.remove(cardIndex);
        placedDown.get(currentPlayerIndex).add(chosen);

        if (chosen.type.equals("wasabi")) {
            wasabiCount.set(currentPlayerIndex, wasabiCount.get(currentPlayerIndex) + 1);
        } else if (chosen.type.equals("chopsticks")) {
            chopsticksCount.set(currentPlayerIndex, chopsticksCount.get(currentPlayerIndex) + 1);
        } else if (chosen.type.equals("pudding")) {
            Player p = players.get(currentPlayerIndex);
            p.pudding += 1;
        } else if (pendingUseWasabi.get(currentPlayerIndex) && chosen.isNigiri()) {
            chosen.type = "wasabi+" + chosen.type;
            int w = wasabiCount.get(currentPlayerIndex);
            if (w > 0) wasabiCount.set(currentPlayerIndex, w - 1);
            pendingUseWasabi.set(currentPlayerIndex, false);
        }

        // No automatic rotation now, rotation happens when player clicks Done
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playersCount;
        passHandsLeft();
    }

    public void passHandsLeft() {
        List<List<Card>> newHands = new ArrayList<>();
        for (int i = 0; i < playersCount; i++) newHands.add(new ArrayList<>());
        for (int i = 0; i < playersCount; i++) {
            int from = (i + 1) % playersCount;
            newHands.get(i).addAll(hands.get(from));
        }
        hands = newHands;
    }

    public void requestUseWasabi(int playerId) {
        if (playerId < 0 || playerId >= playersCount) return;
        if (wasabiCount.get(playerId) > 0) pendingUseWasabi.set(playerId, true);
    }

    public void requestUseChopsticks(int playerId) {
        if (playerId < 0 || playerId >= playersCount) return;
        if (chopsticksCount.get(playerId) > 0) pendingUseChopsticks.set(playerId, true);
    }

    public boolean isRoundFinished() {
        for (List<Card> hand : hands) if (!hand.isEmpty()) return false;
        return true;
    }

    public void endRoundAndScore() {
        for (int i = 0; i < playersCount; i++) {
            List<Card> pd = placedDown.get(i);
            Player p = players.get(i);
            p.table.addAll(pd);
            pd.clear();
        }
        computeRoundScores();
        round++;
        if (round <= 3) {
            deck = Deck.fullDeck();
            startNewRound();
        } else {
            computePuddingScores();
        }
    }

    private void computeRoundScores() {
        int[] makiTotals = new int[playersCount];
        for (int i = 0; i < playersCount; i++) {
            Player p = players.get(i);
            int makiSum = 0;
            for (Card c : p.table) {
                switch (c.type) {
                    case "maki1": makiSum += 1; break;
                    case "maki2": makiSum += 2; break;
                    case "maki3": makiSum += 3; break;
                }
            }
            makiTotals[i] = makiSum;
            p.makiCount = makiSum;
        }
        // maki scoring
        int max = -1;
        for (int v : makiTotals) if (v > max) max = v;
        int maxCount = 0;
        for (int v : makiTotals) if (v == max) maxCount++;
        int second = -1;
        if (maxCount == 1) for (int v : makiTotals) if (v > second && v < max) second = v;
        int secondCount = 0;
        if (second >= 0) for (int v : makiTotals) if (v == second) secondCount++;

        if (max > 0) {
            int award = 6 / Math.max(1, maxCount);
            for (int i = 0; i < playersCount; i++) if (makiTotals[i] == max) players.get(i).score += award;
        }
        if (second > 0) {
            int award = 3 / Math.max(1, secondCount);
            for (int i = 0; i < playersCount; i++) if (makiTotals[i] == second) players.get(i).score += award;
        }

        // per-player scoring
        for (Player p : players) p.score += p.scoreTable();
    }

    private void computePuddingScores() {
        int maxP = Integer.MIN_VALUE, minP = Integer.MAX_VALUE;
        for (Player p : players) {
            if (p.pudding > maxP) maxP = p.pudding;
            if (p.pudding < minP) minP = p.pudding;
        }
        int countMax = 0, countMin = 0;
        for (Player p : players) {
            if (p.pudding == maxP) countMax++;
            if (p.pudding == minP) countMin++;
        }
        if (maxP > 0) {
            int award = 6 / Math.max(1, countMax);
            for (Player p : players) if (p.pudding == maxP) p.score += award;
        }
        if (countMin > 0) {
            int deduct = 6 / Math.max(1, countMin);
            for (Player p : players) if (p.pudding == minP) p.score -= deduct;
        }
    }

    public boolean isGameOver() { return round > 3; }

    public String winnerText() {
        int best = Integer.MIN_VALUE, idx = -1;
        boolean tie = false;
        for (int i = 0; i < playersCount; i++) {
            int s = players.get(i).score;
            if (s > best) { best = s; idx = i; tie = false; }
            else if (s == best) tie = true;
        }
        if (tie) return "It's a tie with " + best + " points!";
        return "Player " + (idx + 1) + " wins with " + best + " points!";
    }
}
