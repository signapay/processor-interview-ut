from fastapi import APIRouter, UploadFile, File
from app.services.transaction_service import TransactionService
from app.services.report_service import ReportService

router = APIRouter()
transaction_service = TransactionService()

@router.post("/upload")
async def upload_file(file: UploadFile = File(...)):
    contents = await file.read()
    transactions = transaction_service.parse_csv(contents.decode())
    for transaction in transactions:
        transaction_service.process_transaction(transaction)
    return {"message": "File processed successfully"}


@router.get("/accounts")
async def get_accounts():
    report_service = ReportService(transaction_service)
    return report_service.get_accounts()


@router.get("/collections")
async def get_collections():
    report_service = ReportService(transaction_service)
    return report_service.get_collections_accounts()


@router.get("/bad-transactions")
async def get_bad_transactions():
    report_service = ReportService(transaction_service)
    return report_service.get_bad_transactions()


@router.post("/reset")
async def reset_system():
    transaction_service.reset()
    return {"message": "System reset successfully"}
