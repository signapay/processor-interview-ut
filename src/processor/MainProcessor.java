package processor;

import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class which takes a csv file and processes the data into 3 reports which can be called by other classes.
 * 1. A HashMap representing a chart of accounts by Card number.
 * 2. A HashMap representing a list of accounts and cards which are negative and need to be given to collections.
 * 3. An ArrayList of bad transactions stored as ArrayLists.
 * @author Will
 *
 */
public class MainProcessor {

	private List<List<String>> data; 												// ArrayList for storing data from given CSV files for persistence
	private List<List<String>> failedTransactions = new ArrayList<>();;				// ArrayList for tracking bad transactions
	private HashMap<String, ArrayList<String>> chartedAccounts = new HashMap<>(); 	// Hashmap for storing sorted accounts
	private HashMap<String, ArrayList<String>> accountsForCollections = new HashMap<>(); // HashMap for accounts to be given to collections
	
	/**
	 * Takes a csvFile and adds it to the data ArrayList. Returns an int as confirmation.
	 * @param csvFile file to be read in comma separated values 
	 * @return Returns 1 if successful.
	 * @throws Exception if there is a failure reading the file
	 */
	public String readFile(File csvFile) throws Exception {
		data = new ArrayList<List<String>>();
		
		String line = "";
		
	    // Check to see if file exists
		if(!csvFile.exists())
			return "File does not exist.";
		// Check to see if the file is a csv
		else if (!getExtension(csvFile).equals("csv"))
			return "File is not a csv.";
		
		BufferedReader br = new BufferedReader(new FileReader(csvFile));  // Creates BufferedReader to temp store information from CSV
		// Loop over data from CSV file and add it to the ArrayList
		while((line = br.readLine()) != null) {
			data.add(Arrays.asList(line.split(",")));
		}
		br.close();
		
		chartAccounts();
		accountsForCollections();
		
		return "Success!";
	}
	
	/**
	 * Takes a file and returns the extension.
	 * @param textFile file to find the extension of.
	 * @return returns extension (csv, txt, etc).
	 */
	private String getExtension(File textFile) {
		// Code for Extension check
		String fileName = textFile.getName();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}
	
	/**
	 * Empties data by assigning a new empty arraylist/hashmap.
	 */
	public void deleteData() {
		data = new ArrayList<>();
		failedTransactions = new ArrayList<>();
		chartedAccounts = new HashMap<>();
	}
	
	/**
	 * Wraps around transactionBasedOnType in order to have error handling and ability to add more methods for new transactions
	 */
	private void chartAccounts() {
		ArrayList<String> tempArray = new ArrayList<String>();	// Temp arraylist for storing information
		
		// Iterate over the data arraylist which contains all given CSV file data
		for(List<String> d : data) {
			try {
				transactionBasedOnType(d, tempArray);
			}
			catch(Exception e) {
				failedTransactions.add(d); // Store any failed transactions to be displayed later
			}
		}		
		
	}
	
	/**
	 * Returns a HashMap of all charted accounts by Card number, balance is the first string in the array. Rest is names tied to that card.
	 * @return HashMap of charted accounts
	 */
	public HashMap<String, ArrayList<String>> getChartedAccounts(){
		
		return chartedAccounts;
		
	}
	
	/**
	 * Function that determines the type of transaction and does all the calculations
	 * 
	 * TODO - refactor and reduce amount of reused code. Manipulating data with transactions that already exist could be added into another method
	 * 
	 * @param d List of Strings that is meant to be in a row format (from data)
	 * @param tempArray temporary arrayList used for storage and moving around data between transactions.
	 */
	private void transactionBasedOnType(List<String> d, ArrayList<String> tempArray) {
		// Add proper error handling for the card, amount, and cardtarget (ensure they are numerical)
		String name = d.get(0); 					// Get the name of the card owner
		String card = d.get(1);						// Get the card number
		Float amount = Float.parseFloat(d.get(2));	// Get the amount of the transaction
		String type = d.get(3);						// Get the type of transaction
		tempArray = new ArrayList<String>();
		
		// Switch that determines what type of transaction
		switch(type) {
			case "Transfer":
				String cardTarget = d.get(5); // If the type is Transfer then we know there is a target card
				// Check to see if these card already exist in the data base. We are assuming these transactions are real
				if(chartedAccounts.containsKey(cardTarget) && chartedAccounts.containsKey(card)) {
					amount += Float.parseFloat(chartedAccounts.get(cardTarget).get(0));
					tempArray = chartedAccounts.get(cardTarget);
					tempArray.set(0, String.valueOf(amount));
					chartedAccounts.put(cardTarget, tempArray);
					
					// Rem

					amount = Float.parseFloat(chartedAccounts.get(card).get(0)) - amount;
					tempArray = chartedAccounts.get(card);
					tempArray.set(0, String.valueOf(amount));
					
					if(!tempArray.contains(name))
						tempArray.add(name); 
					
					chartedAccounts.put(card, tempArray);
				}
				else if (chartedAccounts.containsKey(cardTarget)){	// If the card doesn't exist add it, the account name, and its balance to the hashmap
					amount = -amount;
					tempArray.add(Float.toString(amount));
					tempArray.add(name);
					
					chartedAccounts.put(card, tempArray);
					
					tempArray = new ArrayList<String>();
					amount = -amount;
					amount += Float.parseFloat(chartedAccounts.get(cardTarget).get(0));
					tempArray = chartedAccounts.get(cardTarget);
					tempArray.set(0, String.valueOf(amount));
					chartedAccounts.put(cardTarget, tempArray);
					
				}
				else if (chartedAccounts.containsKey(card)) { // If the target card doesn't exist add it, the account name, and its balance
					amount = Float.parseFloat(chartedAccounts.get(card).get(0)) - amount;
					tempArray = chartedAccounts.get(card);
					tempArray.set(0, String.valueOf(amount));
					
					if(!tempArray.contains(name))
						tempArray.add(name); 
					
					chartedAccounts.put(card, tempArray);
					
					tempArray = new ArrayList<String>();
					amount = -amount;
					tempArray.add(Float.toString(amount));
					tempArray.add(name);
					
					chartedAccounts.put(cardTarget, tempArray);
				}
				else if (!chartedAccounts.containsKey(cardTarget) && !chartedAccounts.containsKey(card)){ // Assuming neither the target nor original exist
					amount = -amount;
					tempArray.add(Float.toString(amount));
					tempArray.add(name);
					
					chartedAccounts.put(card, tempArray);
					
					tempArray = new ArrayList<String>();
					amount = -amount;
					tempArray.add(Float.toString(amount));
					tempArray.add(name);
					
					chartedAccounts.put(cardTarget, tempArray);
				}
				break;
			case "Credit":
				if (chartedAccounts.containsKey(card)){
					amount = Float.parseFloat(chartedAccounts.get(card).get(0)) - amount;
					tempArray = chartedAccounts.get(card);
					tempArray.set(0, String.valueOf(amount));
					if(!tempArray.contains(name))
						tempArray.add(name); 
					
					chartedAccounts.put(card, tempArray);
				}
				else {
					amount = -amount;
					tempArray.add(Float.toString(amount));
					tempArray.add(name);
					
					chartedAccounts.put(card, tempArray);
				}
				break;
			case "Debit":
				if (chartedAccounts.containsKey(card)) {
					amount = Float.parseFloat(chartedAccounts.get(card).get(0)) + amount;
					tempArray = chartedAccounts.get(card);
					tempArray.set(0, String.valueOf(amount));
					
					if(!tempArray.contains(name))
						tempArray.add(name); 
					
					chartedAccounts.put(card, tempArray);
				}
				else {
					tempArray.add(Float.toString(amount));
					tempArray.add(name);
					
					chartedAccounts.put(card, tempArray);
				}
				break;
			default:
				// Add an error for no found type
		}

	}
	
	/**
	 * Iterates over all charted accounts and stores any below 0 in accountsForCollections
	 */
	private void accountsForCollections() {
		
		for(String key : chartedAccounts.keySet()) {
			if(Float.parseFloat(chartedAccounts.get(key).get(0)) < 0)
				accountsForCollections.put(key, chartedAccounts.get(key));
		}
	}

	/** 
	 * @return a HashMap of negative balance accounts with card numbers as keys 
	 */
	public HashMap<String, ArrayList<String>> getAccountsForCollections() {
			
		return accountsForCollections;
		
	}
	
	/**
	 * Function that returns all failed transactions during the charting/parsing process.
	 * 
	 * @return Returns a list that contains each failed transaction as lists.
	 */
	public List<List<String>> failedTransactions(){
		
		return failedTransactions;
		
	}
}
