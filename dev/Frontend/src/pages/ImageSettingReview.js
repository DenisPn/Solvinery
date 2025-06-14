import React, { useState } from "react";
import { useZPL } from "../context/ZPLContext"; // now includes aliases
import { useNavigate, Link } from "react-router-dom";
import "./ImageSettingReview.css";

const ImageSettingReview = () => {
  const {
    selectedVars,
    userId,
    zplCode,
    setTypes,
    setAliases,     // newly pulled-in aliases map
    paramTypes,
    paramAliases,   // newly pulled-in parameter aliases map
    constraintsModules,
    preferenceModules,
  } = useZPL();

  const [imageName, setImageName] = useState("");
  const [imageDescription, setImageDescription] = useState("");
  const [isZplCodeVisible, setIsZplCodeVisible] = useState(false);
  const navigate = useNavigate();

  // Toggle ZPL code visibility
  const handleShowZplCode = () => {
    setIsZplCodeVisible(!isZplCodeVisible);
  };

  // Post image data to server
  const handleSaveImage = async () => {
    const requestData = {
      variables: selectedVars.map(variable => ({
        identifier: variable.identifier,
        structure: variable.structure,
        alias: variable.alias || variable.identifier,
      })),
      constraintModules: constraintsModules.map(module => ({
        moduleName: module.name,
        description: module.description,
        constraints: module.constraints.map(c => c.identifier),
      })),
      preferenceModules: preferenceModules.map(module => ({
        moduleName: module.name,
        description: module.description,
        preferences: module.preferences.map(p => p.identifier),
      })),

      // Use actual aliases from context
      sets: Object.entries(setTypes).map(([setName, rawType]) => {
        const typeArray = Array.isArray(rawType)
          ? rawType
          : rawType.split(",").map(s => s.trim());

        const { alias = setName, typeAlias = [] } = setAliases[setName] || {};

        return {
          setDefinition: {
            name: setName,
            type: typeArray,
            alias,           // real alias
            typeAlias,       // real typeAlias
          },
          values: [],
        };
      }),

      parameters: Object.entries(paramTypes).map(([paramName, rawType]) => {
        const typeArray = Array.isArray(rawType)
          ? rawType
          : rawType.split(",").map(s => s.trim());

        const { alias = paramName, typeAlias = [] } = paramAliases[paramName] || {};

        return {
          parameterDefinition: {
            name: paramName,
            type: typeArray,
            alias,         // real alias
            typeAlias,     // real typeAlias
          },
          value: "",
        };
      }),

      name: imageName,
      description: imageDescription,
      code: zplCode,
    };

    try {
      console.log("Request Data:", requestData);
      const response = await fetch(`/user/${userId}/image`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData),
      });

      if (!response.ok) {
        const errorMsg = await response.text();
        alert(`Failed to save image. Error: ${errorMsg || "Unknown error"}`);
        return;
      }

      const data = await response.json();
      console.log("Image saved successfully:", data);
      alert("Image saved successfully!");
      navigate("/");
    } catch (error) {
      console.error("Error saving image:", error);
      alert(`Error: ${error.message}`);
    }
  };

  // Copy ZPL to clipboard
  const handleCopyToClipboard = async () => {
    try {
      await navigator.clipboard.writeText(zplCode);
      alert("ZPL Code copied to clipboard!");
    } catch (error) {
      console.error("Failed to copy to clipboard:", error);
      alert("Failed to copy the ZPL code.");
    }
  };

  return (
    <div className="image-setting-page background">
      {/* Top Left Buttons */}
      <div className="image-setting-top-left-buttons">
        <Link to="/" title="Go to Home">
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

      {/* Image Name and Description Fields */}
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

      {/* Show ZPL Code Button */}
      <button onClick={handleShowZplCode} className="show-zpl-button">
        Show ZPL Code for this Image
      </button>

      {isZplCodeVisible && (
        <div className="zpl-code-modal">
          <div className="modal-content">
            <button
              className="copy-button"
              onClick={handleCopyToClipboard}
            >
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

      {/* Back Button */}
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
