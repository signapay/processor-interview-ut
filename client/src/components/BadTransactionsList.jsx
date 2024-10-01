import React, { useState } from 'react';
import Pagination from './Pagination';

const BadTransactionsList = ({ badTransactions }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const transactionsPerPage = 10;

  const indexOfLastTransaction = currentPage * transactionsPerPage;
  const indexOfFirstTransaction = indexOfLastTransaction - transactionsPerPage;
  const currentTransactions = badTransactions.slice(indexOfFirstTransaction, indexOfLastTransaction);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  return (
    <div className="p-4">
      {badTransactions.length === 0 ? (
        <div className="text-center text-gray-400">No bad transactions...</div>
      ) : (
        <>
          <table className="min-w-full bg-transparent border border-gray-600">
            <thead className="bg-gray-800 text-gray-200">
              <tr>
                <th className="py-2 px-4 border-b border-gray-600 text-center">S.No</th>
                <th className="py-2 px-4 border-b border-gray-600 text-center">Account Name</th>
                <th className="py-2 px-4 border-b border-gray-600 text-center">Card Number</th>
                <th className="py-2 px-4 border-b border-gray-600 text-center">Transaction Amount</th>
                <th className="py-2 px-4 border-b border-gray-600 text-center">Transaction Type</th>
                <th className="py-2 px-4 border-b border-gray-600 text-center">Description</th>
                <th className="py-2 px-4 border-b border-gray-600 text-center">Target Card Number</th>
              </tr>
            </thead>
            <tbody>
              {currentTransactions.map((transaction, index) => (
                <tr key={index} className="bg-gray-900 text-gray-300">
                  <td className="py-2 px-4 border-b border-gray-600 text-center">{index + 1 + indexOfFirstTransaction}</td>
                  <td className="py-2 px-4 border-b border-gray-600 text-center">{transaction.row[0]}</td>
                  <td className="py-2 px-4 border-b border-gray-600 text-center">{transaction.row[1]}</td>
                  <td className="py-2 px-4 border-b border-gray-600 text-center">{transaction.row[2]}</td>
                  <td className="py-2 px-4 border-b border-gray-600 text-center">{transaction.row[3]}</td>
                  <td className="py-2 px-4 border-b border-gray-600 text-center">{transaction.row[4]}</td>
                  <td className="py-2 px-4 border-b border-gray-600 text-center">{transaction.row[5]}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <Pagination
            itemsPerPage={transactionsPerPage}
            totalItems={badTransactions.length}
            paginate={paginate}
            currentPage={currentPage}
          />
        </>
      )}
    </div>
  );
};

export default BadTransactionsList;
