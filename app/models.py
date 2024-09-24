from sqlalchemy import Column, Integer, String, ForeignKey, Float, Boolean, DateTime, func
from sqlalchemy.orm import relationship
from database import Base

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
    card_id = Column(Integer, ForeignKey('cards.id'))
    target_card_id = Column(Integer, ForeignKey('cards.id'), nullable=True)
    transaction_amount = Column(Float, nullable=False)
    transaction_type = Column(String(20), nullable=False)
    description = Column(String, nullable=True)
    bad_data = Column(Boolean, default=False)
    transaction_date = Column(DateTime, default=func.now())
    card = relationship("Card", back_populates="transactions", foreign_keys=[card_id])
    target_card = relationship("Card", back_populates="target_transactions", foreign_keys=[target_card_id])
