package Datatypes;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Datatype for Account
 */
public class Account implements Comparable<Account>{
    private ArrayList<Card> cards = new ArrayList<>();
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
     * Compare
     * @param o the object to be compared.
     * @return 1 if other is null, or lowercase name comparison
     */
    @Override
    public int compareTo(Account o) {
        if (o == null) return 1;
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }

    /**
     * Retrieve Card; returns list's copy of card if found in card list or null if no such card
     * @param queryCard
     * @return
     */
    public Card getCard(Card queryCard){
        int index = cards.indexOf(queryCard);
        if (index == -1) return null;
        return cards.get(index);
    }

    /**
     * add card; accepts a card and either adds to the card list or adds the balance to the matching member
     * @param newCard
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
     * Get iterator over card list
     * @return Card iterator
     */
    public Iterator<Card> getCardIterable(){
        return cards.listIterator();
    }
}
