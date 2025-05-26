import React, { useState } from "react";
import { useZPL } from "../context/ZPLContext"; // Import useZPL
import { Link } from "react-router-dom";
import "./SolutionPreviewPage.css"; // Assuming you have your CSS

const SolutionPreviewPage = () => {
    const { selectedVars, setSelectedVars, constraintsModules } = useZPL(); // Access selectedVars and constraintsModules from context
    const [editingVariable, setEditingVariable] = useState(null); // To keep track of the variable being edited
    const [editedAlias, setEditedAlias] = useState("");
    const [editedStructure, setEditedStructure] = useState("");

    const [isVariablesSectionVisible, setIsVariablesSectionVisible] = useState(true); // State to toggle visibility between sections

    const handleEditClick = (variable) => {
        setEditingVariable(variable); // Set the variable being edited
        setEditedAlias(variable.alias || ""); // Pre-fill alias if available
        setEditedStructure(variable.structure || ""); // Pre-fill structure if available
    };

    const handleSaveEdit = () => {
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

    const handleCancelEdit = () => {
        setEditingVariable(null); // Close the modal without saving
    };

    const handleDelete = (variable) => {
        // Filter out the variable to delete
        const updatedVars = selectedVars.filter((v) => v !== variable);
        setSelectedVars(updatedVars); // Update the context with the new list
    };

    const toggleSectionVisibility = () => {
        setIsVariablesSectionVisible(!isVariablesSectionVisible); // Toggle the visibility state
    };

    return (
        <div className="solution-preview-page background">
            <h1 className="page-title">Solution Preview</h1>

            {/* Toggle button to switch between sections */}
            <button onClick={toggleSectionVisibility}>
                {isVariablesSectionVisible ? "Show Constraints Section" : "Show Variables Section"}
            </button>

            {/* Conditionally render the Variables Section or Constraints Section */}
            {isVariablesSectionVisible ? (
                <div className="variables-section">
                    <h2 className="section-title">Selected Variables</h2>

                    {/* Slider container for the variable boxes */}
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
                                            <input
                                                type="text"
                                                id={`alias-${index}`}
                                                className="variable-input"
                                                value={variable.alias || ""}
                                                readOnly
                                            />
                                            <h4>Structure</h4>
                                            <input
                                                type="text"
                                                id={`structure-${index}`}
                                                className="variable-input"
                                                value={variable.structure || ""}
                                                readOnly
                                            />
                                            <br />
                                            <br />
                                        </div>

                                        {/* Buttons container at the bottom of each variable box */}
                                        <div className="buttons-container">
                                            {/* Edit button with pencil image */}
                                            <img
                                                src="/images/edit-button.png" // Path to the pencil image in the public folder
                                                alt="Edit"
                                                className="edit-image"
                                                onClick={() => handleEditClick(variable)} // Trigger edit modal
                                            />

                                            {/* Delete button with delete image */}
                                            <img
                                                src="/images/delete.png" // Path to the delete image in the public folder
                                                alt="Delete"
                                                className="delete-image"
                                                onClick={() => handleDelete(variable)} // Trigger delete action
                                            />
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <p>No variables selected yet.</p>
                            )}
                        </div>
                    </div>
                </div>
            ) : (
                <div className="constraints-section">
                    <h2 className="section-title">Selected Constraint Modules</h2>

                    {/* Slider container for the constraint boxes */}
                    <div className="slider-container">
                        <div className="slider">
                            {constraintsModules.length > 0 ? (
                                constraintsModules.map((module, index) => (
                                    <div key={index} className="slide">
                                        <div className="constraint-details">
                                            <h4>Constraint Module Name</h4>
                                            <p>{module.name}</p>
                                            <br />
                                            <h4>Description</h4>
                                            <p>{module.description || "No description"}</p>
                                            <br />
                                        </div>

                                        {/* Buttons container at the bottom of each constraint module box */}
                                        <div className="buttons-container">
                                            {/* Edit button with pencil image */}
                                            <img
                                                src="/images/edit-button.png" // Path to the pencil image in the public folder
                                                alt="Edit"
                                                className="edit-image"
                                            />

                                            {/* Delete button with delete image */}
                                            <img
                                                src="/images/delete.png" // Path to the delete image in the public folder
                                                alt="Delete"
                                                className="delete-image"
                                            />
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <p>No constraint modules selected yet.</p>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* Modal for Editing */}
            {editingVariable && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Edit Variable</h2>

                        <div className="modal-input-group">
                            <label>Alias</label>
                            <br />
                            <br />
                            <input
                                type="text"
                                value={editedAlias}
                                onChange={(e) => setEditedAlias(e.target.value)}
                                className="variable-input"
                            />
                        </div>

                        <div className="modal-input-group">
                            <label>Structure</label>
                            <br />
                            <br />
                            <input
                                type="text"
                                value={editedStructure}
                                onChange={(e) => setEditedStructure(e.target.value)}
                                className="variable-input"
                            />
                        </div>

                        <div className="modal-buttons">
                            <button className="save-button" onClick={handleSaveEdit}>
                                Save
                            </button>
                            <button className="cancel-button" onClick={handleCancelEdit}>
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Back Button */}
            <Link to="/configure-variables" className="back-button">
                Back
            </Link>
        </div>
    );
};

export default SolutionPreviewPage;
