const fs = require('fs');
const csv = require('csv-parser');
const { isValidTransaction } = require('../utils/validators');
const BigNumber = require('bignumber.js');

// Maintain in-memory transactions and bad transactions
global.transactions = [];
global.badTransactions = [];

const uploadFile = (filePath, callback) => {
  fs.createReadStream(filePath)
    .pipe(csv({
      headers: ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number'],
      skipLines: 0,
    }))
    .on('data', (data) => {
      if (isValidTransaction(data)) {
        global.transactions.push(data);
      } else {
        global.badTransactions.push(data);
      }
    })
    .on('end', () => {
      callback(null, { message: 'File processed successfully' });
    })
    .on('error', (error) => {
      callback(error);
    });
};

const resetSystem = () => {
  global.transactions = [];
  global.badTransactions = [];
};

const generateAccountReport = () => {
  return global.transactions.reduce((acc, transaction) => {
    const account = acc[transaction['Account Name']] || { cards: {} };
    const card = account.cards[transaction['Card Number']] || { amount: 0 };

    const amount = new BigNumber(transaction['Transaction Amount']);
    if (transaction['Transaction Type'] === 'Credit') {
      card.amount = new BigNumber(card.amount).plus(amount).toFixed(2);
    } else {
      card.amount = new BigNumber(card.amount).minus(amount).toFixed(2);
    }

    account.cards[transaction['Card Number']] = card;
    acc[transaction['Account Name']] = account;

    return acc;
  }, {});
};

const generateCollectionsReport = (accounts) => {
  const collections = [];
  Object.entries(accounts).forEach(([accountName, accountData]) => {
    Object.entries(accountData.cards).forEach(([cardNumber, cardData]) => {
      if (parseFloat(cardData.amount) < 0) {
        collections.push({
          'Account Name': accountName,
          'Card Number': cardNumber,
          'Amount': cardData.amount,
        });
      }
    });
  });
  return collections;
};

module.exports = {
  uploadFile,
  resetSystem,
  generateAccountReport,
  generateCollectionsReport,
};
