# Transaction Processor Backend

This is the backend component of the **Transaction Processor** project, built using Node.js.

## Getting Started

### Prerequisites

Ensure you have the following installed:

- **Node.js** (version 14 or higher)
- **npm** (comes with Node.js)

### Installation

1. Navigate to the backend folder:

2. Install all the dependencies:

npm install

3. To start the backend server using Nodemon, run:

nodemon server.js

The server will start on http://localhost:9000.

### API Endpoints

Here are some key endpoints exposed by the backend:

- `POST /api/upload` - Upload a single transaction file.
- `GET /api/reports` - Retrieve system reports related to transactions and accounts.
- `POST /api/reset` - Reset the system, clearing all stored transactions and account data.

