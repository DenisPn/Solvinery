import React, { useState } from "react";
import { useZPL } from "../context/ZPLContext";
import { useNavigate, Link } from "react-router-dom";
import "./SolutionPreviewPage.css";

const SolutionPreviewPage = () => {
  const navigate = useNavigate();
  const {
    selectedVars,
    constraintsModules,
    preferenceModules,
    setVariables,
    setSelectedVars,
    setVariablesModule,
    setConstraints,
    setConstraintsModules,
    setPreferences,
    setPreferenceModules,
    setSetTypes,
    setSetAliases,
    setParamTypes,
    setImageId,
    setImageName,
    setImageDescription,
    setZplCode,
    setIsEditMode,
  } = useZPL();

  // variable‐editing state
  const [editingVariable, setEditingVariable] = useState(null);
  const [editedAlias, setEditedAlias] = useState("");
  const [editedStructure, setEditedStructure] = useState("");
  const [editedObjectiveValueAlias, setEditedObjectiveValueAlias] = useState("");

  // constraint & preference state (unchanged)
  const [editingConstraint, setEditingConstraint] = useState(null);
  const [editedConstraintDescription, setEditedConstraintDescription] = useState("");
  const [editingPreference, setEditingPreference] = useState(null);
  const [editedPreferenceDescription, setEditedPreferenceDescription] = useState("");

  const [activeSection, setActiveSection] = useState("variables");

  // Open variable modal
  const handleEditVariableClick = (variable) => {
    setEditingVariable(variable);
    setEditedAlias(variable.alias || "");
    setEditedStructure(variable.structure || "");
    setEditedObjectiveValueAlias(variable.objectiveValueAlias || "");
  };

  // Save variable edits
  const handleSaveVariableEdit = () => {
    const updated = selectedVars.map((v) =>
      v === editingVariable
        ? {
            ...v,
            alias: editedAlias,
            structure: editedStructure,
            objectiveValueAlias: editedObjectiveValueAlias,
          }
        : v
    );
    setSelectedVars(updated);
    setEditingVariable(null);
  };

  const handleCancelVariableEdit = () => {
    setEditingVariable(null);
  };

  const handleDeleteVariable = (variable) => {
    setSelectedVars(selectedVars.filter((v) => v !== variable));
  };

  // Constraints editing (unchanged)
  const handleEditConstraintClick = (constraint) => {
    setEditingConstraint(constraint);
    setEditedConstraintDescription(constraint.description || "");
  };
  const handleSaveConstraintEdit = () => {
    setConstraintsModules(constraintsModules.map((mod) =>
      mod === editingConstraint
        ? { ...mod, description: editedConstraintDescription }
        : mod
    ));
    setEditingConstraint(null);
  };
  const handleDeleteConstraint = (moduleToDelete) => {
    setConstraintsModules(constraintsModules.filter((mod) => mod !== moduleToDelete));
  };

  // Preferences editing (unchanged)
  const handleEditPreferenceClick = (preference) => {
    setEditingPreference(preference);
    setEditedPreferenceDescription(preference.description || "");
  };
  const handleSavePreferenceEdit = () => {
    setPreferenceModules(preferenceModules.map((mod) =>
      mod === editingPreference
        ? { ...mod, description: editedPreferenceDescription }
        : mod
    ));
    setEditingPreference(null);
  };
  const handleDeletePreference = (moduleToDelete) => {
    setPreferenceModules(preferenceModules.filter((mod) => mod !== moduleToDelete));
  };

  const handleToggleSection = (section) => setActiveSection(section);

  const handleHomeClick = () => {
    setVariables([]); setSelectedVars([]); setVariablesModule({
      variablesOfInterest: [], variablesConfigurableSets: [], variablesConfigurableParams: [],
    });
    setConstraints([]); setConstraintsModules([]); setPreferences([]); setPreferenceModules([]);
    setSetTypes({}); setSetAliases({}); setParamTypes({});
    setImageId(null); setImageName(""); setImageDescription(""); setZplCode(""); setIsEditMode(false);
    navigate("/main-page");
  };

  return (
    <div className="solution-preview-page background">
      <img
        src="/images/HomeButton.png"
        alt="Home"
        className="solution-home-button"
        onClick={handleHomeClick}
        title="Go to Home"
      />

      {/* Section toggles */}
      <div className="toggle-section">
        <button onClick={() => handleToggleSection("variables")} className="fancy-button">
          Show Variables
        </button>
        <button onClick={() => handleToggleSection("constraints")} className="fancy-button">
          Show Constraints
        </button>
        <button onClick={() => handleToggleSection("preferences")} className="fancy-button">
          Show Preferences
        </button>
      </div>

      {/* Variables */}
      {activeSection === "variables" && (
        <div className="variables-section">
          <h2 className="section-title">Variables</h2>
          <div className="slider-container">
            <div className="slider">
              {selectedVars.length ? selectedVars.map((variable, idx) => (
                <div key={idx} className="slide">
                  <div className="variable-details">
                    <h4>Variable's name</h4>
                    <p>{variable.identifier}</p>

                    <h4>Alias</h4>
                    <input
                      type="text"
                      className="variable-input"
                      value={variable.alias || ""}
                      readOnly
                    />

                    <h4>Structure</h4>
                    <input
                      type="text"
                      className="variable-input"
                      value={variable.structure || ""}
                      readOnly
                    />

                    <h4>Objective Value Alias</h4>
                    <input
                      type="text"
                      className="variable-input"
                      value={variable.objectiveValueAlias || ""}
                      readOnly
                    />
                  </div>

                  <div className="buttons-container">
                    <img
                      src="/images/edit-button.png"
                      alt="Edit"
                      className="edit-image"
                      onClick={() => handleEditVariableClick(variable)}
                    />
                    <img
                      src="/images/delete.png"
                      alt="Delete"
                      className="delete-image"
                      onClick={() => handleDeleteVariable(variable)}
                    />
                  </div>
                </div>
              )) : (
                <p>No variables selected yet.</p>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Constraints */}
      {activeSection === "constraints" && (
        <div className="constraints-section">
          <h2 className="section-title">Constraints</h2>
          <div className="slider-container">
            <div className="slider">
              {constraintsModules.length ? constraintsModules.map((module, idx) => (
                <div key={idx} className="slide">
                  <div className="variable-details">
                    <h4>Module Name</h4>
                    <p>{module.name}</p>
                    <h4>Description</h4>
                    <input
                      type="text"
                      className="variable-input"
                      value={module.description}
                      readOnly
                    />
                  </div>
                  <div className="buttons-container">
                    <img
                      src="/images/edit-button.png"
                      alt="Edit"
                      className="edit-image"
                      onClick={() => handleEditConstraintClick(module)}
                    />
                    <img
                      src="/images/delete.png"
                      alt="Delete"
                      className="delete-image"
                      onClick={() => handleDeleteConstraint(module)}
                    />
                  </div>
                </div>
              )) : (
                <p>No constraints modules selected yet.</p>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Preferences */}
      {activeSection === "preferences" && (
        <div className="preferences-section">
          <h2 className="section-title">Preferences</h2>
          <div className="slider-container">
            <div className="slider">
              {preferenceModules.length ? preferenceModules.map((module, idx) => (
                <div key={idx} className="slide">
                  <div className="variable-details">
                    <h4>Module Name</h4>
                    <p>{module.name}</p>
                    <h4>Description</h4>
                    <input
                      type="text"
                      className="variable-input"
                      value={module.description}
                      readOnly
                    />
                  </div>
                  <div className="buttons-container">
                    <img
                      src="/images/edit-button.png"
                      alt="Edit"
                      className="edit-image"
                      onClick={() => handleEditPreferenceClick(module)}
                    />
                    <img
                      src="/images/delete.png"
                      alt="Delete"
                      className="delete-image"
                      onClick={() => handleDeletePreference(module)}
                    />
                  </div>
                </div>
              )) : (
                <p>No preference modules selected yet.</p>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Edit Variable Modal */}
      {editingVariable && (
        <div className="modal-overlay" onClick={handleCancelVariableEdit}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Edit Variable</h2>

            <div className="modal-input-group">
              <label>Alias</label>
              <input
                type="text"
                className="variable-input"
                value={editedAlias}
                onChange={(e) => setEditedAlias(e.target.value)}
              />
            </div>

            <div className="modal-input-group">
              <label>Structure</label>
              <input
                type="text"
                className="variable-input"
                value={editedStructure}
                onChange={(e) => setEditedStructure(e.target.value)}
              />
            </div>

            <div className="modal-input-group">
              <label>Objective Value Alias</label>
              <input
                type="text"
                className="variable-input"
                value={editedObjectiveValueAlias}
                onChange={(e) => setEditedObjectiveValueAlias(e.target.value)}
              />
            </div>

            <div className="modal-buttons">
              <button className="save-button" onClick={handleSaveVariableEdit}>
                Save
              </button>
              <button className="cancel-button" onClick={handleCancelVariableEdit}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Edit Constraint Modal */}
      {editingConstraint && (
        <div className="modal-overlay" onClick={() => setEditingConstraint(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Edit Constraint</h2>
            <div className="modal-input-group">
              <label>Description</label>
              <input
                type="text"
                className="variable-input"
                value={editedConstraintDescription}
                onChange={(e) => setEditedConstraintDescription(e.target.value)}
              />
            </div>
            <div className="modal-buttons">
              <button className="save-button" onClick={handleSaveConstraintEdit}>
                Save
              </button>
              <button className="cancel-button" onClick={() => setEditingConstraint(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Edit Preference Modal */}
      {editingPreference && (
        <div className="modal-overlay" onClick={() => setEditingPreference(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Edit Preference</h2>
            <div className="modal-input-group">
              <label>Description</label>
              <input
                type="text"
                className="variable-input"
                value={editedPreferenceDescription}
                onChange={(e) => setEditedPreferenceDescription(e.target.value)}
              />
            </div>
            <div className="modal-buttons">
              <button className="save-button" onClick={handleSavePreferenceEdit}>
                Save
              </button>
              <button className="cancel-button" onClick={() => setEditingPreference(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      <Link to="/image-setting-set-and-params" title="Continue">
        <img
          src="/images/LeftArrowButton.png"
          alt="Continue"
          className="continue-button-image"
        />
      </Link>

      <Link to="/configure-preferences" title="Back">
        <img
          src="/images/RightArrowButton.png"
          alt="Back"
          className="back-button-image"
        />
      </Link>
    </div>
  );
};

export default SolutionPreviewPage;
