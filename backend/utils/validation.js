const validateTransaction = (data) => {
    const cardNumberValid = /^\d{16}$/.test(data.cardNumber); // Check for 16-digit card number
    const targetCardNumberValid = !data.targetCardNumber || /^\d{16}$/.test(data.targetCardNumber); // Check for target card number validity

    return (
        data.accountName &&
        cardNumberValid &&
        !isNaN(data.transactionAmount) &&
        (data.transactionType === 'Credit' || data.transactionType === 'Debit' || data.transactionType === 'Transfer') &&
        targetCardNumberValid
    );
};

module.exports = {
    validateTransaction,
};
