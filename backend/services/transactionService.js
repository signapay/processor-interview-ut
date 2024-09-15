const transactions = [];
const badTransactions = [];
const accounts = {};

// Process Transactions
exports.processTransactions = (transactionList) => {
  transactionList.forEach((transaction) => {
    try {
      // Validate and process transaction
      const { accountName, cardNumber, transactionAmount, transactionType } = transaction;

      // Ensure the account and card exist
      if (!accounts[accountName]) {
        accounts[accountName] = {};
      }
      if (!accounts[accountName][cardNumber]) {
        accounts[accountName][cardNumber] = 0;
      }

      // Process transaction based on type
      if (transactionType === 'Credit') {
        accounts[accountName][cardNumber] += transactionAmount;
      } else if (transactionType === 'Debit') {
        accounts[accountName][cardNumber] -= transactionAmount;
      } else if (transactionType === 'Transfer') {
        // Handle Transfer logic
        if (transaction.targetCardNumber && accounts[accountName][transaction.targetCardNumber]) {
          accounts[accountName][cardNumber] -= transactionAmount;
          accounts[accountName][transaction.targetCardNumber] += transactionAmount;
        } else {
          throw new Error('Invalid transfer target');
        }
      }
    } catch (error) {
      badTransactions.push(transaction);
    }
  });
};

// Get Accounts Report
exports.getAccountsReport = () => {
  return accounts;
};

// Get Collections Report (accounts with negative balance)
exports.getCollectionsReport = () => {
  const collections = [];
  for (const account in accounts) {
    for (const card in accounts[account]) {
      if (accounts[account][card] < 0) {
        collections.push({ accountName: account, cardNumber: card, balance: accounts[account][card] });
      }
    }
  }
  return collections;
};

// Get Bad Transactions
exports.getBadTransactions = () => {
  return badTransactions;
};

// Reset Transactions
exports.resetTransactions = () => {
  transactions.length = 0;
  badTransactions.length = 0;
  for (const account in accounts) {
    delete accounts[account];
  }
};
