from flask import jsonify
from flask import current_app as app

def generate_report(redis_client):
    """
    Generates a report of all account balances and collections.
    """
    report = {}

    try:
        collections = []
        keys = redis_client.keys()  # Get all keys (account names)
        for account in keys:
            # Check if the key is a hash type before calling hgetall
            if redis_client.type(account) == 'hash':
                cards = redis_client.hgetall(account)  # Get all cards and balances for the account
                for card, balance in cards.items():
                    if float(balance) >= 0:
                        if account not in report:
                            report[account] = {}
                        balance = float(balance)

                        # Ensure card number is a string to prevent unintended float conversions
                        card = str(card)
                        report[account][card] = balance
                    # if float(balance) < 0:  # Check if balance is negative
                        # collections.append({
                        #     'Account Name': account,
                        #     'Card Number': card,
                        #     'Balance': float(balance)
                        # })

        # return jsonify(collections), 200
        # app.logger.info(report)
        return report
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    

    # # Fetch all transactions stored in Redis
    # transactions = redis_client.lrange('transactions', 0, -1)

    # for transaction in transactions:
    #     # Convert transaction back to dictionary from string
    #     transaction = eval(transaction)

    #     account_name = transaction['Account Name']
    #     card_number = transaction['Card Number']
    #     amount = float(transaction['Transaction Amount'])

    #     if transaction['Transaction Type'] == 'Transfer':
    #         target_card_number = str(transaction['Target Card Number'])
    #         if target_card_number == 'nan' or target_card_number == '':
    #             # Skip or handle NaN target card number as needed
    #             continue
    #             # return jsonify({'err': 'encountered nan'})

    #     # Initialize account in the report if not already present
    #     if account_name not in report:
    #         report[account_name] = {}

    #     # Initialize card balance if not already present
    #     if card_number not in report[account_name]:
    #         report[account_name][card_number] = 0.0

    #     # Update card balance with transaction amount
    #     report[account_name][card_number] += amount

    # return report
