# Transaction Processor Frontend

This is the frontend component of the **Transaction Processor** project, built using React.js.

## Features

- **File Upload**: Choose and upload transaction files in `.csv` format.
- **Account Balances**: View the account balances after processing transactions.
- **Collections**: Display accounts in collections.
- **Bad Transactions**: View erroneous transactions.
- **System Reset**: Reset all transactions and clear the system data.
- **Authentication**: Login and registration with JWT authentication. Passwords are securely hashed using bcrypt before being sent to the backend.

### Prerequisites

Make sure you have the following installed on your machine:

- **Node.js** (version 14 or higher)
- **npm** (comes with Node.js)

### Installation

1. Navigate to the frontend folder:

   ```bash
   cd client
   ```

2. Install the dependencies:

   ```bash
   npm install
   ```

3. To start the local server, run:

   ```bash
   npm start
   ```

This will run the app in development mode and open http://localhost:3000 in your browser.

<img width="875" alt="image" src="https://github.com/user-attachments/assets/ae2f3aca-32a3-49b2-bee0-1e9cd9029ee7">

- The screenshot shows a login and registration form with input fields for "Username" and "Password." Below the fields are two buttons, "Login" for logging in and "Register" for creating a new account.

<img width="1182" alt="image" src="https://github.com/user-attachments/assets/bcd0426d-62cc-403d-8f1d-daf719bd31f8">

- The screenshot displays a logged-in interface with a "Choose File" button for selecting a transaction file, followed by action buttons for "Upload Transactions," "Get Account Balances," "Get Collections," "Get Bad Transactions," and "Reset System" to manage and process the transaction data.

- Screenshots

<img width="1285" alt="image" src="https://github.com/user-attachments/assets/80a0e0ca-7c74-4d4f-99a4-33b3cd110a10">

<img width="1265" alt="image" src="https://github.com/user-attachments/assets/88c1d403-636e-4df7-bc2d-29b2ac6d59c2">

<img width="1243" alt="image" src="https://github.com/user-attachments/assets/411cf74a-0a34-483b-a7a8-a588f32dca83">




