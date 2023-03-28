package structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.Initalize;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	// Indicates whether the game has been initialised.
	public boolean gameInitalised = false;
	
	// Indicates whether the game has ended.
	public boolean gameEnd = false;

	//thread
	private boolean isHumanThread = true;
	private boolean isUnitMoving = false;
	private boolean isHumanOperating = false;

	
	// Indicates the selected state of an object: 0 for none, 1 for hand, 2 for unit.
	private int somethingSelected = 0;
	
	// The currently selected unit, tile, attacking unit, and defending unit.
	private Unit selectedUnit;
	private Tile selectedTile;
	private	Unit attackUnit;
	private Unit defendUnit;
	
	// The tiles, and the units (health, attack, position, and side) on each tile.
	private Tile[][] tile;  
	
	// The players (health, mana, deck, and hand).
	private Player humanPlayer;
	private Player aiPlayer;
	
	// The avatars of the players.
	private Unit humanAvatar;
	private Unit aiAvatar;

	// For summoning a unit.
	// The list of human player units, including the human avatar.
	private List<Unit> humanPlayerUnits=new ArrayList<>();
	private List<Unit> aiPlayerUnits=new ArrayList<>();
	private int pickedHandPosition;
	private Map<Integer,Unit> allUnits=new HashMap<>();
	
	
	public boolean isHumanOperating() {
		return isHumanOperating;
	}
	public void setHumanOperating(boolean isHumanOperating) {
		this.isHumanOperating = isHumanOperating;
	}
	public boolean isUnitMoving() {
		return isUnitMoving;
	}
	public void setUnitMoving(boolean isUnitMoving) {
		this.isUnitMoving = isUnitMoving;
	}
	public boolean isThread() {
		return isHumanThread;
	}
	public void setThread(boolean isThread) {
		this.isHumanThread = isThread;
	}
	public Unit getAttackUnit() {
		return attackUnit;
	}
	public void setAttackUnit(Unit attackUnit) {
		this.attackUnit = attackUnit;
	}
	public Unit getDefendUnit() {
		return defendUnit;
	}
	public void setDefendUnit(Unit defendUnit) {
		this.defendUnit = defendUnit;
	}
	public Tile getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(Tile selectedTile) {
		this.selectedTile = selectedTile;
	}

	public void setSelectedUnit (Unit unitToSel) {
		this.selectedUnit = unitToSel;
	}
	
	public Unit getSelectedUnit () {
		return this.selectedUnit;
	}
	
	public boolean isGameEnd() {
		return gameEnd;
	}

	public void setGameEnd(boolean gameEnd) {
		this.gameEnd = gameEnd;
	}

	public int getSomethingSelected() {
		return somethingSelected;
	}

	public void setSomethingSelected(int somethingSelected) {
		this.somethingSelected = somethingSelected;
	}

	public Player getAiPlayer() {
		return aiPlayer;
	}

	public void setAiPlayer(Player aiPlayer) {
		this.aiPlayer = aiPlayer;
	}
	
	public Player getHumanPlayer()
	{
		return humanPlayer;
	}

	public void setHumanPlayer(Player humanPlayer) {
		this.humanPlayer = humanPlayer;
	}
	
	public Unit getAiAvatar() {
		return aiAvatar;
	}

	public Unit getHumanAvatar()
	{
		return humanAvatar;
	}
	
	public void setAiAvatar(Unit aiAvatar) {
		this.aiAvatar = aiAvatar;
	}

	public void setHumanAvatar(Unit humanAvatar) {
		this.humanAvatar = humanAvatar;
	}
	
	
	public Tile[][] getTile() {
		return tile;
	}

	public void setTile(Tile[][] tile) {
		this.tile = tile;		
	}

	public List<Unit> getAiPlayerUnits() {
		return aiPlayerUnits;
	}

	public List<Unit> gethumanPlayerUnits()
	{
		return this.humanPlayerUnits;
	}
	
	public void setpickedHandPosition(int handposition)
	{
		this.pickedHandPosition=handposition;
	}
	
	public int getpickedHandPosition()
	{
		return pickedHandPosition;
	}
	
	public Map<Integer,Unit> getallUnits()
	{
		return allUnits;
	}
	
	// Resets the attack and move readiness of all units in a given list.
	public void resetUnit(List<Unit> list) {
		for(Unit unit: list) {
					unit.setReadyToAttack(true);
					unit.setReadyToMove(true);
					if (unit.isWindfury()) {
						unit.setWindfuryUse(true);
					}
		}
	}

	// Summons a unit at a given tile with a given card position.
	public void summonUnit(ActorRef out, int tilex, int tiley, int unitID, Player player, int cardPosition) {
		
		// Set the position of the unit.
		allUnits.get(unitID).setPositionByTile(tile[tilex][tiley], this);
		BasicCommands.drawUnit(out, allUnits.get(unitID),tile[tilex][tiley]);

		// Play an animation.
		EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, ef, tile[tilex][tiley]);
		try { Thread.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }

		
		// Set the unit's health and attack.
		allUnits.get(unitID).setHealth(player.getHandCard().get(cardPosition).getBigCard().getHealth());
		allUnits.get(unitID).setOriginHealth(player.getHandCard().get(cardPosition).getBigCard().getHealth());
		allUnits.get(unitID).setAttack(player.getHandCard().get(cardPosition).getBigCard().getAttack());
		BasicCommands.setUnitAttack(out, allUnits.get(unitID),player.getHandCard().get(cardPosition).getBigCard().getAttack());
		try { Thread.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }

		BasicCommands.setUnitHealth(out, allUnits.get(unitID),player.getHandCard().get(cardPosition).getBigCard().getHealth());
		try { Thread.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }

		
		// If the player is the human player, deduct mana and add the unit to the queue of human player units.
		if(player == humanPlayer) {
		player.deductMana(player.getHandCard().get(cardPosition).getManacost());
		BasicCommands.setPlayer1Mana(out, player);
		try { Thread.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }

		humanPlayerUnits.add(allUnits.get(unitID));
		}
		// If the player is the AI player, deduct mana, set the unit's side to non-human, and add the unit to the queue of AI player units.
		else {
			player.deductMana(player.getHandCard().get(cardPosition).getManacost());
			BasicCommands.setPlayer2Mana(out, player);
			try { Thread.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }
			allUnits.get(unitID).setHumanSide(false);
			aiPlayerUnits.add(allUnits.get(unitID));
		}
		
		// Remove the card from the player's hand.
		player.getHandCard().remove(cardPosition);
	}
	
	// End the game if a player's avatar's health reaches 0.
	// This method is called at the end of TileClicked to check the game state.
	// Health reaching 0 must occur after TileClicked.
	public void endGame0Health(ActorRef out) {
		// If the human player's avatar's health is 0 or less, end the game and display a notification.
		if (humanPlayer.getHealth() <= 0) {
			BasicCommands.addPlayer1Notification(out, "What?I lose? (you lose)", 100000000);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			this.gameEnd = true;
			System.out.println(gameEnd);

		}
		// If the AI player's avatar's health is 0 or less, end the game and display a notification.
		else if (aiPlayer.getHealth() <= 0) {
			BasicCommands.addPlayer1Notification(out, "I got you! (you win)", 1000000);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			this.gameEnd = true;
			System.out.println(gameEnd);

		}

	}
	
	// End the game if a player's deck is empty.
	// This method is called at the beginning of EndTurnClicked to check the game state.
	public void endGame0Deck(ActorRef out) {
		// If the human player's deck is empty, end the game and display a notification.
		if(humanPlayer.getCardDeck().size()==0)
		{
			BasicCommands.addPlayer1Notification(out, "What?I lose? (you lose)", 1000000);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

			this.gameEnd = true;
		}
		// If the AI player's deck is empty, end the game and display a notification.
		else if(aiPlayer.getCardDeck().size()==0)
		{
			BasicCommands.addPlayer1Notification(out, "I got you! (you win)", 1000000);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

			this.gameEnd = true;
		}
	}
	

}
