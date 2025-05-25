import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import '../Themes/MainTheme.css';
import './ViewImagesPage.css';  // Updated CSS file for ViewImagesPage

const ViewImagesPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [images, setImages] = useState([]); // This will store the fetched images (for now it's empty)
  const navigate = useNavigate();

  // Handle search query change
  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  // Handle the back button click to navigate to the previous page
  const handleBack = () => {
    navigate("/");
  };

  return (
    <div className="view-images-background">
      <button className="back-button" onClick={handleBack}>Back</button>

      <div className="view-images-form-container">
        <h1 className="main-view-images-title">View Images</h1>

        {/* Search Bar */}
        <div className="group">
          <input
            type="text"
            placeholder="Search Images"
            value={searchQuery}
            onChange={handleSearchChange}
            className="input-box-view-images"
          />
          <span className="highlight-view-images"></span>
          <span className="bar-view-images"></span>
        </div>

        <br />

        {/* Display images section */}
        <div className="images-section">
          {images.length === 0 ? (
            <p>No images available.</p>
          ) : (
            images.map((image, index) => (
              <div key={index} className="image-item">
                {/* Display image thumbnail */}
                <img src={image.url} alt={`Image ${index}`} className="image-thumbnail" />
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default ViewImagesPage;
