#!/bin/bash

# Create a virtual environment (optional, but recommended)
python3 -m venv venv

# Activate the virtual environment
source venv/bin/activate

# Install dependencies
pip3 install -r requirements.txt

# Run the FastAPI application with Uvicorn on port 5000 with reload enabled
uvicorn main:app --reload --host 127.0.0.1 --port 5000

# chmod +x run_app.sh  --run this to give execute permission 
# ./run_app.sh --run this to execute the file
