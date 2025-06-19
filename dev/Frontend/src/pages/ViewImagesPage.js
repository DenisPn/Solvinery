import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../Themes/MainTheme.css";
import "./ViewImagesPage.css";
import { useZPL } from "../context/ZPLContext";

const ViewImagesPage = () => {
  const navigate = useNavigate();
  const { userId } = useZPL();

  // pagination
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  // filters
  const [filterName, setFilterName] = useState("");
  const [filterDescription, setFilterDescription] = useState("");
  const [filterAuthor, setFilterAuthor] = useState("");
  const [filterBefore, setFilterBefore] = useState("");
  const [filterAfter, setFilterAfter] = useState("");

  // images + loading
  const [imageMap, setImageMap] = useState({});
  const [selectedImage, setSelectedImage] = useState(null);
  const [loading, setLoading] = useState(false);

  // fetch images (GET with query params)
  const fetchImages = async () => {
    setLoading(true);
    try {
      const params = {
        name: filterName,
        description: filterDescription,
        author: filterAuthor,
        after: filterAfter,
        before: filterBefore,
        page,
        size,
      };
      const response = await axios.get("/image/view", { params });
      setImageMap(response.data.images || {});
    } catch (error) {
      console.error("Error fetching view images:", error);
      alert(
        `Error fetching images: ${error.response?.data?.message || error.message
        }`
      );
    } finally {
      setLoading(false);
    }
  };

  // handlers
  const handleSearchClick = () => {
    setPage(0);
    fetchImages();
  };

  const handlePrevPage = () => {
    setPage((p) => Math.max(p - 1, 0));
    fetchImages();
    setSelectedImage(null);
  };

  const handleNextPage = () => {
    setPage((p) => p + 1);
    fetchImages();
    setSelectedImage(null);
  };

  const handleBack = () => navigate("/");

  const handleSaveImage = async () => {
    if (!selectedImage || !selectedImage.imageId || !userId) {
      alert("Missing image or user information.");
      return;
    }
    try {
      const res = await axios.patch(
        `/user/${userId}/image/${selectedImage.imageId}/get`
      );
      alert(`Response: ${JSON.stringify(res.data)}`);
    } catch (error) {
      alert(`Error: ${error.response?.data?.message || error.message}`);
    }
  };

  const images = Object.entries(imageMap).map(([imageId, data]) => ({
    ...data,
    imageId,
  }));

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

        {/* Filters in one row of 6 columns */}
        <div className="filter-grid two-columns">
          <input
            type="text"
            placeholder="Name"
            value={filterName}
            onChange={(e) => setFilterName(e.target.value)}
          />
          <input
            type="text"
            placeholder="Description"
            value={filterDescription}
            onChange={(e) => setFilterDescription(e.target.value)}
          />
          <input
            type="text"
            placeholder="Author"
            value={filterAuthor}
            onChange={(e) => setFilterAuthor(e.target.value)}
          />
          <input
            type="date"
            placeholder="After"
            value={filterAfter}
            onChange={(e) => setFilterAfter(e.target.value)}
          />
          <input
            type="date"
            placeholder="Before"
            value={filterBefore}
            onChange={(e) => setFilterBefore(e.target.value)}
          />
          <input
            type="number"
            min="1"
            placeholder="Page Size"
            value={size}
            onChange={(e) => setSize(Number(e.target.value))}
          />
        </div>

        {/* Search button */}
        <div className="search-button-container">
          <button className="search-button" onClick={handleSearchClick}>
            Search
          </button>
        </div>

        {/* Loading Modal */}
        {loading && (
          <div className="modal-overlay">
            <div className="loading-modal">
                     <div className="spinner" />
                  </div>
          </div>
        )}

        {/* Image Grid */}
        {!loading && (
          <div className="images-section">
            {images.length === 0 ? (
              <p>No images available.</p>
            ) : (
              images.map((image) => (
                <div
                  key={image.imageId}
                  className="image-item"
                  onClick={() => setSelectedImage(image)}
                >
                  <div className="image-thumbnail-text">
                    <h4>{image.name}</h4>
                    <p>{image.description}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        )}

        {/* Pagination */}
        <div className="pagination-container">
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

        {/* Detail Modal */}
        {selectedImage && (
          <div
            className="modal-overlay"
            onClick={() => setSelectedImage(null)}
          >
            <div
              className="modal-content"
              onClick={(e) => e.stopPropagation()}
            >
              <h2>{selectedImage.name}</h2>
              <p>
                <strong>Description:</strong> {selectedImage.description}
              </p>
              <p>
                <strong>Author:</strong> {selectedImage.authorName}
              </p>
              <p>
                <strong>Creation Date:</strong>{" "}
                {new Date(selectedImage.creationDate).toLocaleDateString()}
              </p>

              <div className="modal-buttons">
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
                  alt="Copy Description"
                  className="modal-copy-button"
                  onClick={() =>
                    navigator.clipboard
                      .writeText(selectedImage.description || "")
                      .then(() => alert("Description copied!"))
                      .catch(() => alert("Failed to copy description."))
                  }
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
