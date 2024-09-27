
import streamlit as st
import pandas as pd
from sqlalchemy import create_engine, Column, Integer, String, Float, Boolean, DateTime, ForeignKey, func, inspect
from sqlalchemy.orm import sessionmaker, relationship, declarative_base, Session

# SQLAlchemy Base
Base = declarative_base()

# Define Account, Card, and Transaction models
class Account(Base):
    __tablename__ = 'accounts'

    id = Column(Integer, primary_key=True, index=True)
    account_name = Column(String, index=True, nullable=False)
    cards = relationship("Card", back_populates="account")


class Card(Base):
    __tablename__ = 'cards'

    id = Column(Integer, primary_key=True, index=True)
    card_number = Column(String, index=True)
    account_id = Column(Integer, ForeignKey('accounts.id'))
    balance = Column(Float, default=0)
    account = relationship("Account", back_populates="cards")
    transactions = relationship("Transaction", back_populates="card", foreign_keys='Transaction.card_id')
    target_transactions = relationship("Transaction", foreign_keys='Transaction.target_card_id')


class Transaction(Base):
    __tablename__ = 'transactions'
    
    id = Column(Integer, primary_key=True, index=True)
    card_id = Column(Integer, ForeignKey('cards.id'), nullable=True)
    target_card_id = Column(Integer, ForeignKey('cards.id'), nullable=True)
    transaction_amount = Column(Float, nullable=False)
    transaction_type = Column(String(20), nullable=False)
    description = Column(String, nullable=True)
    bad_data = Column(Boolean, default=False)
    transaction_date = Column(DateTime, default=func.now())

    card = relationship("Card", back_populates="transactions", foreign_keys=[card_id])
    target_card = relationship("Card", back_populates="target_transactions", foreign_keys=[target_card_id])

# SQLAlchemy setup
DATABASE_URL = "sqlite:///./transactions.db"
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Function to check if tables exist and create them if they don't
def ensure_tables_exist(engine):
    inspector = inspect(engine)
    tables = inspector.get_table_names()
    if not tables:
        Base.metadata.create_all(bind=engine)

ensure_tables_exist(engine)

# Function to handle database session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# CRUD Operations
def get_or_create_account(db: Session, account_name: str):
    account = db.query(Account).filter(Account.account_name == account_name).first()
    if not account:
        account = Account(account_name=account_name)
        db.add(account)
        db.commit()
        db.refresh(account)
    return account

def get_or_create_card(db: Session, card_number: str, account_id: int):
    card = db.query(Card).filter(Card.card_number == card_number, Card.account_id == account_id).first()
    if not card:
        card = Card(card_number=card_number, account_id=account_id)
        db.add(card)
        db.commit()
        db.refresh(card)
    return card

def create_transaction(db: Session, card_id: int, target_card_id: int, amount: float, transaction_type: str, description: str, bad_data: bool = False):
    transaction = Transaction(
        card_id=card_id,
        target_card_id=target_card_id if target_card_id else None,
        transaction_amount=amount,
        transaction_type=transaction_type,
        description=description,
        bad_data=bad_data
    )
    db.add(transaction)
    db.commit()
    db.refresh(transaction)
    return transaction

def get_all_accounts(db: Session):
    return db.query(Account).all()

def get_collections(db: Session):
    return db.query(Card).filter(Card.balance < 0).all()

def get_bad_transactions(db: Session):
    return db.query(Transaction).filter(Transaction.bad_data == True).all()

def reset_system(db: Session):
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    db.commit()

# Function to process transactions from a CSV file
def process_transactions(file):
    try:
        column_names = ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number']
        df = pd.read_csv(file, header=None, names=column_names, dtype={'Card Number': str, 'Target Card Number': str})
        bad_transactions = []

        db = next(get_db())
        
        for _, row in df.iterrows():
            try:
                if len(row) != len(column_names):
                    raise ValueError("Invalid number of columns")

                account_name = row['Account Name']
                card_number = row['Card Number']
                transaction_amount = row['Transaction Amount']
                transaction_type = row['Transaction Type']
                description = row.get('Description', '')
                target_card_number = row.get('Target Card Number', None)

                # Basic validation
                if not card_number.isdigit():
                    raise ValueError("Card number must be numeric")

                if pd.isna(account_name) or pd.isna(card_number) or pd.isna(transaction_amount) or pd.isna(transaction_type):
                    raise ValueError("Missing required fields")

                transaction_amount = float(transaction_amount)
                if transaction_type not in ['Credit', 'Debit', 'Transfer']:
                    raise ValueError("Invalid transaction type")

                account = get_or_create_account(db, account_name)
                target_card_id = None

                if transaction_type == 'Transfer':
                    if pd.isna(target_card_number) or not str(target_card_number).isdigit():
                        raise ValueError("Invalid target card number")

                    target_card = get_or_create_card(db, str(target_card_number), account.id)
                    target_card_id = target_card.id
                    card = get_or_create_card(db, card_number, account.id)

                    card.balance -= transaction_amount
                    target_card.balance += transaction_amount
                    db.commit()

                elif transaction_type == 'Credit':
                    card = get_or_create_card(db, card_number, account.id)
                    card.balance += transaction_amount
                    db.commit()

                elif transaction_type == 'Debit':
                    card = get_or_create_card(db, card_number, account.id)
                    card.balance -= transaction_amount
                    db.commit()

                create_transaction(db, card_id=card.id, target_card_id=target_card_id, amount=transaction_amount, transaction_type=transaction_type, description=description)

            except Exception as e:
                bad_transaction = row.to_dict()
                bad_transaction["Error"] = str(e)
                bad_transactions.append(bad_transaction)

                create_transaction(db, card_id=None, target_card_id=None, amount=0, transaction_type='Invalid', description="Bad data", bad_data=True)

        st.session_state.bad_transactions = bad_transactions

        success_message = "File uploaded successfully!" if not bad_transactions else "File uploaded with errors."
        st.success(success_message)

    except Exception as e:
        st.error(f"An error occurred: {str(e)}")


def reset_database():
    db = next(get_db())
    try:
        reset_system(db)
        st.session_state.clear()
        st.write('<meta http-equiv="refresh" content="0">', unsafe_allow_html=True)
    except Exception as e:
        st.error(f"An error occurred while resetting the system: {str(e)}")


# UI enhancements
st.sidebar.title("Navigation")
st.sidebar.markdown("Navigate through the different sections of the app.")
page = st.sidebar.radio("Go to", ["📂 Upload Transactions", "📊 Chart of Accounts", "⚠️ Accounts for Collections", "❌ Bad Transactions"])

# Page 1: Upload Transactions
if page == "📂 Upload Transactions":
    st.title("Upload Transactions")
    st.markdown("### Please upload your CSV transaction files here.")

    uploaded_file = st.file_uploader("Upload a CSV transaction file", type=["csv"])

    if uploaded_file:
        process_transactions(uploaded_file)

    st.markdown("---")

    reset_col1, reset_col2 = st.columns([0.7, 0.3])
    with reset_col2:
        if st.button("🔄 Reset System"):
            reset_database()

# Page 2: Chart of Accounts
elif page == "📊 Chart of Accounts":
    st.title("Chart of Accounts")
    st.markdown("### Here are all the accounts along with their card balances.")
    db = next(get_db())
    accounts = get_all_accounts(db)

    if accounts:
        with st.expander("View Account Balances", expanded=True):
            for account in accounts:
                st.markdown(f"**Account**: {account.account_name}")
                for card in account.cards:
                    st.markdown(f"- Card: `{card.card_number}`, Balance: **{card.balance:.2f}**")
    else:
        st.info("No accounts processed yet.")

# Page 3: Accounts for Collections
elif page == "⚠️ Accounts for Collections":
    st.title("Accounts for Collections")
    st.markdown("### These accounts have negative balances and need to be sent to collections.")
    db = next(get_db())
    collections = get_collections(db)
    if collections:
        collections_data = [{"Account": c.account.account_name, "Card": c.card_number, "Balance": c.balance} for c in collections]
        st.table(pd.DataFrame(collections_data))
    else:
        st.success("No accounts need to be sent to collections!")

# Page 4: Bad Transactions
elif page == "❌ Bad Transactions":
    st.title("Bad Transactions")
    st.markdown("### Here are the transactions that were marked as bad data during processing.")
    db = next(get_db())
    bad_transactions = get_bad_transactions(db)
    if bad_transactions:
        bad_data_list = [{"ID": t.id, "Transaction Type": t.transaction_type, "Description": t.description} for t in bad_transactions]
        st.table(pd.DataFrame(bad_data_list))
    else:
        st.success("No bad transactions found!")



