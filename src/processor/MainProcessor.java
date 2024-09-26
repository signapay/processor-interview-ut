package processor;

import javax.swing.*;

public class MainProcessor {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicUI();
        });
    }
}
