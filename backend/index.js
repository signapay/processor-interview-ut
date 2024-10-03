const express = require('express');
const dotenv = require('dotenv');
const cors = require('cors');
const transactionRoutes = require('./routes/transactionRoutes');

dotenv.config();

const app = express();
const port = 3001;

app.use(cors());
app.use(express.json());

app.use(transactionRoutes);

app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
});