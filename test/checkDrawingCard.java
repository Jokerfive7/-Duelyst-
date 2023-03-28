import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class checkDrawingCard {
	
	
	
	@Test
	public void checkDrawingCard()
	{
		GameState gameState=new GameState();
		Initalize initalizeProcessor=new Initalize();
		
		ObjectNode eventMessage=Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage);
		
		//Player testPlayer=new Player();
		
		Card a=BasicObjectBuilders.loadCard(StaticConfFiles.c_comodo_charger, 0, Card.class);
		Card b=BasicObjectBuilders.loadCard(StaticConfFiles.c_pureblade_enforcer, 1, Card.class);
		Card c=BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 2, Card.class);
		Card d=BasicObjectBuilders.loadCard(StaticConfFiles.c_silverguard_knight, 3, Card.class);
		Card e=BasicObjectBuilders.loadCard(StaticConfFiles.c_truestrike, 4, Card.class);
		Card f=BasicObjectBuilders.loadCard(StaticConfFiles.c_azure_herald, 5, Card.class);
		Card g=BasicObjectBuilders.loadCard(StaticConfFiles.c_ironcliff_guardian, 6, Card.class);
		
		
		//check if first 3 cards are orderly drew from card deck
		assertTrue(gameState.getHumanPlayer().getHandCard().get(0).getId()==a.getId());
		assertTrue(gameState.getHumanPlayer().getHandCard().get(1).getId()==b.getId());
		assertTrue(gameState.getHumanPlayer().getHandCard().get(2).getId()==c.getId());
		
		
		//enlarge the hand card size to 6
		gameState.getHumanPlayer().getHandCard().add(d);
		gameState.getHumanPlayer().getHandCard().add(e);
		gameState.getHumanPlayer().getHandCard().add(f);
		
		//If user already had 6 cards, user can't draw more cards
		gameState.getHumanPlayer().drawCard();	//This card object id is 6
		gameState.getHumanPlayer().drawCard();	
		gameState.getHumanPlayer().drawCard();
		assertTrue(gameState.getHumanPlayer().getHandCard().size()==6);
		for(Card card:gameState.getHumanPlayer().getHandCard())
		{
			
				//make sure card object id=6 is not in handcard
			
				assertTrue(card.getId()!=6);
						
		}
		
		

		
		
		
		
		
		
		
		
		
	}

}
