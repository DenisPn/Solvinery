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

    // Save selected variables, sets, and parameters in context when navigating
    const handleContinue = () => {
        const variablesOfInterest = selectedVars.map(v => v.identifier);
    
        // Create a map where each variable is mapped to an array of placeholder strings
        // The length of each array is determined by the number of setDependencies of the variable
        const variableAliases = Object.fromEntries(
            selectedVars.map(variable => [
                variable.identifier,
                new Array(variable.dep?.setDependencies?.length || 0).fill("placeholder")
            ])
        );
    
        setVariablesModule({
            variablesOfInterest,
            variablesConfigurableSets: selectedSets,
            variablesConfigurableParams: selectedParams,
            variableAliases
        });
    };
    
    

    return (
        <div className="configure-variables-page">
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
                            <Checkbox
                                key={index}
                                label={set}
                                checked={selectedSets.includes(set)}
                                onChange={() => handleSetCheckboxChange(set)}
                            />
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
