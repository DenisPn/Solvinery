import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useZPL } from "../context/ZPLContext";  // Import the context
import '../Themes/MainTheme.css';
import './LogInPage.css';

const LogInPage = () => {
  const [userName, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState(""); 
  const { setUserId } = useZPL();  // Access setUserId from context
  const navigate = useNavigate();

  const handleLogin = async () => {
  try {
  // Log the data before sending the request
  console.log("Sending login request with username:", userName, "and password:", password);

  const requestData = { userName, password };

  // Use POST for sending login data to the updated backend endpoint
  const response = await axios.post("/users/session", requestData, {
    headers: { "Content-Type": "application/json" }, // Ensure content-type is set for JSON
  });

  // Log the URL of the POST request to verify if it's correct
  console.log("Generated URL:", response.config.url);

  // Log the response data to see the result from the server
  console.log("Login successful:", response.data);

  if (response.data.userId) {
    // Save the userId in the context
    setUserId(response.data.userId);
    setUsername(response.data.username);
    setErrorMessage(""); // Clear any previous error
    console.log(response.data.userId);
    navigate("/"); // Redirect to home page or dashboard
  } else {
    setErrorMessage("Invalid username or password.");
  }

} catch (error) {
  // Handle different types of errors and log appropriate messages
  if (error.response) {
    // Server responded but with a non-2xx status
    const errorMsg = error.response.data?.msg || error.response.data?.message || "Unknown error occurred";
    setErrorMessage(`Server Error: ${error.response.status} - ${errorMsg}`);
    console.log("Server error:", error.response.data); // Log server error details
  } else if (error.request) {
    // Request was made but no response received
    setErrorMessage("Network Error: No response from server. Please check your internet connection or if the backend is running.");
    console.log("Request was made, but no response received:", error.request);
  } else {
    // Something went wrong while setting up the request
    setErrorMessage(`Error: ${error.message}`);
    console.log("Request setup error:", error.message); // Log the error message
  }
}


  };

  return (
    <div className="background">
      {/* Register Button */}
      <button className="back-button" onClick={() => navigate("/register")}>Register</button>

      <div className="form-container">
        <h1 className="main-login-title">Log In</h1>

        <form>
          <div className="group">
            <input
              type="text"
              placeholder="User name"
              value={userName}
              onChange={(e) => setUsername(e.target.value)}
              className="input-box-login"
            />
            <span className="highlight-login"></span>
            <span className="bar-login"></span>
          </div>
          <br />
          <div className="group">
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input-box-login"
            />
            <span className="highlight-login"></span>
            <span className="bar-login"></span>
          </div>
        </form>

        <br />
        {errorMessage && (
          <div className="error-message">{errorMessage}</div>
        )}
        <br />
        <button className="button" onClick={handleLogin}>Log In</button>
      </div>
    </div>
  );
};

export default LogInPage;
