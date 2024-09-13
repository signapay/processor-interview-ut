import {useState} from 'react'
import Papa from 'papaparse'

function MainContent() {
    const [fileData, setFileData] = useState([]);
    const [badTransactions, setBadTransaction] = useState([]);
  
    const tableColNames = ['Account Name', 'Card Number', 'Transaction Amount', 'Transaction Type', 'Description', 'Target Card Number'];
    const parsedRows = [];
  
    const handleFileUpload = (e) => {
        const file = e.target.files[0];
        
        Papa.parse(file, {
          step: function(row, parser) {
            if (row.errors.length > 0) {
              
              console.log("Could not parse row:", row);
              setBadTransaction(prevTransactions => [...prevTransactions, row.error[0]])
            } else {
              
              console.log("Parsed row:", row.data);
              parsedRows.push(row.data);
            }
          },
          skipEmptyLines: true,
          complete: function(results) {
            console.log("Finished:", parsedRows);
            setFileData(prevData => [...prevData, ...parsedRows]);
          },
          error: function(error, file) {
            console.log("Error parsing:", error);
          },
        });
    }
  
    console.log(badTransactions)
  
    const deleteData = () => {
      setFileData([]);
      setBadTransaction([]);
    }

    const accountsTable = () => {
      return (
        <div className=''>
          <table>
            <thead>
              <tr>
                {tableColNames.slice(0,3).map((names, i) => (
                  <th key={i} className='border border-gray-300 p-2 text-left bg-gray-100'>{names}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {fileData.map((entry, index) => 
                    <tr key={index}>
                      {entry.slice(0,3).map((val, i) => (
                        <td key={i} className="border border-gray-300 p-2">{val}</td>
                      ))}
                    </tr>
                  )
              }
            </tbody>
          </table>
        </div>
        
      )
    }

    const collectionsTable = () => {

      //If transaction amount is < 0 add entry to filteredArray
      const filteredArray = fileData.filter(entry => entry[2] < 0)
      console.log(filteredArray);

      return (
        <div className='mt-5'>
          <table className=''>
            <thead>
              <tr>
                {tableColNames.map((colName, i) => (
                  <th key={i} className='border border-gray-300 p-2 text-left bg-gray-100'>{colName}</th>
                ))}
              </tr>
            </thead>
            <tbody>
                {filteredArray.map((entry, i) => (
                  <tr key={i}>
                    {entry.map((val,index) => (
                      <td key={index} className='border border-gray-30 p-2'>{val}</td>
                    ))}
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
      )
    }

    const badTransactionsTable = () => {
      return (
        <div className='mt-5'>
          <table className=''>
            <thead>
              <tr>
                {tableColNames.map((colName, i) => (
                  <th key={i} className='border border-gray-300 p-2 text-left bg-gray-100'>{colName}</th>
                ))}
              </tr>
            </thead>
            <tbody>
                {badTransactions.map((entry, i) => (
                  <tr key={i}>
                    {entry.map((val,index) => (
                      <td key={index} className='border border-gray-30 p-2'>{val}</td>
                    ))}
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
      )
    }
  
    return (
        <div className='bg-background min-h-screen p-10'>
          <input type='file' onChange={handleFileUpload} onClick={(event)=> { 
               event.target.value = null
          }} accept='.csv'></input>
          <button onClick={deleteData} className='border-2 rounded-md hover:bg-blue-700 p-2 bg-button '>Clear Data</button>
          <h2 className='font-bold text-center m-2 text-2xl'>Accounts Transactions</h2>
          <div className='bg-white rounded-xl p-5 mt-5 overflow-y-auto max-h-[400px] [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]'>
            {accountsTable()}
          </div>

          <h2 className='font-bold text-center m-3 text-2xl'>Accounts Needing Collections</h2>
          <div className='bg-white rounded-xl p-5 mt-5 overflow-y-auto max-h-[400px] [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]'>
            {collectionsTable()}
          </div>

          <h2 className='font-bold text-center m-3 text-2xl'>Bad Transactions</h2>
          <div className='bg-white rounded-xl p-5 mt-5 overflow-y-auto max-h-[400px] [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]'>
            {badTransactions.length != 0 ? badTransactionsTable() : <p>There are no bad transactions</p>}
          </div>
        
        </div>
    )
}


export default MainContent