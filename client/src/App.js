import React, { useState, useRef } from 'react';
import axios from 'axios';
import FileUpload from './components/FileUpload';
import ResetButton from './components/ResetButton';
import ReportTable from './components/ReportTable';
import { API_ENDPOINTS } from './constants/api';
import { ACCOUNT_TABLE_HEADERS, COLLECTIONS_TABLE_HEADERS, BAD_TRANSACTIONS_TABLE_HEADERS } from './constants/tableHeaders';
import './App.css'; // Import CSS file for styles

const App = () => {
  const [file, setFile] = useState(null);
  const [report, setReport] = useState(null);
  const fileInputRef = useRef(null);  // Create the file input ref here

  const handleFetchReport = async () => {
    try {
      const response = await axios.get(API_ENDPOINTS.fetchReport);
      setReport(response.data);
    } catch (error) {
      console.error('Error fetching report:', error);
    }
  };

  return (
    <div className="container">
      <h1>Transaction Processor</h1>
      
      <div className="button-group">
        <div className="button-item">
          <label>Upload a CSV file:</label>
          <FileUpload setFile={setFile} fileInputRef={fileInputRef} />
        </div>

        <div className="button-item">
          <label>Generate Report:</label>
          <button className="btn-fetch-report" onClick={handleFetchReport}>Show Report</button>
        </div>
        
        <div className="button-item">
          <label>Reset the System:</label>
          <ResetButton setReport={setReport} setFile={setFile} fileInputRef={fileInputRef} />
        </div>
      
      </div>

      {report && (
        <div>
          <h2>Chart of Accounts</h2>
          <ReportTable report={report} headers={ACCOUNT_TABLE_HEADERS} dataKey="accounts" />

          <h2>Accounts for Collections</h2>
          <ReportTable report={report} headers={COLLECTIONS_TABLE_HEADERS} dataKey="collections" />

          <h2>Bad Transactions</h2>
          <ReportTable report={report} headers={BAD_TRANSACTIONS_TABLE_HEADERS} dataKey="badTransactions" />
        </div>
      )}
    </div>
  );
};

export default App;
