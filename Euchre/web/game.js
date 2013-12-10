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

// DEBUG
var globalResponseXML;

// We need to keep track of these so we know when we've started a new hand (and can tell who won the last one),
// by comparing them with the latest ones sent by the server.


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
    leftRightTrumpYes = document.getElementById("team2TrumpInd");
    
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
            console.log("Error: " + errorNode.childNode[0]);
            return;
        }
        
        var teamNodes = responseXMLRoot.getElementsByTagName("team");
        var yourTeamNum;
        if(teamNodes[0].getElementsByTagName("you"))
            yourTeamNum = 1;
        else
            yourTeamNum = 2;
        
        var playerNodes = responseXMLRoot.getElementsByTagName("player");
        var yourPlayerIndex, leftPlayerIndex, topPlayerIndex, rightPlayerIndex;
        var yourPlayerNumber, leftPlayerNumber, topPlayerNumber, rightPlayerNumber;
        if(playerNodes[0].getElementsByTagName("you"))
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
        else if(playerNodes[1].getElementsByTagName("you"))
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
        else if(playerNodes[2].getElementsByTagName("you"))
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
        else if(playerNodes[3].getElementsByTagName("you"))
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
    }
}