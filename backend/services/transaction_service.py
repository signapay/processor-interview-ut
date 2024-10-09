from decimal import Decimal
from flask import current_app as app

def process_transaction(transaction, redis_client):
    """
    Processes a single transaction and updates Redis cache.
    """
    account_name = transaction['Account Name']
    card_number = transaction['Card Number']
    transaction_type = transaction['Transaction Type']
    # amount = transaction['Transaction Amount']
    # amount = Decimal(str(transaction['Transaction Amount']))
    amount = float(transaction['Transaction Amount'])

    # app.logger.info(type(amount))
    # Get the current balance from Redis or default to 0.0

    # app.logger.info(f"Processing {transaction_type} transaction for {account_name} - {card_number}")
    # app.logger.info(f"Initial balance: {current_balance}, Amount: {amount}")

    if amount < 0:
        return False, "Bad Transaction: Negative amount parsed for type credit."
    # current_balance = Decimal(redis_client.hget(unique_key, 'balance') or 0.0)
    current_balance = float(redis_client.hget(account_name, card_number) or 0.0)
    # Update balance based on transaction type
    if transaction_type == 'Credit':
        current_balance += amount
    elif transaction_type == 'Debit':
        current_balance -= amount
    elif transaction_type == 'Transfer':
        # Transfer logic: Deduct from this card and add to the target card
        target_card = int(transaction.get('Target Card Number'))
        # app.logger.info(target_card)
        # app.logger.info(type(target_card))
        if not target_card:
            return False, "Missing target card number for transfer."
        current_balance -= amount
        # app.logger.info(f"Updated source balance for {card_number}: {current_balance}")
        # Use Account Name + Target Card Number as the key
        # target_key = f"{account_name}_{target_card}"
        # redis_client.hincrbyfloat(target_key, 'balance', float(amount))  # Increment target card balance
        # redis_client.hincrbyfloat(account_name, target_card, float(amount))  # Increment target card balance
        # Increment the target card balance
        # target_balance = Decimal(redis_client.hget(target_key, 'balance') or '0.0')
        # target_balance = Decimal(redis_client.hget(account_name, str(target_card)) or 0.0)
        target_balance = float(redis_client.hget(account_name, target_card) or 0.0)
        target_balance += amount
        # app.logger.info(f"Updated target balance for {target_card}: {target_balance}")
        # Update target card balance
        # redis_client.hset(target_key, 'balance', str(target_balance))
        redis_client.hset(account_name, target_card, str(target_balance))


    # Update the Redis cache with the new balance
    # redis_client.hset(unique_key, 'balance', str(current_balance))
    redis_client.hset(account_name, card_number, str(current_balance))

    # app.logger.info(f"Final balance for {card_number}: {current_balance}\n")
    # redis_client.hset(account_name, card_number, str(current_balance))
    return True, f"Transaction processed successfully for {account_name}."
