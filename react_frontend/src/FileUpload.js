// src/FileUpload.js
import React, { useState } from 'react';
import axios from 'axios';
import './FileUpload.css';


const FileUpload = () => {
    const [file, setFile] = useState(null);
    const [uploadStatus, setUploadStatus] = useState('');
    const [validTransactions, setValidTransactions] = useState([]);
    const [invalidRecords, setInvalidRecords] = useState([]);
    const [loading, setLoading] = useState(false);


    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleUpload = async () => {
        if (!file) {
            setUploadStatus('Please select a file to upload.');
            return;
        }
        setLoading(true);
        setUploadStatus('Processing file...');


        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await axios.post('http://localhost:8000/api/upload/', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            setLoading(false);

            setUploadStatus(response.data.message);
            setValidTransactions(response.data.valid_transactions);
            setInvalidRecords(response.data.invalid_records);
        } catch (error) {
            setUploadStatus('Error uploading the file.');
        }
    };

    return (
        <div className="file-upload">
            <label className="custom-file-upload">
                <input type="file" onChange={handleFileChange}/>
            <button onClick={handleUpload}>Upload File</button>
            </label>


            <p>{uploadStatus}</p>
            {/* Display Invalid Records */}
            {invalidRecords.length > 0 && (
                <div>
                    <h3>Invalid Records</h3>
                    <table>
                        <thead>
                        <tr>
                            <th>Record</th>
                            <th>Errors</th>
                        </tr>
                        </thead>
                        <tbody>
                        {invalidRecords.map((record, index) => (
                            <tr key={index}>
                                <td>{record.record.join(', ')}</td>
                                <td>
                                    {Object.keys(record.errors).map((errorKey, i) => (
                                        <div key={i}>
                                            {errorKey}: {record.errors[errorKey].join(', ')}
                                        </div>
                                    ))}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

        </div>
    );
};

export default FileUpload;
