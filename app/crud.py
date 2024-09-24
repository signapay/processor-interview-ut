from sqlalchemy.orm import Session
from models import Account, Card, Transaction

# Create or get account
def get_or_create_account(db: Session, account_name: str):
    account = db.query(Account).filter(Account.account_name == account_name).first()
    if not account:
        account = Account(account_name=account_name)
        db.add(account)
        db.commit()
        db.refresh(account)
    return account

# Create or get card
def get_or_create_card(db: Session, card_number: str, account_id: int):
    card = db.query(Card).filter(Card.card_number == card_number, Card.account_id == account_id).first()
    if not card:
        card = Card(card_number=card_number, account_id=account_id)
        db.add(card)
        db.commit()
        db.refresh(card)
    return card

# Create a transaction
def create_transaction(db: Session, card_id: int, target_card_id: int, amount: float, transaction_type: str, description: str, bad_data: bool = False):
    transaction = Transaction(
        card_id=card_id,
        target_card_id=target_card_id if target_card_id else None,  # Nullable for non-transfer transactions
        transaction_amount=amount,
        transaction_type=transaction_type,
        description=description,
        bad_data=bad_data
    )
    db.add(transaction)
    db.commit()
    db.refresh(transaction)
    return transaction

# Get all accounts with their cards and balances
def get_all_accounts(db: Session):
    return db.query(Account).all()

# Get accounts that need to be sent to collections (negative balances)
from sqlalchemy.orm import joinedload

def get_collections(db: Session):
    # Query the cards with negative balances and eagerly load the related accounts
    return db.query(Card).options(joinedload(Card.account)).filter(Card.balance < 0).all()


# Get all bad transactions
def get_bad_transactions(db: Session):
    return db.query(Transaction).filter(Transaction.bad_data == True).all()

# Reset the system
def reset_system(db: Session):
    db.query(Transaction).delete()
    db.query(Card).delete()
    db.query(Account).delete()
    db.commit()
