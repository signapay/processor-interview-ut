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
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_RIGHT

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

        styles = getSampleStyleSheet()
        styles.add(ParagraphStyle(name='Center', alignment=TA_CENTER))
        styles.add(ParagraphStyle(name='Right', alignment=TA_RIGHT))

        elements.append(Paragraph(f"Transaction Report for Account: {account}, Card: {card}", styles['Center']))
        elements.append(Spacer(1, 12))

        data = [["Date", "Amount", "Type", "Description"]]
        for t in transactions:
            data.append([t.get('date', 'N/A'), f"${t['amount']:.2f}", t['type'], t['description']])

        table = Table(data)
        table.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.blue),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
            ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
            ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
            ('FONTSIZE', (0, 0), (-1, 0), 12),
            ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
            ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
            ('TEXTCOLOR', (0, 1), (-1, -1), colors.black),
            ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
            ('FONTNAME', (0, 1), (-1, -1), 'Helvetica'),
            ('FONTSIZE', (0, 1), (-1, -1), 10),
            ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
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
        styles.add(ParagraphStyle(name='Center', alignment=TA_CENTER))
        styles.add(ParagraphStyle(name='Right', alignment=TA_RIGHT))
        
        elements.append(Paragraph(f"Transaction Report for {person}", styles['Center']))
        elements.append(Spacer(1, 20))
        
        if person not in self.processed_transactions:
            elements.append(Paragraph("No transactions found.", styles['Normal']))
        else:
            for card, transactions in self.processed_transactions[person].items():
                elements.append(Paragraph(f"Card: {card}", styles['Heading2']))
                elements.append(Spacer(1, 10))
                
                data = [["Date", "Amount", "Type", "Description"]]
                for t in transactions:
                    data.append([t.get('date', 'N/A'), f"${t['amount']:.2f}", t['type'], t['description']])
                
                col_widths = [doc.width/6, doc.width/6, doc.width/6, doc.width/2]
                
                table = Table(data, colWidths=col_widths)
                table.setStyle(TableStyle([
                    ('BACKGROUND', (0, 0), (-1, 0), colors.blue),
                    ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
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
            elements.append(Paragraph(f"Total Balance: ${total_balance:.2f}", styles['Right']))

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
            account_balance = Decimal('0')
            for card, transactions in cards.items():
                card_balance = sum(Decimal(str(t['amount'])) for t in transactions)
                account_balance += card_balance
                if card_balance < 0:
                    collections.append(f"Account: {account}, Card: {card}, Balance: ${card_balance:.2f}")
            if account_balance < 0 and not any(account in item for item in collections):
                collections.append(f"Account: {account}, Total Balance: ${account_balance:.2f}")
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
                'description': row['Description'],
                'date': row.get('Date', 'N/A')
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

    def export_bad_transactions_pdf(self) -> str:
        """
        Export bad transactions as a PDF.

        Returns:
            str: Base64 encoded PDF content.
        """
        buffer = io.BytesIO()
        doc = SimpleDocTemplate(buffer, pagesize=letter)
        elements = []

        styles = getSampleStyleSheet()
        styles.add(ParagraphStyle(name='Center', alignment=TA_CENTER))

        elements.append(Paragraph("Bad Transactions Report", styles['Center']))
        elements.append(Spacer(1, 12))

        data = [["Row", "Error"]]
        for t in self.bad_transactions:
            data.append([str(t['row']), t['error']])

        table = Table(data)
        table.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.blue),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
            ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
            ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
            ('FONTSIZE', (0, 0), (-1, 0), 12),
            ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
            ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
            ('TEXTCOLOR', (0, 1), (-1, -1), colors.black),
            ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
            ('FONTNAME', (0, 1), (-1, -1), 'Helvetica'),
            ('FONTSIZE', (0, 1), (-1, -1), 10),
            ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
            ('GRID', (0, 0), (-1, -1), 1, colors.black)
        ]))

        elements.append(table)
        doc.build(elements)

        pdf = buffer.getvalue()
        buffer.close()
        return base64.b64encode(pdf).decode('utf-8')

    def export_chart_of_accounts_pdf(self) -> str:
        """
        Export chart of accounts as a PDF.

        Returns:
            str: Base64 encoded PDF content.
        """
        buffer = io.BytesIO()
        doc = SimpleDocTemplate(buffer, pagesize=letter)
        elements = []

        styles = getSampleStyleSheet()
        styles.add(ParagraphStyle(name='Center', alignment=TA_CENTER))

        elements.append(Paragraph("Chart of Accounts", styles['Center']))
        elements.append(Spacer(1, 12))

        data = [["Account", "Card", "Balance"]]
        for account, cards in self.processed_transactions.items():
            for card, transactions in cards.items():
                balance = sum(t['amount'] for t in transactions)
                data.append([account, card, f"${balance:.2f}"])

        table = Table(data)
        table.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.blue),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
            ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
            ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
            ('FONTSIZE', (0, 0), (-1, 0), 12),
            ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
            ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
            ('TEXTCOLOR', (0, 1), (-1, -1), colors.black),
            ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
            ('FONTNAME', (0, 1), (-1, -1), 'Helvetica'),
            ('FONTSIZE', (0, 1), (-1, -1), 10),
            ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
            ('GRID', (0, 0), (-1, -1), 1, colors.black)
        ]))

        elements.append(table)
        doc.build(elements)

        pdf = buffer.getvalue()
        buffer.close()
        return base64.b64encode(pdf).decode('utf-8')

    def export_collections_pdf(self) -> str:
        """
        Export accounts for collections as a PDF.

        Returns:
            str: Base64 encoded PDF content.
        """
        buffer = io.BytesIO()
        doc = SimpleDocTemplate(buffer, pagesize=letter)
        elements = []

        styles = getSampleStyleSheet()
        styles.add(ParagraphStyle(name='Center', alignment=TA_CENTER))

        elements.append(Paragraph("Accounts for Collections", styles['Center']))
        elements.append(Spacer(1, 12))

        collections = self.get_accounts_for_collections()
        data = [["Account", "Card", "Balance"]]
        for item in collections:
            parts = item.split(', ')
            account = parts[0].split(': ')[1]
            card = parts[1].split(': ')[1] if len(parts) > 2 else 'Total'
            balance = parts[-1].split(': ')[1]
            data.append([account, card, balance])

        table = Table(data)
        table.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.blue),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
            ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
            ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
            ('FONTSIZE', (0, 0), (-1, 0), 12),
            ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
            ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
            ('TEXTCOLOR', (0, 1), (-1, -1), colors.black),
            ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
            ('FONTNAME', (0, 1), (-1, -1), 'Helvetica'),
            ('FONTSIZE', (0, 1), (-1, -1), 10),
            ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
            ('GRID', (0, 0), (-1, -1), 1, colors.black)
        ]))

        elements.append(table)
        doc.build(elements)

        pdf = buffer.getvalue()
        buffer.close()
        return base64.b64encode(pdf).decode('utf-8')

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
            names=['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number', 'Date'],
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

# Define custom colors
ORANGE = "#FFA500"
BLUE = "#0000FF"
WHITE = "#FFFFFF"

# Define the layout with updated colors
app.layout = dbc.Container([
    html.H1("Transaction Processor", className="my-4 text-center", style={'color': BLUE}),

    dbc.Row([
        dbc.Col([
            dbc.Card([
                dbc.CardBody([
                    html.H5("Upload Transactions", className="card-title", style={'color': BLUE}),
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
                            'backgroundColor': WHITE,
                            'color': BLUE
                        },
                        multiple=False
                    ),
                    dbc.Button('Reset', id='reset-button', color="warning", className="mt-3", style={'backgroundColor': ORANGE, 'color': WHITE}),
                ])
            ], className="mb-4", style={'backgroundColor': WHITE, 'borderColor': BLUE}),

            dbc.Card([
                dbc.CardBody([
                    html.H5("Export Transactions", className="card-title", style={'color': BLUE}),
                    dcc.Dropdown(id='person-dropdown', placeholder="Select a person", className="mb-3"),
                    dbc.Button('Export All Transactions for Person', id='export-person-pdf-button', color="primary", className="mb-2 w-100", style={'backgroundColor': BLUE, 'color': WHITE}),
                    dbc.Button("Export Bad Transactions", id="export-bad-transactions-button", color="primary", className="mb-2 w-100", style={'backgroundColor': BLUE, 'color': WHITE}),
                    dbc.Button("Export Chart of Accounts", id="export-chart-of-accounts-button", color="primary", className="mb-2 w-100", style={'backgroundColor': BLUE, 'color': WHITE}),
                    dbc.Button("Export Accounts for Collections", id="export-collections-button", color="primary", className="w-100", style={'backgroundColor': BLUE, 'color': WHITE}),
                ])
            ], style={'backgroundColor': WHITE, 'borderColor': BLUE}),
        ], md=4),

        dbc.Col([
            dbc.Card([
                dbc.CardBody([
                    html.H5("Processing Results", className="card-title", style={'color': BLUE}),
                    html.Div(id='output-data-upload'),
                ])
            ], className="mb-4", style={'backgroundColor': WHITE, 'borderColor': BLUE}),

            dbc.Card([
                dbc.CardBody([
                    html.H5("Account Balance Chart", className="card-title", style={'color': BLUE}),
                    dcc.Graph(id='balance-chart'),
                ])
            ], style={'backgroundColor': WHITE, 'borderColor': BLUE}),
        ], md=8),
    ]),

    # Download components
    dcc.Download(id="download-person-pdf"),
    dcc.Download(id="download-bad-transactions"),
    dcc.Download(id="download-chart-of-accounts"),
    dcc.Download(id="download-collections"),
], fluid=True, style={'backgroundColor': WHITE})

@app.callback(
    [Output('output-data-upload', 'children'),
     Output('balance-chart', 'figure'),
     Output('upload-data', 'contents'),
     Output('person-dropdown', 'options'),
     Output('export-person-pdf-button', 'disabled'),
     Output('export-bad-transactions-button', 'disabled'),
     Output('export-chart-of-accounts-button', 'disabled'),
     Output('export-collections-button', 'disabled')],
    [Input('upload-data', 'contents'),
     Input('reset-button', 'n_clicks')],
    [State('upload-data', 'filename'),
     State('upload-data', 'last_modified')]
)
def update_output(content, reset_clicks, name, date):
    ctx = dash.callback_context
    if not ctx.triggered:
        return dash.no_update, dash.no_update, dash.no_update, dash.no_update, True, True, True, True

    trigger_id = ctx.triggered[0]['prop_id'].split('.')[0]

    if trigger_id == 'reset-button':
        processor.reset()
        return html.Div("Data has been reset.", style={'color': BLUE}), go.Figure(), None, [], True, True, True, True

    elif trigger_id == 'upload-data' and content is not None:
        df, warnings = parse_csv(content, name)
        if df.empty:
            return html.Div([html.P(warning, style={'color': 'red'}) for warning in warnings]), go.Figure(), dash.no_update, [], True, True, True, True

        processor.process_transactions(df)

        person_options = [{'label': person, 'value': person} for person in processor.processed_transactions.keys()]

        chart_of_accounts = processor.generate_chart_of_accounts()
        
        return html.Div([
            html.H6(f'File "{name}" has been processed.', style={'color': 'green'}),
            html.H6('Warnings:', className="mt-3", style={'color': BLUE}),
            html.Ul([html.Li(warning, style={'color': ORANGE}) for warning in warnings]) if warnings else html.P("No warnings.", style={'color': 'gray'}),
        ]), processor.generate_balance_chart(), dash.no_update, person_options, False, False, False, False

    return dash.no_update, dash.no_update, dash.no_update, dash.no_update, True, True, True, True

@app.callback(
    Output("download-person-pdf", "data"),
    [Input("export-person-pdf-button", "n_clicks")],
    [State('person-dropdown', 'value')]
)
def export_person_pdf(n_clicks, person):
    if n_clicks is None or not person:
        return dash.no_update
    content = processor.export_person_transactions(person)
    return dcc.send_bytes(base64.b64decode(content), filename=f"{person}_all_transactions.pdf")

@app.callback(
    Output("download-bad-transactions", "data"),
    [Input("export-bad-transactions-button", "n_clicks")]
)
def export_bad_transactions(n_clicks):
    if n_clicks is None:
        return dash.no_update
    content = processor.export_bad_transactions_pdf()
    return dcc.send_bytes(base64.b64decode(content), filename="bad_transactions.pdf")

@app.callback(
    Output("download-chart-of-accounts", "data"),
    [Input("export-chart-of-accounts-button", "n_clicks")]
)
def export_chart_of_accounts(n_clicks):
    if n_clicks is None:
        return dash.no_update
    content = processor.export_chart_of_accounts_pdf()
    return dcc.send_bytes(base64.b64decode(content), filename="chart_of_accounts.pdf")

@app.callback(
    Output("download-collections", "data"),
    [Input("export-collections-button", "n_clicks")]
)
def export_collections(n_clicks):
    if n_clicks is None:
        return dash.no_update
    content = processor.export_collections_pdf()
    return dcc.send_bytes(base64.b64decode(content), filename="accounts_for_collections.pdf")

if __name__ == '__main__':
    app.run_server(debug=True)