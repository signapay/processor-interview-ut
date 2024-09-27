const express = require('express');
const { processTransactions, getReports, resetSystem } = require('../controllers/transactionController');
const upload = require('../middlewares/uploadMiddleware');

const router = express.Router();

router.post('/upload', upload.single('file'), processTransactions);
router.get('/reports', getReports);
router.post('/reset', resetSystem);

module.exports = router;
