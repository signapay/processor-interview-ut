import streamlit as st
import pandas as pd
from typing import Dict, Optional

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
    
    # Ensure amount is numeric value
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