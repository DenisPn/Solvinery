import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { UploadContext } from '../context/UploadContext';
import './UploadZPLPage.css';
import mockResponse from '../mock/mockResponse.json';

const UploadZPLPage = () => {
    const [file, setFile] = useState(null);
    const { setUploadedFile } = useContext(UploadContext);
    const navigate = useNavigate();

    // Handle file change
    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    // Simulate upload and navigation
    const handleUpload = () => {
        if (!file) {
            alert('Please upload a file!');
            return;
        }

        // Save the uploaded file in global state
        setUploadedFile(file);

        // Simulate a delay for file processing
        setTimeout(() => {
            console.log('Mock JSON Response:', mockResponse);
            navigate('/configure-variables', { state: { data: mockResponse } });
        }, 1000);
    };

    return (
        <div className="upload-zpl-page">
            <h1>Upload ZPL File</h1>
            <div className="upload-container">
                <input type="file" onChange={handleFileChange} className="file-input" />
                <button onClick={handleUpload} className="upload-button">Upload</button>
            </div>
        </div>
    );
};

export default UploadZPLPage;
