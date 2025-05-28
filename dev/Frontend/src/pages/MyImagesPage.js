import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import '../Themes/MainTheme.css';
import './MyImagesPage.css';

const MyImagesPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [images, setImages] = useState([]);
  const navigate = useNavigate();

  // Fetch images on component mount
  useEffect(() => {
    const fetchImages = async () => {
      try {
        const response = await axios.get("/images"); // Relative path
        setImages(response.data);
      } catch (error) {
        console.error("Error fetching images:", error);
      }
    };

    fetchImages();
  }, []);

  // Handle search query change
  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  // Handle the back button click
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
            images
              .filter(image =>
                image.name.toLowerCase().includes(searchQuery.toLowerCase())
              )
              .map((image, index) => (
                <div key={index} className="image-item">
                  <img
                    src={image.url}
                    alt={image.name}
                    className="image-thumbnail"
                  />
                </div>
              ))
          )}
        </div>
      </div>
    </div>
  );
};

export default MyImagesPage;
