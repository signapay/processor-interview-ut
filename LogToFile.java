import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;

// log text to specified file
public class LogToFile {
    public static boolean errorNotReported = true;

    // log to file
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
