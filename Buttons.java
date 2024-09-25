import javax.swing.*;

/**
 * Factory class for button objects
 */
public class Buttons{
    /**
     * Generate file selection button
     * @param state state to register button to, or null if no state desired
     * @return Button for file selection
     */
    public static JButton generateFileButton(StateManager state){
        JButton button = new JButton("Select File");
        button.setActionCommand("select_file");
        button.setToolTipText("Choose a CSV file from the filesystem to load data from.");

        if (state != null) button.addActionListener(state);

        return button;
    }

    /**
     * Generate button for File opening
     * @param state state to register button to, or null if no state desired
     * @return Button for file opening
     */
    public static JButton generateOpenButton(StateManager state){
        JButton button = new JButton("Load File");
        button.setActionCommand("read_file");
        button.setToolTipText("Load selected file into memory (appends contents to transactions already found).");

        button.setEnabled(state.hasFile());

        if (state != null) {
            state.registerOpenButton(button);
            button.addActionListener(state);
        }

        return button;
    }

    /**
     * Generate button for record erasing
     * @param state state to register button to, or null if no state desired
     * @return Button for record erasing
     */
    public static JButton generateClearRecordButton(StateManager state){
        JButton button = new JButton("Clear Records");
        button.setActionCommand("clear_record");
        button.setToolTipText("Clear memory. Warning: will clear persistence file.");

        if (state != null) {
            state.registerClearButton(button);
            button.addActionListener(state);
        }

        return button;
    }

    /**
     * Generate display button accessory text label
     * @param state state, or null if no state desired
     * @return Label object
     */
    public static JLabel generateButtonLabel(StateManager state){
        JLabel label = new JLabel();
        if (state != null) state.registerButtonLabel(label);

        return label;
    }
}
