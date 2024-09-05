import dash
from dash import html, dcc, Input, Output, State
import base64
import io
import pandas as pd
import plotly.graph_objs as go
from decimal import Decimal, InvalidOperation
from typing import Dict, List, Tuple

class TransactionProcessor:
    def __init__(self):
        self.processed_transactions: Dict[str, Dict[str, Decimal]] = {}
        self.bad_transactions: List[Dict] = []

    def generate_chart_of_accounts(self):
        """Generate a formatted chart of accounts."""
        chart = []
        for account, cards in self.processed_transactions.items():
            account_info = f"Account: {account}\n"
            for card, balance in cards.items():
                account_info += f"  Card: {card}, Balance: ${balance:.2f}\n"
            chart.append(account_info)
        return "\n".join(chart)

    def get_accounts_for_collections(self):
        """Identify accounts that need to go to collections."""
        collections = []
        for account, cards in self.processed_transactions.items():
            for card, balance in cards.items():
                if balance < 0:
                    collections.append(f"Account: {account}, Card: {card}, Balance: ${balance:.2f}")
        return collections

    def generate_balance_chart(self):
        """Generate a bar chart of account balances."""
        accounts = []
        balances = []
        colors = []
        for account, cards in self.processed_transactions.items():
            for card, balance in cards.items():
                accounts.append(f"{account} ({card})")
                balances.append(float(balance))
                colors.append('red' if balance < 0 else 'green')
        
        return go.Figure(data=[go.Bar(
            x=accounts,
            y=balances,
            marker_color=colors
        )], layout=go.Layout(
            title="Account Balances",
            xaxis_title="Accounts",
            yaxis_title="Balance ($)"
        ))

    def process_transaction(self, row: pd.Series) -> Tuple[bool, str]:
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
            
            if account_name not in self.processed_transactions:
                self.processed_transactions[account_name] = {}
            
            if card_number not in self.processed_transactions[account_name]:
                self.processed_transactions[account_name][card_number] = Decimal('0')
            
            if transaction_type == 'Credit':
                self.processed_transactions[account_name][card_number] += amount
            elif transaction_type == 'Debit':
                self.processed_transactions[account_name][card_number] -= amount
            elif transaction_type == 'Transfer':
                target_card = str(row['Target Card Number'])
                if target_card not in self.processed_transactions[account_name]:
                    self.processed_transactions[account_name][target_card] = Decimal('0')
                self.processed_transactions[account_name][card_number] -= amount
                self.processed_transactions[account_name][target_card] += amount
            else:
                return False, f"Invalid transaction type: {transaction_type}"
            
            return True, ""
        except KeyError as e:
            return False, f"Missing required field: {str(e)}"
        except InvalidOperation:
            return False, f"Invalid amount: {row['Transaction Amount']}"
        except Exception as e:
            return False, f"Unexpected error: {str(e)}"

    def process_transactions(self, df: pd.DataFrame):
        """
        Process all transactions in the DataFrame.
        
        Args:
        df (pd.DataFrame): The DataFrame containing all transactions
        """
        for _, row in df.iterrows():
            success, error_message = self.process_transaction(row)
            if not success:
                self.bad_transactions.append({
                    'row': row.to_dict(),
                    'error': error_message
                })

    def reset(self):
        """Reset the processor state."""
        self.processed_transactions.clear()
        self.bad_transactions.clear()

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

# Initialize the Dash app and TransactionProcessor
app = dash.Dash(__name__)
processor = TransactionProcessor()

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
    
    # Div to display the balance chart
    dcc.Graph(id='balance-chart')
])

# Combined callback for file upload and reset
@app.callback(
    [Output('output-data-upload', 'children'),
     Output('balance-chart', 'figure'),
     Output('upload-data', 'contents')],
    [Input('upload-data', 'contents'),
     Input('reset-button', 'n_clicks')],
    [State('upload-data', 'filename'),
     State('upload-data', 'last_modified')]
)
def update_output(content, n_clicks, name, date):
    ctx = dash.callback_context
    if not ctx.triggered:
        return dash.no_update, dash.no_update, dash.no_update
    
    trigger_id = ctx.triggered[0]['prop_id'].split('.')[0]
    
    if trigger_id == 'reset-button':
        processor.reset()
        return html.Div("Data has been reset."), go.Figure(), None
    
    elif trigger_id == 'upload-data' and content is not None:
        df, warnings = parse_csv(content, name)
        if df.empty:
            return html.Div([html.P(warning) for warning in warnings]), go.Figure(), dash.no_update
        
        processor.process_transactions(df)
        
        chart_of_accounts = processor.generate_chart_of_accounts()
        accounts_for_collections = processor.get_accounts_for_collections()
        
        return html.Div([
            html.H5(f'File "{name}" has been processed.'),
            html.H6('Warnings:'),
            html.Ul([html.Li(warning) for warning in warnings]) if warnings else html.P("No warnings."),
            html.H6('Chart of Accounts:'),
            html.Pre(chart_of_accounts),
            html.H6('Accounts for Collections:'),
            html.Ul([html.Li(account) for account in accounts_for_collections]) if accounts_for_collections else html.P("No accounts for collections."),
            html.H6('Bad Transactions:'),
            html.Ul([html.Li(f"Row: {t['row']}, Error: {t['error']}") for t in processor.bad_transactions]) if processor.bad_transactions else html.P("No bad transactions.")
        ]), processor.generate_balance_chart(), dash.no_update
    
    return dash.no_update, dash.no_update, dash.no_update

if __name__ == '__main__':
    app.run_server(debug=True)
