import React, { useContext, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom'; // Import useNavigate
import { UploadContext } from '../context/UploadContext';
import './ConfigureVariablesPage.css';

const ConfigureVariablesPage = () => {
    const location = useLocation();
    const navigate = useNavigate(); // Initialize navigate
    const { uploadedFile } = useContext(UploadContext);

    const data = location.state?.data;

    // State to track the selected variable index
    const [selectedVariableIndex, setSelectedVariableIndex] = useState(0);

    // Handle radio button selection
    const handleVariableSelection = (index) => {
        setSelectedVariableIndex(index);
    };

    // Handle Continue button click
    const handleContinue = () => {
        navigate('/configure-constraints', { state: { data } }); // Pass data to the next page
    };

    return (
        <div className="configure-variables-page">
            <h1>Configure Variables of Interest</h1>
            {uploadedFile && <p>Uploaded File: {uploadedFile.name}</p>}

            {data ? (
                <div className="variables-container">
                    {/* Left Column: Involved Sets and Params */}
                    <div className="left-column">
                        <h2>Involved Sets</h2>
                        <ul>
                            {data.variablesInvolvedSets[selectedVariableIndex]?.map((set, index) => {
                                const type = data.types.sets.find((s) => s[set])?.[set] || 'undefined';
                                return (
                                    <li key={index}>
                                        <input type="checkbox" id={`set-${index}`} />
                                        <label htmlFor={`set-${index}`}>
                                            {set} <span className="type-label">({type})</span>
                                        </label>
                                    </li>
                                );
                            })}
                        </ul>
                        <h2>Involved Params</h2>
                        <ul>
                            {data.variablesInvolvedParams[selectedVariableIndex]?.map((param, index) => {
                                const type = data.types.params.find((p) => p[param])?.[param] || 'undefined';
                                return (
                                    <li key={index}>
                                        <input type="checkbox" id={`param-${index}`} />
                                        <label htmlFor={`param-${index}`}>
                                            {param} <span className="type-label">({type})</span>
                                        </label>
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
                                    <input
                                        type="radio"
                                        name="parsed-variable"
                                        id={`variable-radio-${index}`}
                                        checked={selectedVariableIndex === index}
                                        onChange={() => handleVariableSelection(index)}
                                    />
                                    <input
                                        type="checkbox"
                                        id={`variable-checkbox-${index}`}
                                    />
                                    <label htmlFor={`variable-radio-${index}`}>{variable}</label>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            ) : (
                <p>No data received.</p>
            )}

            {/* Continue Button */}
            <button className="continue-button" onClick={handleContinue}>
                Continue
            </button>
        </div>
    );
};

export default ConfigureVariablesPage;
