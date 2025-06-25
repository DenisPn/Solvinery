import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import './ConfigureConstraintsPage.css';

const MAX_NAME_LENGTH = 25;

const ConfigureConstraintsPage = () => {
  const navigate = useNavigate();
  const {
    constraints,
    constraintsModules,
    setConstraintsModules = () => {},
    setVariables,
    setSelectedVars,
    setVariablesModule,
    setConstraints,
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

  const [availableConstraints, setAvailableConstraints] = useState([]);
  const [selectedModuleIndex, setSelectedModuleIndex] = useState(null);

  useEffect(() => {
    const usedIds = constraintsModules.flatMap(m =>
      m.constraints.map(c => c.identifier)
    );
    setAvailableConstraints(
      constraints.filter(c => !usedIds.includes(c.identifier))
    );
  }, [constraints, constraintsModules]);

  const addConstraintModule = () => {
    const raw = window.prompt(
      `Enter new module name (1–${MAX_NAME_LENGTH} chars):`
    );
    if (!raw) return;
    const name = raw.trim();
    if (!name) {
      alert('Module name cannot be empty.');
      return;
    }
    if (name.length > MAX_NAME_LENGTH) {
      alert(`Must be under ${MAX_NAME_LENGTH} characters.`);
      return;
    }
    setConstraintsModules(prev => [
      ...prev,
      { name, description: '', constraints: [] },
    ]);
    setSelectedModuleIndex(constraintsModules.length);
  };

  const updateModuleName = () => {
    if (selectedModuleIndex === null) return;
    const current = constraintsModules[selectedModuleIndex].name;
    const raw = window.prompt(
      `Rename module (1–${MAX_NAME_LENGTH} chars):`,
      current
    );
    if (!raw) return;
    const newName = raw.trim();
    if (!newName) {
      alert('Module name cannot be empty.');
      return;
    }
    if (newName.length > MAX_NAME_LENGTH) {
      alert(`Must be under ${MAX_NAME_LENGTH} characters.`);
      return;
    }
    setConstraintsModules(prev =>
      prev.map((mod, i) =>
        i === selectedModuleIndex ? { ...mod, name: newName } : mod
      )
    );
  };

  const updateModuleDescription = newDesc => {
    if (selectedModuleIndex === null) return;
    setConstraintsModules(prev =>
      prev.map((mod, i) =>
        i === selectedModuleIndex ? { ...mod, description: newDesc } : mod
      )
    );
  };

  const addConstraintToModule = c => {
    if (selectedModuleIndex === null) {
      alert('Please select a module first!');
      return;
    }
    setConstraintsModules(prev =>
      prev.map((mod, i) =>
        i === selectedModuleIndex &&
        !mod.constraints.some(x => x.identifier === c.identifier)
          ? { ...mod, constraints: [...mod.constraints, c] }
          : mod
      )
    );
  };

  const removeConstraintFromModule = identifier => {
    if (selectedModuleIndex === null) return;
    setConstraintsModules(prev =>
      prev.map((mod, i) =>
        i === selectedModuleIndex
          ? {
              ...mod,
              constraints: mod.constraints.filter(c => c.identifier !== identifier),
            }
          : mod
      )
    );
  };

  const removeModule = () => {
    if (selectedModuleIndex === null) return;
    const name = constraintsModules[selectedModuleIndex].name;
    if (window.confirm(`Delete module “${name}”?`)) {
      setConstraintsModules(prev =>
        prev.filter((_, i) => i !== selectedModuleIndex)
      );
      setSelectedModuleIndex(null);
    }
  };

  const handleHomeClick = () => {
    setVariables([]); setSelectedVars([]);
    setVariablesModule({
      variablesOfInterest: [],
      variablesConfigurableSets: [],
      variablesConfigurableParams: [],
    });
    setConstraints([]); setConstraintsModules([]);
    setPreferences([]); setPreferenceModules([]);
    setSetTypes({}); setSetAliases({}); setParamTypes({});
    setImageId(null); setImageName(''); setImageDescription('');
    setZplCode(''); setIsEditMode(false);
    setSelectedModuleIndex(null);
  };

  return (
    <div className="configure-constraints-page">
      <div className="top-bar">
        <div className="top-bar-left">
          <Link to="/main-page" onClick={handleHomeClick} title="Home">
            <img src="`${process.env.PUBLIC_URL}/images/HomeButton.png'" alt="Home" className="top-bar-button" />
          </Link>
          <img
            src="`${process.env.PUBLIC_URL}/images/LeftArrowButton.png'"
            alt="Continue"
            className="top-bar-button"
            onClick={() => navigate('/configure-preferences')}
            title="Continue"
          />
        </div>
        <div className="top-bar-right">
          <Link to="/configure-variables" title="Back">
            <img src="`${process.env.PUBLIC_URL}/images/RightArrowButton.png'" alt="Back" className="top-bar-button" />
          </Link>
        </div>
      </div>

      <div className="grid-container">
        {/* Module Creator */}
        <div className="module-creator">
          <div className="creator-controls">
            <select
              className="module-select"
              value={selectedModuleIndex ?? ''}
              onChange={e =>
                setSelectedModuleIndex(
                  e.target.value === '' ? null : Number(e.target.value)
                )
              }
            >
              <option value="" disabled>
                — Select Module —
              </option>
              {constraintsModules.map((mod, idx) => (
                <option key={idx} value={idx}>
                  {mod.name}
                </option>
              ))}
            </select>
            <button className="btn" onClick={addConstraintModule}>
              Create Module
            </button>
          </div>
        </div>

        {/* Available Constraints */}
        <div className="available-constraints-sidebar">
          <h2>Available Constraints</h2>
          {availableConstraints.length > 0 ? (
            availableConstraints.map((c, i) => (
              <button
                key={i}
                className="constraint-item"
                onClick={() => addConstraintToModule(c)}
              >
                {c.identifier}
              </button>
            ))
          ) : (
            <p>No constraints available</p>
          )}
        </div>

        {/* Define Constraint Module */}
        <div className="module-details">
          {selectedModuleIndex === null ? (
            <p>Please select a module</p>
          ) : (
            <>
              <div className="module-header-with-edit">
                <button
                  className="delete-module-btn"
                  onClick={removeModule}
                  title="Delete module"
                >
                  🗑️
                </button>
                <h2>{constraintsModules[selectedModuleIndex].name}</h2>
                <button
                  className="edit-btn"
                  onClick={updateModuleName}
                  title="Rename module"
                >
                  ✏️
                </button>
              </div>

              <div className="module-details-grid">
                <div className="module-description">
                  <textarea
                    value={
                      constraintsModules[selectedModuleIndex].description || ''
                    }
                    onChange={e => updateModuleDescription(e.target.value)}
                    placeholder="Enter module description…"
                  />
                </div>
                <div className="module-constraints">
                  <div className="module-drop-area">
                    {constraintsModules[selectedModuleIndex].constraints.length >
                    0 ? (
                      constraintsModules[selectedModuleIndex].constraints.map(
                        (c, i) => (
                          <div key={i} className="dropped-constraint">
                            <span>{c.identifier}</span>
                            <button
                              className="delete-btn"
                              onClick={() => removeConstraintFromModule(c.identifier)}
                              title="Remove constraint"
                            >
                              🗑️
                            </button>
                          </div>
                        )
                      )
                    ) : (
                      <p>No constraints added</p>
                    )}
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default ConfigureConstraintsPage;
