import React, { useState } from 'react';
import { uploadFile, getReports, resetSystem } from '../apiService';

function TransactionUploader() {
  const [file, setFile] = useState(null);
  const [reports, setReports] = useState([]);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (file) {
      const response = await uploadFile(file);
      console.log(response);
      alert(response.message || 'File uploaded successfully!');
    }
  };

  const handleFetchReports = async () => {
    const reports = await getReports();
    setReports(reports);
  };

  const handleReset = async () => {
    await resetSystem();
    alert('System reset successfully!');
    setReports([]);
  };

  return (
    <div>
      <h1>Transaction Processor</h1>
      <input type="file" onChange={handleFileChange} />
      <button onClick={handleUpload}>Upload File</button>
      <button onClick={handleFetchReports}>Fetch Reports</button>
      <button onClick={handleReset}>Reset System</button>

      <h2>Reports</h2>
      <ul>
        {reports.map((report, index) => (
          <li key={index}>
            {report['Account Name']} - {report['Card Number']} - Balance: {report.Balance}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransactionUploader;
