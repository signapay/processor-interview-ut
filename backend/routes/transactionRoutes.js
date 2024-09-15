const express = require('express');
const router = express.Router();
const transactionsController = require('../controllers/transactionsController');

// Upload transactions
router.post('/upload', transactionsController.uploadTransactions);

// Get reports
router.get('/accounts', transactionsController.getAccountsReport);
router.get('/collections', transactionsController.getCollectionsReport);
router.get('/bad-transactions', transactionsController.getBadTransactions);

module.exports = router;
