import React, { useState } from 'react';
import axios from 'axios';

const TransactionUploader = () => {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState('');

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleFileUpload = async () => {
    if (!file) {
      setMessage('Please select a file first');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await axios.post('/api/transactions/upload', formData);
      setMessage('File uploaded successfully');
    } catch (error) {
      setMessage('Failed to upload file');
    }
  };

  const handleReset = async () => {
    try {
      await axios.post('/api/reset');
      setMessage('System reset successfully');
    } catch (error) {
      setMessage('Failed to reset system');
    }
  };

  return (
    <div>
      <input type="file" onChange={handleFileChange} />
      <button onClick={handleFileUpload}>Upload File</button>
      <button onClick={handleReset}>Reset System</button>
      <p>{message}</p>
    </div>
  );
};

export default TransactionUploader;
