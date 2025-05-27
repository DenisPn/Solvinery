import React, { useState } from 'react';
import { useZPL } from '../context/ZPLContext';
import { Link, useNavigate } from 'react-router-dom';
import './ImageSettingReview.css'; // Assuming you have the corresponding CSS file

const ImageSettingReview = () => {
    const { zplCode } = useZPL();  // Access zplCode from context
    const [imageName, setImageName] = useState('');
    const [imageDescription, setImageDescription] = useState('');
    const [isZPLVisible, setIsZPLVisible] = useState(false);
    const [isImageSaved, setIsImageSaved] = useState(false);
    const navigate = useNavigate();

    // Handle showing ZPL Code modal
    const handleShowZPLCode = () => {
        setIsZPLVisible(true);
    };

    // Handle saving image
    const handleSaveImage = () => {
        setIsImageSaved(true);
    };

    // Handle closing "saved" confirmation modal
    const handleCloseSaveModal = () => {
        setIsImageSaved(false);
        navigate('/'); // Go to home after saving
    };

    return (
        <div className="image-setting-review-page background">
            <h1>Image Setting Review</h1>
            <div className="input-container">
                <label>Image Name:</label>
                <input
                    type="text"
                    value={imageName}
                    onChange={(e) => setImageName(e.target.value)}
                    placeholder="Enter image name"
                />
            </div>
            <div className="input-container">
                <label>Image Description:</label>
                <input
                    type="text"
                    value={imageDescription}
                    onChange={(e) => setImageDescription(e.target.value)}
                    placeholder="Enter image description"
                />
            </div>

            <div className="button-container">
                <button className="image-setting-button" onClick={handleShowZPLCode}>Show ZPL Code for this Image</button>
                <button className="image-setting-button" onClick={handleSaveImage}>Save Image</button>
            </div>

            {/* Modal for showing ZPL Code */}
            {isZPLVisible && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>ZPL Code</h2>
                        <textarea
                            value={zplCode}
                            readOnly
                            rows="10"
                            cols="50"
                            style={{ width: '100%', resize: 'none' }}
                        />
                        <button onClick={() => setIsZPLVisible(false)}>Close</button>
                    </div>
                </div>
            )}

            {/* Modal for Save Image Confirmation */}
            {isImageSaved && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h2>Your image has been saved!</h2>
                        <p>You can see it under the "My Images" page.</p>
                        <button onClick={handleCloseSaveModal}>Back to Home</button>
                    </div>
                </div>
            )}

            <div className="navigation-buttons">
                <Link to="/image-setting-set-and-params" className="back-button">
                    Back
                </Link>
                <Link to="/" className="quit-button">
                    Quit
                </Link>
            </div>
        </div>
    );
};

export default ImageSettingReview;
