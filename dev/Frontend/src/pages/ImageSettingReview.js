import React, { useState } from 'react';
import { useZPL } from "../context/ZPLContext";  // Import context to get ZPL data
import { useNavigate } from 'react-router-dom';
import './ImageSettingReview.css';  // Your custom styles

const ImageSettingReview = () => {
  const { userId, imageId, zplCode } = useZPL();  // Destructure the context for userId, imageId, and zplCode
  const [imageName, setImageName] = useState("");
  const [imageDescription, setImageDescription] = useState("");
  const [showZplModal, setShowZplModal] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");  // To hold the error message in case of a failed request
  const navigate = useNavigate();

  // Handle the "Save Image" functionality (Updated to PATCH)
  const handleSaveImage = async () => {
    try {
      const response = await fetch(`/user/${userId}/image/${imageId}/publish`, {
        method: "PATCH",  // Changed from POST to PATCH
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          imageName,
          imageDescription,
        }),
      });

      if (response.ok) {
        alert("Your image has been saved!");
        navigate("/");  // Redirect to home page or wherever necessary
      } else {
        // If the response is not ok, get the error message from the response
        const errorData = await response.json();
        setErrorMessage(errorData.msg || "Unknown error occurred while saving the image.");
        alert(`Error: ${errorData.msg || "Unknown error occurred."}`);
      }
    } catch (error) {
      console.error("Error:", error);
      setErrorMessage(error.message || "An unexpected error occurred while saving the image.");
      alert(`Error: ${error.message || "An unexpected error occurred."}`);
    }
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

      {/* Show ZPL Code Button */}
      <button className="show-zpl-button" onClick={() => setShowZplModal(true)}>
        Show ZPL Code for this image
      </button>

      {/* Save Image Button */}
      <button className="save-image-button" onClick={handleSaveImage}>
        Save Image
      </button>

      {/* Modal for ZPL Code */}
      {showZplModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>ZPL Code</h2>
            <textarea
              readOnly
              value={zplCode || ""}
              rows="10"
              cols="80"
              style={{ width: "100%", resize: "none" }}
            ></textarea>
            <button className="close-modal-button" onClick={() => setShowZplModal(false)}>
              Close
            </button>
          </div>
        </div>
      )}

      <button className="back-button" onClick={() => navigate("/image-setting-set-and-params")}>
        Back
      </button>
    </div>
  );
};

export default ImageSettingReview;
