package sgp.transactionprocessor;

import java.util.*;
import java.io.*;

public class Main 
{
        public static void main( String[] args )
        {  
            System.out.print("Enter the full path of the file to be processed:");
            Scanner readFile = new Scanner(System.in); // get the file path from the user

            //String filePath = "/Users/tanaypatel/Desktop/transaction/src/main/java/sgp/transactionprocessor/data.csv";
            String filePath = readFile.nextLine();

            File file = new File(filePath);

            while(!(file.exists())) // prompt user until a valid file is provided
            {   
                System.out.print("File not found, please provide a valid file address:");
                readFile = new Scanner(System.in);
                filePath = readFile.nextLine();
                file = new File(filePath);
            }

            readFile.close();

            // creating an arraylist (could have created an array but chose arraylist as it is more understandable by the examiner)
            ArrayList<Transaction> transactions = new ArrayList<>(); 

            ArrayList<FaultyTrans> faultyTransactions = new ArrayList<>();

            String accName = "", transType = "", desc = "";
            long cardNo = 0, trgNo = 0;
            double transAmount = 0.0;

            String[] values = {""};

            try (Scanner reader = new Scanner(file)) {
                while (reader.hasNextLine()) {
                    values = reader.nextLine().split(","); // comma seperated transaction in the csv file

                    for (int i = 0; i < values.length; i++) {
                        accName = values[0];
                        cardNo = Long.parseLong(values[1]); // parse from string to number
                        transAmount = Double.parseDouble(values[2]);
                        transType = values[3];
                        desc = values[4];
                        if(values.length > 5) // only a few will have the 6th column being transfer card no.
                            trgNo = Long.parseLong(values[5]);
                    }

                    // add the transaction in the transactions arraylist
                    transactions.add(new Transaction(accName, cardNo, transAmount, transType, desc, trgNo));
                }

            } catch (FileNotFoundException e) {
                System.out.println("File not found. Program terminated.");
                System.exit(0);
                e.printStackTrace();
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                // Any transaction having a typo would be recorded in the faultyTransaction arraylist
                faultyTransactions.add(new FaultyTrans(values[0], values[1], values[2], values[3], values[4], ""));
            }

            Collections.sort(transactions, new NameComparator()); // sort the transactions by AccountName

            // Process Transactions:

            boolean flag = true;

            int j = 0;

            System.out.println();
            System.out.println();

            try {
                // will also be writing to a file names Reporting.txt, along with displaying in terminal

                FileWriter fileWriter = new FileWriter("Reporting.txt");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    
                // formatting output to display more aesthetically / in a tabular like form
                String header = String.format("%-20s %-20s %-15s\n", "Account Name", "Cards No.", "Transaction Amount ");
                String separator = "--------------------------------------------------------------------";
                
                System.out.print(header);
                System.out.println(separator);
    
                bufferedWriter.write(header);
                bufferedWriter.write(separator);
                bufferedWriter.newLine();
    
                while (j < (transactions.size() - 1)) // until the end of transactions arraylist
                {
                    if (flag) // using a flag to display the Account Name only once
                    {
                        String accountLine = String.format("%-20s %-20s %-15s\n", transactions.get(j).accountName, "", "");
                        System.out.print(accountLine);
                        bufferedWriter.write(accountLine);
                        bufferedWriter.newLine();
                    }
    
                    // if the next transaction has the same account name, keep outputting the card no. and amount
                    if ((transactions.get(j).accountName).equals(transactions.get(j + 1).accountName)) 
                    {
                        flag = false;

                        if (transactions.get(j).transactionAmount > 0) 
                        {
                            String transactionLine = String.format("%-20s %-20s %-15.2f\n", "", transactions.get(j).cardNo, transactions.get(j).transactionAmount);
                            System.out.print(transactionLine);
                            bufferedWriter.write(transactionLine);
                        }
                    } 

                    else // else set flag to true, and go to next Account Name details
                    {
                        if (transactions.get(j).transactionAmount > 0) 
                        {
                            String transactionLine = String.format("%-20s %-20s %-15.2f\n", "", transactions.get(j).cardNo, transactions.get(j).transactionAmount);
                            System.out.print(transactionLine);
                            bufferedWriter.write(transactionLine);
                        }

                        flag = true;
                    }
    
                    j++; // loop counter
                }

                // displaying the last transaction seperately due to chosen logic style
    
                if (transactions.get(transactions.size() - 1).transactionAmount > 0) 
                {
                    // if last transaction has the same AccountName for the second last one - 
                    if((transactions.get(transactions.size() - 1).accountName).equals(transactions.get(transactions.size() - 2).accountName))
                    {
                    String lastTransaction = String.format("%-20s %-20s %-15.2f\n", "", transactions.get(transactions.size() - 1).cardNo, transactions.get(transactions.size() - 1).transactionAmount);
                    System.out.print(lastTransaction);
                    bufferedWriter.write(lastTransaction);
                    }
                    else // else it has a different AccountName -
                    {
                        String accountLine = String.format("%-20s %-20s %-15s\n", transactions.get(transactions.size() - 1).accountName, "", "");
                        System.out.print(accountLine);
                        bufferedWriter.write(accountLine);
                        bufferedWriter.newLine();
                        String lastTransaction = String.format("%-20s %-20s %-15.2f\n", "", transactions.get(transactions.size() - 1).cardNo, transactions.get(transactions.size() - 1).transactionAmount);
                        System.out.print(lastTransaction);
                        bufferedWriter.write(lastTransaction);
                    }
                }
    
                j = 0;
    
                header = "\nList of Cards with negative amounts (Need to be submitted to collections):";
                separator = "-----------------------------------------------------------------";
                String tableHeader = String.format("%-20s %-20s %-15s\n", "Account Name", "Card No", "Transaction Amount");
    
                System.out.println(header);
                System.out.println();
                System.out.print(tableHeader);
                System.out.println(separator);
    
                // format for the reporting.txt file

                bufferedWriter.newLine();
                bufferedWriter.write(header);
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write(tableHeader);
                bufferedWriter.newLine();
                bufferedWriter.write(separator);
                bufferedWriter.newLine();
                
    
                while (j < transactions.size()) 
                {
                    if (transactions.get(j).transactionAmount < 0) // if amount is less than 0
                    {
                        String negAmtTransaction = String.format("%-20s %-20s %-15.2f\n", transactions.get(j).accountName, transactions.get(j).cardNo, transactions.get(j).transactionAmount);
                        System.out.print(negAmtTransaction);
                        bufferedWriter.write(negAmtTransaction);
                    }

                    j++;
                }
    
                header = "\nList of Bad Transactions:";
                separator = "-----------------------------------------------------------------";
                tableHeader = String.format("%-20s %-20s %-15s\n", "Account Name", "Card No", "Transaction Amount");
    
                System.out.println(header);
                System.out.println();
                System.out.print(tableHeader);
                System.out.println(separator);
    
                bufferedWriter.newLine();
                bufferedWriter.write(header);
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write(tableHeader);
                bufferedWriter.newLine();
                bufferedWriter.write(separator);
                bufferedWriter.newLine();
                
               // display and write the faulty transactions to the reporting.txt file
                for (int i = 0; i < faultyTransactions.size(); i++) 
                {
                    String faultyTransaction = String.format("%-20s %-20s %-15s\n", faultyTransactions.get(i).accountName, faultyTransactions.get(i).cardNo, faultyTransactions.get(i).transactionAmount);
                    System.out.print(faultyTransaction);
                    bufferedWriter.write(faultyTransaction);
                }
    
                bufferedWriter.close();
            } 

            catch (IOException e) 
            {
                System.out.println("Error occured while writing to the file: " + e.getMessage());
            }
        }
    }

    // Complexity Analysis:

    // Cyclomatic Complexity: 
    //          no. of loops used for construction - 1 for storing all transactions in the ArrayList
    //          no. of loops used for transactions processing - 3 (used more no. of loops to minimize time complexity)

    // Time Complexity:
    //          Time Complexity would be Loglinear : let's say N is the no. of transactions in the arraylist, then
    //          N + 2N (checking ith and i+1 th trans. for processing) + N log N (sort) + N (searching for negative amts.) =
    //          4N + N log N ~ O(N log N).

    // Space Complexity:
    //          no. of data structures used - 1 Arraylist for all the transactions which is large, and
    //                                       1 Arraylist for faulty transactions which is of very small size
