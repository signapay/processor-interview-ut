import React, { useState, useRef } from 'react';
import FileUpload from '../components/FileUpload';
import AccountSummary from '../components/AccountSummary';
import CollectionsList from '../components/CollectionsList';
import BadTransactionsList from '../components/BadTransactionsList';
import '../styles/App.css';

function TransactionProcessor() {
    const [accounts, setAccounts] = useState({});
    const [badTransactions, setBadTransactions] = useState([]);
    const [collections, setCollections] = useState([]);
    const fileInputRef = useRef(null); // Create a ref to access the file input

    const handleFileUpload = (fileData) => {
        const updatedAccounts = { ...accounts }; // Preserve existing accounts
        const badTrans = [...badTransactions];   // Preserve existing bad transactions
        const collectList = [...collections];    // Preserve existing collections

        fileData.forEach((transaction) => {
            const { AccountName, CardNumber, TransactionAmount, TransactionType, Description, TargetCardNumber } = transaction;

            const isBadTransaction = 
                !AccountName || 
                !CardNumber || 
                TransactionAmount === undefined || 
                !TransactionType;

            if (isBadTransaction) {
                badTrans.push(transaction);
            } else {
                if (!updatedAccounts[AccountName]) {
                    updatedAccounts[AccountName] = [];
                }

                updatedAccounts[AccountName].push({
                    cardNumber: CardNumber,
                    transactionAmount: parseFloat(TransactionAmount),
                    transactionType: TransactionType,
                    description: Description,
                    targetCardNumber: TargetCardNumber || 'N/A',
                });

                if (parseFloat(TransactionAmount) < 0) {
                    collectList.push({
                        accountName: AccountName,
                        cardNumber: CardNumber,
                        balance: parseFloat(TransactionAmount),
                    });
                }
            }
        });

        setAccounts(updatedAccounts);
        setCollections(collectList);
        setBadTransactions(badTrans);
    };

    const handleReset = () => {
        // Reset the state
        setAccounts({});
        setCollections([]);
        setBadTransactions([]);

        // Reset the file input element
        if (fileInputRef.current) {
            fileInputRef.current.value = '';  // Clear the file input value
        }
    };

    return (
        <div className="transaction-processor-container">
            <h1>Transaction Processor</h1>
            <div className="file-upload-section">
                <FileUpload onUpload={handleFileUpload} fileInputRef={fileInputRef} />  {/* Pass the ref */}
                <button className="reset-button" onClick={handleReset}>Reset System</button>
            </div>
            <div className="report-section">
                <AccountSummary accounts={accounts} />
                <CollectionsList collections={collections} />
                <BadTransactionsList badTransactions={badTransactions} />
            </div>
        </div>
    );
}

export default TransactionProcessor;
