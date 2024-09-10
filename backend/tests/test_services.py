import pytest
from pathlib import Path
from app.models.transaction import Transaction
from app.services.report_service import ReportService
from app.services.transaction_service import TransactionService


@pytest.fixture
def transaction_service():
    return TransactionService()


@pytest.fixture
def report_service(transaction_service):
    return ReportService(transaction_service)


def test_parse_csv(transaction_service):
    csv_path = Path(__file__).parent / 'test.csv'
    with open(csv_path, 'r') as file:
        csv_content = file.read()

    transactions = transaction_service.parse_csv(csv_content)

    # Expected transactions based on the CSV file
    expected_transactions = [
        {"account_name": "Grace Pink", "card_number": 6271836696117800, "transaction_amount": 400.04,
            "transaction_type": "Credit", "description": "Monthly Subscription Fee", "target_card_number": None},
        {"account_name": "Eva Green", "card_number": 9673863606462894, "transaction_amount": 670.42, "transaction_type": "Transfer",
            "description": "Reimbursement for Travel Expenses", "target_card_number": 5843609540116587},
        {"account_name": "Grace Pink", "card_number": 4982396442950129, "transaction_amount": 79.24,
            "transaction_type": "Debit", "description": "Transfer to Savings Account", "target_card_number": None},
        {"account_name": "Carol White", "card_number": 2887262876020801, "transaction_amount": 115.24,
            "transaction_type": "Transfer", "description": "Client Payment Received", "target_card_number": 4982396442950129},
        {"account_name": "Carol White", "card_number": 9936868078262041, "transaction_amount": 921.69,
            "transaction_type": "Credit", "description": "Office Supplies Purchase", "target_card_number": None},
        {"account_name": "Bob Brown", "card_number": 9673863606462894, "transaction_amount": 771.54,
            "transaction_type": "Debit", "description": "Monthly Subscription Fee", "target_card_number": None},
        {"account_name": "Grace Pink", "card_number": 9936868078262041, "transaction_amount": 79.03,
            "transaction_type": "Credit", "description": "Office Supplies Purchase", "target_card_number": None},
        {"account_name": "Jane Smith", "card_number": 4982396442950129, "transaction_amount": 506.67,
            "transaction_type": "Debit", "description": "Bonus Payment", "target_card_number": None},
        {"account_name": "John Doe", "card_number": 2215428225650665, "transaction_amount": 332.78,
            "transaction_type": "Debit", "description": "Credit Card Payment", "target_card_number": None},
        {"account_name": "Jane Smith", "card_number": 5843609540116587, "transaction_amount": 845.65,
            "transaction_type": "Transfer", "description": "Refund for Returned Items", "target_card_number": 9673863606462894},
        {"account_name": "Bob Brown", "card_number": 6271836696117800, "transaction_amount": 809.07,
            "transaction_type": "Credit", "description": "Office Supplies Purchase", "target_card_number": None},
        {"account_name": "Alice Johnson", "card_number": 4982396442950129, "transaction_amount": 875.97,
            "transaction_type": "Credit", "description": "Payment for Services Rendered", "target_card_number": None},
        {"account_name": "Carol White", "card_number": 5199647623824526, "transaction_amount": 287.53,
            "transaction_type": "Transfer", "description": "Monthly Subscription Fee", "target_card_number": 9673863606462894},
        {"account_name": "Frank Blue", "card_number": 9940885544910543, "transaction_amount": -285.49,
            "transaction_type": "Transfer", "description": "Monthly Subscription Fee", "target_card_number": 6271836696117800},
        {"account_name": "Grace Pink", "card_number": 4982396442950129, "transaction_amount": -90.10,
            "transaction_type": "Transfer", "description": "Credit Card Payment", "target_card_number": 9673863606462894},
        {"account_name": "Alice Johnson", "card_number": 6271836696117800, "transaction_amount": 548.24,
            "transaction_type": "Debit", "description": "Reimbursement for Travel Expenses", "target_card_number": None},
        {"account_name": "Carol White", "card_number": 9940885544910543, "transaction_amount": -101.51,
            "transaction_type": "Transfer", "description": "Monthly Subscription Fee", "target_card_number": 9673863606462894},
        {"account_name": "Jane Smith", "card_number": 9673863606462894, "transaction_amount": -679.49,
            "transaction_type": "Debit", "description": "Credit Card Payment", "target_card_number": None},
        {"account_name": "Carol White", "card_number": 2215428225650665, "transaction_amount": -185.59,
            "transaction_type": "Credit", "description": "Reimbursement for Travel Expenses", "target_card_number": None},
        {"account_name": "Carol White", "card_number": 9936868078262041, "transaction_amount": 377.43,
            "transaction_type": "Debit", "description": "Reimbursement for Travel Expenses", "target_card_number": None},
    ]

    assert len(transactions) == len(expected_transactions)

    for parsed, expected in zip(transactions, expected_transactions):
        assert parsed.account_name == expected["account_name"]
        assert parsed.card_number == expected["card_number"]
        assert parsed.transaction_amount == pytest.approx(
            expected["transaction_amount"])
        assert parsed.transaction_type == expected["transaction_type"]
        assert parsed.description == expected["description"]
        assert parsed.target_card_number == expected["target_card_number"]

    # Additional checks
    assert all(t.transaction_type in ["Credit", "Debit", "Transfer"] for t in transactions)
    assert all(t.target_card_number is not None for t in transactions if t.transaction_type == "Transfer")
    assert any(t.transaction_amount < 0 for t in transactions)

    # Check specific transactions with negative amounts
    negative_transactions = [t for t in transactions if t.transaction_amount < 0]
    assert len(negative_transactions) == 5
    assert negative_transactions[0].account_name == "Frank Blue"
    assert negative_transactions[0].transaction_amount == pytest.approx(-285.49)
    assert negative_transactions[1].account_name == "Grace Pink"
    assert negative_transactions[1].transaction_amount == pytest.approx(-90.10)
    assert negative_transactions[2].account_name == "Carol White"
    assert negative_transactions[2].transaction_amount == pytest.approx(-101.51)
    assert negative_transactions[3].account_name == "Jane Smith"
    assert negative_transactions[3].transaction_amount == pytest.approx(-679.49)
    assert negative_transactions[4].account_name == "Carol White"
    assert negative_transactions[4].transaction_amount == pytest.approx(-185.59)


def test_process_transaction(transaction_service):
    transaction = Transaction(
        account_name="John Doe",
        card_number=1234,
        transaction_amount=100.00,
        transaction_type="Credit",
        description="Deposit"
    )
    transaction_service.process_transaction(transaction)
    assert transaction_service.accounts["John Doe"][1234] == 100.00

    debit_transaction = Transaction(
        account_name="John Doe",
        card_number=1234,
        transaction_amount=50.00,
        transaction_type="Debit",
        description="Withdrawal"
    )
    transaction_service.process_transaction(debit_transaction)
    assert transaction_service.accounts["John Doe"][1234] == 50.00


def test_bad_transaction(transaction_service):
    bad_transaction = Transaction(
        account_name="Jane Smith",
        card_number=5678,
        transaction_amount=25.00,
        transaction_type="Invalid",
        description="Bad transaction"
    )
    transaction_service.process_transaction(bad_transaction)
    assert len(transaction_service.bad_transactions) == 1
    assert transaction_service.bad_transactions[0] == bad_transaction


def test_chart_of_accounts(transaction_service, report_service):
    transaction_service.process_transaction(Transaction(
        account_name="Alice",
        card_number=1111,
        transaction_amount=200.00,
        transaction_type="Credit",
        description="Deposit"
    ))
    transaction_service.process_transaction(Transaction(
        account_name="Alice",
        card_number=2222,
        transaction_amount=100.00,
        transaction_type="Credit",
        description="Deposit"
    ))
    chart = report_service.get_accounts()
    assert "Alice" in chart
    assert chart["Alice"][1111] == 200.00
    assert chart["Alice"][2222] == 100.00


def test_collections_accounts(transaction_service, report_service):
    transaction_service.process_transaction(Transaction(
        account_name="Bob",
        card_number=3333,
        transaction_amount=100.00,
        transaction_type="Debit",
        description="Withdrawal"
    ))
    collections = report_service.get_collections_accounts()
    assert "Bob" in collections
    assert collections["Bob"][3333] == -100.00


def test_reset(transaction_service):
    transaction_service.process_transaction(Transaction(
        account_name="Charlie",
        card_number=4444,
        transaction_amount=50.00,
        transaction_type="Credit",
        description="Deposit"
    ))
    assert len(transaction_service.accounts) == 1
    transaction_service.reset()
    assert len(transaction_service.accounts) == 0
    assert len(transaction_service.bad_transactions) == 0
