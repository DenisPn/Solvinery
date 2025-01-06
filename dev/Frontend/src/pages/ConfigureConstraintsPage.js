import React, { useContext, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { UploadContext } from '../context/UploadContext';
import './ConfigureConstraintsPage.css';

const ConfigureConstraintsPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { uploadedFile } = useContext(UploadContext);

    const data = location.state?.data;

    // State to track the selected constraint index
    const [selectedConstraintIndex, setSelectedConstraintIndex] = useState(0);

    // Handle radio button selection
    const handleConstraintSelection = (index) => {
        setSelectedConstraintIndex(index);
    };

    // Handle Continue button click
    const handleContinue = () => {
        navigate('/configure-preferences', { state: { data } });
    };

    return (
        <div className="configure-constraints-page">
            <h1>Configure High-Level Constraints</h1>
            {uploadedFile && <p>Uploaded File: {uploadedFile.name}</p>}

            {data ? (
                <div className="constraints-container">
                    {/* Left Column: Involved Sets and Params */}
                    <div className="left-column">
                        <h2>Involved Sets</h2>
                        <ul>
                            {data.constraintsInvolvedSets[selectedConstraintIndex]?.map((set, index) => (
                                <li key={index}>
                                    <input type="checkbox" id={`set-${index}`} />
                                    <label htmlFor={`set-${index}`}>
                                        {set} <span className="type-label">(Set Type)</span>
                                    </label>
                                </li>
                            ))}
                        </ul>
                        <h2>Involved Params</h2>
                        <ul>
                            {data.constraintsInvolvedParams[selectedConstraintIndex]?.map((param, index) => (
                                <li key={index}>
                                    <input type="checkbox" id={`param-${index}`} />
                                    <label htmlFor={`param-${index}`}>
                                        {param} <span className="type-label">(Param Type)</span>
                                    </label>
                                </li>
                            ))}
                        </ul>
                    </div>

                    {/* Right Column: Parsed Constraints */}
                    <div className="right-column">
                        <h2>Parsed Constraints</h2>
                        <ul>
                            {data.constraints.map((constraint, index) => (
                                <li key={index}>
                                    <input
                                        type="radio"
                                        name="parsed-constraint"
                                        id={`constraint-radio-${index}`}
                                        checked={selectedConstraintIndex === index}
                                        onChange={() => handleConstraintSelection(index)}
                                    />
                                    <input
                                        type="checkbox"
                                        id={`constraint-checkbox-${index}`}
                                    />
                                    <label htmlFor={`constraint-radio-${index}`}>{constraint}</label>
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

export default ConfigureConstraintsPage;
