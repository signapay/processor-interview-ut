# Transaction Processor

## Pre-requisites:

- **Docker**: Ensure Docker is installed on your machine. You can download and install Docker Desktop from [here](https://www.docker.com/products/docker-desktop/).
- **Docker Compose**: Docker Compose is usually included with Docker Desktop. Verify by running:
  
  `docker-compose --version`

## How to Run the Project:



1. Clone the repository to your local machine:

  `git clone <repository-url>`

  `cd <repository-directory>`

2. Build and start the Docker containers:

  `docker-compose up --build`

3. Access the Application:
  
 *  Frontend (React): Open a browser and go to `http://localhost:3000`
  
 *  Backend (Flask): It is exposed on `http://localhost:5000`

 *  Redis: Exposed on port `6379`. Redis service runs internally and is accessible by the Flask backend.


Note: *Ensure ports **3000, 5000, 6379** are not in use by other services on your system. No additional installations are required on the host system apart from Docker and Docker Compose. All dependencies for each service (e.g., Python packages for Flask) are handled within their respective containers.*

## Contact Information

- **Email**: [parmeshwalunj@gmail.com](mailto:parmeshwalunj@gmail.com)
- **LinkedIn**: [Parmesh Walunj](https://www.linkedin.com/in/pw7/)

