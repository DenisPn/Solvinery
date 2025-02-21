import React, { createContext, useContext, useState,useEffect } from "react";
console.log("🔄 ZPLContext.js is re-rendering!");

const ZPLContext = createContext();

export const ZPLProvider = ({ children }) => {
    console.log("🔄 ZPLContext.js is re-rendering!");

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


    useEffect(() => {
        console.log("🔍 Context Updated !!! : setAliases =", setAliases);
    }, [setAliases]);

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
        }}>
            {children}
        </ZPLContext.Provider>
    );
};

export const useZPL = () => useContext(ZPLContext);
