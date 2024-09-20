const express = require('express');
const multer = require('multer');
const csvParser = require('csv-parser');
const redis = require('redis');
const fs = require('fs');
const path = require('path');

const app = express();
const port = 3001;

const cors = require('cors');
app.use(cors());

const client = redis.createClient({
    host: '127.0.0.1',
    port: 6379
  });

client.connect().catch(console.error);

const upload = multer({ dest: 'uploads/' });

app.use(express.json());

/**
 * Function to process the uploaded CSV file
 * @param {String} filePath - Path to the uploaded file
 * @returns {Promise} - Resolves with an array of transaction objects
 */
const processTransactions = (filePath) => {
    return new Promise((resolve, reject) => {
        const results = [];
        fs.createReadStream(filePath)
            .pipe(csvParser())
            .on('data', (data) => {
                results.push(data);
            })
            .on('end', () => {
                resolve(results);
            })
            .on('error', (error) => {
                reject(error);
            });
    });
};

app.post('/upload', upload.single('file'), async (req, res) => {
    try {
        const transactions = await processTransactions(req.file.path);
        await client.set('transactions', JSON.stringify(transactions));
        res.status(200).json({ message: 'Transactions processed successfully' });
    } catch (error) {
        res.status(500).json({ error: 'Failed to process transactions' });
    }
});

app.get('/report/accounts', async (req, res) => {
    const transactions = JSON.parse(await client.get('transactions')) || [];
    const accounts = {};

    transactions.forEach(tx => {
        const { 'Account Name': accountName, 'Transaction Amount': amount, 'Card Number': cardNumber } = tx;
        if (!accounts[accountName]) {
            accounts[accountName] = {};
        }
        if (!accounts[accountName][cardNumber]) {
            accounts[accountName][cardNumber] = 0;
        }
        accounts[accountName][cardNumber] += parseFloat(amount);
    });

    res.status(200).json(accounts);
});

app.get('/report/bad-transactions', async (req, res) => {
    const transactions = JSON.parse(await client.get('transactions')) || [];
    const badTransactions = transactions.filter(tx => !tx['Account Name'] || !tx['Transaction Amount']);
    res.status(200).json(badTransactions);
});

app.get('/report/collections', async (req, res) => {
    const transactions = JSON.parse(await client.get('transactions')) || [];
    const collections = [];

    transactions.forEach(tx => {
        if (parseFloat(tx['Transaction Amount']) < 0) {
            collections.push(tx['Account Name']);
        }
    });

    res.status(200).json([...new Set(collections)]);
});

app.post('/reset', async (req, res) => {
    await client.del('transactions');
    res.status(200).json({ message: 'System reset successfully' });
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
