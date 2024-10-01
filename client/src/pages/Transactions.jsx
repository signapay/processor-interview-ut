import React, { useEffect, useState } from 'react';
import BadTransactionsList from '../components/BadTransactionsList';
import { getBadTransactions } from '../utils/api';

const Transactions = () => {
  const [badTransactions, setBadTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchBadTransactions = async () => {
      try {
        const data = await getBadTransactions();
        console.log(data);
        setBadTransactions(Array.isArray(data) ? data : []);
      } catch (error) {
        setError('Failed to fetch bad transactions');
      } finally {
        setLoading(false);
      }
    };

    fetchBadTransactions();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="container mx-auto p-4">
      <h2 className="text-lg font-bold mt-8 mb-4">Bad Transactions</h2>
      <BadTransactionsList badTransactions={badTransactions} />
    </div>
  );
};

export default Transactions;
