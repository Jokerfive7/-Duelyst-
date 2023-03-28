package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.TileHighlight;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import utils.UnitAbiliityHelper;

public class Spell {

	// This method damages an AI unit by 2 health points
	public static void damageToAiUnit(ActorRef out, GameState gameState, int tilex, int tiley) {

		// Load the inmolation effect animation and play it on the targeted tile
		EffectAnimation ef1 = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
		BasicCommands.playEffectAnimation(out, ef1, gameState.getTile()[tilex][tiley]);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Decrement the target unit's health by 2 and display the updated health
		gameState.getTile()[tilex][tiley].getUnit()
				.setHealth(gameState.getTile()[tilex][tiley].getUnit().getHealth() - 2);
		BasicCommands.setUnitHealth(out, gameState.getTile()[tilex][tiley].getUnit(),
				gameState.getTile()[tilex][tiley].getUnit().getHealth());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Update AIplayer health values and check if the game has ended
		gameState.getAiPlayer().setHealth(gameState.getAiAvatar().getHealth());
		BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());

		// Deduct 1 mana from the human player and display the updated mana
		gameState.getHumanPlayer().deductMana(1);
		BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Check if the target unit's health is less than or equal to 0, if so perform
		// death animation and remove the unit
		if (gameState.getTile()[tilex][tiley].getUnit().getHealth() <= 0) {
			// Play death animation for the unit
			BasicCommands.playUnitAnimation(out, gameState.getTile()[tilex][tiley].getUnit(), UnitAnimationType.death);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Delete the unit from the tile
			BasicCommands.deleteUnit(out, gameState.getTile()[tilex][tiley].getUnit());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Call the unitDead method
			gameState.getTile()[tilex][tiley].getUnit().unitDead(out, gameState);
			gameState.endGame0Health(out);

		}
		// Remove the played card from the human player's hand
		gameState.getHumanPlayer().getHandCard().remove(gameState.getpickedHandPosition());
	}

	// This method buffs a human unit by increasing its health by 5, and if the unit
	// is the avatar, it also increases the human player's health accordingly
	public static void buffForHumanUnit(ActorRef out, GameState gameState, int tilex, int tiley) {
		// Load the buff effect animation and play it on the targeted tile
		EffectAnimation ef1 = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
		BasicCommands.playEffectAnimation(out, ef1, gameState.getTile()[tilex][tiley]);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Check if the unit's current health plus 5 is less than its original health,
		// if so, increase its health by 5 and display the updated health
		if (gameState.getTile()[tilex][tiley].getUnit()
				.getOriginHealth() > gameState.getTile()[tilex][tiley].getUnit().getHealth() + 5) {

			gameState.getTile()[tilex][tiley].getUnit()
					.setHealth(gameState.getTile()[tilex][tiley].getUnit().getHealth() + 5);
			BasicCommands.setUnitHealth(out, gameState.getTile()[tilex][tiley].getUnit(),
					gameState.getTile()[tilex][tiley].getUnit().getHealth());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		// If increasing the unit's health by 5 would exceed its original health, set
		// its health to its original health and display the updated health
		else {
			gameState.getTile()[tilex][tiley].getUnit()
					.setHealth(gameState.getTile()[tilex][tiley].getUnit().getOriginHealth());
			BasicCommands.setUnitHealth(out, gameState.getTile()[tilex][tiley].getUnit(),
					gameState.getTile()[tilex][tiley].getUnit().getHealth());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		// If the targeted unit is the human avatar, then increase the human player's
		// health by the same amount
		if (gameState.getTile()[tilex][tiley].getUnit().getId() == 100) {
			gameState.getHumanPlayer().setHealth(gameState.getTile()[tilex][tiley].getUnit().getHealth());
			BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		// Deduct 1 mana from the human player and display the updated mana
		gameState.getHumanPlayer().deductMana(1);
		BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Remove the played card from the human player's hand
		gameState.getHumanPlayer().getHandCard().remove(gameState.getpickedHandPosition());

	}

	// This method damages a human unit by setting its health to 0 and performing
	// death animation if the unit is not the avatar
	public static void damageToHumanUnit(ActorRef out, GameState gameState, int tilex, int tiley, int handPosition) {
		// Load the martyrdom effect animation
		EffectAnimation ef1 = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);

		// Check if the targeted unit is the human avatar, if so, display a notification
		// that the avatar cannot be damaged
		if (gameState.getTile()[tilex][tiley].getUnit().getId() != 100) {
			// Play the martyrdom effect animation on the targeted tile
			BasicCommands.playEffectAnimation(out, ef1, gameState.getTile()[tilex][tiley]);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameState.getTile()[tilex][tiley].getUnit().setHealth(0);

			// Set the targeted unit's health to 0 and display the updated health
			BasicCommands.setUnitHealth(out, gameState.getTile()[tilex][tiley].getUnit(),
					gameState.getTile()[tilex][tiley].getUnit().getHealth());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Deduct 5 mana from the AI player and display the updated mana
			gameState.getAiPlayer().deductMana(5);
			BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Play death animation for the unit and remove it from the tile
			BasicCommands.playUnitAnimation(out, gameState.getTile()[tilex][tiley].getUnit(), UnitAnimationType.death);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BasicCommands.deleteUnit(out, gameState.getTile()[tilex][tiley].getUnit());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameState.getTile()[tilex][tiley].getUnit().unitDead(out, gameState);

			// Remove the played card from the AI player's hand
			gameState.getAiPlayer().getHandCard().remove(handPosition);

		} else {
			// Display a notification to the player that the avatar cannot be damaged
			BasicCommands.addPlayer1Notification(out, "You can't damage avtar.Try Again!", tiley);
		}

	}

	// This method buffs an AI unit by increasing its attack by 2
	public static void buffForAiUnit(ActorRef out, GameState gameState, int tilex, int tiley, int handPosition) {
		// Load the buff effect animation and play it on the targeted tile
		EffectAnimation ef1 = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
		BasicCommands.playEffectAnimation(out, ef1, gameState.getTile()[tilex][tiley]);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Increase the targeted unit's attack by 2 and display the updated attack
		gameState.getTile()[tilex][tiley].getUnit()
				.setAttack(gameState.getTile()[tilex][tiley].getUnit().getAttack() + 2);
		BasicCommands.setUnitAttack(out, gameState.getTile()[tilex][tiley].getUnit(),
				gameState.getTile()[tilex][tiley].getUnit().getAttack());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Deduct 2 mana from the AI player and display the updated mana
		gameState.getAiPlayer().deductMana(2);
		BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Remove the played card from the AI player's hand
		gameState.getAiPlayer().getHandCard().remove(handPosition);
	}

}
