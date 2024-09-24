from fastapi import FastAPI, Depends, UploadFile, File, Request
from fastapi.responses import HTMLResponse, JSONResponse
from fastapi.templating import Jinja2Templates
from sqlalchemy.orm import Session
import crud, models
from database import SessionLocal, engine
import pandas as pd
import uvicorn

models.Base.metadata.create_all(bind=engine)

app = FastAPI()
templates = Jinja2Templates(directory="../app/templates")

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.get("/", response_class=HTMLResponse)
async def upload_form(request: Request):
    return templates.TemplateResponse("upload.html", {"request": request, "bad_transactions": []})

@app.post("/upload", response_class=HTMLResponse)
async def upload_transactions(request: Request, file: UploadFile = File(...), db: Session = Depends(get_db)):
    try:
        column_names = ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number']
        df = pd.read_csv(file.file, header=None, names=column_names, dtype={'Card Number': str, 'Target Card Number': str})
        bad_transactions = []

        for _, row in df.iterrows():
            try:
                if len(row) != len(column_names):
                    raise ValueError(f"Invalid number of columns")

                account_name, card_number, transaction_amount, transaction_type = row['Account Name'], row['Card Number'], row['Transaction Amount'], row['Transaction Type']
                description, target_card_number = row.get('Description', ''), row.get('Target Card Number')

                if not card_number.isdigit():
                    raise ValueError("Card number must be numeric")

                if pd.isna(account_name) or pd.isna(card_number) or pd.isna(transaction_amount) or pd.isna(transaction_type):
                    raise ValueError("Missing required fields")

                transaction_amount = float(transaction_amount)
                if transaction_type not in ['Credit', 'Debit', 'Transfer']:
                    raise ValueError("Invalid transaction type")

                account = crud.get_or_create_account(db, account_name)
                target_card_id = None

                if transaction_type == 'Transfer':
                    if pd.isna(target_card_number) or not str(target_card_number).isdigit():
                        raise ValueError("Invalid target card number")
                    target_card = crud.get_or_create_card(db, str(target_card_number), account.id)
                    target_card_id = target_card.id
                    card = crud.get_or_create_card(db, card_number, account.id)
                    card.balance -= transaction_amount
                    target_card.balance += transaction_amount
                    db.commit()
                elif transaction_type == 'Credit':
                    card = crud.get_or_create_card(db, card_number, account.id)
                    card.balance += transaction_amount
                    db.commit()
                elif transaction_type == 'Debit':
                    card = crud.get_or_create_card(db, card_number, account.id)
                    card.balance -= transaction_amount
                    db.commit()

                crud.create_transaction(db, card_id=card.id, target_card_id=target_card_id, amount=transaction_amount, transaction_type=transaction_type, description=description)

            except Exception as e:
                bad_transaction = row.to_dict()
                bad_transaction["Error"] = str(e)
                bad_transactions.append(bad_transaction)
                crud.create_transaction(db, card_id=None, target_card_id=None, amount=0, transaction_type='Invalid', description="Bad data", bad_data=True)

        success_message = "File uploaded successfully!" if not bad_transactions else "File uploaded with errors."
        return templates.TemplateResponse("upload.html", {"request": request, "bad_transactions": bad_transactions, "success_message": success_message})
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})

@app.get("/reports", response_class=HTMLResponse)
async def get_reports(request: Request, db: Session = Depends(get_db)):
    accounts = crud.get_all_accounts(db)
    collections = crud.get_collections(db)
    bad_transactions = crud.get_bad_transactions(db)
    return templates.TemplateResponse("reports.html", {
        "request": request,
        "accounts": accounts,
        "collections": collections,
        "bad_transactions": bad_transactions
    })

@app.get("/reset", response_class=HTMLResponse)
async def reset_form(request: Request):
    return templates.TemplateResponse("reset.html", {"request": request})

@app.post("/reset", response_class=HTMLResponse)
async def reset_system(request: Request, db: Session = Depends(get_db)):
    crud.reset_system(db)
    return templates.TemplateResponse("reset.html", {"request": request, "success_message": "System reset successfully!"})

if __name__ == "__main__":
    uvicorn.run("main:app", host="127.0.0.1", port=5000, reload=True)
