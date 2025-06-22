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
    constraintsModules,
    preferenceModules,
  } = useZPL();

  const [selectedSets, setSelectedSets] = useState([]);
  const [selectedParams, setSelectedParams] = useState([]);
  const [displaySets, setDisplaySets] = useState([]);
  const [displayParams, setDisplayParams] = useState([]);
  const [setAliases, setSetAliases] = useState({});

  const navigate = useNavigate();

  // Whenever selectedVars change, update which sets/params to show
  useEffect(() => {
    const newDisplaySets = selectedVars
      .flatMap(v => v.dep?.setDependencies ?? [])
      .reduce((u, s) => (u.includes(s) ? u : [...u, s]), []);
    const newDisplayParams = selectedVars
      .flatMap(v => v.dep?.paramDependencies ?? [])
      .reduce((u, p) => (u.includes(p) ? u : [...u, p]), []);
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

  const handleSetCheckboxChange = s => {
    setSelectedSets(prev =>
      prev.includes(s) ? prev.filter(x => x !== s) : [...prev, s]
    );
  };

  const handleParamCheckboxChange = p => {
    setSelectedParams(prev =>
      prev.includes(p) ? prev.filter(x => x !== p) : [...prev, p]
    );
  };

  const handleAliasChange = (s, value) => {
    setSetAliases(prev => ({ ...prev, [s]: value }));
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
  };

  return (
    <div className="configure-variables-page background">
      <div className="top-bar">
        <div className="top-bar-left">
          <Link to="/main-page" title="Home" onClick={handleHomeClick}>
            <img
              src="/images/HomeButton.png"
              alt="Home"
              className="top-bar-button"
            />
          </Link>
          <img
            src="/images/LeftArrowButton.png"
            alt="Continue"
            className="top-bar-button"
            onClick={handleContinue}
            title="Continue"
          />
        </div>
        <div className="top-bar-right">
          <Link to="/upload-zpl" title="Back">
            <img
              src="/images/RightArrowButton.png"
              alt="Back"
              className="top-bar-button"
            />
          </Link>
        </div>
      </div>

      <div className="MainDiv">
        <h1 className="page-title">Variables</h1>
        <div className="variables-layout">
          <div className="available-variables">
            <form className="form">
              {variables.length > 0 ? (
                variables.map((variable, i) => {
                  const isPreselected = selectedVars.some(
                    v => v.identifier === variable.identifier
                  );
                  return (
                    <div className="inputGroup" key={variable.identifier}>
                      <input
                        id={`var-${i}`}
                        type="checkbox"
                        defaultChecked={isPreselected}
                        onChange={() => handleVarCheckboxChange(variable)}
                      />
                      <label htmlFor={`var-${i}`}>
                        {variable.identifier}
                      </label>
                    </div>
                  );
                })
              ) : (
                <p>No variables available.</p>
              )}
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConfigureVariablesPage;
