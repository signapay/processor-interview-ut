import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static String persistenceFile = "test.csv";//".backup.csv";

    public static void main(String args[]) {

        // internal state
        ArrayList<Transaction> records = new ArrayList<>();

        Database db = new Database(true);

        // test file load
        System.out.println(db.records);

        // load gui
        GUI gui = new GUI();
        gui.initialize(db);

        // clear files
        //db.clearRecords();

        // test file load
        System.out.println(db.records);
    }
}
