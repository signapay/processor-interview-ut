package com.signapay.transactionprocessor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class TransactionService {
    private Map<String, Map<String, Double>> accounts = new HashMap<>();
    private List<Transaction> badTransactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        try {
            String accountName = transaction.getAccountName();
            String cardNumber = transaction.getCardNumber();
            Double amount = transaction.getTransactionAmount();
            String transactionType = transaction.getTransactionType();
            String targetCardNumber = transaction.getTargetCardNumber();

            if (accountName == null || cardNumber == null || amount == null || transactionType == null) {
                throw new IllegalArgumentException("Invalid transaction data");
            }

            // Ensure account and card are in the system
            accounts.putIfAbsent(accountName, new HashMap<>());
            Map<String, Double> accountCards = accounts.get(accountName);

            // Process Credit
            if ("credit".equalsIgnoreCase(transactionType)) {
                processCredit(accountCards, cardNumber, amount);

                // Process Debit
            } else if ("debit".equalsIgnoreCase(transactionType)) {
                processDebit(accountCards, cardNumber, amount);

                // Process Transfer
            } else if ("transfer".equalsIgnoreCase(transactionType)) {
                processTransfer(accountName, cardNumber, amount, targetCardNumber);
            }

        } catch (Exception e) {
            transaction.setErrorMessage("Error processing transaction: " + e.getMessage());
            badTransactions.add(transaction);
        }
    }

    // Method to process a credit transaction
    private void processCredit(Map<String, Double> accountCards, String cardNumber, Double amount) {
        double newBalance = accountCards.getOrDefault(cardNumber, 0.0) + Math.abs(amount);
        accountCards.put(cardNumber, round(newBalance, 2));
    }

    // Method to process a debit transaction
    private void processDebit(Map<String, Double> accountCards, String cardNumber, Double amount) {
        double newBalance = accountCards.getOrDefault(cardNumber, 0.0) - Math.abs(amount);
        accountCards.put(cardNumber, round(newBalance, 2));
    }

    // Method to process a transfer transaction
    private void processTransfer(String fromAccountName, String fromCardNumber, Double amount, String targetCardNumber) {
        // Deduct from the source card (fromAccount)
        Map<String, Double> fromAccountCards = accounts.get(fromAccountName);
        double fromBalance = fromAccountCards.getOrDefault(fromCardNumber, 0.0) - Math.abs(amount);
        fromAccountCards.put(fromCardNumber, round(fromBalance, 2));

        // Credit to all accounts that have the target card number
        for (Map.Entry<String, Map<String, Double>> entry : accounts.entrySet()) {
            Map<String, Double> accountCards = entry.getValue();
            if (accountCards.containsKey(targetCardNumber)) {
                double toBalance = accountCards.getOrDefault(targetCardNumber, 0.0) + Math.abs(amount);
                accountCards.put(targetCardNumber, round(toBalance, 2));

                //System.out.println("Transfer to: " + entry.getKey() + " (Card: " + targetCardNumber + ") New Balance: " + toBalance);
            }
        }

       // System.out.println("Transfer from: " + fromAccountName + " (Card: " + fromCardNumber + ") New Balance: " + fromBalance);
    }

    public Map<String, Map<String, Double>> getAccounts() {
        Map<String, Map<String, Double>> visibleAccounts = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> accountEntry : accounts.entrySet()) {
            String accountName = accountEntry.getKey();
            Map<String, Double> cardBalances = accountEntry.getValue();

            Map<String, Double> visibleCardBalances = new HashMap<>();
            for (Map.Entry<String, Double> cardEntry : cardBalances.entrySet()) {
                String cardNumber = cardEntry.getKey();
                Double balance = cardEntry.getValue();
                visibleCardBalances.put(cardNumber, round(balance, 2));
            }

            visibleAccounts.put(accountName, visibleCardBalances);
        }
        return visibleAccounts;
    }

    public List<String> getCollections() {
        List<String> collections = new ArrayList<>();
        for (Map.Entry<String, Map<String, Double>> accountEntry : accounts.entrySet()) {
            String accountName = accountEntry.getKey();
            Map<String, Double> cardBalances = accountEntry.getValue();
            for (Map.Entry<String, Double> cardEntry : cardBalances.entrySet()) {
                String cardNumber = cardEntry.getKey();
                Double balance = cardEntry.getValue();
                if (balance < 0) {
                    collections.add(accountName + " Card: " + cardNumber + " Balance: " + round(balance, 2));
                }
            }
        }
        return collections;
    }

    public List<Transaction> getBadTransactions() {
        return badTransactions;
    }

    public void reset() {
        accounts.clear();
        badTransactions.clear();
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
