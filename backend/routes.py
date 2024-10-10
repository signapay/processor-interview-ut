from flask import Blueprint, request, jsonify
from services.file_service import parse_csv
from services.transaction_service import process_transaction
from services.report_service import generate_report
from utils.redis_client import redis_client
import pandas as pd
from flask import current_app as app

routes = Blueprint('routes', __name__)

# Route for uploading a file
@routes.route('/upload', methods=['POST'])
def upload_file():
    # checking file request type
    if 'file' not in request.files:
        return jsonify({'error': 'No file part in the request'}), 400

    file = request.files['file']
    # check for csv file
    if file.filename == '' or not file.filename.lower().endswith('.csv'):
        return jsonify({'error': 'Invalid file format. Please upload a CSV file.'}), 400
    # Parse file and process transactions
    df, error_message = parse_csv(file)
    
    # Check if the file has a valid filename and is a CSV
    if df.empty:
        return jsonify({"error": f"{error_message}"}), 400
    # check if csv file
    if file.filename == '' or not file.filename.endswith('.csv'):
        return jsonify({'error': 'Invalid file format. Please upload a CSV file.'}), 400

    # storing valid and bad transactions
    valid_transactions = []
    bad_transactions = []
    # Process each transaction in the file
    for _, transaction in df.iterrows():
        # Converting NaN values to None
        transaction = transaction.where(pd.notnull(transaction), None)
        transaction_dict = transaction.to_dict()
        # app.logger.info(transaction_dict)
        success, message = process_transaction(transaction_dict, redis_client)
        if not success:
            # print(f"Failed to process transaction: {message}")
            bad_transactions.append(transaction_dict)
        else:
            valid_transactions.append(transaction_dict)
  
    # Store bad transactions for further review
    for bad_transaction in bad_transactions:
        redis_client.lpush('bad_transactions', str(bad_transaction))

    return jsonify({
        "message": "File uploaded and transactions processed successfully",
        "total_transactions": len(valid_transactions),
        "bad_transactions_count": len(bad_transactions)
    }), 200
    
# Route to reset the system
@routes.route('/reset', methods=['POST'])
def reset_system():
    redis_client.flushdb()  # Clears all data in db
    return jsonify({"message": "System reset successfully"}), 200

# Route to fetch report
@routes.route('/reports', methods=['GET'])
def get_reports():
    try:
        # Generate report by calling the function from report_service
        report = generate_report(redis_client)
        return jsonify(report), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@routes.route('/collections', methods=['GET'])
def get_collections():
    try:
        collections = []
        # Get all keys (account names)
        keys = redis_client.keys()  
        for account in keys:
            # Check if the key is a hash type before calling hgetall
            if redis_client.type(account) == 'hash':
                cards = redis_client.hgetall(account)  
                for card, balance in cards.items():
                    # Check if balance is negative
                    if float(balance) < 0:  
                        collections.append({
                            'Account Name': account,
                            'Card Number': card,
                            'Balance': float(balance)
                        })
        return jsonify(collections), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@routes.route('/bad-transactions', methods=['GET'])
def get_bad_transactions():
    try:
        # fetching the bad transactions from the db
        bad_transactions = redis_client.lrange('bad_transactions', 0, -1)
        return jsonify([eval(transaction) for transaction in bad_transactions]), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
