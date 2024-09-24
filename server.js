const express = require('express');
const multer = require('multer');
const xlsx = require('xlsx');
const cors = require('cors');
const NodeCache = require('node-cache');
const cache = new NodeCache();
const rateLimit = require('express-rate-limit');
const app = express();
const PORT = process.env.PORT || 3000;

// Rate limiter configuration to limit each IP to 100 requests per 15 minutes
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // Limit each IP to 100 requests per windowMs
});

// Enable CORS for all routes
app.use(cors());

// Apply rate limiting only to the '/upload' endpoint
app.use('/upload', apiLimiter); 

// Multer configuration to store file in memory
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
    const newTransactions = xlsx.utils.sheet_to_json(sheet, {
      header: ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number'],
      defval: null // Set default value to null if any field is missing
    });

    // Get existing transactions from cache or initialize as empty array
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
  // Retrieve transactions from cache
  const transactions = cache.get('transactions');

  // If no transactions exist, return 404
  if (!transactions) {
    return res.status(404).json({ message: 'No data available' });
  }

  const accounts = {};
  const collections = [];
  const badTransactions = [];

  // Process each transaction
  transactions.forEach((transaction) => {
    const { 'Account Name': accountName, 'Card Number': cardNumber, 'Transaction Amount': amount, 'Transaction Type': type, 'Description': description, 'Target Card Number': targetCardNumber } = transaction;

    // Validate required fields, if invalid add to bad transactions
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

    // Process transaction based on its type (Credit, Debit, Transfer)
    if (type === 'Credit') {
      accounts[accountName][cardNumber] += amount; // Add amount for credit
    } else if (type === 'Debit') {
      accounts[accountName][cardNumber] -= amount; // Subtract amount for debit
    } else if (type === 'Transfer' && targetCardNumber) {
      // For transfers, subtract from source card and add to target card
      accounts[accountName][cardNumber] -= amount;
      if (!accounts[accountName][targetCardNumber]) {
        accounts[accountName][targetCardNumber] = 0;
      }
      accounts[accountName][targetCardNumber] += amount;
    } else {
      badTransactions.push(transaction); // Add to bad transactions if any issue
    }
  });

  // After processing all transactions, identify accounts with negative balances for collections
  for (const accountName in accounts) {
    for (const cardNumber in accounts[accountName]) {
      if (accounts[accountName][cardNumber] < 0) {
        collections.push({ accountName, cardNumber, balance: accounts[accountName][cardNumber] });
      }
    }
  }

  // Prepare the report object
  const report = {
    chartOfAccounts: accounts,
    collections,
    badTransactions
  };

  // Return the report as JSON
  res.status(200).json(report);
});

// Delete cached data (DELETE)
app.delete('/data', (req, res) => {
  // Delete the transaction data from cache
  const success = cache.del('transactions');

  // If deletion is successful, return success message, otherwise return 404
  if (success) {
    res.status(200).json({ message: 'Cache cleared successfully' });
  } else {
    res.status(404).json({ message: 'No data found in cache to delete' });
  }
});

// Start the server and listen on the specified port
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
