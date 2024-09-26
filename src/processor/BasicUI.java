package processor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class BasicUI {

    public BasicUI() {
    	// Create frame and set defaults
        JFrame frame = new JFrame("Simple Transaction Processor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        
        // Create panel and border
        JPanel panel = new JPanel(new GridLayout());
        //panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(null);
        
        // Create label to ask the user to enter a csv file
        JLabel label = new JLabel("Please enter csv file name with transactions");
        label.setBounds(10, 20, 300, 25);
        label.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        panel.add(label);
        
        // Create field for csv file name
        JTextField transactionList = new JTextField(20);
        transactionList.setBounds(50, 40, 165, 25);
        panel.add(transactionList);
        
        // Create button for processing file
        JButton button = new JButton("Enter");
        button.setBounds(80, 70, 100, 30);
        
        button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
        
        panel.add(button);
        
        // Create button to wipe data
        JButton button2 = new JButton("Clear");
        button2.setBounds(80, 105, 100, 30);
        button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
        panel.add(button2);

        
        // Size the frame 
        frame.add(panel, BorderLayout.CENTER);
        //frame.pack();
        // Make the frame visible
        frame.setVisible(true);
        // Center frame
        frame.setLocationRelativeTo(null);
    }

}