const express = require('express');
const cors = require('cors');
const transactionsRouter = require('./routes/transactions');
const path = require('path');

const app = express();

// Middleware setup
app.use(cors({ origin: 'http://localhost:3000', methods: ['GET', 'POST'] }));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Transaction-related routes
app.use('/', transactionsRouter);

module.exports = app;