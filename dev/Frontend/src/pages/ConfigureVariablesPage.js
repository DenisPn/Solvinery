import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useZPL } from "../context/ZPLContext";
import "./ConfigureVariablesPage.css";
import Checkbox from "../reusableComponents/Checkbox.js";

const ConfigureVariablesPage = () => {
    const { variables, variablesModule, setVariablesModule } = useZPL();

    const [selectedVars, setSelectedVars] = useState([]);  // Stores selected variables
    const [selectedSets, setSelectedSets] = useState([]);  // Stores selected sets
    const [selectedParams, setSelectedParams] = useState([]); // Stores selected params
    const [displaySets, setDisplaySets] = useState([]);    // Stores sets that should be displayed
    const [displayParams, setDisplayParams] = useState([]); // Stores params that should be displayed

    // State for storing aliases for each selected set
    const [setAliases, setSetAliases] = useState({});

    // Function to update displayed sets & params when variables are selected
    const updateDisplayedSetsAndParams = () => {
        const newDisplaySets = selectedVars
            .flatMap(variable => variable.dep?.setDependencies ?? [])
            .reduce((unique, item) => unique.includes(item) ? unique : [...unique, item], []);

        const newDisplayParams = selectedVars
            .flatMap(variable => variable.dep?.paramDependencies ?? [])
            .reduce((unique, item) => unique.includes(item) ? unique : [...unique, item], []);

        setDisplaySets(newDisplaySets);
        setDisplayParams(newDisplayParams);
    };

    useEffect(() => {
        updateDisplayedSetsAndParams();
    }, [selectedVars]); // Update when selected variables change

    // Handles variable selection (checkbox clicked)
    const handleVarCheckboxChange = (variable) => {
        setSelectedVars(prevSelectedVars => {
            if (prevSelectedVars.includes(variable)) {
                return prevSelectedVars.filter(v => v !== variable);
            } else {
                return [...prevSelectedVars, variable];
            }
        });
    };

    // Handles set selection (checkbox clicked)
    const handleSetCheckboxChange = (set) => {
        setSelectedSets(prevSelected => {
            if (prevSelected.includes(set)) {
                return prevSelected.filter(s => s !== set);
            } else {
                return [...prevSelected, set];
            }
        });
    };

    // Handles parameter selection (checkbox clicked)
    const handleParamCheckboxChange = (param) => {
        setSelectedParams(prevSelected => {
            if (prevSelected.includes(param)) {
                return prevSelected.filter(p => p !== param);
            } else {
                return [...prevSelected, param];
            }
        });
    };

    // Handles alias input change
    const handleAliasChange = (set, value) => {
        setSetAliases(prevAliases => ({
            ...prevAliases,
            [set]: value
        }));
    };

    // Save selected variables, sets, parameters, and aliases in context when navigating
    const handleContinue = () => {
        const variablesOfInterest = selectedVars.map(v => v.identifier);
    
        // Create a map where each variable is mapped to an array of corresponding set aliases
        const variableAliases = Object.fromEntries(
            selectedVars.map(variable => [
                variable.identifier,
                (variable.dep?.setDependencies ?? []).map(set => setAliases[set] || set) // Use alias or fallback to set name
            ])
        );
    
        setVariablesModule({
            variablesOfInterest,
            variablesConfigurableSets: selectedSets,
            variablesConfigurableParams: selectedParams,
            variableAliases,
        });
    
        setSetAliases(setAliases); // Ensure the latest aliases are saved globally
    };
    
    

    return (
        <div className="configure-variables-page background">
            <h1 className="page-title">Configure Variables</h1>
            <div className="variables-layout">
                
                {/* Variables Section */}
                <div className="available-variables">
                    <h2>Available Variables</h2>
                    {variables.length > 0 ? (
                        variables.map((variable, index) => (
                            <Checkbox
                                key={index}
                                label={variable.identifier}
                                checked={selectedVars.includes(variable)}
                                onChange={() => handleVarCheckboxChange(variable)}
                            />
                        ))
                    ) : (
                        <p>No variables available.</p>
                    )}
                </div>
                
                {/* Sets & Parameters Section (Only for Selected Variables) */}
                <div className="involved-section">
                    <h2>Involved Sets</h2>
                    {displaySets.length > 0 ? (
                        displaySets.map((set, index) => (
                            <div key={index} className="set-item">
                                <Checkbox
                                    label={set}
                                    checked={selectedSets.includes(set)}
                                    onChange={() => handleSetCheckboxChange(set)}
                                />
                                <input
                                    type="text"
                                    placeholder="alias"
                                    value={setAliases[set] || ""}
                                    onChange={(e) => handleAliasChange(set, e.target.value)}
                                    className="alias-input"
                                />
                            </div>
                        ))
                    ) : (
                        <p>No sets available.</p>
                    )}

                    <h2>Involved Parameters</h2>
                    {displayParams.length > 0 ? (
                        displayParams.map((param, index) => (
                            <Checkbox
                                key={index}
                                label={param}
                                checked={selectedParams.includes(param)}
                                onChange={() => handleParamCheckboxChange(param)}
                            />
                        ))
                    ) : (
                        <p>No parameters available.</p>
                    )}
                </div>
            </div>
            
            <Link to="/configure-constraints" className="continue-button" onClick={handleContinue}>
                Continue
            </Link>
        </div>
    );
};

export default ConfigureVariablesPage;
