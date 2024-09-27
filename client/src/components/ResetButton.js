import React from 'react';
import axios from 'axios';
import { API_ENDPOINTS } from '../constants/api';

const ResetButton = ({ setReport, setFile, fileInputRef }) => {
  const handleReset = async () => {
    try {
      const response = await axios.post(API_ENDPOINTS.resetSystem);
      alert(response.data.message);
      setReport(null);
      setFile(null);

      // Clear the file input field
      if (fileInputRef.current) {
        fileInputRef.current.value = '';  // Reset file input to an empty state
      }
    } catch (error) {
      console.error('Error resetting system:', error);
    }
  };

  return <button onClick={handleReset}>Reset System</button>;
};

export default ResetButton;
