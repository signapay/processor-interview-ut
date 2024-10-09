import './App.css';
import ReportsComponent from './components/reportsComponent';
import UploadComponent from './components/uploadComponent';
import GetCollectionsComponent from './components/getCollectionsComponent';
import BadTransactionsComponent from './components/badTransactionsComponent';
import { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
// import { Box, Grid } from '@mui/material';


function App() {
  const [refreshTrigger, setRefreshTrigger] = useState(false);
   // Function to toggle the trigger to refresh reports
   const handleUploadSuccess = () => {
    setRefreshTrigger(!refreshTrigger);
  };
  return (
    <div className="App">
      <UploadComponent onUploadSuccess={handleUploadSuccess} />      
      <ReportsComponent refreshTrigger={refreshTrigger} />
    </div>
  );
}

export default App;
