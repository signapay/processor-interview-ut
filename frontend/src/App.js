import React, { useState } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

function App() {
    const [file, setFile] = useState(null);
    const [reports, setReports] = useState(null);
    const [showReports, setShowReports] = useState(false); 

    const handleFileUpload = (e) => {
        setFile(e.target.files[0]);
    };

    const uploadFile = async () => { //Method that consists of Axios request to upload the user selected file.
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
            alert('File uploaded successfully, Click on Get Reports to view the reports');
        } catch (err) {
            console.error(err);
            alert('Error uploading file');
        }
    };

    const getReports = async () => { //Method to get the reports
        try {
            const res = await axios.get('http://localhost:5000/reports');
            setReports(res.data);
            setShowReports(true); 
        } catch (err) {
            console.error(err);
            alert('Error fetching reports');
        }
    };

    const resetSystem = async () => { // Method to reset the system
        try {
            await axios.post('http://localhost:5000/reset');
            alert('System reset successfully, You can upload the new file or View the reports of the same file again');
            setReports(null);  
            setShowReports(false); 
        } catch (err) {
            console.error(err);
            alert('Error resetting system');
        }
    };


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

            {showReports && reports && (
                <div>
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

                    <h2>Collections</h2>
                    {reports.collections.length > 0 ? (
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
                                    {reports.collections.map((txn, idx) => (
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
                        <p>No accounts in collections.</p>
                    )}

                    <h2>Bad Transactions</h2>
                    {reports.badTransactions.length > 0 ? (
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
                                    {reports.badTransactions.map((txn, idx) => (
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
