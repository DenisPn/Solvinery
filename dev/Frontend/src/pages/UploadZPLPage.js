import React, { useState, useRef } from "react";
import axios from "axios";
import { useZPL } from "../context/ZPLContext"; // Import the context to get userId
import { useNavigate } from "react-router-dom";
import "./UploadZPLPage.css";
import "../Themes/MainTheme.css";

const UploadZPLPage = () => {
  const {
    imageId, setImageId,
    variables, setVariables,
    setTypes, setSetTypes,
    paramTypes, setParamTypes,
    constraints, setConstraints,
    preferences, setPreferences,
    userId // Destructure userId from context
  } = useZPL();

  const [fileContent, setFileContent] = useState("");
  const [message, setMessage] = useState("");

  const navigate = useNavigate();
  const fileInputRef = useRef(null); // 👈 Ref to the hidden file input

  const handleSelectFile = () => {
    fileInputRef.current.click(); // 👈 Trigger the file input click
  };

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (e) => {
      setFileContent(e.target.result);
    };
    reader.readAsText(file);
  };

  const handleUpload = async () => {
    const requestData = {
      code: fileContent,
      userId: userId // Add userId from context to the request payload
    };

    try {
      const response = await axios.post("/images/image", requestData, {
        headers: { "Content-Type": "application/json" },
      });

      const responseData = response.data;
      setImageId(responseData.imageId);
      setVariables(responseData.model.variables);
      setConstraints(responseData.model.constraints);
      setPreferences(responseData.model.preferences);
      setSetTypes(responseData.model.setTypes);
      setParamTypes(responseData.model.paramTypes);

      console.log("Full Response Data:", responseData);

      setMessage("File uploaded successfully!");
      navigate("/configure-variables");
    } catch (error) {
      if (error.response) {
        const errorMsg = error.response.data?.msg || "Unknown error occurred";
        setMessage(`Error: ${error.response.status} - ${errorMsg}`);
      } else if (error.request) {
        setMessage("Error: No response from server. Check if backend is running.");
      } else {
        setMessage(`Error: ${error.message}`);
      }
    }
  };

  return (
    <div className="upload-zpl-page background">
      <div className="upload-container">
        <h1 className="page-title">Upload ZPL File</h1>
        <br />
        <button className="upload-button" onClick={handleSelectFile}>
          Select File
        </button>
        <input
          type="file"
          ref={fileInputRef}
          style={{ display: "none" }}
          accept=".zpl,.txt"
          onChange={handleFileChange}
        />
        <br />
        <textarea
          value={fileContent}
          onChange={(e) => setFileContent(e.target.value)}
          rows={20}
          cols={80}
        />
        <br />
        <button className="button" onClick={handleUpload}>
          Upload
        </button>
      </div>
      {message && <p className="upload-message">{message}</p>}
    </div>
  );
};

export default UploadZPLPage;
