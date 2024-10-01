import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './pages/Home';
import Transactions from './pages/Transactions';
import Accounts from './pages/Accounts';
import Collections from './pages/Collections';
import Navbar from './components/Navbar';

function App() {
  return (
    <Router>
      <Navbar />
      <div className="pt-16">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/transactions" element={<Transactions />} />
          <Route path="/accounts" element={<Accounts />} />
          <Route path="/collections" element={<Collections />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
