import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    public List<Player> players = new ArrayList<>();
    public List<Card> deck;
    public int currentPlayerIndex = 0;
    public int round = 1;
    public final int totalRounds = 3;

    public Game() {
        // create 4 players
        for (int i = 0; i < 4; i++) {
            players.add(new Player(i + 1));
        }
        startNewRound();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Play a card: remove from player's hand and add to their table.
     * Panel should call game.playCard(player, card) when the player clicks Done.
     */
    public void playCard(Player player, Card card) {
        if (player.hand != null) player.hand.remove(card);
        if (player.table == null) player.table = new ArrayList<Card>();
        player.table.add(card);
    }

    /**
     * Called by UI after a player finishes their play (e.g., when the Done button is pressed).
     * Advances the currentPlayerIndex; when all players have played this pass, rotate hands,
     * and check for end-of-round conditions (anyEmpty OR all players have exactly 1 card left).
     */
    public void endTurn() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            // all players have taken a turn for this pass -> rotate hands
            rotateHands();
            currentPlayerIndex = 0;

            // check end-of-round conditions:
            boolean anyEmpty = players.get(0).hand == null || players.get(0).hand.isEmpty();

            boolean allHaveOne = true;
            for (Player p : players) {
                if (p.hand == null || p.hand.size() != 1) {
                    allHaveOne = false;
                    break;
                }
            }

            if (anyEmpty || allHaveOne) {
                // score and start new round if rounds remain
                scoreRound();
                round++;
                if (round <= totalRounds) {
                    startNewRound();
                }
            }
        }
    }

    /**
     * Rotate hands to the left. Uses List.set (not array indexing) and copies lists to avoid aliasing.
     */
    private void rotateHands() {
        int n = players.size();
        List<List<Card>> newHands = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            newHands.add(new ArrayList<Card>()); // placeholder
        }
        for (int i = 0; i < n; i++) {
            List<Card> src = players.get(i).hand;
            List<Card> copy = src == null ? new ArrayList<Card>() : new ArrayList<Card>(src);
            newHands.set((i + 1) % n, copy); // pass to left
        }
        for (int i = 0; i < n; i++) {
            players.get(i).hand = newHands.get(i);
        }
    }

    /**
     * Start a new round: refill deck, clear tables, deal hands.
     * Adjust cardsPerPlayer if your game rules differ.
     */
    private void startNewRound() {
        deck = Deck.fullDeck();

        // reset per-round player state
        for (Player p : players) {
            p.table = new ArrayList<Card>();
            p.faceDownPile = new ArrayList<Card>();
            p.wasabi = false;
            p.makiCount = 0;
        }

        // deal (example: 8 cards per player)
        int cardsPerPlayer = 8;
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (Player p : players) {
                if (!deck.isEmpty()) {
                    if (p.hand == null) p.hand = new ArrayList<Card>();
                    p.hand.add(deck.remove(0));
                }
            }
        }
        currentPlayerIndex = 0;
    }

    /**
     * Score the round. Uses Player.scoreTable() if available (wraps call in try/catch).
     * Also handles pudding and maki basic scoring.
     */
    private void scoreRound() {
        // pudding scoring (example)
        int maxPudding = -1, minPudding = Integer.MAX_VALUE;
        List<Player> maxPlayers = new ArrayList<>();
        List<Player> minPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.pudding > maxPudding) maxPudding = p.pudding;
            if (p.pudding < minPudding) minPudding = p.pudding;
        }
        for (Player p : players) {
            if (p.pudding == maxPudding) maxPlayers.add(p);
            if (p.pudding == minPudding) minPlayers.add(p);
        }
        if (!maxPlayers.isEmpty()) {
            int pts = 6 / Math.max(1, maxPlayers.size());
            for (Player p : maxPlayers) p.score += pts;
        }
        if (!minPlayers.isEmpty()) {
            int pts = 6 / Math.max(1, minPlayers.size());
            for (Player p : minPlayers) p.score -= pts;
        }

        // call per-player table scoring method if it exists
        for (Player p : players) {
            try {
                p.score += p.scoreTable();
            } catch (Throwable t) {
                // method missing or failed: ignore (you can replace with your own scoring)
            }
        }

        // basic maki scoring: top gets 6, second gets 3
        int[] makiCounts = new int[players.size()];
        for (int i = 0; i < players.size(); i++) makiCounts[i] = players.get(i).makiCount;
        int firstIdx = -1, secondIdx = -1;
        int max = -1, second = -1;
        for (int i = 0; i < makiCounts.length; i++) {
            if (makiCounts[i] > max) {
                second = max; secondIdx = firstIdx;
                max = makiCounts[i]; firstIdx = i;
            } else if (makiCounts[i] > second) {
                second = makiCounts[i]; secondIdx = i;
            }
        }
        if (firstIdx >= 0) players.get(firstIdx).score += 6;
        if (secondIdx >= 0) players.get(secondIdx).score += 3;
    }
}
