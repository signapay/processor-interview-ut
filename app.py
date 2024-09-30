import streamlit as st
import pandas as pd
from typing import Dict, Optional

# State Initialization
def initialize_state():
    """Initializes or resets the session state variables."""
    st.session_state['accounts'] = {}
    st.session_state['bad_transactions'] = pd.DataFrame()
    st.session_state['card_to_account'] = {}
    st.session_state['deferred_transfers'] = []

if 'accounts' not in st.session_state:
    initialize_state()

# Helper Functions

def load_transaction_file(uploaded_file) -> Optional[pd.DataFrame]:
    """
    Loads and parses the uploaded CSV file into a DataFrame without headers.
    Assigns appropriate column names.
    Returns None if an error occurs.
    """
    try:
        # Define the column names
        column_names = [
            'Account Name',
            'Card Number',
            'Transaction Amount',
            'Transaction Type',
            'Description',
            'Target Card Number'
        ]
        data = pd.read_csv(uploaded_file, names=column_names, header=None)
        return data
    except Exception as e:
        st.error(f"Error reading the file: {e}")
        return None


def validate_transaction(row: pd.Series) -> Optional[str]:
    """
    Validates a single transaction row.
    Returns an error message if validation fails, otherwise None.
    """
    required_fields = [
        'Account Name',
        'Card Number',
        'Transaction Amount',
        'Transaction Type',
        'Description'
    ]
    for field in required_fields:
        if pd.isna(row.get(field)) or row.get(field) == '':
            return f"Missing required field: {field}"

    transaction_type = str(row['Transaction Type']).strip()
    if transaction_type not in ['Credit', 'Debit', 'Transfer']:
        return f"Invalid Transaction Type: {transaction_type}"
    
    amount = row.get("Transaction Amount")
    try:
        _ = float(amount)
    except ValueError:
        return "Invalid Transaction Amount format"

    if pd.isna(row.get('Card Number')) or row.get('Card Number') == '':
        return "Missing Card Number"
    else:
        # Ensure Card Number is valid
        try:
            _ = str(int(float(row['Card Number'])))
        except ValueError:
            return "Invalid Card Number format"

    if transaction_type == 'Transfer':
        if pd.isna(row.get('Target Card Number')) or row.get('Target Card Number') == '':
            return "Missing Target Card Number for Transfer"
        else:
            # Ensure Target Card Number is valid
            try:
                _ = str(int(float(row['Target Card Number'])))
            except ValueError:
                return "Invalid Target Card Number format"
    return None  # No validation errors

def process_transaction(row: pd.Series, accounts: Dict[str, Dict[str, float]], card_to_account: Dict[str, str]):
    """
    Processes a single valid transaction and updates the accounts dictionary.
    """
    account_name = str(row['Account Name']).strip()
    card_number = str(int(float(row['Card Number'])))
    transaction_amount = float(row['Transaction Amount'])
    transaction_type = str(row['Transaction Type']).strip()
    description = str(row['Description']).strip()
    target_card_number = row.get('Target Card Number')

    card_to_account[card_number] = account_name
    # Ensure the account and card exist in the accounts dictionary
    accounts.setdefault(account_name, {}).setdefault(card_number, 0.0)

    if transaction_type == 'Credit':
        accounts[account_name][card_number] += transaction_amount
    elif transaction_type == 'Debit':
        accounts[account_name][card_number] -= transaction_amount
    elif transaction_type == 'Transfer':
        target_card_number = str(int(float(target_card_number)))
        target_account_name = card_to_account.get(target_card_number)

        if target_account_name is None:
            return False
        
        accounts[target_account_name].setdefault(target_card_number, 0.0)
  
        accounts[account_name][card_number] -= transaction_amount
        accounts[target_account_name][target_card_number] += transaction_amount
    
    return True


def process_transactions(data: pd.DataFrame):
    """
    Processes all transactions in the provided DataFrame.
    Updates the accounts and bad_transactions in the session state.
    """
    accounts = st.session_state['accounts']
    card_to_account = st.session_state['card_to_account']
    deferred_transfers = st.session_state['deferred_transfers']

    for _, row in data.iterrows():
        error_message = validate_transaction(row)
        if error_message:
            row['Error'] = error_message
            st.session_state['bad_transactions'] = st.session_state['bad_transactions']._append(row, ignore_index=True)
        else:
            transaction_type = str(row['Transaction Type']).strip()
            if transaction_type != 'Transfer':
                try:
                    _ = process_transaction(row, accounts, card_to_account)
                except Exception as e:
                    row['Error'] = f"Processing error: {e}"
                    st.session_state['bad_transactions'] = st.session_state['bad_transactions']._append(row, ignore_index=True)
            else:
                deferred_transfers.append(row)
    
    # Process deferred transfers
    transfers_processed = True
    while transfers_processed and deferred_transfers:
        transfers_processed = False
        remaining_transfers = []

        for row in deferred_transfers:
            try:
                success = process_transaction(row, accounts, card_to_account)
                if success:
                    transfers_processed = True
                else:
                    remaining_transfers.append(row)
            except Exception as e:
                row['Error'] = f"Processing error: {e}"
                st.session_state['bad_transactions'] = st.session_state['bad_transactions']._append(row, ignore_index=True)
        deferred_transfers = remaining_transfers
    
    # remaining deffered_transfers are bad transactions
    for row in deferred_transfers:
        row['Error'] = "Unkown target card number"
        st.session_state['bad_transactions'] = st.session_state['bad_transactions'].append(row, ignore_index=True)



def get_accounts_dataframe() -> pd.DataFrame:
    """
    Constructs a DataFrame of accounts, cards, and balances from the session state.
    """
    accounts_list = []
    for account_name, cards in st.session_state['accounts'].items():
        for card_number, balance in cards.items():
            accounts_list.append({
                'Account Name': account_name,
                'Card Number': card_number,
                'Balance': balance
            })
    return pd.DataFrame(accounts_list)

def get_collections_dataframe(accounts_df: pd.DataFrame) -> pd.DataFrame:
    """
    Filters accounts with negative balances for collections.
    """
    return accounts_df[accounts_df['Balance'] < 0.0]

def display_dataframe(title: str, df: pd.DataFrame):
    """
    Displays a DataFrame with a given title. Handles empty DataFrames gracefully.
    """
    st.subheader(title)
    if not df.empty:
        st.dataframe(df)
    else:
        st.write(f"No data to display for {title.lower()}.")

# Main Application Logic

def main():
    st.title("Transaction Processor")
    st.write("Upload a CSV file containing transactions to process them.")

    # Sidebar for file upload and reset button
    with st.sidebar:
        st.header("Options")
        uploaded_file = st.file_uploader("Choose a CSV file", type="csv")
        if st.button("Reset System"):
            initialize_state()
            st.success("System has been reset.")

    # Process the uploaded file
    if uploaded_file is not None:
        data = load_transaction_file(uploaded_file)
        if data is not None:
            process_transactions(data)
            st.success("File processed successfully.")

            # Generate reports
            accounts_df = get_accounts_dataframe()
            collections_df = get_collections_dataframe(accounts_df)

            # Display reports
            display_dataframe("Chart of Accounts", accounts_df)
            display_dataframe("Accounts Needing Collections", collections_df)
            display_dataframe("Bad Transactions", st.session_state['bad_transactions'])

if __name__ == "__main__":
    main()
