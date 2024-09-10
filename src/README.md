# Processor Interview Project - Fazeel Ayaz

This project is a web-based file processing system built using Flask, designed to handle and process various file types. The application provides functionalities to upload files, view file content, generate reports, and manage files with a user-friendly interface.

## Tech Stack

- **Python (Flask)**: Backend framework for building the web application.
- **HTML, CSS, and JavaScript**: For the frontend UI and interactivity.

## Features

### UI Features

- **File Upload and Management**: Easily upload different file types (CSV, TXT, XLSX, DOCX, PDF) through a dedicated upload section.
- **Descriptive Buttons**: Clear and descriptive buttons for actions like uploading, adding files, and resetting the system.
- **Highlighted Buttons**: Buttons highlight to provide visual feedback when interacted with.
- **File Viewing Sections**: Each uploaded file appears in its own mini-section with buttons for viewing and other actions.
- **Navbar for Easy Navigation**: A top navigation bar is included to navigate between different sections and actions while viewing a file.
- **Pagination for Records**: Pagination is implemented to display records with a default of 10 records per page.
- **Dynamic Dropdown for Records per Page**: A dropdown menu is available for users to select the number of records to view per page.
- **Delete File Functionality**: A delete button is included under each file to remove specific files without resetting the entire system.

### Processing Features

- **Dynamic Routes for Pages**: The application dynamically routes to different pages based on the selected actions and files.
- **CSV Data Processing and Display**: CSV files are processed, and the data is displayed as a chart of accounts.
- **Security Enhancement for Card Information**: Only the last 4 digits of a card number are initially displayed to enhance security.
- **Interactive Card Display**: Only one card number can be fully displayed at a time by clicking on it, and all others revert to the default hidden state.

## Run

- run ```python app.py``` command 
