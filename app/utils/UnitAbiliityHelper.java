package utils;

import structures.basic.Unit;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

public class UnitAbiliityHelper {

    // call when AI-player cast a spell
    public static void spellThief(ActorRef out, GameState gameState){
        // check if this unit is on board
        for(Unit u : gameState.gethumanPlayerUnits()){
            if(u.isSpellThief()){
				BasicCommands.addPlayer1Notification(out, "SpellThiefing", 3);
				try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
				
                u.setAttack(u.getAttack() + 1);
     	       BasicCommands.setUnitAttack(out, u, u.getAttack());
   			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
                u.setHealth(u.getHealth() + 1);
                u.setOriginHealth(u.getOriginHealth() + 1);

     	       BasicCommands.setUnitHealth(out, u, u.getHealth());
   			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
            }
        }
    }


    // call when player avatar was attacked
    public static void anger(ActorRef out, GameState gameState){
        // check if this unit is on board
        for(Unit u : gameState.gethumanPlayerUnits()){
            if(u.isAnger()){
				BasicCommands.addPlayer1Notification(out, "Angered", 3);
				try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
				
                u.setAttack(u.getAttack() + 2);
      	       BasicCommands.setUnitAttack(out, u, u.getAttack());
      			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
            }
        }
    }

    // call when unit with on-death is died
    public static void onDeath(ActorRef out, GameState gameState){
        // AI-player draw a card
		BasicCommands.addPlayer1Notification(out, "onDeath", 3);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getAiPlayer().drawCard();
		gameState.endGame0Deck(out);

    }
}
