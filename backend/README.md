# Transaction Processor Backend

This is the backend component of the **Transaction Processor** project, built using Node.js, with JWT-based authentication and MongoDB as the database.

## Getting Started

### Prerequisites

Ensure you have the following installed:

- **Node.js** (version 14 or higher)
- **MongoDB** (Local or MongoDB Atlas connection URI)
- **npm** (comes with Node.js)

### Installation

1. Navigate to the backend folder:

   ```bash
   cd backend
   ```

2. Install all the dependencies:

   ```bash
   npm install
   ```

3. Create a .env file in the root directory and add the following variables:

   ```bash
   MONGODB_URI=your-mongodb-connection-uri
   JWT_SECRET=your-secret-key
   ```
   
4. To start the backend server using Nodemon, run:

   ```bash
   nodemon server.js
   ```

The server will start on http://localhost:9000.

### API Endpoints

Authentication Endpoints

- `POST /api/auth/login`

Authenticate a user with username and password, returning a JWT token upon successful authentication.

- `POST /api/auth/register`

Register a new user with a username and password.

Transaction Endpoints

Note: All transaction routes are protected with JWT. You must provide the JWT token in the Authorization header as a Bearer token.

<img width="1137" alt="image" src="https://github.com/user-attachments/assets/abe4c3aa-5246-43ef-88f6-7a03536b61db">

- `POST /api/transactions/upload`
- `GET /api/transactions/reports`
- `POST /api/transactions/reset`

### Note: 
This project uses **bcrypt** to securely hash and store user passwords. When a user registers, bcrypt generates a salt and hashes the password before saving it in the MongoDB database. This ensures that even if the database is compromised, the passwords remain secure, as they are stored in a non-reversible hashed format. During login, bcrypt compares the hashed password in the database with the provided plain-text password by hashing the latter with the same salt, ensuring secure authentication. This approach enhances the overall security of the system, safeguarding user credentials.

<img width="948" alt="image" src="https://github.com/user-attachments/assets/671b3bb2-d84b-4030-8fb0-879e923122d8">

