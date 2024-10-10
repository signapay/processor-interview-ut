import React, { useState, useEffect } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Typography, Box, TablePagination } from '@mui/material';
import { getBadTransactions } from '../apiService';


function BadTransactionsComponent({ refreshTrigger }) {
  // state to store bad transaction data
  const [badTransactions, setbadTransactions] = useState([]);
  // State for current page
  const [page, setPage] = useState(0); 
  const rowsPerPage = 25

  useEffect(() => {
    // Fetch bad transactions from the API whenever refreshTrigger changes
    const fetchBadTransactions = async () => {
      const data = await getBadTransactions();
      setbadTransactions(data);
    };
    fetchBadTransactions();
  }, [refreshTrigger]);
  
  // Handle page change
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  console.log(badTransactions);
  return (
    <Box>
      {badTransactions.length === 0 ? (
        // Display a message if no data is available
        <Typography variant="h7" gutterBottom>
          Please upload the file
        </Typography>
      ) : (
        <>
          <Typography variant="h5" gutterBottom>
            Bad Transactions
          </Typography>
          <TableContainer component={Paper}>
            <Table aria-label="bad transactions table">
              <TableHead>
                <TableRow>
                  <TableCell align="left" sx={{ fontWeight: 'bold'}}>Account Name</TableCell>
                  <TableCell align="left" sx={{ fontWeight: 'bold'}}>Card Number</TableCell>
                  <TableCell align="left" sx={{ fontWeight: 'bold'}}>Description</TableCell>
                  <TableCell align="left" sx={{ fontWeight: 'bold'}}>Target Card Number</TableCell>
                  <TableCell align="left" sx={{ fontWeight: 'bold'}}>Transaction Amount</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold'}}>Transaction Type</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {badTransactions
                  // Slicing the data to display only the current page's rows
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage) 
                  .map((transaction, index) => (
                    <TableRow key={index}>
                      <TableCell component="th" scope="row">
                        {transaction["Account Name"]}
                      </TableCell>
                      <TableCell align="left" >
                        {transaction["Card Number"]} 
                      </TableCell>
                      <TableCell align="left" >
                        {transaction["Description"]} 
                      </TableCell>
                      <TableCell align="left" >
                        {transaction["Target Card Number"] ? transaction["Target Card Number"] : "-"}
                      </TableCell>
                      <TableCell align="left" >
                        ${parseFloat(transaction["Transaction Amount"]).toFixed(2)} 
                      </TableCell>
                      <TableCell align="right">
                        {transaction["Transaction Type"]} 
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
            count={badTransactions.length} 
            rowsPerPage={rowsPerPage} 
            page={page} 
            onPageChange={handleChangePage}
          />
        </>
      )}
    </Box>
  );
}

export default BadTransactionsComponent;