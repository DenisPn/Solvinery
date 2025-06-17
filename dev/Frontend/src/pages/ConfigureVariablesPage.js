import React, { useState, useEffect } from "react";
import { Link, redirect } from "react-router-dom";
import { useZPL } from "../context/ZPLContext";
import { useNavigate } from "react-router-dom";

import "./ConfigureVariablesPage.css";

const ConfigureVariablesPage = () => {
    const { variables, variablesModule, setVariablesModule } = useZPL();
    const { setSelectedVars, selectedVars } = useZPL();
    const [selectedSets, setSelectedSets] = useState([]);  // Stores selected sets
    const [selectedParams, setSelectedParams] = useState([]); // Stores selected params
    const [displaySets, setDisplaySets] = useState([]);    // Stores sets that should be displayed
    const [displayParams, setDisplayParams] = useState([]); // Stores params that should be displayed
    const navigate = useNavigate();

    const {
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
        constraintsModules, preferenceModules,
    } = useZPL();


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
                console.log("Added variable " + variable + " to selectedVars");
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
        console.log("Test");
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
        setSetAliases({});
        setParamTypes({});
        setImageId(null);
        setImageName("");
        setImageDescription("");
        setZplCode("");
    };



    return (
        <div className="configure-variables-page background">
            {/* Top Navigation Buttons */}
            {/* Top Navigation Row */}
            <div className="top-bar">
                <div className="top-bar-left">
                    <Link to="/" title="Home" onClick={handleHomeClick}>
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

            </div>
        </div>
    );
};

export default ConfigureVariablesPage;
