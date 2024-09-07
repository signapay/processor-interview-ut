package sgp.transactionprocessor;

// An object for a Faulty Transaction :

public class FaultyTrans {
    String accountName;
    String cardNo;
    String transactionAmount;
    String transactionType;
    String description;
    String targetCardNo;

    public FaultyTrans(String accName, String cardNo, String transAmt, String transType, String desc, String trgNo){
    this.accountName = accName;
    this.cardNo = cardNo;
    this.transactionAmount = transAmt;
    this.transactionType  = transType;
    this.description = desc;
    this.targetCardNo = trgNo;
    }
}
