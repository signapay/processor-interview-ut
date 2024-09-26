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

public class BadTransactionsFrame extends JFrame{
    private List<String> result = new ArrayList<>();
    private JLabel confirmation = new JLabel();
    private List<String> combined = new ArrayList<>();
    public BadTransactionsFrame(Launcher launcher){
        setTitle("Bad Transactions View");
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
        panel.add(addSaveBadTransactionsButton(launcher));
        confirmation.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        panel.add(confirmation);
        return panel;
    }
    private JButton addSaveBadTransactionsButton(Launcher launcher){
        JButton button = new JButton("Output to badTransactions.csv");
        button.setForeground(Color.GREEN);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Files.write(Paths.get("output/badTransactions.csv"), result);
                    confirmation.setForeground(Color.GREEN);
                    confirmation.setText("Saved to badTransactions.csv");
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
        JLabel label = new JLabel("Bad Transactions Viewer");
        label.setFont(new Font("Sans-serif", Font.BOLD, 18));
        return label;
    }
    private JTable addSummaryTable(){
        String[][] data = getTableData();
        String[] columnNames = {"Index", "Diagnosis", "Account Name", "Card Number", "Amount", "Type", "Description", "Target Card Number"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        return table;
    }

    private String[][] getTableData(){
        getCombinedList();
        String[][] tableEntries = new String[combined.size()][8];
        for(int i = 0; i<combined.size(); i++){
            String entry = combined.get(i);
            int j = 0;
            String error = "", name = "", cardNumber = "", transactionAmount = "", type = "", description = "", targetCardNumber = "";
            StringTokenizer tokenizer = new StringTokenizer(entry, ",");
            while(tokenizer.hasMoreTokens()) {
                if (j == 0) error = tokenizer.nextToken().trim();
                else if (j == 1) name = tokenizer.nextToken().trim();
                else if (j == 2) cardNumber = tokenizer.nextToken().trim();
                else if (j == 3) transactionAmount = tokenizer.nextToken().trim();
                else if (j == 4) type = tokenizer.nextToken().trim();
                else if (j == 5) description = tokenizer.nextToken().trim();
                else if (j == 6) targetCardNumber = tokenizer.nextToken().trim();
                j++;
            }
            tableEntries[i][0] = String.valueOf(i);
            tableEntries[i][1] = error;
            tableEntries[i][2] = name;
            tableEntries[i][3] = cardNumber;
            tableEntries[i][4] = transactionAmount;
            tableEntries[i][5] = type;
            tableEntries[i][6] = description;
            tableEntries[i][7] = targetCardNumber;
            result.add(entry);
        }
        return tableEntries;
    }
    private void getCombinedList(){
        List<String> current = new ArrayList<>();
        try{
            combined =  Files.readAllLines(Paths.get("history/errorHistory.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            current =  Files.readAllLines(Paths.get("history/transactionErrors.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        combined.addAll(current);
    }

}
