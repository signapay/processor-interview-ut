import DataManage.Database;

/**
 * Main class
 */
public class Main {
    /**
     * Calling function for program
     * @param args n/a
     */
    public static void main(String[] args) {

        // internal state
        Database db = new Database(true);

        // load gui
        GUI gui = new GUI();
        gui.initialize(db);

        // test file load
        //System.out.println(db.records);
    }
}
