import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

// class for managing state of program
public class StateManager implements ActionListener {
    private Database db;
    private JFrame gui;

    public StateManager(Database db, JFrame gui){
        this.db = db;
        this.gui = gui;
    }

    // update display text
    public String message = "Editor ready.";
    public void updateMessage(String text){}
    public String getMessage(){return message;}
    public void setMessage(String message){
        this.message = message;
        if (label != null){
            label.setText(message);
            gui.repaint();
        }

    }

    private File selectedFile = null;
    public void updateSelectedFile(String text){
        selectedFile = new File(text);

        // check validity
        if(openButton != null) {
            openButton.setEnabled(selectedFile.isFile());
        }
    }


    // used to manage button activity
    JButton openButton = null;
    public void registerOpenButton(JButton button) {
        openButton = button;
    }

    JButton clearButton = null;
    public void registerClearButton(JButton button) {
        clearButton = button;
        if (db.hasRecords()){
            if(clearButton != null) clearButton.setEnabled(true);
        }
    }

    public boolean hasFile(){
        if (selectedFile == null) return false;
        return selectedFile.isFile();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // file selection button
        if(e.getActionCommand().equals("select_file")){
            // open file dialogue
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            int val = fc.showOpenDialog(gui);
            if (val == JFileChooser.APPROVE_OPTION){
                selectedFile = fc.getSelectedFile();
                // refresh load capacity
                if(openButton != null) {
                    openButton.setEnabled(selectedFile.isFile());
                }

                setMessage("File selected:" + selectedFile.toString());
            }
        }

        // file open button
        if(e.getActionCommand().equals("read_file")){
            // read to db
            if(selectedFile.isFile()) {
                if (db.readFromFile(selectedFile.toString())) {
                    // write data
                    db.save();

                    // update table
                    resetTableRecord();

                    // revoke file opening
                    setMessage("Read from file '" + selectedFile + "'");
                    if (openButton != null) openButton.setEnabled(false);

                    // permit erasure
                    if (db.hasRecords()) {
                        if (clearButton != null) clearButton.setEnabled(true);
                    }
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

    JLabel label = null;
    public void registerButtonLabel(JLabel label) {
        this.label = label;
        if (label != null) this.label.setText(message);
    }

    ScrollableTable scrollableTable = null;
    public void registerTablePanel(ScrollableTable t){
        this.scrollableTable = t;
        if (scrollableTable != null){
            resetTableRecord();
        }
    }

    private void resetTableRecord(){
        updateTableRecord(db.records);
    }
    private void updateTableRecord(ArrayList<Transaction> list){
        if (scrollableTable != null){
            scrollableTable.updateData(list);
            gui.repaint();
        }
    }
}
