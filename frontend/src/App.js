import React, { useState } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

function App() {
    const [file, setFile] = useState(null);
    const [reports, setReports] = useState(null);
    const [showReports, setShowReports] = useState(false); // Control showing reports

    const handleFileUpload = (e) => {
        setFile(e.target.files[0]);
    };

    const uploadFile = async () => {
        if (!file) {
            alert('Please select a file to upload.');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            await axios.post('http://localhost:5000/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            alert('File uploaded successfully');
        } catch (err) {
            console.error(err);
            alert('Error uploading file');
        }
    };

    const getReports = async () => {
        try {
            const res = await axios.get('http://localhost:5000/reports');
            setReports(res.data);
            setShowReports(true); // Show reports when data is fetched
        } catch (err) {
            console.error(err);
            alert('Error fetching reports');
        }
    };

    const resetSystem = async () => {
        try {
            await axios.post('http://localhost:5000/reset');
            alert('System reset successfully');
            setReports(null);  // Clear the reports
            setShowReports(false); // Hide reports after reset
        } catch (err) {
            console.error(err);
            alert('Error resetting system');
        }
    };

    // Helper function to filter collections and bad transactions
    const categorizeReports = () => {
        const collections = [];
        const badTransactions = [];

        (reports.transactions || []).forEach((txn) => {
            // Check if it's a bad transaction
            if (!txn.accountName || !txn.cardNumber || isNaN(txn.transactionAmount) || !txn.transactionType) {
                badTransactions.push(txn);
            } else if (txn.transactionAmount < 0) {
                // Add to collections if balance is less than 0
                collections.push(txn);
            }
        });

        return { collections, badTransactions };
    };

    const { collections = [], badTransactions = [] } = reports ? categorizeReports() : {};

    return (
        <div className="App container mt-5">
            <h1 className="text-center mb-4">Transaction Processor</h1>

            <div className="mb-3">
                <input type="file" onChange={handleFileUpload} accept=".csv" className="form-control-file" />
            </div>
            <div className="btn-group mb-4" role="group">
                <button onClick={uploadFile} className="btn btn-primary">Upload Transactions</button>
                <button onClick={getReports} className="btn btn-success">Get Reports</button>
                <button onClick={resetSystem} className="btn btn-danger">Reset System</button>
            </div>

            {/* Show reports only when "Get Reports" button is clicked */}
            {showReports && reports && (
                <div>
                    {/* Accounts Report */}
                    <h2>Chart of Accounts</h2>
                    <div className="table-responsive">
                        <table className="table table-bordered table-striped">
                            <thead className="thead-dark">
                                <tr>
                                    <th scope="col">Account Name</th>
                                    <th scope="col">Card Number</th>
                                    <th scope="col">Transaction Amount</th>
                                    <th scope="col">Transaction Type</th>
                                    <th scope="col">Description</th>
                                    <th scope="col">Target Card Number</th>
                                </tr>
                            </thead>
                            <tbody>
                                {(reports.transactions || []).map((txn, idx) => (
                                    <tr key={idx}>
                                        <td>{txn.accountName || 'N/A'}</td>
                                        <td>{txn.cardNumber || 'N/A'}</td>
                                        <td>{txn.transactionAmount || 'N/A'}</td>
                                        <td>{txn.transactionType || 'N/A'}</td>
                                        <td>{txn.description || 'N/A'}</td>
                                        <td>{txn.targetCardNumber || 'N/A'}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    {/* Collections Report */}
                    <h2>Collections</h2>
                    {collections.length > 0 ? (
                        <div className="table-responsive">
                            <table className="table table-bordered table-striped">
                                <thead className="thead-dark">
                                    <tr>
                                        <th scope="col">Account Name</th>
                                        <th scope="col">Card Number</th>
                                        <th scope="col">Balance</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {collections.map((txn, idx) => (
                                        <tr key={idx}>
                                            <td>{txn.accountName || 'N/A'}</td>
                                            <td>{txn.cardNumber || 'N/A'}</td>
                                            <td>{txn.transactionAmount || 'N/A'}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <p>No accounts in collections.</p>
                    )}

                    {/* Bad Transactions Report */}
                    <h2>Bad Transactions</h2>
                    {badTransactions.length > 0 ? (
                        <div className="table-responsive">
                            <table className="table table-bordered table-striped">
                                <thead className="thead-dark">
                                    <tr>
                                        <th scope="col">Account Name</th>
                                        <th scope="col">Card Number</th>
                                        <th scope="col">Transaction Amount</th>
                                        <th scope="col">Transaction Type</th>
                                        <th scope="col">Description</th>
                                        <th scope="col">Target Card Number</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {badTransactions.map((txn, idx) => (
                                        <tr key={idx}>
                                            <td>{txn.accountName || 'N/A'}</td>
                                            <td>{txn.cardNumber || 'N/A'}</td>
                                            <td>{txn.transactionAmount || 'N/A'}</td>
                                            <td>{txn.transactionType || 'N/A'}</td>
                                            <td>{txn.description || 'N/A'}</td>
                                            <td>{txn.targetCardNumber || 'N/A'}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <p>No bad transactions.</p>
                    )}
                </div>
            )}
        </div>
    );
}

export default App;
