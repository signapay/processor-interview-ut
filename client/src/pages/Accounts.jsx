import React, { useEffect, useState } from 'react';
import { getAccounts } from '../utils/api';
import ChartOfAccounts from '../components/ChartOfAccounts';
import Pagination from '../components/Pagination';

const Accounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [accountsPerPage] = useState(16);

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const accountsData = await getAccounts();
        console.log(accounts);
        setAccounts(accountsData);
      } catch (error) {
        console.error('Error fetching accounts:', error);
      }
    };

    fetchAccounts();
  }, []);

  const indexOfLastAccount = currentPage * accountsPerPage;
  const indexOfFirstAccount = indexOfLastAccount - accountsPerPage;
  const currentAccounts = accounts.slice(indexOfFirstAccount, indexOfLastAccount);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  return (
    <div className="container mx-auto p-4">
      <h2 className="text-lg font-bold mb-4 text-white">Chart of Accounts</h2>
      <ChartOfAccounts accounts={currentAccounts} />
      {accounts.length > accountsPerPage && (
        <Pagination
          itemsPerPage={accountsPerPage}
          totalItems={accounts.length}
          paginate={paginate}
          currentPage={currentPage}
        />
      )}
    </div>
  );
};

export default Accounts;
