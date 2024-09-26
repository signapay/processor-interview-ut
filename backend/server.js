const express = require('express');
const multer = require('multer');
const csv = require('csv-parser');
const fs = require('fs');
const cors = require('cors');

// Initialize Express App
const app = express();

// Enable CORS
app.use(cors());

// Configure Multer for file uploads
const upload = multer({ dest: 'uploads/' });

// Manually set proper CSV headers to avoid misinterpretation of the first row
const properHeaders = ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number'];

// In-memory store for transactions and uploaded file names
let allTransactions = [];
let uploadedFiles = [];  // List of all uploaded file names

// Helper function to calculate balances based on transaction type, including transfers
function calculateBalances(transactions) {
  const balances = {};
  const negativeBalances = []; // To store accounts that have negative balance

  transactions.forEach((tx) => {
    const { customer, cardNumber, amount, type, targetCardNumber } = tx;

    // Ensure the initiating customer's card balance is initialized
    if (!balances[customer]) {
      balances[customer] = {};
    }

    if (!balances[customer][cardNumber]) {
      balances[customer][cardNumber] = 0;
    }

    // Handle Credit and Debit types
    if (type === 'Credit') {
      balances[customer][cardNumber] += amount;
    } else if (type === 'Debit') {
      balances[customer][cardNumber] -= amount;
    }

    // Handle Transfer type
    else if (type === 'Transfer') {
      // Debit the sender's card
      balances[customer][cardNumber] -= amount;

      // Credit the target card for the recipient (if it exists in balances)
      if (!balances[customer][targetCardNumber]) {
        balances[customer][targetCardNumber] = 0;
      }
      balances[customer][targetCardNumber] += amount;
    }
  });

  // Convert balances object into an array of customer balances grouped by cards
  const balanceReport = Object.keys(balances).map((customer) => ({
    customer,
    cards: Object.keys(balances[customer]).map((cardNumber) => {
      const cardBalance = parseFloat(balances[customer][cardNumber].toFixed(3)); // Round to 3 decimal places

      // Check for negative balance and add to negativeBalances array
      if (cardBalance < 0.00) {
        negativeBalances.push({
          customer,
          cardNumber,
          balance: cardBalance,
        });
      }

      return {
        cardNumber,
        balance: cardBalance,
      };
    }),
  }));

  // Return the balance report and the negative balances list
  return { balanceReport, negativeBalances };
}

// Route to upload and process CSV file
app.post('/upload', upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ error: 'No file uploaded' });
  }

  const filePath = req.file.path;
  const fileName = req.file.originalname;  // Get the original file name
  uploadedFiles.push(fileName);  // Store the file name in the list

  const transactions = [];
  const badTransactions = [];  // To store any bad/unparsable transactions

  // Read and parse the CSV file
  fs.createReadStream(filePath)
    .pipe(csv({ headers: properHeaders, skipLines: 1 })) // Use proper headers and skip the first line (incorrect headers)
    .on('data', (row) => {
      console.log('CSV Row:', row);  // Log the row for debugging

      // Validate the row before processing
      const accountName = row['Account Name'];
      const cardNumber = row['Card Number'];
      const amount = parseFloat(row['Transaction Amount']);
      const type = row['Transaction Type'];
      const description = row['Description'];
      const targetCardNumber = row['Target Card Number'];

      // Check if any critical field is missing or invalid
      if (!accountName || !cardNumber || isNaN(amount) || !['Credit', 'Debit', 'Transfer'].includes(type)) {
        // Push this row to the badTransactions array if validation fails
        badTransactions.push({
          accountName,
          cardNumber,
          amount: row['Transaction Amount'],  // Original value for transparency
          type,
          description,
          targetCardNumber
        });
      } else {
        // Handle the data according to the provided structure
        transactions.push({
          customer: accountName,  // Account Name
          cardNumber: cardNumber,  // Card Number
          amount: amount,  // Transaction Amount
          type: type,  // Transaction Type (Credit, Debit, or Transfer)
          description: description,  // Transaction Description
          targetCardNumber: targetCardNumber || 'N/A',  // Target Card Number (for Transfer only)
        });
      }
    })
    .on('end', () => {
      // Append new transactions to the in-memory store
      allTransactions = [...allTransactions, ...transactions];

      // Calculate balances and negative balances for the report
      const { balanceReport, negativeBalances } = calculateBalances(allTransactions);

      // Send the balance report, transactions, bad transactions, negative balances, and list of uploaded files as the response
      res.json({ balanceReport, negativeBalances, badTransactions, allTransactions, uploadedFiles });
    })
    .on('error', (err) => {
      console.error('Error processing CSV:', err);
      res.status(500).json({ error: 'Failed to process file' });
    });
});

// Route to reset the system (clear all transactions and file names)
app.post('/reset', (req, res) => {
  allTransactions = [];
  uploadedFiles = [];  // Clear the uploaded file names
  res.json({ message: 'System reset successfully' });
});

// Start the server
const PORT = 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
