import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

        }

        // file clear button
        if(e.getActionCommand().equals("clear_record")){

        }
    }

    JLabel label = null;
    public void registerButtonLabel(JLabel label) {
        this.label = label;
        if (label != null) this.label.setText(message);
    }
}
