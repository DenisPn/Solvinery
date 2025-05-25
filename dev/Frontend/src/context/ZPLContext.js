import React, { createContext, useContext, useState } from "react";

const ZPLContext = createContext();

export const ZPLProvider = ({ children }) => {
    const [constraints, setConstraints] = useState([]);
    const [preferences, setPreferences] = useState([]);
    const [modules, setModules] = useState([]);
    const [preferenceModules, setPreferenceModules] = useState([]);
    const [variables, setVariables] = useState([]);
    const [setTypes, setSetTypes] = useState({});
    const [paramTypes, setParamTypes] = useState({});
    const [imageId, setImageId] = useState(null);
    const [solutionResponse, setSolutionResponse] = useState(null);
    const [variablesModule, setVariablesModule] = useState({
        variablesOfInterest: [],
        variablesConfigurableSets: [],
        variablesConfigurableParams: []
    });
    const [setAliases, setSetAliases] = useState({});

    // Add userId state
    const [userId, setUserId] = useState(null);

    return (
        <ZPLContext.Provider value={{
            constraints, setConstraints,
            preferences, setPreferences,
            modules, setModules,
            preferenceModules, setPreferenceModules,
            variables, setVariables,
            setTypes, setSetTypes,
            paramTypes, setParamTypes,
            imageId, setImageId,
            solutionResponse, setSolutionResponse,
            variablesModule, setVariablesModule,
            setAliases, setSetAliases,
            userId, setUserId, // Provide userId and setUserId
        }}>
            {children}
        </ZPLContext.Provider>
    );
};

export const useZPL = () => useContext(ZPLContext);
