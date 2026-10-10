import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    public final int playersCount;
    public final List<Player> players = new ArrayList<>();
    private List<List<Card>> hands; // rotating hands
    public int currentPlayerIndex = 0;
    public int round = 1;
    public int turnInRound = 1;
    public boolean awaitingPassPrompt = true;
    public boolean usingChopsticksThisTurn = false;
    public int selectsNeeded = 1;
    public Map<Integer, List<Card>> stagedSelections = new HashMap<>();

    private List<Card> deck;

    public GameState(int playersCount) {
        this.playersCount = playersCount;
        for (int i=0;i<playersCount;i++) players.add(new Player(i));
        startRound();
    }

    private int dealCount() {
        switch (playersCount) {
            case 3: return 10;
            case 4: return 9;
            case 5: return 8;
        }
        return 10;
    }

    public void startRound() {
        for (Player p: players) p.newRoundReset();
        deck = Deck.fullDeck();
        // Remove puddings from deck after round 1? In Sushi Go, puddings are in deck all rounds; they persist.
        Collections.shuffle(deck, new Random());
        hands = new ArrayList<>();
        int deal = dealCount();
        for (int i=0;i<playersCount;i++) {
            List<Card> h = new ArrayList<>();
            for (int j=0;j<deal;j++) h.add(deck.remove(0));
            hands.add(h);
            players.get(i).hand = h;
        }
        currentPlayerIndex = 0;
        stagedSelections.clear();
        awaitingPassPrompt = true;
        usingChopsticksThisTurn = false;
        selectsNeeded = 1;
        // turnInRound tracks picks remaining essentially deal count
        turnInRound = 1;
    }

    public List<Card> currentHand() {
        return players.get(currentPlayerIndex).hand;
    }

    public Player currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void toggleUseChopsticks() {
        Player p = currentPlayer();
        boolean hasChop = p.tableau.stream().anyMatch(c -> c.type==CardType.CHOPSTICKS);
        if (hasChop) {
            usingChopsticksThisTurn = !usingChopsticksThisTurn;
            selectsNeeded = usingChopsticksThisTurn ? 2 : 1;
        }
    }

    public boolean selectCard(int handIndex) {
        Player p = currentPlayer();
        if (handIndex < 0 || handIndex >= p.hand.size()) return false;
        List<Card> staged = stagedSelections.computeIfAbsent(p.id, k -> new ArrayList<>());
        Card c = p.hand.get(handIndex);
        if (staged.contains(c)) {
            staged.remove(c);
        } else {
            if (staged.size() < selectsNeeded) staged.add(c);
        }
        return true;
    }

    public boolean canConfirm() {
        List<Card> staged = stagedSelections.getOrDefault(currentPlayer().id, Collections.emptyList());
        return staged.size() == selectsNeeded;
    }

    public void confirmSelection() {
        Player p = currentPlayer();
        List<Card> staged = stagedSelections.getOrDefault(p.id, new ArrayList<>());
        if (staged.size() == 0) return;
        // play staged cards (apply wasabi pairing by play order)
        for (Card c : staged) {
            if (c.type == CardType.WASABI) {
                p.tableau.add(c);
                p.hasPendingWasabi = true;
            } else if (c.isNigiri() && p.hasPendingWasabi) {
                // convert to wasabi+nigiri marker by duplicating? We'll add nigiri and mark pending=false
                p.tableau.add(c); // scoring will triple next nigiri after a wasabi by play order; we'll mark using a synthetic pair list in scoring
                p.hasPendingWasabi = false;
                // We represent order by appending; scoring will pair a wasabi with next nigiri in order of play
            } else {
                p.tableau.add(c);
            }
            if (c.type == CardType.PUDDING) {
                p.pudding += 1;
            }
        }
        // remove staged from hand
        p.hand.removeAll(staged);
        // if using chopsticks, return one chopsticks from tableau to this hand (to be passed later)
        if (usingChopsticksThisTurn) {
            // remove one chopsticks from tableau
            for (int i=0;i<p.tableau.size();i++) {
                if (p.tableau.get(i).type == CardType.CHOPSTICKS) {
                    p.tableau.remove(i);
                    // add back to hand so it will be passed
                    p.hand.add(new Card(CardType.CHOPSTICKS));
                    break;
                }
            }
        }
        // cleanup staged
        stagedSelections.remove(p.id);
        usingChopsticksThisTurn = false;
        selectsNeeded = 1;

        // advance to next player
        currentPlayerIndex = (currentPlayerIndex + 1) % playersCount;
        if (currentPlayerIndex == 0) {
            // all selected this turn, reveal + pass
            passHands();
        }
        awaitingPassPrompt = true;
    }

    private void passHands() {
        // discard final card when only 2 left before selection -> After passing logic, if hand size == 1, discard it.
        // Here, we pass left: rotate list of hands to left by 1
        List<List<Card>> newHands = new ArrayList<>();
        for (int i=0;i<playersCount;i++) newHands.add(null);
        for (int i=0;i<playersCount;i++) {
            int left = (i+1)%playersCount;
            newHands.set(left, hands.get(i));
        }
        hands = newHands;
        for (int i=0;i<playersCount;i++) {
            players.get(i).hand = hands.get(i);
            // If only one card remains after passing decision, discard it
            if (players.get(i).hand.size() == 1) {
                players.get(i).hand.clear();
            }
        }
        turnInRound++;
        // check if round over
        boolean roundOver = players.stream().allMatch(pl -> pl.hand.isEmpty());
        if (roundOver) {
            scoreRound();
            if (round < 3) {
                round++;
                startRound();
            } else {
                // game over -> add pudding score handled in finalizeScores
                finalizeScores();
            }
        }
        currentPlayerIndex = 0;
    }

    private void scoreRound() {
        // count maki for all
        int[] maki = new int[playersCount];
        for (int i=0;i<playersCount;i++) {
            int m = 0;
            for (Card c: players.get(i).tableau) m += c.makiCount();
            maki[i] = m;
        }
        // determine maki points
        int max = Arrays.stream(maki).max().orElse(0);
        List<Integer> firsts = new ArrayList<>();
        for (int i=0;i<playersCount;i++) if (maki[i]==max && max>0) firsts.add(i);
        if (!firsts.isEmpty()) {
            if (firsts.size() > 1) {
                int share = (6+3)/firsts.size();
                for (int idx: firsts) players.get(idx).score += share;
            } else {
                players.get(firsts.get(0)).score += 6;
                // find second
                int secondMax = 0, secondIdxCount = 0;
                for (int i=0;i<playersCount;i++) if (i!=firsts.get(0)) {
                    if (maki[i] > secondMax) { secondMax = maki[i]; secondIdxCount = 1; }
                    else if (maki[i] == secondMax && secondMax>0) { secondIdxCount++; }
                }
                if (secondMax>0) {
                    int each = 3 / secondIdxCount;
                    for (int i=0;i<playersCount;i++) if (i!=firsts.get(0) && maki[i]==secondMax) players.get(i).score += each;
                }
            }
        }
        // tempura, sashimi, dumpling, wasabi+nigiri scoring (by play order pairing)
        for (Player p: players) {
            int tempura = 0, sashimi = 0, dumpling = 0;
            // track wasabi pairing by order:
            int pendingWasabi = 0;
            for (Card c: p.tableau) {
                switch (c.type) {
                    case TEMPURA: tempura++; break;
                    case SASHIMI: sashimi++; break;
                    case DUMPLING: dumpling++; break;
                    case WASABI: pendingWasabi++; break;
                    default:
                        if (c.isNigiri()) {
                            int base = c.nigiriBaseScore();
                            if (pendingWasabi > 0) {
                                p.score += base * 3;
                                pendingWasabi--;
                            } else {
                                p.score += base;
                            }
                        }
                }
            }
            p.score += (tempura/2) * 5;
            p.score += (sashimi/3) * 10;
            if (dumpling >= 5) p.score += 15;
            else if (dumpling == 4) p.score += 10;
            else if (dumpling == 3) p.score += 6;
            else if (dumpling == 2) p.score += 3;
            else if (dumpling == 1) p.score += 1;
        }
        // Clear non-pudding tableaus for next round, but puddings persist (we already tallied pudding when played)
        for (Player p: players) {
            List<Card> keep = new ArrayList<>();
            for (Card c: p.tableau) {
                if (c.type == CardType.PUDDING) keep.add(c);
            }
            p.tableau = keep;
            p.hasPendingWasabi = false;
        }
    }

    private void finalizeScores() {
        // Pudding: most +6, least -6 (3-5 players). Ties share (rounded down).
        int[] pud = new int[playersCount];
        for (int i=0;i<playersCount;i++) pud[i] = players.get(i).pudding;
        int max = Arrays.stream(pud).max().orElse(0);
        int min = Arrays.stream(pud).min().orElse(0);
        List<Integer> most = new ArrayList<>(), least = new ArrayList<>();
        for (int i=0;i<playersCount;i++) {
            if (pud[i]==max && max>0) most.add(i);
            if (pud[i]==min && playersCount>=3) least.add(i);
        }
        if (!most.isEmpty()) {
            int each = 6 / most.size();
            for (int i: most) players.get(i).score += each;
        }
        if (!least.isEmpty()) {
            int each = 6 / least.size();
            for (int i: least) players.get(i).score -= each;
        }
    }

    public boolean isGameOver() {
        return round > 3 || (round==3 && players.stream().allMatch(p -> p.hand.isEmpty()) && currentPlayerIndex==0);
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
}
