import React, { useState } from 'react';
import axios from 'axios';

const App = () => {
  const [file, setFile] = useState(null);
  const [report, setReport] = useState(null);

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleFileUpload = async () => {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await axios.post('http://localhost:5000/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      alert(response.data.message);
    } catch (error) {
      console.error('Error uploading file:', error);
      alert(error);
    }
  };

  const handleReset = () => {
    setReport(false);
  };

  const handleFetchReport = async () => {
    try {
      const response = await axios.get('http://localhost:5000/report');
      setReport(response.data);
    } catch (error) {
      console.error('Error fetching report:', error);
    }
  };

  const renderAccountsTable = () => {
    return (
      <table border="1">
        <thead>
          <tr>
            <th>Account Name</th>
            <th>Card Number</th>
            <th>Amount</th>
          </tr>
        </thead>
        <tbody>
          {Object.entries(report.accounts).map(([accountName, accountData]) => (
            Object.entries(accountData.cards).map(([cardNumber, cardData]) => (
              <tr key={`${accountName}-${cardNumber}`}>
                <td>{accountName}</td>
                <td>{cardNumber}</td>
                <td>{cardData.amount}</td>
              </tr>
            ))
          ))}
        </tbody>
      </table>
    );
  };

  const renderCollectionsTable = () => {
    return (
      <table border="1">
        <thead>
          <tr>
            <th>Account Name</th>
            <th>Card Number</th>
            <th>Amount</th>
          </tr>
        </thead>
        <tbody>
          {report.collections.map((transaction, index) => (
            <tr key={index}>
              <td>{transaction['Account Name']}</td>
              <td>{transaction['Card Number']}</td>
              <td>{transaction['Amount']}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  };

  const renderBadTransactionsTable = () => {
    return (
      <table border="1">
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
          {report.badTransactions.map((transaction, index) => (
            <tr key={index}>
              <td>{transaction['Account Name'] || 'N/A'}</td>
              <td>{transaction['Card Number'] || 'N/A'}</td>
              <td>{transaction['Transaction Amount'] || 'N/A'}</td>
              <td>{transaction['Transaction Type'] || 'N/A'}</td>
              <td>{transaction['Description'] || 'N/A'}</td>
              <td>{transaction['Target Card Number'] || 'N/A'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  };

  return (
    <div>
      <h1>Transaction Processor</h1>
      
      <input type="file" onChange={handleFileChange} />
      <button onClick={handleFileUpload}>Upload File</button>
      <button onClick={handleReset}>Reset System</button>
      <button onClick={handleFetchReport}>Show Report</button>

      {report && (
        <div>
          <h2>Chart of Accounts</h2>
          {renderAccountsTable()}

          <h2>Accounts for Collections</h2>
          {renderCollectionsTable()}

          <h2>Bad Transactions</h2>
          {renderBadTransactionsTable()}
        </div>
      )}
    </div>
  );
};

export default App;
