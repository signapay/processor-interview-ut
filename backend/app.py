from flask import Flask, request, jsonify, session
import pandas as pd
from io import StringIO
from flask_cors import CORS
from datetime import timedelta
from flask_session import Session

app = Flask(__name__)
CORS(app, supports_credentials=True)
app.secret_key = 'secretkey'

app.config['SESSION_TYPE'] = 'filesystem'
app.config['SESSION_FILE_DIR'] = './flask_session/'
app.config['SESSION_PERMANENT'] = True 
app.config['PERMANENT_SESSION_LIFETIME'] = timedelta(minutes=30)
Session(app)

# Route to upload transaction files
@app.route('/upload', methods=['POST'])
def upload_file():
    try:
        # Ensure 'transactions' exists in the session
        if 'transactions' not in session:
            session['transactions'] = []

        file = request.files['file']
        if not file:
            return jsonify({'error': 'No file uploaded'}), 400

        session.permanent = True

        # Read the CSV file
        data = pd.read_csv(file)

        # Process the transactions
        valid_transactions, invalid_transactions = parse_transactions(data)

        # Append valid transactions to session storage
        session['transactions'].extend(valid_transactions)

        # Log to check if the transactions are being saved correctly
        print("Transactions saved in session:", session['transactions'])

        # Store the invalid transactions separately
        session['bad_transactions'] = session.get('bad_transactions', []) + invalid_transactions

        return jsonify({'message': 'File processed successfully'}), 200
    except Exception as e:
        print(f"Error during file upload: {e}")
        return jsonify({'error': 'Failed to process file'}), 500
    
# Route to reset the session
@app.route('/reset', methods=['POST'])
def reset_system():

    # Clear the entire session
    session.clear()
    
    # Reinitialize session data
    session['transactions'] = []
    session['bad_transactions'] = []
    
    return jsonify({'message': 'System reset successfully'}), 200


@app.route('/reports', methods=['GET'])
def get_reports():

    # Log session contents
    print("Session when generating reports: ", session)

    if 'transactions' not in session or not session['transactions']:
        return jsonify({'error': 'No transactions available'}), 400

    transactions = session['transactions']
    valid_transactions_df = pd.DataFrame(transactions)

    # Generate Chart of Accounts
    chart_of_accounts = valid_transactions_df.groupby(['Account Name', 'Card Number']).agg({'Transaction Amount': 'sum'}).reset_index()

    # Generate Collection List (negative balance)
    collection_list = chart_of_accounts[chart_of_accounts['Transaction Amount'] < 0]

    # Retrieve bad transactions
    bad_transactions = session.get('bad_transactions', [])

    return jsonify({
        'chart_of_accounts': chart_of_accounts.to_dict(orient='records'),
        'chart_of_accounts_count': len(chart_of_accounts),
        'collection_list': collection_list.to_dict(orient='records'),
        'collection_list_count': len(collection_list),
        'bad_transactions': bad_transactions,
        'bad_transactions_count': len(bad_transactions)
    }), 200

# Utility function to parse transactions
def parse_transactions(df):
    valid_transactions = []
    invalid_transactions = []

    for index, row in df.iterrows():
        try:
            account_name = row[0]
            card_number = int(row[1])
            transaction_amount = float(row[2])
            transaction_type = row[3]
            description = row[4]
            target_card_number = row[5] if not pd.isna(row[5]) else None

            if transaction_type not in ['Credit', 'Debit', 'Transfer']:
                raise ValueError(f"Invalid transaction type: {transaction_type}")
            
            if transaction_type == 'Transfer' and target_card_number is None:
                raise ValueError("Transfer type transaction missing target card number")

            valid_transactions.append({
                'Account Name': account_name,
                'Card Number': card_number,
                'Transaction Amount': transaction_amount,
                'Transaction Type': transaction_type,
                'Description': description,
                'Target Card Number': target_card_number
            })
        except Exception as e:
            invalid_transactions.append({
                'Row': index,
                'Error': str(e),
                'Data': row.tolist()
            })
    
    return valid_transactions, invalid_transactions

if __name__ == '__main__':
    app.run(debug=True)

