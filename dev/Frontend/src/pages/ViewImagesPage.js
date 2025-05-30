import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../Themes/MainTheme.css";
import "./ViewImagesPage.css";

const ViewImagesPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [imageMap, setImageMap] = useState({});
  const [page, setPage] = useState(0);
  const [selectedImage, setSelectedImage] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchImages = async () => {
      try {
        const response = await axios.get(`/image/view/${page}`);
        console.log("Fetched view images:", response.data);
        setImageMap(response.data.images || {});
      } catch (error) {
        console.error("Error fetching view images:", error);
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

  const handleCopyCode = () => {
    if (selectedImage?.code) {
      navigator.clipboard
        .writeText(selectedImage.code)
        .then(() => alert("ZPL code copied to clipboard!"))
        .catch(() => alert("Failed to copy ZPL code."));
    }
  };

  const handlePublish = async () => {
    if (!selectedImage?.imageId) return;
    try {
      const response = await axios.patch(
        `/user/${selectedImage.authorId}/image/${selectedImage.imageId}/publish`
      );
      alert(JSON.stringify(response.data));
    } catch (err) {
      alert("Error: " + (err.response?.data?.message || err.message));
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
  onClick={() => navigate("/")}
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
                className="image-item clickable"
                onClick={() => setSelectedImage(image)}
              >
                <div className="image-thumbnail-text">
                  <strong>{image.name}</strong>
                  <p>{image.description}</p>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Modal */}
        {selectedImage && (
  <div className="modal-overlay" onClick={() => setSelectedImage(null)}>
    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
      <h2>{selectedImage.name}</h2>
      <p><strong>Description:</strong> {selectedImage.description}</p>
      <p><strong>Author:</strong> {selectedImage.authorName}</p>
      <p><strong>Creation Date:</strong> {new Date(selectedImage.creationDate).toLocaleString()}</p>

      {/* Action Buttons */}
      <div style={{ marginTop: "20px", display: "flex", gap: "10px" }}>
        <img
          src="/images/ExitButton2.png"
          alt="Close"
          className="modal-close-button"
          onClick={() => setSelectedImage(null)}
          title="Close"
        />

        <img
          src="/images/CopyZPLButton.png"
          alt="Copy ZPL"
          className="modal-copy-button"
          onClick={() => {
            navigator.clipboard.writeText(selectedImage.description || "").then(() => {
              alert("Description copied!");
            }).catch(() => {
              alert("Failed to copy description.");
            });
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
