package com.transactionprocessor.controller;
import com.transactionprocessor.model.Transaction;
import com.transactionprocessor.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadTransactions(@RequestParam("file") MultipartFile file) {
        transactionService.processTransactionFile(file);
        return ResponseEntity.ok("Transactions processed successfully");
    }
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetTransactions() {
        transactionService.resetTransactions();
        return ResponseEntity.ok("System reset successfully");
    }

    @GetMapping("/allTransactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @DeleteMapping("/deleteTransaction/{transactionId}")
    public String deleteTransaction(@PathVariable("transactionId") Long transactionId) {
        return transactionService.deleteTransaction(transactionId);
    }

    @PutMapping("/editTransaction/{transactionId}")
    public String editTransaction(@PathVariable("transactionId") Long transactionId,@RequestBody Transaction updatedTransaction) {
        Optional<Transaction> existingTransaction = transactionService.getTransactionById(transactionId);
        if(existingTransaction.isPresent()){
            Transaction transaction = existingTransaction.get();
            transaction.setCardNumber(updatedTransaction.getCardNumber());
            transaction.setAccountName(updatedTransaction.getAccountName());
            transaction.setTransactionAmount(updatedTransaction.getTransactionAmount());
            transaction.setTransactionType(updatedTransaction.getTransactionType());
            transaction.setDescription(updatedTransaction.getDescription());

            Transaction returnedTransaction = transactionService.editTransaction(transaction);
            return "Updated successfully";
        }
        else {
            // If the transaction is not found, return a 404 Not Found response
            return "Transaction is not found";
        }


    }
}





