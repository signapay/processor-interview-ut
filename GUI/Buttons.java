package GUI;

import DataManager.StateManager;

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
        JButton button = generateButton("Select File",
                "select_file",
                "Choose a CSV file from the filesystem to load data from.");

        if (state != null) button.addActionListener(state);

        return button;
    }

    /**
     * Generate button for File opening
     * @param state state to register button to, or null if no state desired
     * @return Button for file opening
     */
    public static JButton generateOpenButton(StateManager state){
        JButton button = generateButton("Load File",
                "read_file",
                "Load selected file into memory (appends contents to transactions already found).");

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
        JButton button = generateButton("Clear Records",
                "clear_record",
                "Clear memory. Warning: will clear persistence file.");

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

    /**
     * Generate button for selecting transactions table
     * @param state state, or null if no state desired
     * @return button for table view selection
     */
    public static JButton generateTableTransactionButton(StateManager state){
        JButton button = generateButton("Transactions",
                "table_transaction",
                "View all successful transactions.");

        if (state != null) {
            state.registerTableTransactionButton(button);
            button.addActionListener(state);
        }

        return button;
    }

    /**
     * Generate button for selecting failed transactions table
     * @param state state, or null if no state desired
     * @return button for table view selection
     */
    public static JButton generateTableFailedTransactionButton(StateManager state){
        JButton button = generateButton("Failed Transactions",
                "table_failures",
                "View the failed transactions (includes both bad parse and logic error).");

        if (state != null) {
            state.registerTableFailuresButton(button);
            button.addActionListener(state);
        }

        return button;
    }

    /**
     * Generate button for selecting accounts table
     * @param state state, or null if no state desired
     * @return button for table view selection
     */
    public static JButton generateTableAccountButton(StateManager state){
        JButton button = generateButton("Accounts",
                "table_account",
                "View all accounts.");

        if (state != null) {
            state.registerTableAccountButton(button);
            button.addActionListener(state);
        }

        return button;
    }

    /**
     * Generate button for selecting failed audit cards table
     * @param state state, or null if no state desired
     * @return button for table view selection
     */
    public static JButton generateTableBadCardButton(StateManager state){
        JButton button = generateButton("Accounts Failing Audit",
                "table_audit",
                "View cards failing audit (those with negative balances).");

        if (state != null) {
            state.registerTableAuditButton(button);
            button.addActionListener(state);
        }

        return button;
    }

    /**
     * Generate button with parameter settings
     * @param text String to display in button
     * @param actionCommand String to serve as action command when action event occurs
     * @param tooltip String to display as tooltip
     * @return new button object
     */
    public static JButton generateButton(String text, String actionCommand, String tooltip){
        JButton button = new JButton(text);
        button.setActionCommand(actionCommand);
        button.setToolTipText(tooltip);

        return button;
    }
}
