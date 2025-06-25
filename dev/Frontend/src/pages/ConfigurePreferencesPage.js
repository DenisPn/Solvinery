import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import './ConfigureConstraintsPage.css';
import '../Themes/MainTheme.css';

const MAX_NAME_LENGTH = 25;

const ConfigurePreferencesPage = () => {
  const navigate = useNavigate();
  const {
    preferences,
    preferenceModules,
    setPreferenceModules = () => {},
    setVariables,
    setSelectedVars,
    setVariablesModule,
    setConstraints,
    setConstraintsModules,
    setPreferences,
    setSetTypes,
    setSetAliases,
    setParamTypes,
    setImageId,
    setImageName,
    setImageDescription,
    setZplCode,
    setIsEditMode,
  } = useZPL();

  const [availablePreferences, setAvailablePreferences] = useState([]);
  const [selectedModuleIndex, setSelectedModuleIndex] = useState(null);

  useEffect(() => {
    const usedIds = preferenceModules.flatMap(m =>
      m.preferences.map(p => p.identifier)
    );
    setAvailablePreferences(
      preferences.filter(p => !usedIds.includes(p.identifier))
    );
  }, [preferences, preferenceModules]);

  const addPreferenceModule = () => {
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
    setPreferenceModules(prev => [
      ...prev,
      { name, description: '', preferences: [] },
    ]);
    setSelectedModuleIndex(preferenceModules.length);
  };

  const updateModuleName = () => {
    if (selectedModuleIndex === null) return;
    const current = preferenceModules[selectedModuleIndex].name;
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
    setPreferenceModules(prev =>
      prev.map((mod, i) =>
        i === selectedModuleIndex ? { ...mod, name: newName } : mod
      )
    );
  };

  const updateModuleDescription = newDesc => {
    if (selectedModuleIndex === null) return;
    setPreferenceModules(prev =>
      prev.map((mod, i) =>
        i === selectedModuleIndex ? { ...mod, description: newDesc } : mod
      )
    );
  };

  const addPreferenceToModule = p => {
    if (selectedModuleIndex === null) {
      alert('Please select a module first!');
      return;
    }
    setPreferenceModules(prev =>
      prev.map((mod, i) =>
        i === selectedModuleIndex &&
        !mod.preferences.some(x => x.identifier === p.identifier)
          ? { ...mod, preferences: [...mod.preferences, p] }
          : mod
      )
    );
  };

  const removePreferenceFromModule = identifier => {
    if (selectedModuleIndex === null) return;
    setPreferenceModules(prev =>
      prev.map((mod, i) =>
        i === selectedModuleIndex
          ? {
              ...mod,
              preferences: mod.preferences.filter(p => p.identifier !== identifier),
            }
          : mod
      )
    );
  };

  const removeModule = () => {
    if (selectedModuleIndex === null) return;
    const name = preferenceModules[selectedModuleIndex].name;
    if (window.confirm(`Delete module “${name}”?`)) {
      setPreferenceModules(prev =>
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
            src="/images/LeftArrowButton.png"
            alt="Continue"
            className="top-bar-button"
            onClick={() => navigate('/image-setting-set-and-params')}
            title="Continue"
          />
        </div>
        <div className="top-bar-right">
          <Link to="/configure-constraints" title="Back">
            <img src="`${process.env.PUBLIC_URL}/images/RightArrowButton.png'" alt="Back" className="top-bar-button" />
          </Link>
        </div>
      </div>

      <div className="grid-container">
        {/* Preference Module Creator */}
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
              {preferenceModules.map((mod, idx) => (
                <option key={idx} value={idx}>
                  {mod.name}
                </option>
              ))}
            </select>
            <button className="btn" onClick={addPreferenceModule}>
              Create Module
            </button>
          </div>
        </div>

        {/* Available Preferences Sidebar */}
        <div className="available-constraints-sidebar">
          <h2>Available Preferences</h2>
          {availablePreferences.length > 0 ? (
            availablePreferences.map((p, i) => (
              <button
                key={i}
                className="constraint-item"
                onClick={() => addPreferenceToModule(p)}
              >
                {p.identifier}
              </button>
            ))
          ) : (
            <p>No preferences available</p>
          )}
        </div>

        {/* Define Preference Module */}
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
                <h2>{preferenceModules[selectedModuleIndex].name}</h2>
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
                      preferenceModules[selectedModuleIndex].description || ''
                    }
                    onChange={e => updateModuleDescription(e.target.value)}
                    placeholder="Enter module description…"
                  />
                </div>
                <div className="module-constraints">
                  <div className="module-drop-area">
                    {preferenceModules[selectedModuleIndex].preferences.length > 0 ? (
                      preferenceModules[selectedModuleIndex].preferences.map((p, i) => (
                        <div key={i} className="dropped-constraint">
                          <span>{p.identifier}</span>
                          <button
                            className="delete-btn"
                            onClick={() => removePreferenceFromModule(p.identifier)}
                            title="Remove preference"
                          >
                            🗑️
                          </button>
                        </div>
                      ))
                    ) : (
                      <p>No preferences added</p>
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

export default ConfigurePreferencesPage;
