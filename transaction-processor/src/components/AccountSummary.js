import React from 'react';

function AccountSummary({ accounts }) {
    if (!Object.keys(accounts).length) {
        return <p>No accounts to display</p>;
    }

    return (
        <div className="account-summary-container">
            <h2>Account Summary</h2>
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
                    {Object.entries(accounts).map(([accountName, cards]) => (
                        cards.map(card => (
                            <tr key={card.cardNumber}>
                                <td>{accountName}</td>
                                <td>{card.cardNumber}</td>
                                <td>{card.transactionAmount}</td>
                                <td>{card.transactionType}</td>
                                <td>{card.description}</td>
                                <td>{card.targetCardNumber}</td>
                            </tr>
                        ))
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default AccountSummary;
