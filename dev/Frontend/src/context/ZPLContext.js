import React, { createContext, useContext, useState } from "react";

const ZPLContext = createContext();

export const ZPLProvider = ({ children }) => {
    const [constraints, setConstraints] = useState([]);
    const [preferences, setPreferences] = useState([]);

    const [constraintsModules, setConstraintsModules] = useState([]);
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
    const [userId, setUserId] = useState(null);
    const [selectedVars, setSelectedVars] = useState([]); 

    return (
        <ZPLContext.Provider value={{

            //variables
            variables, setVariables,
            selectedVars, setSelectedVars,
            variablesModule, setVariablesModule,
            setAliases, setSetAliases,

            //constraints
            constraints, setConstraints,
            constraintsModules, setConstraintsModules,

            //preferences
            preferences, setPreferences,
            preferenceModules, setPreferenceModules,

            //sets and params
            setTypes, setSetTypes,
            paramTypes, setParamTypes,

            //image and user
            imageId, setImageId,
            userId, setUserId,
            
            //solution
            solutionResponse, setSolutionResponse,
        }}>
            {children}
        </ZPLContext.Provider>
    );
};

export const useZPL = () => useContext(ZPLContext);
