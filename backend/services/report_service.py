from flask import jsonify
from flask import current_app as app


# this function generates a report of all account balances and collections
def generate_report(redis_client):
    # dict to store the report
    report = {}
    try:
        # Get all keys (account names)
        keys = redis_client.keys()  
        for account in keys:
            # Check if the key is a hash type before calling hgetall
            if redis_client.type(account) == 'hash':
                # Get all cards and balances for the account
                cards = redis_client.hgetall(account)  
                for card, balance in cards.items():
                    # process only if balance is greater than 0 otherwise it is a bad transaction
                    if float(balance) >= 0:
                        if account not in report:
                            report[account] = {}
                        balance = float(balance)
                        # Ensure card number is a string to prevent unintended float conversions
                        card = str(card)
                        # store in the report dict
                        report[account][card] = balance
        return report
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    