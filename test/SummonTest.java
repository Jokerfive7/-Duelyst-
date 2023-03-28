import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import commands.TileHighlight;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Unit;

public class SummonTest {
	
	@Test
	public void checkSummon(){
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;
		
		GameState gameState = new GameState(); // create state storage
		Initalize initalizeProcessor =  new Initalize(); // create an initalize event processor
		// lets simulate recieveing an initalize message
		ObjectNode eventMessage = Json.newObject(); // create a dummy message
		initalizeProcessor.processEvent(null, gameState, eventMessage); // send it to the initalize event processor
		
		//highlight the tiles around human avatar tile(1,2) when summoning
		for (Unit u : gameState.gethumanPlayerUnits()) {
			TileHighlight.summonHighlight(gameState, u.getPosition().getTilex(), u.getPosition().getTiley());
		}		
		//check the highlighted tiles 
		assertTrue(gameState.getTile()[0][1].getMode() == 1);
		assertTrue(gameState.getTile()[0][2].getMode() == 1);
		assertTrue(gameState.getTile()[0][3].getMode() == 1);

		assertTrue(gameState.getTile()[1][1].getMode() == 1);
		assertTrue(gameState.getTile()[1][2].getMode() == 0);
		assertTrue(gameState.getTile()[1][3].getMode() == 1);

		assertTrue(gameState.getTile()[2][1].getMode() == 1);
		assertTrue(gameState.getTile()[2][2].getMode() == 1);
		assertTrue(gameState.getTile()[2][3].getMode() == 1);

		//put a unit at tile(1,1)
		gameState.summonUnit(null, 1, 1, 0, gameState.getHumanPlayer(), 0);
		assertTrue(gameState.getTile()[1][1].getUnit() == gameState.getallUnits().get(0)); 
		
		//highlight the tiles around tile(1,2) and tile(1,1) when summoning unit around it
		for (Unit u : gameState.gethumanPlayerUnits()) {
			TileHighlight.summonHighlight(gameState, u.getPosition().getTilex(), u.getPosition().getTiley());
		}	
		//check the highlighted tiles again
		assertTrue(gameState.getTile()[0][0].getMode() == 1);
		assertTrue(gameState.getTile()[0][1].getMode() == 1);
		assertTrue(gameState.getTile()[0][2].getMode() == 1);
		assertTrue(gameState.getTile()[0][3].getMode() == 1);
		
		assertTrue(gameState.getTile()[1][0].getMode() == 1);
		assertTrue(gameState.getTile()[1][1].getMode() == 0);
		assertTrue(gameState.getTile()[1][2].getMode() == 0);
		assertTrue(gameState.getTile()[0][3].getMode() == 1);
		
		assertTrue(gameState.getTile()[2][0].getMode() == 1);
		assertTrue(gameState.getTile()[2][1].getMode() == 1);
		assertTrue(gameState.getTile()[2][2].getMode() == 1);
		assertTrue(gameState.getTile()[0][3].getMode() == 1);

	}

}
