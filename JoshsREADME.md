<h1 style="text-align: center;">Transaction Processor</h1> 

##  I created this project using java as the backend and the terminal as the UI

##  Features I completed
Transaction Processing, handles the credits, debits, and transfers. \
\
Error Handling, Tracks and reports bad transactions like negative or invalid accounts along with outtputting why the transaction was bad. \
\
Reports, Generates a chart of all accounts with their associated cards balanced, lists the accounts that need to be sent to collections, lists the bad transactions with the reason for the failure. \ 
\
File input, Reads transaction data from any user provided CSV with error handling to deal with "unclean" data. \ 
\
Reset Functionality, allows the system to be reset for a new blank state. \ 
\
## How it works
The application processes transactions such as Credit, Debit, and Transfer from a CSV file provided by the user. Each transaction updates account balances, and invalid transactions are flagged as "bad transactions." Users can generate reports to view account balances, identify accounts with negative balances, and review any bad transactions.\ 
\

## How to run
Compile the application by running javac App.java AccountManager.java Transactions.java fileReader.java in your terminal. Once compiled, execute the program with java App, follow the menu options to process a transaction file, generate reports, reset the system, or exit.\
\


