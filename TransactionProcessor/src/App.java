import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        fileReader reader = new fileReader();
        AccountManager manager = new AccountManager();

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nMenu:");
            System.out.println("1. Process Transaction File");
            System.out.println("2. Generate Reports");
            System.out.println("3. Reset System");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear the newline after nextInt()

                switch (choice) {
                    case 1:
                        System.out.print("Enter Input File Name: ");
                        String fileName = scanner.nextLine(); // Ask user for file name
                        try {
                            List<AccountInfo> transactions = reader.readFileIn(fileName);
                            manager.processTransactions(transactions);
                            if(reader.fileRead){
                                System.out.println("File processed successfully!");
                            }
                            
                        } catch (IOException e) {
                            System.out.println("Error processing the file. Please try again.");
                        }
                        break;
                    case 2:
                        manager.generateReports();
                        break;
                    case 3:
                        manager.resetSystem();
                        System.out.println("System has been reset.");
                        break;
                    case 4:
                        exit = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
        scanner.close();
    }
}
