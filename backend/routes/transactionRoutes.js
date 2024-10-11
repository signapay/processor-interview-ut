const express = require('express');
const { processTransactions, getReports, resetSystem } = require('../controllers/transactionController');
const upload = require('../middlewares/uploadMiddleware');
const { authenticateToken } = require('../middlewares/authMiddleware');

const router = express.Router();

router.post('/upload', authenticateToken, upload.single('file'), processTransactions);
router.get('/reports', authenticateToken, getReports);
router.post('/reset', authenticateToken, resetSystem);

module.exports = router;