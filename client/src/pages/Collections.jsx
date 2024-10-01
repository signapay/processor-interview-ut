import React, { useEffect, useState } from 'react';
import CollectionsList from '../components/CollectionsList';
import { getCollections } from '../utils/api';

const Collections = () => {
  const [accounts, setAccounts] = useState([]);

  useEffect(() => {
    const fetchCollections = async () => {
      try {
        const data = await getCollections();
        setAccounts(data);
      } catch (error) {
        console.error('Error fetching collections:', error);
      }
    };

    fetchCollections();
  }, []);

  return (
    <div className="container mx-auto p-4">
      <h2 className="text-lg font-bold mb-4">Collections List</h2>
      <CollectionsList accounts={accounts} />
    </div>
  );
};

export default Collections;
