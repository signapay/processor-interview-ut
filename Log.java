import java.nio.file.Path;
import java.nio.file.Paths;

public class Log {
    // file path for log file; defaults to directory
    public static Path file = Paths.get(System.getProperty("user.dir"), "log.txt" );

    // log to file interface
    public static boolean log(String message){
        return LogToFile.log(message, file);
    }
}
