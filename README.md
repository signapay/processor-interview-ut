# Transaction Processor

Interview project for SignaPay

This project is a transaction processor program targeted at an internal account manager for viewing and processing transaction files provided as .csv files. This program is a toy example.

## Use

To run on a system with an appropriate JDK installed, call ```./javac Main.java``` and then ```java Main``` in the same directory (folder) as the code.

Alternatively, using an IDE, open and execute the project from ```Main.java```.

### Techstack

This project was written in ```Java 13``` with use of the ```java.awt``` and ```javax.swing``` packages using a Windows 10 operating system machine.

Java was selected because it is a more secure language than similar languages popular for text processing, such as Python. Java has strict controls of scope and type, which makes it easier to write programs that will produce errors instead of undefined behavior.

An external database was not used due to the complexity of preparing the associated software by another party and the demonstrative nature of this repo.

### Data Files for Testing
In this repo, you will find the following csv files:

| File     | Description                                                                                                                                               |
|----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| test.csv | a smaller file that has a few sample transactions - the intent is to use this file for development. like most development resources, this file is "clean" |
| data.csv | a larger file that has a number of transactions - like the real world, you should not assume that this file is not without its "issues"                   |

## Features

The software processes transactions provided from a .csv list and reports information back to the user.

### User Interface (UI)

- Button for selection of file to open
  - Cumulatively adds to working memory
  - working memory is persisted to ```.backup.csv``` and is automatically loaded on program start
- Button for clearing memory
  - also clears the working memory cache in ```.backup.csv```
- Table for displaying working memory of files
- Buttons to toggle between table views
  - Views are valid transactions, accounts, cards failing audit, and failed transaction records
- Button for optional persistence of views to files

### Table Views

- Full transaction record
  - name, card number, amount, type, description, target card
    - if absent, target card is displayed as -1
- Accounts
  - name, card number, balance
- Collections Accounts
  - name, card number, balance
  - specifically accounts with poor standing
- Failed transaction parses (strings)
  - It was assumed that transaction target card to match a known account
  
### Error Logging

- Errors are logged to ```log.txt``` and include failed type conversions, failed file reads, and logging of deletion operations
- Failed transaction parses are written to ```failed_transactions_parses.csv```
- Failed transactions due to inconsistent logic are written to ```failed_transactions.csv```
- Accounts that fail the audit are written to ```failed_audit.csv```

## Program Documentation

### Transaction Structure

| Field              | Type   | Description                                                       |
|--------------------|--------|-------------------------------------------------------------------|
| Account Name       | Text   | The name of the account                                           |
| Card Number        | Number | The card number used for the transaction, 1:M with account names  |
| Transaction Amount | Number | The amount of the transaction, can be positive or negative        |
| Transaction Type   | Text   | The type of transaction, values can be Credit, Debit, or Transfer |
| Description        | Text   | A brief description of the transaction                            |
| Target Card Number | Number | (optional) only provided if the transaction type is a transfer    |

### Class List

- ```Main``` Main class for launching program
- ```GUI``` Generates window for program

#### Datatypes

- ```Account``` Partially immutable data class for representing account. Allows indirect interaction with cards associated with account
- ```Card``` Partially immutable data class for representing cards. Permits changing balance
- ```Money``` Immutable data class for representing an amount of money losslessly
- ```toCSV``` Interface for generating a string for CSV that is different from toString method
- ```Transaction``` Immutable data class for representing a Transaction

#### DataManager

- ```Database``` Template object to manage an ArrayList of a specified type with file alteration functions.
- ```DataManager``` Subclass of Database<Transaction> used to manage the file I/O for the program. Contains instances of Database for other types; serves as internal record memory
- ```StateManager``` Class that communicates between frontend and DataManager, controlling the state of the program

#### GUI

- ```Buttons``` Static factory functions for buttons used in main GUI
- ```DisplayTableModel``` Table Model used to serve content of ScrollableTable
- ```ScrollableTable``` JScrollPane object used to control display table

#### Log

- ```Log``` Intermediary class for logging to file with a specified logging function
- ```LogToFile``` Class to log text to a specified file


## Interview
### Requested Feedback

- How well does this code encompass the principles of software design?
- How can this code be improved to produce more extensible software?

### Expansion of Functionality

The presented solution is a toy solution. It has been implemented to be modular so adding functionality can be done with minimal refactor cost. A proper implementation of this would rely much more heavily on more neutral types, such as Interfaces, that permit polymorphic behavior of datatypes and objects. Given the toy nature of this example, such extensive measures were deemed unnecessary.
