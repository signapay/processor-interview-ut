import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;


public class Table extends AbstractTableModel {
    ArrayList<Transaction> records = new ArrayList<>();

    public void setRecords(ArrayList<Transaction> t){
        records = t;
    }
    String[] names = {"Account Name", "Card Number", "Amount", "Type", "Description", "Target Card"};

    public Table(StateManager state){
        state.registerTable(this);
    }

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return names[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0,columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction transaction = records.get(rowIndex);    // get row
        switch (columnIndex){
            case 0: return transaction.getAccountName();
            case 1: return transaction.getCardNumber();
            case 2: return transaction.getTransactionAmount();
            case 3: return transaction.getTransactionType();
            case 4: return transaction.getDescription();
            case 5: return transaction.getTargetCardNumber();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
