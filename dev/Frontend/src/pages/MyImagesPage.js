import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import '../Themes/MainTheme.css';
import './MyImagesPage.css';  // New CSS file for MyImagesPage

const MyImagesPage = () => {
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
    <div className="my-images-background">
      <button className="back-button" onClick={handleBack}>Back</button>

      <div className="my-images-form-container">
        <h1 className="main-my-images-title">My Images</h1>

        {/* Search Bar */}
        <div className="group">
          <input
            type="text"
            placeholder="Search My Images"
            value={searchQuery}
            onChange={handleSearchChange}
            className="input-box-my-images"
          />
          <span className="highlight-my-images"></span>
          <span className="bar-my-images"></span>
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

export default MyImagesPage;
