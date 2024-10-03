package com.signapay.transactionprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    Set<String> processedFiles = new HashSet<>();

    @PostMapping("/upload")
    public ResponseEntity<String> uploadTransactions(@RequestParam("file") MultipartFile file) {

        if(processedFiles.contains(file.getOriginalFilename())) {
            return new ResponseEntity<>("File already processed. change file name to process.", HttpStatus.BAD_REQUEST);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (TransactionValidationUtils.isTransactionValid(values)) {
                    transactionService.addTransaction(new Transaction(values[0], values[1], values[2], values[3], (values.length >= 6) ? values[5] : null));
                } else {
                    String errorMessage = TransactionValidationUtils.determineErrorMessage(values);
                    try {
                        transactionService.getBadTransactions().add(new Transaction(
                                values.length > 0 ? values[0] : "Unknown",
                                values.length > 1 ? values[1] : null,
                                values.length > 2 ? values[2] : null,
                                values.length > 3 ? values[3] : null,
                                (values.length >= 6) ? values[5] : null,
                                errorMessage
                        ));
                    } catch (NumberFormatException e){
                        transactionService.getBadTransactions().add(new Transaction(
                                values.length > 0 ? values[0] : "Unknown",
                                values.length > 1 ? values[1] : null,
                                "0",
                                values.length > 3 ? values[3] : null,
                                (values.length >= 6) ? values[5] : null,
                                errorMessage
                        ));
                    }
                }
            }
            processedFiles.add(file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
        return ResponseEntity.ok("File processed successfully");
    }
    private Double parseDoubleSafely(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @GetMapping("/accounts")
    public Map<String, Map<String, Double>> getAccounts() {
        return transactionService.getAccounts();
    }

    @GetMapping("/collections")
    public List<String> getCollections() {
        return transactionService.getCollections();
    }

    @GetMapping("/bad")
    public List<Transaction> getBadTransactions() {
        return transactionService.getBadTransactions();
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        transactionService.reset();
        processedFiles.clear();
        return ResponseEntity.ok("System reset successfully");
    }
}