import React, { useContext, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { UploadContext } from '../context/UploadContext';
import './ConfigurePreferencesPage.css';

const ConfigurePreferencesPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { uploadedFile } = useContext(UploadContext);

    const data = location.state?.data;

    // State to track the selected preference index
    const [selectedPreferenceIndex, setSelectedPreferenceIndex] = useState(0);

    // Handle radio button selection
    const handlePreferenceSelection = (index) => {
        setSelectedPreferenceIndex(index);
    };

    // Handle Continue button click
    const handleContinue = () => {
        navigate('/solution-preview', { state: { data } });
    };

    return (
        <div className="configure-preferences-page">
            <h1>Configure High-Level Preferences</h1>
            {uploadedFile && <p>Uploaded File: {uploadedFile.name}</p>}

            {data ? (
                <div className="preferences-container">
                    {/* Left Column: Involved Sets and Params */}
                    <div className="left-column">
                        <h2>Involved Sets</h2>
                        <ul>
                            {data.preferencesInvolvedSets[selectedPreferenceIndex]?.map((set, index) => (
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
                            {data.preferencesInvolvedParams[selectedPreferenceIndex]?.map((param, index) => (
                                <li key={index}>
                                    <input type="checkbox" id={`param-${index}`} />
                                    <label htmlFor={`param-${index}`}>
                                        {param} <span className="type-label">(Param Type)</span>
                                    </label>
                                </li>
                            ))}
                        </ul>
                    </div>

                    {/* Right Column: Parsed Preferences */}
                    <div className="right-column">
                        <h2>Parsed Preferences</h2>
                        <ul>
                            {data.preferences.map((preference, index) => (
                                <li key={index}>
                                    <input
                                        type="radio"
                                        name="parsed-preference"
                                        id={`preference-radio-${index}`}
                                        checked={selectedPreferenceIndex === index}
                                        onChange={() => handlePreferenceSelection(index)}
                                    />
                                    <input
                                        type="checkbox"
                                        id={`preference-checkbox-${index}`}
                                    />
                                    <label htmlFor={`preference-radio-${index}`}>{preference}</label>
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

export default ConfigurePreferencesPage;
