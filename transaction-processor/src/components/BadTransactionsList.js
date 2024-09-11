import React from 'react';

function BadTransactionsList({ badTransactions }) {
    if (!badTransactions.length) {
        return <p>No bad transactions found.</p>;
    }

    return (
        <div>
            <h2>Bad Transactions</h2>
            <table>
                <thead>
                    <tr>
                        <th>Account Name</th>
                        <th>Card Number</th>
                        <th>Transaction Amount</th>
                        <th>Transaction Type</th>
                        <th>Description</th>
                        <th>Target Card Number</th>
                    </tr>
                </thead>
                <tbody>
                    {badTransactions.map((transaction, index) => (
                        <tr key={index}>
                            <td>{transaction.AccountName || 'Missing Account Name'}</td>
                            <td>{transaction.CardNumber || 'Missing Card Number'}</td>
                            <td>{transaction.TransactionAmount || 'Missing Amount'}</td>
                            <td>{transaction.TransactionType || 'Missing Transaction Type'}</td>
                            <td>{transaction.Description || 'Missing Description'}</td>
                            <td>{transaction.TargetCardNumber || 'N/A'}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default BadTransactionsList;
