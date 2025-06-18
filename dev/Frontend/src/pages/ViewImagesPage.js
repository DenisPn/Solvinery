import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../Themes/MainTheme.css";
import "./ViewImagesPage.css";
import { useZPL } from "../context/ZPLContext";

const ViewImagesPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [imageMap, setImageMap] = useState({});
  const [page, setPage] = useState(0);
  const [selectedImage, setSelectedImage] = useState(null);
  const navigate = useNavigate();
  const { userId } = useZPL();

  useEffect(() => {
    const fetchImages = async () => {
      try {
        const response = await axios.get(`/image/view/${page}`);
        setImageMap(response.data.images || {});
      } catch (error) {
        console.error("Error fetching view images:", error);
        alert(`Error fetching images: ${error.response?.data?.message || error.message}`);
      }
    };

    fetchImages();
  }, [page]);

  const images = Object.entries(imageMap).map(([imageId, imageData]) => ({
    ...imageData,
    imageId,
  }));

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleBack = () => {
    navigate("/");
  };

  const handleNextPage = () => {
    setPage((prev) => prev + 1);
    setSelectedImage(null);
  };

  const handlePrevPage = () => {
    setPage((prev) => Math.max(prev - 1, 0));
    setSelectedImage(null);
  };

  const handleCopyCode = () => {
    if (selectedImage?.code) {
      navigator.clipboard
        .writeText(selectedImage.code)
        .then(() => alert("ZPL code copied to clipboard!"))
        .catch(() => alert("Failed to copy ZPL code."));
    }
  };

  const handleSaveImage = async () => {
    if (!selectedImage || !selectedImage.imageId || !userId) {
      alert("Missing image or user information.");
      return;
    }

    try {
      const response = await axios.patch(`/user/${userId}/image/${selectedImage.imageId}/get`);
      alert(`Response: ${JSON.stringify(response.data)}`);
    } catch (error) {
      alert(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const filteredImages = images.filter((img) =>
    img.name?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="view-images-background">
      <img
        src="/images/HomeButton.png"
        alt="Home"
        className="home-button"
        onClick={handleBack}
        title="Go to Home"
      />

      <div className="view-images-form-container">
        <h1 className="main-view-images-title">Public Images</h1>

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

        <div className="images-section">
          {filteredImages.length === 0 ? (
            <p>No images available.</p>
          ) : (
            filteredImages.map((image, index) => (
              <div
                key={index}
                className="image-item"
                onClick={() => setSelectedImage(image)}
              >
                <div className="image-thumbnail-text">
                  <h4>{image.name}</h4>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Pagination */}
        <div style={{ marginTop: "20px", display: "flex", gap: "10px", alignItems: "center" }}>
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

        {/* Modal */}
        {selectedImage && (
          <div className="modal-overlay" onClick={() => setSelectedImage(null)}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h2>{selectedImage.name}</h2>
              <p><strong>Description:</strong> {selectedImage.description}</p>
              <p><strong>Author:</strong> {selectedImage.authorName}</p>
              <p><strong>Creation Date:</strong> {new Date(selectedImage.creationDate).toLocaleString()}</p>

              <div style={{ marginTop: "20px", display: "flex", gap: "10px" }}>
                <img
                  src="/images/ExitButton2.png"
                  alt="Close"
                  className="modal-close-button"
                  onClick={() => setSelectedImage(null)}
                  title="Close"
                />
                <img
                  src="/images/downloadButton.png"
                  alt="Save to My Images"
                  className="modal-save-button"
                  onClick={handleSaveImage}
                  title="Save image in My Images"
                />
                <img
                  src="/images/CopyZPLButton.png"
                  alt="Copy ZPL"
                  className="modal-copy-button"
                  onClick={() => {
                    navigator.clipboard
                      .writeText(selectedImage.description || "")
                      .then(() => alert("Description copied!"))
                      .catch(() => alert("Failed to copy description."));
                  }}
                  title="Copy image description"
                />
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ViewImagesPage;
