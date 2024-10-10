from flask import current_app as app
import re

def process_transaction(transaction, redis_client):
    """
    Processes a single transaction and updates Redis cache.
    """
    account_name = transaction['Account Name']
    card_number = transaction['Card Number']
    transaction_type = transaction['Transaction Type']
    amount = transaction['Transaction Amount']
    # app.logger.info(type(card_number))
    #checking for any missing value
    if not account_name or not card_number or not transaction_type or not amount:
        return False, "Bad Transaction: Missing Values"
    # checking if card number is valid
    if not bool(re.match(r'^\d+$', str(card_number))):
        return False, "Bad Transaction: invalid card number"
    
    accepted_types = ['debit', 'credit', 'transfer']
    # To check if transaction type is valid
    if transaction_type.lower().strip() not in accepted_types:
        return False, "Bad Transaction: Invalid Transaction Type"
    # checking if amount is valid
    if not re.match(r'^-?\d+(\.\d+)?$', str(amount)):
        return False, "Bad Transaction: Invalid Amount Type"
    amount = float(transaction['Transaction Amount'])

    if amount < 0:
        return False, "Bad Transaction: Negative amount parsed for type credit."
    # fetch the current card balance
    current_balance = float(redis_client.hget(account_name, card_number) or 0.0)
    # Update balance based on transaction type
    if transaction_type == 'Credit':
        current_balance += amount
    elif transaction_type == 'Debit':
        current_balance -= amount
    elif transaction_type == 'Transfer':            
        target_card = transaction.get('Target Card Number')
        # check for missing target card
        if not target_card:
            return False, "Missing target card number for transfer."
        target_card = int(transaction.get('Target Card Number'))
        
        current_balance -= amount
        # fetch the target card balance
        target_balance = float(redis_client.hget(account_name, target_card) or 0.0)
        target_balance += amount
        # Updating the dataset with the new balance for target card 
        redis_client.hset(account_name, target_card, str(target_balance))


    # Update the Redis cache with the new balance for current card
    redis_client.hset(account_name, card_number, str(current_balance))

    return True, f"Transaction processed successfully for {account_name}."
