import java.util.ArrayList;
import java.util.List;

/**
 * Game.java
 * - Uses Player.scoreTable() for per-player table scoring.
 * - Handles Maki first/second awarding with tie-splitting.
 * - Awards pudding points only after the final round.
 * - Keeps pudding counts (p.pudding) accumulated across rounds.
 */
public class Game {
    public List<Player> players = new ArrayList<>();
    public List<Card> deck;
    public int currentPlayerIndex = 0;
    public int round = 1;
    public final int totalRounds = 3;

    public Game() {
        for (int i = 0; i < 4; i++) players.add(new Player(i + 1));
        startNewRound();
    }

    public void startNewRound() {
        deck = Deck.fullDeck();
        for (Player p : players) {
            p.newRoundReset();   // clears table, faceDownPile, resets wasabi flag
            p.hand.clear();
            p.makiCount = 0;     // ensure makiCount starts at 0 for this round
            // DO NOT reset p.pudding here — puddings persist across rounds
        }
        dealHands();
        currentPlayerIndex = 0;
    }

    private void dealHands() {
        int cardsPerPlayer = 8;
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (Player p : players) {
                if (!deck.isEmpty()) p.hand.add(deck.remove(0));
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Play one card from player's hand to their table.
     * If the card is a pudding, increment the player's pudding counter immediately.
     */
    public void playCard(Player p, Card c) {
        if (p == null || c == null) return;
        if (p.hand.contains(c)) p.hand.remove(c);

        // Track puddings as they are played (puddings are scored only at game end)
        if ("pudding".equalsIgnoreCase(c.type)) {
            p.pudding++;
        }
        p.table.add(c);
    }

    /**
     * Use chopsticks: remove chopsticks card instance from table and return it to player's hand,
     * then play the two chosen cards (firstChoice, secondChoice).
     */
    public void useChopsticks(Player p, Card firstChoice, Card secondChoice) {
        if (p == null) {
        	return;
        }
        Card chopstick = null;
        // find chopsticks instance on table and remove it
        for (int i = 0; i < p.table.size(); i++) {
            Card t = p.table.get(i);
            if (t != null && "chopsticks".equals(t.type)) {
            	chopstick = t;
                p.table.remove(i);
                break;
            }
            /*
            if (t != null && "chopsticks".equalsIgnoreCase(t.type)) {
            	chop = t;
                p.table.remove(i);
                break;
            }
            */
        }
        if (chopstick != null) {
            p.hand.add(chopstick);
        }
        // play the two chosen cards (this will update puddings if any of them are puddings)
        playCard(p, firstChoice);
        playCard(p, secondChoice);
    }

    /**
     * End current player's turn. When every player has played once (index wraps),
     * rotate hands left. If after rotation every player has exactly 1 card left,
     * auto-play those last cards, score the round, and either start the next round
     * or finish the game (including final pudding scoring).
     */
    public void endTurn() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            // Everyone had a turn -> rotate hands left (counter-clockwise)
            rotateHandsLeft();
            currentPlayerIndex = 0;

            // If after rotation every player has exactly 1 card, finish this dealing cycle:
            boolean allHaveOne = true;
            for (Player p : players) {
                if (p.hand.size() != 1) { allHaveOne = false; break; }
            }

            if (allHaveOne) {
                // Auto-play last card for each player (and count puddings if necessary)
                for (Player p : players) {
                    if (!p.hand.isEmpty()) {
                        Card last = p.hand.remove(0);
                        if (last != null && "pudding".equalsIgnoreCase(last.type)) p.pudding++;
                        p.table.add(last);
                    }
                }

                // Score this round (table scoring and maki)
                scoreRound();

                // If this was the final round, do pudding final scoring
                if (round >= totalRounds) {
                    finalPuddingScoring();
                    // game finished — do not auto-start a new round
                } else {
                    // go to next round
                    round++;
                    startNewRound();
                }
            }
        }
    }

    private void rotateHandsLeft() {
        if (players.size() < 2) return;
        List<Card> firstHand = players.get(0).hand;
        for (int i = 0; i < players.size() - 1; i++) {
            players.get(i).hand = players.get(i + 1).hand;
        }
        players.get(players.size() - 1).hand = firstHand;
    }

    /**
     * Score table points for the round:
     * - call each player's scoreTable() (this covers tempura/sashimi/dumplings/nigiri+wasabi),
     *   but reset p.makiCount to 0 beforehand so it is computed freshly.
     * - award maki points (6 for first, 3 for second) with tie splitting rules.
     * - clear each player's table for the next round.
     */
    private void scoreRound() {
        int n = players.size();

        // Reset makiCount for each player (scoreTable will accumulate it)
        for (Player p : players) {
            p.makiCount = 0;
        }

        // 1) Base table scoring (calls Player.scoreTable)
        for (Player p : players) {
            int pts = p.scoreTable(); // p.scoreTable should compute and update p.makiCount as it scans p.table
            p.score += pts;
        }

        // 2) Maki scoring: use p.makiCount filled by scoreTable
        int[] makiCounts = new int[n];
        for (int i = 0; i < n; i++) makiCounts[i] = players.get(i).makiCount;

        // find first and second maxima
        int max = -1;
        for (int v : makiCounts) if (v > max) max = v;

        if (max > 0) {
            // count how many have the top value
            int topCount = 0;
            for (int v : makiCounts) if (v == max) topCount++;

            if (topCount > 1) {
                // tie for first -> split 6 among them, no second-place award
                int share = 6 / topCount;
                for (int i = 0; i < n; i++) if (makiCounts[i] == max) players.get(i).score += share;
            } else {
                // single first-place winner
                int firstIdx = -1;
                for (int i = 0; i < n; i++) if (makiCounts[i] == max) { firstIdx = i; break; }
                if (firstIdx != -1) players.get(firstIdx).score += 6;

                // find second highest (exclude the first)
                int secondMax = -1;
                for (int i = 0; i < n; i++) {
                    if (makiCounts[i] != max && makiCounts[i] > secondMax) secondMax = makiCounts[i];
                }
                if (secondMax > 0) {
                    // count how many have secondMax
                    int secondCount = 0;
                    for (int v : makiCounts) if (v == secondMax) secondCount++;
                    int share2 = 3 / Math.max(1, secondCount);
                    for (int i = 0; i < n; i++) if (makiCounts[i] == secondMax) players.get(i).score += share2;
                }
            }
        }

        // 3) clear each player's table and reset temporary flags for next round
        for (Player p : players) {
            p.table.clear();
            p.wasabi = false;
            p.makiCount = 0;
        }
    }

    /**
     * Final pudding scoring performed only at end of the game (after the last round).
     * Award +6 split among players tied for most puddings.
     * Subtract 6 split among players tied for fewest puddings.
     */
    private void finalPuddingScoring() {
        int n = players.size();
        int maxP = Integer.MIN_VALUE, minP = Integer.MAX_VALUE;
        for (Player p : players) {
            if (p.pudding > maxP) maxP = p.pudding;
            if (p.pudding < minP) minP = p.pudding;
        }

        // find players tied for max/min
        List<Player> maxPlayers = new ArrayList<>();
        List<Player> minPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.pudding == maxP) maxPlayers.add(p);
            if (p.pudding == minP) minPlayers.add(p);
        }

        if (!maxPlayers.isEmpty()) {
            int share = 6 / Math.max(1, maxPlayers.size());
            for (Player p : maxPlayers) p.score += share;
        }
        if (!minPlayers.isEmpty()) {
            int share = 6 / Math.max(1, minPlayers.size());
            for (Player p : minPlayers) p.score -= share;
        }
    }
}
