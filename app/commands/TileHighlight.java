package commands;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

/**
 * The TileHighlight class provides methods to highlight and select tiles on the game board.
 */

public class TileHighlight {


	// This method selects a unit and highlights the tiles that the unit can move to or attack.
	public static boolean selectUnit(ActorRef out, GameState gameState, int tilex, int tiley, List<Unit> friendList, List<Unit> enemyList) {

		// Check if the selected unit is provoked, if not, proceed with normal attack/movement
		if (!gameState.getSelectedUnit().isProvoked(out, gameState)) {
			
			// Unit has not moved and attacked yet: display move and attack range
			if (gameState.getSelectedUnit().isReadyToMove()) {
				
				// Call the flying method of the unit
				gameState.getSelectedUnit().flying(out, gameState);
				
				// Highlight move range
				TileHighlight.moveHighlight(gameState, tilex, tiley, friendList);
				
				// If the unit is ranged, highlight attack range
				if (gameState.getTile()[tilex][tiley].getUnit().isRanged()) {
					TileHighlight.attackHighlightRanged(gameState, tilex, tiley, true, enemyList);
				}
				else {
					// Highlight attack range after move
					TileHighlight.attackHighlightAfterMove(out, gameState, tilex, tiley, false, enemyList);
					// Highlight direct attack range
					TileHighlight.attackHighlightDirect(out, gameState, tilex, tiley, true, enemyList);
				}
				// Highlight tiles
				TileHighlight.tileHighlight(out, gameState);
				return true;
			}
			// Unit has moved but not attacked yet: display attackable units
			else if (gameState.getSelectedUnit().isReadyToAttack()) {
				// Highlight attack range
				TileHighlight.attackHighlightDirect(out, gameState, tilex, tiley, true, enemyList);
				// Highlight tiles
				TileHighlight.tileHighlight(out, gameState);
				return true;
			}
			return false;
		}
		return true;
	}
	
    // Method to draw tiles and update the tile mode based on the gameState
	public static void tileHighlight(ActorRef out, GameState gameState) {
		for(int i = 0;i < 9;i++) {
			for(int j = 0;j < 5;j++) {
				BasicCommands.drawTile(out, gameState.getTile()[i][j], gameState.getTile()[i][j].getMode());
				try {Thread.sleep(2);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
	
	// Method to highlight tiles for movement range
	public static void moveHighlight(GameState gameState, int tilex, int tiley, List<Unit> list) {
		int[] oneStep;
		int[][] stepSet = {{-1,0},{1,0},{0,-1},{0,1}};
		for (int i = 0; i < stepSet.length; i++) {
			oneStep = stepSet[i];
	        // Check if the next step is within the game board boundaries
			if (tiley + oneStep[1] >= 0 && tiley + oneStep[1] <= 4 && tilex + oneStep[0] >= 0 && tilex + oneStep[0] <= 8 ) {
	            // Check if the next step is a valid move
				if(gameState.getTile()[tilex + oneStep[0]][tiley + oneStep[1]].getUnit() == null || list.contains(gameState.getTile()[tilex + oneStep[0]][tiley + oneStep[1]].getUnit())) {
	                // Highlight the tile for the first step
					gameState.getTile()[tilex + oneStep[0]][tiley + oneStep[1]].setMode(1);
					
					boolean provokedByOther = false;
					// Check if the tile is already being provoked by an enemy unit
					for(Unit units: gameState.getTile()[tilex + oneStep[0]][tiley + oneStep[1]].getProvoker()) {
						if (units.isHumanSide() != gameState.getSelectedUnit().isHumanSide()) {
							provokedByOther = true;
							break;	
						}	
					}
					
					if (!provokedByOther) {
		                // Highlight the tile for the second step if it's in a straight line
						if (tiley + 2*oneStep[1] >= 0 && tiley + 2*oneStep[1] <= 4 && tilex + 2*oneStep[0] >= 0 && tilex + 2*oneStep[0] <= 8) {
								gameState.getTile()[tilex + 2*oneStep[0]][tiley + 2*oneStep[1]].setMode(1);
						}
		                // Check if the next step is a corner tile
		                // oneStep[1] != 0, the first step is a vertical move, set the move mode to 2
						if (oneStep[1] != 0) {
							if (tilex + 1 <= 8) {
								gameState.getTile()[tilex + 1][tiley + oneStep[1]].setMode(1);
								gameState.getTile()[tilex + 1][tiley + oneStep[1]].setMoveMode(2);
							}
							if (tilex - 1 >= 0) {
								gameState.getTile()[tilex - 1][tiley + oneStep[1]].setMode(1);
								gameState.getTile()[tilex - 1][tiley + oneStep[1]].setMoveMode(2);
							}
						}
		                // oneStep[0] != 0, the first step is a horizontal move, set the move mode to 1
						else if (oneStep[0] != 0) {
							if (tiley + 1 <= 4) {
								gameState.getTile()[tilex + oneStep[0]][tiley + 1].setMode(1);
								gameState.getTile()[tilex + oneStep[0]][tiley + 1].setMoveMode(1);
							}
							if (tiley - 1 >= 0) {
								gameState.getTile()[tilex + oneStep[0]][tiley - 1].setMode(1);
								gameState.getTile()[tilex + oneStep[0]][tiley - 1].setMoveMode(1);	
							}
						}
					}
				}
			}
		}
	    // Clear previous unit highlights
		clearUnitHighlight(gameState,0);
	}
	
	// Move highlight to nearby tiles that are in range for an attack, taking into account provocation
	public static void moveHighlightBeforeAttack (GameState gameState, int tilex, int tiley) {
		// Define the range of nearby tiles
		int[][] rangeSet = {{-1,-1},{-1,0},{-1,1},{0,1},{0,-1},{1,-1},{1,0},{1,1}};
		
		// Create an empty list to store nearby tiles that are in range
		List<Tile> nearbyTile = new ArrayList<>();
		
		// Loop through each nearby tile and check if it's in range and has a unit in attack mode
		for (int[] step: rangeSet) {
			if (tiley + step[1] >= 0 && tiley + step[1] <= 4 && tilex + step[0] >= 0 && tilex + step[0] <= 8 ) {
				if(gameState.getTile()[tilex + step[0]][tiley + step[1]].getMode() == 1) {
					nearbyTile.add(gameState.getTile()[tilex + step[0]][tiley + step[1]]);
				}
			}
		}
		
		// Clear any existing highlight
		clearHighlight(gameState);
		
		// Loop through each nearby tile and set its highlight based on provocation statu
		for(Tile tile: nearbyTile) {
			// If the selected unit is in provoke mode, highlight the tile
			if (gameState.getTile()[tilex][tiley].getUnit().isProvoke()) { 
				tile.setMode(1);
			}
			else {
				boolean provokedByOther = false;
				// Check if the tile is already being provoked by an enemy unit
				for(Unit units: tile.getProvoker()) {
					if (units.isHumanSide() != gameState.getSelectedUnit().isHumanSide()) {
						provokedByOther = true;
						break;	
					}	
				}
				// Highlight the tile if it's not already being provoked by an enemy unit
				if(!provokedByOther) {
					tile.setMode(1);
				}
			}
		}		
	}
	
	
	// Highlight tiles that can be directly attacked by a unit
	public static void attackHighlightDirect (ActorRef out, GameState gameState, int tilex, int tiley, boolean attackMode, List<Unit> enemyList) {
		// Check if the tile is being provoked by an enemy unit
		boolean tileProvokedByEnemy = false;
	
		if (gameState.getTile()[tilex][tiley].getProvoker().size() != 0) {
			for (Unit Provoker: gameState.getTile()[tilex][tiley].getProvoker()) {
				String message = "Unit Provoked";
				
				if (enemyList.contains(Provoker)) {
					gameState.getTile()[Provoker.getPosition().getTilex()][Provoker.getPosition().getTiley()].setMode(2);
					gameState.getTile()[Provoker.getPosition().getTilex()][Provoker.getPosition().getTiley()].setAttackMode(attackMode);
					BasicCommands.addPlayer1Notification(out, message, 2);
					tileProvokedByEnemy = true;
				}
			}
		}
		
		// If the tile is not being provoked by an enemy unit, highlight nearby tiles that have enemy units
		if (!tileProvokedByEnemy) {
			int[][] rangeSet = {{-1,-1},{-1,0},{-1,1},{0,1},{0,-1},{1,-1},{1,0},{1,1}};
			int[] step;
			for (int i = 0; i < rangeSet.length; i++) {
				step = rangeSet[i];
				if (tiley + step[1] >= 0 && tiley + step[1] <= 4 && tilex + step[0] >= 0 && tilex + step[0] <= 8 ) {
					if(gameState.getTile()[tilex + step[0]][tiley + step[1]].getUnit() != null && enemyList.contains(gameState.getTile()[tilex + step[0]][tiley + step[1]].getUnit())) {
						gameState.getTile()[tilex + step[0]][tiley + step[1]].setMode(2);
						gameState.getTile()[tilex + step[0]][tiley + step[1]].setAttackMode(attackMode);
					}
				}
			}
		}
	}
	
	
	// This method highlights the attackable tiles after a unit moves.
	public static void attackHighlightAfterMove (ActorRef out, GameState gameState, int tilex, int tiley, boolean attackMode, List<Unit> enemyList) {
		// Iterate through all tiles in the game board
		for(int i = 0;i < 9;i++) {
			for(int j = 0;j < 5;j++) {
				// If the tile is in attack range, highlight it.
				if(gameState.getTile()[i][j].getMode() == 1){
					attackHighlightDirect(out,gameState,i,j,attackMode, enemyList);
				}
			}
		}
	}

	// This method highlights all tiles in the game board for a ranged attack.
	public static void attackHighlightRanged(GameState gameState, int tilex, int tiley, boolean attackMode, List<Unit> enemyList) {
		boolean tileProvokedByEnemy = false;
		
		// Check if the tile has any provokers (units that can attract enemies to it)
		if (gameState.getTile()[tilex][tiley].getProvoker().size() > 0) {
			// If the tile has provokers, iterate through them.
			for (Unit Provoker: gameState.getTile()[tilex][tiley].getProvoker()) {
				// If the provoker is an enemy, highlight it.
				if (enemyList.contains(Provoker)) {
					gameState.getTile()[Provoker.getPosition().getTilex()][Provoker.getPosition().getTiley()].setMode(2);
					gameState.getTile()[Provoker.getPosition().getTilex()][Provoker.getPosition().getTiley()].setAttackMode(attackMode);
					tileProvokedByEnemy = true;
				}
			}
		}
		// If the tile is not being provoked by an enemy unit, highlight all tiles that have enemy units
		if (!tileProvokedByEnemy) {
			for(int i = 0;i < 9;i++) {
				for(int j = 0;j < 5;j++) {
					if(gameState.getTile()[i][j].getUnit() != null 
							&& enemyList.contains(gameState.getTile()[i][j].getUnit())){
						gameState.getTile()[i][j].setMode(2);
						gameState.getTile()[i][j].setAttackMode(attackMode);
					}
				}
			}
		}
	}
	
	// This method highlights all tiles in the game board where a unit can be summoned.
	public static void summonHighlight (GameState gameState, int tilex, int tiley) {
		int[][] rangeSet = {{-1,-1},{-1,0},{-1,1},{0,1},{0,-1},{1,-1},{1,0},{1,1}};
		int[] step;
		
		// Iterate through all possible tile a unit can take
		for (int i = 0; i < rangeSet.length; i++) {
			step = rangeSet[i];
			
			// Check if the tile is within the bounds of the game board
			if (tiley + step[1] >= 0 && tiley + step[1] <= 4 && tilex + step[0] >= 0 && tilex + step[0] <= 8 ) {
				// If the tile has no unit on it, highlight it.
				if(gameState.getTile()[tilex + step[0]][tiley + step[1]].getUnit() == null) {
					gameState.getTile()[tilex + step[0]][tiley + step[1]].setMode(1);
				}
			}
		}
		// Clear all previous unit highlights.
		clearUnitHighlight(gameState,0);
	}
	
	
	// This function clears or sets the highlighting mode of tiles with a unit.
	public static void clearUnitHighlight(GameState gameState, int mode) {
		for(int i = 0;i < 9;i++) {
			for(int j = 0;j < 5;j++) {
				if(gameState.getTile()[i][j].getUnit() != null){
					gameState.getTile()[i][j].setMode(mode);
				}
			}
		}
	}
	
	// This function clears or sets the highlighting mode of tiles without a unit.
	public static void clearNoUnitHighlight(GameState gameState, int mode) {
		for(int i = 0;i < 9;i++) {
			for(int j = 0;j < 5;j++) {
				if(gameState.getTile()[i][j].getUnit() == null){
					gameState.getTile()[i][j].setMode(mode);
				}
			}
		}
	}
	
	// This function clears the highlighting mode of all tiles.
	public static void clearHighlight(GameState gameState) {
		for(int i = 0;i < 9;i++) {
			for(int j = 0;j < 5;j++) {
				gameState.getTile()[i][j].setMode(0);
			}
		}
	}

	// This function sets the highlighting mode of tiles with human player units.
	public static void setHumanUnitsHightLight(ActorRef out,GameState gameState) {
		for(Unit unit : gameState.gethumanPlayerUnits()) {
			gameState.getTile()[unit.getPosition().getTilex()][unit.getPosition().getTiley()].setMode(1);
		}
	}
	
	// This function sets the highlighting mode of tiles with AI player units.
	public static void setAIUnitsHightLight(ActorRef out,GameState gameState) {
		for(Unit unit : gameState.getAiPlayerUnits()) {
			gameState.getTile()[unit.getPosition().getTilex()][unit.getPosition().getTiley()].setMode(1);
		}
	}
}
