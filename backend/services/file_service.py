import pandas as pd

def parse_csv(file_path):
    """
    Parses the given CSV file and returns a DataFrame of transactions.
    """
    try:
        df = pd.read_csv(file_path)
        # Validate required columns
        df.columns = ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number']
        required_columns = ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number']
        if not all(column in df.columns for column in required_columns):
            raise ValueError("Invalid CSV format. Missing required columns.")

        return df, f"File successful"
    except Exception as e:
        print(f"Error parsing CSV file: {e}")
        return None, f"File unsuccessful"
