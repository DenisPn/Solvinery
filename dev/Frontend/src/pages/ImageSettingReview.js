import React, { useEffect, useState } from "react";
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
    imageId,
  } = useZPL();

  useEffect(() => {
    console.log('paramAliases in context:', paramAliases);
  }, [paramAliases]);

  const [isZplCodeVisible, setIsZplCodeVisible] = useState(false);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

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
    setIsEditMode(false);
  };


  const handleShowZplCode = () => setIsZplCodeVisible(v => !v);

  const handleSaveImage = async () => {
    // Validate name & description
    if (!imageName.trim() || !imageDescription.trim()) {
      alert("Please enter both an image name and a description.");
      return;
    }
    setLoading(true);
    const requestData = {
      variables: selectedVars.map(variable => {
        const struct = Array.isArray(variable.structure)
          ? variable.structure
          : (variable.structure || "").split(",").map(s => s.trim()).filter(Boolean);

        return {
          identifier: variable.identifier,
          structure: struct,
          alias: variable.alias || variable.identifier,
          objectiveValueAlias: variable.objectiveValueAlias || "",
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

        const { alias: userAlias, typeAlias: userTypeAlias = [] } = setAliases[setName] || {};

        return {
          setDefinition: {
            name: setName,
            alias: userAlias || setName,
            structure: userTypeAlias.length ? userTypeAlias : typeArray,
          },
          values: [],
        };
      }),

      parameters: Object.entries(paramTypes).map(([paramName, rawType]) => {
        const structString = Array.isArray(rawType) ? rawType.join(",") : rawType;
        const { alias: userParamAlias } = paramAliases[paramName] || {};

        return {
          parameterDefinition: {
            name: paramName,
            structure: structString,
            alias: userParamAlias || "",
          },
          value: "",
        };
      }),

      name: imageName,
      description: imageDescription,
      code: zplCode,
    };

    console.log("Request Data:", requestData);

    const API_URL = process.env.REACT_APP_API_URL;
    const baseUrl = `/user/${userId}/image${isEditMode ? `/${imageId}` : ""}`;
    const url = isEditMode ? `${baseUrl}?ignoreData=true` : baseUrl;
    const method = isEditMode ? "PATCH" : "POST";
    console.log("Request URL:", url);
    console.log("Test1");

    try {
      const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData),
        credentials: "include"
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
      setIsEditMode(false);

      if (!isEditMode) {
        navigate("/main-page");
      }
      else {
        navigate("/my-images");
      }
    } catch (e) {
      console.error(e);
      alert(`Error: ${e.message}`);
    } finally {
      setLoading(false);
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
      {loading && (
        <div className="modal-overlay" style={{ zIndex: 9999 }}>
          <div className="spinner-modal">
            <div className="spinner" />
            <p className="loading-label">Loading…</p>
          </div>
        </div>
      )}
      <div className="image-setting-top-left-buttons">
        <Link to="/main-page" title="Home" onClick={handleHomeClick}>
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
        <button
          onClick={handleShowZplCode}
          className="show-zpl-button"
          title="Show ZPL Code"
        >
          <img
            src="/images/CopyZPLButton.png"
            alt="Show ZPL Code"
            className="show-zpl-icon"
          />
        </button>
      </div>

      <div className="image-details">
        <label>Image Name</label>
        <input
          type="text"
          value={imageName}
          onChange={e => setImageName(e.target.value)}
          placeholder="Enter image name"
        />
        <div className="description-group">
          <label htmlFor="image-description">Image Description</label>

          <textarea
            id="image-description"
            value={imageDescription}
            onChange={e => setImageDescription(e.target.value)}
            placeholder="Enter image description"
            maxLength={4000}
          />

          <span className="char-limit">Maximum&nbsp;4000&nbsp;chars</span>
        </div>

      </div>


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
