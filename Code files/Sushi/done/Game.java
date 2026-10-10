import java.util.ArrayList;
import java.util.List;

public class Game {
    public List<Player> players = new ArrayList<>();
    public List<Card> deck;
    public int currentPlayerIndex = 0;
    public int round = 1;
    public final int totalRounds = 3;
    private java.util.ArrayList<int[]> roundScores = new java.util.ArrayList<>();
    private boolean gameOver = false;
    
    public Game() {
        for (int i = 0; i < 4; i++) {
        	players.add(new Player(i + 1));
        }
        startNewRound();
    }

    public void startNewRound() {
        deck = Deck.fullDeck();
        for (Player p : players) {
            p.newRoundReset();
            p.hand.clear();
            p.makiCount = 0;
        }
        dealHands();
        currentPlayerIndex = 0;
    }

    
    private void dealHands() {
        int cardsPerPlayer = 8;
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (Player p : players) {
                if (!deck.isEmpty()) {
                	p.hand.add(deck.remove(0));
                }
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void playCard(Player p, Card c) {
        if (p.hand.contains(c)) {
        	p.hand.remove(c);
        }

        if ("pudding".equals(c.type)) {
            p.pudding++;
        }
        p.table.add(c);
    }

    public void useChopsticks(Player p, Card first, Card second) {
        Card chopstick = null;
        for (int i = 0; i < p.table.size(); i++) {
            Card t = p.table.get(i);
            if (t != null && "chopsticks".equals(t.type)) {
            	chopstick = t;
                p.table.remove(i);
                break;
            }
        }
        if (chopstick != null) {
            p.hand.add(chopstick);
        }
        playCard(p, first);
        playCard(p, second);
    }

    public void endTurn() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            rotateHandsLeft();
            currentPlayerIndex = 0;

            boolean allHaveOne = true;
            for (Player p : players) {
                if (p.hand.size() != 1) {
                	allHaveOne = false; 
                	break;
                }
            }

            if (allHaveOne) {
                int[] preScores = new int[players.size()];
                for (int i = 0; i < players.size(); i++) {
                    preScores[i] = players.get(i).score;
                }

                scoreRound();

                if (round >= totalRounds) {
                    finalPuddingScoring();
                }

                int[] postScores = new int[players.size()];
                int[] delta = new int[players.size()];
                for (int i = 0; i < players.size(); i++) {
                    postScores[i] = players.get(i).score;
                    delta[i] = postScores[i] - preScores[i];
                }
                roundScores.add(delta);

                if (round >= totalRounds) {
                    for (Player p : players) {
                        p.hand.clear();
                        p.table.clear();
                    }
                    gameOver = true;
                } else {
                    round++;
                    startNewRound();
                }
            }
            
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public java.util.List<int[]> getRoundScores() {
        return roundScores;
    }

    public int[] getTotalScores() {
        int[] totals = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            totals[i] = players.get(i).score;
        }
        return totals;
    }

    public Player getWinner() {
        int best = Integer.MIN_VALUE;
        Player winner = null;
        for (Player p : players) {
            if (p.score > best) {
                best = p.score;
                winner = p;
            }
        }
        return winner;
    }
    
    
    private void rotateHandsLeft() {
        if (players.size() < 2) {
        	return;
        }
        
        List<Card> firstHand = players.get(0).hand;
        for (int i = 0; i < players.size() - 1; i++) {
            players.get(i).hand = players.get(i + 1).hand;
        }
        players.get(players.size() - 1).hand = firstHand;
    }

    private void scoreRound() {
        int n = players.size();

        for (Player p : players) {
            int pts = p.scoreTable();
            p.score += pts;
        }

        int[] makiCounts = new int[n];
        for (int i = 0; i < n; i++) {
        	makiCounts[i] = players.get(i).makiCount;
        }

        int max = -1;
        for (int num : makiCounts) {
        	if (num > max) {
        		max = num;
        	}
        }

        int topCount = 0;
        for (int value : makiCounts) {
        	if (value == max) {
        		topCount++;
        	}
        }

        if (topCount > 1) {
            int shared = 6 / topCount;
            for (int i = 0; i < n; i++) {
            	if (makiCounts[i] == max) {
            		players.get(i).score += shared;
            	}
            }
        } else {
            int x = -1;
            for (int i = 0; i < n; i++) {
            	if (makiCounts[i] == max) {
            		x = i;
            		break;
            	}
            }
        	players.get(x).score += 6;

            int second = -1;
            for (int i = 0; i < n; i++) {
                if (makiCounts[i] != max && makiCounts[i] > second) {
                	second = makiCounts[i];
                }
            }
            if (second > 0) {
                int secondCount = 0;
                for (int v : makiCounts) {
                	if (v == second) {
                		secondCount++;
                	}
                }
                int share2 = 3/secondCount;
                for (int i = 0; i < n; i++) {
                	if (makiCounts[i] == second) {
                		players.get(i).score += share2;
                	}
                }
            }
        }

        for (Player p : players) {
            p.table.clear();
            p.wasabi = false;
            p.makiCount = 0;
        }
    }

    private void finalPuddingScoring() {
        int n = players.size();
        int maxP = -1, minP = 10;
        for (Player p : players) {
            if (p.pudding > maxP) {
            	maxP = p.pudding;
            }
            if (p.pudding < minP) {
            	minP = p.pudding;
            }
        }

        List<Player> maxPlayers = new ArrayList<>();
        List<Player> minPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.pudding == maxP) {
            	maxPlayers.add(p);
            }
            if (p.pudding == minP) {
            	minPlayers.add(p);
            }
        }

        if (!maxPlayers.isEmpty()) {
            int share = 6 / Math.max(1, maxPlayers.size());
            for (Player p : maxPlayers) {
            	p.score += share;
            }
        }
        if (!minPlayers.isEmpty()) {
            int share = 6 / Math.max(1, minPlayers.size());
            for (Player p : minPlayers) {
            	p.score -= share;
            }
        }
    }
}
