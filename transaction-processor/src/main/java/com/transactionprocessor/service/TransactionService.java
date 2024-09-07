package com.transactionprocessor.service;

import com.transactionprocessor.model.Transaction;
import com.transactionprocessor.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    // Process a file and save transactions
    public void processTransactionFile(MultipartFile file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<Transaction> transactions = br.lines()
                    .skip(1) // Assuming first line is header
                    .map(this::parseTransaction)
                    .collect(Collectors.toList());
            transactionRepository.saveAll(transactions);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the error properly
        }
    }

    // Parse each line into a Transaction object
    private Transaction parseTransaction(String line) {
        String[] fields = line.split(","); // Assuming CSV format
        Transaction transaction = new Transaction();
        transaction.setAccountName(fields[0]);
        transaction.setCardNumber(fields[1]);
        transaction.setTransactionAmount(Double.parseDouble(fields[2]));
        transaction.setTransactionType(fields[3]);
        transaction.setDescription(fields[4]);
        if ("Transfer".equalsIgnoreCase(fields[3])) {
            transaction.setTargetCardNumber(fields[5]);
        }
        return transaction;
    }
    public void resetTransactions() {
        transactionRepository.deleteAll();
    }
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    public String deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            if (!transactionRepository.existsById(id)) {
                return "Transaction deleted successfully";
            } else {
                return "Failed to delete the transaction";
            }
        } else {
            return "Transaction not found";
        }
    }

    public Optional<Transaction> getTransactionById(Long transactionId) {
return transactionRepository.findById(transactionId);
    }

    public Transaction editTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}








