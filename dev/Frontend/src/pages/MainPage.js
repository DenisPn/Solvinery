import React from 'react';
import { Link } from 'react-router-dom';
import './MainPage.css';

const MainPage = () => {
    return (
        <div className="main-page">
            <h1 className="main-title">Plan A</h1>
            <div className="button-container">
                <Link to="/my-images" className="footer-button">
                    My Images
                </Link>
                <Link to="/view-images" className="footer-button">
                    View Images
                </Link>
                <Link to="/upload-zpl" className="footer-button">
                    Create New Image
                </Link>
            </div>
        </div>
    );
};

export default MainPage;
