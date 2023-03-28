package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import commands.AiCommand;
import commands.BasicCommands;
import structures.basic.EffectAnimation;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.CardCheckHelper;
import utils.ImageListForPreLoad;
import utils.StaticConfFiles;
import akka.actor.ActorRef;
import commands.TileHighlight;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.Spell;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile. The event returns the x (horizontal) and y (vertical) indices of
 * the tile that was clicked. Tile indices start at 1.
 * 
 * { messageType = “tileClicked” tilex = <x index of the tile> tiley = <y index
 * of the tile> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		new Thread() {
			public void run() {
				if (gameState.isThread() && !gameState.isUnitMoving() && !gameState.isHumanOperating()) {
					gameState.setHumanOperating(true);

					int tilex = message.get("tilex").asInt();
					int tiley = message.get("tiley").asInt();
					String reminder = "";

					if (gameState.gameEnd == false) {


						// STATE 1: A CARD HAS BEEN SELECTED FROM THE HAND.
						if (gameState.getSomethingSelected() == 1) {

							// If the tile has mode 1 (can summon a unit).
							if (gameState.getTile()[tilex][tiley].getMode() == 1) {

								// Get the ID of the card that was played.
								int unitID = gameState.getHumanPlayer().getHandCard()
										.get(gameState.getpickedHandPosition()).getId();

								// If the card is a spell that damages an AI unit.
								if (unitID == 4 || unitID == 14) {
									
									// Display a notification to the player.
									reminder = "Spell Used";
									BasicCommands.addPlayer1Notification(out, reminder, 2);
									try {
										Thread.sleep(30);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									
									Spell.damageToAiUnit(out, gameState, tilex, tiley);
									
								}
								// If the card is a spell that buffs a human unit.
								else if (unitID == 8 || unitID == 18) {
									
									// Display a notification to the player.
									reminder = "Spell Used";
									BasicCommands.addPlayer1Notification(out, reminder, 2);
									try {
										Thread.sleep(30);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									
									Spell.buffForHumanUnit(out, gameState, tilex, tiley);
								}
								// If the card is a unit that can be summoned.
								else {

									// Display a notification to the player.
									reminder = "Unit Summoned";
									BasicCommands.addPlayer1Notification(out, reminder, 2);
									try {
										Thread.sleep(30);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									
									// If the unit has an "on summon" ability.
									gameState.getallUnits().get(unitID).onSummon(out, gameState);
									gameState.summonUnit(out, tilex, tiley, unitID, gameState.getHumanPlayer(),
											gameState.getpickedHandPosition());
								}

								// Delete the card from the hand.
								BasicCommands.deleteCard(out, gameState.getpickedHandPosition());
								try {
									Thread.sleep(30);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								// Reset the player's cards and show them again.
								gameState.getHumanPlayer().resetCards(out);
								gameState.getHumanPlayer().showCards(out);

							}

							// Remove the highlight from the selected card.
							CardCheckHelper.highlightCard(gameState, out);

							// Clear the highlight from the tiles.
							TileHighlight.clearHighlight(gameState);
							TileHighlight.tileHighlight(out, gameState);

							// Set the selected state to 0 (nothing is selected).
							gameState.setSomethingSelected(0);

							// Update the health of the players.
							BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
							BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());

							// End the game if the health of one player reaches 0.
							gameState.endGame0Health(out);
						}

						// STATE 2: A UNIT HAS BEEN SELECTED.
						else if (gameState.getSomethingSelected() == 2) {

							// If the tile has mode 1 (can be moved to).
							if (gameState.getTile()[tilex][tiley].getMode() == 1) {

								// Display a notification that the unit is moving.
								reminder = "Moving";
								BasicCommands.addPlayer1Notification(out, reminder, 2);

								// Move the selected unit to the clicked tile.
								gameState.getSelectedUnit().unitMove(out, gameState, gameState.getTile()[tilex][tiley]);

								// Clear the highlight from the tiles.
								TileHighlight.clearHighlight(gameState);

								// Set the selected state to 0 (nothing is selected).
								gameState.setSomethingSelected(0);

								// If the unit is set to move and attack.
								if (gameState.getSelectedUnit().isMoveAndAttack()) {
									
									// Display a notification that the unit is attacking.
									reminder = "Attacking";
									BasicCommands.addPlayer1Notification(out, reminder, 2);
									
									// Attack the enemy unit that is in range.
									gameState.getSelectedUnit().unitAttack(out, gameState, tilex, tiley,
											gameState.getDefendUnit());

									// Reset the move and attack flag.
									gameState.getSelectedUnit().setMoveAndAttack(false);
								}
							}

							// If the tile has mode 2 (can be attacked).
							else if (gameState.getTile()[tilex][tiley].getMode() == 2) {

								// Display a notification that the unit is attacking.
								reminder = "Attacking";
								BasicCommands.addPlayer1Notification(out, reminder, 2);

								// Set the selected unit as the attacker and the clicked unit as the defender.
								gameState.setAttackUnit(gameState.getSelectedUnit());
								gameState.setDefendUnit(gameState.getTile()[tilex][tiley].getUnit());

								// If the selected unit is a ranged unit or the clicked tile is in attack mode.
								if (gameState.getSelectedUnit().isRanged()
										|| gameState.getTile()[tilex][tiley].isAttackMode()) {

									// Attack the clicked unit.
									gameState.getSelectedUnit().unitAttack(out, gameState, tilex, tiley,
											gameState.getDefendUnit());

									// Clear the highlight from the tiles.
									TileHighlight.clearHighlight(gameState);

									// Set the selected state to 0 (nothing is selected).
									gameState.setSomethingSelected(0);
								}

								// If the selected unit is not a ranged unit and the clicked tile is not in
								// attack mode.
								else if (!gameState.getTile()[tilex][tiley].isAttackMode()) {

									// Highlight the tiles that the unit can move to before attacking.
									TileHighlight.moveHighlightBeforeAttack(gameState, tilex, tiley);

									// Set the selected unit to move and attack.
									gameState.getSelectedUnit().setMoveAndAttack(true);

									// Set the selected state to 2 (a unit is selected).
									gameState.setSomethingSelected(2);

								}
							}

							// If the selected tile is invalid.
							else {
								// Reset the move and attack flag for the selected unit.
								gameState.getSelectedUnit().setMoveAndAttack(false);

								// Clear the highlight from the tiles.
								TileHighlight.clearHighlight(gameState);

								// Set the selected state to 0 (nothing is selected).
								gameState.setSomethingSelected(0);
							}

							// Highlight the tiles again.
							TileHighlight.tileHighlight(out, gameState);

							// Update the health of the players.
							BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
							BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());

							// End the game if the health of one player reaches 0.
							gameState.endGame0Health(out);

						}

						// STATE 0: NO UNIT OR CARD HAS BEEN SELECTED.
						else if (gameState.getSomethingSelected() == 0) {

							// If the clicked tile has a friendly unit.
							if (gameState.getTile()[tilex][tiley].getUnit() != null
									&& gameState.getTile()[tilex][tiley].getUnit().isHumanSide()) {

								// Set the selected unit to the clicked unit.
								gameState.setSomethingSelected(2);
								gameState.setSelectedUnit(gameState.getTile()[tilex][tiley].getUnit());
								gameState.setSelectedTile(gameState.getTile()[tilex][tiley]);

								// Display a notification that a unit has been selected.
								reminder = "Unit Selected: RA("
										+ gameState.getTile()[tilex][tiley].getUnit().isReadyToAttack() + ") RM("
										+ gameState.getTile()[tilex][tiley].getUnit().isReadyToMove() + ")";
								BasicCommands.addPlayer1Notification(out, reminder, 2);

								// Highlight the selected unit and its possible moves and attacks.
								TileHighlight.selectUnit(out, gameState, tilex, tiley, gameState.gethumanPlayerUnits(),
										gameState.getAiPlayerUnits());
							}

						}

					}
					gameState.setHumanOperating(false);

				}

			}

		}.start();
	}
}
