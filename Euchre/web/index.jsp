<%-- 
    Document   : index
    Created on : Dec 9, 2013, 5:38:52 PM
    Author     : JOHNSONEJ1
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Euchre</title>
        <style type="text/css">
		body	{ font-family: "Arial",sans-serif; background-color: seashell }
                .supertiny { font-size: 8pt; }
	</style>
    </head>
    <body>
        <h1>Welcome to Euchre!</h1>
        <h3>Enter your name to join the game:</h3>
        <form method="post" action="EuchreController">
            <input type="text" id="loginName" size="15" />
            <input type="hidden" id="enterGame" />
            <input type="submit" id="submitButton" />
        </form>
        <p class="supertiny"><a href="EuchreController?resetEverything=true">Click here</a> to reset all game state and
            allow a new game to be started if things get wonky</p>
    </body>
</html>