package frameNavigator;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class SendToCollectionsFrame extends JFrame{
    private List<String> result = new ArrayList<>();
    private JLabel confirmation = new JLabel();
    public SendToCollectionsFrame(Launcher launcher){
        setTitle("Send To Collections View");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(addNorthPanel(), BorderLayout.NORTH);
        add(addCenterPanel(), BorderLayout.CENTER);
        add(addWestPanel(launcher), BorderLayout.WEST);
    }
    private JPanel addWestPanel(Launcher launcher){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(addBackButton(launcher));
        panel.add(addSaveCollectionsButton(launcher));
        confirmation.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        panel.add(confirmation);
        return panel;
    }
    private JButton addSaveCollectionsButton(Launcher launcher){
        JButton button = new JButton("Output to collections.csv");
        button.setForeground(Color.GREEN);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Files.write(Paths.get("output/collections.csv"), result);
                    confirmation.setForeground(Color.GREEN);
                    confirmation.setText("SAVED");
                    new Timer(3000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            confirmation.setText("");
                        }
                    }).start();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        return button;
    }
    private JButton addBackButton(Launcher launcher){
        JButton button = new JButton("Back");
        button.setForeground(Color.RED);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launcher.showReportNavigatorFrame();
            }
        });
        return button;
    }
    private JScrollPane addCenterPanel(){
        return new JScrollPane(addSummaryTable());
    }
    private JPanel addNorthPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(addNorthLabel());
        return panel;
    }
    private JLabel addNorthLabel(){
        JLabel label = new JLabel("Send To Collections Viewer");
        label.setFont(new Font("Sans-serif", Font.BOLD, 18));
        return label;
    }
    private JTable addSummaryTable(){
        String[][] data = getTableData();
        String[] columnNames = {"Index","Account Name", "Card Number", "Amount"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.setGridColor(Color.GRAY);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        return table;
    }

    private String[][] getTableData(){
        String[][] tableEntries = new String[0][0];
        try{
            List<String> lines = Files.readAllLines(Paths.get("history/holder.csv"));
            HashMap<String, Double> values = extractionAlgorithm(lines);
            List<String> sortedKeys = removePositiveValues(values);
            Collections.sort(sortedKeys);
            tableEntries = new String[sortedKeys.size()][4];
            for(int i = 0; i<sortedKeys.size(); i++){
                String entryKey = sortedKeys.get(i);
                Double amount = values.get(entryKey);
                StringTokenizer tokenizer = new StringTokenizer(entryKey, "|");
                String name = tokenizer.nextToken();
                String cardNumber = tokenizer.nextToken();
                tableEntries[i][0] = String.valueOf(i);
                tableEntries[i][1] = name;
                tableEntries[i][2] = cardNumber;
                tableEntries[i][3] = String.format("%.2f", amount);
                result.add(name+", "+cardNumber+", "+amount);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return tableEntries;
    }
    private List<String> removePositiveValues(HashMap<String, Double> entries){
        Iterator<String> it = entries.keySet().iterator();
        List<String> negativeKeys = new ArrayList<>();
        while(it.hasNext()){
            String key = it.next();
            double val = entries.get(key);
            if(val < 0 ) negativeKeys.add(key);
        }
        return negativeKeys;
    }
    private HashMap<String, Double> extractionAlgorithm(List<String> entries){
        HashMap<String, Double> result = new HashMap<>();
        for(String entry : entries){
            StringTokenizer tokenizer = new StringTokenizer(entry, ",");
            String name = tokenizer.nextToken().trim();
            String cardNumber = tokenizer.nextToken().trim();
            String transactionAmount = tokenizer.nextToken().trim();
            logEntry(result, name+"|"+cardNumber, Double.parseDouble(transactionAmount));
        }
        return result;
    }
    private void logEntry(HashMap<String, Double> entryLog, String key, Double amount){
        boolean exists = entryLog.containsKey(key);
        if(exists){
            Double temp = entryLog.get(key);
            entryLog.replace(key, temp+amount);
        }
        else{
            entryLog.put(key, amount);
        }
    }

}
