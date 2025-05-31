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
      const response = await axios.patch(
        `/user/${userId}/image/${selectedImageId}/publish`
      );
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
      navigator.clipboard
        .writeText(selectedImage.code)
        .then(() => {
          alert("ZPL code copied to clipboard!");
        })
        .catch((err) => {
          alert("Failed to copy ZPL code.");
        });
    }
  };




// Optional placeholders
const handleSolve = () => alert("Solve not available yet.");
const handleEdit = () => alert("Edit not available yet.");

const getViewData = () => {
  if (!selectedImage) return null;
  switch (viewSection) {
    case "sets":
      return JSON.stringify(selectedImage.sets, null, 2);
    case "params":
      return JSON.stringify(selectedImage.parameters, null, 2);
    case "constraints":
      return JSON.stringify(selectedImage.constraintModules, null, 2);
    case "preferences":
      return JSON.stringify(selectedImage.preferenceModules, null, 2);
    default:
      return null;
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
  <div className="modal-overlay" onClick={() => { setSelectedImage(null); setViewSection(null); }}>
    <div className="modal-content-fixed" onClick={(e) => e.stopPropagation()}>
      

      {/* Top-left: Close or Back — same style and icon */}
{viewSection === null ? (
  <img
    src="/images/ExitButton2.png"
    alt="Close"
    className="modal-corner-button"
    onClick={() => { setSelectedImage(null); setViewSection(null); }}
    title="Close"
  />
) : (
  <img
    src="/images/ExitButton2.png"
    alt="Back"
    className="modal-corner-button"
    onClick={() => setViewSection(null)}
    title="Back"
  />
)}


      {/* Top-right Action Buttons */}
      <img
        src="/images/PublishButton.png"
        alt="Publish"
        className="modal-publish-button"
        onClick={handlePublishImage}
        title="Publish"
      />
      <img
        src="/images/Solve.png"
        alt="Solve"
        className="modal-solve-button"
        onClick={handleSolve}
        title="Solve"
      />
      <img
        src="/images/EditButton.png"
        alt="Edit"
        className="modal-edit-button"
        onClick={handleEdit}
        title="Edit"
      />
      <img
        src="/images/CopyZPLButton.png"
        alt="Copy ZPL"
        className="modal-copy-button"
        onClick={handleCopyCode}
        title="Copy ZPL"
      />

      {/* Main Modal vs Submodal */}
      {viewSection === null ? (
        <>
          <p><strong>Description:</strong> {selectedImage.description}</p>
          <div className="grid-button-section">
            <button className="modal-square-button" onClick={() => setViewSection("sets")}>Sets</button>
            <button className="modal-square-button" onClick={() => setViewSection("params")}>Parameters</button>
            <button className="modal-square-button" onClick={() => setViewSection("constraints")}>Constraints</button>
            <button className="modal-square-button" onClick={() => setViewSection("preferences")}>Preferences</button>
          </div>
        </>
      ) : (
        <pre className="modal-section-data">{getViewData()}</pre>
      )}

    </div>
  </div>
)}

      </div>
    </div>
  );
};

export default MyImagesPage;
