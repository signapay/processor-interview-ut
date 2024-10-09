# Dockerfile for the Flask app using Python 3.10
FROM python:3.10-slim

# Set the working directory to /app/backend inside the container
WORKDIR /app/backend

# Copy the requirements file from the backend folder and install dependencies
COPY requirements.txt /app/backend/requirements.txt
RUN pip install -r requirements.txt

# Copy the entire backend folder to /app/backend in the container
# COPY backend /app/backend
# COPY . .

# Expose the port that Flask will run on
EXPOSE 5000

# Run the Flask application
CMD ["python", "app.py"]
