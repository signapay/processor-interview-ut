const express = require('express');
const cors = require('cors');
const transactionRoutes = require('./routes/transactionRoutes');

const app = express();

app.use(cors());
app.use(express.json());

// Use transaction routes
app.use('/api/transactions', transactionRoutes);

// Start the server
const PORT = process.env.PORT || 9000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
