package frameNavigator;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class Launcher {
    private HomeFrame homeFrame;
    private CSVSelectorFrame csvSelectorFrame;
    private ReportNavigatorFrame reportNavigatorFrame;
    private ViewSummaryFrame viewSummaryFrame;
    private SendToCollectionsFrame sendToCollectionsFrame;
    private BadTransactionsFrame badTransactionsFrame;

    public Launcher() {
        homeFrame = new HomeFrame(this);
        csvSelectorFrame = new CSVSelectorFrame(this);
        reportNavigatorFrame = new ReportNavigatorFrame(this);
        viewSummaryFrame = new ViewSummaryFrame(this);
        sendToCollectionsFrame = new SendToCollectionsFrame(this);
        badTransactionsFrame = new BadTransactionsFrame(this);
        homeFrame.setVisible(true);
    }

     protected void showHomeFrame() {
         hideAllFrames();
         homeFrame = new HomeFrame(this);
         homeFrame.setVisible(true);
    }

    protected void showCSVSelectorFrame() {
        hideAllFrames();
        csvSelectorFrame = new CSVSelectorFrame(this);
        csvSelectorFrame.setVisible(true);
    }

    protected void showReportNavigatorFrame(){
        hideAllFrames();
        reportNavigatorFrame = new ReportNavigatorFrame(this);
        reportNavigatorFrame.setVisible(true);
    }
    protected void showViewSummaryFrame(){
        hideAllFrames();
        viewSummaryFrame = new ViewSummaryFrame(this);
        viewSummaryFrame.setVisible(true);
    }
    protected void showSendToCollectionsFrame(){
        hideAllFrames();
        sendToCollectionsFrame = new SendToCollectionsFrame(this);
        sendToCollectionsFrame.setVisible(true);
    }
    protected void showBadTransactionsFrame(){
        hideAllFrames();
        badTransactionsFrame = new BadTransactionsFrame(this);
        badTransactionsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Launcher::new);
    }
    protected void eraseFile(String filePath){
        try{
            Files.write(Paths.get(filePath), Collections.emptyList());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    private void hideAllFrames(){
        homeFrame.setVisible(false);
        csvSelectorFrame.setVisible(false);
        reportNavigatorFrame.setVisible(false);
        viewSummaryFrame.setVisible(false);
        sendToCollectionsFrame.setVisible(false);
        badTransactionsFrame.setVisible(false);
    }

}
