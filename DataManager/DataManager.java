package DataManager;
import Datatypes.*;
import Log.Log;
import java.io.*;
import java.util.*;

/**
 * Record management class
 */
public class DataManager extends Database<Transaction>{
    // internal persistence location
    public static final File transactionPersistenceFile = new File(".backup.csv");
    public static final File failedTransactionReporting = new File("failed_transactions.csv");
    public static final File failedTransactionParseReporting = new File("failed_transactions_parses.csv");
    public static final File badAccountPersistenceFile = new File("failed_audit.csv");


    private final Database<String> failedTransactionParses = new Database<String>(failedTransactionReporting);
    private final Database<Card> badAccounts = new Database<Card>(badAccountPersistenceFile);
    /**
     * Maps card number to account name (unformatted string)
     */
    private final TreeMap<Long, String> cardSet = new TreeMap<>();
    /**
     * Maps account name (lowercase) to account associated
     */
    private final TreeMap<String, Account> accountSet = new TreeMap<>();
    private final Database<Transaction> failedTransactionLogic = new Database<Transaction>(failedTransactionParseReporting);

    /**
     * Constructor for database
     */
    public DataManager(){
        super(transactionPersistenceFile);
    }

    // ----------------- read from file -----------------------
    /**
     * Add records from file
     * @param file File to read from
     * @return boolean indicating success
     */
    @Override
    public boolean readFromFile(File file){
        // open specified file and read valid transactions to record
        try {
            Scanner scan = new Scanner(new FileInputStream(file));
            Transaction transaction;
            // for each record, attempt to read as transaction
            while (scan.hasNextLine()){
                String nextLine = scan.nextLine();
                transaction = Transaction.make(nextLine);
                if (transaction != null){
                    if (addTransactionEvent(transaction)) {
                        records.add(transaction);
                    }
                    else{
                        failedTransactionLogic.records.add(transaction);
                        // error already logged by addTransactionEvent
                    }
                }
                else{
                    // add failed transactions to list
                    failedTransactionParses.records.add(nextLine);
                    // error already logged by parsing
                }
            }

            // revalidate initial logic failures
            revalidateFailedLogic();

            // validate and record failed accounts
            auditAccounts();

            // rewrite failed transactions
            failedTransactionParses.save();
            failedTransactionLogic.save();

            // save transaction records
            save();

            return true;
        } catch (FileNotFoundException e) {
            Log.log("Failed to open and read from file '" + file + "'; file not found.");
            return false;
        }
        catch (Exception e){
            Log.log("Failed to open and read from file '" + file + "'; unknown error.");
            return false;
        }
    }


    /**
     * High-level processing of adding a transaction to the database, or finding it is invalid
     * @param transaction transaction to attempt to integrate
     * @return boolean indicating success
     */
    private boolean addTransactionEvent(Transaction transaction){
        // check card validity
        if (validateCard(transaction.getCardNumber(), transaction.getAccountName())){
            // call transaction processing
            Account account = accountSet.get(transaction.getAccountName().toLowerCase());
            return accountProcessing(account, transaction);
        }
        // if invalid, report error
        else{
            // else, illegal card number
            Log.log("Error: card number associated with " + transaction.getAccountName() +
                    " already registered with " + cardSet.get(transaction.getCardNumber()) +
                    "; transaction" + transaction);
            return false;
        }
    }

    /**
     * Aggregate card to account if transaction type is valid, or report error if not
     * @param account Account object to add transaction to
     * @param transaction Transaction to evaluate
     * @return true if succeeded, or false if transaction fails
     */
    private boolean accountProcessing(Account account, Transaction transaction){
        // case Transfer: requires additional processing to validate other card
        if(transaction.getTransactionType().equals("Transfer")){
            // find target account
            if (cardSet.containsKey(transaction.getTargetCardNumber())){
                // get target account
                Account targetAccount = accountSet.get(cardSet.get(transaction.getTargetCardNumber()).toLowerCase());
                // add transfer to primary account
                account.addCard(new Card(transaction.getAccountName(),
                        transaction.getCardNumber(), transaction.getTransactionAmount()));
                // remove transfer from secondary account
                targetAccount.addCard(new Card(targetAccount.getName(),
                        transaction.getTargetCardNumber(),
                        Money.make(-1 * transaction.getTransactionAmount().getTotalCents())));
                return true;
            }
            else{
                Log.log("Error: card number " + transaction.getTargetCardNumber() + "associated with " + transaction.getAccountName() +
                        " already registered with " + cardSet.get(transaction.getCardNumber()) +
                        "; transaction" + transaction);
            }
        }
        // case Credit or Debit
        else if(transaction.getTransactionType().equalsIgnoreCase("Credit")
                || transaction.getTransactionType().equalsIgnoreCase("Debit")){
            // add card amount
            account.addCard(new Card(transaction.getAccountName(),
                    transaction.getCardNumber(), transaction.getTransactionAmount()));
            return true;
        }
        Log.log("Error: unknown transaction type; transaction " + transaction);
        return false;
    }

    /**
     * Evaluate card for presence of dopplegangers with different information; if card does not exist, instantiates.
     * Also source of account instantiation.
     * @param cardNumber number to search for
     * @param name name associated with card
     * @return boolean for whether card is valid or not
     */
    private boolean validateCard(long cardNumber, String name){
        // if null string, evaluate for existence
        if (name == null){
            // if card is known, return true. Else return false.
            return cardSet.containsKey(cardNumber);
        }

        // if not null string, evaluate for account holder
        else{
            // case card exists
            if (cardSet.containsKey(cardNumber)) {
                // if card name matches the name on record
                return name.equalsIgnoreCase(cardSet.get(cardNumber));
            }
            // case card does not exist
            else{
                Account account;
                // if account does not exist, add account
                if (!accountSet.containsKey(name.toLowerCase())){
                    //create account and register card
                    cardSet.put(cardNumber, name);    // add to card set
                    account = new Account(name);            // add account
                    accountSet.put(name.toLowerCase(), account);
                }
                else{account = accountSet.get(name.toLowerCase());} // otherwise get account
                // since card does not exist, no need to worry about collision
                account.addCard(new Card(name, cardNumber, Money.make(0))); // register card to account
                return true;
            }
        }
    }

    /**
     * Reevaluates the logic failure transactions; does NOT write to persistent files
     */
    private void revalidateFailedLogic(){
        ArrayList<Transaction> retry = new ArrayList<>(failedTransactionLogic.records);
        failedTransactionLogic.records.clear(); // reset data

        Collections.reverse(retry); // reverse list

        // for each item, attempt to reintegrate. if successful, add to records. Else add to failed again.
        for(Transaction item : retry){
            if (addTransactionEvent(item)) {
                records.add(item);
            }
            else{
                failedTransactionLogic.records.add(item);
            }
        }
    }


    /**
     * Filter all validated cards and collect those with negative balances; writes result to file
     */
    private void auditAccounts(){
        badAccounts.records.clear();    // clear bad account records

        // for each account
        for(Map.Entry<String, Account> accountSet : accountSet.entrySet()) {
            Account account = accountSet.getValue();    // get account

            // for each card
            for (Card card : account.getCardSetCopy()) {
                // if balance less than zero, add account card to audit
                if (card.getBalance().getTotalCents() < 0) {
                    badAccounts.records.add(card);
                }
            }
        }
        // save
        badAccounts.save();
    }

    // ------------------------- remove files ------------------------
    /**
     * Clear records of internal memory; overridden from super class to clear records of other contained databases
     * @return true
     */
    @Override
    public boolean clearRecords(){
        records.clear();
        failedTransactionParses.clearRecords();
        badAccounts.clearRecords();
        cardSet.clear();
        accountSet.clear();
        failedTransactionLogic.clearRecords();
        removeFile(persistenceFile);
        return true;
    }

    // data display retrieval ----------------------------------------------
    /**
     * Get copy of all valid transactions
     * @return
     */
    public ArrayList<Transaction> getTransactionList(){
        return new ArrayList<Transaction>(records);
    }

    /**
     * Get copy of list of failed transactions; subdivided by failure type
     * @return ArrayList of strings of failed transactions; includes title separators for subtype
     */
    public ArrayList<String> getFailedTransactionList(){
        ArrayList<String> list = new ArrayList<String>();
        list.add("Parse Errors:");
        list.addAll(failedTransactionParses.records);
        list.add("Transaction Logic Errors:");
        for (Transaction item : failedTransactionLogic.records){
            list.add(item.toString());
        }
        return list;
    }

    /**
     * Get copy of knwon valid accounts
     * @return ArrayList of accounts
     */
    public ArrayList<Account> getAllAccounts(){
        return new ArrayList<>(accountSet.values());
    }

    /**
     * Get copy of list of card accounts that failed audit
     * @return ArrayList of Cards
     */
    public ArrayList<Card> getBadCards(){
        return new ArrayList<>(badAccounts.records);
    }

}