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
  const [selectedSetIndex, setSelectedSetIndex] = useState(0);

  const navigate = useNavigate();
  const { userId } = useZPL();

  useEffect(() => {
    if (!userId) return;

    const fetchImages = async () => {
      try {
        const response = await axios.get(`/user/${userId}/image/${page}`);
        console.log("Raw images response:", response.data.images);
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
        .then(() => alert("ZPL code copied to clipboard!"))
        .catch(() => alert("Failed to copy ZPL code."));
    }
  };

  const handleSolve = () => alert("Solve not available yet.");
  const handleEdit = () => alert("Edit not available yet.");

  const getViewData = () => {
    if (!selectedImage) return null;
    switch (viewSection) {
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
                  console.log("Selected image:", image);
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
          <div
            className="modal-overlay"
            onClick={() => {
              setSelectedImage(null);
              setViewSection(null);
            }}
          >
            <div
              className="modal-content-fixed"
              onClick={(e) => e.stopPropagation()}
            >
              {/* Top-left: Close or Back */}
              {viewSection === null ? (
                <img
                  src="/images/ExitButton2.png"
                  alt="Close"
                  className="modal-corner-button"
                  onClick={() => {
                    setSelectedImage(null);
                    setViewSection(null);
                  }}
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

              {/* Modal Content */}
              {viewSection === null ? (
                <>
                  <p>
                    <strong>Description:</strong> {selectedImage.description}
                  </p>
                  <div className="grid-button-section">
                    <button
                      className="modal-square-button"
                      onClick={() => setViewSection("sets")}
                    >
                      Sets
                    </button>
                    <button
                      className="modal-square-button"
                      onClick={() => setViewSection("params")}
                    >
                      Parameters
                    </button>
                    <button
                      className="modal-square-button"
                      onClick={() => setViewSection("constraints")}
                    >
                      Constraints
                    </button>
                    <button
                      className="modal-square-button"
                      onClick={() => setViewSection("preferences")}
                    >
                      Preferences
                    </button>
                  </div>
                </>
              ) : viewSection === "sets" ? (
                <div className="modal-section-data sets-modal">
                  <label htmlFor="set-select" className="sets-label">
                    Choose a set:
                  </label>
                  <select
                    id="set-select"
                    className="set-dropdown"
                    value={selectedSetIndex}
                    onChange={(e) =>
                      setSelectedSetIndex(Number(e.target.value))
                    }
                  >
                    {selectedImage.sets.map((set, index) => (
                      <option key={index} value={index}>
                        {set.setDefinition.name}
                      </option>
                    ))}
                  </select>

                  <div className="add-value-section">
                    <input
                      type="text"
                      className="add-value-input"
                      placeholder="New value"
                      value={
                        selectedImage.sets[selectedSetIndex].newValue || ""
                      }
                      onChange={(e) => {
                        const newImage = { ...selectedImage };
                        newImage.sets[selectedSetIndex].newValue =
                          e.target.value;
                        setSelectedImage(newImage);
                      }}
                    />
                    <button
                      className="add-value-button"
                      onClick={() => {
                        const newValue =
                          selectedImage.sets[selectedSetIndex].newValue?.trim();
                        if (!newValue) return;
                        const newImage = { ...selectedImage };
                        newImage.sets[selectedSetIndex].values.push(newValue);
                        newImage.sets[selectedSetIndex].newValue = "";
                        setSelectedImage(newImage);
                      }}
                    >
                      Add
                    </button>
                  </div>

                  <h4>Values:</h4>
                  <ul className="set-values-list">
                    {selectedImage.sets[selectedSetIndex]?.values.map(
                      (val, i) => {
                        const editingIndex =
                          selectedImage.sets[selectedSetIndex].editingIndex;
                        const editingValue =
                          selectedImage.sets[selectedSetIndex].editingValue ||
                          "";
                        return (
                          <li key={i} className="set-value-item">
                            <div className="value-text">
                              {editingIndex === i ? (
                                <>
                                  <input
                                    type="text"
                                    value={editingValue}
                                    onChange={(e) => {
                                      const newImage = { ...selectedImage };
                                      newImage.sets[
                                        selectedSetIndex
                                      ].editingValue = e.target.value;
                                      setSelectedImage(newImage);
                                    }}
                                  />
                                  <button
                                    onClick={() => {
                                      const newImage = { ...selectedImage };
                                      newImage.sets[selectedSetIndex].values[
                                        i
                                      ] = editingValue;
                                      newImage.sets[
                                        selectedSetIndex
                                      ].editingIndex = null;
                                      newImage.sets[
                                        selectedSetIndex
                                      ].editingValue = "";
                                      setSelectedImage(newImage);
                                    }}
                                  >
                                    ✅
                                  </button>
                                </>
                              ) : (
                                <span>{val}</span>
                              )}
                            </div>
                            {editingIndex !== i && (
                              <div className="value-buttons">
                                <button
                                  className="edit-value-button"
                                  onClick={() => {
                                    const newImage = { ...selectedImage };
                                    newImage.sets[
                                      selectedSetIndex
                                    ].editingIndex = i;
                                    newImage.sets[
                                      selectedSetIndex
                                    ].editingValue = val;
                                    setSelectedImage(newImage);
                                  }}
                                >
                                  ✎
                                </button>
                                <button
                                  className="remove-value-button"
                                  onClick={() => {
                                    const newImage = { ...selectedImage };
                                    newImage.sets[selectedSetIndex].values =
                                      newImage.sets[
                                        selectedSetIndex
                                      ].values.filter((_, idx) => idx !== i);
                                    setSelectedImage(newImage);
                                  }}
                                >
                                  ✕
                                </button>
                              </div>
                            )}
                          </li>
                        );
                      }
                    )}
                  </ul>
                </div>
              ) : viewSection === "params" ? (
                <div className="modal-section-data parameters-modal">
                  <table className="parameters-table">
                    <thead>
                      <tr>
                        <th>Edit</th>
                        <th>Name</th>
                        <th>Alias</th>
                        <th>Value</th>
                      </tr>
                    </thead>
                    <tbody>
                      {selectedImage.parameters.map((param, index) => (
                        <tr key={index}>
                          <td>
                            {param.isEditing ? (
                              <>
                                <button
                                  onClick={() => {
                                    const newImage = { ...selectedImage };
                                    newImage.parameters[index].value =
                                      param.tempValue ?? param.value;
                                    newImage.parameters[
                                      index
                                    ].isEditing = false;
                                    delete newImage.parameters[index].tempValue;
                                    setSelectedImage(newImage);
                                  }}
                                >
                                  ✅
                                </button>
                                <button
                                  onClick={() => {
                                    const newImage = { ...selectedImage };
                                    newImage.parameters[
                                      index
                                    ].isEditing = false;
                                    delete newImage.parameters[index].tempValue;
                                    setSelectedImage(newImage);
                                  }}
                                >
                                  ✕
                                </button>
                              </>
                            ) : (
                              <button
                                onClick={() => {
                                  const newImage = { ...selectedImage };
                                  newImage.parameters[index].isEditing = true;
                                  newImage.parameters[index].tempValue =
                                    param.value;
                                  setSelectedImage(newImage);
                                }}
                              >
                                ✎
                              </button>
                            )}
                          </td>
                          <td>{param.parameterDefinition.name}</td>
                          <td>{param.parameterDefinition.alias || "—"}</td>
                          <td>
                            {param.isEditing ? (
                              <input
                                type="text"
                                value={param.tempValue}
                                onChange={(e) => {
                                  const newImage = { ...selectedImage };
                                  newImage.parameters[index].tempValue =
                                    e.target.value;
                                  setSelectedImage(newImage);
                                }}
                                style={{ textAlign: "center" }}
                              />
                            ) : (
                              param.value
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : viewSection === "constraints" ? (
                <div className="modal-section-data constraints-modal">
                  {selectedImage.constraintModules.length === 0 ? (
                    <p>No constraints available.</p>
                  ) : (
                    selectedImage.constraintModules.map((mod, index) => (
                      <div key={index} className="module-box">
                        <div className="module-title">{mod.moduleName}</div>
                        <div className="module-description">
                          {mod.description}
                        </div>
                        <div className="module-checkbox">
                          <input
                            type="checkbox"
                            checked={mod.enabled ?? true}
                            onChange={() => {
                              const newImage = { ...selectedImage };
                              newImage.constraintModules[index].enabled = !(
                                mod.enabled ?? true
                              );
                              setSelectedImage(newImage);
                            }}
                          />
                          <label>Enabled</label>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              ) : viewSection === "preferences" ? (
                <div className="modal-section-data preferences-modal">
                  {selectedImage.preferenceModules.length === 0 ? (
                    <p>No preferences available.</p>
                  ) : (
                    selectedImage.preferenceModules.map((mod, index) => (
                      <div key={index} className="module-box">
                        <div className="module-title">{mod.moduleName}</div>
                        <div className="module-description">
                          {mod.description}
                        </div>
                        <div className="slider-container">
                          <label htmlFor={`slider-${index}`}>Value:</label>
                          <input
                            type="range"
                            id={`slider-${index}`}
                            min="0"
                            max="100"
                            value={mod.value ?? 50}
                            onChange={(e) => {
                              const newImage = { ...selectedImage };
                              newImage.preferenceModules[index].value =
                                parseInt(e.target.value);
                              setSelectedImage(newImage);
                            }}
                          />
                          <span>{mod.value ?? 50}</span>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              ) : null}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MyImagesPage;
