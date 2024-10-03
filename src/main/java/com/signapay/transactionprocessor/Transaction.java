package com.signapay.transactionprocessor;

// Transaction class
public class Transaction {
    private String accountName;
    private String cardNumber;
    private Double transactionAmount;
    private String transactionType;
    private String targetCardNumber;
    private String errorMessage;

    public Transaction() {}

    public Transaction(String accountName, String cardNumber, String transactionAmount, String transactionType, String targetCardNumber) {
        this.accountName = accountName;
        this.cardNumber = cardNumber;
        this.transactionAmount = Double.parseDouble(transactionAmount);
        this.transactionType = transactionType;
        this.targetCardNumber = targetCardNumber;
        this.errorMessage = null;
    }

    public Transaction(String accountName, String cardNumber, String transactionAmount, String transactionType, String targetCardNumber, String errorMessage) {
        this(accountName, cardNumber, transactionAmount, transactionType, targetCardNumber);
        this.errorMessage = errorMessage;
    }

    // Getters and setters
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public Double getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(Double transactionAmount) { this.transactionAmount = transactionAmount; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getTargetCardNumber() { return targetCardNumber; }
    public void setTargetCardNumber(String targetCardNumber) { this.targetCardNumber = targetCardNumber; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}

