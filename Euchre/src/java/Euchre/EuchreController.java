package Euchre;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class EuchreController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/xml;charset=UTF-8");
        
        ServletContext servletContext = getServletContext();
        HttpSession session = request.getSession();
        
        GameState gameState = (GameState)servletContext.getAttribute("gameState");
        
        String resetEverything = request.getParameter("resetEverything");
        String enterGame = request.getParameter("enterGame");
        String loginName = request.getParameter("loginName");
        
        if(resetEverything != null && resetEverything.equals("true"))
        {
            gameState = null;
            servletContext.setAttribute("gameState", gameState);
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        if(enterGame != null && enterGame.equals("true") && loginName != null && !loginName.isEmpty())
        {
            if(gameState != null && gameState.playerNames != null)
            {
                for(int i = 0; i < 4; i++)
                {
                    if(gameState.playerNames[i] != null && loginName.equals(gameState.playerNames[i])) // you're already in this game and are re-connecting
                    {
                        session.setAttribute("loginName", loginName);
                        RequestDispatcher dispatcher = request.getRequestDispatcher("game.jsp");
                        dispatcher.forward(request, response);
                        return;
                    }
                }
            }
            
            if(gameState == null || gameState.phase == 7) // Build a fresh game state
            {
                gameState = new GameState();
                servletContext.setAttribute("gameState", gameState);

                gameState.phase = 0;
                Random randGen = new Random();
                gameState.dealer = randGen.nextInt(4) + 1;
                gameState.whoseTurn = 2; // while we're picking up new players, this indicates the next spot to be filled
                gameState.playerNames = new String[4];
                gameState.playerNames[0] = loginName;
                session.setAttribute("loginName", loginName); // will be used to identify the player throughout the game
                session.setAttribute("playerNumber", 1); // will be used to tell the player where he sits at the table
                gameState.hasPlayed = new boolean[4];
                gameState.isReady = new boolean[4];
                gameState.isReady[0] = true;

                // Shuffle the deck and deal to players
                gameState.discardPile = new Stack<Card>();
                for(Card.Suit s : Card.Suit.values())
                {
                    gameState.discardPile.push(new Card(s, 1)); // ace
                    for(int faceValue = 9; faceValue <= 13; faceValue++)
                        gameState.discardPile.push(new Card(s, faceValue));
                }
                Collections.shuffle(gameState.discardPile);

                gameState.playerHands = new ArrayList<Stack<Card>>(4);
                for(int i = 0; i < 4; i++)
                    gameState.playerHands.add(new Stack<Card>());
                for(int i = 0; i < 5; i++) // deal 5 cards to each player
                    for(int j = 0; j < 4; j++)
                        gameState.playerHands.get(j).add(gameState.discardPile.pop());
                gameState.pickCard = gameState.discardPile.pop();
                gameState.playedCards = new Card[4];

                gameState.team1Score = 0;
                gameState.team2Score = 0;
                gameState.team1TricksTaken = 0;
                gameState.team2TricksTaken = 0;

                RequestDispatcher dispatcher = request.getRequestDispatcher("game.jsp");
                dispatcher.forward(request, response);
                return;
            }
            else // we're entering a game that already has players
            {
                if(gameState.phase != 0) // we're no longer accepting new players (game is full)
                {
                    session.setAttribute("error", "Sorry, game is full! Please try again later.");
                    RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
                    dispatcher.forward(request, response);
                    return;
                }
                
                gameState.playerNames[gameState.whoseTurn - 1] = loginName;
                session.setAttribute("loginName", loginName);
                session.setAttribute("playerNumber", new Integer(gameState.whoseTurn)); // will be used to tell the player where he sits at the table
                gameState.isReady[gameState.whoseTurn - 1] = true;
                gameState.whoseTurn++;
                if(gameState.whoseTurn > 4) // filled all 4 spaces, continue to pick-up phase
                {
                    gameState.phase = 1;
                    gameState.whoseTurn = gameState.dealer + 1;
                    if(gameState.whoseTurn > 4)
                        gameState.whoseTurn = 1;
                    for(int i = 0; i < 4; i++)
                        gameState.hasPlayed[i] = false;
                }
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("game.jsp");
                dispatcher.forward(request, response);
                return;
            }
        }
        else // this is an AJAX request
        {
            if(gameState == null)
            {
                PrintWriter out = response.getWriter();
                out.println("<error>nullGameState</error>");
                out.close();
                return;
            }
            
            String sessionLoginName = (String)session.getAttribute("loginName");
            if(sessionLoginName == null)
            {
                PrintWriter out = response.getWriter();
                out.println("<error>noUsernameTokenSent</error>");
                out.close();
                return;
            }

            int playerNumber;
            for(playerNumber = 1; playerNumber <= 4; playerNumber++)
                if(gameState.playerNames[playerNumber-1].equals(sessionLoginName))
                    break;
            if(playerNumber > 4)
            {
                PrintWriter out = response.getWriter();
                out.println("<error>notInGame</error>");
                out.close();
                return;
            }
            
            // If we've gotten this far, the player is indeed part of this game, so we'll process his request.
            String action = request.getParameter("action");
            if(action != null && !action.isEmpty())
            {
                if(gameState.whoseTurn != playerNumber)
                {
                    PrintWriter out = response.getWriter();
                    out.println("<error>notYourTurn</error>");
                    out.close();
                    return;
                }
                
                // If none of these actions are matched, we'll just fall through to the code below that forwards
                // to ajaxGameState.jsp (without taking any actions - i.e. we're just sending the current game state)
                String choice = request.getParameter("choice"); // this is defined for some, but not all actions
                if(action.equals("pickUp") && choice != null) // choice should be "yes" or "no"
                {
                    if(gameState.phase != 1)
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>cannotTakeThatActionNow</error>");
                        out.close();
                        return;
                    }
                    
                    if(choice.equals("yes")) // the player wants the dealer to pick up the card
                    {
                        gameState.phase = 2; // the dealer now needs to choose a discard
                        gameState.trumpSuit = gameState.pickCard.getSuit();
                        gameState.whoseTurn = gameState.dealer;
                        gameState.playerHands.get(gameState.dealer - 1).push(gameState.pickCard);
                        gameState.pickCard = null;
                    }
                    else if(choice.equals("no"))
                    {
                        gameState.hasPlayed[gameState.whoseTurn - 1] = true;
                        gameState.whoseTurn++;
                        if(gameState.whoseTurn > 4)
                            gameState.whoseTurn = 1;
                        if(gameState.hasPlayed[gameState.whoseTurn - 1]) // we've gone around the whole circle, move to trump selection phase
                        {
                            gameState.phase = 3;
                            gameState.whoseTurn = gameState.dealer + 1;
                            if(gameState.whoseTurn > 4)
                                gameState.whoseTurn = 1;
                            for(int i = 0; i < 4; i++)
                                gameState.hasPlayed[i] = false;
                            gameState.hasPlayed[gameState.dealer - 1] = true; // dealer does not participate in trump selection phase
                        }
                    }
                    else // invalid choice
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>invalidChoiceForAction</error>");
                        out.close();
                        return;
                    }
                }
                else if(action.equals("discard") && choice != null) // choice should be a card imgName
                {
                    if(gameState.phase != 2)
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>cannotTakeThatActionNow</error>");
                        out.close();
                        return;
                    }
                    
                    Card toDiscard;
                    try { toDiscard = new Card(choice); }
                    catch (Exception e)
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>invalidCard</error>");
                        out.close();
                        return;
                    }
                    
                    int indexToDiscard = 0;
                    for(; indexToDiscard < gameState.playerHands.get(gameState.whoseTurn - 1).size(); indexToDiscard++)
                        if(toDiscard.equals(gameState.playerHands.get(gameState.whoseTurn - 1).get(indexToDiscard)))
                            break;
                    if(indexToDiscard >= gameState.playerHands.get(gameState.whoseTurn - 1).size())
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>invalidCard</error>");
                        out.close();
                        return;
                    }
                    
                    // Discard the selected card, and put the pick card in its place in the dealer's hand
                    gameState.discardPile.push(gameState.playerHands.get(gameState.whoseTurn - 1).get(indexToDiscard));
                    gameState.playerHands.get(gameState.whoseTurn - 1).set(indexToDiscard, gameState.pickCard);
                    gameState.pickCard = null;
                    
                    // Proceed to phase 4 (trump suit has already been picked, so skip phase 3)
                    gameState.phase = 4;
                    gameState.whoseTurn = gameState.dealer + 1;
                    if(gameState.whoseTurn > 4)
                        gameState.whoseTurn = 1;
                    for(int i = 0; i < 4; i++)
                        gameState.hasPlayed[i] = false;
                }
                else if(action.equals("pickTrump") && choice != null) // choice should be a suit name (lowercase) or "pass"
                {
                    if(gameState.phase != 3)
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>cannotTakeThatActionNow</error>");
                        out.close();
                        return;
                    }
                    
                    if(choice.equals("pass"))
                    {
                        gameState.hasPlayed[gameState.whoseTurn - 1] = true;
                        gameState.whoseTurn++;
                        if(gameState.whoseTurn > 4)
                            gameState.whoseTurn = 1;
                        if(gameState.hasPlayed[gameState.whoseTurn - 1]) // we've gone around the whole circle - no suit chosen, reshuffle and restart hand
                        {
                            gameState.phase = 1;
                            gameState.dealer++;
                            if(gameState.dealer > 4)
                                gameState.dealer = 1;
                            gameState.whoseTurn = gameState.dealer + 1;
                            if(gameState.whoseTurn > 4)
                                gameState.whoseTurn = 1;
                            for(int i = 0; i < 4; i++)
                                gameState.hasPlayed[i] = false;
                            for(int i = 0; i < 4; i++)
                                gameState.isReady[i] = false;
                            
                            // Reshuffle and deal deck
                            for(int i = 0; i < 4; i++)
                                while(!gameState.playerHands.get(i).empty())
                                    gameState.discardPile.push(gameState.playerHands.get(i).pop());
                            for(int i = 0; i < 4; i++)
                                if(gameState.playedCards[i] != null)
                                {
                                    gameState.discardPile.push(gameState.playedCards[i]);
                                    gameState.playedCards[i] = null;
                                }
                            gameState.discardPile.push(gameState.pickCard);
                            gameState.pickCard = null;
                            
                            Collections.shuffle(gameState.discardPile);
                            for(int i = 0; i < 5; i++) // deal 5 cards to each player
                                for(int j = 0; j < 4; j++)
                                    gameState.playerHands.get(j).add(gameState.discardPile.pop());
                            gameState.pickCard = gameState.discardPile.pop();
                        }
                    }
                    else if(choice.equals("clubs"))
                    {
                        gameState.trumpSuit = Card.Suit.CLUBS;
                        if(gameState.whoseTurn == 1 || gameState.whoseTurn == 3)
                            gameState.whoPickedTrump = 1;
                        else
                            gameState.whoPickedTrump = 2;
                        gameState.whoseTurn = gameState.dealer + 1;
                        if(gameState.whoseTurn > 4)
                            gameState.whoseTurn = 1;
                        for(int i = 0; i < 4; i++)
                            gameState.hasPlayed[i] = false;
                    }
                    else if(choice.equals("diamonds"))
                    {
                        gameState.trumpSuit = Card.Suit.DIAMONDS;
                        if(gameState.whoseTurn == 1 || gameState.whoseTurn == 3)
                            gameState.whoPickedTrump = 1;
                        else
                            gameState.whoPickedTrump = 2;
                        gameState.whoseTurn = gameState.dealer + 1;
                        if(gameState.whoseTurn > 4)
                            gameState.whoseTurn = 1;
                        for(int i = 0; i < 4; i++)
                            gameState.hasPlayed[i] = false;
                    }
                    else if(choice.equals("spades"))
                    {
                        gameState.trumpSuit = Card.Suit.SPADES;
                        if(gameState.whoseTurn == 1 || gameState.whoseTurn == 3)
                            gameState.whoPickedTrump = 1;
                        else
                            gameState.whoPickedTrump = 2;
                        gameState.whoseTurn = gameState.dealer + 1;
                        if(gameState.whoseTurn > 4)
                            gameState.whoseTurn = 1;
                        for(int i = 0; i < 4; i++)
                            gameState.hasPlayed[i] = false;
                    }
                    else if(choice.equals("hearts"))
                    {
                        gameState.trumpSuit = Card.Suit.HEARTS;
                        if(gameState.whoseTurn == 1 || gameState.whoseTurn == 3)
                            gameState.whoPickedTrump = 1;
                        else
                            gameState.whoPickedTrump = 2;
                        gameState.whoseTurn = gameState.dealer + 1;
                        if(gameState.whoseTurn > 4)
                            gameState.whoseTurn = 1;
                        for(int i = 0; i < 4; i++)
                            gameState.hasPlayed[i] = false;
                    }
                    else
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>invalidTrumpPick</error>");
                        out.close();
                        return;
                    }
                }
                else if(action.equals("playCard") && choice != null) // choice should be a card imgName
                {
                    if(gameState.phase != 4)
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>cannotTakeThatActionNow</error>");
                        out.close();
                        return;
                    }
                    
                    Card toPlay;
                    try { toPlay = new Card(choice); }
                    catch (Exception e)
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>invalidCard</error>");
                        out.close();
                        return;
                    }
                    
                    int indexToPlay = 0;
                    for(; indexToPlay < gameState.playerHands.get(gameState.whoseTurn - 1).size(); indexToPlay++)
                        if(toPlay.equals(gameState.playerHands.get(gameState.whoseTurn - 1).get(indexToPlay)))
                            break;
                    if(indexToPlay >= gameState.playerHands.get(gameState.whoseTurn - 1).size())
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>invalidCard</error>");
                        out.close();
                        return;
                    }
                    
                    // We know that the card is in the player's hand; now make sure it follows suit (if applicable).
                    boolean isLeader = true;
                    for(int i = 0; i < 4; i++)
                        if(gameState.playedCards[i] != null)
                            isLeader = false;
                    if(isLeader)
                        gameState.suitLead = gameState.playerHands.get(gameState.whoseTurn - 1).get(indexToPlay).getSuit();
                    else
                    {
                        if(gameState.playerHands.get(gameState.whoseTurn - 1).get(indexToPlay).getSuit() != gameState.suitLead)
                        {
                            // The card is not of the suit that was lead, but it might still be OK if the player doesn't
                            // have any cards of the suit lead.
                            boolean hasCardOfSuitLead = true;
                            for(int i = 0; i < gameState.playerHands.get(gameState.whoseTurn - 1).size(); i++)
                                if(gameState.playerHands.get(gameState.whoseTurn - 1).get(i).getSuit() == gameState.suitLead)
                                {
                                    hasCardOfSuitLead = true;
                                    break;
                                }
                            if(hasCardOfSuitLead)
                            {
                                PrintWriter out = response.getWriter();
                                out.println("<error>invalidCard</error>");
                                out.close();
                                return;
                            }
                        }
                    }
                    
                    // The card can be played, so we wil do so.
                    gameState.playedCards[gameState.whoseTurn - 1] = gameState.playerHands.get(gameState.whoseTurn - 1).remove(indexToPlay);
                    gameState.hasPlayed[gameState.whoseTurn - 1] = true;
                    gameState.whoseTurn++;
                    if(gameState.whoseTurn > 4)
                        gameState.whoseTurn = 1;
                    if(gameState.hasPlayed[gameState.whoseTurn - 1]) // we've gone around the whole circle - score the trick and start the next one
                    {
                        Card.Suit nextSuit; // the suit with the same color as the trump suit (whose jack is promoted)
                        switch(gameState.trumpSuit)
                        {
                            case CLUBS:
                                nextSuit = Card.Suit.SPADES;
                                break;
                            case DIAMONDS:
                                nextSuit = Card.Suit.HEARTS;
                                break;
                            case SPADES:
                                nextSuit = Card.Suit.CLUBS;
                                break;
                            default:
                                nextSuit = Card.Suit.DIAMONDS;
                        }
                        
                        Card winningCard = gameState.playedCards[0];
                        for(int i = 1; i < 4; i++)
                        {
                            if(gameState.playedCards[i].getFaceValue() == 11)
                            {
                                if(gameState.playedCards[i].getSuit() == gameState.trumpSuit)
                                    winningCard = gameState.playedCards[i];
                                else if(gameState.playedCards[i].getSuit() == nextSuit && winningCard.getSuit() != gameState.trumpSuit)
                                    winningCard = gameState.playedCards[i];
                            }
                            else if(gameState.playedCards[i].getSuit() == gameState.trumpSuit)
                            {
                                if(winningCard.getSuit() != gameState.trumpSuit && !(winningCard.getSuit() == nextSuit && winningCard.getFaceValue() == 11))
                                    winningCard = gameState.playedCards[i];
                            }
                            else if(gameState.playedCards[i].getFaceValue() > winningCard.getFaceValue())
                                winningCard = gameState.playedCards[i];
                            else if(gameState.playedCards[i].getSuit() == gameState.suitLead && winningCard.getSuit() != gameState.suitLead)
                                winningCard = gameState.playedCards[i];
                            else if(gameState.playedCards[i].getSuit() == gameState.suitLead && winningCard.getSuit() == gameState.suitLead)
                                if(gameState.playedCards[i].getFaceValue() > winningCard.getFaceValue())
                                    winningCard = gameState.playedCards[i];
                        }
                        
                        int winningPlayer = 0;
                        for(; winningPlayer < 4; winningPlayer++)
                            if(gameState.playedCards[winningPlayer].equals(winningCard))
                                break;
                        if(winningPlayer > 4)
                        {
                            PrintWriter out = response.getWriter();
                            out.println("<error>Something terribly wrong happened here.</error>");
                            out.close();
                            return;
                        }
                        
                        if(winningPlayer % 2 != 0) // winner is on 1-3 team (team 1)
                            gameState.team1TricksTaken++;
                        else // winner is on 2-4 team (team 2)
                            gameState.team2TricksTaken++;
                        
                        if(gameState.team1TricksTaken + gameState.team2TricksTaken == 5) // hand is over, go to phase 6
                        {
                            gameState.phase = 6;
                            if(gameState.whoPickedTrump == 1)
                                if(gameState.team1TricksTaken == 5)
                                    gameState.team1Score += 2;
                                else if(gameState.team1TricksTaken < 3)
                                    gameState.team2Score += 2; // team 1 has been euchred, give team 2 two points
                                else
                                    gameState.team1Score++;
                            else
                                if(gameState.team2TricksTaken == 5)
                                    gameState.team2Score += 2;
                                else if(gameState.team2TricksTaken < 3)
                                    gameState.team1Score += 2; // team 2 has been euchred, give team 1 two points
                                else
                                    gameState.team2Score++;
                            gameState.team1TricksTaken = 0;
                            gameState.team2TricksTaken = 0;
                            
                            if(gameState.team1Score == 10 && gameState.team2Score == 10) // game is over, go to phase 7
                                gameState.phase = 7; // immediately freezes the game, so no need to change anything else
                            else
                            {
                                gameState.dealer++;
                                if(gameState.dealer > 4)
                                    gameState.dealer = 1;
                                gameState.whoseTurn = gameState.dealer + 1;
                                if(gameState.whoseTurn > 4)
                                    gameState.whoseTurn = 1;
                                for(int i = 0; i < 4; i++)
                                {
                                    gameState.hasPlayed[i] = false;
                                    gameState.isReady[i] = false;
                                }

                                // Reshuffle and deal deck
                                for(int i = 0; i < 4; i++)
                                    while(!gameState.playerHands.get(i).empty())
                                        gameState.discardPile.push(gameState.playerHands.get(i).pop());
                                for(int i = 0; i < 4; i++)
                                    if(gameState.playedCards[i] != null)
                                    {
                                        gameState.discardPile.push(gameState.playedCards[i]);
                                        gameState.playedCards[i] = null;
                                    }
                                gameState.discardPile.push(gameState.pickCard);
                                gameState.pickCard = null;

                                Collections.shuffle(gameState.discardPile);
                                for(int i = 0; i < 5; i++) // deal 5 cards to each player
                                    for(int j = 0; j < 4; j++)
                                        gameState.playerHands.get(j).add(gameState.discardPile.pop());
                                gameState.pickCard = gameState.discardPile.pop();
                            }
                        }
                        else // trick is over, go to phase 5
                        {
                            gameState.phase = 5;
                            gameState.dealer++;
                            if(gameState.dealer > 4)
                                gameState.dealer = 1;
                            gameState.whoseTurn = gameState.dealer + 1;
                            if(gameState.whoseTurn > 4)
                                gameState.whoseTurn = 1;
                            for(int i = 0; i < 4; i++)
                            {
                                gameState.hasPlayed[i] = false;
                                gameState.isReady[i] = false;
                            }
                            
                            // Reshuffle and deal deck
                            for(int i = 0; i < 4; i++)
                                while(!gameState.playerHands.get(i).empty())
                                    gameState.discardPile.push(gameState.playerHands.get(i).pop());
                            for(int i = 0; i < 4; i++)
                                if(gameState.playedCards[i] != null)
                                {
                                    gameState.discardPile.push(gameState.playedCards[i]);
                                    gameState.playedCards[i] = null;
                                }
                            gameState.discardPile.push(gameState.pickCard);
                            gameState.pickCard = null;
                            
                            Collections.shuffle(gameState.discardPile);
                            for(int i = 0; i < 5; i++) // deal 5 cards to each player
                                for(int j = 0; j < 4; j++)
                                    gameState.playerHands.get(j).add(gameState.discardPile.pop());
                            gameState.pickCard = gameState.discardPile.pop();
                        }
                    }
                }
                else if(action.equals("readyUp")) // choice is not used
                {
                    if(gameState.phase == 5) // readying up for the next trick
                    {
                        gameState.isReady[gameState.whoseTurn - 1] = true;
                        gameState.whoseTurn++;
                        if(gameState.whoseTurn > 4)
                            gameState.whoseTurn = 1;
                        if(gameState.isReady[gameState.whoseTurn - 1]) // we've gone around the whole circle - continue to next trick (we've already shuffled and re-dealt above)
                        {
                            gameState.phase = 4;
                            gameState.whoseTurn = gameState.dealer + 1;
                            if(gameState.whoseTurn > 4)
                                gameState.whoseTurn = 1;
                        }
                    }
                    else if(gameState.phase == 6) // readying up for the next hand
                    {
                        gameState.isReady[gameState.whoseTurn - 1] = true;
                        gameState.whoseTurn++;
                        if(gameState.whoseTurn > 4)
                            gameState.whoseTurn = 1;
                        if(gameState.isReady[gameState.whoseTurn - 1]) // we've gone around the whole circle - continue to the next hand (we've already shuffled and re-dealt above)
                        {
                            gameState.phase = 4;
                            gameState.whoseTurn = gameState.dealer + 1;
                            if(gameState.whoseTurn > 4)
                                gameState.whoseTurn = 1;
                        }
                    }
                    else
                    {
                        PrintWriter out = response.getWriter();
                        out.println("<error>cannotTakeThatActionNow</error>");
                        out.close();
                        return;
                    }
                }
            }
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("ajaxGameState.jsp");
            dispatcher.forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
