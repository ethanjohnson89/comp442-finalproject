<%-- 
    Document   : ajaxGameState
    Created on : Dec 9, 2013, 8:21:52 PM
    Author     : GERBERMW1
--%><%@page import="java.util.Stack"
%><%@page import="Euchre.Card"
%><%@page import="Euchre.GameState"
%><%@page contentType="application/xml" pageEncoding="UTF-8"
%><?xml version="1.0" encoding="utf-8"?>
<% GameState gameState = (GameState)getServletContext().getAttribute("gameState");
   Integer playerNumber = (Integer)session.getAttribute("playerNumber"); %>
<table>
    <team>
        <teamNum>1</teamNum>
        <% if(gameState.whoPickedTrump == 1) { %>
        <trump><%= gameState.trumpSuit %></trump>
        <% } %>
        <score><%= gameState.team1Score %></score>
        <tricks><%= gameState.team1TricksTaken %></tricks>
        <% for(int p = 0; p < 3; p += 2) { %>
        <player>
            <playerNum>1</playerNum>
            <name><%= gameState.playerNames[p] %></name>
            <% if(gameState.dealer == p+1) { %>
            <dealer />
            <% } %>
            <% if(playerNumber == p+1) { %>
            <you />
            <% } %>
            <% if(gameState.whoseTurn == p+1) { %>
            <hasTurn />
            <% } %>
            <% if(gameState.hasPlayed[p]) { %>
            <hasPlayed />
            <% } %>
            <% if(gameState.isReady[p]) { %>
            <ready />
            <% } %>
            <% for(int i = 0; i < gameState.playerHands[p].size(); i++) { %>
            <handCard>
                <% if(playerNumber == p+1) { %>
                <overturned>true</overturned>
                <faceValue><%= gameState.playerHands[p].get(i).getFaceValue() %></faceValue>
                <suit><%= gameState.playerHands[p].get(i).getSuit() %></suit>
                <imgFileName><%= gameState.playerHands[p].get(i).getImgFileName() %></imgFileName>
                <% } else { %>
                <overturned>false</overturned>
                <imgFileName>back</imgFileName>
                <% } %>
            </handCard>
            <% } %>
            <% if(gameState.playedCards[p] != null) { %>
            <playedCard>
                <overturned>true</overturned>
                <faceValue><%= gameState.playedCards[p].getFaceValue() %></faceValue>
                <suit><%= gameState.playedCards[p].getSuit() %></suit>
                <imgFileName><%= gameState.playedCards[p].getImgFileName() %></imgFileName>
            </playedCard>
            <% } %>
        </player>
        <% } %>
    </team>
    <team>
        <teamNum>2</teamNum>
        <% if(gameState.whoPickedTrump == 2) { %>
        <trump><%= gameState.trumpSuit %></trump>
        <% } %>
        <score><%= gameState.team2Score %></score>
        <tricks><%= gameState.team2TricksTaken %></tricks>
        <% for(int p = 1; p < 4; p += 2) { %>
        <player>
            <playerNum>1</playerNum>
            <name><%= gameState.playerNames[p] %></name>
            <% if(gameState.dealer == p+1) { %>
            <dealer />
            <% } %>
            <% if(playerNumber == p+1) { %>
            <you />
            <% } %>
            <% if(gameState.whoseTurn == p+1) { %>
            <hasTurn />
            <% } %>
            <% if(gameState.hasPlayed[p]) { %>
            <hasPlayed />
            <% } %>
            <% if(gameState.isReady[p]) { %>
            <ready />
            <% } %>
            <% for(int i = 0; i < gameState.playerHands[p].size(); i++) { %>
            <handCard>
                <% if(playerNumber == p+1) { %>
                <overturned>true</overturned>
                <faceValue><%= gameState.playerHands[p].get(i).getFaceValue() %></faceValue>
                <suit><%= gameState.playerHands[p].get(i).getSuit() %></suit>
                <imgFileName><%= gameState.playerHands[p].get(i).getImgFileName() %></imgFileName>
                <% } else { %>
                <overturned>false</overturned>
                <imgFileName>back</imgFileName>
                <% } %>
            </handCard>
            <% } %>
            <% if(gameState.playedCards[p] != null) { %>
            <playedCard>
                <overturned>true</overturned>
                <faceValue><%= gameState.playedCards[p].getFaceValue() %></faceValue>
                <suit><%= gameState.playedCards[p].getSuit() %></suit>
                <imgFileName><%= gameState.playedCards[p].getImgFileName() %></imgFileName>
            </playedCard>
            <% } %>
        </player>
        <% } %>
    </team>
    <phase><%= gameState.phase %></phase>
    <suitLead><%= gameState.suitLead %></suitLead>
    <% if(gameState.pickCard != null) { %>
    <pickCard>
        <overturned>true</overturned>
        <faceValue><%= gameState.pickCard.getFaceValue() %></faceValue>
        <suit><%= gameState.pickCard.getSuit() %></suit>
        <imgFileName><%= gameState.pickCard.getImgFileName() %></imgFileName>
    </pickCard>
    <% } %>
</table>
