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
                .info   { color: green }
		
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
		border: 1px solid blue;
		margin: 0px;
		height: 15px;
		}
		div.boxT2
		{
		width: 72px;
		padding: 2px;
		border: 1px solid red;
		margin: 0px;
		height: 15px;
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
	<body>
	<table> <!-- move these guys to the right of the cards -->
            <!-- The <td>'s dealerHand and playerHand will be populated with card images by the doAjaxRequest() script function.
                    They will contain <span>'s in the "card" class, each containing one <img> element specifying the card image. !-->
			<tr>
				<td><div class="box3">  </div></td>
				<td><div class="box3"> </div></td>
				<td><div class="box3">  </div></td>
				<td><div class="boxT1">Team 1</div></td>
				<td><div class="box3">Trump 1</div></td><!-- indicates team one called trump -->
				<td><div class="box3">  </div></td>
				<td><div class="box3">  </div></td>
				<td><div class="boxT2">Team 2</div></td>
				<td><div class="box3">Trump 2</div></td><!-- indicates team two called trump  -->
				<td><div class="box3"> </div></td>
				<td><div class="box3">  </div></td>
			</tr>
			<tr>
				<td><div class="box3">  </div></td>
				<td><div class="box3">  </div></td>
				<td><div class="box3">  </div></td>
				<td><div class="boxT1">Score</div></td>
				<td><div class="box3">Suit 1</div></td><!-- indicates the trump suit for team one -->
				<td><div class="box3">  </div></td>
				<td><div class="box3">  </div></td>
				<td><div class="boxT2">Score</div></td>
				<td><div class="box3">Suit2 </div></td><!-- indicates the trump suit for team two-->
				<td><div class="box3"> </div></td>
				<td><div class="box3">  </div></td>
			</tr>
			<tr>
				<td><div class="box3">  </div></td>
				<td><div class="box3">Player 2</div></td><!-- player indicator -->
				<td><div class="box3"> < dealer </div></td><!-- indicates player is dealer -->
				<td><div class="boxT1">tricks: 5</div></td><!-- number of tricks team one has taken -->
				<td><div class="box3"> </div></td>
				<td><div class="box3">Player 3</div></td><!-- player indicator -->
				<td><div class="box3"> < dealer </div></td><!-- indicates player is dealer -->
				<td><div class="boxT2">tricks</div></td><!-- number of tricks team two has taken -->
				<td><div class="box3">  </div></td>
				<td><div class="box3">Player 4</div></td><!-- player indicator -->
				<td><div class="box3"> < dealer </div></td><!-- indicates player is dealer -->
			</tr>
		</table>
	<table> <!-- shift all of them down one row -->
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="1" src="back.png" onclick="show('1');" /> </div></td><!-- P2's card -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxR"> <img id="11" src="back.png" onclick="show('11');" /> </div></td><!-- P3's card -->
			<td><div class="boxR"> <img id="12" src="back.png" onclick="show('12');" /> </div></td><!-- P3's card-->
			<td><div class="boxR"> <img id="13" src="back.png" onclick="show('13');" /> </div></td><!-- P3's card-->
			<td><div class="boxR"> <img id="14" src="back.png" onclick="show('14');" /> </div></td><!-- P3's card-->
			<td><div class="boxR"> <img id="15" src="back.png" onclick="show('15');" /> </div></td><!-- P3's card-->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="6" src="back.png" onclick="show('6');" /> </div></td><!-- P4's card -->
		</tr>
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="2" src="back.png" onclick="show('2');" /> </div></td><!-- P2's card -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxR"><img id="21" src="qd.png" onclick="show('21');"  /></div></td><!-- card P3 played -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="7" src="back.png" onclick="show('7');" /> </div></td><!-- P4's card -->
		</tr>
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="3" src="back.png" onclick="show('3');" /> </div></td><!-- P2's card -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"><img id="22" src="kc.png" onclick="show('22');"  /></div></td><!-- card P2 played -->
			<td><div class="boxQ"><img id="30" src="ks.png" onclick="show('30');"  /></div></td><!-- card that can be picked up to declare trump-->
			<td><div class="boxB"><img id="23" src="jc.png" onclick="show('23');"  /> </div></td><!-- card P4 played -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="8" src="back.png" onclick="show('8');" /> </div></td><!--P4's card -->
		</tr>
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="4" src="back.png" onclick="show('4');" /> </div></td><!--P2's card -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"><span>Player 1</span></div></td><!-- player indicator -->
			<td><div class="boxBlank"><span>< dealer</span></div></td><!-- indicates player is dealer -->
			<td><div class="boxR"><img id="24" src="9h.png" onclick="show('24');"  /></div></td><!-- card P1 played -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="9" src="back.png" onclick="show('9');" /> </div></td><!--P4's card -->
		</tr>
		<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="5" src="back.png" onclick="show('5');" /> </div></td><!--P2's card -->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxR"> <img id="16" src="9s.png" onclick="show('16');"  /> </div></td><!-- P1's card -->
			<td><div class="boxR"> <img id="17" src="10s.png" onclick="show('17');"  /> </div></td><!-- P1's card-->
			<td><div class="boxR"> <img id="18" src="js.png" onclick="show('18');"  /> </div></td><!-- P1's card-->
			<td><div class="boxR"> <img id="19" src="qs.png" onclick="show('19');"  /> </div></td><!-- P1's card-->
			<td><div class="boxR"> <img id="20" src="ks.png" onclick="show('20');"  /> </div></td><!-- P1's card-->
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxB"> <img id="10" src="back.png" onclick="show('10');" /> </div></td><!--P4's card -->
		</tr>
            	<tr>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank"> </div></td>
			<td><div class="boxBlank" id="41"><button type="button" onclick="">Hearts</button></div></td>
			<td><div class="boxBlank" id="42"><button type="button" onclick="">Spades</button> </div></td>
			<td><div class="boxBlank" id="43"><button type="button" onclick="">Clubs</button> </div></td>
		</tr>
	</table>
	
	</body>
</html>