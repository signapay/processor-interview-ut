/**
 * Immutable data object class for money
 */
public class Money implements Comparable<Money>{
    private int totalCents;

    // accessors -----------------

    /**
     * Get dollar value
     * @return dollars with sign
     */
    public int getDollars(){return totalCents / 100;}

    /**
     * Get cent value
     * @return cents with sign
     */
    public int getCents(){return totalCents % 100;}

    /**
     * Get total in cents
     * @return total in cents with sign
     */
    public int getTotalCents(){return totalCents;}

    // helper methods ---------------------

    /**
     * Evaluate a number's sign as positive or negative
     * @param element integer value to test
     * @return false if negative, true if positive
     */
    public static boolean evaluateSign(int element){if (element < 0) return false; else return true;}

    // Constructors --------------------

    /**
     * Concealed constructor
     */
    private Money(){}

    // factory from string

    /**
     * Object factory from string
     * @param input string of monetary amount
     * @return Money object, or null if failed to convert string
     */
    public static Money make(String input){
        input = input.strip();  // remove leading and trailing whitespace

        try {
            Money newMoney = new Money();

            // evaluate sign polarity
            int sign = 1;
            if (input.charAt(0) == '-') {
                sign = -1; // evaluate sign
                input = input.substring(1);
            }

            // get decimal location
            int decimal = input.indexOf('.');

            // case: no decimal (dollars only)
            if (decimal == -1){
                newMoney.totalCents = sign * 100 * Integer.parseInt(input);
                return newMoney;
            }
            // case: decimal is first (cents only)
            if (decimal == 0){
                newMoney.totalCents = sign * Integer.parseInt(input.substring(1));
                return newMoney;
            }
            // case: decimal is embedded (dollars and cents)
            newMoney.totalCents = sign * ((100 * Integer.parseInt(input.substring(0,decimal))) +
                    Integer.parseInt(input.substring(decimal + 1)));
            return newMoney;
        }
        catch(NumberFormatException e){
            Log.log("Number parse error: illegal characters detected.");
        }
        catch(IndexOutOfBoundsException e){
            Log.log("Number parse error: missing length detected.");
        }
        catch(Exception e){
            Log.log("Number parse error: unknown error occurred.");
        }

        return null;
    }

    /**
     * Object factory from total of cents
     * @param input integer number of total cents
     * @return new Money object
     */
    public static Money make(int input){
        Money temp = new Money();
        temp.totalCents = input;
        return temp;
    }

    // other ----------------------

    /**
     * Add
     * @param other amount to be added
     * @return new Money object of sum amount, or this if other is null
     */
    public Money add(Money other){
        if (other == null) return this;
        return Money.make(this.totalCents + other.totalCents);
    }

    /**
     * Subtract
     * @param other amount to be subtracted
     * @return new Money object of difference amount, or this if other is null
     */
    public Money subtract(Money other){
        if (other == null) return this;
        return Money.make(this.totalCents - other.totalCents);
    }

    /**
     * Compare equality of two money objects.
     * @param other Money object to be compared
     * @return true if equal value, false otherwise or if other is null.
     */
    public boolean equals(Money other){
        if (other == null) return false;
        return this.totalCents == other.totalCents;
    }

    /**
     * Comparison
     * @param o the object to be compared.
     * @return integer of comparison. positive is >, 0 is equal, negative is <
     */
    @Override
    public int compareTo(Money o) {
        if (o == null) throw new NumberFormatException("Cannot compare null to object");
        return Integer.compare(this.totalCents, o.totalCents);
    }

    /**
     * Convert to String
     * @return String representation of value
     */
    @Override
    public String toString(){
        // set sign to print
        String signPrint = "-";
        if (evaluateSign(totalCents)) signPrint = "";

        // set zeros display
        int cents = Math.abs(getCents());
        String centDisplay = Integer.toString(cents);
        if (cents >= 0 && cents < 10) centDisplay = "0" + centDisplay; // add leading zero
        //note: can be done more cleanly with print format

        return signPrint + Math.abs(getDollars()) + "." + centDisplay;
    }
}
