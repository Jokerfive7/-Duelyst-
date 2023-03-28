package utils;

import structures.basic.Card;
import structures.GameState;

import akka.actor.ActorRef;

import commands.BasicCommands;


import java.util.ArrayList;
import java.util.List;


public class CardCheckHelper {
    
    // Check if the card is unit card or spell card
    // Return true: unit card
    // Return false: spell card
    public static boolean checkCard(Card card){
        boolean IsUnit = false;
        switch(card.getId()){
            case 0: IsUnit = true;
            break;
            case 1: IsUnit = true;
            break;
            case 2: IsUnit = true;
            break;
            case 3: IsUnit = true;
            break;
            case 4: IsUnit = false;
            break;
            case 5: IsUnit = true;
            break;
            case 6: IsUnit = true;
            break;
            case 7: IsUnit = true;
            break;
            case 8: IsUnit = false;
            break;
            case 9: IsUnit = true;
            break;
            case 10: IsUnit = true;
            break;
            case 11: IsUnit = true;
            break;
            case 12: IsUnit = true;
            break;
            case 13: IsUnit = true;
            break;
            case 14: IsUnit = false;
            break;
            case 15: IsUnit = true;
            break;
            case 16: IsUnit = true;
            break;
            case 17: IsUnit = true;
            break;
            case 18: IsUnit = false;
            break;
            case 19: IsUnit = true;
            break;
            case 20: IsUnit = true;
            break;
            case 21: IsUnit = true;
            break;
            case 22: IsUnit = false;
            break;
            case 23: IsUnit = true;
            break;
            case 24: IsUnit = true;
            break;
            case 25: IsUnit = true;
            break;
            case 26: IsUnit = true;
            break;
            case 27: IsUnit = false;
            break;
            case 28: IsUnit = true;
            break;
            case 29: IsUnit = true;
            break;
            case 30: IsUnit = true;
            break;
            case 31: IsUnit = true;
            break;
            case 32: IsUnit = false;
            break;
            case 33: IsUnit = true;
            break;
            case 34: IsUnit = true;
            break;
            case 35: IsUnit = true;
            break;
            case 36: IsUnit = true;
            break;
            case 37: IsUnit = false;
            break;
            case 38: IsUnit = true;
            break;
            case 39: IsUnit = true;
            break;
        }
        return IsUnit;
    }

    // Highlight avalible cards
    public static void highlightCard(GameState gameState, ActorRef out){
        // check current mana
        // check which card is avalible
        // highlight
        int mana = gameState.getHumanPlayer().getMana();
        List<Card> handCard = gameState.getHumanPlayer().getHandCard();
        for(int i = 0; i < handCard.size(); i++){
            Card c = handCard.get(i);
                BasicCommands.drawCard(out, c, i+1, 0);
        		try {
        			Thread.sleep(2);
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
            
        }
    }
}
