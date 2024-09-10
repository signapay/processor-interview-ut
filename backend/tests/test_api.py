from app.main import app, get_api_key
from app.core.config import settings
from fastapi.testclient import TestClient
import pytest

client = TestClient(app)

async def override_get_api_key():
    return "mocked_api_key"

app.dependency_overrides[get_api_key] = override_get_api_key

@pytest.fixture(autouse=True)
def reset_before_test():
    """Reset the system before each test"""
    response = client.post(f"{settings.API_PREFIX}/reset")
    assert response.status_code == 200
    assert response.json() == {"message": "System reset successfully"}

def test_root():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Transaction Processor API"}

def setup_upload_file(csv_content):
    """Helper function to upload a file"""
    response = client.post(f"{settings.API_PREFIX}/upload", files={"file": ("test.csv", csv_content)})
    assert response.status_code == 200
    assert response.json() == {"message": "File processed successfully"}

def setup():
    """Global setup function to prepare test data"""
    csv_content = (
        "Grace Pink,6271836696117800,400.04,Credit,Monthly Subscription Fee,\n"
        "Eva Green,9673863606462894,670.42,Credit,Salary Deposit,\n"
        "Grace Pink,4982396442950129,79.24,Debit,Grocery Shopping,\n"
    )
    setup_upload_file(csv_content)

def test_get_accounts():
    setup()
    
    response = client.get(f"{settings.API_PREFIX}/accounts")
    assert response.status_code == 200
    accounts = response.json()
    print(f"Received accounts: {accounts}")  # Add this line
    assert "Grace Pink" in accounts
    assert "Eva Green" in accounts
    assert len(accounts["Grace Pink"]) == 2
    assert accounts["Grace Pink"]["6271836696117800"] == 400.04
    assert accounts["Grace Pink"]["4982396442950129"] == -79.24

def test_get_collections(caplog):
    setup()
    
    response = client.get(f"{settings.API_PREFIX}/collections")
    assert response.status_code == 200
    collections = response.json()
    print(f"Received collections: {collections}")  # Add this line
    assert "Grace Pink" in collections
    assert collections["Grace Pink"]["4982396442950129"] == -79.24
    print(caplog.text)  # Add this line to print captured logs

def test_get_bad_transactions(caplog):
    # Upload a file with a bad transaction
    csv_content = (
        "Grace Pink,6271836696117800,400.04,Credit,Monthly Subscription Fee,\n"
        "Bad Transaction,NotANumber,NotANumber,InvalidType,Bad Description,\n"
    )
    client.post(f"{settings.API_PREFIX}/upload", files={"file": ("test.csv", csv_content)})
    
    response = client.get(f"{settings.API_PREFIX}/bad-transactions")
    assert response.status_code == 200
    bad_transactions = response.json()
    print(f"Received bad transactions: {bad_transactions}")  # Add this line
    assert len(bad_transactions) == 1
    print(caplog.text)  # Add this line to print captured logs

def test_reset_system():
    setup()
    
    # Then reset
    response = client.post(f"{settings.API_PREFIX}/reset")
    assert response.status_code == 200
    assert response.json() == {"message": "System reset successfully"}
    
    # Verify that the chart of accounts is empty after reset
    response = client.get(f"{settings.API_PREFIX}/accounts")
    assert response.status_code == 200
    assert response.json() == {}

def test_upload_invalid_file(caplog):
    csv_content = "Invalid,Data,Format\n"
    response = client.post(f"{settings.API_PREFIX}/upload", files={"file": ("invalid.csv", csv_content)})
    assert response.status_code == 200  # The API still processes the file, but no valid transactions are added
    assert response.json() == {"message": "File processed successfully"}
    
    response = client.get(f"{settings.API_PREFIX}/bad-transactions")
    assert response.status_code == 200
    bad_transactions = response.json()
    print(f"Received bad transactions: {bad_transactions}")  # Add this line
    assert "Invalid,Data,Format" in bad_transactions
    print(caplog.text)  # Add this line to print captured logs