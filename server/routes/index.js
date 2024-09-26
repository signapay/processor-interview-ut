const express = require('express');
const multer = require('multer');
const csv = require('csv-parser');
const fs = require('fs');

const router = express.Router();
const upload = multer({ dest: 'uploads/' });

let transactions = [];
let badTransactions = [];

// Handle file upload and processing
router.post('/upload', upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).send('No file uploaded.');
  }
  const filePath = req.file.path;

  fs.createReadStream(filePath)
    .pipe(csv({
      headers: ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number'],  // Explicitly define headers
      skipLines: 0,  // Do not skip any lines (since there is no header row in the file)
    }))
    .on('data', (data) => {
      try {
        // Validate and process transaction data
        if (isValidTransaction(data)) {
          transactions.push(data);
        } else {
          badTransactions.push(data);
        }
      } catch (error) {
        badTransactions.push(data);
      }
    })
    .on('end', () => {
      console.log(transactions);
      res.json({ message: 'File processed successfully' });
    });
});


const isValidTransaction = (transaction) => {
  const validTransactionTypes = ['Transfer', 'Credit', 'Debit'];

  // Check if all the required fields are present and valid
  if (
    !transaction['Account Name'] ||
    !transaction['Card Number'] ||
    !transaction['Transaction Amount'] ||
    !transaction['Transaction Type'] ||
    !transaction['Description']
  ) {
    return false;
  }

  // 1. Validate the Transaction Type is one of the allowed values
  if (!validTransactionTypes.includes(transaction['Transaction Type'])) {
    return false;
  }

  // 2. If the Transaction Type is 'Transfer', ensure 'Target Card Number' exists
  if (transaction['Transaction Type'] === 'Transfer' && !transaction['Target Card Number']) {
    return false;
  }

  // 3. Check if the Transaction Amount is a valid number
  const transactionAmount = parseFloat(transaction['Transaction Amount']);
  if (isNaN(transactionAmount)) {
    return false;
  }

  // 4. Ensure that Card Number and Target Card Number (if present) only contain digits
  const cardNumberRegex = /^\d+$/;
  if (!cardNumberRegex.test(transaction['Card Number'])) {
    return false;
  }
  if (transaction['Target Card Number'] && !cardNumberRegex.test(transaction['Target Card Number'])) {
    return false;
  }

  // If all checks pass, the transaction is valid
  return true;
};

// API to get report
router.get('/report', (req, res) => {
  const accounts = generateAccountReport(transactions);
  // const collections = transactions.filter(t => parseFloat(t['Transaction Amount']) < 0);  // Check for negative balance
  const collections = generateCollectionsReport(accounts);
  res.json({ accounts, collections, badTransactions });
});

// Generate report for accounts
const generateAccountReport = (transactions) => {
  return transactions.reduce((acc, transaction) => {
    const account = acc[transaction['Account Name']] || { cards: {} };
    const card = account.cards[transaction['Card Number']] || { amount: 0 };
    
    // Add transaction amount to card balance
    if (transaction['Transaction Type'] === 'Credit'){
      card.amount += parseFloat(transaction['Transaction Amount']);
    } else {
      card.amount -= parseFloat(transaction['Transaction Amount']);
    }
    
    card.amount = parseFloat(card.amount).toFixed(2);

    account.cards[transaction['Card Number']] = card;
    
    acc[transaction['Account Name']] = account;
    return acc;
  }, {});
};

const generateCollectionsReport = (accounts) => {
  let collections = [];

  // Iterate over each account in the report
  Object.entries(accounts).forEach(([accountName, accountData]) => {
    // Iterate over each card in the account
    Object.entries(accountData.cards).forEach(([cardNumber, cardData]) => {
      // Check if the card amount is negative
      if (cardData.amount < 0) {
        // Add the card to the collections list
        collections.push({
          'Account Name': accountName,
          'Card Number': cardNumber,
          'Amount': cardData.amount
        });
      }
    });
  });

  return collections;
};

// Handle reset system
router.post('/reset', (req, res) => {
  transactions = [];
  badTransactions = [];
  res.json({ message: 'System reset successfully' });
});


module.exports = router;
