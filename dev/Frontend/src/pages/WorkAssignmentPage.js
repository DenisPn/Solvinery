import React from 'react';
import { Link } from 'react-router-dom';
import './WorkAssignmentPage.css';

const WorkAssignmentPage = () => {
    return (
        <div className="work-assignment-page">
            <h1 className="page-title">Solvinery</h1>
            <div className="button-container">
                <button className="main-button">Set times</button>
                <button className="main-button">Set positions</button>
                <button className="main-button">Set participants</button>
                <button className="main-button">Continute</button>
            </div>
            {/* Back Button */}
            <Link to="/main-page" className="back-button">
                Back
            </Link>
        </div>
    );
};

export default WorkAssignmentPage;
