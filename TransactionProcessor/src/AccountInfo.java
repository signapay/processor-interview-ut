public class AccountInfo {
    private String accountName;
    private String cardNumber;
    private double transactionAmount;
    private String transactionType;
    private String description;
    private String targetCardNumber;  // Only for transfers

    public AccountInfo(String accountName, String cardNumber, double transactionAmount, String transactionType, String description, String targetCardNumber) {
        this.accountName = accountName;
        this.cardNumber = cardNumber;
        this.transactionAmount = transactionAmount;
        this.transactionType = transactionType;
        this.description = description;
        this.targetCardNumber = targetCardNumber;
    }

    // Getters and setters
    public String getAccountName() {
        return accountName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getDescription() {
        return description;
    }

    public String getTargetCardNumber() {
        return targetCardNumber;
    }
}
