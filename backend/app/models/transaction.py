from pydantic import BaseModel
from typing import Optional


class Transaction(BaseModel):
    account_name: str
    card_number: int
    transaction_amount: float
    transaction_type: str
    description: str
    target_card_number: Optional[int] = None
