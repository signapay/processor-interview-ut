package GUI;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Class for custom display model
 */
public class DisplayTableModel extends AbstractTableModel {
    // record set
    private ArrayList<String[]> records = new ArrayList<>();

    // name set
    private String[] titles = new String[]{};

    /**
     * Constructor
     */
    public DisplayTableModel(){}

    /**
     * Update table contents to reflect new content
     * @param records arraylist of rows given as arrays of strings
     * @param titles array of titles to associate with rows
     */
    public void setRecords(ArrayList<String[]> records, String[] titles){
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
       return Math.max(1,titles.length);
    }

    /**
     * Column name retrieval
     * @param columnIndex  the column being queried
     * @return name of column
     */
    @Override
    public String getColumnName(int columnIndex) {
        //return "title";
        if (titles.length == 0) return "[nothing to display]";
        return titles[columnIndex];
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
        return records.get(rowIndex)[columnIndex];
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
