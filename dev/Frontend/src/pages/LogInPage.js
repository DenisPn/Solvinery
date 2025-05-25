import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import '../Themes/MainTheme.css';
import './LogInPage.css';

const LogInPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState(""); // 👈 New state
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const requestData = { userName: username, password: password };

      const response = await axios.post("/login", requestData, {
        headers: { "Content-Type": "application/json" },
      });

      console.log("Login successful:", response.data);
      setErrorMessage(""); // Clear any previous error
      navigate("/");

    } catch (error) {
      if (error.response) {
        // Server responded but with a non-2xx status
        const errorMsg = error.response.data?.msg || error.response.data?.message || "Unknown error occurred";
        setErrorMessage(`Server Error: ${error.response.status} - ${errorMsg}`);
      } else if (error.request) {
        // Request was made but no response received
        setErrorMessage("Network Error: No response from server. Please check your internet connection or if the backend is running.");
      } else {
        // Something went wrong while setting up the request
        setErrorMessage(`Error: ${error.message}`);
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
              value={username}
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
