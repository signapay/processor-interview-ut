const express = require('express');
const multer = require('multer');
const csv = require('csv-parser');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

let transactions = [];  
let badTransactions = [];  
let collections = [];

const upload = multer({ dest: 'uploads/' });


const validateTransaction = (data) => { //validating the transactions whether they are good or bad transaction
    return (
        data.accountName &&
        data.cardNumber &&
        !isNaN(data.transactionAmount) &&
        (data.transactionType === 'Credit' || data.transactionType === 'Debit' || data.transactionType === 'Transfer')
    );
};


app.post('/upload', upload.single('file'), (req, res) => { //api to read the data from the csv file and segregate into array based on condition
    const results = [];
    const filePath = req.file.path;
    fs.createReadStream(filePath)
        .pipe(csv({ headers: false }))  
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
            if (validateTransaction(parsedData) && parsedData.transactionAmount>0) {
                results.push(parsedData);
                transactions.push(parsedData)
            }else if (validateTransaction(parsedData) && parsedData.transactionAmount<0){
                collections.push(parsedData)
            }
             else {
                badTransactions.push(parsedData);
            }
        })
        .on('end', () => {
            transactions = transactions.concat(results);
            fs.unlinkSync(filePath);
            res.status(200).send({
                message: 'Transactions processed successfully',
                transactions: results,
                collections,
                badTransactions
            });
        });
});


app.get('/reports', (req, res) => {
    res.status(200).send({ transactions, collections, badTransactions });
});


app.post('/reset', (req, res) => {//api to reset the array's
    transactions = [];
    badTransactions = [];
    collections = [];
    res.status(200).send({ message: 'System reset successfully' });
});

app.use(express.static(path.join(__dirname, '../frontend/build'))); // connect to the frontend files in the dist folder of frontend

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, '../frontend/build', 'index.html'));
});


const PORT = 5000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
