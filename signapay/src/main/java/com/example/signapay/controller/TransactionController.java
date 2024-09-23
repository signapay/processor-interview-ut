package com.example.signapay.controller;

import com.example.signapay.entity.Account;
import com.example.signapay.entity.CreateCardRequest;
import com.example.signapay.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.signapay.service.TransactionProcessorService;

import java.util.List;

@RestController

/**
 * allowing cross origin requests from any domain
 * if we have to allow from any specific domain then we can restrict it
 * but for the scope of our project to be simpler I am allowing for all domains
 */
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionProcessorService transactionProcessorService;

    // api for handling uploaded csv transactions file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadTransactionFile(@RequestParam("file") MultipartFile file) {
        transactionProcessorService.processTransactionFile(file);
        return ResponseEntity.ok("File processed successfully.");
    }

    // api to fetch list of accounts that we need to give to collections
    @GetMapping("/collections")
    public List<Account> getCollectionAccounts() {
        return transactionProcessorService.getAccountsForCollections();
    }

    /**
     * api to list bad transactions
     * this does not include unparsable transactions as they cannot be stored in database
     * we can handle these unparsable transactions by saving them in a error-file for each uploaded file if needed
     * and return to the user so that they can validate
     */
    @GetMapping("/bad-transactions")
    public List<Transaction> getBadTransactions() {
        return transactionProcessorService.getBadTransactions();
    }

    /**
     *  api to reset the system
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetSystem() {
        transactionProcessorService.resetSystem();
        return ResponseEntity.ok("System reset successfully.");
    }

    /**
     * api to create accounts, this is need to create data for accounts table
     * because if we dont have account we cannot validate the transactions which makes their status BAD
     * since the accounts and cards data is not provided I have taken Accounts and Cards data
     * from test.csv file provided
     *
     * NOTE: FRONT END IS NOT PROVIDED
     */
    @PostMapping("/createAccount")
    public ResponseEntity<String> createAccounts(@RequestBody List<String> accountNames) {
        String response = transactionProcessorService.createNewAccount(accountNames);
        return ResponseEntity.ok(response);
    }

    /**
     * api to create Card, this is need to create data for Card table
     * because if we dont have card we cannot validate the transactions which makes their status BAD
     * since the accounts and cards data is not provided I have taken Accounts and Cards data
     * from test.csv file provided
     *
     * NOTE: FRONT END IS NOT PROVIDED
     */
    @PostMapping("/createCard")
    public ResponseEntity<String> createNewCards(@RequestBody List<CreateCardRequest> cardRequests) {
        for (CreateCardRequest cardRequest : cardRequests) {
            // Fetch the account by accountName
            Account account = transactionProcessorService.findAccountByName(cardRequest.getAccountName());
            if (account == null) {
                return ResponseEntity.badRequest().body("Account with name " + cardRequest.getAccountName() + " not found.");
            }
            // Create a new card linked to the account
            transactionProcessorService.createNewCard(cardRequest.getCardNumber(), account);
        }
        return ResponseEntity.ok("Cards created successfully.");
    }

    // api to fetch accounts and their corresponding cards
    @GetMapping("/accounts")
    public List<Account> getAccounts(){
        return transactionProcessorService.getAllAccount();
    }
}

