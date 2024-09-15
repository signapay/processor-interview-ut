const transactionService = require('../services/transactionService');

// Upload Transactions Controller
exports.uploadTransactions = (req, res) => {
  const { transactions } = req.body;
  try {
    transactionService.processTransactions(transactions);
    res.status(200).send({ message: 'Transactions processed successfully' });
  } catch (error) {
    res.status(400).send({ message: 'Failed to process transactions', error });
  }
};

// Accounts Report Controller
exports.getAccountsReport = (req, res) => {
  const accountsReport = transactionService.getAccountsReport();
  res.status(200).send(accountsReport);
};

// Collections Report Controller
exports.getCollectionsReport = (req, res) => {
  const collectionsReport = transactionService.getCollectionsReport();
  res.status(200).send(collectionsReport);
};

// Bad Transactions Report Controller
exports.getBadTransactions = (req, res) => {
  const badTransactions = transactionService.getBadTransactions();
  res.status(200).send(badTransactions);
};
