import React, { useEffect, useState } from 'react';
import axios from 'axios';

const Reports = ({ type }) => {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let url = '';
        switch (type) {
            case 'accounts':
                url = 'http://localhost:8000/api/report/accounts/';
                break;
            case 'collections':
                url = 'http://localhost:8000/api/report/collections/';
                break;
            case 'bad-transactions':
                url = 'http://localhost:8000/api/bad-transactions/';  // API endpoint for bad transactions
                break;
            default:
                return;
        }
        axios.get(url).then((response) => {
            setData(response.data);
            setLoading(false);
        }).catch((error) => {
            console.error("There was an error fetching the data!", error);
        });
    }, [type]);

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div>
            <h2>{type.replace('-', ' ')}</h2>
            <table>
                <thead>
                <tr>
                    {Object.keys(data[0] || {}).map((key) => (
                        <th key={key}>{key}</th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {data.map((item, index) => (
                    <tr key={index}>
                        {Object.values(item).map((value, i) => (
                            <td key={i}>{value}</td>
                        ))}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default Reports;
