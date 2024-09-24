from flask import Flask, render_template, request, redirect, url_for, flash
import csv

app = Flask(__name__)
app.secret_key = "4444"  # Used for flashing messages

# In-memory storage to keep track of transactions and accounts
transactions = []
accounts = {}

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/upload', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        flash('No file part')
        return redirect(url_for('index'))

    file = request.files['file']
    
    if file.filename == '':
        flash('No selected file')
        return redirect(url_for('index'))
    
    if file:
        # Read the CSV file and process transactions
        file_data = file.read().decode('utf-8')
        csv_reader = csv.reader(file_data.splitlines())

        for row in csv_reader:
            process_transaction(row)
        
        flash('File successfully uploaded and processed')
        return redirect(url_for('report'))

def process_transaction(row):
    global transactions, accounts

    account_name = row[0]
    card_number = row[1]
    transaction_amount = float(row[2])
    transaction_type = row[3]
    description = row[4]
    
    if account_name not in accounts:
        accounts[account_name] = {}

    if card_number not in accounts[account_name]:
        accounts[account_name][card_number] = 0  # Initialize balance to 0

    # Update the account balance based on transaction type
    if transaction_type == 'Credit':
        accounts[account_name][card_number] += transaction_amount
    elif transaction_type == 'Debit':
        accounts[account_name][card_number] -= transaction_amount
    elif transaction_type == 'Transfer' and len(row) == 6:
        target_card_number = row[5]
        if target_card_number in accounts[account_name]:
            accounts[account_name][card_number] -= transaction_amount
            accounts[account_name][target_card_number] += transaction_amount
        else:
            transactions.append({
                'status': 'error', 
                'error': 'Target card not found', 
                'transaction': row
            })
    else:
        transactions.append({
            'status': 'error',
            'error': 'Invalid transaction',
            'transaction': row
        })

@app.route('/report')
def report():
    collections = []
    bad_transactions = []

    for account, cards in accounts.items():
        for card, balance in cards.items():
            if balance < 0:
                collections.append({
                    'account': account,
                    'card': card,
                    'balance': balance
                })

    for transaction in transactions:
        if transaction['status'] == 'error':
            bad_transactions.append(transaction)

    return render_template('report.html', accounts=accounts, collections=collections, bad_transactions=bad_transactions)

@app.route('/reset')
def reset():
    global transactions, accounts
    transactions = []
    accounts = {}
    flash('System reset successfully.')
    return redirect(url_for('index'))


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8000, debug=True)