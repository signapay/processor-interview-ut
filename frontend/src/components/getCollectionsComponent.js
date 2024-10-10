import React, { useState, useEffect } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Typography, Box, TablePagination } from '@mui/material';
import { getCollections } from '../apiService';


function GetCollectionsComponent({ refreshTrigger }) {
  // state to store collections
  const [collections, setCollections] = useState([]);
  // State for current page
  const [page, setPage] = useState(0); 
  const rowsPerPage = 25

  useEffect(() => {
    // Fetch bad transactions from the API whenever refreshTrigger changes
    const fetchCollections = async () => {
      const data = await getCollections();
      setCollections(data);
    };
    fetchCollections();
  }, [refreshTrigger]);

  // Handle page change
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  console.log(collections);
  return (
<Box>
      {collections.length === 0 ? (
        // Display a message if no data is available
        <Typography variant="h7" gutterBottom>
          Please upload the file
        </Typography>
      ) : (
        <>
          <Typography variant="h5" gutterBottom>
            Collections
          </Typography>
          <TableContainer component={Paper}>
            <Table aria-label="get collections table">
              <TableHead>
                <TableRow>
                  <TableCell align="left" sx={{ fontWeight: 'bold'}}>Account Name</TableCell>
                  <TableCell align="left" sx={{ fontWeight: 'bold'}}>Card Number</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold'}}>Balance</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {collections
                  // Slicing the data to display only the current page's rows
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((transaction, index) => (
                    <TableRow key={index}>
                      <TableCell component="th" scope="row">
                        {transaction["Account Name"]} 
                      </TableCell>
                      <TableCell align="left">
                        {transaction["Card Number"]} 
                      </TableCell>
                      <TableCell align="right">
                        ${parseFloat(transaction["Balance"]).toFixed(2)} 
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
            count={collections.length} 
            rowsPerPage={rowsPerPage} 
            page={page} 
            onPageChange={handleChangePage} 
          />
        </>
      )}
    </Box>
  );
}

export default GetCollectionsComponent;