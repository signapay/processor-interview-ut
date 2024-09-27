import React from 'react';
import axios from 'axios';
import { API_ENDPOINTS } from '../constants/api';

const FileUpload = ({ setFile, fileInputRef }) => {

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleFileUpload = async () => {
    const formData = new FormData();
    formData.append('file', fileInputRef.current.files[0]);

    try {
      const response = await axios.post(API_ENDPOINTS.uploadFile, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      alert(response.data.message);
    } catch (error) {
      console.error('Error uploading file:', error);
      alert(error);
    }
  };

  return (
    <div>
      <input type="file" onChange={handleFileChange} ref={fileInputRef} />
      <button onClick={handleFileUpload}>Upload File</button>
    </div>
  );
};

export default FileUpload;
