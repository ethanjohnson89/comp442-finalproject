package Euchre;

import java.util.ArrayList;
import java.util.Stack;

// This class is used internally within EuchreController as a container for game-state variables.
// All members are public since it's for internal consumption only (if this were C++ it'd be a struct),
public class GameState {
    
    // 0 - waiting for players
    // 1 - choosing whether the dealer should pick up the turned-over card
    // 2 - waiting for the dealer to choose a discard after taking the turned-over card
    // 3 - picking the trump suit (if the dealer didn't pick up)
    // 4 - during the hand
    // 5 - between tricks (waiting for players to ready up - this is automatically initiated by a JS timer on the client side)
    // 6 - between hands (waiting for players to ready up - they need to click a button)
    // 7 - game over
    public int phase;
    
    public String[] playerNames; // array of size 4 - names for players 1-4 respectively
    public int whoseTurn; // 1, 2, 3, or 4; these increment in clockwise order around the table
    public int dealer; // 1, 2, 3, or 4
    public boolean[] hasPlayed; // will be initialized to an array of size 4, indicating whether a player has taken his turn
                                // yet in this trick (or other trick-like action, such as picking the trump suit)
    public boolean[] isReady; // array of size 4, indicating whether the player has readied up
    
    public Card.Suit trumpSuit;
    public int whoPickedTrump; // team #: 1 or 2
    public Card.Suit suitLead;
    
    public ArrayList<Stack<Card>> playerHands; // this would be an array of size 4 if Java could make arrays of generic types
    public Stack<Card> discardPile;
    public Card pickCard; // null means the card has been taken, or otherwise cleared off the table
    public Card[] playedCards; // array of size 4 - cards currently out on the table (null element means spot is empty)
    
    public int team1Score, team2Score; // # of hands each team has won
    public int team1TricksTaken, team2TricksTaken; // # of tricks taken by each time in this hand (reset to 0 each hand)
}
