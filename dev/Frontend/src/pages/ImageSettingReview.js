import React, { useEffect,useState } from "react";
import { useZPL } from "../context/ZPLContext";
import { useNavigate, Link } from "react-router-dom";
import "./ImageSettingReview.css";

const ImageSettingReview = () => {
  const {
    selectedVars,
    userId,
    zplCode,
    setTypes,
    setAliases,
    paramTypes,
    paramAliases,
    constraintsModules,
    preferenceModules,
    imageName,
    imageDescription,
    setVariables,
    setSelectedVars,
    setVariablesModule,
    setConstraints,
    setConstraintsModules,
    setPreferences,
    setPreferenceModules,
    setSetTypes,
    setSetAliases,
    setParamTypes,
    setImageId,
    setImageName,
    setImageDescription,
    setZplCode,
    isEditMode,
    setIsEditMode,
    imageId
  } = useZPL();

   useEffect(() => {
    console.log('paramAliases in context:', paramAliases);
  }, [paramAliases]);

  const [isZplCodeVisible, setIsZplCodeVisible] = useState(false);
  const navigate = useNavigate();

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


  const handleShowZplCode = () => setIsZplCodeVisible(v => !v);

  const handleSaveImage = async () => {
    const requestData = {


      variables: selectedVars.map(variable => {
        // Ensure structure is always an array:
        const struct = Array.isArray(variable.structure)
          ? variable.structure
          : (variable.structure || "").split(",").map(s => s.trim()).filter(Boolean);

        return {
          identifier: variable.identifier,
          structure: struct,  // a real array
          alias: variable.alias || variable.identifier,
        };
      }),





      constraintModules: constraintsModules.map(mod => ({
        moduleName: mod.name,
        description: mod.description,
        constraints: mod.constraints.map(c => c.identifier),
      })),
      preferenceModules: preferenceModules.map(mod => ({
        moduleName: mod.name,
        description: mod.description,
        preferences: mod.preferences.map(p => p.identifier),
      })),

      sets: Object.entries(setTypes).map(([setName, rawType]) => {
        const typeArray = Array.isArray(rawType)
          ? rawType
          : rawType.split(",").map(s => s.trim());

        // Always default alias to the setName
        const { alias: userAlias, typeAlias: userTypeAlias = [] } =
          setAliases[setName] || {};

        return {
          setDefinition: {
            name: setName,
            alias: userAlias || setName,
            structure: userTypeAlias.length ? userTypeAlias : typeArray,
          },
          values: [],
        };
      }),

      // …
      parameters: Object.entries(paramTypes).map(([paramName, rawType]) => {
        // normalize the structure to a string
        const structString = Array.isArray(rawType)
          ? rawType.join(",")
          : rawType;

        // **grab the user‐entered alias from paramAliases**
        const { alias: userParamAlias } = paramAliases[paramName] || {};

        return {
          parameterDefinition: {
            name: paramName,
            structure: structString,
            // if they never set an alias, send empty string (or omit if you prefer)
            alias: userParamAlias || ""
          },
          value: ""
        };
      }),
      // …




      name: imageName,
      description: imageDescription,
      code: zplCode,
    };

    console.log("Request Data:", requestData);

    const url = isEditMode
      ? `/user/${userId}/image/${imageId}`
      : `/user/${userId}/image`;
    const method = isEditMode ? "PATCH" : "POST";

    try {
      const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData),
      });
      if (!response.ok) {
        const err = await response.text();
        alert(`Failed to ${isEditMode ? "update" : "save"} image. Error: ${err || "Unknown error"}`);
        return;
      }
      alert(`Image ${isEditMode ? "updated" : "saved"} successfully!`);

      // Reset everything
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

      // Turn off edit mode if it was on
      if (isEditMode) {
        setIsEditMode(false);
      }

      navigate("/");
    } catch (e) {
      console.error(e);
      alert(`Error: ${e.message}`);
    }
  };

  const handleCopyToClipboard = async () => {
    try {
      await navigator.clipboard.writeText(zplCode);
      alert("ZPL Code copied!");
    } catch {
      alert("Copy failed.");
    }
  };

  return (
    <div className="image-setting-page background">
      <div className="image-setting-top-left-buttons">
        <Link to="/" title="Home" onClick={handleHomeClick}>
          <img
            src="/images/HomeButton.png"
            alt="Home"
            className="image-setting-home-button"
          />
        </Link>
        <img
          src="/images/SaveButton.png"
          alt="Save"
          className="image-setting-save-button"
          onClick={handleSaveImage}
          title="Save Image"
        />
      </div>

      <h1 className="page-title">Image Setting: Sets and Parameters</h1>
      <div className="image-details">
        <label>Image Name</label>
        <input
          type="text"
          value={imageName}
          onChange={e => setImageName(e.target.value)}
          placeholder="Enter image name"
        />
        <label>Image Description</label>
        <textarea
          value={imageDescription}
          onChange={e => setImageDescription(e.target.value)}
          placeholder="Enter image description"
        />
      </div>

      <button onClick={handleShowZplCode} className="show-zpl-button">
        Show ZPL Code for this Image
      </button>
      {isZplCodeVisible && (
        <div className="zpl-code-modal">
          <div className="modal-content">
            <button className="copy-button" onClick={handleCopyToClipboard}>
              Copy code to clipboard
            </button>
            <button
              className="close-button"
              onClick={() => setIsZplCodeVisible(false)}
            >
              ×
            </button>
            <h2>ZPL Code</h2>
            <pre>{zplCode}</pre>
          </div>
        </div>
      )}

      <Link to="/image-setting-set-and-params" title="Back">
        <img
          src="/images/RightArrowButton.png"
          alt="Back"
          className="image-setting-back-button"
        />
      </Link>
    </div>
  );
};

export default ImageSettingReview;
