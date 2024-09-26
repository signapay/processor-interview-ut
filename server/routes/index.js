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

module.exports = router;
