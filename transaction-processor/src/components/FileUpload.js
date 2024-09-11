import React from 'react';
import Papa from 'papaparse';

function FileUpload({ onUpload, fileInputRef }) {  
    const handleFileChange = (e) => {
        const file = e.target.files[0];

        if (file) {
            Papa.parse(file, {
                header: false, // No headers in the file
                skipEmptyLines: true,
                complete: (results) => {
                    const data = results.data.map((row) => ({
                        AccountName: row[0],          // First column: Account Name
                        CardNumber: row[1],           // Second column: Card Number
                        TransactionAmount: row[2],    // Third column: Transaction Amount
                        TransactionType: row[3],      // Fourth column: Transaction Type
                        Description: row[4],          // Fifth column: Description
                        TargetCardNumber: row[5],     // Sixth column: Target Card Number (for Transfer)
                    }));
                    onUpload(data);  // Send the parsed data to the parent
                },
                error: (error) => {
                    console.error("Error while parsing:", error);
                }
            });
        }
    };

    return (
        <div>
            <label htmlFor="fileInput">Upload Transaction File:</label>
            <input
                type="file"
                id="fileInput"
                accept=".csv"
                onChange={handleFileChange}
                ref={fileInputRef}  
            />
        </div>
    );
}

export default FileUpload;
