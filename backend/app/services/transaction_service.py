from app.models.transaction import Transaction
import logging
import csv
from typing import Union

class TransactionService:
    TRANSFER_TYPE = 'Transfer'
    VALID_TRANSACTION_TYPES = ['Credit', 'Debit', 'Transfer']
    
    def __init__(self):
        self.accounts = {}
        self.bad_transactions = []
        self.logger = logging.getLogger(__name__)

    def process_transaction(self, transaction: Transaction):
        self.logger.info(f"Processing transaction: {transaction}")
        if transaction.transaction_type not in ['Credit', 'Debit', 'Transfer']:
            self.bad_transactions.append(transaction)
            return
        
        if len(str(transaction.card_number)) != 16:
            self.bad_transactions.append(transaction)
            return

        if transaction.account_name not in self.accounts:
            self.accounts[transaction.account_name] = {}

        if transaction.card_number not in self.accounts[transaction.account_name]:
            self.accounts[transaction.account_name][transaction.card_number] = 0

        if transaction.transaction_type == 'Credit':
            self.accounts[transaction.account_name][transaction.card_number] += transaction.transaction_amount
            self.logger.info(f"Added credit to {transaction.account_name}: {transaction.transaction_amount}")
        elif transaction.transaction_type == 'Debit':
            self.accounts[transaction.account_name][transaction.card_number] += transaction.transaction_amount
            self.logger.info(f"Deducted debit from {transaction.account_name}: {transaction.transaction_amount}")
        elif transaction.transaction_type == 'Transfer':        
            if transaction.target_card_number is None:
                self.bad_transactions.append(transaction)
                return
            # Deduct from the source account
            self.accounts[transaction.account_name][transaction.card_number] += transaction.transaction_amount
            self.logger.info(f"Deducted transfer from {transaction.account_name}: {transaction.transaction_amount}")
        self.logger.info(f"Current balance for {transaction.account_name}: {self.accounts[transaction.account_name][transaction.card_number]}")
        
    def add_bad_transaction(self, line: int, raw_data: str):
        self.bad_transactions.append({"line": line, "content": raw_data})

    def reset(self):
        self.accounts = {}
        self.bad_transactions = []

    def validate_row(self, row: list[str]) -> Union[Transaction, None]:
        if len(row) != 6:
            self.logger.error(f"Invalid row length: {len(row)}")
            return None

        try:
            account_name = row[0].strip()
            card_number = int(row[1])
            transaction_amount = float(row[2])
            transaction_type = row[3].strip()
            description = row[4].strip()
            target_card_number = int(row[5]) if row[5] and transaction_type == self.TRANSFER_TYPE else None

            if not account_name:
                raise ValueError("Account name is empty")
            if transaction_type not in self.VALID_TRANSACTION_TYPES:
                raise ValueError(f"Invalid transaction type: {transaction_type}")
            if transaction_type == self.TRANSFER_TYPE and target_card_number is None:
                raise ValueError("Transfer transaction missing target card number")

            return Transaction(
                account_name=account_name,
                card_number=card_number,
                transaction_amount=transaction_amount,
                transaction_type=transaction_type,
                description=description,
                target_card_number=target_card_number
            )
        except (ValueError, IndexError) as e:
            self.logger.error(f"Error validating row: {e}")
            return None

    def parse_csv(self, file_content: str) -> list[Transaction]:
        transactions = []
        reader = csv.reader(file_content.splitlines())
        self.logger.info("Parsing CSV file.")
        for line_number, row in enumerate(reader, start=1):
            transaction = self.validate_row(row)
            if transaction:
                transactions.append(transaction)
                self.logger.info(f"Parsed transaction: {transaction}")
            else:
                self.logger.error(f"Error validating row: {row}")   
                self.add_bad_transaction(line_number, ','.join(row))

        self.logger.info(f"Parsed {len(transactions)} transactions and {len(self.bad_transactions)} bad transactions")
        return transactions
