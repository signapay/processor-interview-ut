// Elements selection
const uploadForm = document.getElementById('uploadForm');
const fileInput = document.getElementById('file_input');
const fileStatus = document.getElementById('fileStatus');
const transactionsBody = document.getElementById('transactionsBody');
const collectionsBody = document.getElementById('collectionsBody');
const badTransactionsBody = document.getElementById('badTransactionsBody');

// Calculate a hash for file caching
async function calculateFileHash(file) {
    const arrayBuffer = await file.arrayBuffer();
    const hashBuffer = await crypto.subtle.digest('SHA-256', arrayBuffer);
    return Array.from(new Uint8Array(hashBuffer)).map(b => b.toString(16).padStart(2, '0')).join('');
}

// Handle file upload and submission
uploadForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    const file = fileInput.files[0];
    if (!file) return updateStatus('Please select a file.', 'red');

    const fileHash = await calculateFileHash(file);
    const cachedData = localStorage.getItem(fileHash);
    if (cachedData) return updateStatus('Loaded from cache.', 'green'), displayResults(JSON.parse(cachedData));

    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch('/upload', { method: 'POST', body: formData });
        const data = await response.json();
        if (response.ok) {
            localStorage.setItem(fileHash, JSON.stringify(data));
            updateStatus('File processed and cached successfully!', 'green');
            displayResults(data);
        } else {
            updateStatus(data.message || 'Error processing the file.', 'red');
        }
    } catch (error) {
        console.error('Error:', error);
        updateStatus('Error uploading file.', 'red');
    }
});

// Display results in tables
function displayResults(data) {
    clearTables();
    populateTable(data.transactions, transactionsBody);
    populateTable(data.collections, collectionsBody);
    populateTable(data.bad_transactions, badTransactionsBody);
}

// Populate a table with data
function populateTable(dataArray, tableBody) {
    tableBody.innerHTML = dataArray && dataArray.length > 0 ? 
        dataArray.map(item => `
            <tr>
                <td>${item["Account Name"] || 'N/A'}</td>
                <td>${item["Card Number"] || 'N/A'}</td>
                <td>${item["Transaction Amount"] || 'N/A'}</td>
                <td>${item["Transaction Type"] || 'N/A'}</td>
                <td>${item["Description"] || 'N/A'}</td>
                <td>${item["Target Card Number"] || 'N/A'}</td>
            </tr>`).join('') 
        : '<tr><td colspan="7">This file has no errors.</td></tr>';
}

// Clear all tables
function clearTables() {
    transactionsBody.innerHTML = '';
    collectionsBody.innerHTML = '';
    badTransactionsBody.innerHTML = '';  
}

// Update status message
function updateStatus(message, color) {
    fileStatus.textContent = message;
    fileStatus.style.color = color;
}

// Clear cache
function clearCache() {
    localStorage.clear();
    updateStatus('Cache cleared.', 'green');
}

// Reset database and clear tables
function resetDatabase() {
    fetch('/reset', { method: 'POST' })
        .then(response => response.json())
        .then(data => updateStatus(data.message, 'green'))
        .catch(error => console.error('Error resetting database:', error));
}

// Handle cached file reload on change
fileInput.addEventListener('change', async function() {
    const file = fileInput.files[0];
    if (file) {
        const fileHash = await calculateFileHash(file);
        const cachedData = localStorage.getItem(fileHash);
        if (cachedData) {
            updateStatus('Cached data found for this file.', 'green');
            displayResults(JSON.parse(cachedData));
        }
    }
});
