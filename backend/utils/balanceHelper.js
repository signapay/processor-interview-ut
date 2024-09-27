const sortByAccountName = (array) => {
    return array.sort((a, b) => a.accountName.localeCompare(b.accountName));
};

const updateAccountBalance = (parsedData, accountBalances) => {
    const { cardNumber, transactionAmount, transactionType, targetCardNumber, accountName } = parsedData;

    if (!cardNumber) return;

    if (!accountBalances[cardNumber]) {
        accountBalances[cardNumber] = { balance: 0, accountNames: new Set() };
    }

    accountBalances[cardNumber].accountNames.add(accountName || "No account name found");

    if (transactionType === 'Credit') {
        accountBalances[cardNumber].balance += transactionAmount;
    } else if (transactionType === 'Debit') {
        accountBalances[cardNumber].balance -= transactionAmount;
    } else if (transactionType === 'Transfer') {
        accountBalances[cardNumber].balance -= transactionAmount;

        if (targetCardNumber && targetCardNumber !== cardNumber) {
            if (!accountBalances[targetCardNumber]) {
                accountBalances[targetCardNumber] = { balance: 0, accountNames: new Set() };
            }
            accountBalances[targetCardNumber].balance += transactionAmount;
        }
    }
};

const updateCollections = (accountBalances) => {
    return Object.entries(accountBalances)
        .filter(([, { balance }]) => balance < 0)
        .map(([cardNumber, { balance, accountNames }]) => ({
            cardNumber,
            balance,
            accountNames: accountNames.size > 0 ? Array.from(accountNames) : ['NO ACCOUNT NAME FOUND'],
        }));
};

module.exports = {
    sortByAccountName,
    updateAccountBalance,
    updateCollections,
};
