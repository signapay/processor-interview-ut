const express = require('express');
const multer = require('multer');
const xlsx = require('xlsx');
const Decimal = require('decimal.js');
const cors = require('cors');
const NodeCache = require('node-cache');
const cache = new NodeCache();
const rateLimit = require('express-rate-limit');
const app = express();
const PORT = process.env.PORT || 3000;

const apiLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 100 // Limit each IP to 100 requests per windowMs
  });
// Enable CORS for all routes
app.use(cors());
app.use('/upload', apiLimiter); 
// Multer configuration
const storage = multer.memoryStorage();
const upload = multer({ storage: storage });

// Upload endpoint (POST)
app.post('/upload', upload.single('file'), (req, res) => {
    try {
      // Read the workbook from the uploaded file buffer
      const workbook = xlsx.read(req.file.buffer, { type: 'buffer' });
      const sheetName = workbook.SheetNames[0]; // Get the first sheet
      const sheet = workbook.Sheets[sheetName];
  
      // Convert the sheet data to JSON, ensuring the column headers match the keys in the transactions
      const newTransactions  = xlsx.utils.sheet_to_json(sheet, {
        header: ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number'],
        defval: null // Set default value to null if any field is missing
      });
  
      // Cache the uploaded transaction data with the correct keys
      let existingTransactions = cache.get('transactions') || [];

    // Append new transactions to existing ones
    const updatedTransactions = [...existingTransactions, ...newTransactions];

    // Cache the updated transactions
    cache.set('transactions', updatedTransactions);
  
      // Return success response with the uploaded data
      res.status(200).json({ message: "File successfully uploaded and cached", data: updatedTransactions });
    } catch (error) {
      console.error(error);
      res.status(500).json({ error: 'Failed to process the uploaded file' });
    }
  });
  

// Generate and return reports (GET)
app.get('/data', (req, res) => {
  const transactions = cache.get('transactions');

  if (!transactions) {
    return res.status(404).json({ message: 'No data available' });
  }

  const accounts = {};
  const collections = [];
  const badTransactions = [];

  // Process the transactions
  transactions.forEach((transaction) => {
    const { 'Account Name': accountName, 'Card Number': cardNumber, 'Transaction Amount': amount, 'Transaction Type': type, 'Description': description, 'Target Card Number': targetCardNumber } = transaction;

    // Validate required fields
    if (!accountName || !cardNumber || !amount || !type || typeof amount !== 'number' || !['Credit', 'Debit', 'Transfer'].includes(type)) {
      badTransactions.push(transaction);
      return;
    }

    // Chart of accounts: Keep track of accounts, cards, and amounts
    if (!accounts[accountName]) {
      accounts[accountName] = {};
    }
    if (!accounts[accountName][cardNumber]) {
      accounts[accountName][cardNumber] = 0;
    }

    // Process transactions
    if (type === 'Credit') {
      accounts[accountName][cardNumber] += amount;
    } else if (type === 'Debit') {
      accounts[accountName][cardNumber] -= amount;
    } else if (type === 'Transfer' && targetCardNumber) {
      accounts[accountName][cardNumber] -= amount;
      if (!accounts[accountName][targetCardNumber]) {
        accounts[accountName][targetCardNumber] = 0;
      }
      accounts[accountName][targetCardNumber] += amount;
    } else {
      badTransactions.push(transaction);
    }

    // Check for accounts that need to go to collections (negative balance)
    // if (accounts[accountName][cardNumber] < 0) {
    //   collections.push({ accountName, cardNumber, balance: accounts[accountName][cardNumber] });
    // }
  });
  for (const accountName in accounts) {
    for (const cardNumber in accounts[accountName]) {
      if (accounts[accountName][cardNumber] < 0) {
        collections.push({ accountName, cardNumber, balance: accounts[accountName][cardNumber] });
      }
    }
  }
  // Prepare report
  const report = {
    chartOfAccounts: accounts,
    collections,
    badTransactions
  };

  res.status(200).json(report);
});

// Delete cached data (DELETE)
app.delete('/data', (req, res) => {
  const success = cache.del('transactions'); // Delete the data from cache

  if (success) {
    res.status(200).json({ message: 'Cache cleared successfully' });
  } else {
    res.status(404).json({ message: 'No data found in cache to delete' });
  }
});

// Start the server
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
