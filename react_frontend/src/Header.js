import React from 'react';
import './Header.css';
import FileUpload from './FileUpload';
import Reports from './Reports';
import Footer from './Footer';


const Header = ({ setActiveTab , clearData}) => {
    return (
        <div className="header">

            <div className="header-container">
                <div className="tabs">
                    <img src="/company-logo.png" alt="Company Logo" className="logo"/>
                    <span onClick={() => setActiveTab('Upload')}>Upload Transactions</span>
                    <span onClick={() => setActiveTab('accounts')}>Chart of Accounts</span>
                    <span onClick={() => setActiveTab('collections')}>Collections Report</span>
                    <span onClick={() => setActiveTab('bad-transactions')}>Bad Transactions</span>
                </div>
                <button className="clear-data-button" onClick={clearData}>Clear Data</button> {/* Clear Data Button */}

            </div>
        </div>
    );
};

export default Header;
