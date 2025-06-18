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
    const [username, setUsername] = useState('');
    const [userId, setUserId] = useState(null);
    const [selectedVars, setSelectedVars] = useState([]); 

    // Add imageName, imageDescription, and zplCode to the context
    const [imageName, setImageName] = useState('');
    const [imageDescription, setImageDescription] = useState('');
    const [zplCode, setZplCode] = useState('');

    const [isEditMode, setIsEditMode] = useState(false);
    const [paramAliases, setParamAliases] = useState({});


    return (
        <ZPLContext.Provider value={{

            //User
            userId, setUserId,
            username, setUsername,


            // Variables
            variables, setVariables,
            selectedVars, setSelectedVars,
            variablesModule, setVariablesModule, //check this one

            // Constraints
            constraints, setConstraints,
            constraintsModules, setConstraintsModules,

            // Preferences
            preferences, setPreferences,
            preferenceModules, setPreferenceModules,

            // Sets
            setTypes, setSetTypes,
            setAliases, setSetAliases,

            // Params
            paramTypes, setParamTypes,
            paramAliases, setParamAliases,

            // Image
            imageId, setImageId,
            imageName, setImageName,
            imageDescription, setImageDescription,
            zplCode, setZplCode,

            // Solution
            solutionResponse, setSolutionResponse,

            // Edit mode
            isEditMode, setIsEditMode,
            
        }}>
            {children}
        </ZPLContext.Provider>
    );
};

export const useZPL = () => useContext(ZPLContext);
