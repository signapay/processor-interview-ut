import streamlit as st
from transaction_processor import handle_transactions #Processing logic
import pandas as pd 

# Initialize session state for persistence
if 'actDict' not in st.session_state:
    st.session_state.actDict = {}
if 'collections' not in st.session_state:
    st.session_state.collections = []
if 'bad_transactions' not in st.session_state:
    st.session_state.bad_transactions = []

def main():
    st.title("SignaPay Transaction Processor")

    # Upload file
    upl_file=st.file_uploader("Drop csv file",type=["csv"])

    # button to clear the session state
    if st.button("Reset"):
        st.session_state.actDict = {}
        st.session_state.collections = []
        st.session_state.bad_transactions = []

        # Refresh the page
        st.warning("System reset successfully. Please refresh the page to continue.")

    if upl_file:

        # Call function and store values
        handle_transactions(upl_file,st.session_state.actDict,st.session_state.collections,st.session_state.bad_transactions)

        # Display account names, cards and balance
        st.write("## Account Names and Cards, Balance") 
        if st.session_state.actDict:

            # DataFrame to hold account name, card number, and balance
            data = []
            for a, c in st.session_state.actDict.items():
                for cNum, bal in c.items():
                    data.append({
                        "Account Name": a,
                        "Card Number": cNum,
                        "Balance": f"${bal:}"
                    })
            
            
            # Create DataFrame
            df_accounts = pd.DataFrame(data,index=pd.RangeIndex(start=1,stop=len(data)+1))
            
            # Display DataFrame as a table
            st.dataframe(df_accounts)

        # Display accounts to be sent for collections
        st.write("## Accounts to be sent to collections:")
        if st.session_state.collections:
            collec=pd.DataFrame(st.session_state.collections,columns=['Account Name'],index=pd.RangeIndex(start=1,stop=len(st.session_state.collections)+1))
            st.dataframe(collec)
            # print(collections)
        else:
            st.write("No accounts need to go to collections.")

        # Display bad transactions
        st.write("## Bad Transactions:")
        if st.session_state.bad_transactions:
            # bad_df = pd.DataFrame(bad_transactions)
            # st.dataframe(st.session_state.bad_transactions)
            # print(bad_transactions)
            cols=['Account Name','Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number']
            bdf=pd.DataFrame(st.session_state.bad_transactions,columns=cols,index=pd.RangeIndex(start=1,stop=len(st.session_state.bad_transactions)+1))
            st.table(bdf)
        else:
            st.write("No bad transactions found.")

if __name__ == '__main__':
    main()