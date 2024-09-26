package Datatypes;

import Log.Log;

/**
 * Transaction data storage object (immutable).
 * Fields: accountName, cardNumber, transactionAmount, transactionType, description, targetCardNumber
 */
public class Transaction implements toCSV {
    private static final int CARD_LENGTH = 16;
    private static final int MIN_NAME = 3;

    // fields -------------------
    /**
     * Getter for accountName.
     * account name (text): The name of the account
     * @return accountName
     */
    public String getAccountName(){return accountName;}
    private String accountName;

    /**
     * Getter for cardNumber.
     * card number (number): The card number used for the transaction, 1:M with account names
     * @return cardNumber
     */
    public long getCardNumber(){return cardNumber;}
    private long cardNumber;

    /**
     * Getter for transactionAmount.
     * transaction amount (number): The amount of the transaction, can be positive or negative
     * @return transactionAmount
     */
    public Money getTransactionAmount(){return transactionAmount;}
    private Money transactionAmount;

    /**
     * Getter for transactionType.
     * transaction type (text): The type of transaction, values can be Credit, Debit, or Transfer
     *  (will be 'Credit', 'Debit', or 'Transfer')
     * @return transactionType
     */
    public String getTransactionType(){return transactionType;}
    private String transactionType;

    /**
     * Getter for description.
     * transaction description (text): A brief description of the transaction
     * @return description
     */
    public String getDescription(){return description;}
    private String description;

    /**
     * Getter for targetCardNumber.
     * target card (number): only provided if the transaction type is a transfer
     * @return targetCardNumber, or -1 if no such number
     */
    public long getTargetCardNumber(){
        if (hasTargetCard) return targetCardNumber;
        return -1;
    }
    private boolean hasTargetCard = false; // used to determine internally if target card exists
    private long targetCardNumber;

    // construction ----------
    /**
     * Concealed constructor
     */
    private Transaction(){ }

    /**
     * Construction factory for transaction object. Prints to log on fail.
     * @param input comma-separated string of the form "accountName, cardNumber, transactionAmount, transactionType,
     *              transactionDescription, targetCardNumber (optional)"
     * @return Transaction, or null if invalid
     */
    public static Transaction make(String input){
        // if null string, reject
        if (input == null) return null;

        // create transaction
        Transaction transaction = new Transaction();

        // parse items
        String[] arguments = input.split("\\s*,\\s*"); // regex: 'whitespace, comma, whitespace' is separator

        // check parse element number, reject invalid count
        if (arguments.length < 5 || arguments.length > 6){
            Log.log("Transaction parse failed; expected 5 or 6 arguments, found " + arguments.length + "; transaction: " + input);
            return null;
        }

        // clean leading and trailing whitespace from items
        for(int i = 0; i < arguments.length; i++) {arguments[i] = arguments[i].strip();}

        // parse 1: account name --------------------
        // requires a minimum length, alphabet characters and - or , or whitespace only.
        // reject impossible length. defaulted to 1, but can be changed.
        if (arguments[0].length() < 1){
            Log.log("Transaction parse failed; no account name; transaction: " + input);
            return null;
        }
        // clean whitespace to at most one character per word
        String[] components = arguments[0].split("\\s+"); // split on whitespace
        String result = "";
        for (String component:components) {
            result += component + " ";
        }
        result = result.strip(); // remove trailing whitespace
        // if illegal number of elements, reject
        if (result.length() < MIN_NAME){
            Log.log("Transaction parse failed; no account name; transaction: " + input);
            return null;
        }
        for(int i = 0; i < result.length(); i++){
            char a = result.charAt(i);
            if (!(Character.isAlphabetic(a) ||
                    Character.isWhitespace(a) ||
                    a == '\'' || a == '-')){
                Log.log("Transaction parse failed; illegal character in account name; transaction: " + input);
                return null;
            }
        }
        transaction.accountName = arguments[0];

        // parse 2: card number --------------------
        try{
            long cardNumber = Long.parseLong(arguments[1]); // parse int
            // reject negative number.
            if (cardNumber < 1){
                Log.log("Transaction parse failed; invalid account number (negative number); transaction: " + input);
                return null;
            }
            // reject impossible card number
            if (arguments[1].length() != CARD_LENGTH){
                Log.log("Transaction parse failed; invalid account number. Expected length " + CARD_LENGTH + "; found length " + arguments[1].length() + "; transaction: " + input);
                return null;
            }
            transaction.cardNumber = cardNumber;
        }
        catch (Exception e){
            Log.log("Transaction parse failed; invalid account number; transaction: " + input);
            return null;
        }

        // parse 3: transaction amount --------------------
        transaction.transactionAmount = Money.make(arguments[2]);
        // if failed conversion, fail parse
        if (transaction.transactionAmount == null){
            Log.log("Transaction parse failed; invalid transaction amount; transaction: " + input);
            return null;
        }

        // parse 4: transaction type --------------------
        String type = arguments[3].toLowerCase();  // get transaction holotype
        // assign type
        if (type.equals("credit")){ transaction.transactionType = "Credit";}
        else if (type.equals("debit")){ transaction.transactionType = "Debit";}
        else if (type.equals("transfer")){ transaction.transactionType = "Transfer";}
        else {
            Log.log("Transaction parse failed; invalid transaction type; transaction: " + input);
            return null;
        }

        // parse 5: description --------------------
        // transaction description is permitted to be absent
        transaction.description = arguments[4];

        // parse 6: target card --------------------
        // case type Transfer
        if (transaction.transactionType.equals("Transfer")){
            // reject invalid length
            if (arguments.length == 5){
                Log.log("Transaction parse failed; missing target card number; transaction: " + input);
                return null;
            }

            // parse number value
            try{
                long cardNumber = Long.parseLong(arguments[5]); // parse int
                // reject negative number.
                if (cardNumber < 1){
                    Log.log("Transaction parse failed; invalid account number (negative number); transaction: " + input);
                    return null;
                }
                // reject impossible card number
                if (arguments[5].length() != CARD_LENGTH){
                    Log.log("Transaction parse failed; invalid account number. Expected length " + CARD_LENGTH + "; found length " + arguments[5].length() + "; transaction: " + input);
                    return null;
                }
                transaction.targetCardNumber = cardNumber;
                transaction.hasTargetCard = true;   // override flag
            }
            catch (Exception e){
                Log.log("Transaction parse failed; invalid target account number; transaction: " + input);
                return null;
            }
        }
        // case type non-transfer
        else{
            //permit extra information
            if (arguments.length == 6 && arguments[5].length() > 0){
                Log.log("Transaction parse recovered: found target card number for non-transfer transaction; transaction: " + input);
            }
        }

        // return transaction
        return transaction;
    }

    // other methods ------------------------

    /**
     * Generate string for csv storage; adds commas where elements are missing.
     * @return comma-separated string
     */
    public String toCsv() {
        if (hasTargetCard) return toString();   // add trailing comma if missing
        return this + ",";    // comma separation for relevant records
    }

    /**
     * Generate string for display.
     * @return string
     */
    @Override
    public String toString(){
        // if no target card number, omit element and associated comma
        String temp = "";
        if (hasTargetCard) temp = "," + targetCardNumber;
        // string
        return accountName + "," + cardNumber + "," +
                transactionAmount + "," + transactionType + "," + description + temp;
    }
}