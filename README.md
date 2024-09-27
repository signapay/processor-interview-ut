# Transaction Processor

## Overview
This project processes transaction files in CSV format and generates reports. The system keeps track of accounts, processes valid transactions, and reports invalid ones. The backend is built with **Express.js**, and the frontend is built with **React.js**.

## Features
- Upload CSV file to process transactions
- View reports of accounts, collections, and bad transactions
- Reset system to clear transactions
- Numerical precision for transaction amounts
- Server runs on **Express.js**, frontend built with **React.js**

## Tech Stack
### Frontend
- **React.js**: For building the user interface and handling interactions such as file upload and report display.
- **Axios**: For making HTTP requests from the frontend to the backend.

### Backend
- **Node.js**: JavaScript runtime for building the backend API.
- **Express.js**: Lightweight web framework for handling HTTP requests and routing.
- **Multer**: Middleware for handling file uploads.
- **CSV-Parser**: For parsing CSV files into JSON format.
- **BigNumber.js**: For numerical precision in handling transaction amounts to ensure accurate financial calculations.
- **Nodemon**: For automatic server reload during development.

## Prerequisites
- Node.js (>= 14.x)
- npm (>= 6.x)

## Installation
1. Clone the repository:
   ```bash
  git clone https://github.com/your-repo/transaction-processor.git

2. Navigate to the project directory:
   ```bash
  cd transaction-processor

3. Install dependencies:
   - For the backend
    ```bash
    cd server
    npm install
   
   - For the frontend
    ```bash
    cd client
    npm install

4. Running the Application
   Backend: Start the Express server (by default, runs on port 5000):
     ```bash
    cd server
    npm run dev

    Frontend: Start the React frontend (runs on port 3000):
     ```bash
    cd client
    npm start

5. Usage
  - Visit http://localhost:3000 to upload transaction files and view reports.
  - Reports show a chart of accounts, accounts that need collection, and bad transactions.

6. API Endpoints
 - POST /upload - Upload CSV file
 - POST /reset - Reset system
 - GET /report - Get report of transactions

## Security
### Implemented Security Measures:
1. **CORS Protection**:
   - Configured **CORS** (Cross-Origin Resource Sharing) to restrict access to the API, allowing requests only from `http://localhost:3000` (the React frontend).
   - CORS ensures that the API is not accessible by unauthorized domains, preventing cross-origin attacks.
   ```javascript
   const cors = require('cors');
   app.use(cors({ origin: 'http://localhost:3000', methods: ['GET', 'POST'] }));

2. **Input Validation**:
   - Implemented comprehensive input validation for all transaction fields in the backend. Invalid or incomplete transactions (missing fields, invalid card numbers, etc.) are filtered into a "bad transactions" report.
   - This ensures that only valid transactions are processed, reducing the risk of malicious or malformed data.

3. **No Open Endpoints**:
   - No sensitive data or logic is exposed via open API endpoints. Only necessary API routes (/upload, /report, /reset) are made accessible to the frontend.  