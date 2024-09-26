package frameNavigator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ReportNavigatorFrame extends JFrame {

    protected ReportNavigatorFrame(Launcher launcher){
        setTitle("View Report");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(addHeaderPanel(), BorderLayout.NORTH);
        add(addButtonPanel(launcher), BorderLayout.CENTER);
        add(addWestPanel(launcher), BorderLayout.WEST);
    }
    private JPanel addWestPanel(Launcher launcher){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.add(addBackButton(launcher));
        return panel;
    }
    private JButton addBackButton(Launcher launcher){
        JButton button = new JButton("Back");
        button.setForeground(Color.RED);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launcher.eraseFile("history/transactionErrors.csv");
                launcher.eraseFile("history/holder.csv");
                launcher.showCSVSelectorFrame();
            }
        });
        return button;
    }
    private JPanel addHeaderPanel(){
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel label = new JLabel("Report Summary");
        label.setFont(new Font("Sans-serif", Font.BOLD, 36));
        label.setForeground(Color.WHITE);
        panel.add(label);
        panel.setBackground(new Color(150, 150, 150));
        return panel;
    }
    private JPanel addButtonPanel(Launcher launcher){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(addViewSummaryButton(launcher));
        panel.add(sendToCollectionsListButton(launcher));
        panel.add(addBadTransactionsButton(launcher));
        panel.add(addSaveCurrentReportButton());
        panel.setBackground(new Color(255, 255, 255));
        return panel;
    }
    private JButton addViewSummaryButton(Launcher launcher){
        JButton viewSummaryButton = new JButton("View Summary");
        viewSummaryButton.setPreferredSize(new Dimension(200, 75));
        viewSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                launcher.showViewSummaryFrame();
            }
        });
        return viewSummaryButton;
    }
    private JButton sendToCollectionsListButton(Launcher launcher){
        JButton sendToCollectionsButton = new JButton("Send to Collections");
        sendToCollectionsButton.setPreferredSize(new Dimension(200, 75));
        sendToCollectionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                launcher.showSendToCollectionsFrame();
            }
        });
        return sendToCollectionsButton;
    }
    private JButton addBadTransactionsButton(Launcher launcher){
        JButton badTransactionsButton = new JButton("Bad Transactions List");
        badTransactionsButton.setPreferredSize(new Dimension(200, 75));
        badTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                launcher.showBadTransactionsFrame();
            }
        });
        return badTransactionsButton;
    }
    private JButton addSaveCurrentReportButton(){
        JButton saveCurrentReportButton = new JButton("Add Report to Saved History");
        saveCurrentReportButton.setPreferredSize(new Dimension(200, 75));
        saveCurrentReportButton.setForeground(new Color(50,200,50));
        saveCurrentReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    List<String> lines = Files.readAllLines(Paths.get("history/holder.csv"));
                    Files.write(Paths.get("history/history.csv"), lines, StandardOpenOption.APPEND);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    List<String> lines = Files.readAllLines(Paths.get("history/transactionErrors.csv"));
                    Files.write(Paths.get("history/errorHistory.csv"), lines, StandardOpenOption.APPEND);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return saveCurrentReportButton;
    }
}
