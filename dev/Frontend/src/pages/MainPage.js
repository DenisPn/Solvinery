import React from 'react';
// Corrected import statement: Add Link back
import { Link, useNavigate } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import './MainPage.css';
import '../Themes/MainTheme.css';

const MainPage = () => {
    const { username, setUserId } = useZPL();
    const navigate = useNavigate();

    const handleLogout = () => {
        setUserId("");
        navigate("/log-in");
    };

    const handleCreateNewImage = () => {
        // Display the confirmation dialog
        if (window.confirm("Creating image requires understanding of the ZPL model. Continue?")) {
            // If the user clicks "OK", navigate to the upload page
            navigate("/upload-zpl");
        }
        // If the user clicks "Cancel", do nothing
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

                {/* These Links now have their component definition imported */}
                <Link to="/my-images" className="button">My Images</Link>
                <Link to="/view-images" className="button">Public Images</Link>

                {/* This remains a button to handle the confirmation logic */}
                <button onClick={handleCreateNewImage} className="button">
                    Create New Image
                </button>
            </div>
        </div>
    );
};

export default MainPage;