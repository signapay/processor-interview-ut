
# Transaction Processor

## Overview

This project is a **Transaction Processor** built with a **React** frontend and a **Spring Boot** backend. The software processes transaction files, generates reports, and provides key functionalities for managing accounts, collections, and bad transactions.

## Prerequisites

Before you begin, ensure the following software is installed on your system:

- **Java 20** 

## Getting Started

### 1. Clone the Repository

Clone the project repository from GitHub and navigate to the project directory.

### 2. Build and Run the Project

**Locate the JAR File:**

- The JAR file is located in the root project & named as `processor-0.0.1.jar`.

**Run/Generate new JAR File:**

To run the application, open a terminal in project root folder and execute the following command: <br>
`./mvnw clean package` or <br> use the jar that is shipped along with the code.

```bash
java -jar target/processor-0.0.1.jar or  java -jar processor-0.0.1.jar
```

If you need to run the application on a different port, use the following command:

```bash
java -jar target/processor-0.0.1.jar --server.port=9090
```

### 3. Verify the Application is Running

- By default, the application runs on **http://localhost:8080**.
- If you changed the port, replace `8080` with the new port number in the URL, i.e., **http://localhost:9090**.

### 4. Access the Application

Once the application is running, open your browser and navigate to:

**http://localhost:8080** (or your custom port).

You should see the login page. Use the following credentials to log in:

- **Username**: `admin`
- **Password**: `admin`

After logging in, you will be able to access the file upload page.

### 5. Functional Requirements

#### User Interface (UI):
- **File Upload**: The application accepts transaction files. Each file submission continues from where the previous one left off.
- **System Reset**: Users can reset the system, clearing all previous transaction data and starting fresh.

#### Logic:
- The application processes the uploaded transactions and performs necessary validations.
- Any parsing errors will be flagged as "bad transactions."

#### Reporting:
The application provides the following reports:

- **Chart of Accounts**: Lists account names, associated cards, and balances.
- **Collections List**: Identifies accounts with a balance less than 0.00 and flags them for collections.
- **Bad Transactions**: Lists transactions that could not be processed or parsed, which require manual review.

#### Persistence:
- The system uses an internal data structure to cache transaction data during a session. Data remains persistent only during a "run" of the software, allowing users to continue from where they left off, unless a system reset is performed.

### Security

The login page requires valid credentials to access the system.

- **Username**: `admin`
- **Password**: `admin`


