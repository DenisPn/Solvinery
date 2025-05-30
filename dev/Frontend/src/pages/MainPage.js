import React from 'react';
import { Link } from 'react-router-dom';
import './MainPage.css';
import '../Themes/MainTheme.css'

const MainPage = () => {
    return (
        <div className="main-page background">
            <div className="logout-button">
            <Link to="/log-in" className="back-button">
                            Log Out
                        </Link>
            </div>
            <div className="button-container">
            <h1 className="main-title">Solvinery</h1>
                <Link to="/my-images" className="button">
            
                    My Images
                   
                </Link>
        
                <Link to="/view-images" className="button">
                    Public Images
                </Link>
                <Link to="/upload-zpl" className="button">
                    Create New Image
                </Link>
            </div>
        </div>
    );
};

export default MainPage;
