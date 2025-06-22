import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import './ConfigureConstraintsPage.css';
import '../Themes/MainTheme.css';

const ConfigurePreferencesPage = () => {
  const navigate = useNavigate();

  // ZPL context
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

  // Local state
  const [availablePreferences, setAvailablePreferences] = useState([]);
  const [moduleName, setModuleName] = useState('');
  const [selectedModuleIndex, setSelectedModuleIndex] = useState(null);

  // Whenever `preferences` or `preferenceModules` change,
  // filter out any preference already assigned to a module
  useEffect(() => {
    const usedIds = preferenceModules
      .flatMap(m => m.preferences.map(p => p.identifier));
    const filtered = preferences.filter(p => !usedIds.includes(p.identifier));
    setAvailablePreferences(filtered);
  }, [preferences, preferenceModules]);

  // Add a new empty module
  const addPreferenceModule = () => {
    if (!moduleName.trim()) return;
    setPreferenceModules(prev => [
      ...prev,
      { name: moduleName, description: '', preferences: [], involvedSets: [], involvedParams: [] }
    ]);
    setModuleName('');
  };

  // Update description of the selected module
  const updateModuleDescription = newDescription => {
    setPreferenceModules(prev =>
      prev.map((mod, i) =>
        i === selectedModuleIndex ? { ...mod, description: newDescription } : mod
      )
    );
  };

  // Add one preference into the selected module
  const addPreferenceToModule = preference => {
    if (selectedModuleIndex === null) {
      alert('Please select a module first!');
      return;
    }

    setPreferenceModules(prev =>
      prev.map((mod, i) => {
        if (i === selectedModuleIndex &&
            !mod.preferences.some(p => p.identifier === preference.identifier)) {
          return {
            ...mod,
            preferences: [...mod.preferences, preference]
          };
        }
        return mod;
      })
    );
    // (No manual removal here — our effect will pick up the change and filter it out.)
  };

  const handleHomeClick = () => {
    // Reset everything in context
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
    setImageName('');
    setImageDescription('');
    setZplCode('');
    setIsEditMode(false);
  };

  return (
    <div className="configure-constraints-page background">
      <div className="top-bar">
        <div className="top-bar-left">
          <Link to="/main-page" title="Home" onClick={handleHomeClick}>
            <img src="/images/HomeButton.png" alt="Home" className="top-bar-button" />
          </Link>
          <img
            src="/images/LeftArrowButton.png"
            alt="Continue"
            className="top-bar-button"
            onClick={() => navigate('/solution-preview')}
            title="Continue"
          />
        </div>
        <div className="top-bar-right">
          <Link to="/configure-constraints" title="Back">
            <img src="/images/RightArrowButton.png" alt="Back" className="top-bar-button" />
          </Link>
        </div>
      </div>

      <div className="constraints-layout">
        {/* Preference Modules Section */}
        <div className="constraint-modules">
          <h2>Preference Modules</h2>
          <input
            type="text"
            placeholder="Module Name"
            value={moduleName}
            onChange={e => setModuleName(e.target.value)}
          />
          <button onClick={addPreferenceModule}>Add Preference Module</button>
          <div className="module-list">
            {preferenceModules.map((mod, idx) => (
              <div key={idx} className="module-item-container">
                <button
                  className={`module-item ${selectedModuleIndex === idx ? 'selected' : ''}`}
                  onClick={() => setSelectedModuleIndex(idx)}
                >
                  {mod.name}
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Define Preference Module Section */}
        <div className="define-constraint-module">
          <h2>Define Preference Module</h2>
          {selectedModuleIndex === null ? (
            <p>Select a module</p>
          ) : (
            <>
              <h3>{preferenceModules[selectedModuleIndex]?.name || 'Unnamed Module'}</h3>
              <label>Description:</label>
              <hr />
              <textarea
                value={preferenceModules[selectedModuleIndex]?.description || ''}
                onChange={e => updateModuleDescription(e.target.value)}
                placeholder="Enter module description..."
                style={{ resize: 'none', width: '100%', height: '80px' }}
              />
              <p>This module's preferences:</p>
              <hr />
              <div className="module-drop-area">
                {preferenceModules[selectedModuleIndex]?.preferences.length > 0 ? (
                  preferenceModules[selectedModuleIndex].preferences.map((p, i) => (
                    <div key={i} className="dropped-constraint">
                      {p.identifier}
                    </div>
                  ))
                ) : (
                  <p>No preferences added</p>
                )}
              </div>
            </>
          )}
        </div>

        {/* Available Preferences Section */}
        <div className="available-constraints">
          <h2>Available Preferences</h2>
          {availablePreferences.length > 0 ? (
            availablePreferences.map((p, idx) => (
              <div key={idx} className="constraint-item-container">
                <button
                  className="constraint-item"
                  onClick={() => addPreferenceToModule(p)}
                >
                  {p.identifier}
                </button>
              </div>
            ))
          ) : (
            <p>No preferences available</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default ConfigurePreferencesPage;
