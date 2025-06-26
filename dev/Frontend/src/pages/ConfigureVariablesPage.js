import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useZPL } from "../context/ZPLContext";
import "./ConfigureVariablesPage.css";

const ConfigureVariablesPage = () => {
  const {
    variables,
    variablesModule,
    setVariablesModule,
    selectedVars,
    setSelectedVars,
    setVariables,
    setConstraints,
    setConstraintsModules,
    setPreferences,
    setPreferenceModules,
    setSetTypes,
    setParamTypes,
    setImageId,
    setImageName,
    setImageDescription,
    setZplCode,
    setIsEditMode,
  } = useZPL();

  const [selectedSets, setSelectedSets] = useState([]);
  const [selectedParams, setSelectedParams] = useState([]);
  const [displaySets, setDisplaySets] = useState([]);
  const [displayParams, setDisplayParams] = useState([]);
  const [setAliases, setSetAliases] = useState({});
  const [editVar, setEditVar] = useState(null);
  const [editedAlias, setEditedAlias] = useState("");
  const [editedStructure, setEditedStructure] = useState("");
  const [editedObjectiveValueAlias, setEditedObjectiveValueAlias] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    const newDisplaySets = selectedVars
      .flatMap(v => v.dep?.setDependencies ?? [])
      .filter((s, i, a) => a.indexOf(s) === i);
    const newDisplayParams = selectedVars
      .flatMap(v => v.dep?.paramDependencies ?? [])
      .filter((p, i, a) => a.indexOf(p) === i);
    setDisplaySets(newDisplaySets);
    setDisplayParams(newDisplayParams);
  }, [selectedVars]);

  const handleVarCheckboxChange = variable => {
    setSelectedVars(prev =>
      prev.some(v => v.identifier === variable.identifier)
        ? prev.filter(v => v.identifier !== variable.identifier)
        : [...prev, variable]
    );
  };

  const handleContinue = () => {
    const variablesOfInterest = selectedVars.map(v => v.identifier);
    const variableAliases = Object.fromEntries(
      selectedVars.map(v => [
        v.identifier,
        (v.dep?.setDependencies ?? []).map(s => setAliases[s] || s),
      ])
    );
    setVariablesModule({
      variablesOfInterest,
      variablesConfigurableSets: selectedSets,
      variablesConfigurableParams: selectedParams,
      variableAliases,
    });
    navigate("/configure-constraints");
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
    setParamTypes({});
    setImageId(null);
    setImageName("");
    setImageDescription("");
    setZplCode("");
    setIsEditMode(false);
  };

  const openModal = v => {
    setEditVar(v);
    setEditedAlias(v.alias || "");
    setEditedStructure(v.structure || "");
    setEditedObjectiveValueAlias(v.objectiveValueAlias || "");
  };

  const closeModal = () => setEditVar(null);

const handleSaveEdit = () => {
  // Update variables list
  setVariables(prev =>
    prev.map(v =>
      v.identifier === editVar.identifier
        ? {
            ...v,
            alias: editedAlias,
            structure: editedStructure,
            objectiveValueAlias: editedObjectiveValueAlias,
          }
        : v
    )
  );

  // Update selectedVars list (if applicable)
  setSelectedVars(prev =>
    prev.map(v =>
      v.identifier === editVar.identifier
        ? {
            ...v,
            alias: editedAlias,
            structure: editedStructure,
            objectiveValueAlias: editedObjectiveValueAlias,
          }
        : v
    )
  );

  // Close modal
  setEditVar(null);
};


  return (
    <>
      {/* Modal */}
      {editVar && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h2 className="modal-title">
              <span className="modal-title-icon">✏️</span>
              Edit Variable:{" "}
              <span className="variable-name-highlight">{editVar.identifier}</span>
            </h2>

            <div className="modal-input-group">
              <label><span className="label-icon">🏷️</span>Alias</label>
              <input
                className="modal-input"
                value={editedAlias}
                onChange={e => setEditedAlias(e.target.value)}
              />
            </div>

            <div className="modal-input-group">
              <label><span className="label-icon">🧱</span>Structure</label>
              <input
                className="modal-input"
                value={editedStructure}
                onChange={e => setEditedStructure(e.target.value)}
              />
            </div>

            <div className="modal-input-group">
              <label><span className="label-icon">🎯</span>Objective Value Alias</label>
              <input
                className="modal-input"
                value={editedObjectiveValueAlias}
                onChange={e => setEditedObjectiveValueAlias(e.target.value)}
              />
            </div>

            <div className="modal-buttons">
              <button className="save-button" onClick={handleSaveEdit}>Save</button>
              <button className="cancel-button" onClick={closeModal}>Cancel</button>
            </div>
          </div>
        </div>
      )}

      {/* Main Page */}
      <div className="configure-variables-page background">
        <div
          className="top-bar"
          style={{ pointerEvents: editVar ? "none" : "auto" }}
        >
          <div className="top-bar-left">
            <Link to="/main-page" onClick={handleHomeClick} title="Home">
              <img
                alt="Home"
                src={`${process.env.PUBLIC_URL}/images/HomeButton.png`}
                className="top-bar-button"
              />
            </Link>
            <img
              alt="Continue"
              src={`${process.env.PUBLIC_URL}/images/LeftArrowButton.png`}
              className="top-bar-button"
              onClick={handleContinue}
              title="Continue"
            />
          </div>

          <div className="top-bar-right">
            <Link to="/upload-zpl" title="Back">
              <img
                alt="Back"
                src={`${process.env.PUBLIC_URL}/images/RightArrowButton.png`}
                className="top-bar-button"
                onClick={e => {
                  e.preventDefault();
                  setIsEditMode(false);
                  navigate("/upload-zpl");
                }}
              />
            </Link>
          </div>
        </div>

        <div className={`MainDiv ${editVar ? "hide-edit-buttons" : ""}`}>
          <h1 className="page-title">Variables</h1>
          <div className="variables-layout">
            <div className="available-variables">
              <form className="form">
                {variables.length ? (
                  variables.map((v, i) => (
                    <div className="inputGroup" key={v.identifier}>
                      <input
                        id={`var-${i}`}
                        type="checkbox"
                        defaultChecked={selectedVars.some(
                          x => x.identifier === v.identifier
                        )}
                        onChange={() => handleVarCheckboxChange(v)}
                      />
                      <label htmlFor={`var-${i}`}>{v.identifier}</label>

                      <button
                        type="button"
                        className="edit-button"
                        onClick={() => openModal(v)}
                        title="Edit"
                      >
                        ✏️
                      </button>
                    </div>
                  ))
                ) : (
                  <p>No variables available.</p>
                )}
              </form>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default ConfigureVariablesPage;
