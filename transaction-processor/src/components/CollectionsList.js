import React from 'react';

function CollectionsList({ collections }) {
    if (!collections.length) {
        return <p>No collections to display</p>;
    }

    return (
        <div>
            <h2>Collections</h2>
            <table>
                <thead>
                    <tr>
                        <th>Account Name</th>
                        <th>Card Number</th>
                        <th>Balance</th>
                    </tr>
                </thead>
                <tbody>
                    {collections.map((account, index) => (
                        <tr key={index}>
                            <td>{account.accountName}</td>
                            <td>{account.cardNumber}</td>
                            <td>{account.balance}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default CollectionsList;
