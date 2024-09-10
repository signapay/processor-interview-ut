from flask import Flask, render_template, request, redirect, url_for, flash, session
import csv
import math
import io

app = Flask(__name__)
app.secret_key = 'secretkey'  # Needed for flashing messages

# Placeholder for storing processed data
transactions = {
    'accounts': [],
    'collections': [],
    'bad_transactions': []
}

# Home route to upload the file
@app.route('/', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        file = request.files['file']
        if file.filename.endswith('.csv'):
            process_csv(file)  # Call the function to process CSV

            # If 'files_uploaded' is not in the session, initialize it as an empty list
            if 'files_uploaded' not in session:
                session['files_uploaded'] = []

            # Append the file name to the list of uploaded files
            session['files_uploaded'].append(file.filename)
            session.modified = True  # Ensure that the session is updated

            flash('File processed successfully!', 'success')
            return redirect(url_for('upload_file'))  # Redirect to the same page to display the card
        else:
            flash('Invalid file format. Please upload a CSV file.', 'danger')

    # Pass the list of files to the template
    return render_template('upload.html', files_uploaded=session.get('files_uploaded', []))


# Route for Chart of Accounts
@app.route('/accounts/<filename>', methods=['GET'])
def report_accounts(filename):
    # Ensure the transactions data for the given file is retrieved
    accounts = transactions.get(filename, {}).get('accounts', [])
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('records_per_page', 10, type=int)
    total_pages = (len(accounts) + per_page - 1) // per_page

    # Ensure current page isn't out of bounds
    if page > total_pages:
        page = total_pages

    # Paginate the accounts
    start = (page - 1) * per_page
    end = start + per_page
    accounts_on_page = accounts[start:end]

    return render_template('report_accounts.html', accounts=accounts_on_page, page=page, per_page=per_page, total_pages=total_pages, filename=filename)


# Route for Collections
@app.route('/collections/<filename>')
def report_collections(filename):
    collections = transactions.get(filename, {}).get('collections', [])
    return render_template('report_collections.html', collections=collections, filename=filename)


@app.route('/bad-transactions/<filename>')
def report_bad_transactions(filename):
    bad_transactions = transactions.get(filename, {}).get('bad_transactions', [])
    return render_template('report_bad_transactions.html', bad_transactions=bad_transactions, filename=filename)


@app.route('/clear-files', methods=['POST'])
def clear_files():
    session.pop('files_uploaded', None)  # Clear uploaded files from the session
    transactions.clear()
    flash('All uploaded files cleared.', 'success')
    return redirect(url_for('upload_file'))

@app.route('/delete-file/<filename>', methods=['POST'])
def delete_file(filename):
    if 'files_uploaded' in session and filename in session['files_uploaded']:
        session['files_uploaded'].remove(filename)  # Remove file from session
        transactions.pop(filename, None)  # Remove associated data from the transactions
        session.modified = True
        flash(f'File "{filename}" has been deleted successfully!', 'success')
    else:
        flash(f'File "{filename}" not found!', 'danger')
    return redirect(url_for('upload_file'))  # Redirect back to the upload page


# Function to process CSV file
import io


def process_csv(file):
    global transactions
    file_name = file.filename

    # Initialize a new dictionary for this file's transactions
    transactions[file_name] = {
        'accounts': [],
        'collections': [],
        'bad_transactions': []
    }

    # Open file in text mode
    stream = io.TextIOWrapper(file.stream, encoding='utf-8')
    reader = csv.DictReader(stream, fieldnames=[
        'Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Extra Column'
    ])

    # Skip the first row since it's data, not headers
    next(reader)

    for row in reader:
        try:
            account_name = row['Account Name']
            card_number = row['Card Number']
            amount = float(row['Transaction Amount'])
            transaction_type = row['Transaction Type']

            # Add to accounts (simplified logic for now)
            transactions[file_name]['accounts'].append({
                'account': account_name,
                'card': card_number,
                'amount': amount
            })

            # Check for negative balance (collections)
            if amount < 0:
                transactions[file_name]['collections'].append({
                    'account': account_name,
                    'balance': amount
                })

        except Exception as e:
            # Handle bad transactions
            transactions[file_name]['bad_transactions'].append(row)


if __name__ == '__main__':
    app.run(debug=True)