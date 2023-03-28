package events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.TileHighlight;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;
import utils.CardCheckHelper;
import utils.CreateUnitHelper;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { messageType = “initalize” }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change

					gameState.gameInitalised = true;

					// Draw the tiles on the game board
					Tile[][] tileArray = new Tile[9][5];
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 5; j++) {
							tileArray[i][j] = BasicObjectBuilders.loadTile(i, j);
							BasicCommands.drawTile(out, tileArray[i][j], 0);
							try {
								Thread.sleep(2);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					// Set the human player and generate their deck
					Player humanPlayer = new Player();
					humanPlayer.generateHumanDeck();

					// Draw the cards for the human player
					humanPlayer.drawCard();
					humanPlayer.drawCard();
					humanPlayer.drawCard();
					humanPlayer.showCards(out);

					// Set the initial mana for the human player and reload their mana
					humanPlayer.setMana(1);
					humanPlayer.reloadMana();
					BasicCommands.setPlayer1Mana(out, humanPlayer);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Set the avatar for the human player
					Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 100, Unit.class);
					humanAvatar.setPositionByTile(tileArray[1][2], gameState);
					BasicCommands.drawUnit(out, humanAvatar, tileArray[1][2]);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Set the health for the human player
					BasicCommands.setPlayer1Health(out, humanPlayer);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Set the attack and health for the human avatar
					BasicCommands.setUnitAttack(out, humanAvatar, 2);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					BasicCommands.setUnitHealth(out, humanAvatar, 20);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					humanAvatar.setAttack(2);
					humanAvatar.setHealth(20);
					humanAvatar.setOriginHealth(20);

					// Set the AI player and generate their deck
					Player aiPlayer = new Player();
					aiPlayer.generateAIDeck();
					aiPlayer.drawCard();
					aiPlayer.drawCard();
					aiPlayer.drawCard();

					// Set the initial mana for the AI player
					aiPlayer.setMana(1);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Set the avatar for the AI player
					Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 101, Unit.class);
					aiAvatar.setHumanSide(false);
					aiAvatar.setPositionByTile(tileArray[7][2], gameState);
					BasicCommands.drawUnit(out, aiAvatar, tileArray[7][2]);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Set the health for the AI player
					BasicCommands.setPlayer2Health(out, aiPlayer);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Set the attack and health for the AI avatar
					BasicCommands.setUnitAttack(out, aiAvatar, 2);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					BasicCommands.setUnitHealth(out, aiAvatar, 20);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					aiAvatar.setAttack(2);
					aiAvatar.setHealth(20);

					// Create all units and add abilities to them
					for (int i = 0; i < 40; i++) {
						if (i == 4 || i == 14 || i == 8 || i == 18 || i == 22 || i == 32 || i == 27 || i == 37) {
							continue;
						}

						gameState.getallUnits().put(i,
								BasicObjectBuilders.loadUnit(StaticConfFiles.loadAllUnitsConf().get(i), i, Unit.class));

					}
					CreateUnitHelper.addAbilityToUnit(gameState);

					// Set the game state
					gameState.setHumanPlayer(humanPlayer);
					gameState.setAiPlayer(aiPlayer);
					gameState.setTile(tileArray);
					gameState.setHumanAvatar(humanAvatar);
					gameState.setAiAvatar(aiAvatar);
					gameState.gethumanPlayerUnits().add(humanAvatar);
					gameState.getAiPlayerUnits().add(aiAvatar);


	}
}
