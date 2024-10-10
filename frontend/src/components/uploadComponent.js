import React, { useState } from 'react';
import { Button, Alert, AlertTitle, CircularProgress, Box} from '@mui/material';
import { uploadFile } from '../apiService';

function UploadComponent({ onUploadSuccess, onReset }) {
  // state to store the file
  const [file, setFile] = useState(null);
  // state to store the msg for file upload
  const [message, setMessage] = useState("");
  // state for file upload process
  const [isUploading, setIsUploading] = useState(false);

  // Handle file input change
  const handleFileChange = (event) => {
    if (event.target.files) {
      setFile(event.target.files[0]);
    }
  };

  // Handle upload button click
  const handleUpload = async () => {
    if (file) {
      setIsUploading(true);
      try {
        const response = await uploadFile(file);
        setMessage(response.message);
        onUploadSuccess();
      } catch (error) {
        setMessage("File upload failed. Please try again.");
      } finally {
        setIsUploading(false);
      }
    } else {
      setMessage("Please select a file to upload.");
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
        <Button variant="contained" component="label">
          Choose File
          <input type="file" accept=".csv" hidden onChange={handleFileChange} />
        </Button>
        <Button variant="contained" onClick={handleUpload} disabled={isUploading}>
          {isUploading ? <CircularProgress size={24} /> : 'Upload'}
        </Button>
        <Button variant="outlined" onClick={onReset}>Reset System</Button>
      </Box>
      {file && <Box sx={{ mb: 2 }}>Selected file: {file.name}</Box>}
      {message && (
        <Alert severity={message.includes('failed') ? 'error' : 'success'}>
          <AlertTitle>Upload Status</AlertTitle>
          {message}
        </Alert>
      )}
    </Box>
  );
}
export default UploadComponent