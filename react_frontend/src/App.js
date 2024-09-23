
import React, { useState, useEffect } from 'react';

import './App.css';
import Header from './Header';
import FileUpload from './FileUpload';
import Reports from './Reports';
import Footer from './Footer';

function App() {
    const [activeTab, setActiveTab] = useState('upload');
    const renderContent = () => {
        switch (activeTab) {
            case 'upload':
                return <FileUpload />;
            case 'accounts':
                return <Reports type="accounts" />;
            case 'collections':
                return <Reports type="collections" />;
            case 'bad-transactions':
                return <Reports type="bad-transactions" />;
            default:
                return <FileUpload />;
        }
    };

    const handleClearData = () => {
        if (window.confirm("Are you sure you want to clear all data?")) {
            fetch('http://localhost:8000/api/clear-data/', {
                method: 'POST',
            })
                .then(() => alert('Data cleared successfully!'))
                .catch((error) => alert('Error clearing data: ' + error));
        }
    };

    return (
        <div className="App">
            <Header setActiveTab={setActiveTab} clearData={handleClearData} />
            <div className="content">
                {renderContent()}
            </div>
            <Footer />
        </div>
    );
}

export default App;
