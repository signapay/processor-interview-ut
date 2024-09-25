import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Interface class for logging to file
 */
public class Log {
    // designated file path for log file; defaults to directory
    public static Path file = Paths.get(System.getProperty("user.dir"), "log.txt" );

    /**
     * Log message to designated log file using designated logging function
     * @param message text to be logged
     * @return true if succeeded, false if failed
     */
    public static boolean log(String message){
        return LogToFile.log(message, file);
    }
}
