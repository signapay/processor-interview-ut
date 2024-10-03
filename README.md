# Transaction Processor

A Node.js application for processing transactions from CSV files with a RESTful API.

## Features

- Upload CSV files containing transaction data
- Process transactions and categorize them into accounts and cards
- Identify and store bad transactions (transactions with missing fields)
- Retrieve accounts, cards, and bad transactions via API endpoints
- Reset the system to clear all data
- Secure APIs with CORS and API keys
- Pagination for large datasets

## Prerequisites

- Node.js (v14 or higher)
- npm (v6 or higher)

## Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/yourusername/transaction-processor.git
   cd transaction-processor

2. Install dependencies for backend:
   ```bash
   npm install
   npm install http-server
   ```


## Usage

1. Start the backend server:
   ```bash
   cd backend
   npm run dev
   ```
2. Start the frontend server:
  ```bash
   cd frontend
   http-server
   ```

2. Open your browser and navigate to [http://localhost:8081](http://localhost:3001) to access the application.

## API Endpoints

### Upload Transactions: `POST /upload`

Upload a CSV file containing transaction data.

- **Request Body**: Use a form with a file input to upload the CSV file.
- **Response**: Success message or error details.

### Get Collections: `GET /collections`
Retrieve all collections of accounts with negative balance.

### Get Bad Transactions: `GET /bad-transactions`

Retrieve all bad transactions (transactions with missing fields).

### Reset System: `POST /api/reset`

Clear all data from the system (transactions, cards, and accounts).

- **Response**: Success message confirming data reset.
```
