package Euchre;

// This class is used internally within EuchreController as a container for game-state variables.

import java.util.Stack;

// All members are public since it's for internal consumption only (if this were C++ it'd be a struct),
public class GameState {
    
    // 0 - waiting for players
    // 1 - choosing whether the dealer should pick up the turned-over card
    // 2 - picking the trump suit (if the dealer didn't pick up)
    // 3 - during the hand
    // 4 - between tricks (waiting for players to ready up - this is automatically initiated by a JS timer on the client side)
    // 5 - between hands (waiting for players to ready up - they need to click a button)
    // 6 - game over
    public int phase;
    
    public String[] playerNames; // array of size 4 - names for players 1-4 respectively
    public int whoseTurn; // 1, 2, 3, or 4
    public int dealer; // 1, 2, 3, or 4
    public boolean[] hasPlayed; // will be initialized to an array of size 4, indicating whether a player has taken his turn
                                // yet in this trick (or other trick-like action, such as picking the trump suit)
    public boolean[] isReady; // array of size 4, indicating whether the player has readied up
    
    public Stack<Card> player1Hand, player2Hand, player3Hand, player4Hand;
    public Stack<Card> discardPile;
    public Card pickCard;
    public Card[] playedCards; // array of size 4 - cards currently out on the table (null element means spot is empty)
    
    public int team1Score, team2Score; // # of hands each team has won
    public int team1TricksTaken, team2TricksTaken; // # of tricks taken by each time in this hand (reset to 0 each hand)
}
