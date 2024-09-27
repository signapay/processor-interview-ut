const fs = require('fs');
const csv = require('csv-parser');
const {sortByAccountName, updateAccountBalance, updateCollections} = require('../utils/balanceHelper');
const {validateTransaction} = require("../utils/validation")

let transactions = [];
let badTransactions = [];
let collections = [];
let accountBalances = {};

const processTransactions = (req, res) => {
    const results = [];
    const badTransactionsTemp = [];

    const parseTransactionAmount = (amount) => {
        // Parse the transaction amount and handle various formats
        if (amount.startsWith('-')) {
            return parseFloat(amount);
        }
        if (amount.startsWith('(') && amount.endsWith(')')) {
            return -parseFloat(amount.replace(/[()]/g, ''));
        }
        return parseFloat(amount);
    };

    const filePath = req.file.path; // Get the uploaded file path
    fs.createReadStream(filePath)
        .pipe(csv({ headers: false }))
        .on('data', (data) => {
            const [accountName, cardNumber, transactionAmount, transactionType, description, targetCardNumber] = Object.values(data);
            const parsedData = {
                accountName,
                cardNumber: cardNumber || null, // Ensure cardNumber is null if missing
                transactionAmount: parseTransactionAmount(transactionAmount), // Parse transaction amount
                transactionType,
                description,
                targetCardNumber: targetCardNumber || null,
            };

            // Validate and process the transaction
            if (validateTransaction(parsedData)) {
                updateAccountBalance(parsedData, accountBalances);
                results.push(parsedData);
            } else {
                badTransactionsTemp.push({
                    ...parsedData,
                    originalTransactionAmount: transactionAmount,
                });
            }
        })
        .on('end', () => {
            transactions = sortByAccountName(transactions.concat(results));
            badTransactions = sortByAccountName(badTransactions.concat(badTransactionsTemp));

            collections = updateCollections(accountBalances); // Update collections after processing

            fs.unlinkSync(filePath); // Delete uploaded file after processing
            res.status(200).send({
                message: 'Transactions processed successfully',
                transactions,
                collections,
                badTransactions,
                accountBalances,
            });
        });
};

const getReports = (req, res) => {
    const formattedBalances = Object.entries(accountBalances).map(([cardNumber, { balance, accountNames }]) => ({
        cardNumber,
        balance,
        accountNames: accountNames.size > 0 ? Array.from(accountNames) : ['NO ACCOUNT NAME FOUND'], // Return "No account name found" if no names exist
    }));

    res.status(200).send({
        transactions,
        collections,
        badTransactions,
        accountBalances: formattedBalances, // Send formatted balances with names and balances
    });
};

const resetSystem = (req, res) => {
    transactions = [];
    badTransactions = [];
    collections = [];
    accountBalances = {}; // Reset account balances
    res.status(200).send({ message: 'System reset successfully' });
};

module.exports = {
    processTransactions,
    getReports,
    resetSystem,
};
