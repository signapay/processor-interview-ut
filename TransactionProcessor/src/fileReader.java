import java.util.*;
import java.io.*;

public class fileReader {
    boolean fileRead = true;
    
    public List<AccountInfo> readFileIn(String fileName) throws IOException{
        List<AccountInfo> data = new ArrayList<>();
        
        //System.out.println("You entered: " + fileName); to make sure the correct file is being read in
        //String fileName = "C:\\Users\\Josh\\OneDrive - The University of Texas at Dallas\\Desktop\\coding projects\\SignaPayOA\\sgpOA\\src\\smallsample.csv";
        //System.out.println("Current working directory: " + System.getProperty("user.dir"));
        
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");  // Split each line into fields
                
                // Validate the number of fields
                if (fields.length < 5 || fields.length > 6) {
                    System.out.println("Invalid line: \"" + line + "\" - Incorrect number of fields.");
                    continue; // Skip this line
                }

                String targetCardNumber = (fields.length == 6) ? fields[5].trim() : null;
                String accountName = fields[0].trim();
                String cardNumber = fields[1].trim();
                double transactionAmount;

                // Parse the transaction amount with error handling
                try {
                    transactionAmount = Double.parseDouble(fields[2].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid transaction amount in line: \"" + line + "\" - " + e.getMessage());
                    fileRead = false;
                    continue; // Skip this line
                }

                String transactionType = fields[3].trim();
                String description = fields[4].trim();

                // Create and add the Transactions object
                AccountInfo transaction = new AccountInfo(accountName, cardNumber, transactionAmount, transactionType, description, targetCardNumber);
                data.add(transaction);
                fileRead=true;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Please try again." + fileName);
            fileRead = false;
        
        }catch (IOException e) {
            System.out.println("An error occurred. Please try again.");
            fileRead = false;
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid data format. Please try again.");
            fileRead = false;
        }
            /*
            System.out.println("Data read in: ");
            for(String[] s : data) {
                System.out.println(Arrays.toString(s));
            }
            */
            
          
    
    return data;
}
public boolean getReadFile(){
    return fileRead;
}

}

