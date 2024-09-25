import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Record management class
 */
public class Database {
    // internal persistence location
    public static final String persistenceFile = ".backup.csv";

    // internal memory
    public ArrayList<Transaction> records = new ArrayList<>();

    /**
     * Constructor for database
     * @param read boolean for reading from persistence file (true for yes)
     */
    public Database(boolean read){
        if (read){
            // read file if constructor requests
            readFromFile(persistenceFile);
        }
    }

    /**
     * Add records from file
     * @param file File to read from
     * @return boolean indicating success
     */
    public boolean readFromFile(String file){
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

    /**
     * Save records to persistence file
     * @return boolean indicating success
     */
    public boolean save(){
        return writeToFile(new File(persistenceFile));
    }

    /**
     * Write record state to specified file
     * @param file File to write to (overwrites)
     * @return boolean indicating success
     */
    public boolean writeToFile(File file){
        // open writing
        try{
            // open file in overwrite mode
            PrintWriter writer = new PrintWriter(new FileOutputStream(file.toString(), false));

            // record information
            for (Transaction item : records){
                writer.println(item.toCsv()); // write to file
            }

            // close file
            writer.close();

            return true;
        }
        catch (Exception e){
            Log.log("Failed to open and write to file '" + file + "'.");
            return false;
        }
    }

    /**
     * delete file from filesystem
     * @param file File to remove
     * @return boolean indicating success
     */
    public boolean removeFile(File file){
        // test if file is file
        if (file.isFile()){
            try{
                file.delete();  // delete file
                Log.log("Deleted file '" + file + "'");
                return true;
            }
            catch (Exception e){
                Log.log("Cannot delete file '" + file + "'; error occurred when trying to delete.");
                return false;
            }
        }
        else{
            Log.log("Cannot delete file '" + file + "'; file does not exist.");
            return true;
        }
    }

    /**
     * Clear internal record state. Erases dynamic state and persistent record.
     * @return boolean indicating success
     */
    public boolean clearRecords(){
        if (removeFile(new File(persistenceFile))){
            records.clear();
            return true;
        }
        else{
            Log.log("Cannot clear memory due to file persistence.");
            return false;
        }
    }

    /**
     * Determine if the record state has any records
     * @return true if records, false if no records
     */
    public boolean hasRecords(){
        if (records.isEmpty()) return false;
        return true;
    }

}
