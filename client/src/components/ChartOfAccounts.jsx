import React, { useState } from 'react';
import Pagination from './Pagination';

const ChartOfAccounts = ({ accounts }) => {
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [cardsPerPage] = useState(10);

  const indexOfLastCard = currentPage * cardsPerPage;
  const indexOfFirstCard = indexOfLastCard - cardsPerPage;
  const currentCards = selectedAccount ? selectedAccount.cards.slice(indexOfFirstCard, indexOfLastCard) : [];

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  const handleViewDetails = (account) => {
    if (selectedAccount && selectedAccount.accountName === account.accountName) {
      setSelectedAccount(null);
    } else {
      setSelectedAccount(account);
      setCurrentPage(1);
    }
  };

  return (
    <div className="p-4">
      {accounts.length === 0 ? (
        <div className="text-center text-gray-400">No data...</div>
      ) : (
        <table className="min-w-full bg-transparent border border-gray-600">
          <thead className="bg-gray-800 text-gray-200">
            <tr>
              <th className="py-2 px-4 border-b border-gray-600 w-2/4 text-center">Account Name</th>
              <th className="py-2 px-4 border-b border-gray-600 w-1/4 text-center"># of Cards</th>
              <th className="py-2 px-4 border-b border-gray-600 w-1/4 text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {accounts.map((account) => (
              <React.Fragment key={account.accountName}>
                <tr className="bg-gray-900 text-gray-300 text-center">
                  <td className="py-2 px-4 border-b border-gray-600 w-2/4">{account.accountName}</td>
                  <td className="py-2 px-4 border-b border-gray-600 w-1/4">{account.cards.length}</td>
                  <td className="py-2 px-4 border-b border-gray-600 w-1/4">
                    <button
                      className="bg-blue-500 text-white text-sm px-2 py-1 rounded hover:bg-blue-700"
                      onClick={() => handleViewDetails(account)}
                    >
                      {selectedAccount && selectedAccount.accountName === account.accountName
                        ? 'Hide'
                        : 'View'}
                    </button>
                  </td>
                </tr>

                {/* Details row - appears when "View Details" is clicked */}
                {selectedAccount && selectedAccount.accountName === account.accountName && (
                  <tr className="bg-gray-800 text-gray-300">
                    <td colSpan="3" className="p-4">
                      <h3 className="text-md font-bold mb-2">Cards for {selectedAccount.accountName}</h3>
                      <ul className="list-disc pl-5">
                        {currentCards.map((card, index) => (
                          <li key={index}>
                            Card Number: {card.cardNumber} | Balance: ${card.balance.toFixed(2)}
                          </li>
                        ))}
                      </ul>

                      {selectedAccount.cards.length > cardsPerPage && (
                        <Pagination
                          itemsPerPage={cardsPerPage}
                          totalItems={selectedAccount.cards.length}
                          paginate={paginate}
                          currentPage={currentPage}
                        />
                      )}
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ChartOfAccounts;
