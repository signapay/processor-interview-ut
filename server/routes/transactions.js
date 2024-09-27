const express = require('express');
const multer = require('multer');
const path = require('path');
const { uploadFile, resetSystem, generateAccountReport, generateCollectionsReport } = require('../services/transactionService');

const router = express.Router();
const upload = multer({ dest: path.join(__dirname, '../uploads/') });

// Handle file upload and processing
router.post('/upload', upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ error: 'No file uploaded.' });
  }

  const filePath = req.file.path;
  uploadFile(filePath, (err, result) => {
    if (err) {
      return res.status(500).json({ error: 'Error processing file' });
    }
    res.json(result);
  });
});

// Handle system reset
router.post('/reset', (req, res) => {
  resetSystem();
  res.json({ message: 'System reset successfully' });
});

// API to get the report
router.get('/report', (req, res) => {
  const accounts = generateAccountReport();
  const collections = generateCollectionsReport(accounts);
  res.json({ accounts, collections, badTransactions: global.badTransactions });
});

module.exports = router;
