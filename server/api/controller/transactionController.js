import Papa from 'papaparse';

let chartOfAccounts = {};
let collections = [];
let badTransactions = [];

const FIELD_MAPPING = {
  accountName: 0,
  cardNumber: 1,
  amount: 2,
  transactionType: 3,
  description: 4,
  targetCardNumber: 5,
};

const uploadTransactions = (req, res) => {
    try {
        const file = req.file;

        if (!file) {
            return res.status(400).json({ message: 'No file uploaded' });
        }

        const csvData = file.buffer.toString('utf-8');
        Papa.parse(csvData, {
            header: false,
            skipEmptyLines: true,
            complete: (results) => {
                const parsedData = results.data;
                const FIELD_MAPPING = { accountName: 0, cardNumber: 1, amount: 2, transactionType: 3, description: 4, targetCardNumber: 5 };
                
                parsedData.forEach((row) => {
                    const accountName = row[FIELD_MAPPING.accountName];
                    const cardNumber = row[FIELD_MAPPING.cardNumber];
                    const amount = row[FIELD_MAPPING.amount];
                    const transactionType = row[FIELD_MAPPING.transactionType];
                    const description = row[FIELD_MAPPING.description] || '';
                    const targetCardNumber = row[FIELD_MAPPING.targetCardNumber] || null;

                    if (!/^\d+$/.test(cardNumber)) {
                        badTransactions.push(row);
                        return;
                    }

                    const transactionAmount = parseFloat(amount);
                    
                    if (isNaN(transactionAmount)) {
                        badTransactions.push(row);
                        return;
                    }

                    if (accountName && cardNumber && transactionType) {
                        if (!chartOfAccounts[accountName]) {
                            chartOfAccounts[accountName] = {};
                        }

                        if (!chartOfAccounts[accountName][cardNumber]) {
                            chartOfAccounts[accountName][cardNumber] = 0;
                        }

                        if (transactionType === 'Credit') {
                            chartOfAccounts[accountName][cardNumber] += transactionAmount;
                        } else if (transactionType === 'Debit') {
                            chartOfAccounts[accountName][cardNumber] -= transactionAmount;
                        } else if (transactionType === 'Transfer') {
                            if (!targetCardNumber || !/^\d+$/.test(targetCardNumber)) {
                                badTransactions.push(row);
                                return;
                            }

                            chartOfAccounts[accountName][cardNumber] -= transactionAmount;

                            if (!chartOfAccounts[accountName][targetCardNumber]) {
                                chartOfAccounts[accountName][targetCardNumber] = 0;
                            }

                            chartOfAccounts[accountName][targetCardNumber] += transactionAmount;
                        } else {
                            badTransactions.push(row);
                            return;
                        }

                        if (chartOfAccounts[accountName][cardNumber] < 0) {
                            collections.push({
                                accountName,
                                cardNumber,
                                amount: chartOfAccounts[accountName][cardNumber],
                                transactionType,
                                description,
                                targetCardNumber,
                            });
                        } else {
                            const index = collections.findIndex((item) => item.cardNumber === cardNumber && item.accountName === accountName);
                            if (index !== -1) {
                                collections.splice(index, 1);
                            }
                        }
                    } else {
                        badTransactions.push(row);
                    }
                });
                res.status(200).json({
                    message: 'Transactions processed successfully',
                    chartOfAccounts,
                    collections,
                    badTransactions,
                });
            },
            error: (error) => {
                res.status(500).json({ message: 'Error parsing CSV file', error: error.message });
            }
        });
    } catch (error) {
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

const resetTransactions = (req, res) => {
    try {
        chartOfAccounts = {};
        collections = [];
        badTransactions = [];

        res.status(200).json({ message: 'Data cleared successfully' });
    } catch (error) {
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

const getAccounts = (req, res) => {
    try {
        const accounts = Object.keys(chartOfAccounts).map(accountName => ({
            accountName,
            cards: Object.keys(chartOfAccounts[accountName]).map(cardNumber => ({
                cardNumber,
                balance: chartOfAccounts[accountName][cardNumber]
            }))
        }));
  
        res.status(200).json({
            message: "Accounts retrieved successfully",
            accounts
        });
    } catch (error) {
        res.status(500).json({ message: "Server error", error: error.message });
    }
};

  
const getCollections = (req, res) => {
    try {
        res.status(200).json({
            message: "Collections retrieved successfully",
            collections
        });
    } catch (error) {
        res.status(500).json({ message: "Server error", error: error.message });
    }
};
  
  // Function to get bad transactions
const getBadTransactions = (req, res) => {
    try {
        res.status(200).json({
            message: "Bad transactions retrieved successfully",
            badTransactions: badTransactions.map(transaction => ({
                row: transaction
            }))
        });
    } catch (error) {
        res.status(500).json({ message: "Server error", error: error.message });
    }
};


export { uploadTransactions, resetTransactions, getAccounts, getCollections, getBadTransactions };
