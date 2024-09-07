package sgp.transactionprocessor;

import java.util.*;

// creating own Comparator that overrides the java-provided to sort the data based on Account Name:

public class NameComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction t1, Transaction t2) {
        return t1.getAccountName().compareTo(t2.getAccountName());
    }
}
