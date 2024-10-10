import pandas as pd

# this function parses the given CSV file and returns a DataFrame of transactions
def parse_csv(file_path):
    try:
        df = pd.read_csv(file_path)
        # Adding columns headers to the data
        df.columns = ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number']
        
        return df, f"File successful"
    except Exception as e:
        print(f"Error parsing CSV file: {e}")
        return None, f"File unsuccessful"
