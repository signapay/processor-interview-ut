import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Class to log text to a specified file
 */
public class LogToFile {
    // error with file flag, to prevent console spam
    public static boolean errorNotReported = true;

    /**
     * Log text to file; logs in append mode
     * @param text text to be logged
     * @param file Path of file to log to
     * @return true if succeeded, or false if failed
     */
    public static boolean log(String text, Path file){
        try {
            // open file in append mode
            PrintWriter writer = new PrintWriter(new FileOutputStream(file.toString(), true));

            // log information
            writer.println(text);

            // close file
            writer.close();

            return true;
        }
        catch(Exception e) {
            // report error; only print once to not clog console
            if (errorNotReported) {
                System.out.println("NOTICE: Log file " + file + " is unavailable. Logging to file failed. Check log file path.");
                errorNotReported = false; // permits only one printing regardless of log file titles for simplicity
            }
        }
        return false;
    }
}
