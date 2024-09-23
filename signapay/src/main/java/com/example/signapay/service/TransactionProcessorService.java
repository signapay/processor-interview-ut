package com.example.signapay.service;

import com.example.signapay.entity.Account;
import com.example.signapay.entity.Card;
import com.example.signapay.entity.Transaction;
import com.example.signapay.entity.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.signapay.repository.AccountRepository;
import com.example.signapay.repository.CardRepository;
import com.example.signapay.repository.TransactionRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TransactionProcessorService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    private static final Logger log = LoggerFactory.getLogger(TransactionProcessorService.class);

    private String TRANSFER = "transfer";

    private double handlePrecision(double value){
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.FLOOR)
                .doubleValue();
    }

    /**
     * Processes a CSV file containing transactions and saves valid or bad transactions.
     */
    public void processTransactionFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // skipping the empty lines in the file
                line = line.trim();
                if(line.isEmpty()){
                    continue;
                }
                try {
                    log.info("Processing the file {} with line : {}", file.getName(), line);
                    processTransaction(line, TransactionStatus.VALID, null);
                } catch (Exception e) {
                    log.error("Error occured while processing the file {} with line {}", file.getName(), line);
                    log.error("Error message : {}", e.getMessage());
                    processTransaction(line, TransactionStatus.BAD, e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the file", e);
        }
    }

    /**
     * processes a single transaction and determines if it is valid or bad.
     */
    private void processTransaction(String csvLine, TransactionStatus status, String errorReason) {
        String[] fields = csvLine.split(",");
        if((TRANSFER.equalsIgnoreCase(fields[3]) && fields.length < 6) || (!TRANSFER.equalsIgnoreCase(fields[3]) && fields.length<5)){
            if(TransactionStatus.BAD.equals(status)) {
                /**
                 * NOTE :
                 *
                 * if a BAD transaction cannot be saved in database we skip the transactions by printing them in the logs
                 * In actual system we have to save then in an error file
                 */

                log.error("Invalid data to save the transaction in the database. so please use some form of txt file to save these kind of transactions");
                log.error("error line : {}", csvLine);
                return;
            } else if (TransactionStatus.VALID.equals(status)) {
                throw new IllegalArgumentException("Invalid number of fields");
            }
        }

        String accountName = fields[0];
        String cardNumber = fields[1];
        double amount = 0;
        try {
            amount = handlePrecision(Double.parseDouble(fields[2]));
        } catch (Exception e) {
            /**
             * NOTE :
             *
             * if a transaction amount cannot be parsed so it cannot be saved in database we skip the transactions by
             * printing them in the logs, In actual system we have to save then in an error file
             */
            log.error("Error occured while parsing the amount {}", fields[2]);
            log.error("Error message : {}", e.getMessage());
            return;
        }
        String transactionType = fields[3];
        String description = fields[4];
        String targetCardNumber = fields.length > 5 ? fields[5] : null;

        // Create and save the transaction
        Transaction transaction = new Transaction();
        transaction.setAccountName(accountName);
        transaction.setCardNumber(cardNumber);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setDescription(description);
        transaction.setTargetCardNumber(targetCardNumber);
        transaction.setStatus(status);
        transaction.setErrorReason(errorReason);

        transactionRepository.save(transaction);

        if (status == TransactionStatus.VALID) {
            try {
                // handling card balance
                processValidTransaction(accountName, cardNumber, amount, transactionType, targetCardNumber);
            } catch (Exception e) {
                // If processing failed, mark transaction as bad
                transaction.setStatus(TransactionStatus.BAD);
                transaction.setErrorReason(e.getMessage());
                transactionRepository.save(transaction);
            }
        }
    }

    /**
     * Processes a valid transaction: credit, debit, or transfer.
     */
    private void processValidTransaction(String accountName, String cardNumber, double amount, String transactionType, String targetCardNumber) {
        // Find the account associated with the account name
        List<Account> accounts = accountRepository.findByNameIgnoreCase(accountName);
        if (accounts.isEmpty()) {
            throw new IllegalArgumentException("Account not found: " + accountName);
        }

        Account account = accounts.get(0);

        // Find the card associated with the card number
        List<Card> cards = cardRepository.findByCardNumber(cardNumber);
        if (cards.isEmpty()) {
            throw new IllegalArgumentException("Card not found: " + cardNumber);
        }

        Card card = cards.get(0);

        // Handle different transaction types: Credit, Debit, Transfer
        switch (transactionType.toUpperCase()) {
            case "CREDIT":
                card.setBalance(card.getBalance() + amount);
                break;

            case "DEBIT":
                card.setBalance(card.getBalance() - amount);
                break;

            case "TRANSFER":
                if (targetCardNumber == null || targetCardNumber.isEmpty()) {
                    throw new IllegalArgumentException("Target card number is required for transfer");
                }
                // Deduct from the source card
                card.setBalance(card.getBalance() - amount);
                // Find the target card and add the amount
                List<Card> targetCards = cardRepository.findByCardNumber(targetCardNumber);
                if (targetCards.isEmpty()) {
                    throw new IllegalArgumentException("Target card not found: " + targetCardNumber);
                }
                Card targetCard = targetCards.get(0);
                targetCard.setBalance(targetCard.getBalance() + amount);
                cardRepository.save(targetCard); // Update target card balance
                break;

            default:
                throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }

        // Update the card's balance in the repository
        cardRepository.save(card);
    }

    /**
     * Retrieves all bad transactions.
     */
    public List<Transaction> getBadTransactions() {
        return transactionRepository.findByStatus(TransactionStatus.BAD);
    }

    /**
     * Retrieves all accounts that need to go to collections (any cards with a negative balance).
     */
    public List<Account> getAccountsForCollections() {
        return accountRepository.findAccountsForCollections();
    }

    public List<Account> getAllAccount(){
        return accountRepository.findAll();
    }

    /**
     * Resets the system by clearing all data (transactions, accounts, and cards).
     */
    public void resetSystem() {
        log.warn("Deleting the data in the tables to reset the system");
        transactionRepository.deleteAll();
        cardRepository.deleteAll();
        accountRepository.deleteAll();
    }

    // Function to create new account
    public String createNewAccount(List<String> accountNames) {
        for(String accountName: accountNames){
            Account account = new Account();
            account.setName(accountName);
            accountRepository.save(account);
        }
        return "Accounts created Successfully";
    }

    // Find account by name
    public Account findAccountByName(String accountName) {
        return accountRepository.findByNameIgnoreCase(accountName).get(0);  // Assuming findByName is implemented in AccountRepository
    }

    // Function to create a new card
    public Card createNewCard(String cardNumber, Account account) {
        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setAccount(account);
        return cardRepository.save(card);
    }
}
