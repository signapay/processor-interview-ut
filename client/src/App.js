import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, Input, Typography, TextField } from '@mui/material';
import { CloudUpload } from '@mui/icons-material';
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
    const [file, setFile] = useState(null);
    const [accountBalances, setAccountBalances] = useState(null);
    const [collections, setCollections] = useState(null);
    const [badTransactions, setBadTransactions] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isLoggedIn, setIsLoggedIn] = useState(!!token);

    useEffect(() => {
        if (token) {
            localStorage.setItem('token', token);
            setIsLoggedIn(true);
        } else {
            localStorage.removeItem('token');
            setIsLoggedIn(false);
        }
    }, [token]);

    const api = axios.create({
        baseURL: 'http://localhost:9000/api',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });

    const handleLogin = async () => {
        try {
            const response = await axios.post('http://localhost:9000/api/auth/login', { username, password });
            setToken(response.data.token);
            alert('Logged in successfully');
        } catch (error) {
            console.error('Login error', error);
            alert('Error logging in');
        }
    };

    const handleRegister = async () => {
        try {
            await axios.post('http://localhost:9000/api/auth/register', { username, password });
            alert('Registered successfully. Please log in.');
        } catch (error) {
            console.error('Registration error', error);
            alert('Error registering');
        }
    };

    const handleLogout = () => {
        setToken(null);
        setUsername('');
        setPassword('');
    };

    const handleFileUpload = (e) => {
        const selectedFile = e.target.files[0];
        alert(`${selectedFile.name} chosen successfully. Please click UPLOAD TRANSACTION to upload the file.`);
        setFile(selectedFile);
    };

    const uploadFile = async () => {
        if (!file) {
            alert('Please choose a file for the upload process.');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            await api.post('/transactions/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            alert('File uploaded successfully.');
        } catch (err) {
            console.error(err);
            alert('Error uploading file');
        }
    };

    const getAccountBalances = async () => {
        try {
            const res = await api.get('/transactions/reports');
            setAccountBalances(res.data.accountBalances);
            setCollections(null);
            setBadTransactions(null);
        } catch (err) {
            console.error(err);
            alert('Error fetching account balances');
        }
    };

    const getCollections = async () => {
        try {
            const res = await api.get('/transactions/reports');
            setCollections(res.data.collections);
            setAccountBalances(null);
            setBadTransactions(null);
        } catch (err) {
            console.error(err);
            alert('Error fetching collections');
        }
    };

    const getBadTransactions = async () => {
        try {
            const res = await api.get('/transactions/reports');
            setBadTransactions(res.data.badTransactions);
            setAccountBalances(null);
            setCollections(null);
        } catch (err) {
            console.error(err);
            alert('Error fetching bad transactions');
        }
    };

    const resetSystem = async () => {
        try {
            await api.post('/transactions/reset');
            alert('System reset successfully.');
            setAccountBalances(null);
            setCollections(null);
            setBadTransactions(null);
        } catch (err) {
            console.error(err);
            alert('Error resetting system');
        }
    };

    const renderAccountBalances = () => {
        return (
            <div>
                <h1>Account Balances</h1>
                <TableContainer component={Paper}>
                    <Table sx={{ minWidth: 650 }} aria-label="account balances table">
                        <TableHead>
                            <TableRow>
                                <TableCell align="center">Card Number</TableCell>
                                <TableCell align="center">Account Names</TableCell>
                                <TableCell align="center">Balance</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {accountBalances.map((entry, idx) => (
                                <TableRow key={idx}>
                                    <TableCell align="center">{entry.cardNumber}</TableCell>
                                    <TableCell align="center">{entry.accountNames.join(', ')}</TableCell>
                                    <TableCell align="center">${entry.balance.toFixed(2)}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </div>
        );
    };

    const renderCollections = () => {
        return (
            <div>
                <h1>Collections</h1>
                {collections.length > 0 ? (
                    <TableContainer component={Paper}>
                        <Table sx={{ minWidth: 650 }} aria-label="collections table">
                            <TableHead>
                                <TableRow>
                                    <TableCell align="center">Card Number</TableCell>
                                    <TableCell align="center">Account Names</TableCell>
                                    <TableCell align="center">Balance</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {collections.map((txn, idx) => (
                                    <TableRow key={idx}>
                                        <TableCell align="center">{txn.cardNumber || 'N/A'}</TableCell>
                                        <TableCell align="center">{txn.accountNames.join(', ') || 'N/A'}</TableCell>
                                        <TableCell align="center">${txn.balance.toFixed(2)}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                ) : (
                    <p>No accounts in collections.</p>
                )}
            </div>
        );
    };

    const renderBadTransactions = () => {
        return (
            <div>
                <h1>Bad Transactions</h1>
                {badTransactions.length > 0 ? (
                    <TableContainer component={Paper}>
                        <Table sx={{ minWidth: 650 }} aria-label="bad transactions table">
                            <TableHead>
                                <TableRow>
                                    <TableCell align="center">Account Name</TableCell>
                                    <TableCell align="center">Card Number</TableCell>
                                    <TableCell align="center">Transaction Amount</TableCell>
                                    <TableCell align="center">Transaction Type</TableCell>
                                    <TableCell align="center">Description</TableCell>
                                    <TableCell align="center">Target Card Number</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {badTransactions.map((txn, idx) => (
                                    <TableRow key={idx}>
                                        <TableCell align="center">{txn.accountName || 'N/A'}</TableCell>
                                        <TableCell align="center">{txn.cardNumber || 'N/A'}</TableCell>
                                        <TableCell align="center">{txn.transactionAmount}</TableCell>
                                        <TableCell align="center">{txn.transactionType}</TableCell>
                                        <TableCell align="center">{txn.description || 'N/A'}</TableCell>
                                        <TableCell align="center">{txn.targetCardNumber || 'N/A'}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                ) : (
                    <p>No bad transactions.</p>
                )}
            </div>
        );
    };

    return (
        <div className="App container mt-5">
            <Typography 
                variant="h3" 
                align="center" 
                sx={{ mb: 4, fontWeight: 'bold', color: 'black' }} 
            >
                Transaction Processor
            </Typography>

            {!isLoggedIn ? (
                <div>
                    <TextField label="Username" value={username} onChange={(e) => setUsername(e.target.value)} />
                    <TextField type="password" label="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
                    <Button onClick={handleLogin}>Login</Button>
                    <Button onClick={handleRegister}>Register</Button>
                </div>
            ) : (
                <>
                    <Button onClick={handleLogout}>Logout</Button>
                    <div className="mb-3">
                        <label htmlFor="file-upload" style={{ display: 'flex', alignItems: 'center' }}>
                            <Input
                                id="file-upload"
                                type="file"
                                onChange={handleFileUpload}
                                accept=".csv"
                                style={{ display: 'none' }}
                            />
                            <Button
                                variant="contained"
                                component="span"
                                startIcon={<CloudUpload />}
                                sx={{ backgroundColor: '#4CAF50', color: '#fff', textTransform: 'none' }}
                            >
                                Choose File
                            </Button>
                            {file && <span style={{ marginLeft: '10px' }}>{file.name}</span>}
                        </label>
                    </div>

                    <div className="flex space-x-4 mb-4">
                        <Button variant="contained" onClick={uploadFile}>Upload Transactions</Button>
                        <Button variant="contained" onClick={getAccountBalances}>Get Account Balances</Button>
                        <Button variant="contained" onClick={getCollections}>Get Collections</Button>
                        <Button variant="contained" onClick={getBadTransactions}>Get Bad Transactions</Button>
                        <Button variant="contained" onClick={resetSystem} sx={{ backgroundColor: 'red', color: 'white', '&:hover': { backgroundColor: '#d32f2f' } }}>Reset System</Button>
                    </div>

                    {accountBalances && renderAccountBalances()}
                    {collections && renderCollections()}
                    {badTransactions && renderBadTransactions()}
                </>
            )}
        </div>
    );
}

export default App;