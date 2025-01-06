import React, { useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { UploadContext } from '../context/UploadContext';
import './ConfigureVariablesPage.css';

const ConfigureVariablesPage = () => {
    const location = useLocation();
    const { uploadedFile } = useContext(UploadContext);

    const data = location.state?.data;

    return (
        <div className="configure-variables-page">
            <h1>Configure Variables of Interest</h1>
            {uploadedFile && <p>Uploaded File: {uploadedFile.name}</p>}

            {data ? (
                <div className="variables-container">
                    {/* Left Column: Sets and Params */}
                    <div className="left-column">
                        <h2>Involved Sets</h2>
                        <ul>
                            {data.types.sets.map((set, index) => {
                                const [setName, setType] = Object.entries(set)[0];
                                return (
                                    <li key={index}>
                                        <strong>{setName}</strong>: {setType}
                                    </li>
                                );
                            })}
                        </ul>
                        <h2>Involved Params</h2>
                        <ul>
                            {data.types.params.map((param, index) => {
                                const [paramName, paramType] = Object.entries(param)[0];
                                return (
                                    <li key={index}>
                                        <strong>{paramName}</strong>: {paramType}
                                    </li>
                                );
                            })}
                        </ul>
                    </div>

                    {/* Right Column: Parsed Variables */}
                    <div className="right-column">
                        <h2>Parsed Variables</h2>
                        <ul>
                            {data.variables.map((variable, index) => (
                                <li key={index}>
                                    <input type="checkbox" id={`variable-${index}`} />
                                    <label htmlFor={`variable-${index}`}>{variable}</label>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            ) : (
                <p>No data received.</p>
            )}
        </div>
    );
};

export default ConfigureVariablesPage;
