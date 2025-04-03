import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import '../Themes/MainTheme.css'
import './LogInPage.css'

const LogInPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = () => {
    // TODO: Implement login logic
    console.log("Login button clicked!");
  };

  return (
    <div className="background">
         {/* Back Button */}
         <button className="back-button" onClick={() => navigate("/")}>Back</button>
      
    <div className="form-container">
   
      <h1 className="main-login-title">Log In</h1>

<form>

  <div class="group">
      {/* Username Input */}
      <input
        type="text"
        placeholder="User name"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        className="input-box-login"
      />
      <span class="highlight-login"></span>
      <span class="bar-login"></span>
      </div>
      <br></br>
      <div class="group">
      {/* Password Input */}
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="input-box-login"
      />
        <span class="highlight-login"></span>
        <span class="bar-login"></span>
</div>
</form>
      <br></br>
      <br></br>
      {/* Login Button */}
      <button className="button" onClick={handleLogin}>Log In</button>
    </div>
    </div>
  );
};

export default LogInPage;
