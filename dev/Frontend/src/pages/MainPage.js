import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext'; 
import './MainPage.css';
import '../Themes/MainTheme.css';

const MainPage = () => {
    const { username,setUserId } = useZPL(); 
    const navigate = useNavigate(); 

    const handleLogout = () => {
        setUserId(""); 
        navigate("/log-in");
    };

    return (
        <div className="main-page background">
            
            <div className="logout-button">
                <button className="back-button" onClick={handleLogout}>
                    Log Out
                </button>
            </div>
            <div className="button-container">
                <h1 className="main-title">Solvinery</h1>
                
                <Link to="/my-images" className="button">My Images</Link>
                <Link to="/view-images" className="button">Public Images</Link>
                <Link to="/upload-zpl" className="button">Create New Image</Link>
            </div>
        </div>
    );
};

export default MainPage;
