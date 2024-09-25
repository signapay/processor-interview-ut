public class Money {
    private int totalCents;

    // accessors
    public int getDollars(){return totalCents / 100;}
    public int getCents(){return totalCents % 100;}
    public int getTotalCents(){return totalCents;}
    public boolean isNegative(){return !evaluateSign(totalCents);}

    // helper methods
    public static boolean evaluateSign(int element){if (element < 0) return false; else return true;}

    // private constructor
    private Money(){};

    // factory from string
    public static Money make(String input){
        input = input.strip();  // remove whitespace

        try {
            Money newMoney = new Money();

            // evaluate sign
            int sign = 1;
            if (input.charAt(0) == '-') {
                sign = -1; // evaluate sign
                input = input.substring(1);
            }

            // get decimal
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

    // factory constructor from total of cents
    public static Money make(int input){
        Money temp = new Money();
        temp.totalCents = input;
        return temp;
    }

    public Money add(Money other){
        if (other == null) return this;
        Money temp = new Money();
        temp.totalCents = this.totalCents + other.totalCents;
        return temp;
    }

    public Money subtract(Money other){
        if (other == null) return this;
        Money temp = new Money();
        temp.totalCents = this.totalCents - other.totalCents;
        return temp;
    }

    // comparison function for equals
    public boolean equals(Money other){
        if (other == null) return false;
        if (this.totalCents == other.totalCents) return true;
        return false;
    }

    // to string
    @Override
    public String toString(){
        // set sign print
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
