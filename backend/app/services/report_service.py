import logging

class ReportService:
    def __init__(self, transaction_service):
        self.transaction_service = transaction_service
        self.logger = logging.getLogger(__name__)

    def get_accounts(self):
        self.logger.info("Retrieving chart of accounts")
        return self.transaction_service.accounts

    def get_collections_accounts(self):
        self.logger.info("Retrieving collections accounts")
        collections = {}
        for account, cards in self.transaction_service.accounts.items():
            self.logger.info(f"Checking account: {account}")
            self.logger.info(f"Cards: {cards}")
            
            negative_cards = {card: balance for card,
                              balance in cards.items() if balance < 0}
            if negative_cards:
                collections[account] = negative_cards
        self.logger.info(f"Found {len(collections)} accounts with negative balances")
        return collections

    def get_bad_transactions(self):
        self.logger.info("Retrieving bad transactions")
        return self.transaction_service.bad_transactions
