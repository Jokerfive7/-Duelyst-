import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import commands.TileHighlight;

import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class MoveTest {
	@Test
	public void checkMove(){
		GameState gameState = new GameState(); // create state storage
		Initalize initalizeProcessor =  new Initalize(); // create an initalize event processor
		// lets simulate recieveing an initalize message
		ObjectNode eventMessage = Json.newObject(); // create a dummy message
		initalizeProcessor.processEvent(null, gameState, eventMessage); // send it to the initalize event processor
		
		TileHighlight.moveHighlight(gameState, 2 ,3, gameState.getAiPlayerUnits());
	
		//check the highlighted tiles 
		assertTrue(gameState.getTile()[1][4].getMode() == 1);
		assertTrue(gameState.getTile()[2][4].getMode() == 1);
		assertTrue(gameState.getTile()[3][4].getMode() == 1);
		assertTrue(gameState.getTile()[2][2].getMode() == 1);
		assertTrue(gameState.getTile()[3][2].getMode() == 1);
		assertTrue(gameState.getTile()[1][3].getMode() == 1);
		assertTrue(gameState.getTile()[3][3].getMode() == 1);
		assertTrue(gameState.getTile()[4][3].getMode() == 1);
		assertTrue(gameState.getTile()[2][1].getMode() == 1);
		assertTrue(gameState.getTile()[0][3].getMode() == 1);
		// Tile with unit
		assertTrue(gameState.getTile()[1][2].getMode() == 0);
		// Tile out of range 
		assertTrue(gameState.getTile()[5][3].getMode() == 0);

	}
}
