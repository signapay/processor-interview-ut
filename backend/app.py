from flask import Flask, jsonify, request
from flask_cors import CORS, cross_origin
from redis import Redis
from routes import routes
import pandas as pd
import os
from flask import current_app as app


# Initialize Flask app and configurations
app = Flask(__name__)

# Apply CORS to the main Flask app
CORS(app, resources={r"/*": {"origins": ["http://localhost:3000"]}})

app.register_blueprint(routes)


# Initialize Redis
redis_client = Redis(host='localhost', port=6379, decode_responses=True)

# Define a static API key for authentication
API_KEY = "mysecretapikey"

# ALLOWED_IPS = ['127.0.0.1', '::1','192.168.65.1', '11.44.254.22']

# Middleware to check API key before each request
@app.before_request
def authenticate():
    # To allow specific IPs
    # user_ip = request.remote_addr
    # if user_ip not in ALLOWED_IPS:
    #     app.logger.info(user_ip)
    #     return jsonify({'error': 'Forbidden: Unauthorized IP address.'}), 403
    
    if request.method == 'OPTIONS':
        return jsonify({'status': 'OK'}), 200 
    # Bypass the middleware for health check route or other non-protected routes
    if request.endpoint == 'health_check':
        return

    # Get the API key from headers
    api_key = request.headers.get('x-api-key')
    
    # Validate the API key
    if api_key != API_KEY:
        return jsonify({'error': 'Unauthorized. Invalid or missing API key.'}), 401
@app.after_request
def add_cors_headers(response):
    origin = request.headers.get('Origin')
    # Allow CORS only from your React frontend
    if origin == 'http://localhost:3000':
        response.headers.add('Access-Control-Allow-Origin', origin)
        response.headers.add('Access-Control-Allow-Headers', 'Content-Type,Authorization,x-api-key')
        response.headers.add('Access-Control-Allow-Methods', 'GET,POST,OPTIONS,PUT,DELETE')
        response.headers.add('Access-Control-Allow-Credentials', 'true')  # Allow credentials
    return response

# Health check route
@app.route('/')
def health_check():
    return jsonify({'status': 'Transaction Processor API is up and running!!!'})


# Run the Flask app
if __name__ == '__main__':
    # app.run(debug=True)
    app.run(host='0.0.0.0', port=5000, debug=True)
