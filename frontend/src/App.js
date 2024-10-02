import React, { useState } from 'react';

function App() {
  const [file, setFile] = useState(null);
  const [reports, setReports] = useState(null);
  const [message, setMessage] = useState('');

  // Handle file upload
  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch('http://localhost:5000/upload', {
        method: 'POST',
        body: formData,
        credentials: 'include', // cookies
      });
      const result = await response.json();
      setMessage(result.message);
    } catch (error) {
      console.error('Error uploading file:', error);
      setMessage('Error uploading file');
    }
  };

  const handleReset = async () => {
    try {
      const response = await fetch('http://localhost:5000/reset', {
        method: 'POST',
        credentials: 'include',
      });
      const result = await response.json();
      setMessage(result.message);
  
      // Clear the local state to remove displayed data
      setReports(null);  // Clear reports
      setFile(null);     // Clear the uploaded file
  
      // Reset the file input field
      document.querySelector('input[type="file"]').value = ''; 
    } catch (error) {
      console.error('Error resetting system:', error);
      setMessage('Error resetting system');
    }
  };

  const fetchReports = async () => {
    try {
      const response = await fetch('http://localhost:5000/reports', {
        method: 'GET',
        credentials: 'include', 
      });
      const data = await response.json();
      setReports(data);
    } catch (error) {
      console.error('Error fetching reports:', error);
    }
  };

  return (
    <div className="App">
      <h1>Transaction Processor</h1>

      {/* File Upload */}
      <input type="file" onChange={handleFileChange} />
      <button onClick={handleUpload}>Upload File</button>

      {/* Fetch Reports */}
      <button onClick={fetchReports}>Get Reports</button>

      {/* Reset System */}
      <button onClick={handleReset}>Reset System</button>

      {/* Display Messages */}
      {message && <p>{message}</p>}

      {/* Display Reports */}
      {reports && (
        <div>
          <h2>Chart of Accounts</h2>
          <pre>{JSON.stringify(reports.chart_of_accounts, null, 2)}</pre>

          <h2>Collection List</h2>
          <pre>{JSON.stringify(reports.collection_list, null, 2)}</pre>

          <h2>Bad Transactions</h2>
          <pre>{JSON.stringify(reports.bad_transactions, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}

export default App;
