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
        
        // The PrintWriter will be used when we need to return simple XML indicating an error;
        // for more complex responses, we'll forward to a .jsp page.
        response.setContentType("application/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
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
        }
        
        if(enterGame != null && enterGame.equals("true") && loginName != null && !loginName.isEmpty())
        {
            if(gameState == null) // Build a fresh game state
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

                for(int i = 0; i < 5; i++) // deal 5 cards to each player
                    for(int j = 0; j < 4; j++)
                        gameState.playerHands[j].add(gameState.discardPile.pop());
                gameState.pickCard = gameState.discardPile.pop();
                gameState.playedCards = new Card[4];

                gameState.team1Score = 0;
                gameState.team2Score = 0;
                gameState.team1TricksTaken = 0;
                gameState.team2TricksTaken = 0;

                RequestDispatcher dispatcher = request.getRequestDispatcher("game.jsp");
                dispatcher.forward(request, response);
            }
            else // we're entering a game that already has players
            {
                if(gameState.phase != 0) // we're no longer accepting new players (game is full)
                {
                    session.setAttribute("error", "Sorry, game is full! Please try again later.");
                    RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
                    dispatcher.forward(request, response);
                }
                
                gameState.playerNames[gameState.whoseTurn - 1] = loginName;
                session.setAttribute("loginName", loginName);
                gameState.isReady[gameState.whoseTurn - 1] = true;
                gameState.whoseTurn++;
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("game.jsp");
                dispatcher.forward(request, response);
            }
        }
        else // this is an AJAX request
        {
            if(gameState == null)
            {
                session.setAttribute("error", "Unknown internal error! You will probably need to reset the game with the link below.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
                dispatcher.forward(request, response);
            }
            
            String sessionLoginName = (String)session.getAttribute("loginName");
            if(sessionLoginName == null)
            {
                session.setAttribute("error", "No username token sent! Try joining the game again.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
                dispatcher.forward(request, response);
            }

            int playerNumber;
            for(playerNumber = 1; playerNumber <= 4; playerNumber++)
                if(gameState.playerNames[playerNumber-1].equals(sessionLoginName))
                    break;
            if(playerNumber > 4)
            {
                session.setAttribute("error", "Sorry, you're not in this game! Try joining the game again.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
                dispatcher.forward(request, response);
            }
            
            
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
