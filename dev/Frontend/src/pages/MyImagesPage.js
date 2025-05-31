import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useZPL } from "../context/ZPLContext";
import "../Themes/MainTheme.css";
import "./MyImagesPage.css";

const MyImagesPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [imagesMap, setImagesMap] = useState({});
  const [page, setPage] = useState(0);
  const [selectedImage, setSelectedImage] = useState(null);
  const [selectedImageId, setSelectedImageId] = useState(null);
  const [viewSection, setViewSection] = useState(null);

  const navigate = useNavigate();
  const { userId } = useZPL();
  

  useEffect(() => {
    if (!userId) return;

    const fetchImages = async () => {
      try {
        const response = await axios.get(`/user/${userId}/image/${page}`);
        setImagesMap(response.data.images || {});
      } catch (error) {
        console.error("Error fetching images:", error);
      }
    };

    fetchImages();
  }, [userId, page]);

const handlePublishImage = async () => {
  if (!selectedImageId || !userId) return;

  try {
    const response = await axios.patch(`/user/${userId}/image/${selectedImageId}/publish`);
    alert(`Publish Success: ${JSON.stringify(response.data)}`);
  } catch (error) {
    const errMsg = error.response?.data || error.message || "Unknown error";
    alert(`Publish Failed: ${JSON.stringify(errMsg)}`);
  }
};


  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleBack = () => {
    navigate("/");
  };

  const handleNextPage = () => {
    setPage((prev) => prev + 1);
    setSelectedImage(null);
    setSelectedImageId(null);
  };

  const handlePrevPage = () => {
    setPage((prev) => Math.max(prev - 1, 0));
    setSelectedImage(null);
    setSelectedImageId(null);
  };

  const handleCopyCode = () => {
    if (selectedImage?.code) {
      navigator.clipboard.writeText(selectedImage.code).then(() => {
        alert("ZPL code copied to clipboard!");
      }).catch(err => {
        alert("Failed to copy ZPL code.");
      });
    }
  };

  const imageEntries = Object.entries(imagesMap);
  const filteredEntries = imageEntries.filter(([_, img]) =>
    img.name?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="my-images-background">
      <img
        src="/images/HomeButton.png"
        alt="Home"
        className="home-button"
        onClick={handleBack}
        title="Go to Home"
      />

      <div className="my-images-form-container">
        <h1 className="main-my-images-title">My Images</h1>

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

        <div className="images-section">
          {filteredEntries.length === 0 ? (
            <p>No images available.</p>
          ) : (
            filteredEntries.map(([imageId, image]) => (
              <div
                key={imageId}
                className="image-item clickable"
                onClick={() => {
                  setSelectedImage(image);
                  setSelectedImageId(imageId);
                }}
              >
                <div className="image-thumbnail-text">
                  <strong>{image.name}</strong>
                  <p>{image.description}</p>
                </div>
              </div>
            ))
          )}
        </div>

        <div style={{ marginTop: "20px", display: "flex", gap: "10px" }}>
          <img
            src="/images/LeftArrowButton.png"
            alt="Previous Page"
            className="prev-page-button"
            onClick={handlePrevPage}
            title="Previous Page"
            style={{
              opacity: page === 0 ? 0.3 : 1,
              pointerEvents: page === 0 ? "none" : "auto",
            }}
          />
          <span>Page {page + 1}</span>
          <img
            src="/images/RightArrowButton.png"
            alt="Next Page"
            className="next-page-button"
            onClick={handleNextPage}
            title="Next Page"
          />
        </div>

    {selectedImage && (
  <div className="modal-overlay" onClick={() => setSelectedImage(null)}>
    <div className="modal-content"onClick={(e) => e.stopPropagation()} >

  {/* Close button (top-left) */}
  <img
    src="/images/ExitButton2.png"
    alt="Close"
    className="modal-close-button"
    onClick={() => setSelectedImage(null)}
    title="Close"
  />

  {/* Optional top actions (Edit, Publish, etc.) */}

  <h2>{selectedImage.name}</h2>
  <p>{selectedImage.description}</p>

  {/* Button Grid OR Section Content */}
  {viewSection === null ? (
    <div className="modal-grid-buttons">
      <button className="modal-square-button" onClick={() => setViewSection("sets")}>Sets</button>
      <button className="modal-square-button" onClick={() => setViewSection("params")}>Parameters</button>
      <button className="modal-square-button" onClick={() => setViewSection("constraints")}>Constraints</button>
      <button className="modal-square-button" onClick={() => setViewSection("preferences")}>Preferences</button>
    </div>
  ) : (
    <div className="modal-section-content">
      <h3>{viewSection.charAt(0).toUpperCase() + viewSection.slice(1)}</h3>
      <pre style={{ maxHeight: "400px", overflowY: "auto" }}>
        {JSON.stringify(selectedImage[viewSection], null, 2)}
      </pre>
      <button className="modal-back-button" onClick={() => setViewSection(null)}>Back</button>
    </div>
  )}
</div>
  </div>
)}



      </div>
    </div>
  );
};

export default MyImagesPage;
