// UploadComponent.js
import React, { useState } from 'react';
import { uploadFile } from '../apiService';

const UploadComponent = ({ onUploadSuccess }) => {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleUpload = async () => {
    if (file) {
      try {
        const response = await uploadFile(file);
        setMessage(response.message);
        // Notify parent or other components of upload success
        if (onUploadSuccess) onUploadSuccess();  // Trigger re-fetch of reports
      } catch (error) {
        setMessage("File upload failed. Please try again.");
      }
    } else {
      setMessage("Please select a file to upload.");
    }
  };

  return (
    <div>
      <h3>Upload CSV File</h3>
      <input type="file" onChange={handleFileChange} />
      <button onClick={handleUpload}>Upload</button>
      <p>{message}</p>
    </div>
  );
};

export default UploadComponent;
