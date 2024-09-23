

A **Django + React** web application that processes transaction files, generates reports, and provides useful analytics. It is designed to handle CSV uploads, report on invalid transactions, and offer insight into account balances. This application is containerized using Docker for easy setup and deployment.

## Features

- **Upload CSV Files**: Upload transaction files in CSV format for processing.
- **Transaction Reporting**:
    - Generates a summary of accounts and associated cards.
    - Identifies accounts with negative balances that need to go to collections.
    - Flags bad transactions for manual review.
- **Error Handling**: Transactions that cannot be parsed are added to a "bad transactions" list.
- **Data Persistence**: Use SQLite for local persistence, or easily switch to a production database like PostgreSQL.

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Setup Instructions](#setup-instructions)
    - [Running without Docker](#running-without-docker)
    - [Running with Docker](#running-with-docker)
- [Usage Instructions](#usage-instructions)
    - [File Upload](#file-upload)
    - [Viewing Reports](#viewing-reports)
- [Future Enhancements](#future-enhancements)
    - [Additional File Formats](#additional-file-formats)
    - [Authentication](#authentication)
    - [Advanced Analytics](#advanced-analytics)
- [Contributing](#contributing)
- [License](#license)

## Setup Instructions

### Running without Docker

1. **Backend Setup (Django)**:
    - Navigate to the `backend` directory:
      ```bash
      cd backend
      ```
    - Install the required Python packages:
      ```bash
      pip install -r requirements.txt
      ```
    - Apply migrations to set up the SQLite database:
      ```bash
      python manage.py migrate
      ```
    - Run the Django development server:
      ```bash
      python manage.py runserver
      ```

   The backend will now be accessible at `http://localhost:8000`.

2. **Frontend Setup (React)**:
    - Navigate to the `frontend` directory:
      ```bash
      cd frontend
      ```
    - Install the required Node.js packages:
      ```bash
      npm install
      ```
    - Run the React development server:
      ```bash
      npm start
      ```

   The frontend will now be accessible at `http://localhost:3000`.

### Running with Docker

1. **Build and Run the Containers**:
    - In the project root directory (where the `docker-compose.yml` file is located), run the following command to build and start both services:
      ```bash
      docker-compose up --build
      ```

   This will:
    - Start the **backend** (Django) on `http://localhost:8000`.
    - Start the **frontend** (React) on `http://localhost:3000`.

2. **Stopping the Containers**:
    - To stop the containers, run:
      ```bash
      docker-compose down
      ```

## Usage Instructions

### File Upload

1. Navigate to the **file upload page** via the frontend interface.
2. Select a **CSV file** to upload. The file must contain transactions in the expected format.
3. After uploading, the system will process the transactions.

### Viewing Reports

1. **Accounts Summary**: View a summary of accounts, including card balances.
2. **Collections Report**: Identify accounts with negative balances that need to go to collections.
3. **Bad Transactions**: View a list of transactions that could not be processed.

## Future Enhancements

This project can be extended in the following ways:

### Additional File Formats

Currently, the app supports only **CSV** file uploads. You can extend support to other formats like **Excel** (.xlsx), **JSON**, or **XML** for broader use cases.

### Authentication

Add user authentication using popular methods 

### Advanced Analytics

Extend the app to offer more sophisticated analytics:
- **Trend Analysis**: Provide visual trends in account balances over time.
- **Audit Trails**: Track and log user actions for security and compliance.
- **Transaction History**: Include historical transaction views with filtering options.

# Transaction Processor

## Context

Thank you for taking a the time to complete our interview code project. We realize that there are many ways to conduct the "technical part" of the interview process from L33T code tests to whiteboards, and each has its own respective pros / cons. We intentionally chose the take-home project approach because we believe it gives you the best chance to demonstrate your skills and knowledge in a "normal environment" - i.e. your computer, keyboard, and IDE.

Our expectation is that this project should take between 3-5 hours of effort. We realize that you have a life outside of this interview process so we do not specify a timeframe in which you need to complete the project. That said, we are generally actively recruiting, so a long delay may result in the opening having already been filled.

You are free to use whatever tech stack you prefer to complete the project. Once your project is complete, the next step in the process is a conversation about why you made the choices you did and a review of your solution. We believe that this conversation is as important as the code itself and provides an opportunity for feedback both ways.

We encourage you to have fun with the project, while producing a solution that you believe accurately represents how you would bring your skillset to the team.

We have attempted to make this repo as clear as possible, but if you have any questions, we encourage you to reach out.

## Project

We would like you to build a transaction processor. In this scenario, the user of your software is an internal account manager, who has been provided a transaction file from one or more of our transaction providers.

We will provide your software with a list of transactions. It needs to process the transactions and provide some reporting information back to the user. We will detail the content of the file and required reporting below.

Beyond these basic requirements, the implementation is up to you.

### Transaction Details

These transactions will contain the following information:

| Field              | Type   | Description                                                       |
| ------------------ | ------ | ----------------------------------------------------------------- |
| Account Name       | Text   | The name of the account                                           |
| Card Number        | Number | The card number used for the transaction, 1:M with account names  |
| Transaction Amount | Number | The amount of the transaction, can be positive or negative        |
| Transaction Type   | Text   | The type of transaction, values can be Credit, Debit, or Transfer |
| Description        | Text   | A brief description of the transaction                            |
| Target Card Number | Number | only provided if the transaction type is a transfer               |

### Functional Requirements

Your solutions needs to provide a system with the following functionality:

- **UI**
    - Accepts a file containing the transactions to process
        - Each file submitted should continue "in continuation of" previous submissions
    - An ability to reset the system to blank (new)
- **Logic** that correctly processes the file
- **Reporting**
    - A chart of accounts that list the account name, its cards, and the amount on each card
    - A list of accounts that we need to give to collections (any cards with a < 0.00 balance)
    - A list of "bad transactions" that someone needs to go look at (any transactions that you were unable to parse)
- **Persistence**
    - Persistence during a "run" of the software is required
        - for example, if you choose to build a nextjs or remix site, we expect that you, at minimum, use an in memory cache that maintains state as long as the process is running
        - Long term persistence such as a database is allowed, but not required.
            - If implemented, be sure that initialization is easy / documented for our review

### Data Files

In this repo, you will find the following csv files:

| File     | Description                                                                                                                                               |
| -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| test.csv | a smaller file that has a few sample transactions - the intent is to use this file for development. like most development resources, this file is "clean" |
| data.csv | a larger file that has a number of transactions - like the real world, you should not assume that this file is not without its "issues"                   |

## Submission

You should fork this repo to your own account and then submit a PR when you are ready for your solution to be evaluated. This workflow closely follows our daily practice of feature branching > PR and following it is required for your submission to be considered "complete".

## Final Thoughts and Hints

- In this scenario, you are the initial architect creating the first pass at this project. You can consider our review the same as a Senior level engineer coming on to the project. Make sure that when we "pick up" the repo, it is clear how to stand up the project, run the solution, and potentially contribute code
- Since you are tackling this specific project, our expectation is that you are at a senior engineer level. While we 100% want your code to represent your preferred style, there are some things we consider "basic" that should be in your submission. These include ideas like the following list. This list is not exhaustive, it is meant to point in a direction:
    - Clear, consistent, readable code
    - Proper use of your selected stack
        - for example, if you choose C#, we would expect to see IOC/DI appropriately implemented
    - DRY
    - Low cyclomatic complexity
    - Low Coupling / High Cohesion
    - Clear thought and patterns for maintainability and expansion
        - This scenario is obviously simplified from reality, that said you should consider future requests like other transaction types, different file formats, etc. - this will at minimum, be a topic in the conversation
- While it should be obvious, this scenario involves "money". This means numerical accuracy is required and at least minimal security should be considered in your submission (we aren't going to "hack your solution", but there shouldn't be open API endpoints either).
- We do NOT expect you to be a designer, we do expect you to consider your user and make the experience intuitive and easy to use

As we said above, if you have any questions, please reach out.