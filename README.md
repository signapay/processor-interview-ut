# Transaction Processor

A Streamlit application to process transaction files and provide reporting for internal account managers at a credit card transaction processing company.

## Introduction

This application processes transaction CSV files provided by transaction providers. It parses the transactions, maintains state during the runtime, and provides reporting features, including:

- Chart of accounts listing account names, associated cards, and balances.
- List of accounts needing collections (cards with a balance less than 0.00).
- List of bad transactions that couldn't be parsed.

## Features

- Accepts transaction files in CSV format (without headers).
- Handles transactions of types: Credit, Debit, and Transfer.
- Processes transactions and updates in-memory state.
- Provides reporting with interactive tables.
- Offers a reset functionality to clear the current state.

## Prerequisites

- Python 3.10.8
- pip (Python package installer)

## Run the Application

### 1. Clone the Repository

Clone the repository to your local machine using:

```bash
git clone <repository_url>
```
### 2. Create virtual environment and install dependencies

```bash
pip install -r requirements.txt
```

### 3. Run app

```bash
streamlit run app.py --server.enableXsrfProtection false   
```

This command will launch the application in your web browswer at ```http://localhost:8501/```