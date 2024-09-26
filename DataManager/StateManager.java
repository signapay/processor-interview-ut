package DataManager;

import Datatypes.Account;
import Datatypes.Card;
import Datatypes.Transaction;
import GUI.ScrollableTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Class for managing state of program and gui window
 */
public class StateManager implements ActionListener {
    private final DataManager db;
    private final JFrame gui;

    /**
     * Initialize state constructor
     * @param db database object for use with program
     * @param gui gui object spawning state manager (used to invoke redraw)
     */
    public StateManager(DataManager db, JFrame gui){
        this.db = db;
        this.gui = gui;
    }

    // display label ------------------------------

    /**
     * Update button display label text
     * @param message string to use
     */
    public void setMessage(String message){
        if (message == null) message = "";  // prevent null assignment
        this.message = message;
        // if label object, refresh label text and redraw
        if (label != null){
            label.setText(message);
            gui.repaint();
        }
    }
    public String message = "Editor ready.";

    /**
     * register button label text
     * @param label JLabel to register
     */
    public void registerButtonLabel(JLabel label) {
        this.label = label;
        if (label != null) this.label.setText(message);
    }
    JLabel label = null;

    // button registration ------------------------------
    /**
     * Register file opening button
     * @param button button to register
     */
    public void registerOpenButton(JButton button) { openButton = button; button.setEnabled(hasFile());}
    private JButton openButton = null;

    /**
     * Register button for clearing files
     * @param button Button to register
     */
    public void registerClearButton(JButton button) {
        clearButton = button;
        if (db.hasRecords()){
            if(clearButton != null) clearButton.setEnabled(true);
        }
    }
    private JButton clearButton = null;

    public void registerTableTransactionButton(JButton button) {
        tabletransactionButton = button;
    }
    private JButton tabletransactionButton = null;

    public void registerTableFailuresButton(JButton button) {
        tableFailuresButton = button;
    }
    private JButton tableFailuresButton = null;

    public void registerTableAccountButton(JButton button) {
        tableAccountButton = button;
    }
    private JButton tableAccountButton = null;

    public void registerTableAuditButton(JButton button) {
        tableAuditButton = button;
    }
    private JButton tableAuditButton = null;

    // button functionality ------------------------------
    private File selectedFile = null;

    /**
     * Update selected file
     * @param text file to select
     */
    public void updateSelectedFile(File text){
        selectedFile = text;  // select file

        // check validity before enabling
        if(openButton != null && selectedFile.isFile()) {
            openButton.setEnabled(selectedFile.isFile());
        }

        // alter message
        setMessage("File selected:" + selectedFile.toString());
    }

    public boolean hasFile(){
        if (selectedFile == null) return false;
        return selectedFile.isFile();
    }

    /**
     * Listen for action event, react appropriately
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // file selection button
        if(e.getActionCommand().equals("select_file")){
            // open file dialogue
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            int val = fc.showOpenDialog(gui);
            if (val == JFileChooser.APPROVE_OPTION){
                updateSelectedFile(fc.getSelectedFile());   // select file
                selectedFile = fc.getSelectedFile();
                // read to db
                if (db.readFromFile(selectedFile)) {
                    // write data
                    db.save();

                    // update table
                    resetTableRecord();

                    // revoke file opening ability
                    setMessage("Read from file '" + selectedFile + "'");
                    if (openButton != null) openButton.setEnabled(false);

                    // permit erasure
                    if (db.hasRecords()) {
                        if (clearButton != null) clearButton.setEnabled(true);
                    }
                }
            }
        }

        // file open button
        if(e.getActionCommand().equals("read_file")){
            // read to db
            if (db.readFromFile(selectedFile)) {
                // write data
                db.save();

                // update table
                resetTableRecord();

                // revoke file opening ability
                setMessage("Read from file '" + selectedFile + "'");
                if (openButton != null) openButton.setEnabled(false);

                // permit erasure
                if (db.hasRecords()) {
                    if (clearButton != null) clearButton.setEnabled(true);
                }
            }
        }

        // file clear button
        if(e.getActionCommand().equals("clear_record")){
            if (db.hasRecords()){
                db.clearRecords();
                resetTableRecord();
                if(clearButton != null) clearButton.setEnabled(false);
                setMessage("Records cleared.");
            }
        }

        // table transaction button
        if (e.getActionCommand().equals("table_transaction")){
            resetTableRecord();
        }

        // table account button
        if (e.getActionCommand().equals("table_account")){
            ArrayList<String[]> accounts = new ArrayList<>();
            // for each account, parse into text grid
            for (Account account: db.getAllAccounts()){
                String name = account.getName();
                for(Card card: account.getCardSetCopy()){
                    accounts.add(new String[]{name, Long.toString(card.cardNumber()), card.getBalance().toString()});
                    if (!name.equals("")) name = "";    // revoke name after first
                }
            }

            // call table update
            updateTableRecord(accounts, accountNames);
        }

        // table fail button
        if (e.getActionCommand().equals("table_failures")){
            ArrayList<String[]> records = new ArrayList<>();
            for (String string: db.getFailedTransactionList()){records.add(new String[]{string});}
            updateTableRecord(records, failedTransactionNames);
        }

        // table audit button
        if (e.getActionCommand().equals("table_audit")){
            ArrayList<String[]> records = new ArrayList<>();
            for (Card card: db.getBadCards()){
                records.add(new String[]{card.getName(), Long.toString(card.cardNumber()), card.getBalance().toString()});
            }
            updateTableRecord(records, auditFailNames);
        }
    }


    // table functionality ------------------------------

    // Default title sets
    String[] defaultColumnNames = {"Account Name", "Card Number", "Amount", "Type", "Description", "Target Card"};
    String[] accountNames = {"Account Holder", "Card Number", "Card Balance"};
    String[] auditFailNames = {"Account Name", "Card number", "Card Balance"};
    String[] failedTransactionNames = {"Failed Transactions"};

    /**
     * Update table records from database
     */
    private void resetTableRecord(){
        ArrayList<String[]> records = new ArrayList<>();
        for (Transaction transaction : db.records){
            if (transaction.getTransactionType().equals("Transfer")){
                records.add(new String[]{transaction.getAccountName(), Long.toString(transaction.getCardNumber()),
                        transaction.getTransactionAmount().toString(), transaction.getTransactionType(),
                        transaction.getDescription(), Long.toString(transaction.getTargetCardNumber())});
            }
            else{
                records.add(new String[]{transaction.getAccountName(), Long.toString(transaction.getCardNumber()),
                        transaction.getTransactionAmount().toString(), transaction.getTransactionType(),
                        transaction.getDescription(), "N/A"});
            }
        }
        updateTableRecord(records, defaultColumnNames);
    }

    private void updateTableRecord(ArrayList<String[]> list, String[] names){
        if (scrollableTable != null){
            scrollableTable.updateData(list, names);
            //gui.revalidate();
            gui.repaint();
        }
    }



    /**
     * Register scroll table object for data control purposes
     * @param table scrollable table object
     */
    public void registerTablePanel(ScrollableTable table){
        this.scrollableTable = table;
        if (scrollableTable != null){
            resetTableRecord();
        }
    }
    ScrollableTable scrollableTable = null;

    // generate table buttons
}
