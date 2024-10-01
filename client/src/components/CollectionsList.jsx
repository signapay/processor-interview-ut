import React, { useState } from 'react';
import Pagination from './Pagination';

const CollectionsList = ({ accounts }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const accountsPerPage = 11;

  const indexOfLastAccount = currentPage * accountsPerPage;
  const indexOfFirstAccount = indexOfLastAccount - accountsPerPage;
  const currentAccounts = accounts.slice(indexOfFirstAccount, indexOfLastAccount);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  return (
    <div className="p-4">
      {accounts.length === 0 ? (
        <div className="text-center text-gray-400">No accounts to display...</div>
      ) : (
        <>
          <table className="min-w-full bg-transparent border border-gray-600">
            <thead className="bg-gray-800 text-gray-200">
              <tr>
                <th className="py-2 px-4 border-b border-gray-600 text-center">Card Holder</th>
                <th className="py-2 px-4 border-b border-gray-600 text-center">Card Number</th>
                <th className="py-2 px-4 border-b border-gray-600 text-center">Amount</th>
              </tr>
            </thead>
            <tbody>
              {currentAccounts.map((account, index) => (
                <tr key={index} className="bg-gray-900 text-gray-300">
                  <td className="py-2 px-4 border-b border-gray-600 text-center">{account.accountName}</td>
                  <td className="py-2 px-4 border-b border-gray-600 text-center">{account.cardNumber}</td>
                  <td className="py-2 px-4 border-b border-gray-600 text-center">${account.amount.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <Pagination
            itemsPerPage={accountsPerPage}
            totalItems={accounts.length}
            paginate={paginate}
            currentPage={currentPage}
          />
        </>
      )}
    </div>
  );
};

export default CollectionsList;
