package structures.basic;

import javax.swing.text.Highlighter.Highlight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.TileHighlight;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import utils.UnitAbiliityHelper;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	//id of the unit
	int id;
	
	//type of animation
	UnitAnimationType animation;
	
	//position of the unit
	Position position;
	
	//set of animations for the unit
	UnitAnimationSet animations;
	
	//correction for the unit image
	ImageCorrection correction;
	
	//health value after being updated
	private int updatedHealth;
	
	//attack value of the unit
	private int attack;
	
	//side of the unit, true=human, false=AI
	private boolean humanSide = true;
	
	//permission to move
	private boolean readyToMove = false;
	
	//permission to attack
	private boolean readyToAttack = false;
	
	//movement and attack status
	private boolean moveAndAttack = false;
	
	//special effects
	//1 spellthief: when an enemy casts a spell, attributes change
	private boolean isSpellThief;
	
	//2 provoke: adjacent taunting units cannot move, and can only select this unit when attacking
	private boolean isProvoke;
	
	//3 when the friendly avatar is injured, attributes change
	private boolean isAnger;
	
	//4 windfury: has 2 attack chances
	private boolean isWindfury;
	private boolean isWindfuryUse;

	//5 ranged: can attack any enemy unit
	private boolean isRanged;
	
	//6 airdrop: can summon at any location
	private boolean isAirdrop;
	
	//7 flying: can move to any position
	private boolean isFlying;
	
	//8 on death: deathrattle effect (currently only drawing cards)
	private boolean isOnDeath;
	
	//9 on summon: battlecry effect (avatar adds attributes, draw cards)
	private boolean isOnSummon;

	//for Spell
	//health value that cannot be changed
	private int originHealth;
	
	public Unit() {}
	
	//constructor for creating a unit without a current tile
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
		
		
	}
	
	//constructor for creating a unit with a current tile
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
		
	}
	

	//constructor for creating a unit with specific attributes
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
		
	}
	
	//Returns whether the unit can move and attack in one step
	public boolean isMoveAndAttack() {
		return moveAndAttack;
	}

	//Sets whether the unit can move and attack in one step
	public void setMoveAndAttack(boolean moveAndAttack) {
		this.moveAndAttack = moveAndAttack;
	}

	//Returns whether the unit has the spellthief effect
	public boolean isSpellThief() {
		return isSpellThief;
	}

	//Sets whether the unit has the spellthief effect
	public void setSpellThief(boolean isSpellThief) {
		this.isSpellThief = isSpellThief;
	}

	//Returns whether the unit has the provoke effect
	public boolean isProvoke() {
		return isProvoke;
	}

	//Sets whether the unit has the provoke effect
	public void setProvoke(boolean isProvoke) {
		this.isProvoke = isProvoke;
	}

	//Returns whether the unit has the anger effect
	public boolean isAnger() {
		return isAnger;
	}

	//Sets whether the unit has the anger effect
	public void setAnger(boolean isAnger) {
		this.isAnger = isAnger;
	}

	//Returns whether the unit has the windfury effect
	public boolean isWindfury() {
		return isWindfury;
	}
	
	//Returns whether the unit has used the windfury effect
	public boolean isWindfuryUse() {
		return isWindfuryUse;
	}
	
	//Sets whether the unit has the windfury effect
	public void setWindfury(boolean isWindfury) {
		this.isWindfury = isWindfury;
	}
	
	//Sets whether the unit has used the windfury effect
	public void setWindfuryUse(boolean isWindfuryUse) {
		this.isWindfuryUse = isWindfuryUse;
	}

	//Returns whether the unit has the ranged effect
	public boolean isRanged() {
		return isRanged;
	}
	
	//Sets whether the unit has the ranged effect
	public void setRanged(boolean isRanged) {
		this.isRanged = isRanged;
	}

	//Returns whether the unit has the airdrop effect
	public boolean isAirdrop() {
		return isAirdrop;
	}

	//Sets whether the unit has the airdrop effect
	public void setAirdrop(boolean isAirdrop) {
		this.isAirdrop = isAirdrop;
	}

	//Returns whether the unit has the flying effect
	public boolean isFlying() {
		return isFlying;
	}

	//Sets whether the unit has the flying effect
	public void setFlying(boolean isFlying) {
		this.isFlying = isFlying;
	}

	//Returns whether the unit has the on-death effect
	public boolean isOnDeath() {
		return isOnDeath;
	}

	//Sets whether the unit has the on-death effect
	public void setOnDeath(boolean isOnDeath) {
		this.isOnDeath = isOnDeath;
	}

	//Returns whether the unit has the on-summon effect
	public boolean isOnSummon() {
		return isOnSummon;
	}

	//Sets whether the unit has the on-summon effect
	public void setOnSummon(boolean isOnSummon) {
		this.isOnSummon = isOnSummon;
	}

	//Returns the id of the unit
	public int getId() {
		return id;
	}
	
	//Sets the id of the unit
	public void setId(int id) {
		this.id = id;
	}
	
	//Returns the animation type of the unit
	public UnitAnimationType getAnimation() {
		return animation;
	}
	
	//Sets the animation type of the unit
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	//Returns the image correction for the unit
	public ImageCorrection getCorrection() {
		return correction;
	}
	
	//Sets the image correction for the unit
	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}
	
	//Returns the position of the unit
	public Position getPosition() {
		return position;
	}

	//Sets the position of the unit
	public void setPosition(Position position) {
		this.position = position;
	}
	
	//Returns the set of animations for the unit
	public UnitAnimationSet getAnimations() {
		return animations;
	}

	//Sets the set of animations for the unit
	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}

	//Returns the health value of the unit before it was modified
	public int getOriginHealth()
	{
		return originHealth;
	}
	
	//Returns whether the unit is on the human side (true) or AI side (false)
	public boolean isHumanSide() {
		return humanSide;
	}

	//Sets whether the unit is on the human side (true) or AI side (false)
	public void setHumanSide(boolean side) {
		this.humanSide = side;
	}

	//Returns the current attack value of the unit
	public int getAttack() {
		return this.attack;
	}
	
	//Returns the current health value of the unit
	public int getHealth() {
		return this.updatedHealth;
	}
	
	//Sets the current health value of the unit and returns the updated value
	public int setHealth(int ModifiedHealth) {
		this.updatedHealth = ModifiedHealth;
		return this.updatedHealth;
	}
	
	//Sets the current attack value of the unit and returns the updated value
	public int setAttack(int ModifiedAttack) {
		this.attack=ModifiedAttack;
		return this.attack;
		
	}
	
	//Sets the health value of the unit that cannot be changed
	public void setOriginHealth(int originHealth) {
		this.originHealth=originHealth;
	}
	
	//Returns whether the unit has permission to move
	public boolean isReadyToMove() {
		return readyToMove;
	}
	
	//Sets whether the unit has permission to move
	public void setReadyToMove(boolean readyToMove) {
		this.readyToMove = readyToMove;
	}

	//Returns whether the unit has permission to attack
	public boolean isReadyToAttack() {
		return readyToAttack;
	}
	
	//Sets whether the unit has permission to attack
	public void setReadyToAttack(boolean readyToAttack) {
		this.readyToAttack = readyToAttack;
	}


	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	
	//Sets the position of the unit based on the given tile and adds the unit to the tile
	public void setPositionByTile(Tile tile, GameState gameState) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
				
		//Adds the unit to the target tile
		tile.setUnit(this);
		
		//Special case for provoke effect
		setProvokeTile(gameState);
		
	}
	
	//Moves the unit to the given tile and updates the unit's position and status
	public void unitMove(ActorRef out, GameState gameState, Tile tile) {
		if(tile != null) {
			gameState.getTile()[position.getTilex()][position.getTiley()].clearUnit();
			clearProvokeTile(gameState);
			
			//Moves the unit to the tile based on the move mode of the tile
			if(tile.getMoveMode() == 1) {
				BasicCommands.moveUnitToTile(out, this, tile);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}	
			}
			else if(tile.getMoveMode() == 2){
				BasicCommands.moveUnitToTile(out, this, tile, true);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}	
			}
			this.setReadyToMove(false);
			this.setPositionByTile(tile, gameState);
		}
	}
	
	
	//Removes the unit from the tile and clears any provoke effect on the tile
	public void unitDead(ActorRef out, GameState gameState) {
		//Executes on-death effect if the unit has the ability
	    if(isOnDeath) {
	    	UnitAbiliityHelper.onDeath(out, gameState);
	    }
		gameState.getTile()[position.getTilex()][position.getTiley()].clearUnit();
		clearProvokeTile(gameState);
		if(isHumanSide()) {
			gameState.gethumanPlayerUnits().remove(this);
		}
		else {
			gameState.getAiPlayerUnits().remove(this);
		}
	}
	
	// This method is called when a Unit attacks another Unit, and it plays out the attack animation and adjusts health values
	public void unitAttack(ActorRef out, GameState gameState, int tilex, int tiley, Unit DefendUnit) {
		
		// Play move animation
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.move);
		try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

		// Play attack animation
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
		try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace();}
		
		if(isRanged) {
			EffectAnimation efRanged = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
			BasicCommands.playProjectileAnimation(out, efRanged, 0, gameState.getSelectedTile(), gameState.getTile()[DefendUnit.getPosition().getTilex()][DefendUnit.getPosition().getTiley()]);
			try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

		}

		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.idle);
		BasicCommands.playUnitAnimation(out, DefendUnit, UnitAnimationType.idle);
		
		// Reduce health of the DefendUnit
		if (DefendUnit.getHealth() - this.getAttack() > 0) {
			DefendUnit.setHealth(DefendUnit.getHealth() - this.getAttack());
		}
		else {
			DefendUnit.setHealth(0);
		}
		BasicCommands.setUnitHealth(out, DefendUnit, DefendUnit.getHealth());

		// trigger the anger ability
		if(DefendUnit == gameState.getHumanAvatar()) {
			UnitAbiliityHelper.anger(out, gameState);
		}
		
		// Update player health values
		gameState.getAiPlayer().setHealth(gameState.getAiAvatar().getHealth());
		gameState.getHumanPlayer().setHealth(gameState.getHumanAvatar().getHealth());
		BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
		BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());
		try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
		
		// Set unit states after attack
		this.setReadyToMove(false);
		this.setReadyToAttack(false);
		
		// Ability Windfury: if the unit has the Windfury ability, set it to attack again
		if (this.isWindfuryUse()) {
			BasicCommands.addPlayer1Notification(out, "Windfury, attack agagin", 3);
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			this.setReadyToAttack(true);
			this.setWindfuryUse(false);
		}
		
		// If the DefendUnit is still alive, it counterattacks
		if (DefendUnit.getHealth() > 0 && 
				Math.abs(DefendUnit.getPosition().tilex - this.position.tilex) < 2 && Math.abs(DefendUnit.getPosition().tiley - this.position.tiley) < 2) {
			
			// Counterattack animation
			BasicCommands.playUnitAnimation(out, DefendUnit, UnitAnimationType.attack);
			try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
			
			if(DefendUnit.isRanged()) {
				EffectAnimation efRanged = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
				BasicCommands.playProjectileAnimation(out, efRanged, 0, gameState.getTile()[DefendUnit.getPosition().getTilex()][DefendUnit.getPosition().getTiley()], gameState.getSelectedTile());
				try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

			}
			
			BasicCommands.playUnitAnimation(out, this, UnitAnimationType.idle);
			BasicCommands.playUnitAnimation(out, DefendUnit, UnitAnimationType.idle);

			// Update health values of the attacking unit
			if (this.getHealth() - DefendUnit.getAttack() > 0) {
				this.setHealth(this.getHealth() - DefendUnit.getAttack());
			}
			else {
				this.setHealth(0);
			}
			gameState.getAiPlayer().setHealth(gameState.getAiAvatar().getHealth());
			gameState.getHumanPlayer().setHealth(gameState.getHumanAvatar().getHealth());
			BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
			BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());
			BasicCommands.setUnitHealth(out, this, this.getHealth());

			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

			// If the attacking unit dies, play death animation and remove the unit
			if (this.getHealth() <= 0) { 
				// Clear the tile of the attacking unit and call unitDead method to handle more cleanup
				unitDead(out, gameState);
				// Death animation
				BasicCommands.playUnitAnimation(out, this, UnitAnimationType.death);
				try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace();}
				// Delete the unit from the game
				BasicCommands.deleteUnit(out, this);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
				
				// and check if the game has ended
				gameState.endGame0Health(out);

			}
			
			// trigger the anger ability
			if(this == gameState.getHumanAvatar()) {
				UnitAbiliityHelper.anger(out, gameState);
			}
		} 
		// If the DefendUnit dies, remove it from the game
		else if (DefendUnit.getHealth() <= 0) { 
			// Clear the tile of the DefendUnit and call its unitDead method to handle more cleanup
			DefendUnit.unitDead(out, gameState);
			// Death animation
			BasicCommands.playUnitAnimation(out, DefendUnit, UnitAnimationType.death);
			try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
			// Delete the unit from the game
			BasicCommands.deleteUnit(out, DefendUnit);
			try { Thread.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
			
			// and check if the game has ended
			gameState.endGame0Health(out);
		}
	}
	
	
	//airdrop ability
	public void airdrop(ActorRef out, GameState gameState) {
		if(isAirdrop) {
			BasicCommands.addPlayer1Notification(out, "Airdroping", 3);
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			TileHighlight.clearNoUnitHighlight(gameState, 1);
		}
	}
	
	//flying ability
	public void flying(ActorRef out, GameState gameState) {
			if(isFlying) {	
				BasicCommands.addPlayer1Notification(out, "Flying", 3);
				try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			TileHighlight.clearNoUnitHighlight(gameState, 1);
			
		}
	}
	
	//on summon ability
	public void onSummon(ActorRef out,GameState gameState)
	{
		if(isOnSummon)
		{	
			BasicCommands.addPlayer1Notification(out, "OnSummon", 3);
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			
			if(this.id==5||this.id==15)
				
			//Effect: When this unit is summoned give your avatar +3 health (maximum 20)
			//This is human player's unit
			{
				if(gameState.getHumanPlayer().getHealth()<=17)
				{
					gameState.getHumanPlayer().setHealth(gameState.getHumanPlayer().getHealth()+3);
					gameState.gethumanPlayerUnits().get(0).setHealth(gameState.gethumanPlayerUnits().get(0).getHealth()+3);
					
				}else
				{
					gameState.getHumanPlayer().setHealth(20);
					gameState.gethumanPlayerUnits().get(0).setHealth(20);
				}
				
				EffectAnimation ef1 = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
				
				BasicCommands.playEffectAnimation(out, ef1, gameState.getTile()[gameState.gethumanPlayerUnits().get(0).getPosition().getTilex()][gameState.gethumanPlayerUnits().get(0).getPosition().getTiley()]);
				BasicCommands.playUnitAnimation(out, gameState.gethumanPlayerUnits().get(0), UnitAnimationType.channel);
				try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
				BasicCommands.playUnitAnimation(out, gameState.gethumanPlayerUnits().get(0), UnitAnimationType.idle);
				BasicCommands.setUnitHealth(out, gameState.gethumanPlayerUnits().get(0), gameState.gethumanPlayerUnits().get(0).getHealth());
				BasicCommands.setUnitAttack(out, gameState.gethumanPlayerUnits().get(0), gameState.gethumanPlayerUnits().get(0).getAttack());
				BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
						
			}
			
			if(this.id==23||this.id==33)
			//Effects: When this unit is summoned, both players draw a card
			//This is AI player's unit
			{
				//If humancard deck==0, then human player lose the game
					gameState.endGame0Deck(out);
				//Human draw a card
					if (!gameState.getHumanPlayer().drawCard()) {
						BasicCommands.addPlayer1Notification(out, "My handcards are full!", 3);
						try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
					}else
					{
						BasicCommands.addPlayer1Notification(out, "Draw a Card", 3);
					}
					gameState.getHumanPlayer().showCards(out);
				//AI draw a card
					gameState.getAiPlayer().drawCard();
			}
		}
	}
	
	// This method checks if the unit is being provoked by an enemy unit and highlights the enemy unit's tile if it is.
	// It also sets or clears the unit's provocation tiles based on whether the unit is currently being provoked or not.
	public boolean isProvoked(ActorRef out, GameState gameState) {
		boolean isProvoked = false;
		for (Unit units : gameState.getTile()[position.getTilex()][position.getTiley()].getProvoker()) {

			// If there is an enemy unit in the provoker list, the unit is being provoked and the enemy unit's tile is highlighted.
			if (units.isHumanSide() != this.isHumanSide()) {
				if(this.isReadyToAttack()) {
				BasicCommands.drawTile(out, gameState.getTile()[units.getPosition().getTilex()][units.getPosition().getTiley()], 2);
				gameState.getTile()[units.getPosition().getTilex()][units.getPosition().getTiley()].setAttackMode(true);
				gameState.getTile()[units.getPosition().getTilex()][units.getPosition().getTiley()].setMode(2);
				
				try { Thread.sleep(20); }  catch (InterruptedException e) { e.printStackTrace(); }
				}
				isProvoked = true;
			}
		}
		return isProvoked;
	}
	
	// This method sets the provocation tiles for the unit.
	private void setProvokeTile(GameState gameState) {
		if(isProvoke) {
			int[][] range = {{-1,-1},{-1,0},{-1,1},{0,1},{0,-1},{1,-1},{1,0},{1,1}};
			int[] step;

			// For each adjacent tile, add the unit to the tile's provoker list.
			for (int i = 0; i < range.length; i++) {
				step = range[i];
				if (position.getTiley() + step[1] >= 0 && position.getTiley() + step[1] <= 4 && position.getTilex() + step[0] >= 0 && position.getTilex() + step[0] <= 8 ) {
				gameState.getTile()[position.getTilex() + step[0]][position.getTiley() + step[1]].getProvoker().add(this);
				}
			}
		}
	}
	
	// This method clears the provocation tiles for the unit.
	private void clearProvokeTile(GameState gameState) {
		if(isProvoke) {
			int[][] range = {{-1,-1},{-1,0},{-1,1},{0,1},{0,-1},{1,-1},{1,0},{1,1}};
			int[] step;

			for (int i = 0; i < range.length; i++) {
				step = range[i];
				if (position.getTiley() + step[1] >= 0 && position.getTiley() + step[1] <= 4 && position.getTilex() + step[0] >= 0 && position.getTilex() + step[0] <= 8 ) {
				gameState.getTile()[position.getTilex() + step[0]][position.getTiley() + step[1]].getProvoker().remove(this);
				}
			}
		}
	}
}
