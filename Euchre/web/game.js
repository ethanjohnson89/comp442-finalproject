// Global handles to freqeuently accessed DOM nodes


// DEBUG
var globalResponseXML;

// We need to keep track of these so we know when we've started a new hand (and can tell who won the last one),
// by comparing them with the latest ones sent by the server.


function init()
{
    // init node globals
    
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
        
        
    }
}