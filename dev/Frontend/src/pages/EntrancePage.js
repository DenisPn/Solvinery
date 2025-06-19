import React from 'react';
import { Link } from 'react-router-dom';
import './EntrancePage.css';
import '../Themes/MainTheme.css';

const EntrancePage = () => {
  return (
    <div className="entrance-page background">
      <div className="entrance-frame">
        <h1 className="entrance-title">Welcome to Solvinery</h1>
        <div className="entrance-button-group">
          <Link to="/log-in" className="entrance-button">
            Log In
          </Link>
          <Link to="/register" className="entrance-button">
            Register
          </Link>
        </div>
      </div>
    </div>
  );
};

export default EntrancePage;
