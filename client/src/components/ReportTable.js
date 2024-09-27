import React from 'react';

const ReportTable = ({ report, headers, dataKey }) => {
  const renderRows = () => {
    if (dataKey === 'accounts') {
      // Handle accounts object structure
      return Object.entries(report.accounts).map(([accountName, accountData]) => (
        Object.entries(accountData.cards).map(([cardNumber, cardData]) => (
          <tr key={`${accountName}-${cardNumber}`}>
            <td>{accountName}</td>
            <td>{cardNumber}</td>
            <td>{cardData.amount}</td>
          </tr>
        ))
      ));
    } else {
      // Handle collections and badTransactions which are arrays
      return report[dataKey].map((item, index) => (
        <tr key={index}>
          {headers.map((header, headerIndex) => (
            <td key={headerIndex}>{item[header] || 'N/A'}</td>
          ))}
        </tr>
      ));
    }
  };

  return (
    <table border="1">
      <thead>
        <tr>
          {headers.map((header, index) => (
            <th key={index}>{header}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {renderRows()}
      </tbody>
    </table>
  );
};

export default ReportTable;
