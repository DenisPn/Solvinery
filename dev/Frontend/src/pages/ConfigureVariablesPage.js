import React, { useContext, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { UploadContext } from '../context/UploadContext';
import './ConfigureVariablesPage.css';

const ConfigureVariablesPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { uploadedFile, checkboxValues, setCheckboxValues } = useContext(UploadContext);

    const data = location.state?.data;

    const [selectedVariableIndex, setSelectedVariableIndex] = useState(0);

    const handleVariableSelection = (index) => {
        setSelectedVariableIndex(index);
    };

    const handleCheckboxChange = (variable) => {
        setCheckboxValues((prevValues) => ({
            ...prevValues,
            variables: {
                ...prevValues.variables,
                [variable]: !prevValues.variables[variable]
            }
        }));
    };

    const handleContinue = () => {
        navigate('/configure-constraints', { state: { data } });
    };

    return (
        <div className="configure-variables-page">
            <h1>Configure Variables of Interest</h1>
            {uploadedFile && <p>Uploaded File: {uploadedFile.name}</p>}

            {data ? (
                <div className="variables-container">
                    <div className="left-column">
                        <h2>Involved Sets</h2>
                        <ul>
                            {data.variablesInvolvedSets[selectedVariableIndex]?.map((set, index) => (
                                <li key={index}>
                                    <input type="checkbox" id={`set-${index}`} />
                                    <label htmlFor={`set-${index}`}>{set}</label>
                                </li>
                            ))}
                        </ul>
                        <h2>Involved Params</h2>
                        <ul>
                            {data.variablesInvolvedParams[selectedVariableIndex]?.map((param, index) => (
                                <li key={index}>
                                    <input type="checkbox" id={`param-${index}`} />
                                    <label htmlFor={`param-${index}`}>{param}</label>
                                </li>
                            ))}
                        </ul>
                    </div>

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
                                        checked={checkboxValues.variables[variable] || false}
                                        onChange={() => handleCheckboxChange(variable)}
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

            <button className="continue-button" onClick={handleContinue}>
                Continue
            </button>
        </div>
    );
};

export default ConfigureVariablesPage;
