package DataManager;

import Datatypes.Transaction;
import GUI.ScrollableTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
    public void registerOpenButton(JButton button) { openButton = button; }
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
    }


    // table functionality ------------------------------

    // Default title set
    ArrayList<String> defaultColumnNames = new ArrayList<>(Arrays.asList(
            "Account Name", "Card Number", "Amount", "Type", "Description", "Target Card"));

    /**
     * Update table records from database
     */
    private void resetTableRecord(){
        updateTableRecord(db.records);
    }

    /**
     * Update table records to specified array list
     * @param list list of transaction elements to enter as data
     */
    private void updateTableRecord(ArrayList<Transaction> list){
        if (scrollableTable != null){
            scrollableTable.updateData(list, defaultColumnNames);
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
}