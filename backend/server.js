const express = require('express');
const multer = require('multer');
const csv = require('csv-parser');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

let transactions = [];  // Store transactions in-memory
let badTransactions = [];  // Store invalid transactions

// Multer config for file upload
const upload = multer({ dest: 'uploads/' });

// Validate the transaction
const validateTransaction = (data) => {
    return (
        data.accountName &&
        data.cardNumber &&
        !isNaN(data.transactionAmount) &&
        (data.transactionType === 'Credit' || data.transactionType === 'Debit' || data.transactionType === 'Transfer')
    );
};

// Process uploaded file and extract data
app.post('/upload', upload.single('file'), (req, res) => {
    const results = [];
    const filePath = req.file.path;
    fs.createReadStream(filePath)
        .pipe(csv({ headers: false }))  // No headers in the CSV
        .on('data', (data) => {
            const [accountName, cardNumber, transactionAmount, transactionType, description, targetCardNumber] = Object.values(data);
            const parsedData = {
                accountName,
                cardNumber,
                transactionAmount: parseFloat(transactionAmount),
                transactionType,
                description,
                targetCardNumber: targetCardNumber || null
            };
            if (validateTransaction(parsedData)) {
                results.push(parsedData);
            } else {
                badTransactions.push(parsedData);
            }
        })
        .on('end', () => {
            transactions = transactions.concat(results);
            // Delete the file after processing
            fs.unlinkSync(filePath);
            res.status(200).send({
                message: 'Transactions processed successfully',
                transactions: results,
                badTransactions
            });
        });
});

// Generate reports: Accounts and Collections
app.get('/reports', (req, res) => {
    const accounts = {};
    const collections = [];

    transactions.forEach((txn) => {
        const { accountName, cardNumber, transactionAmount, transactionType } = txn;

        if (!accounts[accountName]) {
            accounts[accountName] = {};
        }

        if (!accounts[accountName][cardNumber]) {
            accounts[accountName][cardNumber] = 0;
        }

        if (transactionType === 'Credit' || transactionType === 'Transfer') {
            accounts[accountName][cardNumber] += transactionAmount;
        } else if (transactionType === 'Debit') {
            accounts[accountName][cardNumber] -= transactionAmount;
        }

        if (accounts[accountName][cardNumber] < 0) {
            collections.push({ accountName, cardNumber, balance: accounts[accountName][cardNumber] });
        }
    });

    res.status(200).send({ accounts, collections, badTransactions });
});

// Reset system
app.post('/reset', (req, res) => {
    transactions = [];
    badTransactions = [];
    res.status(200).send({ message: 'System reset successfully' });
});

app.use(express.static(path.join(__dirname, '../frontend/build')));

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, '../frontend/build', 'index.html'));
});

// Start the server
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
