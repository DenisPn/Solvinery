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
  const [selectedImage, setSelectedImage] = useState(null);
  const navigate = useNavigate();
  const { userId } = useZPL();

  useEffect(() => {
    if (!userId) return;

    const fetchImages = async () => {
      try {
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
    setSelectedImage(null);
  };

  const handlePrevPage = () => {
    setPage((prev) => Math.max(prev - 1, 0));
    setSelectedImage(null);
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

  const filteredImages = images.filter((img) =>
    img.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

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

        {/* Image Grid */}
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

        {/* Pagination */}
        <div style={{ marginTop: "20px", display: "flex", gap: "10px" }}>
          <button onClick={handlePrevPage} disabled={page === 0}>Previous</button>
          <span>Page {page + 1}</span>
          <button onClick={handleNextPage}>Next</button>
        </div>

        {/* Modal View */}
        {selectedImage && (
          <div className="modal-overlay" onClick={() => setSelectedImage(null)}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
              <h2>{selectedImage.name}</h2>
              <p><strong>Description:</strong> {selectedImage.description}</p>

              <h3>Variables</h3>
              <ul>
                {selectedImage.variables.map((v, i) => (
                  <li key={i}>
                    {v.identifier} ({v.structure.join(", ")}) — {v.alias}
                  </li>
                ))}
              </ul>

              <h3>Constraints</h3>
              <ul>
                {selectedImage.constraintModules.map((mod, i) => (
                  <li key={i}>
                    <strong>{mod.moduleName}</strong>: {mod.description}<br />
                    Constraints: {mod.constraints.join(", ")}
                  </li>
                ))}
              </ul>

              <h3>Preferences</h3>
              <ul>
                {selectedImage.preferenceModules.map((mod, i) => (
                  <li key={i}>
                    <strong>{mod.moduleName}</strong>: {mod.description}<br />
                    Preferences: {mod.preferences.join(", ")}
                  </li>
                ))}
              </ul>

              <h3>Sets</h3>
              <ul>
                {selectedImage.sets.map((s, i) => (
                  <li key={i}>
                    {s.setDefinition.name} ({s.setDefinition.type.join(", ")}) = [{s.values.join(", ")}]
                  </li>
                ))}
              </ul>

              <h3>Parameters</h3>
              <ul>
                {selectedImage.parameters.map((p, i) => (
                  <li key={i}>
                    {p.parameterDefinition.name} = {p.value}
                  </li>
                ))}
              </ul>

              {/* Action Buttons */}
              <div style={{ marginTop: "20px", display: "flex", gap: "10px" }}>
                <button onClick={handleCopyCode}>Copy ZPL Code</button>
                <button onClick={() => alert("Publish action coming soon!")}>Publish Image</button>
                <button onClick={() => setSelectedImage(null)}>Close</button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MyImagesPage;
