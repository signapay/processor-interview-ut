package Datatypes;

/**
 * Datatype Object for Card
 */
public class Card implements Comparable<Card>{
    public String getName(){return name;}
    private String name;    // used for enforcing uniqueness

    /**
     * Accessor for balance
     * @return current balance
     */
    public Money getBalance(){return balance;}
    private Money balance;

    /**
     * Accessor for card number
     * @return card number
     */
    public long cardNumber(){return cardNumber;}
    private long cardNumber;

    /**
     * Constructor [WARNING: does not do type checking]
     * @param name Card Owner
     * @param cardNumber Card Number
     * @param balance Card Balance
     */
    public Card(String name, Long cardNumber, Money balance){
        this.balance = balance;
        this.name = name;
        this.cardNumber = cardNumber;
    }

    /**
     * Get new card total
     * @param other card to add (adds to this card)
     * @return updated balance
     */
    public Money add(Card other){
        if (other != null && balance != null && other.balance != null) this.balance = this.balance.add(other.balance);
        return balance;
    }

    /**
     * Compare cards. If other card is null, return 1. Otherwise, return card number comparison.
     * @param o the object to be compared.
     * @return compare result
     */
    @Override
    public int compareTo(Card o) {
        if (o == null) return Long.compare(this.cardNumber, o.cardNumber);
        return 1;
    }
}
