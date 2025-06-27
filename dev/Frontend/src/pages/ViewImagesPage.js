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
        `Error fetching images: ${err.response?.data?.message || err.message
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
  const handleBack = () => navigate("/main-page");

  const handleSaveImage = async () => {
    if (!selectedImage?.imageId || !userId) {
      alert("Missing image or user information.");
      return;
    }
    try {
      const res = await axios.patch(
        `/user/${userId}/image/${selectedImage.imageId}/get`
      );
      alert(`Image added to your images successfully!`);
    } catch (err) {
      alert(`Error: ${err.response?.data?.message || err.message}`);
    }
  };

  const images = Object.entries(imageMap).map(([id, data]) => ({
    imageId: id,
    ...data,
  }));
  
  // The reliable click handler for the modal background
  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      setSelectedImage(null);
    }
  };

  return (
    <div className="view-images-background">
      <img
        src={`${process.env.PUBLIC_URL}/images/HomeButton.png`}
        alt="Home"
        className="home-button"
        onClick={handleBack}
      />

      <div className="view-images-form-container">
        <h1 className="main-view-images-title">Public Images</h1>

        {/* Filters */}
        <div className="filters-card">
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
              type="text"
              placeholder="After creation date"
              value={filterAfter}
              onFocus={(e) => (e.target.type = "date")}
              onBlur={(e) => !e.target.value && (e.target.type = "text")}
              onChange={(e) => setFilterAfter(e.target.value)}
            />
            <input
              type="text"
              placeholder="Before creation date"
              value={filterBefore}
              onFocus={(e) => (e.target.type = "date")}
              onBlur={(e) => !e.target.value && (e.target.type = "text")}
              onChange={(e) => setFilterBefore(e.target.value)}
            />
          </div>
          <div className="search-button-container">
            <button className="search-button" onClick={handleSearchClick}>
              Search
            </button>
          </div>
        </div>
        
        {loading && (
          // We keep the old overlay for the loading modal as it's simple
          <div className="modal-overlay"> 
            <div className="loading-modal">
              <div className="spinner" />
              <p className="loading-label">Loading…</p>
            </div>
          </div>
        )}

        {!loading && (
          <div className="images-section">
            {images.length === 0 ? (
              <p>No images available.</p>
            ) : (
              images.map((img) => (
                <div
                  key={img.imageId}
                  className="image-item tooltip"
                  onClick={() => setSelectedImage(img)}
                >
                  <div className="image-thumbnail-text">
                    <h4>{img.name}</h4>
                  </div>
                  <div className="tooltip-bubble">{img.description || "— No description —"}</div>
                </div>
              ))
            )}
          </div>
        )}

        <div className="pagination-container">
          <img
            src={`${process.env.PUBLIC_URL}/images/LeftArrowButton.png`}
            alt="Prev"
            className="prev-page-button"
            onClick={handlePrevPage}
            style={{ opacity: hasPrevious ? 1 : 0.3, pointerEvents: hasPrevious ? "auto" : "none" }}
          />
          <span>Page {page + 1} out of {totalPages}</span>
          <img
            src={`${process.env.PUBLIC_URL}/images/RightArrowButton.png`}
            alt="Next"
            className="next-page-button"
            onClick={handleNextPage}
            style={{ opacity: hasNext ? 1 : 0.3, pointerEvents: hasNext ? "auto" : "none" }}
          />
        </div>

        {/* --- FINAL, WORKING DETAIL MODAL --- */}
        {selectedImage && (
          <div className="modal-container" onClick={handleOverlayClick}>
            <div className="modal-content">
              <div className="modal-header">
                <div className="modal-header-left">
                  <div className="icon-wrapper" data-tip="Close">
                    <img
                      src={`${process.env.PUBLIC_URL}/images/ExitButton2.png`}
                      alt="Close"
                      className="modal-back-btn"
                      onClick={() => setSelectedImage(null)}
                    />
                  </div>
                </div>
                <div className="modal-header-right">
                  <div className="icon-wrapper" data-tip="Add to your images">
                    <img
                      src={`${process.env.PUBLIC_URL}/images/downloadButton.png`}
                      alt="Save"
                      className="modal-save-button"
                      onClick={handleSaveImage}
                    />
                  </div>
                </div>
              </div>
              <div className="image-box">
                <h2 className="image-title">{selectedImage.name}</h2>
                <p className="image-subtext">{selectedImage.description}</p>
                <p className="image-subtext"><strong>Author:</strong> {selectedImage.authorName}</p>
                <p className="image-subtext"><strong>Creation Date:</strong> {new Date(selectedImage.creationDate).toLocaleDateString()}</p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ViewImagesPage;