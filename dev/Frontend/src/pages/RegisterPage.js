import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import '../Themes/MainTheme.css';
import './RegisterPage.css';

const RegisterPage = () => {
  const [userName, setUserName] = useState("");
  const [nickname, setNickname] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState(""); // Renamed to message to handle both error and success messages
  const navigate = useNavigate();

  const handleRegister = async () => {
    if (password !== confirmPassword) {
      setMessage("Passwords do not match.");
      return;
    }

    try {
      const requestData = { userName, nickname, password, email };

      // Send the POST request to "/users/user"
      const response = await axios.post("/users", requestData, {
        headers: { "Content-Type": "application/json" },
      });

      console.log("Registration successful:", response.data);
      setMessage("Registration successful!"); // Success message

      // Optionally navigate to another page or reset form fields
      // navigate("/login");

    } catch (error) {
      if (error.response) {
        // Server responded but with a non-2xx status
        const errorMsg = error.response.data?.msg || error.response.data?.message || "Unknown error occurred";
        setMessage(`Server Error: ${error.response.status} - ${errorMsg}`); // Error message
      } else if (error.request) {
        // Request was made but no response received
        setMessage("Network Error: No response from server. Please check your internet connection or if the backend is running.");
      } else {
        // Something went wrong while setting up the request
        setMessage(`Error: ${error.message}`);
      }
    }
  };

  return (
    <div className="register-background">
      <button className="back-button" onClick={() => navigate("/log-in")}>Log In</button>

      <div className="register-form-container">
        <h1 className="main-register-title">Register</h1>

       <form className="form-grid">
  <div className="group">
    <input
      type="text"
      placeholder="User Name"
      value={userName}
      onChange={(e) => setUserName(e.target.value)}
      className="input-box-register"
    />
  </div>

  <div className="group">
    <input
      type="text"
      placeholder="Nickname"
      value={nickname}
      onChange={(e) => setNickname(e.target.value)}
      className="input-box-register"
    />
  </div>

  <div className="group">
    <input
      type="password"
      placeholder="Password"
      value={password}
      onChange={(e) => setPassword(e.target.value)}
      className="input-box-register"
    />
  </div>

  <div className="group">
    <input
      type="password"
      placeholder="Confirm Password"
      value={confirmPassword}
      onChange={(e) => setConfirmPassword(e.target.value)}
      className="input-box-register"
    />
  </div>

  <div className="group full-width">
    <input
      type="email"
      placeholder="Email"
      value={email}
      onChange={(e) => setEmail(e.target.value)}
      className="input-box-register"
    />
  </div>
</form>


        <br />
        {message && (
          <div className="message">{message}</div> 
        )}
        <br />
        <button className="button" onClick={handleRegister}>Register</button>
      </div>
    </div>
  );
};

export default RegisterPage;
