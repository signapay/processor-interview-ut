package frameNavigator;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.io.IOException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CSVSelectorFrame extends JFrame {
    private List<String> gatherFiles;
    private boolean removeDuplicatesSelected = false;
    private JLabel confirmationLabel = new JLabel();
    public CSVSelectorFrame(Launcher launcher) {
        setTitle("CSV Selection Screen");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(addNorthPanel(launcher), BorderLayout.NORTH);
        add(addCenterPanel(launcher), BorderLayout.CENTER);
        add(addWestPanel(launcher), BorderLayout.WEST);
    }
    private JPanel addCenterPanel(Launcher launcher){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(addViewReportButton(launcher));
        addCheckBoxSection(panel);
        confirmationLabel.setForeground(new Color(50,200,50));
        panel.add(confirmationLabel);
        return panel;
    }
    private void addCheckBoxSection(JPanel panel){
        JLabel label = new JLabel("(Optional) Add the following files to Report: ");
        label.setFont(new Font("Sans-serif", Font.BOLD, 18));
        panel.add(label);
        List<String> csvFileNames = getCSVs();
        List<JCheckBox> checkBoxes = new ArrayList<>();
        gatherFiles = new ArrayList<>();
        for(String csvFile : csvFileNames){
            JCheckBox checkBox = new JCheckBox(csvFile);
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }
        JPanel addAddToReportPanel = new JPanel(new FlowLayout());
        JButton addToReportButton = new JButton("Add to Report");
        JCheckBox removeDuplicatesCheckBox = new JCheckBox("Remove Duplicate Entries");
        addToReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringBuilder sb = new StringBuilder();
                for(JCheckBox cb : checkBoxes){
                    if(cb.isSelected()){
                        String cbSelected = cb.getText();
                        gatherFiles.add("csvCatalog/" +cbSelected);
                        sb.append(cbSelected+", ");
                    }
                }
                String message = "Files: "+sb.toString()+" have been added to the Report";
                confirmationLabel.setText(message);
                new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        confirmationLabel.setText(""); // Clear the label
                    }
                }).start();
                removeDuplicatesSelected = removeDuplicatesCheckBox.isSelected();
            }

        });
        addAddToReportPanel.add(addToReportButton);
        addAddToReportPanel.add(removeDuplicatesCheckBox);
        addAddToReportPanel.setBackground(new Color(255, 255, 255));
        panel.add(addAddToReportPanel);
        panel.setBackground(new Color(255, 255, 255));
    }
    private void updateHolderFile(List<String> gatherFiles, boolean removeDuplicatesSelected) throws IOException {
        String historyPath = "history/history.csv";
        HashSet<String> entries = new HashSet<>();
        List<String> errors = new ArrayList<>();
        parseFile(historyPath, removeDuplicatesSelected, entries, errors);
        for(String file : gatherFiles){
            parseFile(file, removeDuplicatesSelected, entries, errors);
        }
        List<String> entriesList = List.of(entries.toArray(new String[0]));
        try{
            Files.write(Paths.get("history/holder.csv"), entriesList);
        }catch (IOException e) {
        e.printStackTrace();
        }
        try {
            Files.write(Paths.get("history/transactionErrors.csv"), errors);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
    private void parseFile(String filePath, boolean removeDuplicatesSelected, HashSet<String> entries, List<String> errors){
        try{
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for(String line : lines ){
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                String name = "", cardNumber = "", transactionAmount = "", type = "", description = "", targetCardNumber = "";
                int i = 0;
                while(tokenizer.hasMoreTokens()) {
                    if (i == 0) name = tokenizer.nextToken().trim();
                    else if (i == 1) cardNumber = tokenizer.nextToken().trim();
                    else if (i == 2) transactionAmount = tokenizer.nextToken().trim();
                    else if (i == 3) type = tokenizer.nextToken().trim();
                    else if (i == 4) description = tokenizer.nextToken().trim();
                    else if (i == 6) targetCardNumber = tokenizer.nextToken().trim();
                    i++;
                }
                String validFormat = validityChecker(name, cardNumber, transactionAmount, type, description, targetCardNumber);
                String entry = name + "," + cardNumber + "," + transactionAmount + "," + type + "," + description + "," + targetCardNumber;
                if (validFormat.equalsIgnoreCase("PASS")) {
                    if (removeDuplicatesSelected) {
                        if (!entries.contains(entry)) {
                            entries.add(entry);
                        }
                    }
                    else entries.add(entry);
                }
                else errors.add(validFormat + ", " + entry);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    private enum TransactionEnum {CREDIT, DEBIT, TRANSFER}
    private boolean inTransactionType(String input){
        for(TransactionEnum value: TransactionEnum.values()){
            if(value.name().equalsIgnoreCase(input)) return true;
        }
        return false;
    }
    private String validityChecker(String name,String cardNumber,String transactionAmount,String type,String description,String targetCardNumber){
        if(name.length() == 0) return "Account Name Invalid";
        if(cardNumber.length() ==0) return "Card Number Invalid";
        try {
            Long.parseLong(cardNumber);
        } catch (NumberFormatException e) {
            return "Card Number Value contains non-numerical values";
        }
        if(transactionAmount.length()==0) return "Transaction Amount Invalid";
        try {
            Double.parseDouble(transactionAmount);
        }
        catch (NumberFormatException e){
            return "Transaction Amount Value contains non-numerical values";
        }
        if(!inTransactionType(type)) return "Transaction Type Invalid";
        if(targetCardNumber.length()>0){
            try {
                Long.parseLong(targetCardNumber);
            } catch (NumberFormatException e) {
                return "Target Card Number Value contains non-numerical values";
            }
            if(!type.equalsIgnoreCase("TRANSFER")) return "Transaction Type does not allow Target Card Number";
        }
        return "PASS";
    }
    private JButton addViewReportButton(Launcher launcher){
        JButton viewReportButton = new JButton("View Last Saved Report");
        viewReportButton.setForeground(new Color(0,125,255));
        viewReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    updateHolderFile(gatherFiles, removeDuplicatesSelected);
                    gatherFiles.clear();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                launcher.showReportNavigatorFrame();
            }
        });
        return viewReportButton;
    }
    private List<String> getCSVs() {
        String directoryPath = "csvCatalog/";
        List<String> csvFileNames = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath), "*.csv")) {
            for (Path path : directoryStream) {
                csvFileNames.add(path.getFileName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvFileNames;
    }
    private JPanel addWestPanel(Launcher launcher){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.add(addBackButton(launcher));
        return panel;
    }
    private JPanel addNorthPanel(Launcher launcher){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.add(addNorthLabel());
        panel.setBackground(new Color(150, 150, 150));
        return panel;
    }

    private JLabel addNorthLabel(){
        JLabel label = new JLabel("Report Summarizer");
        label.setFont(new Font("Sans-serif", Font.BOLD, 36));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton addBackButton(Launcher launcher){
        JButton button = new JButton("Back");
        button.setForeground(Color.RED);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launcher.showHomeFrame();
            }
        });
        return button;
    }
}
