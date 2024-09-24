import javax.swing.*;
import javax.swing.plaf.nimbus.State;

public class Buttons{
    // file selection button
    public static JButton generateFileButton(StateManager state){
        JButton button = new JButton("Select File");
        button.setActionCommand("select_file");
        button.setToolTipText("Choose a CSV file from the filesystem to load data from.");
        button.addActionListener(state);
        return button;
    }

    // file commit button
    public static JButton generateOpenButton(StateManager state){
        JButton button = new JButton("Load File");
        button.setActionCommand("read_file");
        button.setToolTipText("Load selected file into memory (appends contents to transactions already found).");

        button.setEnabled(state.hasFile());

        state.registerOpenButton(button);
        button.addActionListener(state);

        return button;
    }

    // records clear button
    public static JButton generateClearRecordButton(StateManager state){
        JButton button = new JButton("Clear Records");
        button.setActionCommand("clear_record");
        button.setToolTipText("Clear memory. Warning: will clear persistence file.");

        state.registerClearButton(button);
        button.addActionListener(state);

        return button;
    }

    // button text label
    public static JLabel generateButtonLabel(StateManager state){
        JLabel label = new JLabel();
        state.registerButtonLabel(label);

        return label;
    }
}
