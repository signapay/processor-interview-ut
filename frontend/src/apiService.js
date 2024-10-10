import axios from 'axios';

const API_URL = "http://localhost:5000";  // Flask backend URL

// Upload a CSV file
export const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append("file", file);
  const response = await axios.post(`${API_URL}/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;  // Return the parsed data directly
};

// Fetch the reports
export const getReports = async () => {
  const response = await axios.get(`${API_URL}/reports`);
  return response.data;
};

// Fetch the collections
export const getCollections = async () => {
  const response = await axios.get(`${API_URL}/collections`);
  return response.data;
};

// Fetch the bad transactions for review
export const getBadTransactions = async () => {
  const response = await axios.get(`${API_URL}/bad-transactions`);
  return response.data;
};

// Reset the system 
export const resetSystem = async () => {
  const response = await axios.post(`${API_URL}/reset`);
  return response.data;
};
