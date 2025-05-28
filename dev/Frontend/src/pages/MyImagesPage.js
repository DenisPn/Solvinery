import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useZPL } from "../context/ZPLContext";
import "../Themes/MainTheme.css";
import "./MyImagesPage.css";

const MyImagesPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [images, setImages] = useState([]);
  const [page, setPage] = useState(0);
  const navigate = useNavigate();
  const { userId } = useZPL();

  // Fetch images when page or userId changes
  useEffect(() => {
    if (!userId) return;

    const fetchImages = async () => {
      try {
        console.log("User ID : " + userId);
        console.log("Page : " + page);
        const response = await axios.get(`/user/${userId}/image/${page}`);
        setImages(response.data.images || []);
      } catch (error) {
        console.error("Error fetching images:", error);
      }
    };

    fetchImages();
  }, [userId, page]);

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleBack = () => {
    navigate("/");
  };

  const handleNextPage = () => {
    setPage((prev) => prev + 1);
  };

  const handlePrevPage = () => {
    setPage((prev) => Math.max(prev - 1, 0));
  };

  const filteredImages = images.filter((img) =>
    img.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="my-images-background">
      <button className="back-button" onClick={handleBack}>
        Back
      </button>

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

        {/* Display images */}
        <div className="images-section">
          {filteredImages.length === 0 ? (
            <p>No images available.</p>
          ) : (
            filteredImages.map((image, index) => (
              <div key={index} className="image-item">
                <div className="image-thumbnail-text">
                  <strong>{image.name}</strong>
                  <p>{image.description}</p>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Pagination */}
        <div style={{ marginTop: "20px", display: "flex", gap: "10px" }}>
          <button onClick={handlePrevPage} disabled={page === 0}>
            Previous
          </button>
          <span>Page {page + 1}</span>
          <button onClick={handleNextPage}>Next</button>
        </div>
      </div>
    </div>
  );
};

export default MyImagesPage;
