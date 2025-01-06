import React, { useContext } from 'react';
import { UploadContext } from '../context/UploadContext';
import './SolutionPreviewPage.css';

const SolutionPreviewPage = () => {
    const { checkboxValues } = useContext(UploadContext);

    const { variables, constraints, preferences } = checkboxValues;

    return (
        <div className="solution-preview-page">
            <h1>Solution Preview</h1>

            <div className="preview-section">
                <h2>Selected Variables</h2>
                <ul>
                    {Object.keys(variables).filter((key) => variables[key]).map((variable, index) => (
                        <li key={index}>{variable}</li>
                    ))}
                </ul>
            </div>

            <div className="preview-section">
                <h2>Selected Constraints</h2>
                <ul>
                    {Object.keys(constraints).filter((key) => constraints[key]).map((constraint, index) => (
                        <li key={index}>{constraint}</li>
                    ))}
                </ul>
            </div>

            <div className="preview-section">
                <h2>Selected Preferences</h2>
                <ul>
                    {Object.keys(preferences).filter((key) => preferences[key]).map((preference, index) => (
                        <li key={index}>{preference}</li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default SolutionPreviewPage;
