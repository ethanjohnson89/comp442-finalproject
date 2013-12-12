// Global handles to freqeuently accessed DOM nodes
var bottom_cards; // array of 6 handles to div nodes (cards 1-6)
var top_cards; // top, left, and right only have 5 cards (no extra space)
var left_cards;
var right_cards;

var bottom_card_played;
var top_card_played;
var left_card_played;
var right_card_played;

var pickCardNode;

var readyButtonSpan;
var suitSelectButtonsSpan;
var pickUpButtonsSpan;

var bottomNameInd;
var topNameInd;
var leftNameInd;
var rightNameInd;

var bottomDealerInd;
var topDealerInd;
var leftDealerInd;
var rightDealerInd;

var topBottomTeamInd;
var leftRightTeamInd;
var topBottomScoreInd;
var leftRightScoreInd;
var topBottomTricksInd;
var leftRightTricksInd;

var topBottomTrumpYes;
var leftRightTrumpYes;
var topBottomTrumpInd;
var leftRightTrumpInd;

var whoseTurnInd;
var infoNode;

// DEBUG
var globalResponseXML;

// We need to keep track of these so we know when we've started a new hand (and can tell who won the last one),
// by comparing them with the latest ones sent by the server.
var whoseTurn; // 1, 2, 3, or 4
var dealer;

function init()
{
    // init node globals
    bottom_cards = new Array();
    bottom_cards.push(document.getElementById("bottom_card1"));
    bottom_cards.push(document.getElementById("bottom_card2"));
    bottom_cards.push(document.getElementById("bottom_card3"));
    bottom_cards.push(document.getElementById("bottom_card4"));
    bottom_cards.push(document.getElementById("bottom_card5"));
    bottom_cards.push(document.getElementById("bottom_card6"));
    
    top_cards = new Array();
    top_cards.push(document.getElementById("top_card1"));
    top_cards.push(document.getElementById("top_card2"));
    top_cards.push(document.getElementById("top_card3"));
    top_cards.push(document.getElementById("top_card4"));
    top_cards.push(document.getElementById("top_card5"));
    
    left_cards = new Array();
    left_cards.push(document.getElementById("left_card1"));
    left_cards.push(document.getElementById("left_card2"));
    left_cards.push(document.getElementById("left_card3"));
    left_cards.push(document.getElementById("left_card4"));
    left_cards.push(document.getElementById("left_card5"));
    
    right_cards = new Array();
    right_cards.push(document.getElementById("right_card1"));
    right_cards.push(document.getElementById("right_card2"));
    right_cards.push(document.getElementById("right_card3"));
    right_cards.push(document.getElementById("right_card4"));
    right_cards.push(document.getElementById("right_card5"));
    
    bottom_card_played = document.getElementById("bottom_cardplayed");
    top_card_played = document.getElementById("top_cardplayed");
    left_card_played = document.getElementById("left_cardplayed");
    right_card_played = document.getElementById("right_cardplayed");
    
    pickCardNode = document.getElementById("pickCard");
    
    readyButtonSpan = document.getElementById("42");
    suitSelectButtonsSpan = document.getElementById("41");
    pickUpButtonsSpan = document.getElementById("pickUpButtons");
    
    bottomNameInd = document.getElementById("bottom_name");
    topNameInd = document.getElementById("top_name");
    leftNameInd = document.getElementById("left_name");
    rightNameInd = document.getElementById("right_name");
    
    bottomDealerInd = document.getElementById("bottom_dealer");
    topDealerInd = document.getElementById("top_dealer");
    leftDealerInd = document.getElementById("left_dealer");
    rightDealerInd = document.getElementById("right_dealer");
    
    topBottomTeamInd = document.getElementById("topBottomTeam");
    leftRightTeamInd = document.getElementById("leftRightTeam");
    topBottomScoreInd = document.getElementById("topBottomScore");
    leftRightScoreInd = document.getElementById("leftRightScore");
    topBottomTricksInd = document.getElementById("topBottomTricks");
    leftRightTricksInd = document.getElementById("leftRightTricks");
    
    topBottomTrumpYes = document.getElementById("team1TrumpYes");
    leftRightTrumpYes = document.getElementById("team2TrumpYes");
    topBottomTrumpInd = document.getElementById("team1TrumpInd");
    leftRightTrumpInd = document.getElementById("team2TrumpInd");
    
    whoseTurnInd = document.getElementById("whoseTurn");
    infoNode = document.getElementById("info");
    
    // init value globals
    
    doAjaxRequest("", "");
}

// action: string specifying either "hit", "stand", or "playDealer"; if empty, no action is performed
// (just pull current game status from server)
function doAjaxRequest(action, choice)
{
    var connection = new XMLHttpRequest();
    
    if(connection)
    {
        connection.open("POST", "EuchreController", true);
        connection.onreadystatechange = function() { updateGameState(connection); };
        connection.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        connection.send("action=" + action + "&choice=" + choice);
    }
    
    return;
}

// callback function for AJAX requests initiated by doAjaxRequest()
function updateGameState(connection)
{
    if(connection.readyState === 4 && connection.status === 200)
    {
        var responseXMLRoot = connection.responseXML.documentElement;
        globalResponseXML = responseXMLRoot; // DEBUG
        
        var errorNode = responseXMLRoot.getElementsByTagName("error")[0];
        if(errorNode)
        {
            window.setTimeout(function() { doAjaxRequest("", ""); }, 1500);
            console.log("Error: " + errorNode.childNode[0]);
            return;
        }
        
        var teamNodes = responseXMLRoot.getElementsByTagName("team");
        var yourTeamNum;
        if(teamNodes[0].getElementsByTagName("you")[0])
            yourTeamNum = 1;
        else
            yourTeamNum = 2;
        
        var playerNodes = responseXMLRoot.getElementsByTagName("player");
        var yourPlayerIndex, leftPlayerIndex, topPlayerIndex, rightPlayerIndex;
        var yourPlayerNumber, leftPlayerNumber, topPlayerNumber, rightPlayerNumber;
        if(playerNodes[0].getElementsByTagName("you")[0])
        {
            yourPlayerNumber = 1;
            yourPlayerIndex = 0;
            leftPlayerNumber = 2;
            leftPlayerIndex = 2;
            topPlayerNumber = 3;
            topPlayerIndex = 1;
            rightPlayerNumber = 4;
            rightPlayerIndex = 3;
        }
        else if(playerNodes[1].getElementsByTagName("you")[0])
        {
            yourPlayerNumber = 3;
            yourPlayerIndex = 1;
            leftPlayerNumber = 4;
            leftPlayerIndex = 3;
            topPlayerNumber = 1;
            topPlayerIndex = 0;
            rightPlayerNumber = 2;
            rightPlayerIndex = 2;
        }
        else if(playerNodes[2].getElementsByTagName("you")[0])
        {
            yourPlayerNumber = 2;
            yourPlayerIndex = 2;
            leftPlayerNumber = 3;
            leftPlayerIndex = 1;
            topPlayerNumber = 4;
            topPlayerIndex = 3;
            rightPlayerNumber = 1;
            rightPlayerIndex = 0;
        }
        else if(playerNodes[3].getElementsByTagName("you")[0])
        {
            yourPlayerNumber = 4;
            yourPlayerIndex = 3;
            leftPlayerNumber = 1;
            leftPlayerIndex = 0;
            topPlayerNumber = 2;
            topPlayerIndex = 2;
            rightPlayerNumber = 3;
            rightPlayerIndex = 1;
        }
        
        var yourName = playerNodes[yourPlayerIndex].getElementsByTagName("name")[0].childNodes[0].data;
        var leftPlayerName = playerNodes[leftPlayerIndex].getElementsByTagName("name")[0].childNodes[0].data;
        var topPlayerName = playerNodes[topPlayerIndex].getElementsByTagName("name")[0].childNodes[0].data;
        var rightPlayerName = playerNodes[rightPlayerIndex].getElementsByTagName("name")[0].childNodes[0].data;
        
        if(leftPlayerName === "null")
            leftPlayerName = "Waiting...";
        if(topPlayerName === "null")
            topPlayerName = "Waiting...";
        if(rightPlayerName === "null")
            rightPlayerName = "Waiting...";
        
        bottomNameInd.children[0].childNodes[0].data = yourName;
        leftNameInd.childNodes[0].data = leftPlayerName;
        topNameInd.childNodes[0].data = topPlayerName;
        rightNameInd.childNodes[0].data = rightPlayerName;
        
        var phase = Number(responseXMLRoot.getElementsByTagName("phase")[0].childNodes[0].data);
        
        if(playerNodes[0].getElementsByTagName("hasTurn")[0])
            whoseTurn = 1;
        else if(playerNodes[1].getElementsByTagName("hasTurn")[0])
            whoseTurn = 3;
        else if(playerNodes[2].getElementsByTagName("hasTurn")[0])
            whoseTurn = 2;
        else if(playerNodes[3].getElementsByTagName("hasTurn")[0])
            whoseTurn = 4;
        whoseTurnInd.childNodes[0].data = "Player " + whoseTurn;
        
        if(playerNodes[0].getElementsByTagName("dealer")[0])
            dealer = 1;
        else if(playerNodes[1].getElementsByTagName("dealer")[0])
            dealer = 3;
        else if(playerNodes[2].getElementsByTagName("dealer")[0])
            dealer = 2;
        else if(playerNodes[3].getElementsByTagName("dealer")[0])
            dealer = 4;
        
        bottomDealerInd.style.display = "none";
        leftDealerInd.style.display = "none";
        topDealerInd.style.display = "none";
        rightDealerInd.style.display = "none";
        
        console.log("dealer is" + dealer);
            console.log("you are player " + yourPlayerNumber);
            console.log("turn is" + whoseTurn);
        
        if(yourPlayerNumber === dealer)
            bottomDealerInd.style.display = "block";
        else if(leftPlayerNumber === dealer)
            leftDealerInd.style.display = "block";
        else if(topPlayerNumber === dealer)
            topDealerInd.style.display = "block";
        else if(rightPlayerNumber === dealer)
            rightDealerInd.style.display = "block";
        
        readyButtonSpan.style.display = "none";
        //suitSelectButtonsSpan.style.display = "none";
        hidePickSuit();
        //pickUpButtonsSpan.style.display = "none";
        hidePickUp();
        
        // Update scores and # of tricks taken
        var team1Score = Number(teamNodes[0].getElementsByTagName("score")[0].childNodes[0].data);
        var team2Score = Number(teamNodes[1].getElementsByTagName("score")[0].childNodes[0].data);
        var team1Tricks = Number(teamNodes[0].getElementsByTagName("tricks")[0].childNodes[0].data);
        var team2Tricks = Number(teamNodes[1].getElementsByTagName("tricks")[0].childNodes[0].data);
        var topBottomScore, leftRightScore, topBottomTricks, leftRightTricks;
        if(yourTeamNum === 1)
        {
            topBottomScore = team1Score;
            leftRightScore = team2Score;
            topBottomTricks = team1Tricks;
            leftRightTricks = team2Tricks;
            topBottomTeamInd.childNodes[0].data = "Team 1";
            leftRightTeamInd.childNodes[0].data = "Team 2";
        }
        else
        {
            topBottomScore = team2Score;
            leftRightScore = team1Score;
            topBottomTricks = team2Tricks;
            leftRightTricks = team1Tricks;
            topBottomTeamInd.childNodes[0].data = "Team 2";
            leftRightTeamInd.childNodes[0].data = "Team 1";
        }
        topBottomScoreInd.childNodes[0].data = "Score: " + topBottomScore;
        leftRightScoreInd.childNodes[0].data = "Score: " + leftRightScore;
        topBottomTricksInd.childNodes[0].data = "Tricks: " + topBottomTricks;
        leftRightTricksInd.childNodes[0].data = "Tricks: " + leftRightTricks;
        
        // TODO: display current cards on screen and update trump indicators
        var bottomNewCards = playerNodes[yourPlayerIndex].getElementsByTagName("handCard");
        var index;
        for(var index = 0; index < bottomNewCards.length; index++)
        {
            var imgFileName = bottomNewCards[index].getElementsByTagName("imgFileName")[0].childNodes[0].data;
            
            var img = bottom_cards[index].children[0];
            if(!img)
            {
                img = document.createElement("img");
                img.setAttribute("class", "cardImg");
                bottom_cards[index].appendChild(img);
            }
            
            img.setAttribute("src", "img/" + imgFileName + ".png");
            // It's safe to not check whether the card is playable before sending the AJAX, because the server will simply
            // ignore any invalid moves. Since we're not immediately removing the card from the screen when it's clicked
            // (rather, it will be removed when the server responds, sending down the fresh game state where this card
            // is no longer in the player's hand), an incorrect move will not cause any weird behavior for the user
            // either - simply nothing will happen, which is what we want.
            img.setAttribute("onclick", "doAjaxRequest('playCard', '" + imgFileName + "');");
        }
        for(; index < 6; index++)
        {
            var img = bottom_cards[index].children[0];
            if(img)
                bottom_cards[index].removeChild(img);
        }
        var leftNewCards = playerNodes[leftPlayerIndex].getElementsByTagName("handCard");
        for(index = 0; index < leftNewCards.length; index++)
        {
            if(index >= 5)
                break;
            var imgFileName = leftNewCards[index].getElementsByTagName("imgFileName")[0].childNodes[0].data;
            
            var img = left_cards[index].children[0];
            if(!img)
            {
                img = document.createElement("img");
                img.setAttribute("class", "cardImg");
                left_cards[index].appendChild(img);
            }
            
            img.setAttribute("src", "img/" + imgFileName + ".png");
        }
        for(; index < 5; index++)
        {
            var img = left_cards[index].children[0];
            if(img)
                left_cards[index].removeChild(img);
        }
        var topNewCards = playerNodes[topPlayerIndex].getElementsByTagName("handCard");
        for(index = 0; index < topNewCards.length; index++)
        {
            if(index >= 5)
                break;
            var imgFileName = topNewCards[index].getElementsByTagName("imgFileName")[0].childNodes[0].data;
            
            var img = top_cards[index].children[0];
            if(!img)
            {
                img = document.createElement("img");
                img.setAttribute("class", "cardImg");
                top_cards[index].appendChild(img);
            }
            
            img.setAttribute("src", "img/" + imgFileName + ".png");
        }
        for(; index < 5; index++)
        {
            var img = top_cards[index].children[0];
            if(img)
                top_cards[index].removeChild(img);
        }
        var rightNewCards = playerNodes[rightPlayerIndex].getElementsByTagName("handCard");
        for(index = 0; index < rightNewCards.length; index++)
        {
            if(index >= 5)
                break;
            var imgFileName = rightNewCards[index].getElementsByTagName("imgFileName")[0].childNodes[0].data;
            
            var img = right_cards[index].children[0];
            if(!img)
            {
                img = document.createElement("img");
                img.setAttribute("class", "cardImg");
                right_cards[index].appendChild(img);
            }
            
            img.setAttribute("src", "img/" + imgFileName + ".png");
        }
        for(; index < 5; index++)
        {
            var img = right_cards[index].children[0];
            if(img)
                right_cards[index].removeChild(img);
        }
        var bottomPlayedCard = playerNodes[yourPlayerIndex].getElementsByTagName("playedCard")[0];
        var leftPlayedCard = playerNodes[leftPlayerIndex].getElementsByTagName("playedCard")[0];
        var topPlayedCard = playerNodes[topPlayerIndex].getElementsByTagName("playedCard")[0];
        var rightPlayedCard = playerNodes[rightPlayerIndex].getElementsByTagName("playedCard")[0];
        var pickCard = responseXMLRoot.getElementsByTagName("pickCard")[0];
        if(bottomPlayedCard)
        {
            var imgFileName = bottomPlayedCard.getElementsByTagName("imgFileName")[0].childNodes[0].data;
            
            var img = bottom_card_played.children[0];
            if(!img)
            {
                img = document.createElement("img");
                img.setAttribute("class", "cardImg");
                bottom_card_played.appendChild(img);
            }
            
            img.setAttribute("src", "img/" + imgFileName + ".png");
        }
        else
        {
            var img = bottom_card_played.children[0];
            if(img)
                bottom_card_played.removeChild(img);
        }
        if(leftPlayedCard)
        {
            var imgFileName = leftPlayedCard.getElementsByTagName("imgFileName")[0].childNodes[0].data;
            
            var img = left_card_played.children[0];
            if(!img)
            {
                img = document.createElement("img");
                img.setAttribute("class", "cardImg");
                left_card_played.appendChild(img);
            }
            
            img.setAttribute("src", "img/" + imgFileName + ".png");
        }
        else
        {
            var img = left_card_played.children[0];
            if(img)
                left_card_played.removeChild(img);
        }
        if(topPlayedCard)
        {
            var imgFileName = topPlayedCard.getElementsByTagName("imgFileName")[0].childNodes[0].data;
            
            var img = top_card_played.children[0];
            if(!img)
            {
                img = document.createElement("img");
                img.setAttribute("class", "cardImg");
                top_card_played.appendChild(img);
            }
            
            img.setAttribute("src", "img/" + imgFileName + ".png");
        }
        else
        {
            var img = top_card_played.children[0];
            if(img)
                top_card_played.removeChild(img);
        }
        if(rightPlayedCard)
        {
            var imgFileName = rightPlayedCard.getElementsByTagName("imgFileName")[0].childNodes[0].data;
            
            var img = right_card_played.children[0];
            if(!img)
            {
                img = document.createElement("img");
                img.setAttribute("class", "cardImg");
                right_card_played.appendChild(img);
            }
            
            img.setAttribute("src", "img/" + imgFileName + ".png");
        }
        else
        {
            var img = right_card_played.children[0];
            if(img)
               right_card_played.removeChild(img);
        }
        if(pickCard)
        {
            var imgFileName = pickCard.getElementsByTagName("imgFileName")[0].childNodes[0].data;
            
            var img = pickCardNode.children[0];
            if(!img)
            {
                img = document.createElement("img");
                img.setAttribute("class", "cardImg");
                pickCardNode.appendChild(img);
            }
            
            img.setAttribute("src", "img/" + imgFileName + ".png");
        }
        else
        {
            var img = pickCardNode.children[0];
            if(img)
                pickCardNode.removeChild(img);
        }
        
        // Main game logic
        if(whoseTurn === yourPlayerNumber)
        {
            switch(phase)
            {
                case 0: // waiting for players to join the game
                    // Set a 1.5-second AJAX request timer to refresh and see if anyone's joined
                    window.setTimeout(function() { doAjaxRequest("", ""); }, 1500);
                    // (this should never happen, since the turn is immediately passed on to the next guy when you join)
                    break;
                case 1: // choosing whether the dealer should pick up the turned-over card
                    //pickUpButtonsSpan.style.display = "inline";
                    showPickUp();

                    break;
                case 2: // waiting for the dealer to choose a discard after taking the turned-over card
                    // nothing to do here, just wait for a card to be clicked

                    break;
                case 3: // picking the trump suit (if the dealer didn't pick up)
                    //suitSelectButtonsSpan.style.display = "inline";
                    showPickSuit();

                    break;
                case 4: // during the hand
                    // nothing to do here, just wait for a card to be clicked

                    break;
                case 5: // between tricks (waiting for players to ready up - this is automatically initiated by a JS timer on the client side)
                    window.setTimeout(function() { doAjaxRequest("readyUp", ""); }, 1500);

                    break;
                case 6: // between hands (waiting for players to ready up - they need to click a button)
                    readyButtonSpan.style.display = "inline";

                    break;
                case 7: // game over
                    var resultInfoDiv = document.createElement("div");
                    if(topBottomScore > leftRightScore)
                        resultInfoDiv.appendChild(document.createTextNode("Game over - team " + yourTeamNum + " wins!"));
                    else
                    {
                        var otherTeam;
                        if(yourTeamNum === 1)
                            otherTeam = 2;
                        else
                            otherTeam = 1;
                        resultInfoDiv.appendChild(document.createTextNode("Game over - team " + otherTeam + " wins."));
                    }
                    infoNode.appendChild(resultInfoDiv);
                    resultInfoDiv = document.createElement("div")
                    var linkNode = document.createElement("a");
                    linkNode.setAttribute("href", "index.jsp");
                    linkNode.appendChild(document.createTextNode("Click here"));
                    resultInfoDiv.appendChild(linkNode);
                    resultInfoDiv.appendChild(document.createTextNode(" to return to the main menu."));
                    infoNode.appendChild(resultInfoDiv);
            }
        }
        else
            window.setTimeout(function() { doAjaxRequest("", ""); }, 1500);
    }
}

function showPickSuit()
{
    
}

function hidePickSuit()
{
    
}

function showPickUp()
{
    
}

function hidePickUp()
{
    
}