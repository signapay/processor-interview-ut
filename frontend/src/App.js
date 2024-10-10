import { Paper, Box, ThemeProvider, createTheme,CssBaseline, Container, Tabs, Tab, Typography} from '@mui/material';
import ReportsComponent from './components/reportsComponent';
import UploadComponent from './components/uploadComponent';
import GetCollectionsComponent from './components/getCollectionsComponent';
import BadTransactionsComponent from './components/badTransactionsComponent';
import { useState } from 'react';
import { resetSystem } from './apiService';


// Creating a MUI theme
const theme = createTheme();

// Renders the data based on the Tabpanel selected
function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    // Show content only if the current tab value matches the index
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        // box container
        <Box sx={{ p: 3 }}>
          {children}
        </Box>
      )}
    </div>
  );
}

function App() {
  const [refreshTrigger, setRefreshTrigger] = useState(false);
  const [tabValue, setTabValue] = useState(0);

  // Toggle refresh state when file is uploaded successfully
  const handleUploadSuccess = () => {
    setRefreshTrigger(prev => !prev);
  };

//  REMOVE REFRESHTRIGGER IF POSSIBLE
  const handleReset = async () => {
    try {
      // Calling the reset API endpoint
      const response = await resetSystem();  
      console.log(response.message);  
      setRefreshTrigger(prev => !prev);  // Trigger refresh to update UI
    } catch (error) {
      console.error("Failed to reset the system:", error);
    }
  };


  // Handle tab change event
  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container maxWidth="lg">
        <Box sx={{ my: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            Transaction Processor System
          </Typography>
          <Paper sx={{ mb: 2, p: 2 }}>
            <UploadComponent onUploadSuccess={handleUploadSuccess} onReset={handleReset} />
          </Paper>
          <Paper sx={{ width: '100%' }}>
            <Tabs value={tabValue} onChange={handleTabChange} aria-label="transaction data tabs">
              <Tab label="Account Reports" id="simple-tab-0" aria-controls="simple-tabpanel-0" />
              <Tab label="Get Collections" id="simple-tab-1" aria-controls="simple-tabpanel-1" />
              <Tab label="Bad Transactions" id="simple-tab-2" aria-controls="simple-tabpanel-2" />
            </Tabs>
            <TabPanel value={tabValue} index={0}>
              <ReportsComponent refreshTrigger={refreshTrigger} />
            </TabPanel>
            <TabPanel value={tabValue} index={1}>
              <GetCollectionsComponent refreshTrigger={refreshTrigger} />
            </TabPanel>
            <TabPanel value={tabValue} index={2}>
              <BadTransactionsComponent refreshTrigger={refreshTrigger} />
            </TabPanel>
          </Paper>
        </Box>
      </Container>
    </ThemeProvider>
  );
}
export default App