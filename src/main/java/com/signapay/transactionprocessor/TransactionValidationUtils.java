package com.signapay.transactionprocessor;

public class TransactionValidationUtils {

    public static boolean isTransactionValid(String[] values) {
        if (values.length < 5) return false;
        if (!values[0].matches("^[a-zA-Z0-9 ]+$")) return false;
        if (!values[1].matches("\\d{16}")) return false;
        try {
            Double.parseDouble(values[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        String transactionType = values[3].toLowerCase();
        if (!transactionType.equals("credit") && !transactionType.equals("debit") && !transactionType.equals("transfer")) {
            return false;
        }
        if (transactionType.equals("transfer") && (values.length < 6 || values[5] == null || !values[5].matches("\\d{16}"))) {
            return false;
        }
        return true;
    }

    public static String determineErrorMessage(String[] values) {
        if (values.length < 5) return "Transaction has missing fields";
        if (!values[0].matches("^[a-zA-Z0-9 ]+$")) return "Invalid account name: " + values[0];
        if (!values[1].matches("\\d{16}")) return "Invalid card number: " + values[1];
        try {
            Double.parseDouble(values[2]);
        } catch (NumberFormatException e) {
            return "Invalid transaction amount: " + values[2];
        }
        String transactionType = values[3].toLowerCase();
        if (!transactionType.equals("credit") && !transactionType.equals("debit") && !transactionType.equals("transfer")) {
            return "Invalid transaction type: " + values[3];
        }
        if (transactionType.equals("transfer") && (values.length < 6 || values[5] == null || !values[5].matches("\\d{16}"))) {
            return "Invalid or missing target card number for transfer: " + (values.length >= 6 ? values[5] : "N/A");
        }
        return "Unknown error";
    }


}
