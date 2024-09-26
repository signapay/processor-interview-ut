# Datatypes.Transaction Processor

Interview project for signapay

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

### Error Logging

- Errors are logged to ```log.txt``` and include failed type conversions, failed file reads, and logging of deletion operations
- Failed transaction parses are written to ```failed_transactions.csv```

## Program Documentation

### Transaction Structure

| Field              | Type   | Description                                                       |
|--------------------|--------|-------------------------------------------------------------------|
| Account Name       | Text   | The name of the account                                           |
| Card Number        | Number | The card number used for the transaction, 1:M with account names  |
| Datatypes.Transaction Amount | Number | The amount of the transaction, can be positive or negative        |
| Datatypes.Transaction Type   | Text   | The type of transaction, values can be Credit, Debit, or Transfer |
| Description        | Text   | A brief description of the transaction                            |
| Target Card Number | Number | (optional) only provided if the transaction type is a transfer    |


### Class List



## Interview
### Requested Feedback

- How well does this code encompass the principles of software design?
- How can this code be improved to produce more extensible software?

### Expansion of Functionality

The presented solution is a toy solution. It has been implemented to be modular so adding functionality can be done with minimal refactor cost. A proper implementation of this would rely much more heavily on more neutral types, such as Interfaces, that permit polymorphic behavior of datatypes and objects. Given the toy nature of this example, such extensive measures were deemed unnecessary.



## TODO
- table views
  - failed transactions
  - accounts
  - negative accounts
- account processing
- write failed transactions to file / create persistence
- remove open button
- buttons to toggle table views
- write class list documentation