import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./LogInPage.css"; // We'll style it later

const LogInPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = () => {
    // TODO: Implement login logic
    console.log("Login button clicked!");
  };

  return (
    <div className="login-container">
      {/* Back Button */}
      <button className="back-button" onClick={() => navigate("/main")}>Back</button>
      
      <h1>Log In</h1>

      {/* Username Input */}
      <input
        type="text"
        placeholder="User name"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        className="input-box"
      />
      
      {/* Password Input */}
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="input-box"
      />
      
      {/* Login Button */}
      <button className="login-button" onClick={handleLogin}>Log In</button>
    </div>
  );
};

export default LogInPage;
