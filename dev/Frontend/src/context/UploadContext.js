import React, { createContext, useState } from 'react';

// Create the context
export const UploadContext = createContext();

// Create a provider component
export const UploadProvider = ({ children }) => {
    const [uploadedFile, setUploadedFile] = useState(null);
    const [checkboxValues, setCheckboxValues] = useState({
        variables: {},
        constraints: {},
        preferences: {}
    });

    return (
        <UploadContext.Provider value={{ uploadedFile, setUploadedFile, checkboxValues, setCheckboxValues }}>
            {children}
        </UploadContext.Provider>
    );
};
