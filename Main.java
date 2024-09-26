import DataManager.DataManager;

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
        DataManager dm = new DataManager();

        // load gui
        GUI gui = new GUI();
        gui.initialize(dm);

        // test file load
        //System.out.println(db.records);
    }
}
