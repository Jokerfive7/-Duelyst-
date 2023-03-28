package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.TileHighlight;
import structures.GameState;
import utils.CardCheckHelper;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case somewhere that is not on a card tile or the end-turn button.
 * 
 * { messageType = “otherClicked” }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		new Thread() {
			public void run() {
				if (gameState.isThread() && !gameState.isUnitMoving() && !gameState.isHumanOperating()) {
					if (gameState.gameEnd == false) {

					// Clear the MoveAndAttack state
					if (gameState.getSelectedUnit() != null) {
						gameState.getSelectedUnit().setMoveAndAttack(false);
					}
					// Clear the selection state
					gameState.setSomethingSelected(0);

					// Highlight the selected card
					CardCheckHelper.highlightCard(gameState, out);

					// Clear any previously highlighted tiles
					TileHighlight.clearHighlight(gameState);
					TileHighlight.tileHighlight(out, gameState);
					}
				}
			}
		}.start();
	}

}
