import java.util.*;
public class Game {
    public final int playersCount = 4;
    public final List<Player> players = new ArrayList<>();
    private List<List<Card>> hands; // current hands for each player (index = player id)
    public int currentPlayerIndex = 0; // whose turn (0..3)
    public int round = 1; // 1..3
    public boolean awaitingPassPrompt = false; // if true, waiting for user to press "Start Round"
    public int selectsNeeded = 1; // number of cards to select this turn (chopsticks ignored)
    private List<Card> deck;

    public Game() {
        // init players
        for (int i=0;i<playersCount;i++) players.add(new Player(i));
        deck = Deck.fullDeck();
        startNewRound();
    }

    public void startNewRound() {
        // reset player tableau
        for (Player p: players) {
            p.newRoundReset();
        }
        // deal hands: for sushi go standard 4 players -> 10 cards each (we'll limit by deck)
        hands = new ArrayList<>();
        for (int i=0;i<playersCount;i++) hands.add(new ArrayList<>());
        int handSize = Math.min(10, deck.size()/playersCount);
        for (int i=0;i<handSize;i++) {
            for (int j=0;j<playersCount;j++) {
                if (!deck.isEmpty()) hands.get(j).add(deck.remove(0));
            }
        }
        currentPlayerIndex = 0;
        awaitingPassPrompt = false;
        selectsNeeded = 1;
    }

    public List<Card> currentHand() {
        return hands.get(currentPlayerIndex);
    }

    public Player currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public boolean canConfirm() {
        List<Integer> staged = stagedIdx().getOrDefault(currentPlayerIndex, new ArrayList<>());
        return staged.size() == selectsNeeded;
    }

    public Map<Integer,List<Integer>> stagedIdx() {
        // lazy init
        Map<Integer,List<Integer>> m = new HashMap<>();
        if (stagedIdxMap != null) return stagedIdxMap;
        stagedIdxMap = new HashMap<>();
        return stagedIdxMap;
    }

    private Map<Integer,List<Integer>> stagedIdxMap = null;

    public void toggleSelect(int cardIndex) {
        List<Integer> staged = stagedIdx().getOrDefault(currentPlayerIndex, new ArrayList<>());
        if (staged.contains(cardIndex)) staged.remove((Integer)cardIndex);
        else {
            if (staged.size() < selectsNeeded) staged.add(cardIndex);
            else {
                // replace first
                staged.set(0, cardIndex);
            }
        }
        stagedIdx().put(currentPlayerIndex, staged);
    }

    public void confirmSelection() {
        List<Integer> staged = stagedIdx().getOrDefault(currentPlayerIndex, new ArrayList<>());
        if (staged.size() != selectsNeeded) return;
        // sort descending to remove by index safely
        Collections.sort(staged, Collections.reverseOrder());
        List<Card> hand = hands.get(currentPlayerIndex);
        for (int idx: staged) {
            if (idx >= 0 && idx < hand.size()) {
                Card c = hand.remove(idx);
                currentPlayer().table.add(c);
            }
        }
        staged.clear();
        stagedIdx().put(currentPlayerIndex, staged);
        // advance player
        currentPlayerIndex++;
        if (currentPlayerIndex >= playersCount) {
            // end of round: pass hands (rotate left), if hands empty -> score and next round
            passHandsLeft();
            // check if hands still have cards (continue round) else end round
            boolean anyLeft = false;
            for (List<Card> h: hands) if (!h.isEmpty()) anyLeft=true;
            if (!anyLeft) {
                // end round: simple scoring and next round setup
                computeRoundScores();
                round++;
                if (round <= 3) {
                    // reshuffle deck and deal again (simple approach: rebuild deck and shuffle)
                    deck = Deck.fullDeck();
                    startNewRound();
                }
            }
            currentPlayerIndex = 0;
        }
    }

    private void passHandsLeft() {
        // rotate each player's hand to the left (player 0 gets player1's hand etc.)
        List<List<Card>> newHands = new ArrayList<>();
        for (int i=0;i<playersCount;i++) newHands.add(new ArrayList<>());
        for (int i=0;i<playersCount;i++) {
            int from = (i+1)%playersCount;
            newHands.get(i).addAll(hands.get(from));
        }
        hands = newHands;
    }

    private void computeRoundScores() {
        // SIMPLE scoring: each card on tableau gives 1 point.
        for (Player p: players) {
            p.score += p.table.size();
        }
    }

    public boolean isGameOver() {
        return round > 3;
    }

    public String winnerText() {
        int best = Integer.MIN_VALUE; int idx = -1; boolean tie=false;
        for (Player p: players) {
            if (p.score > best) { best = p.score; idx = p.id; tie=false; }
            else if (p.score == best) tie = true;
        }
        if (tie) return "It's a tie with " + best + " points!";
        return "Player " + (idx+1) + " wins with " + best + " points!";
    }

    // helper to get staged cards for a player (by objects)
    public List<Card> stagedCardsFor(int playerId) {
        List<Integer> idxs = stagedIdx().getOrDefault(playerId, new ArrayList<>());
        List<Card> res = new ArrayList<>();
        List<Card> hand = hands.get(playerId);
        for (int i: idxs) if (i>=0 && i<hand.size()) res.add(hand.get(i));
        return res;
    }
}