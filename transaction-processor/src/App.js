import React, { useState } from 'react';
import TransactionProcessor from './pages/TransactionProcessor';
import './styles/App.css';  

function App() {
  return (
    <div className="App">
      <main>
        <TransactionProcessor />
      </main>
    </div>
  );
}

export default App;
