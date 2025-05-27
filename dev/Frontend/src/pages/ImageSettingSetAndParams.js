import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import "./ImageSettingSetAndParams.css"; // Assuming you have your CSS file for this page

const ImageSettingSetAndParams = () => {
    const { setTypes, paramTypes } = useZPL(); // Accessing data from the context
    const navigate = useNavigate();

    const [activeSection, setActiveSection] = useState("sets"); // Tracks the active section (sets or params)
    const [editingSet, setEditingSet] = useState(null); // Tracks the set being edited
    const [editedSetData, setEditedSetData] = useState(""); // Stores the edited data for a set
    const [editingParam, setEditingParam] = useState(null); // Tracks the parameter being edited
    const [editedParamData, setEditedParamData] = useState(""); // Stores the edited data for a parameter

    // Set Click Handler
    const handleEditSetClick = (setName) => {
        setEditingSet(setName); // Set the set being edited
        setEditedSetData(setTypes[setName] || ""); // Pre-fill the data of the set if available
    };

    // Set Delete Handler
    const handleDeleteSet = (setName) => {
        const updatedSets = { ...setTypes };
        delete updatedSets[setName];
        // Assuming setTypes is updated here
    };

    // Save Set Edit Handler
    const handleSaveSetEdit = () => {
        const updatedSets = { ...setTypes };
        updatedSets[editingSet] = editedSetData; // Update the set with the edited data
        // Update the context with the modified setTypes
        setEditingSet(null);
    };

    // Param Click Handler
    const handleEditParamClick = (paramName) => {
        setEditingParam(paramName); // Set the parameter being edited
        setEditedParamData(paramTypes[paramName] || ""); // Pre-fill the data of the param if available
    };

    // Param Delete Handler
    const handleDeleteParam = (paramName) => {
        const updatedParams = { ...paramTypes };
        delete updatedParams[paramName];
        // Assuming paramTypes is updated here
    };

    // Save Param Edit Handler
    const handleSaveParamEdit = () => {
        const updatedParams = { ...paramTypes };
        updatedParams[editingParam] = editedParamData; // Update the parameter with the edited data
        // Update the context with the modified paramTypes
        setEditingParam(null);
    };

    return (
        <div className="image-setting-page background">
            {/* Top Buttons */}
            <div className="top-buttons">
                {/* Left Buttons (Quit and Continue) */}
                <div className="left-buttons">
                    <button 
                        className="quit-button" 
                        onClick={() => navigate("/")} // Quit button to navigate to home
                    >
                        Quit
                    </button>
                    <button 
                        className="continue-button" 
                        onClick={() => navigate("/")} // Continue button, to be updated later
                    >
                        Continue
                    </button>
                </div>
                {/* Right Buttons (Back) */}
                <div className="right-buttons">
                    <button 
                        className="back-button" 
                        onClick={() => navigate("/solution-preview")} // Back button to navigate to SPP
                    >
                        Back
                    </button>
                </div>
            </div>

            <h1 className="page-title">Image Settings - Sets and Parameters</h1>

            <div className="toggle-section">
                <button onClick={() => setActiveSection("sets")} className="toggle-button">Show Sets</button>
                <button onClick={() => setActiveSection("params")} className="toggle-button">Show Parameters</button>
            </div>

            {/* Sets Section */}
            {activeSection === "sets" && (
                <div className="sets-section">
                    <h2 className="section-title">Sets</h2>
                    <div className="slider-container">
                        <div className="slider">
                            {Object.entries(setTypes).map(([setName, setData], index) => (
                                <div key={index} className="slide">
                                    <div className="variable-details">
                                        <h4>{setName}</h4>
                                        <input type="text" value={setData} className="variable-input" readOnly />
                                    </div>
                                    <div className="buttons-container">
                                        <img
                                            src="/images/edit-button.png"
                                            alt="Edit"
                                            className="edit-image"
                                            onClick={() => handleEditSetClick(setName)}
                                        />
                                        <img
                                            src="/images/delete.png"
                                            alt="Delete"
                                            className="delete-image"
                                            onClick={() => handleDeleteSet(setName)}
                                        />
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* Parameters Section */}
            {activeSection === "params" && (
                <div className="parameters-section">
                    <h2 className="section-title">Parameters</h2>
                    <div className="slider-container">
                        <div className="slider">
                            {Object.entries(paramTypes).map(([paramName, paramData], index) => (
                                <div key={index} className="slide">
                                    <div className="variable-details">
                                        <h4>{paramName}</h4>
                                        <input type="text" value={paramData} className="variable-input" readOnly />
                                    </div>
                                    <div className="buttons-container">
                                        <img
                                            src="/images/edit-button.png"
                                            alt="Edit"
                                            className="edit-image"
                                            onClick={() => handleEditParamClick(paramName)}
                                        />
                                        <img
                                            src="/images/delete.png"
                                            alt="Delete"
                                            className="delete-image"
                                            onClick={() => handleDeleteParam(paramName)}
                                        />
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* Modals for Editing */}
            {editingSet && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Edit Set</h2>
                        <input
                            type="text"
                            value={editedSetData}
                            onChange={(e) => setEditedSetData(e.target.value)}
                            className="variable-input"
                        />
                        <div className="modal-buttons">
                            <button onClick={handleSaveSetEdit}>Save</button>
                            <button onClick={() => setEditingSet(null)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {editingParam && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Edit Parameter</h2>
                        <input
                            type="text"
                            value={editedParamData}
                            onChange={(e) => setEditedParamData(e.target.value)}
                            className="variable-input"
                        />
                        <div className="modal-buttons">
                            <button onClick={handleSaveParamEdit}>Save</button>
                            <button onClick={() => setEditingParam(null)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ImageSettingSetAndParams;
