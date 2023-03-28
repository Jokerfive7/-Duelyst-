package commands;

import structures.GameState;
import structures.basic.Card;
import structures.basic.Spell;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import utils.UnitAbiliityHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import akka.actor.ActorRef;
import commands.TileHighlight;
import utils.CardCheckHelper;

public class AiCommand {
	private static Unit targetUnit = null;
	
	// This method is called every time to choose a card to play and returns its position in the handcard.
	public static int aiChooseCard(GameState gameState) {

		// Sort the handcard.
		Collections.sort(gameState.getAiPlayer().getHandCard());

		// Use Decay if human has the Ironcliff Guardian or Fire Spitter.
		targetUnit = null;
		if (gameState.gethumanPlayerUnits().contains(gameState.getallUnits().get(2))) {
			targetUnit = gameState.getallUnits().get(2);
		}
		else if	(gameState.gethumanPlayerUnits().contains(gameState.getallUnits().get(6))){
			targetUnit = gameState.getallUnits().get(6);

		}
		else if	(gameState.gethumanPlayerUnits().contains(gameState.getallUnits().get(11))){
			targetUnit = gameState.getallUnits().get(11);

		}
		else if	(gameState.gethumanPlayerUnits().contains(gameState.getallUnits().get(16))) {
			targetUnit = gameState.getallUnits().get(16);

		}
		if(targetUnit != null) {
			for (int i = 0; i < gameState.getAiPlayer().getHandCard().size(); i++) {
				if (gameState.getAiPlayer().getHandCard().get(i).getId() == 27
						|| gameState.getAiPlayer().getHandCard().get(i).getId() == 37) {
					if (gameState.getAiPlayer().getMana() >= 5) {
						return i;
					}
				}
			}
		}
		
		// Otherwise, summon a unit, bigger first.
		for (int i = 0; i < gameState.getAiPlayer().getHandCard().size(); i++) {
			if (gameState.getAiPlayer().getHandCard().get(i).getManacost() <= gameState.getAiPlayer().getMana()) {
				if(gameState.getAiPlayer().getHandCard().get(i).getId() != 27 && gameState.getAiPlayer().getHandCard().get(i).getId() != 37)
				{
					return i;
				}
			}
		}

		// If nothing can be used, return -1.
		return -1;
	}

	public static void aiUseCard(ActorRef out, GameState gameState) {

		// use the card chosen
		int position = aiChooseCard(gameState);

		// Summon a unit.
		if (CardCheckHelper.checkCard(gameState.getAiPlayer().getHandCard().get(position))) {

			Unit unit = gameState.getallUnits().get(gameState.getAiPlayer().getHandCard().get(position).getId());
			
			// Airdrop if the unit has this ability.
			unit.airdrop(out, gameState);

			// Show all summon tiles.
			for (Unit units : gameState.getAiPlayerUnits()) {
				int x = units.getPosition().getTilex();
				int y = units.getPosition().getTiley();
				TileHighlight.summonHighlight(gameState, x, y);
			}
			TileHighlight.tileHighlight(out, gameState);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// On summon if the unit has this ability.
			unit.onSummon(out, gameState);
			
			// unit or AI Avatar.
			Tile tile;
			// If the unit has Provoke ability, summon it around the ranged
			if (unit.isProvoke()) {
				if (gameState.getAiPlayerUnits().contains(gameState.getallUnits().get(25))) {
					tile = nearestTile(gameState, gameState.getallUnits().get(25));
				} else if (gameState.getAiPlayerUnits().contains(gameState.getallUnits().get(35))) {
					tile = nearestTile(gameState, gameState.getallUnits().get(35));
				} else {
					tile = nearestTile(gameState, gameState.getAiPlayerUnits().get(0));
				}
				if (tile != null) {
					gameState.summonUnit(out, tile.getTilex(), tile.getTiley(), unit.getId(), gameState.getAiPlayer(),
							position);
				}
			}

			// Otherwise, summon it at the nearest tile by the human Avatar.
			else {
				tile = nearestTile(gameState, gameState.gethumanPlayerUnits().get(0));
				if (tile != null) {
					gameState.summonUnit(out, tile.getTilex(), tile.getTiley(), unit.getId(), gameState.getAiPlayer(),
							position);
				}
			}

		}

		// Spell use.
		else {
			// Spell thieving if human has the unit with this ability.
			UnitAbiliityHelper.spellThief(out, gameState);
			
			if (gameState.getAiPlayer().getHandCard().get(position).getId() == 27
					|| gameState.getAiPlayer().getHandCard().get(position).getId() == 37) {
				int x = targetUnit.getPosition().getTilex();
				int y = targetUnit.getPosition().getTiley();
				TileHighlight.setHumanUnitsHightLight(out, gameState);
				gameState.getTile()[gameState.gethumanPlayerUnits().get(0).getPosition().getTilex()][gameState.gethumanPlayerUnits().get(0).getPosition().getTiley()].setMode(0);
				TileHighlight.tileHighlight(out, gameState);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Spell.damageToHumanUnit(out, gameState, x, y, position);
			} else {
				int x = gameState.getAiPlayerUnits().get(0).getPosition().getTilex();
				int y = gameState.getAiPlayerUnits().get(0).getPosition().getTiley();
				gameState.getTile()[x][y].setMode(1);
				TileHighlight.tileHighlight(out, gameState);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Spell.buffForAiUnit(out, gameState, x, y, position);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Clear all summon tiles.
		TileHighlight.clearHighlight(gameState);
		TileHighlight.tileHighlight(out, gameState);

	}


	
	// This method controls the AI player's unit movement and attack behavior
	public static void aiMoveAndAttack(ActorRef out, GameState gameState) {
		
		// Get an array of all the AI player's units
		Unit[] unitArray = gameState.getAiPlayerUnits().toArray(new Unit[gameState.getAiPlayerUnits().size()]);
		
		// Loop through each unit and perform actions
		for (int i = 0; i < unitArray.length; i++) {
			
			// Select the current unit
			gameState.setSelectedUnit(unitArray[i]);
			gameState.setSelectedTile(gameState.getTile()[unitArray[i].getPosition().getTilex()][unitArray[i].getPosition().getTiley()]);
			
			// If the unit can be selected, move and attack
			if (TileHighlight.selectUnit(out, gameState, unitArray[i].getPosition().getTilex(),
					unitArray[i].getPosition().getTiley(), gameState.getAiPlayerUnits(), gameState.gethumanPlayerUnits())) {
				
				// Wait for a short time
				try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
				
				// Create a list of tiles that the unit can attack
				List<Tile> attackList = new ArrayList<>();

				for (int m = 0; m < 9; m++) {
					for (int j = 0; j < 5; j++) {
						if (gameState.getTile()[m][j].getMode() == 2) {
							attackList.add(gameState.getTile()[m][j]);
						}
					}
				}
				
				// If there are no tiles to attack, move the unit towards the nearest enemy unit
				if (attackList.size() == 0) {
					Tile tile = nearestTile(gameState, gameState.gethumanPlayerUnits().get(0));
					unitArray[i].unitMove(out, gameState, tile);
					
					// Wait for a longer time
					try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
				} 
				
				// If there are tiles to attack, try to attack the weakest one
				else if (attackList.size() > 0) {
					
					// Initialize some variables
					boolean attacked = false;
					Tile targetTile = null;
					
					// Loop through each tile that can be attacked
					for (Tile tiles : attackList) {
						
						// If the tile's unit has low enough health to be defeated in one attack, attack it
						if (tiles.getUnit().getHealth() <= unitArray[i].getAttack()) {
							if (tiles.isAttackMode()) {
								targetTile = tiles;
								attacked = true;
								break;
							} else {
								TileHighlight.moveHighlightBeforeAttack(gameState, tiles.getTilex(), tiles.getTiley());
								TileHighlight.tileHighlight(out, gameState);
								
								// Wait for a short time
								try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
								
								Tile tile = nearestTile(gameState, gameState.gethumanPlayerUnits().get(0));
								unitArray[i].unitMove(out, gameState, tile);
								
								// If the unit was successfully moved, attack the tile's unit
								if (tile != null) {
									targetTile = tiles;
									attacked = true;
									break;
								} else {
									attacked = false;
								}
							}
						}
					}
					
					// If no weak enough tile was found, try to attack the enemy hero
					if (!attacked) {
						for (Tile tiles : attackList) {
							if (tiles.getUnit().getId() == 100) {
								if (tiles.isAttackMode()) {
									targetTile = tiles;
									attacked = true;
									break;
								} else {
									TileHighlight.moveHighlightBeforeAttack(gameState, tiles.getTilex(), tiles.getTiley());
									TileHighlight.tileHighlight(out, gameState);
									
									// Wait for a short time
									try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
									Tile tile = nearestTile(gameState, gameState.gethumanPlayerUnits().get(0));
									unitArray[i].unitMove(out, gameState, tile);
									
									// If the unit was successfully moved, attack the enemy hero
									if (tile != null) {
										targetTile = tiles;
										attacked = true;
										break;
									} else {
										attacked = false;
									}
								}
							}
						}
					}

					// If no attack was made yet, attack the first available tile
					if (!attacked) {
						for (Tile tiles : attackList) {
								if (tiles.isAttackMode()) {
									targetTile = tiles;
									break;
								} else {
									TileHighlight.moveHighlightBeforeAttack(gameState, tiles.getTilex(), tiles.getTiley());
									TileHighlight.tileHighlight(out, gameState);
									try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
									
									Tile tile = nearestTile(gameState, gameState.gethumanPlayerUnits().get(0));
									unitArray[i].unitMove(out, gameState, tile);
									
									// If the unit was successfully moved, attack the first available tile
									if (tile != null) {
										targetTile = tiles;
										break;
									} 
								}
							}
						}
					
					// Attack the selected tile
					unitArray[i].unitAttack(out, gameState, targetTile.getTilex(), targetTile.getTiley(), targetTile.getUnit());
					
					// If the unit has the windfury ability, repeat the attack process
					if(unitArray[i].isWindfury()) {
						i -= 1;
					}
				}
			}
			
			// Clear all summon tiles
			TileHighlight.clearHighlight(gameState);
			TileHighlight.tileHighlight(out, gameState);
		}
		
	}

	// This method returns the nearest summon tile to a target unit
	public static Tile nearestTile(GameState gameState, Unit target) {
		// Create a list of all summon tiles and calculate their distances from the target unit
		List<Tile> list = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				if (gameState.getTile()[i][j].getMode() == 1) {
					int x = i - target.getPosition().getTilex();
					if (x < 0) {
						x = -x;
					}
					int y = j - target.getPosition().getTiley();
					if (y < 0) {
						y = -y;
					}
					gameState.getTile()[i][j].setDistance(x + y);

					list.add(gameState.getTile()[i][j]);
				}
			}
		}
		
		// Sort the summon tiles by distance and return the nearest one
		Collections.sort(list);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
