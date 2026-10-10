// Game.java
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
        //adding 4 players with ID 1-4
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
        }
        dealHands();
        currentPlayerIndex = 0;
    }
    
    //gives each player their hand for the round
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

    public void playCard(Player player, Card card) {
        player.hand.remove(card);
        player.table.add(card);
    }

    public void endTurn() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            // all players played -> rotate hands
            rotateHands();
            currentPlayerIndex = 0;
            // if hands empty -> end round
            if (players.get(0).hand.isEmpty()) {
                scoreRound();
                round++;
                if (round <= totalRounds) {
                    startNewRound();
                }
            }
        }
    }

    private void rotateHands() {
        List<Card> temp = new ArrayList<>(players.get(0).hand);
        for (int i = 0; i < players.size() - 1; i++) {
            players.get(i).hand = players.get(i + 1).hand;
        }
        players.get(players.size() - 1).hand = temp;
    }

    private void scoreRound() {
        //scoring pudding
        int maxPudding = -1, minPudding = Integer.MAX_VALUE;
        List<Player> maxPlayers = new ArrayList<>();
        List<Player> minPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.pudding > maxPudding) {
            	maxPudding = p.pudding;
            }
            if (p.pudding < minPudding) {
            	minPudding = p.pudding;
            }
        }
        for (Player p : players) {
            if (p.pudding == maxPudding) {
            	maxPlayers.add(p);
            }
            if (p.pudding == minPudding) {
            	minPlayers.add(p);
            }
        }
        
        int puddingPoints = 6 / maxPlayers.size(); // split ties
        for (Player p : maxPlayers) {
        	p.score += puddingPoints;
        }
        
        int negativePuddingPoints = 6 / minPlayers.size();
        for (Player p : minPlayers) {
        	p.score -= negativePuddingPoints;
        }

        //score each player
        for (Player p : players) {
            p.score += p.scoreTable();
        }

        //scoring for maki
        int[] makiCounts = new int[players.size()];
        for (int i = 0; i < players.size(); i++) makiCounts[i] = players.get(i).makiCount;
        int first = -1, second = -1;
        int max = -1, sec = -1;
        for (int i = 0; i < makiCounts.length; i++) {
            if (makiCounts[i] > max) {
                sec = max; 
                second = first;
                max = makiCounts[i]; 
                first = i;
            } else if (makiCounts[i] > sec) {
                sec = makiCounts[i];
                second = i;
            }
        }
        
        if (first != -1) {
        	players.get(first).score += 6;
        }
        if (second != -1) {
        	players.get(second).score += 3;
        }
    }
}
