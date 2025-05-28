import React, { useState } from "react";
import { useZPL } from "../context/ZPLContext"; // Import the useZPL hook
import { Link } from "react-router-dom";
import "./ImageSettingReview.css"; // Assuming you have your CSS

const ImageSettingReview = () => {
  const { userId, zplCode, setTypes, paramTypes, constraintsModules, preferenceModules } = useZPL(); // Destructure from context
  const [imageName, setImageName] = useState("");
  const [imageDescription, setImageDescription] = useState("");
  const [isZplCodeVisible, setIsZplCodeVisible] = useState(false);

  // Handle Show ZPL Code
  const handleShowZplCode = () => {
    setIsZplCodeVisible(!isZplCodeVisible);
  };

  // Handle Save Image (post to server)
 const handleSaveImage = async () => {
  const requestData = {
    variables: Object.keys(setTypes).map((set) => ({
      identifier: set,
      structure: setTypes[set],
      alias: set, // For simplicity, using set as alias, you can adjust this logic.
    })),
    constraintModules: constraintsModules.map((module) => ({
      moduleName: module.name,
      description: module.description,
      constraints: module.constraints, // Constraints inside each module
    })),
    preferenceModules: preferenceModules.map((module) => ({
      moduleName: module.name,
      description: module.description,
      preferences: module.preferences, // Preferences inside each module
    })),
    sets: Object.keys(setTypes).map((set) => ({
      setDefinition: { name: set, type: setTypes[set] },
      values: [], // Add your logic for values
    })),
    parameters: Object.keys(paramTypes).map((param) => ({
      parameterDefinition: { name: param, type: paramTypes[param] },
      value: "", // Default value, adjust based on your need
    })),
    name: imageName,
    description: imageDescription,
    code: zplCode, // The zpl code from context
  };

  // Send POST request with the data
  try {
    //const response = await fetch("/user/"+userId+"/image+", {
    console.log(userId);
    const response = await fetch(`/user/${userId}/image`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestData),
    });

    if (!response.ok) {
      // If status code is not 2xx, handle the error
      const errorMsg = await response.text(); // Get the error message from response
      alert(`Failed to save image. Error: ${errorMsg || 'Unknown error occurred'}`);
      return;
    }

    const data = await response.json();
    console.log("Image saved successfully:", data);
    alert("Image saved successfully!");
  } catch (error) {
    console.error("Error saving image:", error);
    alert(`Error: ${error.message}`);
  }
};

  return (
    <div className="image-setting-page background">
      <h1 className="page-title">Image Setting: Sets and Parameters</h1>

      {/* Image Name and Description Fields */}
      <div className="image-details">
        <label>Image Name</label>
        <input
          type="text"
          value={imageName}
          onChange={(e) => setImageName(e.target.value)}
          placeholder="Enter image name"
        />
        <label>Image Description</label>
        <textarea
          value={imageDescription}
          onChange={(e) => setImageDescription(e.target.value)}
          placeholder="Enter image description"
        />
      </div>

      {/* Show ZPL Code Button */}
      <button onClick={handleShowZplCode} className="show-zpl-button">
        Show ZPL Code for this Image
      </button>

      {isZplCodeVisible && (
        <div className="zpl-code-modal">
          <h2>ZPL Code</h2>
          <pre>{zplCode}</pre>
        </div>
      )}

      {/* Save Image Button */}
      <button onClick={handleSaveImage} className="save-image-button">
        Save Image
      </button>

      {/* Back Button */}
      <Link to="/solution-preview" className="back-button">Back</Link>
    </div>
  );
};

export default ImageSettingReview;
