package structures.basic;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EndTurnClicked;
import utils.OrderedCardLoader;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	//The health and mana of the player.
	int health;
	int mana;

	//The card deck and hand card of the player.
	List<Card> cardDeck = new ArrayList<Card>();
	List<Card> handCard = new ArrayList<Card>();

	//The maximum mana the player can have.
	private int maxMana = 1;

	//Constructor to initialize the player with default health and mana.
	public Player() {
		super();
		this.health = 20;
		this.mana = 2;
	}
	
	//Constructor to initialize the player with specified health and mana.
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
	}
	
	//Getter method for player's health.
	public int getHealth() {
		return health;
	}
	
	//Setter method for player's health.
	public void setHealth(int health) {
		this.health = health;
	}
	
	//Getter method for player's mana.
	public int getMana() {
		return mana;
	}
	
	//Setter method for player's mana.
	public void setMana(int mana) {
		this.mana = mana;
	}
	
	//Getter method for player's maximum mana.
	public int getMaxMana() {
		return maxMana;
	}

	//Setter method for player's maximum mana.
	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}
	
	//Reload the player's mana at the beginning of their turn.
	public void reloadMana() {	
		if(maxMana < 9) {
			maxMana++;
			this.mana = maxMana;
		}else
		{
			this.mana=9;
		}
	}
	
	//Deduct mana from the player.
	public void deductMana(int mana)
	{
		this.mana = this.mana-mana;
	}

	//Generate a deck for the human player.
	public void generateHumanDeck() {
		cardDeck = OrderedCardLoader.getPlayer1Cards();
	}
	
	//Generate a deck for the AI player.
	public void generateAIDeck() {
		cardDeck = OrderedCardLoader.getPlayer2Cards();
	}

	//Draw a card from the deck and add it to the player's hand.
	public boolean drawCard() {
		if(cardDeck.size() > 0) {
			if (handCard.size()<6) {
				handCard.add(cardDeck.get(0));	
				cardDeck.remove(0);
				return true;
			}
			else {
				cardDeck.remove(0);
				return false;
			}	
		}
		return false;

	}
	
	
	//Show the player's hand cards.
	public void showCards(ActorRef out) {
		for(int i = 1;i < handCard.size()+1;i++) {
			BasicCommands.drawCard(out, handCard.get(i-1), i , 0);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	
	
	//Reset the order of the player's hand cards.
	public void resetCards(ActorRef out)
	{
		for(int i=0;i<7;i++)
		{
			BasicCommands.deleteCard(out, i);
			
		}
	}
	
	//Getter method for player's card deck.
	public List<Card> getCardDeck()
	{
		return this.cardDeck;
	}
	
	//Getter method for player's hand card.
	public List<Card> getHandCard()
	{
		return this.handCard;
	}
	
	
}
