// data class
public class Transaction {

    // fields -------------------
    // account name
    private String accountName;
    public String getAccountName(){return accountName;}
    // card number
    private long cardNumber;
    public long getCardNumber(){return cardNumber;}
    // transaction amount
    private Money transactionAmount;
    public Money getTransactionAmount(){return transactionAmount;}
    // transaction type
    private String transactionType;
    public String getTransactionType(){return transactionType;}
    // transaction description
    private String description;
    public String getDescription(){return description;}
    // target card
    private boolean hasTargetCard = false;
    private long targetCardNumber;
    public long getTargetCardNumber(){return targetCardNumber;}

    // constructor ---------------
    // concealed, does nothing
    private Transaction(){ }

    // construction factory ----------
    public static Transaction make(String input){
        // create transaction
        Transaction transaction = new Transaction();

        // parse items
        String[] arguments = input.split("\\s*,\\s*"); // whitespace, comma, whitespace

        // check parse element number, reject invalid count
        if (arguments.length < 5 || arguments.length > 6){
            Log.log("Transaction parse failed; expected 5 or 6 arguments, found " + arguments.length + "; transaction: " + input);
            return null;
        }

        // clean whitespace from items
        for(int i = 0; i < arguments.length; i++) {arguments[i] = arguments[i].trim();}

        // parse 1: account name --------------------
        // reject impossible length. defaulted to 1, but can be changed.
        if (arguments[0].length() < 1){
            Log.log("Transaction parse failed; no account name; transaction: " + input);
            return null;
        }
        transaction.accountName = arguments[0];

        // parse 2: card number --------------------
        try{
            long cardNumber = Long.parseLong(arguments[1]); // parse int
            if (cardNumber < 1){
                // NOTE: not sure what min/max length are.
                // reject illegal number.
                Log.log("Transaction parse failed; invalid account number; transaction: " + input);
                return null;}
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
        // if transaction amount is negative, reject that also
        // actually permitted, because I don't know the rules of transaction amounts
//        if (transaction.transactionAmount.isNegative()){
//            Log.log("Transaction parse failed; transaction amount cannot be negative; transaction: " + input);
//            return null;
//        }

        // parse 4: transaction type --------------------
        String type = arguments[3].toLowerCase();  // get holotype
        // assign type
        if (type.equals("credit")){ transaction.transactionType = "Credit";}
        else if (type.equals("debit")){ transaction.transactionType = "Debit";}
        else if (type.equals("transfer")){ transaction.transactionType = "Transfer";}
        else {
            Log.log("Transaction parse failed; invalid transaction type; transaction: " + input);
            return null;
        }

        // parse 5: description --------------------
        transaction.description = arguments[4];
        // transaction description is permitted to be absent

        // parse 6: target card --------------------
        // branching paths
        if (transaction.transactionType.equals("Transfer")){
            // reject invalid length
            if (arguments.length == 5){
                Log.log("Transaction parse failed; missing target card number; transaction: " + input);
                return null;
            }

            // parse number value
            try{
                long cardNumber = Long.parseLong(arguments[5]); // parse int
                if (cardNumber < 1){
                    // NOTE: not sure what min/max length are.
                    // reject illegal number.
                    Log.log("Transaction parse failed; invalid target account number; transaction: " + input);
                    return null;}
                transaction.cardNumber = cardNumber;
                transaction.hasTargetCard = true;   // override flag
            }
            catch (Exception e){
                Log.log("Transaction parse failed; invalid target account number; transaction: " + input);
                return null;
            }
        }
        else{
            //permit extra information
            if (arguments.length == 6 && arguments[5].length() > 0){
                Log.log("Transaction parse recovered: found card number for non-transfer transaction; transaction: " + input);
            }
        }

        // return transaction
        return transaction;
    }

    public String toCsv() {
        // add trailing comma if missing
        if (hasTargetCard) return toString();
        return toString() + ",";    // comma separation for relevant records
    }

    @Override
    public String toString(){
        String temp = "";
        if (hasTargetCard) temp = "," + targetCardNumber;
        return new StringBuilder().append(accountName).append(",").append(cardNumber).append(",").
        append(transactionAmount).append(",").append(transactionType).append(",").append(description).
        append(",").append(temp).toString();
    }

    //Field	Type	Description
    //Account Name	Text	The name of the account
    //Card Number	Number	The card number used for the transaction, 1:M with account names
    //Transaction Amount	Number	The amount of the transaction, can be positive or negative
    //Transaction Type	Text	The type of transaction, values can be Credit, Debit, or Transfer
    //Description	Text	A brief description of the transaction
    //Target Card Number	Number	only provided if the transaction type is a transfer
}
