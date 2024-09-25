import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import java.awt.*;

public class GUI {
    private JFrame jframe = null;
    private StateManager state;

    public void initialize(Database db){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Log.log("Unable to support native look and feel. Using default appearance.");
        }

        if (jframe == null){
            // create jframe
            jframe = new JFrame("Interview Program Test");

            // create state manager
            state = new StateManager(db, jframe);

            //initialize jframe
            jframe.setSize(850,500);
            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //enable exit button

            // top pane
            JPanel top = new JPanel();
            top.add(Buttons.generateFileButton(state));
            top.add(Buttons.generateOpenButton(state));
            top.add(Buttons.generateClearRecordButton(state));
            top.add(Buttons.generateButtonLabel(state));

            // bottom pane
            JPanel bottom = new JPanel();
            JTable table = new JTable(new Table(state));
            table.setFillsViewportHeight(true);
            bottom.add(new JScrollPane(table));
            //jframe.add(new JScrollPane(table));

            // add split pane
            JSplitPane pane = generateSeparator(top, bottom);
            jframe.add(pane);

            // render jframe
            jframe.setVisible(true);
        }
    }

    private static JSplitPane generateSeparator(JPanel top, JPanel bottom){
        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
        pane.setOneTouchExpandable(true);

        // set minimum size
        Dimension min = new Dimension(100,50);
        top.setMinimumSize(min);
        bottom.setMinimumSize(min);

        // set starting proportions
        pane.setDividerLocation(200);

        return pane;
    }
}
