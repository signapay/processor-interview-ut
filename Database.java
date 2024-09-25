import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Database {
    public static String persistenceFile = ".backup.csv";

    // internal memory
    public ArrayList<Transaction> records = new ArrayList<>();

    public Database(boolean read){
        if (read){
            // read file if constructor requests
            readFromFile(persistenceFile);
        }
    }

    // read from file
    public boolean readFromFile(String file){
        // open specified file and read valid transactions to record
        try {
            Scanner scan = new Scanner(new FileInputStream(file));
            Transaction transaction = null;
            while (scan.hasNext()){
                transaction = Transaction.make(scan.nextLine());
                if (transaction != null){
                    records.add(transaction);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            Log.log("Failed to open and read from file '" + file + "'.");
            return false;
        }
    }

    // save file
    public boolean save(){
        return writeToFile(new File(persistenceFile));
    }

    // write to file
    public boolean writeToFile(File file){
        // open writing
        try{
            // open file in append mode
            PrintWriter writer = new PrintWriter(new FileOutputStream(file.toString(), false));

            // log information
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

    // remove file
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

    // clear transactions
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

    public boolean hasRecords(){
        if (records.isEmpty()) return false;
        return true;
    }

}
