package frameNavigator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeFrame extends JFrame {
    private JLabel confirmationLabel;
    protected HomeFrame(Launcher launcher) {
        setTitle("Home");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(addHeaderPanel(launcher), BorderLayout.NORTH);
        add(addButtonPanel(launcher), BorderLayout.CENTER);
        add(addBottomPanel(launcher), BorderLayout.SOUTH);
    }
    private JPanel addBottomPanel(Launcher launcher){
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(notifications());
        return panel;
    }
    private JLabel notifications(){
        confirmationLabel = new JLabel();
        confirmationLabel.setForeground(Color.RED);
        confirmationLabel.setHorizontalAlignment(JLabel.CENTER);
        return  confirmationLabel;
    }
    private JPanel addHeaderPanel(Launcher launcher){
        JPanel panel = new JPanel();
        panel.add(addLabel());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }
    private JLabel addLabel(){
        JLabel label = new JLabel("Account Summarizer");
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Sans-serif", Font.BOLD, 36));
        ImageIcon labelIcon = new ImageIcon("img/SPlogo.jpg");
        label.setIcon(labelIcon);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        return label;
    }
    private JPanel addButtonPanel(Launcher launcher){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.add(addContinueFromLastSaveButton(launcher));
        panel.add(addDeleteHistoryButton(launcher));
        return panel;
    }

    private JButton addContinueFromLastSaveButton(Launcher launcher){
        JButton button = new JButton("Continue From Last Save");
        button.setForeground(Color.BLUE);
        button.setPreferredSize(new Dimension(200, 75));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launcher.showCSVSelectorFrame();
            }
        });
        return button;
    }
    private JButton addDeleteHistoryButton(Launcher launcher){
        JButton button = new JButton("Delete History");
        button.setForeground(Color.RED);
        button.setPreferredSize(new Dimension(200, 75));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launcher.eraseFile("history/history.csv");
                launcher.eraseFile("history/transactionErrors.csv");
                launcher.eraseFile("history/holder.csv");
                launcher.eraseFile("history/errorHistory.csv");
                confirmationLabel.setText("History Deleted");
                new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        confirmationLabel.setText("");
                    }
                }).start();
            }
        });
        return button;
    }

}
