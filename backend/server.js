require('dotenv').config();
const express = require('express');
const cors = require('cors');
const mongoose = require('mongoose');
const transactionRoutes = require('./routes/transactionRoutes');
const authRoutes = require('./routes/authRoutes');

const app = express();

app.use(cors());
app.use(express.json());

// MongoDB Connection
const uri = process.env.MONGODB_URI

function connectWithRetry() {
  console.log('MongoDB connection with retry');
  mongoose.connect(uri)
    .then(() => {
      console.log('MongoDB is connected');
    })
    .catch(err => {
      console.error('MongoDB connection unsuccessful, retry after 5 seconds.', err);
      console.error('Cause:', err.cause);
      setTimeout(connectWithRetry, 2000);
    });
}

connectWithRetry();

// Use auth routes
app.use('/api/auth', authRoutes);

// Use transaction routes
app.use('/api/transactions', transactionRoutes);

// Start the server
const PORT = process.env.PORT || 9000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});