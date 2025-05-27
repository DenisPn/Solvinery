import React, { useState } from "react";
import { useZPL } from "../context/ZPLContext"; // Import useZPL
import { Link } from "react-router-dom";
import "./SolutionPreviewPage.css"; // Assuming you have your CSS

const SolutionPreviewPage = () => {
    const { selectedVars, setSelectedVars, constraintsModules, preferenceModules, setConstraintsModules, setPreferenceModules } = useZPL(); // Access selectedVars, constraintsModules, and preferenceModules from context
    const [editingVariable, setEditingVariable] = useState(null); // To keep track of the variable being edited
    const [editedAlias, setEditedAlias] = useState("");
    const [editedStructure, setEditedStructure] = useState("");
    const [editingConstraint, setEditingConstraint] = useState(null); // To keep track of the constraint being edited
    const [editingPreference, setEditingPreference] = useState(null); // To keep track of the preference being edited
    const [editedConstraintDescription, setEditedConstraintDescription] = useState("");
    const [editedPreferenceDescription, setEditedPreferenceDescription] = useState("");
    const [activeSection, setActiveSection] = useState("variables"); // Default to "variables" section

    // Handle editing variable data
    const handleEditVariableClick = (variable) => {
        setEditingVariable(variable); // Set the variable being edited
        setEditedAlias(variable.alias || ""); // Pre-fill alias if available
        setEditedStructure(variable.structure || ""); // Pre-fill structure if available
    };

    const handleSaveVariableEdit = () => {
        const updatedVars = selectedVars.map((variable) => {
            if (variable === editingVariable) {
                return {
                    ...variable,
                    alias: editedAlias,
                    structure: editedStructure,
                };
            }
            return variable;
        });
        setSelectedVars(updatedVars); // Save the edited data to context
        setEditingVariable(null); // Close the modal
    };

    const handleCancelVariableEdit = () => {
        setEditingVariable(null); // Close the modal without saving
    };

    const handleDeleteVariable = (variable) => {
        const updatedVars = selectedVars.filter((v) => v !== variable);
        setSelectedVars(updatedVars); // Update the context with the new list
    };

    // Handle editing constraint data
    const handleEditConstraintClick = (constraint) => {
        setEditingConstraint(constraint);
        setEditedConstraintDescription(constraint.description || ""); // Pre-fill description if available
    };

    const handleSaveConstraintEdit = () => {
        const updatedConstraints = constraintsModules.map((module) => {
            if (module.constraints.some(c => c.identifier === editingConstraint.identifier)) {
                return {
                    ...module,
                    constraints: module.constraints.map(c => 
                        c.identifier === editingConstraint.identifier ? 
                        { ...c, description: editedConstraintDescription } : c
                    ),
                };
            }
            return module;
        });
        setConstraintsModules(updatedConstraints); // Save the edited constraint
        setEditingConstraint(null); // Close the modal
    };

    const handleDeleteConstraint = (constraint) => {
        const updatedConstraints = constraintsModules.filter(c => c.identifier !== constraint.identifier);
        setConstraintsModules(updatedConstraints); // Remove the deleted constraint from context
    };

    // Handle editing preference data
    const handleEditPreferenceClick = (preference) => {
        setEditingPreference(preference);
        setEditedPreferenceDescription(preference.description || ""); // Pre-fill description if available
    };

    const handleSavePreferenceEdit = () => {
        const updatedPreferences = preferenceModules.map((module) => {
            if (module.preferences.some(p => p.identifier === editingPreference.identifier)) {
                return {
                    ...module,
                    preferences: module.preferences.map(p =>
                        p.identifier === editingPreference.identifier ? 
                        { ...p, description: editedPreferenceDescription } : p
                    ),
                };
            }
            return module;
        });
        setPreferenceModules(updatedPreferences); // Save the edited preference
        setEditingPreference(null); // Close the modal
    };

    const handleDeletePreference = (preference) => {
        const updatedPreferences = preferenceModules.filter(p => p.identifier !== preference.identifier);
        setPreferenceModules(updatedPreferences); // Remove the deleted preference from context
    };

    const handleToggleSection = (section) => {
        setActiveSection(section); // Switch between "variables", "constraints", "preferences"
    };

    return (
        <div className="solution-preview-page background">
            <h1 className="page-title">Solution Preview</h1>

            {/* Button to Toggle Sections */}
            <div className="toggle-section">
                <button onClick={() => handleToggleSection("variables")} className="toggle-button">
                    Show Variables
                </button>
                <button onClick={() => handleToggleSection("constraints")} className="toggle-button">
                    Show Constraints
                </button>
                <button onClick={() => handleToggleSection("preferences")} className="toggle-button">
                    Show Preferences
                </button>
            </div>

            {/* Conditional Rendering of Sections */}
            {activeSection === "variables" && (
                <div className="variables-section">
                    <h2 className="section-title">Variables</h2>

                    <div className="slider-container">
                        <div className="slider">
                            {selectedVars.length > 0 ? (
                                selectedVars.map((variable, index) => (
                                    <div key={index} className="slide">
                                        <div className="variable-details">
                                            <h4>Variable's name</h4>
                                            <p>{variable.identifier}</p>
                                            <br />
                                            <h4>Alias</h4>
                                            <input type="text" id={`alias-${index}`} className="variable-input" value={variable.alias || ""} readOnly />
                                            <h4>Structure</h4>
                                            <input type="text" id={`structure-${index}`} className="variable-input" value={variable.structure || ""} readOnly />
                                        </div>

                                        {/* Edit/Delete buttons */}
                                        <div className="buttons-container">
                                            <img src="/images/edit-button.png" alt="Edit" className="edit-image" onClick={() => handleEditVariableClick(variable)} />
                                            <img src="/images/delete.png" alt="Delete" className="delete-image" onClick={() => handleDeleteVariable(variable)} />
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <p>No variables selected yet.</p>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {activeSection === "constraints" && (
                <div className="constraints-section">
                    <h2 className="section-title">Constraints</h2>

                    <div className="slider-container">
                        <div className="slider">
                            {constraintsModules.length > 0 ? (
                                constraintsModules.map((module, index) => (
                                    <div key={index} className="slide">
                                        <div className="variable-details">
                                            <h4>Module Name</h4>
                                            <p>{module.name}</p>
                                            <h4>Description</h4>
                                            <input type="text" value={module.description} readOnly className="variable-input" />
                                        </div>

                                        {/* Edit/Delete buttons */}
                                        <div className="buttons-container">
                                            <img src="/images/edit-button.png" alt="Edit" className="edit-image" onClick={() => handleEditConstraintClick(module)} />
                                            <img src="/images/delete.png" alt="Delete" className="delete-image" onClick={() => handleDeleteConstraint(module)} />
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <p>No constraints modules selected yet.</p>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {activeSection === "preferences" && (
                <div className="preferences-section">
                    <h2 className="section-title">Preferences</h2>

                    <div className="slider-container">
                        <div className="slider">
                            {preferenceModules.length > 0 ? (
                                preferenceModules.map((module, index) => (
                                    <div key={index} className="slide">
                                        <div className="variable-details">
                                            <h4>Module Name</h4>
                                            <p>{module.name}</p>
                                            <h4>Description</h4>
                                            <input type="text" value={module.description} readOnly className="variable-input" />
                                        </div>

                                        {/* Edit/Delete buttons */}
                                        <div className="buttons-container">
                                            <img src="/images/edit-button.png" alt="Edit" className="edit-image" onClick={() => handleEditPreferenceClick(module)} />
                                            <img src="/images/delete.png" alt="Delete" className="delete-image" onClick={() => handleDeletePreference(module)} />
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <p>No preference modules selected yet.</p>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* Modal for Editing Variable */}
            {editingVariable && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Edit Variable</h2>
                        <div className="modal-input-group">
                            <label>Alias</label>
                            <input type="text" value={editedAlias} onChange={(e) => setEditedAlias(e.target.value)} className="variable-input" />
                        </div>
                        <div className="modal-input-group">
                            <label>Structure</label>
                            <input type="text" value={editedStructure} onChange={(e) => setEditedStructure(e.target.value)} className="variable-input" />
                        </div>
                        <div className="modal-buttons">
                            <button className="save-button" onClick={handleSaveVariableEdit}>Save</button>
                            <button className="cancel-button" onClick={handleCancelVariableEdit}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal for Editing Constraint */}
            {editingConstraint && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Edit Constraint</h2>
                        <div className="modal-input-group">
                            <label>Description</label>
                            <input type="text" value={editedConstraintDescription} onChange={(e) => setEditedConstraintDescription(e.target.value)} className="variable-input" />
                        </div>
                        <div className="modal-buttons">
                            <button className="save-button" onClick={handleSaveConstraintEdit}>Save</button>
                            <button className="cancel-button" onClick={() => setEditingConstraint(null)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal for Editing Preference */}
            {editingPreference && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Edit Preference</h2>
                        <div className="modal-input-group">
                            <label>Description</label>
                            <input type="text" value={editedPreferenceDescription} onChange={(e) => setEditedPreferenceDescription(e.target.value)} className="variable-input" />
                        </div>
                        <div className="modal-buttons">
                            <button className="save-button" onClick={handleSavePreferenceEdit}>Save</button>
                            <button className="cancel-button" onClick={() => setEditingPreference(null)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Save Image Button */}
            <button
                className="save-image-button"
                onClick={() => window.location.href = '/'} // Navigate to home
            >
                Save Image
            </button>

            {/* Back Button */}
            <Link to="/configure-variables" className="back-button">Back</Link>
        </div>
    );
};

export default SolutionPreviewPage;
