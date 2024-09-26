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


    private Database<String> failedTransactionParses = new Database<String>(failedTransactionReporting);
    private Database<Card> badAccounts = new Database<Card>(badAccountPersistenceFile);
    private TreeMap<Long, String> cardSet = new TreeMap<Long, String>();
    private TreeMap<String, Account> accountSet = new TreeMap<String, Account>();
    private Database<Transaction> failedTransactionLogic = new Database<Transaction>(failedTransactionParseReporting);

    /**
     * Constructor for database
     */
    public DataManager(){
        super(transactionPersistenceFile);
    }

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
                    }
                }
                else{
                    // add failed transactions to list
                    failedTransactionParses.records.add(nextLine);
                }
            }

            // revalidate initial logic failures
            revalidateFailedLogic();

            // validate and record failed accounts
            auditAccounts();
            badAccounts.save();

            // rewrite failed transactions
            failedTransactionParses.save();
            failedTransactionLogic.save();

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

    private boolean addTransactionEvent(Transaction transaction){
        // check card validity
        if (validateCard(transaction.getCardNumber(), transaction.getAccountName().toLowerCase())){
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
            failedTransactionLogic.records.add(transaction);
            return false;
        }
    }

    private boolean accountProcessing(Account account, Transaction transaction){
        // case Transfer
        if(transaction.getTransactionType().equals("Transfer")){
            // find target account
            if (cardSet.containsKey(transaction.getTargetCardNumber())){
                // get target account
                Account targetAccount = accountSet.get(cardSet.get(transaction.getTargetCardNumber()).toLowerCase());
                // add to accounts
                account.addCard(new Card(transaction.getAccountName().toLowerCase(),
                        transaction.getCardNumber(), transaction.getTransactionAmount()));
                targetAccount.addCard(new Card(targetAccount.getName().toLowerCase(),
                        transaction.getTargetCardNumber(),
                        Money.make(-1 * transaction.getTransactionAmount().getTotalCents())));
                return true;
            }
            else{
                Log.log("Error: card number associated with " + transaction.getAccountName() +
                        " already registered with " + cardSet.get(transaction.getCardNumber()) +
                        "; transaction" + transaction);
                failedTransactionLogic.records.add(transaction);
            }
        }
        // case Credit or Debit
        else if(transaction.getTransactionType().equals("Credit") || transaction.getTransactionType().equals("Debit")){
            // add card amount
            account.addCard(new Card(transaction.getAccountName().toLowerCase(),
                    transaction.getCardNumber(), transaction.getTransactionAmount()));
            return true;
        }
        Log.log("Error: unknown transaction type; transaction " + transaction);
        failedTransactionLogic.records.add(transaction);
        return false;
    }

    private boolean validateCard(long cardNumber, String name){
        // if null string, evaluate for existence
        if (name == null){
            // if card is known, return true. Else return false.
            if (cardSet.containsKey(cardNumber)){
                return true;
            }
            return false;
        }

        // if not null string, evaluate for account holder
        else{
            // case card exists
            if (cardSet.containsKey(cardNumber)) {
                // if card name matches the name on record
                if (name.toLowerCase().equals(cardSet.get(cardNumber).toLowerCase())){
                    return true;
                }
                return false;
            }
            // case card does not exist
            else{
                Account account;
                // if account does not exist, add account
                if (!accountSet.containsKey(name.toLowerCase())){
                    //create account and register card
                    cardSet.put(cardNumber, name.toLowerCase());    // add to card set
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

    private void auditAccounts(){
        badAccounts.records.clear();

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
    }

    // transaction list
    public ArrayList<Transaction> getTransactionList(){
        return records;
    }

    // failed transaction list
    public ArrayList<String> getFailedTransactionList(){
        ArrayList<String> list = new ArrayList<String>(failedTransactionParses.records);
        for (Transaction item : failedTransactionLogic.records){
            list.add(item.toString());
        }
        return list;
    }

    // account list
    public ArrayList<Account> getAllAccounts(){
        return new ArrayList<>(accountSet.values());
    }

    // truant card list
    public ArrayList<Card> getBadCards(){
        return new ArrayList<>(badAccounts.records);
    }

    // ovverrides
    @Override
    public boolean clearRecords(){
        records.clear();
        failedTransactionParses.clearRecords();
        badAccounts.clearRecords();
        cardSet.clear();
        accountSet.clear();
        failedTransactionLogic.clearRecords();
        return true;
    }
}