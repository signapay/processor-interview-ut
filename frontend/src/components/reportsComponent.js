// ReportsComponent.js
import React, { useState, useEffect } from 'react';
import { getReports } from '../apiService';

const ReportsComponent = ({refreshTrigger}) => {
  const [reports, setReports] = useState([]);

  useEffect(() => {
    const fetchReports = async () => {
      const data = await getReports();
      setReports(data);
    };
    fetchReports();
  }, [refreshTrigger]);

  return (
    <div>
      <h3>Account Reports</h3>
      {Object.keys(reports).map((account) => (
        <div key={account}>
          <h4>{account}</h4>
          {Object.entries(reports[account]).map(([card, balance]) => (
            <p key={card}>
              Card: {card}, Balance: {balance}
            </p>
          ))}
        </div>
      ))}
    </div>
  //   <div className="container mt-5">
  //   <h3 className="mb-4">Account Reports</h3>
  //   {Object.keys(reports).map((account) => (
  //     <div key={account} className="mb-4">
  //       <h4>{account}</h4>
  //       <table className="table table-striped table-bordered">
  //         <thead className="thead-dark">
  //           <tr>
  //             <th scope="col">Card Number</th>
  //             <th scope="col">Balance</th>
  //           </tr>
  //         </thead>
  //         <tbody>
  //           {Object.entries(reports[account]).map(([card, balance]) => (
  //             <tr key={card}>
  //               <td>{card}</td>
  //               <td>${parseFloat(balance).toFixed(2)}</td>
  //             </tr>
  //           ))}
  //         </tbody>
  //       </table>
  //     </div>
  //   ))}
  // </div>
  );
};

export default ReportsComponent;
