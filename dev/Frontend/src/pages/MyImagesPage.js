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
  // at the top of MyImagesPage:
  const [newTupleValues, setNewTupleValues] = useState([]);        // for “Add row” inputs
  const [editingRow, setEditingRow] = useState(null);            // index of the row being edited
  const [editTupleValues, setEditTupleValues] = useState([]);    // for “Edit row” inputs
  useEffect(() => {
    if (!selectedImage) return;
    const struct = selectedImage.sets[selectedSetIndex]?.setDefinition?.structure || [];
    setNewTupleValues(Array(struct.length).fill(""));
    setEditingRow(null);
    setEditTupleValues([]);
  }, [selectedSetIndex, selectedImage]);


  const navigate = useNavigate();
  const { userId, constraintsModules, preferenceModules, setSolutionResponse } = useZPL();
  const {
    setVariables,
    setConstraints,
    setPreferences,
    setSelectedVars,
    setConstraintsModules,
    setPreferenceModules,
    setSetTypes,
    setSetAliases,
    setParamTypes,
    setImageId,
    setImageName,
    setImageDescription,
    setZplCode,
    selectedVars,
    setIsEditMode
  } = useZPL();

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
      alert(`Image was published successfully, you can now see it in the public gallery.`);
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

  const handleSolveImage = async () => {
    if (!selectedImageId || !selectedImage) return;

    // 1. Build preferenceModulesScalars (0–1)
    const preferenceModulesScalars = {};
    selectedImage.preferenceModules.forEach((mod) => {
      const raw = mod.value != null ? Number(mod.value) : 50; // slider 0–100 or default 50
      preferenceModulesScalars[mod.moduleName] = Math.min(
        Math.max(raw / 100, 0),
        1
      );
    });

    // 2. Build enabledConstraintModules
    const enabledConstraintModules = selectedImage.constraintModules
      .filter((mod) => mod.enabled ?? true)
      .map((mod) => mod.moduleName);

    const payload = {
      preferenceModulesScalars,
      enabledConstraintModules,
      timeout: 20,
    };

    console.log("Solve payload:", payload);

    try {
      const response = await axios.post(
        `/user/${userId}/image/${selectedImageId}/solver`,
        payload,
        { headers: { "Content-Type": "application/json" } }
      );
      // 3. Store in context
      setSolutionResponse(response.data);
      // 4. Redirect
      navigate("/solution-results");
    } catch (err) {
      console.error("Solver error:", err);
      alert(`Solver error: ${err.response?.data?.msg || err.message}`);
    }
  };

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


  const handleDeleteImage = async () => {
    if (!selectedImageId || !userId) return;
    try {
      await axios.delete(`/user/${userId}/image/${selectedImageId}`);
      alert("Image deleted successfully!");
      navigate("/");
    } catch (err) {
      const msg = err.response?.data || err.message || "Unknown error";
      alert(`Delete failed: ${JSON.stringify(msg)}`);
    }
  };

  const handleEditImage = async () => {
    if (!selectedImageId || !selectedImage) return;

    // 1) Copy basic image fields into context
    setImageId(selectedImageId);
    setImageName(selectedImage.name);
    setImageDescription(selectedImage.description);
    setZplCode(selectedImage.code);

    // 2) Restore the image’s own selectedVars
    setSelectedVars(selectedImage.variables || []);

    // 3) (Optional) fetch full model variables from server
    try {
      const modelResp = await axios.post(
        `/user/${userId}/image/model`,
        { code: selectedImage.code },
        { headers: { "Content-Type": "application/json" } }
      );
      setVariables(modelResp.data.variables || []);
      setConstraints(modelResp.data.constraints || []);
      setPreferences(modelResp.data.preferences || []);

    } catch (err) {
      console.error("Model fetch failed:", err);
      alert(
        `Failed to load image model: ${err.response?.data?.msg || err.message || "Unknown error"
        }`
      );
    }

    // 4) Restore constraintModules, preserving { identifier } objects
    setConstraintsModules(
      (selectedImage.constraintModules || []).map(mod => ({
        name: mod.moduleName,
        description: mod.description || "",
        constraints: Array.isArray(mod.constraints)
          ? mod.constraints.map(c => ({ identifier: c }))
          : []
      }))
    );

    // 5) Restore preferenceModules, preserving { identifier } objects
    setPreferenceModules(
      (selectedImage.preferenceModules || []).map(mod => ({
        name: mod.moduleName,
        description: mod.description || "",
        preferences: Array.isArray(mod.preferences)
          ? mod.preferences.map(p => ({ identifier: p }))
          : []
      }))
    );

    // 6) Rebuild sets & aliases
    const newSetTypes = {};
    const newSetAliases = {};
    (selectedImage.sets || []).forEach(s => {
      newSetTypes[s.setDefinition.name] = s.setDefinition.structure;
      newSetAliases[s.setDefinition.name] = {
        alias: s.setDefinition.alias,
        typeAlias: s.setDefinition.structure,

      };
    });
    setSetTypes(newSetTypes);
    setSetAliases(newSetAliases);

    // 7) Rebuild params & aliases
    const newParamTypes = {};
    const newParamAliases = {};
    (selectedImage.parameters || []).forEach(p => {
      newParamTypes[p.parameterDefinition.name] = p.parameterDefinition.type;
      newParamAliases[p.parameterDefinition.name] = {
        alias: p.parameterDefinition.alias,
        typeAlias: p.parameterDefinition.typeAlias
      };
    });
    setParamTypes(newParamTypes);
    // If tracking parameter aliases separately:
    // setParamAliases(newParamAliases);

    // 8) Set Edit Mode
    setIsEditMode(true);

    // 9) Navigate to the review screen
    navigate("/image-setting-review");
  };







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
                onClick={handleSolveImage}
                title="Solve"
              />
              <img
                src="/images/EditButton.png"
                alt="Edit"
                className="modal-edit-button"
                onClick={handleEditImage}
                title="Edit"
              />
              <img
                src="/images/CopyZPLButton.png"
                alt="Copy ZPL"
                className="modal-copy-button"
                onClick={handleCopyCode}
                title="Copy ZPL"
              />
              <img
                src="/images/delete.png"
                alt="Delete"
                className="modal-delete-button"
                onClick={handleDeleteImage}
                title="Delete"
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
                  {/* Set selector */}
                  <label htmlFor="set-select" className="sets-label">
                    Choose a set:
                  </label>
                  <select
                    id="set-select"
                    className="set-dropdown"
                    value={selectedSetIndex}
                    onChange={(e) => setSelectedSetIndex(Number(e.target.value))}
                  >
                    {selectedImage.sets.map((set, idx) => (
                      <option key={idx} value={idx}>
                        {set.setDefinition.alias}
                      </option>
                    ))}
                  </select>

                  {/* Values section */}
                  <h4>Values:</h4>
                  {(() => {
                    const setObj = selectedImage.sets[selectedSetIndex];
                    const vals = setObj?.values || [];
                    const struct = setObj?.setDefinition?.structure || [];
                    const isTuple = vals.length > 0 && vals.every((v) => /^<.*>$/.test(v));

                    if (isTuple) {
                      // === TUPLE MODE ===

                      // Parse each "<a,b,c>" → [ "a","b","c" ]
                      const rows = vals.map((v) =>
                        v.slice(1, -1).split(",").map((c) => c.trim())
                      );

                      return (
                        <>
                          {/* --- Add New Tuple Row --- */}
                          <div className="tuple-add-row">
                            {struct.map((col, ci) => (
                              <input
                                key={ci}
                                className="tuple-add-input"
                                placeholder={col}
                                value={newTupleValues[ci] || ""}
                                onChange={(e) => {
                                  const copy = [...newTupleValues];
                                  copy[ci] = e.target.value;
                                  setNewTupleValues(copy);
                                }}
                              />
                            ))}
                            <button
                              className="add-value-button"
                              onClick={() => {
                                const joined = `<${newTupleValues.join(",")}>`;
                                const img = { ...selectedImage };
                                img.sets[selectedSetIndex].values.push(joined);
                                setSelectedImage(img);
                                setNewTupleValues(Array(struct.length).fill(""));
                              }}
                            >
                              Add Row
                            </button>
                          </div>

                          {/* --- Tuple Table --- */}
                          <table className="tuple-values-table">
                            <thead>
                              <tr>
                                {struct.map((col, ci) => (
                                  <th key={ci}>{col}</th>
                                ))}
                                <th>Actions</th>
                              </tr>
                            </thead>
                            <tbody>
                              {rows.map((row, ri) => {
                                const isEditing = editingRow === ri;
                                return (
                                  <tr key={ri}>
                                    {row.map((cell, ci) => (
                                      <td key={ci}>
                                        {isEditing ? (
                                          <input
                                            className="tuple-edit-input"
                                            value={editTupleValues[ci] ?? row[ci]}
                                            onChange={(e) => {
                                              const copy = [...editTupleValues];
                                              copy[ci] = e.target.value;
                                              setEditTupleValues(copy);
                                            }}
                                          />
                                        ) : (
                                          cell
                                        )}
                                      </td>
                                    ))}
                                    <td>
                                      {isEditing ? (
                                        <>
                                          <button
                                            onClick={() => {
                                              const newString = `<${editTupleValues.join(
                                                ","
                                              )}>`;
                                              const img = { ...selectedImage };
                                              img.sets[selectedSetIndex].values[ri] = newString;
                                              setSelectedImage(img);
                                              setEditingRow(null);
                                            }}
                                          >
                                            ✅
                                          </button>
                                          <button onClick={() => setEditingRow(null)}>
                                            ✕
                                          </button>
                                        </>
                                      ) : (
                                        <>
                                          <button
                                            onClick={() => {
                                              setEditingRow(ri);
                                              setEditTupleValues(rows[ri]);
                                            }}
                                          >
                                            ✎
                                          </button>
                                          <button
                                            onClick={() => {
                                              const img = { ...selectedImage };
                                              img.sets[selectedSetIndex].values.splice(ri, 1);
                                              setSelectedImage(img);
                                            }}
                                          >
                                            🗑
                                          </button>
                                        </>
                                      )}
                                    </td>
                                  </tr>
                                );
                              })}
                            </tbody>
                          </table>
                        </>
                      );
                    } else {
                      // === FALLBACK LIST MODE ===
                      return (
                        <>
                          {/* Single‐string “Add New Value” */}
                          <div className="add-value-section">
                            <input
                              type="text"
                              className="add-value-input"
                              placeholder="New value"
                              value={selectedImage.sets[selectedSetIndex].newValue || ""}
                              onChange={(e) => {
                                const img = { ...selectedImage };
                                img.sets[selectedSetIndex].newValue = e.target.value;
                                setSelectedImage(img);
                              }}
                              onKeyDown={(e) => {
                                if (e.key === "Enter") {
                                  const val =
                                    selectedImage.sets[selectedSetIndex].newValue?.trim();
                                  if (!val) return;
                                  const img = { ...selectedImage };
                                  img.sets[selectedSetIndex].values.push(val);
                                  img.sets[selectedSetIndex].newValue = "";
                                  setSelectedImage(img);
                                }
                              }}
                            />
                            <button
                              className="add-value-button"
                              onClick={() => {
                                const val =
                                  selectedImage.sets[selectedSetIndex].newValue?.trim();
                                if (!val) return;
                                const img = { ...selectedImage };
                                img.sets[selectedSetIndex].values.push(val);
                                img.sets[selectedSetIndex].newValue = "";
                                setSelectedImage(img);
                              }}
                            >
                              Add
                            </button>
                          </div>

                          {/* Existing list of values */}
                          <ul className="set-values-list">
                            {selectedImage.sets[selectedSetIndex].values.map((val, i) => (
                              <li key={i} className="set-value-item">
                                <span>{val}</span>
                                <div className="value-buttons">
                                  <button
                                    onClick={() => {
                                      const newValue = prompt("Edit value:", val);
                                      if (newValue != null) {
                                        const img = { ...selectedImage };
                                        img.sets[selectedSetIndex].values[i] = newValue;
                                        setSelectedImage(img);
                                      }
                                    }}
                                  >
                                    ✎
                                  </button>
                                  <button
                                    onClick={() => {
                                      const img = { ...selectedImage };
                                      img.sets[selectedSetIndex].values.splice(i, 1);
                                      setSelectedImage(img);
                                    }}
                                  >
                                    ✕
                                  </button>
                                </div>
                              </li>
                            ))}
                          </ul>
                        </>
                      );
                    }
                  })()}
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
                                  newImage.parameters[index].tempValue = e.target.value;
                                  setSelectedImage(newImage);
                                }}
                                onKeyDown={(e) => {
                                  if (e.key === "Enter") {
                                    // Update value on Enter press
                                    const newImage = { ...selectedImage };
                                    newImage.parameters[index].value = param.tempValue ?? param.value;
                                    newImage.parameters[index].isEditing = false;
                                    delete newImage.parameters[index].tempValue;
                                    setSelectedImage(newImage);
                                  }
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
                              // flip the flag
                              const newImage = { ...selectedImage };
                              newImage.constraintModules[index].enabled = !(
                                mod.enabled ?? true
                              );
                              setSelectedImage(newImage);

                              // log the full array so we can inspect each module's enabled state
                              console.log(
                                "constraintModules after toggle:",
                                newImage.constraintModules
                              );
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
