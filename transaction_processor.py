import pandas as pd
import csv
import io

def file_reader(fileName,bad_transactions):
    good_transactions=[]

    fileName.seek(0)
    decoded_file = io.TextIOWrapper(fileName, encoding='utf-8')

    # Open file
    reader=csv.reader(decoded_file)
    for split in reader:
        # split=line.strip().split(',')

        # Inital check: If too little columns or too many then add to bad transactions
        if len(split) < 5 or len(split)>6:
            bad_transactions.append(split)
            continue

        # Check if card number is numeric if not add to bad transactions
        if not split[1].strip().isnumeric():
            bad_transactions.append(split)
            continue
        
        # Checking if amount is numeric
        if isinstance(split[2].strip(),float):
            bad_transactions.append(split)
            continue

        # Check if transaction type is transfer and target card number is nan or if its numeric
        if split[3].strip()=="Transfer":

            # If not null check for numeric
            if not split[5].strip():
                if not split[5].strip().isnumeric():
                    bad_transactions.append(split)
                    continue

        # if no issues add to good transactions
        good_transactions.append(split)

    return good_transactions

def process_transactions(actDict,collections,df):

    # Process all transactions
    for i,row in df.iterrows():
        name=row['Account Name']
        fromNum=row['Card Number']
        type=row['Transaction Type']
        amount=row['Transaction Amount']
        toNum=row['Target Card Number']

        if name in actDict:
            if fromNum in actDict[name]:
                # Call transaction function
                transaction(fromNum,amount,type,actDict,name,toNum)

            else:
                actDict[name][fromNum]=0

                # Call transaction function
                transaction(fromNum,amount,type,actDict,name,toNum)
        else:
            # Add account Name
            actDict[name]={}
            actDict[name][fromNum]=0

            # Call transaction function
            transaction(fromNum,amount,type,actDict,name,toNum)

    # Check for cards with negative balance and add to collections
    for a in actDict:
        for c in actDict[a]:
            if actDict[a][c]<0.00:
                if a not in collections: 
                    collections.append(a)
                break
            else:
                if a in collections: 
                    collections.remove(a)
    
    return
    


    

def transaction(fromNum,amount,type,actDict,name,toNum=''):
    if type=="Credit":
        # Assuming that credit is money added to account
        actDict[name][fromNum]+=amount

    elif type=="Debit":
        # Assuming debit is money removed from account
        actDict[name][fromNum]-=amount
        
    elif type=="Transfer":

        # Assuming that the to accountNum has alreaady occured in file
        actDict[name][fromNum]-=amount

        # Assuming that card numbers are unique and no 2 names have cards with same num
        for a in actDict:
            if toNum in a:
                actDict[a][toNum]+=amount
                break


def handle_transactions(file,actDict,collections,bad_transactions):

    # Read the file
    good_transactions=file_reader(file,bad_transactions)

    # Define columns of df
    cols=['Account Name','Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number']
   
    # Add good transactions into df for easy processing
    df=pd.DataFrame(good_transactions,columns=cols)

    # Casting cols to required datatypes
    df['Transaction Amount'] = pd.to_numeric(df['Transaction Amount'], errors='coerce')

    process_transactions(actDict,collections,df)

    return