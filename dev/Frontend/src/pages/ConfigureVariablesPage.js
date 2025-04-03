import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useZPL } from "../context/ZPLContext";
import "./ConfigureVariablesPage.css";

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
            <div className="MainDiv">
            <h1 className="page-title">Configure Variables</h1>
            <div className="variables-layout">
                
                {/* Variables Section */}
                <div className="available-variables">

                    <form class="form">
                    

                    {variables.length > 0 ? (
                        variables.map((variable, index) => (
                      
                         <div class="inputGroup">
                            <input
                                id={index}
                                label={variable.identifier}
                                checked={selectedVars.includes(variable)}
                                onChange={() => handleVarCheckboxChange(variable)}
                                type="checkbox"
                            />
                            <label for={index}>{variable.identifier}</label>
                        </div>
                        
                        ))
                    ) : (
                        <p>No variables available.</p>
                    )}
                    
                    </form>
                    

                </div>
                
            
            </div>
            
            <Link to="/configure-constraints" className="button" onClick={handleContinue}>
                Continue
            </Link>
        </div>
        </div>
    );
};

export default ConfigureVariablesPage;
