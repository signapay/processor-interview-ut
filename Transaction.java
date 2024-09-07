package sgp.transactionprocessor;

import java.util.*;
import java.io.*;

// An object for a Valid Transaction:

public class Transaction {
    String accountName;
    long cardNo;
    double transactionAmount;
    String transactionType;
    String description;
    long targetCardNo;

    public Transaction(String accName, long cardNo, double transAmt, String transType, String desc, long trgNo){
    this.accountName = accName;
    this.cardNo = cardNo;
    this.transactionAmount = transAmt;
    this.transactionType  = transType;
    this.description = desc;
    this.targetCardNo = trgNo;
    }

    // returns the Account name of the Transaction object
    public String getAccountName()
    {
        return this.accountName;
    }

}


