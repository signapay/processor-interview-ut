const validTransactionTypes = ['Transfer', 'Credit', 'Debit'];

const isValidTransaction = (transaction) => {
  if (
    !transaction['Account Name'] ||
    !transaction['Card Number'] ||
    !transaction['Transaction Amount'] ||
    !transaction['Transaction Type'] ||
    !transaction['Description']
  ) {
    return false;
  }

  if (!validTransactionTypes.includes(transaction['Transaction Type'])) {
    return false;
  }

  if (transaction['Transaction Type'] === 'Transfer' && !transaction['Target Card Number']) {
    return false;
  }

  const transactionAmount = parseFloat(transaction['Transaction Amount']);
  if (isNaN(transactionAmount)) {
    return false;
  }

  const cardNumberRegex = /^\d+$/;
  if (!cardNumberRegex.test(transaction['Card Number'])) {
    return false;
  }

  if (transaction['Target Card Number'] && !cardNumberRegex.test(transaction['Target Card Number'])) {
    return false;
  }

  return true;
};

module.exports = { isValidTransaction };
