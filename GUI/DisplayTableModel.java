package GUI;

import Datatypes.Transaction;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Class for custom display model
 */
public class DisplayTableModel extends AbstractTableModel {
    // record set
    private ArrayList<Transaction> records = new ArrayList<>();

    // name set
    private ArrayList<String> titles = new ArrayList<>();

    /**
     * Constructor
     */
    public DisplayTableModel(){}

    /**
     * Update table contents to reflect new content
     * @param records records to display
     * @param titles titles to display
     */
    public void setRecords(ArrayList<Transaction> records, ArrayList<String> titles){
        this.records = records;
        this.titles = titles;
        this.fireTableDataChanged();
    }

    /**
     * Get number of rows
     * @return number of rows
     */
    @Override
    public int getRowCount() {
        return records.size();
    }

    /**
     * Get number of columns
     * @return number of columns
     */
    @Override
    public int getColumnCount() {
        return titles.size();
    }

    /**
     * Column name retrieval
     * @param columnIndex  the column being queried
     * @return name of column
     */
    @Override
    public String getColumnName(int columnIndex) {
        return titles.get(columnIndex);
    }

    /**
     * Column class retrieval
     * @param columnIndex  the column being queried
     * @return class of content
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0,columnIndex).getClass();
    }

    /**
     * Retrieve value of specified column location
     * @param rowIndex        the row whose value is to be queried
     * @param columnIndex     the column whose value is to be queried
     * @return value at location
     */
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

    /**
     * Query editable status of cell
     * @param rowIndex  the row being queried
     * @param columnIndex the column being queried
     * @return permission to edit status
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
