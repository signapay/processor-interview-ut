import java.util.*;

public class AccountManager {
    private Map<String, Map<String, Double>> accounts = new HashMap<>();
    private List<BadTransaction> badTransactions = new ArrayList<>();

    class BadTransaction {
        private AccountInfo transaction;
        private String reason;
    
        public BadTransaction(AccountInfo transaction, String reason) {
            this.transaction = transaction;
            this.reason = reason;
        }
    
        public AccountInfo getTransaction() {
            return transaction;
        }
    
        public String getReason() {
            return reason;
        }
    }
    

    // Process a list of transactions
    public void processTransactions(List<AccountInfo> transactions) {
        for (AccountInfo t : transactions) {
            try {
                if (t.getTransactionType().equalsIgnoreCase("Credit") || t.getTransactionType().equalsIgnoreCase("Debit")) {
                    processCreditOrDebit(t);
                } else if (t.getTransactionType().equalsIgnoreCase("Transfer")) {
                    processTransfer(t);
                } else {
                    // Add to bad transactions with a reason
                    badTransactions.add(new BadTransaction(t, "Invalid transaction type."));
                }
            } catch (Exception e) {
                // Add to bad transactions with a reason for the error
                badTransactions.add(new BadTransaction(t, "Error processing transaction: " + e.getMessage()));
            }
        }
    }
    

    // Process Credit or Debit transactions
    private void processCreditOrDebit(AccountInfo t) {
        accounts.putIfAbsent(t.getAccountName(), new HashMap<>());
        Map<String, Double> cards = accounts.get(t.getAccountName());
    
        cards.putIfAbsent(t.getCardNumber(), 0.0);
        double balance = cards.get(t.getCardNumber());
    
        if (t.getTransactionAmount() < 0) {
            badTransactions.add(new BadTransaction(t, "Negative transaction amount."));
            return;
        }
    
        if (t.getTransactionType().equalsIgnoreCase("Credit")) {
            balance += t.getTransactionAmount();
        } else if (t.getTransactionType().equalsIgnoreCase("Debit")) {
            if (balance < t.getTransactionAmount()) {
                badTransactions.add(new BadTransaction(t, "Insufficient funds for debit."));
                return;
            }
            balance -= t.getTransactionAmount();
        }
        cards.put(t.getCardNumber(), balance);
    }
    
    

    // Process Transfer transactions
    private void processTransfer(AccountInfo t) {
        if (t.getTransactionAmount() < 0 || t.getTargetCardNumber() == null) {
            badTransactions.add(new BadTransaction(t, "Negative amount or missing target card for transfer."));
            return;
        }
    
        String targetAccountName = findAccountByCard(t.getTargetCardNumber());
        if (!accounts.containsKey(t.getAccountName()) || !accounts.get(t.getAccountName()).containsKey(t.getCardNumber())
            || targetAccountName == null) {
            badTransactions.add(new BadTransaction(t, "Source or target account/card not found."));
            return;
        }
    
        Map<String, Double> sourceCards = accounts.get(t.getAccountName());
        Map<String, Double> targetCards = accounts.get(targetAccountName);
        double sourceBalance = sourceCards.get(t.getCardNumber());
    
        if (sourceBalance < t.getTransactionAmount()) {
            badTransactions.add(new BadTransaction(t, "Insufficient funds for transfer."));
            return;
        }
    
        sourceBalance -= t.getTransactionAmount();
        double targetBalance = targetCards.getOrDefault(t.getTargetCardNumber(), 0.0) + t.getTransactionAmount();
    
        sourceCards.put(t.getCardNumber(), sourceBalance);
        targetCards.put(t.getTargetCardNumber(), targetBalance);
    }
    
    
    private String findAccountByCard(String cardNumber) {
        for (String accountName : accounts.keySet()) {
            if (accounts.get(accountName).containsKey(cardNumber)) {
                return accountName;
            }
        }
        return null;
    }
    

    // Generate reports
    public void generateReports() {
        System.out.println("Chart of Accounts:");
        for (String accountName : accounts.keySet()) {
            System.out.println("Account: " + accountName);
            Map<String, Double> cards = accounts.get(accountName);
            for (String cardNumber : cards.keySet()) {
                System.out.println("Card: " + cardNumber + ", Balance: " + cards.get(cardNumber));
            }
        }

        System.out.println("\nAccounts to be sent to collections:");
        for (String accountName : accounts.keySet()) {
            Map<String, Double> cards = accounts.get(accountName);
            for (String cardNumber : cards.keySet()) {
                if (cards.get(cardNumber) < 0) {
                    System.out.println("Account: " + accountName + ", Card: " + cardNumber);
                }
            }
        }

        System.out.println("\nBad Transactions:");
        for (BadTransaction bt : badTransactions) {
        AccountInfo t = bt.getTransaction();
        System.out.println(t.getAccountName() + ", " + t.getCardNumber() + ", " + t.getDescription() + " - Reason: " + bt.getReason());
    }
}

    // Reset the system
    public void resetSystem() {
        accounts.clear();
        badTransactions.clear();
    }
}
