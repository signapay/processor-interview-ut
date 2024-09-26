package DataManager;

import Datatypes.Account;
import Datatypes.Transaction;
import Log.Log;
import java.io.*;
import java.util.Scanner;

/**
 * Record management class
 */
public class DataManager extends Database<Transaction>{
    // internal persistence location
    public static final File persistenceFile = new File(".backup.csv");

    private Database<String> failedTransactions = new Database<String>(new File("failed_transactions.csv"));
    private Database<Account> badAccounts = new Database<Account>(null);

    /**
     * Constructor for database
     */
    public DataManager(){
        super(persistenceFile);
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
            while (scan.hasNext()){
                transaction = Transaction.make(scan.nextLine());
                if (transaction != null){
                    records.add(transaction);
                }
            }
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
}
