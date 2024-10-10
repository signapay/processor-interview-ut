from flask import Flask, jsonify, request
from flask_cors import CORS
from redis import Redis
from routes import routes
import pandas as pd
import os

# Initialize Flask app and configurations
app = Flask(__name__)
CORS(app)
app.register_blueprint(routes)


# Initialize Redis
redis_client = Redis(host='localhost', port=6379, decode_responses=True)

# Health check route
@app.route('/')
def health_check():
    return jsonify({'status': 'Transaction Processor API is up and running!!!'})


# Run the Flask app
if __name__ == '__main__':
    # app.run(debug=True)
    app.run(host='0.0.0.0', port=5000, debug=True)
