import axios from 'axios';

//As I cannot send .env to signapay. I am storing the key in the file. 
const apiKey = '0DvG1LKgEs7Y0RBX';

export const processCSVData = async (file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);

    const response = await axios.post('http://localhost:5021/api/routes/transactions/upload-transactions', formData, {
      headers: {
        'api-key': apiKey,
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  } catch (error) {
    console.error('Error processing CSV:', error);
    throw error;
  }
};

export const clearStoredTransactions = async () => {
  try {
    const response = await axios.delete('http://localhost:5021/api/routes/transactions/clear-transactions', {
        headers: {
            'api-key': apiKey,
            'Content-Type': 'multipart/form-data',
        },
    });
    return response.data;
  } catch (error) {
    console.error('Error clearing transactions:', error);
    throw error;
  }
};

export const getAccounts = async () => {
    try {
      const response = await axios.get('http://localhost:5021/api/routes/transactions/get-accounts', {
        headers: {
          'api-key': apiKey,
          'Content-Type': 'application/json',
        },
      });
      return response.data.accounts;
    } catch (error) {
      console.error('Error retrieving accounts:', error);
      throw error;
    }
  };

  export const getCollections = async () => {
    try {
      const response = await axios.get('http://localhost:5021/api/routes/transactions/get-collections', {
        headers: {
          'api-key': apiKey,
          'Content-Type': 'application/json',
        },
      });
      return response.data.collections;
    } catch (error) {
      console.error('Error retrieving collections:', error);
      throw error;
    }
  };

  export const getBadTransactions = async () => {
    try {
        const response = await axios.get('http://localhost:5021/api/routes/transactions/get-bad-transactions', {
            headers: {
                'api-key': apiKey,
                'Content-Type': 'application/json',
            },
        });
        return response.data.badTransactions;
    } catch (error) {
        console.error('Error retrieving bad transactions:', error);
        throw error;
    }
};

  
  
  
