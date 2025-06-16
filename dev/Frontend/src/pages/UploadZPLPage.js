import React, { useState, useRef } from "react";
import axios from "axios";
import { useZPL } from "../context/ZPLContext";
import { useNavigate, Link } from "react-router-dom";
import "./UploadZPLPage.css";
import "../Themes/MainTheme.css";

const UploadZPLPage = () => {
  const {
    setVariables,
    setConstraints,
    setPreferences,
    setSetTypes,
    setParamTypes,
    setZplCode,
    userId
  } = useZPL();

  const {
    setSelectedVars,
    setVariablesModule,
    setConstraintsModules,
    setPreferenceModules,
    setSetAliases,
    setImageId,
    setImageName,
    setImageDescription,
  } = useZPL();


  const [fileContent, setFileContent] = useState("");
  const [message, setMessage] = useState("");

  const navigate = useNavigate();
  const fileInputRef = useRef(null);

  const handleSelectFile = () => {
    fileInputRef.current.click();
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
    const requestData = { code: fileContent };

    try {
      const response = await axios.post(`/user/${userId}/image/model`, requestData, {
        headers: { "Content-Type": "application/json" },
      });

      const responseData = response.data;
      setZplCode(fileContent);
      setVariables(responseData.variables);
      setConstraints(responseData.constraints);
      setPreferences(responseData.preferences);
      setSetTypes(responseData.setTypes);
      setParamTypes(responseData.paramTypes);

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

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();

    const file = e.dataTransfer.files[0];
    if (file && (file.name.endsWith(".txt") || file.name.endsWith(".zpl"))) {
      const reader = new FileReader();
      reader.onload = (event) => {
        setFileContent(event.target.result);
      };
      reader.readAsText(file);
    } else {
      alert("Please drop a valid .zpl or .txt file.");
    }
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.stopPropagation();
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
    <div className="upload-zpl-page background">
      {/* Home Button */}
      <div className="top-left-buttons">
        <Link to="/" title="Home" onClick={handleHomeClick}>
          <img
            src="/images/HomeButton.png"
            alt="Home"
            className="home-button-image"
          />
        </Link>
      </div>

      <div className="upload-container">

        <h1 className="page-title">Upload ZPL File</h1>
        <br />

        <div
          className="drop-zone"
          onDrop={handleDrop}
          onDragOver={handleDragOver}
        >
          <p>Drag & drop your ZPL file here</p>
        </div>


        <button className="button" onClick={handleSelectFile}>
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
          rows={15}
          cols={60}
          className="zpl-textarea"
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
