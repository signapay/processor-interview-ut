const express = require('express');
const bodyParser = require('body-parser');
const transactionRoutes = require('./routes/transactionRoutes');
const app = express();

app.use(bodyParser.json());

// Transaction Routes
app.use('/api/transactions', transactionRoutes);

// Reset endpoint
app.post('/api/reset', (req, res) => {
  // Reset logic
  transactionService.resetTransactions();
  res.status(200).send({ message: 'System reset successfully' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});