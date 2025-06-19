import React, { useState, useEffect } from "react";
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
  const PAGE_SIZE = 5;
  const [totalPages, setTotalPages] = useState(1);

  // filters
  const [filterName, setFilterName] = useState("");
  const [filterDescription, setFilterDescription] = useState("");
  const [filterAuthor, setFilterAuthor] = useState("");
  const [filterAfter, setFilterAfter] = useState("");
  const [filterBefore, setFilterBefore] = useState("");

  // images + loading + page flags
  const [imageMap, setImageMap] = useState({});
  const [selectedImage, setSelectedImage] = useState(null);
  const [loading, setLoading] = useState(false);
  const [hasPrevious, setHasPrevious] = useState(false);
  const [hasNext, setHasNext] = useState(false);

  // fetch images
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
        size: PAGE_SIZE,
      };
      const resp = await axios.get("/image/view", { params });
      setImageMap(resp.data.images || {});
      setHasPrevious(!!resp.data.hasPrevious);
      setHasNext(!!resp.data.hasNext);
      setTotalPages(resp.data.totalPages ?? 1);
    } catch (err) {
      console.error("Error fetching view images:", err);
      alert(
        `Error fetching images: ${
          err.response?.data?.message || err.message
        }`
      );
    } finally {
      setLoading(false);
    }
  };

  // initial load & when page changes
  useEffect(() => {
    fetchImages();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  // handlers
  const handleSearchClick = () => {
    setPage(0);
    fetchImages();
  };
  const handlePrevPage = () => hasPrevious && setPage((p) => Math.max(p - 1, 0));
  const handleNextPage = () => hasNext && setPage((p) => p + 1);
  const handleBack = () => navigate("/");

  const handleSaveImage = async () => {
    if (!selectedImage?.imageId || !userId) {
      alert("Missing image or user information.");
      return;
    }
    try {
      const res = await axios.patch(
        `/user/${userId}/image/${selectedImage.imageId}/get`
      );
      alert(`Response: ${JSON.stringify(res.data)}`);
    } catch (err) {
      alert(`Error: ${err.response?.data?.message || err.message}`);
    }
  };

  const images = Object.entries(imageMap).map(([id, data]) => ({
    imageId: id,
    ...data,
  }));

  return (
    <div className="view-images-background">
      <img
        src="/images/HomeButton.png"
        alt="Home"
        className="home-button"
        onClick={handleBack}
      />

      <div className="view-images-form-container">
        <h1 className="main-view-images-title">Public Images</h1>

        {/* Filters */}
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

          {/* After */}
          <input
            type="text"
            placeholder="After"
            value={filterAfter}
            onFocus={(e) => (e.target.type = "date")}
            onBlur={(e) => !e.target.value && (e.target.type = "text")}
            onChange={(e) => setFilterAfter(e.target.value)}
          />

          {/* Before */}
          <input
            type="text"
            placeholder="Before"
            value={filterBefore}
            onFocus={(e) => (e.target.type = "date")}
            onBlur={(e) => !e.target.value && (e.target.type = "text")}
            onChange={(e) => setFilterBefore(e.target.value)}
          />
        </div>

        {/* Search button */}
        <div className="search-button-container">
          <button className="search-button" onClick={handleSearchClick}>
            Search
          </button>
        </div>

        {/* Loading modal */}
        {loading && (
          <div className="modal-overlay">
            <div className="loading-modal">
              <div className="spinner" />
            </div>
          </div>
        )}

        {/* Images grid */}
        {!loading && (
          <div className="images-section">
            {images.length === 0 ? (
              <p>No images available.</p>
            ) : (
              images.map((img) => (
                <div
                  key={img.imageId}
                  className="image-item"
                  onClick={() => setSelectedImage(img)}
                >
                  <div className="image-thumbnail-text">
                    <h4>{img.name}</h4>
                    <p>{img.description}</p>
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
            alt="Prev"
            className="prev-page-button"
            onClick={handlePrevPage}
            style={{
              opacity: hasPrevious ? 1 : 0.3,
              pointerEvents: hasPrevious ? "auto" : "none",
            }}
          />
          <span>
            Page {page + 1} out of {totalPages}
          </span>
          <img
            src="/images/RightArrowButton.png"
            alt="Next"
            className="next-page-button"
            onClick={handleNextPage}
            style={{
              opacity: hasNext ? 1 : 0.3,
              pointerEvents: hasNext ? "auto" : "none",
            }}
          />
        </div>

        {/* Detail modal */}
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
                />
                <img
                  src="/images/downloadButton.png"
                  alt="Save"
                  className="modal-save-button"
                  onClick={handleSaveImage}
                />
                <img
                  src="/images/CopyZPLButton.png"
                  alt="Copy"
                  className="modal-copy-button"
                  onClick={() => {
                    navigator.clipboard
                      .writeText(selectedImage.description || "")
                      .then(() => alert("Description copied!"))
                      .catch(() => alert("Copy failed."));
                  }}
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
