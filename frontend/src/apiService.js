import axios from 'axios';

const API_URL = "http://localhost:5000";  // Flask backend URL

// Define your API key
const API_KEY = "mysecretapikey";

// Function to configure headers with API key
const getAuthHeaders = () => ({
  'x-api-key': API_KEY,  
});

// Upload a CSV file
export const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append("file", file);
  const response = await axios.post(`${API_URL}/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      ...getAuthHeaders(), 
    },
  });
  return response.data;  
};

// Fetch the reports
export const getReports = async () => {
  const response = await axios.get(`${API_URL}/reports`,{
    headers: getAuthHeaders(), 
  });
  return response.data;
};

// Fetch the collections
export const getCollections = async () => {
  const response = await axios.get(`${API_URL}/collections`,{
    headers: getAuthHeaders(), 
  });
  return response.data;
};

// Fetch the bad transactions for review
export const getBadTransactions = async () => {
  const response = await axios.get(`${API_URL}/bad-transactions`,{
    headers: getAuthHeaders(), 
  });
  return response.data;
};

// Reset the system 
export const resetSystem = async () => {
  const response = await axios.post(`${API_URL}/reset`, {},{
    headers: getAuthHeaders(), 
  });
  return response.data;
};
