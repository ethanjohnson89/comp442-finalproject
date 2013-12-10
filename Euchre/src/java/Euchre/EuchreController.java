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
        
        GameState gameState = (GameState)servletContext.getAttribute("gameState");
        
        String resetEverything = request.getParameter("resetEverything");
        String enterGame = request.getParameter("enterGame");
        
        if(resetEverything != null && resetEverything.equals("true"))
        {
            gameState = null;
            servletContext.setAttribute("gameState", gameState);
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        }
        
        if(enterGame != null && enterGame.equals("true"))
        {
            if(gameState == null) // Build a fresh game state
            {
                gameState = new GameState();
                servletContext.setAttribute("gameState", gameState);

                gameState.phase = 0;
                Random randGen = new Random();
                gameState.dealer = randGen.nextInt(4) + 1;
                gameState.whoseTurn = ((gameState.dealer - 1) % 4) + 1;
                gameState.playerNames = new String[4];
                gameState.hasPlayed = new boolean[4];
                gameState.isReady = new boolean[4];

                // Shuffle the deck and deal to players
                gameState.discardPile = new Stack<Card>();
                for(Card.Suit s : Card.Suit.values())
                {
                    gameState.discardPile.push(new Card(s, 1)); // ace
                    for(int faceValue = 9; faceValue <= 13; faceValue++)
                        gameState.discardPile.push(new Card(s, faceValue));
                }
                Collections.shuffle(gameState.discardPile);

                for(int i = 0; i < 5; i++)
                {
                    gameState.player1Hand.add(gameState.discardPile.pop());
                    gameState.player2Hand.add(gameState.discardPile.pop());
                    gameState.player3Hand.add(gameState.discardPile.pop());
                    gameState.player4Hand.add(gameState.discardPile.pop());
                }
                gameState.pickCard = gameState.discardPile.pop();
                gameState.playedCards = new Card[4];

                gameState.team1Score = 0;
                gameState.team2Score = 0;
                gameState.team1TricksTaken = 0;
                gameState.team2TricksTaken = 0;

                RequestDispatcher dispatcher = request.getRequestDispatcher("game.jsp");
                dispatcher.forward(request, response);
            }
            else
            {

            }
        }
        else
        {
            
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
