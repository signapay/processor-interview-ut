import streamlit as st
import pandas as pd
from typing import Dict, Optional

from helper import *

# State Initialization
def initialize_state():
    """Initializes or resets the session state variables."""
    st.session_state['accounts'] = {}
    st.session_state['bad_transactions'] = pd.DataFrame()
    st.session_state['card_to_account'] = {}
    st.session_state['deferred_transfers'] = []

if 'accounts' not in st.session_state:
    initialize_state()


def process_transactions(data: pd.DataFrame):
    """
    Processes all transactions in the provided DataFrame.
    Updates the accounts and bad_transactions in the session state.
    """
    accounts = st.session_state['accounts']
    card_to_account = st.session_state['card_to_account']
    deferred_transfers = st.session_state['deferred_transfers']

    # Process all non-transfer transactions
    for _, row in data.iterrows():
        error_message = validate_transaction(row)
        if error_message:
            row['Error'] = error_message
            st.session_state['bad_transactions'] = st.session_state['bad_transactions']._append(row, ignore_index=True)
        else:
            transaction_type = str(row['Transaction Type']).strip()
            if transaction_type != 'Transfer':
                _ = process_transaction(row, accounts, card_to_account)
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
        st.session_state['bad_transactions'] = st.session_state['bad_transactions']._append(row, ignore_index=True)



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


def main():
    st.title("Transaction Processor")
    st.write("Upload a CSV file containing transactions to process them.")

    # Sidebar for file upload and reset button
    uploaded_file = None
    with st.sidebar:
        st.header("Options")
        uploaded_file = st.file_uploader("Choose a CSV file", type="csv")
        if st.button("Reset System"):
            initialize_state()
            uploaded_file = None
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
