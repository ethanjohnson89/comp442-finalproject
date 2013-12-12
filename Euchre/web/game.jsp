<%-- 
    Document   : game
    Created on : Dec 10, 2013, 2:38:24 AM
    Author     : GERBERMW1
--%>

<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTDxhtml1-strict.dtd">
	
<html>
	<head>
		<title>Euchre</title>
		<script type="text/javascript" src="game.js"></script>
		<meta http-equiv="Content-Script-Type" content="text/javascript"/>
		<style type="text/css">
		body	{ font-family: "Arial",sans-serif; background-color: seashell }
                .title  { font-size: 16pt; font-weight: bold }
                .info   { color: green; font-size: 12pt; font-weight: bold }
		
		td  {vertical-align:bottom; text-align:center}
		div.box
		{
		width: 72px;
		padding: 0px;
		border: 3px solid yellow;
		margin: 0px;
		height: 96px;
		}
		div.boxT1
		{
		width: 72px;
		padding: 2px;
		border: 1px solid red;
		margin: 0px;
		height: 20px;
                font-size: small;
		}
		div.boxT2
		{
		width: 72px;
		padding: 2px;
		border: 1px solid blue;
		margin: 0px;
		height: 20px;
                font-size: small;
		}
		div.box3
		{
		width: 78px;
		padding: 0px;
		border: 0px solid yellow;
		margin: 0px;
		height: 15px;	
		}
		div.boxBlank
		{
		width: 78px;
		padding: 0px;
		border: 0px solid yellow;
		margin: 0px;
		height: 96px;
		position: relative; 
		}
                div.boxBlank2
		{
		width: 78px;
		padding: 0px;
		border: 0px solid yellow;
		margin: 0px;
		height: 20px;
		position: relative; 
		}
		div span { position: absolute; bottom:0; left:0 }
		div.boxB
		{
		width: 72px;
		padding: 2px;
		border: 1px solid blue;
		margin: 0px;
		height: 96px;
		}
		div.boxR
		{
		width: 72px;
		padding: 2px;
		border: 1px solid red;
		margin: 0px;
		height: 96px;
		}
		div.boxQ
		{
		width: 72px;
		padding: 2px;
		border: 0px solid black;
		margin: 0px;
		height: 96px;
		}
  
		</style>
	</head>
	<body onload="init();">
	<table> <!-- move these guys to the right of the cards -->
            <!-- The <td>'s dealerHand and playerHand will be populated with card images by the doAjaxRequest() script function.
                    They will contain <span>'s in the "card" class, each containing one <img> element specifying the card image. !-->
			<tr>
				<td><div class="box3">  </div></td>
				<td><div class="box3"> </div></td>
				<td><div class="box3">  </div></td>
                                <td><div class="boxT1" id="topBottomTeam">Team 1</div></td>
                                <td><div class="box3" id="team1TrumpYes">Trump</div></td><!-- indicates team one called trump -->
				<td><div class="box3">  </div></td>
				<td><div class="box3">  </div></td>
                                <td><div class="boxT2" id="leftRightTeam">Team 2</div></td>
                                <td><div class="box3" id="team2TrumpYes">Trump</div></td><!-- indicates team two called trump  -->
				<td><div class="box3"> </div></td>
				<td><div class="box3">  </div></td>
			</tr>
			<tr>
				<td><div class="box3">  </div></td>
				<td><div class="box3">  </div></td>
				<td><div class="box3">  </div></td>
                                <td><div class="boxT1" id="topBottomScore">x</div></td>
                                <td><div class="box3" id="team1TrumpInd">Suit 1</div></td><!-- indicates the trump suit for team one -->
				<td><div class="box3">  </div></td>
				<td><div class="box3">  </div></td>
                                <td><div class="boxT2" id="leftRightScore">x</div></td>
                                <td><div class="box3" id="team2TrumpInd">Suit2</div></td><!-- indicates the trump suit for team two-->
				<td><div class="box3"> </div></td>
				<td><div class="box3">  </div></td>
			</tr>
			<tr>
				<td><div class="box3">  </div></td>
                                <td><div class="box3" id="left_name">Player 2</div></td><!-- player indicator -->
                                <td><div class="box3" id="left_dealer"> < dealer </div></td><!-- indicates player is dealer -->
                                <td><div class="boxT1" id="topBottomTricks">x</div></td><!-- number of tricks team one has taken -->
				<td><div class="box3"> </div></td>
                                <td><div class="box3" id="top_name">Player 3</div></td><!-- player indicator -->
                                <td><div class="box3" id="top_dealer"> < dealer </div></td><!-- indicates player is dealer -->
                                <td><div class="boxT2" id="leftRightTricks">x</div></td><!-- number of tricks team two has taken -->
				<td><div class="box3">  </div></td>
                                <td><div class="box3" id="right_name">Player 4</div></td><!-- player indicator -->
                                <td><div class="box3" id="right_dealer"> < dealer </div></td><!-- indicates player is dealer -->
			</tr>
		</table>
	<table> <!-- shift all of them down one row -->
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB" id="left_card1"></div></td><!-- P2's card -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxR" id="top_card1"></div></td><!-- P3's card -->
			<td><div class="boxR" id="top_card2"></div></td><!-- P3's card-->
			<td><div class="boxR" id="top_card3"></div></td><!-- P3's card-->
			<td><div class="boxR" id="top_card4"></div></td><!-- P3's card-->
			<td><div class="boxR" id="top_card5"></div></td><!-- P3's card-->
			<td><div class="boxBlank"></div></td>
			<td><div class="boxB" id="right_card1"></div></td><!-- P4's card -->
		</tr>
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB" id="left_card2"></div></td><!-- P2's card -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxR" id="top_cardplayed"></div></td><!-- card P3 played -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB" id="right_card2"></div></td><!-- P4's card -->
		</tr>
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB" id="left_card3"></div></td><!-- P2's card -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB" id="left_cardplayed"></div></td><!-- card P2 played -->
			<td><div class="boxQ" id="pickCard"></div></td><!-- card that can be picked up to declare trump-->
			<td><div class="boxB" id="right_cardplayed"></div></td><!-- card P4 played -->
			<td><div class="boxBlank"><span id="42"><button type="button" onclick="doAjaxRequest('readyUp', '');">Click when ready</button></span> </div></td><!-- ready up button -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB" id="right_card3"></div></td><!--P4's card -->
		</tr>
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB" id="left_card4"></div></td><!--P2's card -->
			<td><div class="boxBlank"> </div></td>
                        <td><div class="boxBlank" id="bottom_name"><span class="spanOne">Player 1<span></div></td><!-- player indicator -->
                                        <td><div class="boxBlank" id="bottom_dealer"><span class="spanOne">< dealer</span></div></td><!-- indicates player is dealer -->
			<td><div class="boxR" id="bottom_cardplayed"></div></td><!-- card P1 played -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank" id="discardInd">Discard:</div></td>
			<td><div class="boxB" id="right_card4"></div></td><!--P4's card -->
		</tr>
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB" id="left_card5"></div></td><!--P2's card -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxR" id="bottom_card1"></div></td><!-- P1's card -->
			<td><div class="boxR" id="bottom_card2"></div></td><!-- P1's card-->
			<td><div class="boxR" id="bottom_card3"></div></td><!-- P1's card-->
			<td><div class="boxR" id="bottom_card4"></div></td><!-- P1's card-->
			<td><div class="boxR" id="bottom_card5"></div></td><!-- P1's card-->
			<td><div class="boxBlank" id="bottom_card6"></div></td><!-- if the player wants to pick the card up it goes here-->
			<td><div class="boxB" id="right_card5"></div></td><!--P4's card -->
		</tr>
            	<tr>
			<td><div class="boxBlank2"> </div></td>
			<td><div class="boxBlank2"> </div></td>
			<td><div class="boxBlank2"> </div></td><!--these buttons all need an onclick function -->
			<!--<span id = "41">                      <!--titles on buttons subject to change -->
				<td><div class="boxBlank2" id="pickSuitButton0" ><button type="button" onclick="doAjaxRequest('pickTrump', 'hearts');">Hearts</button></div></td>
				<td><div class="boxBlank2" id="pickSuitButton1" ><button type="button" onclick="doAjaxRequest('pickTrump', 'spades');">Spades</button> </div></td>
				<td><div class="boxBlank2" id="pickSuitButton2" ><button type="button" onclick="doAjaxRequest('pickTrump', 'clubs');">Clubs</button> </div></td>
                                <td><div class="boxBlank2" id="pickSuitButton3" ><button type="button" onclick="doAjaxRequest('pickTrump', 'diamonds');">Diamonds</button> </div></td>
                                <td><div class="boxBlank2" id="pickSuitButton4" ><button type="button" onclick="doAjaxRequest('pickTrump', 'pass');">Pass</button> </div></td>
			<!--</span> -->
		</tr>
                <tr>
                        <td><div class="boxBlank2"> </div></td>
			<td><div class="boxBlank2"> </div></td>
			<td><div class="boxBlank2"> </div></td>
			<td><div class="boxBlank2"> </div></td><!--these buttons all need an onclick function -->
			<!--<span id = "pickUpButtons">                      <!--titles on buttons subject to change -->
				<td><div class="boxBlank2" id="pickUp0" >Pick up?</div></td>
				<td><div class="boxBlank2" id="pickUp1" ><button type="button" onclick="doAjaxRequest('pickUp', 'yes');">Yes</button> </div></td>
				<td><div class="boxBlank2" id="pickUp2" ><button type="button" onclick="doAjaxRequest('pickUp', 'no');">No</button> </div></td>
			<!--</span> -->
                </tr>
                <tr>
                        <td><div class="boxBlank2"> </div></td>
			<td><div class="boxBlank2"> </div></td>
			<td><div class="boxBlank2"> </div></td>
			<td><div class="boxBlank2"> </div></td>
			<!--<span id = "whoseTurnDisplay"> -->
                            <td><div class="boxBlank2" id="turn0"><strong>Turn:</strong></div></td>
                            <td><div class="boxBlank2" id="turn1"><strong><span id="whoseTurn">x</span></strong></div></td>
			<!--</span> -->
                </tr>
	</table>
        <p id="info" class="info"></p>
	
	</body>
</html>