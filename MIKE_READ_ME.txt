<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>
Overview:
This project allows a user to summarize a csv file containing transaction entries. Viewing options include the following; a "View Summarizer" that displays the account name,
card number, and accrued total associated with the account name & card number; a "Send To Collections Viewer" that shows all the accrued transactions that have a negative
balance; finally the "Bad Transactions Viewer" shows the transactions that are incorrectly formatted or had an error during processing.

<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

It is composed of the following:
• (csvCatalog) - place any csv files in here that you would like to include in a report. Make sure it ends in ".csv" so that the program will recognize it and
    have it selectable from its csv selector screen.
• (frameNavigator) - is the program itself. This is where the program's java files/classes/logic is held. it can further be broken down into the following:
        ~ <Launcher.java> The system manager that initializes the program and allows navigation between the pages. it also holds commonly used functions like
            clearing file contents.
        ~ <HomeFrame.java> The home page of the program. When the program first launches, you are greeted with this screen. It gives you the option to either continue to
            the report summarizer or clear the history.
        ~ <CSVSelectorFrame.java> This page allows you to select all the csv files that you want to include in your report. Make sure you check the boxes of all the
            files you want to include and to click the "Add to Report" button, so that they will show up in the summary. There is also a "remove duplicates" checkbox that would
            remove transactions with identical values. This class also hold the logic that parses the selected files, adding validly formatted entries to the "history/holder.csv"
            file and flagging invalid entries into the "history/transactionErrors.csv" file.
        ~ <ReportNavigatorFrame.java> This page allows the user to select which report they want to see. This includes the "Summary", "Send to Collections", "Bad Transactions"
            viewers. You are also given the option to save the current report to history, so that you can access it later, or add more csv file entries to it.
        ~ <ViewSummaryFrame.java> Is the standard viewer that displays the account name, card number, and accrued transactions associated with it, into an indexed list.
            aside from the table, it also has a button that allows you to output that table contents to "output/summary.csv" for easy copy & pasting.
        ~ <SendToCollectionFrame.java> Shows the account name, card number, and balance of all entries that have accrued a negative total. Similar to the summary viewer,
            it has a button that outputs the table contents to "output/collections.csv" for easy copy & pasting.
        ~ <BadTransactionsFrame.java> Shows all entries that had an error occur during processing. It also shows the diagnosis of the error. Similar to the other viewers,
            it allows one to output the table contents to "output/badTransactions.csv" for easy copy & pasting.
• (history) - The storage method used by the program. The user is not meant to tamper with this directory. The files "holder.csv" and "transactionError.csv" are meant
    to temporarily hold information that is used to populate tables. The "errorHistory.csv" and "history.csv" files are meant for holding information saved by the user
    until they clear the history from the Home Frame.
• (img) - used to store images referenced in the program.
• (output) - as mentioned earlier, this is where output csv files are held. This allows users to easily copy and paste data from the report tables.

<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

The program was packaged into a jar file for ease of use.

STEPS TO RUN THE PROGRAM:
1) Have a JDK installed on your computer. (this program was tested using openJDK 23 on my Mac laptop running macOS Sonoma 14.4.1)
2) Open a bash shell
3) Navigate to the "processor-interview-ut" directory.
4) Run the command "java -jar out/artifacts/processor_interview_ut_jar/processor-interview-ut.jar".
5) The Program should launch immediately.
6) Exit/Quit the program at any time by clicking the 'x' in the program window.

