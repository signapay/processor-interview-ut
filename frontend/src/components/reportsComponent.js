import React, { useState, useEffect } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Typography, Box, TablePagination } from '@mui/material';
import { getReports } from '../apiService';

const ReportsComponent = ({refreshTrigger}) => {
  // state to store reports
  const [reports, setReports] = useState([]);
  // State for current page
  const [page, setPage] = useState(0);
  const rowsPerPage = 25

  useEffect(() => {
    // Fetch bad transactions from the API whenever refreshTrigger changes
    const fetchReports = async () => {
      const data = await getReports();
      setReports(data);
    };
    fetchReports();
  }, [refreshTrigger]);

   // Calculate total rows from reports
   const rows = [];
   Object.keys(reports).forEach((account) => {
     Object.entries(reports[account]).forEach(([card, balance]) => {
       rows.push({ account, card, balance });
     });
   });

   // Handle page change
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  console.log(reports)
  return (
    <Box>
      {rows.length === 0 ? (
        // Display a message if no data is available
        <Typography variant="h7" gutterBottom>
          Please upload the file
        </Typography>
      ) : (
        <>
          <Typography variant="h5" gutterBottom>
            Account Reports
          </Typography>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell align="left" sx={{ fontWeight: 'bold'}}>Account Name</TableCell>
                  <TableCell align="left" sx={{ fontWeight: 'bold'}}>Card Number</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold'}}>Balance</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {rows
                  // Slicing the data to display only the current page's rows
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((row, index) => (
                    <TableRow key={`${row.account}-${row.card}-${index}`}>
                      <TableCell align="left">{row.account}</TableCell>
                      <TableCell align="left">{row.card}</TableCell>
                      <TableCell align="right">
                        ${parseFloat(row.balance).toFixed(2)}
                      </TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          </TableContainer>
          {/* Pagination Component */}
          <TablePagination
            rowsPerPageOptions={[]} 
            component="div"
            count={rows.length} 
            rowsPerPage={rowsPerPage} 
            page={page} 
            onPageChange={handleChangePage} 
          />
        </>
      )}
    </Box>
  );
};

export default ReportsComponent;
