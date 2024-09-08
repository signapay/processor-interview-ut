import React, { useState, useEffect, useRef } from 'react';

// Define all possible headers for the table
const allHeaders = ["Account Name", "Card Number", "Transaction Amount", "Transaction Type", "Description", "Target Card Number"];

function FileProcess({ file, btnNum }) {
  // State variables to store data for each button
  const [data1, setData1] = useState([]);
  const [data2, setData2] = useState([]);
  const [data3, setData3] = useState([]);
  const [headers, setHeaders] = useState(allHeaders); // State to store table headers
  const [prevFile, setPrevFile] = useState(null); // State to store the previous file
  const isInitialRender = useRef(true); // Ref to track initial render

  // Effect to handle file upload and data processing
  useEffect(() => {
    if (isInitialRender.current) {
      isInitialRender.current = false;
      return;
    }

    console.log('useEffect for file triggered');
    if (file && file !== prevFile) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const text = e.target.result;
        const rows = text.split('\n').map(row => row.split(','));

        // Process data for button 1
        let parsedData1 = rows.map(cells => [cells[0], cells[1], cells[2]]);

        // Process data for button 2: any cards with sum of all transactions < 0.00, excluding bad transactions
        let parsedData2 = rows.reduce((acc, cells) => {
          if (cells.some(cell => cell.trim() === '')) return acc; // Exclude bad transactions
          const card = cells[1];
          const amount = parseFloat(cells[2].trim());
          if (!acc[card]) {
            acc[card] = 0;
          }
          acc[card] += amount;
          return acc;
        }, {});
        parsedData2 = Object.entries(parsedData2)
                           .filter(([card, balance]) => balance < 0)
                           .map(([card, balance]) => {
                             const accountName = rows.find(cells => cells[1] === card)[0];
                             return [accountName, card, balance.toFixed(2)];
                           });

        // Process data for button 3: only show rows with missing elements
        let parsedData3 = rows.filter(cells => cells.some(cell => cell.trim() === ''));

        // Update state with the new data
        setData1(prevData => [...prevData, ...parsedData1]);
        setData2(prevData => [...prevData, ...parsedData2]);
        setData3(prevData => [...prevData, ...parsedData3]);
        setPrevFile(file);
      };
      reader.readAsText(file);
    }
  }, [file, prevFile]);

  // Effect to update table headers based on the button clicked
  useEffect(() => {
    console.log('useEffect for btnNum triggered');
    if (btnNum === 1) {
      setHeaders(["Account Name", "Card Number", "Transaction Amount"]);
    } else if (btnNum === 2) {
      setHeaders(["Account Name", "Card Number", "Transaction Amount"]);
    } else if (btnNum === 3) {
      setHeaders(allHeaders);
    }
  }, [btnNum]);

  // Function to render the table with the given data
  const renderTable = (data) => (
    <table>
      <thead>
        <tr>
          {headers.map((header, index) => (
            <th key={index}>{header}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {data.map((row, rowIndex) => (
          <tr key={rowIndex}>
            {row.map((cell, cellIndex) => (
              <td key={cellIndex}>{cell}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );

  return (
    <div>
      {btnNum === 1 && renderTable(data1)}
      {btnNum === 2 && renderTable(data2)}
      {btnNum === 3 && renderTable(data3)}
    </div>
  );
}

export default FileProcess;
