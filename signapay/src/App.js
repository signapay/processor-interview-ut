import React, { useState, useRef } from 'react';
import './App.css';
import FileProcess from './FileProcess.js';

function App() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [showTable, setShowTable] = useState(false);
  const [buttonNumber, setButtonNumber] = useState(0);
  const [uploadedFiles, setUploadedFiles] = useState([]);
  const fileInputRef = useRef(null);

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    setSelectedFile(file);
    setUploadedFiles(prevFiles => [...prevFiles, file]);
  };

  const handleButton = (number) => {
    setButtonNumber(number);
    if (number === 0) {
      setShowTable(false);
      setUploadedFiles([]); // Clear the uploaded files list
      setSelectedFile(null); // Clear the selected file in input form
      if (fileInputRef.current) {
        fileInputRef.current.value = ''; // Clear the input form
      }
      console.log('Reset button clicked');
    } else {
      setShowTable(true);
      //console.log(`${number === 1 ? 'Account Chart' : number === 2 ? 'Give to collections' : 'Bad transactions'} button clicked`);
    }
  };

  return (
    <div className="App">
      <h1> Transaction Processor</h1>
      <div className='upLoadFile'>
        <div>
          <input type="file" accept=".csv" onChange={handleFileChange} ref={fileInputRef} />
            <span><strong>Uploaded File:  </strong>  </span>
          
            {uploadedFiles.map((file, index) => (
              <span key={index}>{' '+file.name+', '}</span>
            ))}
       
         </div>
          <div><button className='button-27' onClick={() => handleButton(0)}>Reset ↻</button></div>
      </div>
      <div style={{border: '2px solid red'}}>
        <h1>Reporting</h1>
        <div className='Reporting'>
          <button className='button-27' onClick={() => handleButton(1)}>Account Chart</button>
          <button className='button-27' onClick={() => handleButton(2)}>Give to collections</button>
          <button className='button-27' onClick={() => handleButton(3)}>Bad transactions</button>
        </div>
        {showTable && <FileProcess file={selectedFile} btnNum={buttonNumber} />}
      </div>
     
    </div>
  );
}

export default App;
