package DataManager;
import Datatypes.toCSV;
import Log.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Database<T> {
    // internal memory
    public ArrayList<T> records = new ArrayList<>();

    // internal persistence location
    public final File persistenceFile;

    /**
     * Constructor for database
     * @param file file to read from, or null
     */
    public Database(File file){
        persistenceFile = file;
        if (file != null){
            // read file if constructor requests
            readFromFile(persistenceFile);
        }
    }

    // read from file ----------------
    public boolean readFromFile(File file){return true;}

    // write to file -----------------
    /**
     * Write record state to specified file
     * @param file File to write to (overwrites)
     * @return boolean indicating success
     */
    public boolean writeToFile(File file){
        // do not write if nothing to write
        if (records.isEmpty()) return true;
        // open writing
        try{
            // open file in overwrite mode
            PrintWriter writer = new PrintWriter(new FileOutputStream(file.toString(), false));

            // record information
            //case: implements toCSV
            if (records.get(0) instanceof toCSV){
                for (T item : records){
                    writer.println(((toCSV)item).toCsv()); // write to file
                }
            }
            else{
                for (T item : records){
                    writer.println(item); // write to file
                }
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
     * Save records to persistence file
     * @return boolean indicating success
     */
    public boolean save(){
        if (persistenceFile != null) return writeToFile(persistenceFile);
        return true;
    }

    // remove records ----------------
    /**
     * Clear internal record state. Erases dynamic state and persistent record.
     * @return boolean indicating success
     */
    public boolean clearRecords(){
        if (removeFile(persistenceFile)){
            records.clear();
            return true;
        }
        else{
            Log.log("Cannot clear memory due to file persistence.");
            return false;
        }
    }

    // control data ------------------

    /**
     * delete file from filesystem
     * @param file File to remove
     * @return boolean indicating success
     */
    public boolean removeFile(File file){
        if (file == null) return false;
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
     * Determine if the record state has any records
     * @return true if records, false if no records
     */
    public boolean hasRecords(){
        if (records.isEmpty()) return false;
        return true;
    }


}
