import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useZPL } from "../context/ZPLContext";
import "./ImageSettingSetAndParams.css";

export default function ImageSettingSetAndParams() {
  const {
    // Context values and setters
    setTypes,
    setAliases,
    paramTypes,
    paramAliases,
    setParamTypes,
    setParamAliases,
    setVariables,
    setSelectedVars,
    setVariablesModule,
    setConstraints,
    setConstraintsModules,
    setPreferences,
    setPreferenceModules,
    setSetTypes,
    setSetAliases,
    setImageId,
    setImageName,
    setImageDescription,
    setZplCode,
  } = useZPL();

  const navigate = useNavigate();

  // Debug log
  useEffect(() => {
    console.log("setTypes:", setTypes);
    console.log("setAliases:", setAliases);
    console.log("paramTypes:", paramTypes);
    console.log("paramAliases:", paramAliases);
  }, [setTypes, setAliases, paramTypes, paramAliases]);

  const [activeSection, setActiveSection] = useState("sets");

  // --- Set editing state ---
  const [editingSet, setEditingSet] = useState(null);
  const [editedSetAlias, setEditedSetAlias] = useState("");

  const handleEditSetClick = (setName) => {
    setEditingSet(setName);
    const { alias = setName } = setAliases[setName] || {};
    setEditedSetAlias(alias);
  };

  const handleSaveSetEdit = () => {
    setSetAliases((prev) => ({
      ...prev,
      [editingSet]: { alias: editedSetAlias.trim() },
    }));
    setEditingSet(null);
  };

  const handleDeleteSet = (setName) => {
    const newTypes = { ...setTypes };
    delete newTypes[setName];
    setSetTypes(newTypes);

    const newAliases = { ...setAliases };
    delete newAliases[setName];
    setSetAliases(newAliases);
  };

  // --- Param editing state ---
  const [editingParam, setEditingParam] = useState(null);
  const [editedParamAlias, setEditedParamAlias] = useState("");

  const handleEditParamClick = (paramName) => {
    setEditingParam(paramName);
    const { alias = paramName } = paramAliases[paramName] || {};
    setEditedParamAlias(alias);
  };

  const handleSaveParamEdit = () => {
    setParamAliases((prev) => ({
      ...prev,
      [editingParam]: { alias: editedParamAlias.trim() },
    }));
    setEditingParam(null);
  };

  const handleDeleteParam = (paramName) => {
    const newTypes = { ...paramTypes };
    delete newTypes[paramName];
    setParamTypes(newTypes);

    const newAliases = { ...paramAliases };
    delete newAliases[paramName];
    setParamAliases(newAliases);
  };

  // --- Reset and navigate home ---
  const handleHomeClick = () => {
    setVariables([]);
    setSelectedVars([]);
    setVariablesModule({
      variablesOfInterest: [],
      variablesConfigurableSets: [],
      variablesConfigurableParams: [],
    });
    setConstraints([]);
    setConstraintsModules([]);
    setPreferences([]);
    setPreferenceModules([]);
    setSetTypes({});
    setSetAliases({});
    setParamTypes({});
    setParamAliases({});
    setImageId(null);
    setImageName("");
    setImageDescription("");
    setZplCode("");
    navigate("/main-page");
  };

  return (
    <div className="image-setting-page background">
      <div className="image-setting-top-left-buttons">
        <Link to="/main-page" onClick={handleHomeClick}>
          <img src="/images/HomeButton.png" alt="Home" className="icon-btn" />
        </Link>
        <img
          src="/images/LeftArrowButton.png"
          alt="Continue"
          className="icon-btn"
          onClick={() => navigate("/image-setting-review")}
        />
        <img
          src="/images/RightArrowButton.png"
          alt="Back"
          className="icon-btn"
          onClick={() => navigate("/solution-preview")}
        />
      </div>

      <h1 className="page-title">Image Settings – Sets &amp; Parameters</h1>
      <div className="toggle-section">
        <button
          onClick={() => setActiveSection("sets")}
          className="toggle-button"
        >
          Show Sets
        </button>
        <button
          onClick={() => setActiveSection("params")}
          className="toggle-button"
        >
          Show Parameters
        </button>
      </div>

      {activeSection === "sets" && (
        <div className="sets-section">
          <h2 className="section-title">Sets</h2>
          <div className="slider-container">
            <div className="slider">
              {Object.entries(setTypes).map(([setName, data], idx) => {
                const { alias = setName } = setAliases[setName] || {};
                return (
                  <div key={idx} className="slide">
                    <h4>{setName}</h4>
                    <div className="field">
                      <span className="field-label">Type:</span>
                      <span className="field-value">
                        {Array.isArray(data) ? data.join(", ") : data}
                      </span>
                    </div>
                    <div className="field">
                      <span className="field-label">Alias:</span>
                      <span className="field-value">{alias}</span>
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
                );
              })}
            </div>
          </div>
        </div>
      )}

      {activeSection === "params" && (
        <div className="parameters-section">
          <h2 className="section-title">Parameters</h2>
          <div className="slider-container">
            <div className="slider">
              {Object.entries(paramTypes).map(([paramName, data], idx) => {
                const { alias = paramName } = paramAliases[paramName] || {};
                return (
                  <div key={idx} className="slide">
                    <h4>{paramName}</h4>
                    <div className="field">
                      <span className="field-label">Type:</span>
                      <span className="field-value">
                        {Array.isArray(data) ? data.join(", ") : data}
                      </span>
                    </div>
                    <div className="field">
                      <span className="field-label">Alias:</span>
                      <span className="field-value">{alias}</span>
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
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/* Edit Set Modal */}
      {editingSet && (
        <div className="modal-overlay" onClick={() => setEditingSet(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Edit {editingSet}</h2>
            <label>Alias:</label>
            <input
              type="text"
              value={editedSetAlias}
              onChange={(e) => setEditedSetAlias(e.target.value)}
            />
            <div className="modal-buttons">
              <button onClick={handleSaveSetEdit}>Save</button>
              <button onClick={() => setEditingSet(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}

      {/* Edit Param Modal */}
      {editingParam && (
        <div className="modal-overlay" onClick={() => setEditingParam(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>Edit {editingParam}</h2>
            <label>Alias:</label>
            <input
              type="text"
              value={editedParamAlias}
              onChange={(e) => setEditedParamAlias(e.target.value)}
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
}
