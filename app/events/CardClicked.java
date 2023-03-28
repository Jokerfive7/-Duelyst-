package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.TileHighlight;
import structures.GameState;

import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.ImageListForPreLoad;
import utils.StaticConfFiles;
import utils.CardCheckHelper;
import commands.BasicCommands;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a card. The event returns the position in the player's hand the card
 * resides within.
 * 
 * { messageType = “cardClicked” position = <hand index position [1-6]> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		new Thread() {
			public void run() {
				if (gameState.isThread() && !gameState.isUnitMoving() && !gameState.isHumanOperating()) {
					gameState.setHumanOperating(true);
					int handPosition = message.get("position").asInt();
					if (gameState.gameEnd == false) {

					// If a unit is already selected, deselect it to prevent unintended actions.
					if (gameState.getSelectedUnit() != null) {
						gameState.getSelectedUnit().setMoveAndAttack(false);
					}

					// Clear any previous tile highlights on the game board.
					TileHighlight.clearHighlight(gameState);
					TileHighlight.tileHighlight(out, gameState);

					// Highlight tiles on the game board based on the player's action.
					if (gameState.getHumanPlayer().getMana() < gameState.getHumanPlayer().getHandCard()
							.get(handPosition - 1).getManacost()) {
						// Notify the player that they don't have enough mana to play the selected card.
						BasicCommands.addPlayer1Notification(out, "Insufficient Mana", 3);
					}

					else if (gameState.getHumanPlayer().getHandCard().get(handPosition - 1).getId() == 4
							|| gameState.getHumanPlayer().getHandCard().get(handPosition - 1).getId() == 14)
					{
						// Highlight tiles on the game board based on the spell card the player wants to
						// play.
						TileHighlight.setAIUnitsHightLight(out, gameState);
					} else if (gameState.getHumanPlayer().getHandCard().get(handPosition - 1).getId() == 8
							|| gameState.getHumanPlayer().getHandCard().get(handPosition - 1).getId() == 18) {
						TileHighlight.setHumanUnitsHightLight(out, gameState);
					} else
					{
						// If the player wants to play a unit card, summon the unit and highlight tiles
						// around friendly units.
						gameState.getallUnits()
								.get(gameState.getHumanPlayer().getHandCard().get(handPosition - 1).getId())
								.airdrop(out, gameState);

						for (Unit u : gameState.gethumanPlayerUnits()) {
							TileHighlight.summonHighlight(gameState, u.getPosition().getTilex(),
									u.getPosition().getTiley());
						}
					}

					// Update the game board with the new tile highlights.
					TileHighlight.tileHighlight(out, gameState);

					// Update the GameState object to reflect the player's action.
					gameState.setpickedHandPosition(handPosition - 1);
					gameState.setSomethingSelected(1);

					// Notify the player of their action.
					String notification = "";
					if (CardCheckHelper.checkCard(gameState.getHumanPlayer().getHandCard().get(handPosition - 1))) {
						notification = "Unit card selected";
					} else {
						notification = "Spell card selected";
					}
					BasicCommands.addPlayer1Notification(out, notification, 2);

					// Update the player's hand of cards and display the card that was just played.
					gameState.getHumanPlayer().showCards(out);
					if (gameState.getHumanPlayer().getMana() >= gameState.getHumanPlayer().getHandCard()
							.get(handPosition - 1).getManacost()) {
					BasicCommands.drawCard(out, gameState.getHumanPlayer().getHandCard().get(handPosition - 1),
							handPosition, 1);
					
					gameState.setHumanOperating(false);

					}
				}
				}
			}
		}.start();
	}

}
