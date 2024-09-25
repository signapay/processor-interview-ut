import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.ArrayList;

public class TablePanel extends JScrollPane {
    private JTable table;
    private Table tableModel;

    //constructor
    private TablePanel(JTable table){
        super(table);
    }

    public static TablePanel make(StateManager state){
        // construction chain
        Table tableModel = new Table();
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        TablePanel tablePanel = new TablePanel(table);

        // register internal references
        tablePanel.table = table;
        tablePanel.tableModel = tableModel;

        //register to state
        state.registerTablePanel(tablePanel);

        return tablePanel;
    }


    // update data
    public void updateData(ArrayList<Transaction> list){
        if (list != null) list = (ArrayList<Transaction>) list.clone(); // prevent mutation of original data
        tableModel.setRecords(list);
    }

    // redraw
    public void updateGui(){
        table.revalidate();
        this.revalidate();
    }
}
