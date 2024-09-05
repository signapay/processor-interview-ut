import dash
from dash import html, dcc, Input, Output, State
import base64
import io
import pandas as pd
from decimal import Decimal, InvalidOperation
from typing import Dict, List, Tuple

# Initialize the Dash app
app = dash.Dash(__name__)

# Global variable to store processed transactions
processed_transactions: Dict[str, Dict[str, Decimal]] = {}
bad_transactions: List[Dict] = []

def process_transaction(row: pd.Series) -> Tuple[bool, str]:
    """
    Process a single transaction and update the processed_transactions dictionary.
    
    Args:
    row (pd.Series): A row from the transaction DataFrame
    
    Returns:
    Tuple[bool, str]: A tuple containing a boolean indicating success and an error message if applicable
    """
    try:
        account_name = row['Account Name']
        card_number = str(row['Card Number'])
        amount = Decimal(str(row['Transaction Amount']))
        transaction_type = row['Transaction Type']
        
        if account_name not in processed_transactions:
            processed_transactions[account_name] = {}
        
        if card_number not in processed_transactions[account_name]:
            processed_transactions[account_name][card_number] = Decimal('0')
        
        if transaction_type == 'Credit':
            processed_transactions[account_name][card_number] += amount
        elif transaction_type == 'Debit':
            processed_transactions[account_name][card_number] -= amount
        elif transaction_type == 'Transfer':
            target_card = str(row['Target Card Number'])
            if target_card not in processed_transactions[account_name]:
                processed_transactions[account_name][target_card] = Decimal('0')
            processed_transactions[account_name][card_number] -= amount
            processed_transactions[account_name][target_card] += amount
        else:
            return False, f"Invalid transaction type: {transaction_type}"
        
        return True, ""
    except KeyError as e:
        return False, f"Missing required field: {str(e)}"
    except InvalidOperation:
        return False, f"Invalid amount: {row['Transaction Amount']}"
    except Exception as e:
        return False, f"Unexpected error: {str(e)}"

def process_transactions(df: pd.DataFrame) -> Tuple[Dict[str, Dict[str, Decimal]], List[Dict]]:
    """
    Process all transactions in the DataFrame.
    
    Args:
    df (pd.DataFrame): The DataFrame containing all transactions
    
    Returns:
    Tuple[Dict[str, Dict[str, Decimal]], List[Dict]]: A tuple containing the processed transactions and bad transactions
    """
    global processed_transactions, bad_transactions
    
    for _, row in df.iterrows():
        success, error_message = process_transaction(row)
        if not success:
            bad_transactions.append({
                'row': row.to_dict(),
                'error': error_message
            })
    
    return processed_transactions, bad_transactions

def parse_csv(contents: str, filename: str) -> Tuple[pd.DataFrame, List[str]]:
    """
    Parse the contents of a CSV file, handling potential issues.
    
    Args:
    contents (str): The contents of the uploaded file
    filename (str): The name of the uploaded file
    
    Returns:
    Tuple[pd.DataFrame, List[str]]: A tuple containing the parsed DataFrame and a list of warnings/errors
    """
    content_type, content_string = contents.split(',')
    decoded = base64.b64decode(content_string)
    warnings = []
    
    try:
        # Read the CSV file, handling trailing commas and no header
        df = pd.read_csv(
            io.StringIO(decoded.decode('utf-8')),
            header=None,
            names=['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number'],
            dtype=str,
            keep_default_na=False,
            na_values=[''],
            skipinitialspace=True
        )
        
        # Remove empty columns (caused by trailing commas)
        df = df.dropna(how='all', axis=1)
        
        # Check for empty values in required columns
        required_columns = ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type']
        for col in required_columns:
            empty_count = df[col].isna().sum()
            if empty_count > 0:
                warnings.append(f"Column '{col}' has {empty_count} empty values.")
        
        return df, warnings
    except Exception as e:
        return pd.DataFrame(), [f"There was an error processing this file: {str(e)}"]

# Define the layout
app.layout = html.Div([
    html.H1("Transaction Processor"),
    
    # File Upload component
    dcc.Upload(
        id='upload-data',
        children=html.Div([
            'Drag and Drop or ',
            html.A('Select Files')
        ]),
        style={
            'width': '100%',
            'height': '60px',
            'lineHeight': '60px',
            'borderWidth': '1px',
            'borderStyle': 'dashed',
            'borderRadius': '5px',
            'textAlign': 'center',
            'margin': '10px'
        },
        multiple=False
    ),
    
    # Reset button
    html.Button('Reset', id='reset-button', n_clicks=0),
    
    # Div to display processing result
    html.Div(id='output-data-upload'),
])

# Callback for file upload
@app.callback(
    Output('output-data-upload', 'children'),
    Input('upload-data', 'contents'),
    State('upload-data', 'filename'),
    State('upload-data', 'last_modified')
)
def update_output(content, name, date):
    if content is not None:
        df, warnings = parse_csv(content, name)
        if df.empty:
            return html.Div([html.P(warning) for warning in warnings])
        
        processed_transactions, bad_transactions = process_transactions(df)
        
        return html.Div([
            html.H5(f'File "{name}" has been processed.'),
            html.H6('Warnings:'),
            html.Ul([html.Li(warning) for warning in warnings]) if warnings else html.P("No warnings."),
            html.H6('Chart of Accounts:'),
            html.Pre(str(processed_transactions)),
            html.H6('Bad Transactions:'),
            html.Pre(str(bad_transactions))
        ])

# Callback for reset button
@app.callback(
    Output('upload-data', 'contents'),
    Input('reset-button', 'n_clicks')
)
def reset_data(n_clicks):
    if n_clicks > 0:
        return None

if __name__ == '__main__':
    app.run_server(debug=True)
