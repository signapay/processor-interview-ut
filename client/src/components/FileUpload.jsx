import React, { useState } from 'react';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const FileUpload = ({ onFileSubmit }) => {
  const [file, setFile] = useState(null);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    
    if (selectedFile && selectedFile.type !== 'text/csv') {
      toast.error('Please upload a CSV file.');
      setFile(null);
    } else {
      setFile(selectedFile);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (file) {
      onFileSubmit(file);
      toast.error('File Uploaded Successfully.');
    } else {
      toast.error('No File Uploded');
    }
  };

  return (
    <div className="p-4 flex justify-center items-center">
      <form onSubmit={handleSubmit} className="flex flex-col items-center">
        <input
          type="file"
          onChange={handleFileChange}
          className="mb-4 block w-full text-sm text-gray-900 bg-gray-50 border border-gray-300 rounded-lg cursor-pointer"
        />
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
          Submit
        </button>
      </form>

      <ToastContainer />
    </div>
  );
};

export default FileUpload;
