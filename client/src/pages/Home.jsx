import React from 'react';
import FileUpload from '../components/FileUpload';
import ResetButton from '../components/ResetButton';
import { processCSVData, clearStoredTransactions } from '../utils/api';

const Home = () => {
  const handleFileSubmit = async (file) => {
    try {
      const result = await processCSVData(file);
      console.log('Data processed:', result);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleReset = async () => {
    try {
      const result = await clearStoredTransactions();
      console.log('Data cleared:', result);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <div className="flex flex-col justify-center items-center w-screen h-screen bg-[#242424] text-white relative">
      <div className="absolute top-4 right-4">
        <ResetButton onReset={handleReset} />
      </div>

      <div className="flex justify-center items-center">
        <FileUpload onFileSubmit={handleFileSubmit} />
      </div>
    </div>
  );
};

export default Home;
