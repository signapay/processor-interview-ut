package Datatypes;

import java.util.ArrayList;

/**
 * Datatype for Account
 */
public class Account implements Comparable<Account>{
    private final ArrayList<Card> cards = new ArrayList<>();

    /**
     * Accessor for account owner name
     * @return owner name
     */
    public String getName(){return name;}
    private String name = "";


    /**
     * Constructor
     * @param name account name string
     */
    public Account(String name){
        if (name == null) return;
        this.name = name;
    }

    /**
     * Compare to other account (by name)
     * @param o the object to be compared.
     * @return 1 if other is null, or lowercase name comparison
     */
    @Override
    public int compareTo(Account o) {
        if (o == null) return 1;
        return name.compareToIgnoreCase(o.name);
    }

    /**
     * Retrieve Card; returns list's copy of card if found in card list or null if no such card
     * @param queryCard card to search for
     * @return card from card list matching card sought, or null
     */
    public Card getCard(Card queryCard){
        for (Card card : cards){
            if (card.compareTo(queryCard) == 0) return card;
        }
        return null;
    }

    /**
     * add card; accepts a card and either adds to the card list or adds the balance to the matching member
     * @param newCard card to add or aggregate
     */
    public void addCard(Card newCard){
        Card extant = getCard(newCard);
        if (extant == null){
            cards.add(newCard);
        }
        else{
            extant.add(newCard);
        }
    }

    /**
     * Get a copy of the set of cards
     * @return copy of card set
     */
    public ArrayList<Card> getCardSetCopy(){
        return (new ArrayList<>(cards));
    }

    /**
     * Write to string for CSV
     * @return CSV string
     */
    @Override
    public String toString(){
        StringBuilder build = new StringBuilder();
        for(Card card:cards){
            build.append(name).append(",").append(card.cardNumber()).append(',').append(card.getBalance()).append('\n');
        }
        return build.toString();
    }
}
