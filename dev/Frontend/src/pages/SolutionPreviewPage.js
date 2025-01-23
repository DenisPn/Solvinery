import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import './SolutionPreviewPage.css';

const SolutionPreviewPage = () => {
    const { constraints, preferences, variables } = useZPL();

    return (
        <div className="solution-preview-page">
            <h1 className="page-title">Solution Preview</h1>

            {/* Constraints and Preferences Section */}
            <div className="constraints-preferences-section">
                <h2>Constraints</h2>
                {constraints && constraints.length > 0 ? (
                    constraints.map((constraint, index) => (
                        <div key={index} className="constraint-item">
                            <h3>{constraint.identifier}</h3>
                            <p>Set Dependencies: {constraint.dep?.setDependencies?.join(', ') || 'None'}</p>
                            <p>Param Dependencies: {constraint.dep?.paramDependencies?.join(', ') || 'None'}</p>
                        </div>
                    ))
                ) : (
                    <p>No constraints available</p>
                )}

                <h2>Preferences</h2>
                {preferences && preferences.length > 0 ? (
                    preferences.map((preference, index) => (
                        <div key={index} className="preference-item">
                            <h3>{preference.identifier}</h3>
                            <p>Set Dependencies: {preference.dep?.setDependencies?.join(', ') || 'None'}</p>
                            <p>Param Dependencies: {preference.dep?.paramDependencies?.join(', ') || 'None'}</p>
                        </div>
                    ))
                ) : (
                    <p>No preferences available</p>
                )}
            </div>

            {/* Variables Section */}
            <div className="variables-section">
                <h2>Variables</h2>
                {variables && Object.entries(variables).length > 0 ? (
                    Object.entries(variables).map(([key, value]) => (
                        <div key={key} className="variable-item">
                            <h3>{key}</h3>
                            <ul>
                                {value.map((item, index) => (
                                    <li key={index}>{item}</li>
                                ))}
                            </ul>
                            <button className="add-button">+</button>
                        </div>
                    ))
                ) : (
                    <p>No variables available</p>
                )}
            </div>

            {/* Navigation Buttons */}
            <Link to="/" className="back-button">Back</Link>
        </div>
    );
};

export default SolutionPreviewPage;
