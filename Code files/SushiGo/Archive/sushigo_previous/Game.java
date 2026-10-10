// Simplified but corrected Game.java with proper scoring rules
import java.util.*;

public class Game {
    List<Player> players;
    int round = 1;
    int totalRounds = 3;
    int currentPlayerIndex = 0;

    public void playCard(Player p, Card c) {
        p.hand.remove(c);
        if (c.type == Card.Type.PUDDING) p.pudding++;
        p.table.add(c);
    }

    public void endTurn() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            rotateHandsLeft();
            currentPlayerIndex = 0;

            boolean allHaveOne = true;
            for (Player pl : players) {
                if (pl.hand.size() != 1) { allHaveOne = false; break; }
            }

            if (allHaveOne) {
                for (Player pl : players) {
                    if (!pl.hand.isEmpty()) {
                        Card last = pl.hand.remove(0);
                        if (last.type == Card.Type.PUDDING) pl.pudding++;
                        pl.table.add(last);
                    }
                }
                scoreRound();
                if (round >= totalRounds) finalPuddingScoring();
                else { round++; startNewRound(); }
            }
        }
    }

    private void rotateHandsLeft() {
        if (players.size() < 2) return;
        List<Card> first = players.get(0).hand;
        for (int i = 0; i < players.size() - 1; i++) {
            players.get(i).hand = players.get(i+1).hand;
        }
        players.get(players.size()-1).hand = first;
    }

    private void scoreRound() {
        for (Player p : players) {
            p.makiCount = 0;
        }

        for (Player p : players) {
            p.score += scoreTable(p);
        }

        // Maki roll scoring
        int max = players.stream().mapToInt(pl -> pl.makiCount).max().orElse(0);
        int secondMax = players.stream().filter(pl -> pl.makiCount < max).mapToInt(pl -> pl.makiCount).max().orElse(0);

        if (max > 0) {
            long topCount = players.stream().filter(pl -> pl.makiCount == max).count();
            int share = 6 / (int)topCount;
            players.stream().filter(pl -> pl.makiCount == max).forEach(pl -> pl.score += share);
            if (topCount == 1 && secondMax > 0) {
                long secondCount = players.stream().filter(pl -> pl.makiCount == secondMax).count();
                int share2 = 3 / (int)secondCount;
                players.stream().filter(pl -> pl.makiCount == secondMax).forEach(pl -> pl.score += share2);
            }
        }

        for (Player p : players) {
            p.table.clear();
            p.wasabi = false;
            p.makiCount = 0;
        }
    }

    private int scoreTable(Player p) {
        int points = 0;
        int tempura = 0, sashimi = 0, dumplings = 0;
        for (Card c : p.table) {
            switch (c.type) {
                case TEMPURA -> tempura++;
                case SASHIMI -> sashimi++;
                case DUMPLINGS -> dumplings++;
                case MAKI1 -> p.makiCount += 1;
                case MAKI2 -> p.makiCount += 2;
                case MAKI3 -> p.makiCount += 3;
                case SALMON_NIGIRI -> points += 2;
                case SQUID_NIGIRI -> points += 3;
                case EGG_NIGIRI -> points += 1;
                case WASABI -> p.wasabi = true;
                default -> {}
            }
        }
        points += (tempura / 2) * 5;
        points += (sashimi / 3) * 10;
        points += switch (dumplings) {
            case 1 -> 1;
            case 2 -> 3;
            case 3 -> 6;
            case 4 -> 10;
            default -> dumplings >= 5 ? 15 : 0;
        };
        return points;
    }

    private void finalPuddingScoring() {
        int max = players.stream().mapToInt(pl -> pl.pudding).max().orElse(0);
        int min = players.stream().mapToInt(pl -> pl.pudding).min().orElse(0);
        long maxCount = players.stream().filter(pl -> pl.pudding == max).count();
        long minCount = players.stream().filter(pl -> pl.pudding == min).count();
        int add = 6 / (int)maxCount;
        int sub = 6 / (int)minCount;
        players.stream().filter(pl -> pl.pudding == max).forEach(pl -> pl.score += add);
        players.stream().filter(pl -> pl.pudding == min).forEach(pl -> pl.score -= sub);
    }

    private void startNewRound() {
        // redeal etc (unchanged from your original)
    }

    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }
}
