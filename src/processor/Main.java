package processor;

import javax.swing.SwingUtilities;

/**
 * Main class and method who calls the GUI injecting a MainProccessor object.
 * @author Will
 *
 */
public class Main {

    public static void main(String[] args) {
    	MainProcessor process = new MainProcessor();
        SwingUtilities.invokeLater(() -> {
            new GUI(process);
        });
    }
}
