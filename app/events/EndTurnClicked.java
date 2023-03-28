package events;

import java.util.Collections;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.AiCommand;
import commands.BasicCommands;
import commands.TileHighlight;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import utils.CardCheckHelper;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case the end-turn button.
 * 
 * { messageType = “endTurnClicked” }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		new Thread() {
			public void run() {
				if (gameState.isThread() && !gameState.isUnitMoving() && !gameState.isHumanOperating()) {
					gameState.setThread(false);

					// Clear move and attack status of the selected unit
					if (gameState.getSelectedUnit() != null) {
						gameState.getSelectedUnit().setMoveAndAttack(false);
					}

					// Reset the selected state
					gameState.setSomethingSelected(0);

					// Clear tile highlights and highlight available tiles
					TileHighlight.clearHighlight(gameState);
					TileHighlight.tileHighlight(out, gameState);

					// Check if game is over and update the end-game screen
					gameState.endGame0Health(out);

					// When player clicks the end turn button, if the game has not ended
					if (gameState.isGameEnd() == false) {

						// Human player draws a card and shows their hand
						if (!gameState.getHumanPlayer().drawCard()) {
							BasicCommands.addPlayer1Notification(out, "My handcards are full!", 3);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						gameState.endGame0Deck(out);

						gameState.getHumanPlayer().showCards(out);
						CardCheckHelper.highlightCard(gameState, out);

						// Reset mana and display the updated mana for the human player
						gameState.getHumanPlayer().setMana(0);
						BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// AI logic
						// remind player AI starts
						BasicCommands.addPlayer1Notification(out, "AI operating", 2);
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						// AI player reloads mana and displays the updated mana
						gameState.getAiPlayer().reloadMana();
						BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// Reset movement and attack status of AI units if it's not the first turn
						if (gameState.getAiPlayer().getMaxMana() > 2) {
							gameState.resetUnit(gameState.getAiPlayerUnits());
						}

						// AI uses cards with available mana
						while (AiCommand.aiChooseCard(gameState) != -1) {
							AiCommand.aiUseCard(out, gameState);
						}

						// AI units move and attack
						AiCommand.aiMoveAndAttack(out, gameState);

						// AI draws a card and temporarily loses all mana at the end of the turn
						gameState.getAiPlayer().drawCard();
						gameState.endGame0Deck(out);
						
						gameState.getAiPlayer().setMana(0);
						BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						// remind player AI is end
						BasicCommands.addPlayer1Notification(out, "AI stop operating", 2);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// Human player reloads mana and displays the updated mana
						gameState.getHumanPlayer().reloadMana();
						BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// Reset movement and attack status of human player units
						gameState.resetUnit(gameState.gethumanPlayerUnits());

						// Notify the player that their turn has begun
						BasicCommands.addPlayer1Notification(out, "Your turn.", 2);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
					gameState.setThread(true);

				}

			}
		}.start();
	}

}
