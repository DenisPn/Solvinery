import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import './ConfigureConstraintsPage.css';

const ConfigureConstraintsPage = () => {
    const navigate = useNavigate();

    const { constraints: jsonConstraints = [], setConstraintsModules = () => { } } = useZPL();
    const {
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
        constraintsModules,
        preferenceModules,
    } = useZPL();

    const [availableConstraints, setAvailableConstraints] = useState([]);
    const [moduleName, setModuleName] = useState('');
    const [selectedModuleIndex, setSelectedModuleIndex] = useState(null);

 useEffect(() => {
  console.log("constraintsModules:", constraintsModules);
  console.log("preferenceModules:", preferenceModules);
}, [constraintsModules, preferenceModules]); // Log when constraintModules or preferenceModules change


    // Initialize available constraints dynamically from JSON
    useEffect(() => {
        setAvailableConstraints(jsonConstraints);
    }, [jsonConstraints]);

    // Add a new module
    const addConstraintModule = () => {
        if (moduleName.trim() !== '') {
            setConstraintsModules((prevModules) => [
                ...prevModules,
                { name: moduleName, description: "", constraints: [] },
            ]);
            setModuleName(""); // Reset the input field
        }
    };

    // Update module description
    const updateModuleDescription = (newDescription) => {
        setConstraintsModules((prevModules) =>
            prevModules.map((module, idx) =>
                idx === selectedModuleIndex ? { ...module, description: newDescription } : module
            )
        );
    };

    // Add constraint to selected module
    const addConstraintToModule = (constraint) => {
        if (selectedModuleIndex === null) {
            alert('Please select a module first!');
            return;
        }

        setConstraintsModules((prevModules) => {
            return prevModules.map((module, idx) => {
                if (idx === selectedModuleIndex) {
                    if (!module.constraints.some(c => c.identifier === constraint.identifier)) {
                        return {
                            ...module,
                            constraints: [...module.constraints, constraint],
                        };
                    }
                }
                return module;
            });
        });

        // Remove constraint from the available list
        setAvailableConstraints((prev) =>
            prev.filter((c) => c.identifier !== constraint.identifier)
        );
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
        <div className="configure-constraints-page background">
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
                        onClick={() => navigate("/configure-preferences")}
                        title="Continue"
                    />
                </div>

                <div className="top-bar-right">
                    <Link to="/configure-variables" title="Back">
                        <img
                            src="/images/RightArrowButton.png"
                            alt="Back"
                            className="top-bar-button"
                        />
                    </Link>
                </div>
            </div>

            <h1 className="page-title">Configure High-Level Constraints</h1>

            <div className="constraints-layout">
                <div className="constraint-modules">
                    <h2>Constraint Modules</h2>
                    <input
                        type="text"
                        placeholder="Module Name"
                        value={moduleName}
                        onChange={(e) => setModuleName(e.target.value)}
                    />
                    <button onClick={addConstraintModule}>Add Constraint Module</button>
                    <div className="module-list">
                        {constraintsModules.map((module, index) => (
                            <div key={index} className="module-item-container">
                                <button
                                    className={`module-item ${selectedModuleIndex === index ? 'selected' : ''}`}
                                    onClick={() => setSelectedModuleIndex(index)}
                                >
                                    {module.name}
                                </button>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="define-constraint-module">
                    <h2>Define Constraint Module</h2>
                    {selectedModuleIndex === null ? (
                        <p>Select a module</p>
                    ) : (
                        <>
                            <h3>{constraintsModules[selectedModuleIndex]?.name || 'Unnamed Module'}</h3>
                            <label>Description:</label>
                            <textarea
                                value={constraintsModules[selectedModuleIndex]?.description || ""}
                                onChange={(e) => updateModuleDescription(e.target.value)}
                                placeholder="Enter module description..."
                                style={{ resize: "none", width: "100%", height: "80px" }}
                            />
                            <p>This module's constraints:</p>
                            <div className="module-drop-area">
                                {constraintsModules[selectedModuleIndex]?.constraints.length > 0 ? (
                                    constraintsModules[selectedModuleIndex].constraints.map((c, i) => (
                                        <div key={i} className="dropped-constraint">
                                            {c.identifier}
                                        </div>
                                    ))
                                ) : (
                                    <p>No constraints added</p>
                                )}
                            </div>
                        </>
                    )}
                </div>

                <div className="available-constraints">
                    <h2>Available Constraints</h2>
                    {availableConstraints.length > 0 ? (
                        availableConstraints.map((constraint, idx) => (
                            <div key={idx} className="constraint-item-container">
                                <button
                                    className="constraint-item"
                                    onClick={() => addConstraintToModule(constraint)}
                                >
                                    {constraint.identifier}
                                </button>
                            </div>
                        ))
                    ) : (
                        <p>No constraints available</p>
                    )}
                </div>
            </div>



        </div>
    );
};

export default ConfigureConstraintsPage;
