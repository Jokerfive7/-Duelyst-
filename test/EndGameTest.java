import com.fasterxml.jackson.databind.node.ObjectNode;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import play.libs.Json;
import structures.GameState;

public class EndGameTest {

	@Test
	public void checkEndGame() {

		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

		GameState gameState = new GameState(); // create state storage
		Initalize initalizeProcessor = new Initalize(); // create an initalize event processor
		// lets simulate recieveing an initalize message
		ObjectNode eventMessage = Json.newObject(); // create a dummy message
		initalizeProcessor.processEvent(null, gameState, eventMessage);

		gameState.endGame0Health(null);
		assertFalse(gameState.gameEnd);
		gameState.getHumanPlayer().setHealth(0);
		gameState.endGame0Health(null);
		assertTrue(gameState.gameEnd);

	}

	@Test
	public void checkEndGame1() {
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

		GameState gameState = new GameState(); // create state storage
		Initalize initalizeProcessor = new Initalize(); // create an initalize event processor
		// lets simulate recieveing an initalize message
		ObjectNode eventMessage = Json.newObject(); // create a dummy message
		initalizeProcessor.processEvent(null, gameState, eventMessage);

		gameState.endGame0Deck(null);
		assertFalse(gameState.gameEnd);
		gameState.getAiPlayer().getCardDeck().clear();
		gameState.endGame0Deck(null);
		assertTrue(gameState.gameEnd);
	}

}
