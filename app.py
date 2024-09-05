import dash
from dash import html, dcc, Input, Output, State
import dash_bootstrap_components as dbc
import base64
import io
import pandas as pd
import plotly.graph_objs as go
from decimal import Decimal, InvalidOperation
from typing import Dict, List, Tuple
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph, Spacer
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet

class TransactionProcessor:
    """Processes and manages financial transactions."""

    def __init__(self):
        self.processed_transactions: Dict[str, Dict[str, List[Dict]]] = {}
        self.bad_transactions: List[Dict] = []

    def generate_chart_of_accounts(self) -> str:
        """Generate a formatted chart of accounts."""
        chart = []
        for account, cards in self.processed_transactions.items():
            account_info = f"Account: {account}\n"
            for card, transactions in cards.items():
                balance = sum(t['amount'] for t in transactions)
                account_info += f"  Card: {card}, Balance: ${balance:.2f}\n"
            chart.append(account_info)
        return "\n".join(chart)

    def get_account_transactions(self, account: str, card: str) -> List[Dict]:
        """
        Get all transactions for a specific account and card.

        Args:
            account (str): The account name.
            card (str): The card number.

        Returns:
            List[Dict]: A list of transactions for the specified account and card.
        """
        if account not in self.processed_transactions or card not in self.processed_transactions[account]:
            return []
        return self.processed_transactions[account][card]

    def export_transactions_txt(self, account: str, card: str) -> str:
        """
        Export transactions for a specific account and card as a text string.

        Args:
            account (str): The account name.
            card (str): The card number.

        Returns:
            str: A formatted string containing transaction details.
        """
        transactions = self.get_account_transactions(account, card)
        output = f"Transactions for Account: {account}, Card: {card}\n\n"
        for t in transactions:
            output += f"Amount: ${t['amount']:.2f}, Type: {t['type']}, Description: {t['description']}\n"
        return output

    def export_transactions_pdf(self, account: str, card: str) -> str:
        """
        Export transactions for a specific account and card as a PDF.

        Args:
            account (str): The account name.
            card (str): The card number.

        Returns:
            str: Base64 encoded PDF content.
        """
        transactions = self.get_account_transactions(account, card)
        buffer = io.BytesIO()
        doc = SimpleDocTemplate(buffer, pagesize=letter)
        elements = []

        data = [["Amount", "Type", "Description"]]
        for t in transactions:
            data.append([f"${t['amount']:.2f}", t['type'], t['description']])

        table = Table(data)
        table.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.grey),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
            ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
            ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
            ('FONTSIZE', (0, 0), (-1, 0), 14),
            ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
            ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
            ('TEXTCOLOR', (0, 1), (-1, -1), colors.black),
            ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
            ('FONTNAME', (0, 1), (-1, -1), 'Helvetica'),
            ('FONTSIZE', (0, 1), (-1, -1), 12),
            ('TOPPADDING', (0, 1), (-1, -1), 6),
            ('BOTTOMPADDING', (0, 1), (-1, -1), 6),
            ('GRID', (0, 0), (-1, -1), 1, colors.black)
        ]))

        elements.append(table)
        doc.build(elements)

        pdf = buffer.getvalue()
        buffer.close()
        return base64.b64encode(pdf).decode('utf-8')

    def export_person_transactions(self, person: str) -> str:
        """
        Export all transactions for a specific person, including all their cards.

        Args:
            person (str): The person's name.

        Returns:
            str: Base64 encoded PDF content.
        """
        buffer = io.BytesIO()
        doc = SimpleDocTemplate(buffer, pagesize=letter, topMargin=50, bottomMargin=50, leftMargin=50, rightMargin=50)
        elements = []
        styles = getSampleStyleSheet()
        
        elements.append(Paragraph(f"Transaction Report for {person}", styles['Title']))
        elements.append(Spacer(1, 20))
        
        if person not in self.processed_transactions:
            elements.append(Paragraph("No transactions found.", styles['Normal']))
        else:
            for card, transactions in self.processed_transactions[person].items():
                elements.append(Paragraph(f"Card: {card}", styles['Heading2']))
                elements.append(Spacer(1, 10))
                
                data = [["Amount", "Type", "Description"]]
                for t in transactions:
                    data.append([f"${t['amount']:.2f}", t['type'], t['description']])
                
                col_widths = [doc.width/4, doc.width/4, doc.width/2]
                
                table = Table(data, colWidths=col_widths)
                table.setStyle(TableStyle([
                    ('BACKGROUND', (0, 0), (-1, 0), colors.lightblue),
                    ('TEXTCOLOR', (0, 0), (-1, 0), colors.black),
                    ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
                    ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
                    ('FONTSIZE', (0, 0), (-1, 0), 12),
                    ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
                    ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
                    ('TEXTCOLOR', (0, 1), (-1, -1), colors.black),
                    ('ALIGN', (0, 1), (-1, -1), 'LEFT'),
                    ('FONTNAME', (0, 1), (-1, -1), 'Helvetica'),
                    ('FONTSIZE', (0, 1), (-1, -1), 10),
                    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
                    ('GRID', (0, 0), (-1, -1), 1, colors.black),
                    ('BOX', (0, 0), (-1, -1), 2, colors.black),
                ]))
                elements.append(table)
                elements.append(Spacer(1, 20))

            total_balance = sum(sum(t['amount'] for t in transactions) for transactions in self.processed_transactions[person].values())
            elements.append(Paragraph(f"Total Balance: ${total_balance:.2f}", styles['Heading2']))

        def add_page_number(canvas, doc):
            page_num = canvas.getPageNumber()
            text = f"Page {page_num}"
            canvas.drawRightString(doc.pagesize[0] - 50, 30, text)

        doc.build(elements, onFirstPage=add_page_number, onLaterPages=add_page_number)
        pdf = buffer.getvalue()
        buffer.close()
        return base64.b64encode(pdf).decode('utf-8')

    def get_accounts_for_collections(self) -> List[str]:
        """
        Identify accounts that need to go to collections.

        Returns:
            List[str]: A list of accounts with negative balances.
        """
        collections = []
        for account, cards in self.processed_transactions.items():
            for card, transactions in cards.items():
                balance = sum(t['amount'] for t in transactions)
                if balance < 0:
                    collections.append(f"Account: {account}, Card: {card}, Balance: ${balance:.2f}")
        return collections

    def generate_balance_chart(self, top_n: int = 20) -> go.Figure:
        """
        Generate a bar chart of account balances for the top N accounts.

        Args:
            top_n (int): Number of top accounts to display.

        Returns:
            go.Figure: A plotly Figure object representing the balance chart.
        """
        account_balances = []
        for account, cards in self.processed_transactions.items():
            total_balance = sum(sum(t['amount'] for t in transactions) for transactions in cards.values())
            account_balances.append((account, total_balance))
        
        top_accounts = sorted(account_balances, key=lambda x: abs(x[1]), reverse=True)[:top_n]
        
        accounts = [f"{account} (Total)" for account, _ in top_accounts]
        balances = [float(balance) for _, balance in top_accounts]
        colors = ['red' if balance < 0 else 'green' for balance in balances]
        
        return go.Figure(data=[go.Bar(
            x=accounts,
            y=balances,
            marker_color=colors
        )], layout=go.Layout(
            title=f"Top {top_n} Account Balances",
            xaxis_title="Accounts",
            yaxis_title="Balance ($)"
        ))

    def process_transaction(self, row: pd.Series) -> Tuple[bool, str]:
        """
        Process a single transaction and update the processed_transactions dictionary.
        
        Args:
            row (pd.Series): A row from the transaction DataFrame.
        
        Returns:
            Tuple[bool, str]: A tuple containing a boolean indicating success and an error message if applicable.
        """
        try:
            account_name = row['Account Name']
            card_number = str(row['Card Number'])
            amount = Decimal(str(row['Transaction Amount']))
            transaction_type = row['Transaction Type']
            
            if account_name not in self.processed_transactions:
                self.processed_transactions[account_name] = {}
            
            if card_number not in self.processed_transactions[account_name]:
                self.processed_transactions[account_name][card_number] = []
            
            transaction = {
                'amount': amount,
                'type': transaction_type,
                'description': row['Description']
            }
            
            if transaction_type == 'Transfer':
                transaction['target_card'] = str(row['Target Card Number'])
            
            self.processed_transactions[account_name][card_number].append(transaction)
            
            return True, ""
        except KeyError as e:
            return False, f"Missing required field: {str(e)}"
        except InvalidOperation:
            return False, f"Invalid amount: {row['Transaction Amount']}"
        except Exception as e:
            return False, f"Unexpected error: {str(e)}"

    def process_transactions(self, df: pd.DataFrame) -> None:
        """
        Process all transactions in the DataFrame.
        
        Args:
            df (pd.DataFrame): The DataFrame containing all transactions.
        """
        for _, row in df.iterrows():
            success, error_message = self.process_transaction(row)
            if not success:
                self.bad_transactions.append({
                    'row': row.to_dict(),
                    'error': error_message
                })

    def reset(self) -> None:
        """Reset the processor state."""
        self.processed_transactions.clear()
        self.bad_transactions.clear()

def parse_csv(contents: str, filename: str) -> Tuple[pd.DataFrame, List[str]]:
    """
    Parse the contents of a CSV file, handling potential issues.
    
    Args:
        contents (str): The contents of the uploaded file.
        filename (str): The name of the uploaded file.
    
    Returns:
        Tuple[pd.DataFrame, List[str]]: A tuple containing the parsed DataFrame and a list of warnings/errors.
    """
    content_type, content_string = contents.split(',')
    decoded = base64.b64decode(content_string)
    warnings = []
    
    try:
        df = pd.read_csv(
            io.StringIO(decoded.decode('utf-8')),
            header=None,
            names=['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number'],
            dtype=str,
            keep_default_na=False,
            na_values=[''],
            skipinitialspace=True
        )
        
        df = df.dropna(how='all', axis=1)
        
        required_columns = ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type']
        for col in required_columns:
            empty_count = df[col].isna().sum()
            if empty_count > 0:
                warnings.append(f"Column '{col}' has {empty_count} empty values.")
        
        return df, warnings
    except Exception as e:
        return pd.DataFrame(), [f"There was an error processing this file: {str(e)}"]

# Initialize the Dash app and TransactionProcessor
app = dash.Dash(__name__, external_stylesheets=[dbc.themes.FLATLY], title="Transactions")
processor = TransactionProcessor()

# Define the layout
app.layout = dbc.Container([
    html.H1("Transaction Processor", className="my-4 text-center"),

    dbc.Row([
        dbc.Col([
            dbc.Card([
                dbc.CardBody([
                    html.H5("Upload Transactions", className="card-title"),
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
                        },
                        multiple=False
                    ),
                    dbc.Button('Reset', id='reset-button', color="secondary", className="mt-3"),
                ])
            ], className="mb-4"),

            dbc.Card([
                dbc.CardBody([
                    html.H5("Export Transactions", className="card-title"),
                    dcc.Dropdown(id='person-dropdown', placeholder="Select a person", className="mb-3"),
                    dbc.Button('Export All Transactions for Person', id='export-person-pdf-button', color="primary"),
                ])
            ]),
        ], md=4),

        dbc.Col([
            dbc.Card([
                dbc.CardBody([
                    html.H5("Processing Results", className="card-title"),
                    html.Div(id='output-data-upload'),
                ])
            ], className="mb-4"),

            dbc.Card([
                dbc.CardBody([
                    html.H5("Account Balance Chart", className="card-title"),
                    dcc.Graph(id='balance-chart'),
                ])
            ]),
        ], md=8),
    ]),

    # Download components
    dcc.Download(id="download-person-pdf"),
], fluid=True)

@app.callback(
    [Output('output-data-upload', 'children'),
     Output('balance-chart', 'figure'),
     Output('upload-data', 'contents'),
     Output('person-dropdown', 'options')],
    [Input('upload-data', 'contents'),
     Input('reset-button', 'n_clicks')],
    [State('upload-data', 'filename'),
     State('upload-data', 'last_modified')]
)
def update_output(content, reset_clicks, name, date):
    """
    Callback function to handle file upload and reset actions.

    Args:
        content: The content of the uploaded file.
        reset_clicks: Number of times the reset button has been clicked.
        name: Name of the uploaded file.
        date: Last modified date of the uploaded file.

    Returns:
        Tuple containing updated children for output-data-upload, balance chart figure,
        upload-data contents, and person-dropdown options.
    """
    ctx = dash.callback_context
    if not ctx.triggered:
        return dash.no_update, dash.no_update, dash.no_update, dash.no_update

    trigger_id = ctx.triggered[0]['prop_id'].split('.')[0]

    if trigger_id == 'reset-button':
        processor.reset()
        return html.Div("Data has been reset."), go.Figure(), None, []

    elif trigger_id == 'upload-data' and content is not None:
        df, warnings = parse_csv(content, name)
        if df.empty:
            return html.Div([html.P(warning, className="text-danger") for warning in warnings]), go.Figure(), dash.no_update, []

        processor.process_transactions(df)

        person_options = [{'label': person, 'value': person} for person in processor.processed_transactions.keys()]

        return html.Div([
            html.H6(f'File "{name}" has been processed.', className="text-success"),
            html.H6('Warnings:', className="mt-3"),
            html.Ul([html.Li(warning, className="text-warning") for warning in warnings]) if warnings else html.P("No warnings.", className="text-muted"),
            html.H6('Bad Transactions:', className="mt-3"),
            html.Ul([html.Li(f"Row: {t['row']}, Error: {t['error']}", className="text-danger") for t in processor.bad_transactions[:100]]) if processor.bad_transactions else html.P("No bad transactions.", className="text-muted")
        ]), processor.generate_balance_chart(), dash.no_update, person_options

    return dash.no_update, dash.no_update, dash.no_update, dash.no_update

@app.callback(
    Output("download-person-pdf", "data"),
    [Input("export-person-pdf-button", "n_clicks")],
    [State('person-dropdown', 'value')]
)
def export_person_pdf(n_clicks, person):
    """
    Callback function to export all transactions for a person as PDF.

    Args:
        n_clicks: Number of times the export button has been clicked.
        person: Selected person from the dropdown.

    Returns:
        Dict containing filename and content of the PDF to be downloaded.
    """
    if n_clicks is None or not person:
        return dash.no_update
    content = processor.export_person_transactions(person)
    return dcc.send_bytes(base64.b64decode(content), filename=f"{person}_all_transactions.pdf")

if __name__ == '__main__':
    app.run_server(debug=True)
