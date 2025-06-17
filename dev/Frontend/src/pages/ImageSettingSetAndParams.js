import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useZPL } from "../context/ZPLContext";
import "./ImageSettingSetAndParams.css";

export default function ImageSettingSetAndParams() {
  const { setTypes, setAliases, setVariables,
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
    setZplCode, } = useZPL();
  const navigate = useNavigate();

  // Debug: log both maps
  useEffect(() => {
    console.log("setTypes:", setTypes);
    console.log("setAliases:", setAliases);
  }, [setTypes, setAliases]);


  
  const [activeSection, setActiveSection] = useState("sets");
  const [editingSet, setEditingSet] = useState(null);
  const [editedAlias, setEditedAlias] = useState("");
  const [editedTypeAlias, setEditedTypeAlias] = useState("");

  const handleEditSetClick = (setName) => {
    setEditingSet(setName);
    const { alias = setName, typeAlias = [] } = setAliases[setName] || {};
    setEditedAlias(alias);
    setEditedTypeAlias(Array.isArray(typeAlias) ? typeAlias.join(",") : "");
  };

  const handleSaveSetEdit = () => {
    setSetAliases(prev => ({
      ...prev,
      [editingSet]: {
        alias: editedAlias.trim(),
        typeAlias: editedTypeAlias
          .split(",")
          .map(s => s.trim())
          .filter(s => s),
      }
    }));
    setEditingSet(null);
  };

  const handleDeleteSet = (setName) => {
    const updated = { ...setTypes };
    delete updated[setName];
    setSetTypes(updated);
    const updatedAliases = { ...setAliases };
    delete updatedAliases[setName];
    setSetAliases(updatedAliases);
  };

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
    setImageId(null);
    setImageName("");
    setImageDescription("");
    setZplCode("");
  };


  return (
    <div className="image-setting-page background">
      <div className="image-setting-top-left-buttons">
        <Link to="/" onClick={handleHomeClick}>
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

      <h1 className="page-title">Image Settings - Sets and Parameters</h1>
      <div className="toggle-section">
        <button onClick={() => setActiveSection("sets")} className="toggle-button">
          Show Sets
        </button>
        <button onClick={() => setActiveSection("params")} className="toggle-button">
          Show Parameters
        </button>
      </div>

      {activeSection === "sets" && (
        <div className="sets-section">
          <h2 className="section-title">Sets</h2>
          <div className="slider-container">
            <div className="slider">
              {Object.entries(setTypes).map(([setName, data], idx) => {
                const { alias = setName, typeAlias = [] } = setAliases[setName] || {};
                return (
                  <div key={idx} className="slide">
                    <h4>{setName}</h4>
                    <div className="field">
                      <span className="field-label">Type:</span>
                      <span className="field-value">
                        {Array.isArray(data)
                          ? data.join(", ")
                          : data}
                      </span>
                    </div>
                    <div className="field">
                      <span className="field-label">Alias:</span>
                      <span className="field-value">{alias}</span>
                    </div>
                    <div className="field">
                      <span className="field-label">Type Alias:</span>
                      <span className="field-value">
                        {Array.isArray(typeAlias)
                          ? typeAlias.join(", ")
                          : ""}
                      </span>
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
        </div>
      )}

      {editingSet && (
        <div className="modal-overlay" onClick={() => setEditingSet(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2>Edit {editingSet}</h2>
            <label>Alias:</label>
            <input
              type="text"
              value={editedAlias}
              onChange={e => setEditedAlias(e.target.value)}
            />
            <label>Type Alias (comma separated):</label>
            <input
              type="text"
              value={editedTypeAlias}
              onChange={e => setEditedTypeAlias(e.target.value)}
            />
            <div className="modal-buttons">
              <button onClick={handleSaveSetEdit}>Save</button>
              <button onClick={() => setEditingSet(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
