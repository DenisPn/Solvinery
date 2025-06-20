import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useZPL } from "../context/ZPLContext";
import "../Themes/MainTheme.css";
import "./MyImagesPage.css";

const MyImagesPage = () => {
  // Local filter inputs
  const [filterName, setFilterName] = useState("");
  const [filterDescription, setFilterDescription] = useState("");

  // Search criteria & pagination
  const [criteria, setCriteria] = useState({ name: "", description: "", page: 0 });
  const PAGE_SIZE = 10;

  // Data + loading + pagination metadata
  const [imagesMap, setImagesMap] = useState({});
  const [loading, setLoading] = useState(false);
  const [hasNext, setHasNext] = useState(false);
  const [hasPrevious, setHasPrevious] = useState(false);
  const [totalPages, setTotalPages] = useState(1);

  // Modal/detail
  const [viewSection, setViewSection] = useState(null);
  const [selectedSetIndex, setSelectedSetIndex] = useState(0);
  const [showAddModal, setShowAddModal] = useState(false);
  const [sortConfig, setSortConfig] = useState({ colIndex: null, direction: "asc" });

  // Tuple‐editing
  const [newTupleValues, setNewTupleValues] = useState([]);
  const [editingRow, setEditingRow] = useState(null);
  const [editTupleValues, setEditTupleValues] = useState([]);

  // Context & nav
  const {
    userId,
    constraintsModules,
    preferenceModules,
    setSolutionResponse,
    selectedImage,
    setSelectedImage,
    selectedImageId,
    setSelectedImageId,
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
  const navigate = useNavigate();

  // Fetch images according to current criteria
  const fetchImages = useCallback(async () => {
    if (!userId) return;
    setLoading(true);
    try {
      const resp = await axios.get(`/user/${userId}/image/view`, {
        params: {
          name: criteria.name || undefined,
          description: criteria.description || undefined,
          page: criteria.page,
          size: PAGE_SIZE
        }
      });
      setImagesMap(resp.data.images || {});
      setHasNext(Boolean(resp.data.hasNext));
      setHasPrevious(Boolean(resp.data.hasPrevious));
      setTotalPages(resp.data.totalPages || 1);
    } catch (err) {
      console.error(err);
      alert(`Error fetching images: ${err.response?.data?.message || err.message}`);
    } finally {
      setLoading(false);
    }
  }, [userId, criteria]);

  // On mount and whenever criteria changes
  useEffect(() => {
    fetchImages();
  }, [fetchImages]);

  // Restore selectedImage from map
  useEffect(() => {
    if (selectedImageId && imagesMap[selectedImageId]) {
      setSelectedImage(imagesMap[selectedImageId]);
    }
  }, [imagesMap, selectedImageId, setSelectedImage]);

  // Initialize tuple inputs when selectedImage or set index changes
  useEffect(() => {
    if (!selectedImage) return;
    const struct = selectedImage.sets[selectedSetIndex]?.setDefinition?.structure || [];
    setNewTupleValues(Array(struct.length).fill(""));
    setEditingRow(null);
    setEditTupleValues([]);
  }, [selectedImage, selectedSetIndex]);

  // Helper: PATCH full image
  async function updateImageOnServer() {
    if (!selectedImage || !selectedImageId || !userId) return;
    const payload = {
      variables: (selectedImage.variables || []).map(v => ({
        identifier: v.identifier,
        structure: Array.isArray(v.structure) ? v.structure : [],
        alias: v.alias || v.identifier
      })),
      constraintModules: (selectedImage.constraintModules || []).map(mod => ({
        moduleName: mod.moduleName,
        description: mod.description,
        constraints: Array.isArray(mod.constraints) ? mod.constraints : []
      })),
      preferenceModules: (selectedImage.preferenceModules || []).map(mod => ({
        moduleName: mod.moduleName,
        description: mod.description,
        preferences: Array.isArray(mod.preferences) ? mod.preferences : []
      })),
      sets: (selectedImage.sets || []).map(s => ({
        setDefinition: {
          name: s.setDefinition.name,
          structure: Array.isArray(s.setDefinition.structure)
            ? s.setDefinition.structure
            : [],
          alias: s.setDefinition.alias
        },
        values: Array.isArray(s.values) ? s.values : []
      })),
      parameters: (selectedImage.parameters || []).map(p => ({
        parameterDefinition: {
          name: p.parameterDefinition.name,
          structure: p.parameterDefinition.structure,
          alias: p.parameterDefinition.alias
        },
        value: p.value != null ? String(p.value) : ""
      })),
      name: selectedImage.name,
      description: selectedImage.description,
      code: selectedImage.code
    };
    await axios.patch(
      `/user/${userId}/image/${selectedImageId}`,
      payload,
      { headers: { "Content-Type": "application/json" } }
    );
  }

  // Action handlers
  const handlePublishImage = async () => {
    if (!selectedImageId || !userId) return;
    await updateImageOnServer();
    try {
      await axios.patch(`/user/${userId}/image/${selectedImageId}/publish`);
      alert("Published successfully");
    } catch (err) {
      alert(`Publish failed: ${err.message}`);
    }
  };
  const handleSolveImage = async () => {
    if (!selectedImageId || !selectedImage) return;
    await updateImageOnServer();
    const preferenceModulesScalars = {};
    selectedImage.preferenceModules.forEach(mod => {
      const raw = Number(mod.value ?? 50);
      preferenceModulesScalars[mod.moduleName] = Math.min(Math.max(raw / 100, 0), 1);
    });
    const enabledConstraintModules = selectedImage.constraintModules
      .filter(mod => mod.enabled ?? true)
      .map(mod => mod.moduleName);
    try {
      const resp = await axios.post(
        `/user/${userId}/image/${selectedImageId}/solver`,
        { preferenceModulesScalars, enabledConstraintModules, timeout: 20 }
      );
      setSolutionResponse(resp.data);
      navigate("/solution-results");
    } catch (err) {
      alert(`Solve error: ${err.message}`);
    }
  };
  const handleDeleteImage = async () => {
    if (!selectedImageId || !userId) return;
    try {
      await axios.delete(`/user/${userId}/image/${selectedImageId}`);
      alert("Deleted");
      navigate("/main-page");
    } catch (err) {
      alert(`Delete failed: ${err.message}`);
    }
  };
  const handleEditImage = async () => {
    if (!selectedImageId || !selectedImage) return;
    await updateImageOnServer();
    setImageId(selectedImageId);
    setImageName(selectedImage.name);
    setImageDescription(selectedImage.description);
    setZplCode(selectedImage.code);
    setSelectedVars(selectedImage.variables || []);
    try {
      const modelResp = await axios.post(
        `/user/${userId}/image/model`,
        { code: selectedImage.code }
      );
      setVariables(modelResp.data.variables || []);
      setConstraints(modelResp.data.constraints || []);
      setPreferences(modelResp.data.preferences || []);
    } catch { }
    setConstraintsModules(
      selectedImage.constraintModules.map(mod => ({
        name: mod.moduleName,
        description: mod.description,
        constraints: mod.constraints.map(c => ({ identifier: c }))
      }))
    );
    setPreferenceModules(
      selectedImage.preferenceModules.map(mod => ({
        name: mod.moduleName,
        description: mod.description,
        preferences: mod.preferences.map(p => ({ identifier: p }))
      }))
    );
    const newSetTypes = {}, newSetAliases = {};
    selectedImage.sets.forEach(s => {
      newSetTypes[s.setDefinition.name] = s.setDefinition.structure;
      newSetAliases[s.setDefinition.name] = {
        alias: s.setDefinition.alias,
        typeAlias: s.setDefinition.structure
      };
    });
    setSetTypes(newSetTypes);
    setSetAliases(newSetAliases);
    const newParamTypes = {}, newParamAliases = {};
    selectedImage.parameters.forEach(p => {
      newParamTypes[p.parameterDefinition.name] = p.parameterDefinition.type;
      newParamAliases[p.parameterDefinition.name] = {
        alias: p.parameterDefinition.alias,
        typeAlias: p.parameterDefinition.typeAlias
      };
    });
    setParamTypes(newParamTypes);
    setIsEditMode(true);
    navigate("/image-setting-review");
  };
  const handleCopyCode = () => {
    if (selectedImage?.code) {
      navigator.clipboard.writeText(selectedImage.code)
        .then(() => alert("Copied"))
        .catch(() => alert("Copy failed"));
    }
  };

  return (
    <div className="my-images-background">
      {/* Loading spinner modal */}
      {loading && (
        <div className="modal-overlay">
          <div className="spinner-modal">
            <div className="spinner" />
            <p>Loading…</p>
          </div>
        </div>
      )}

      <img
        src="/images/HomeButton.png"
        alt="Home"
        className="home-button"
        onClick={() => navigate("/main-page")}
      />

      <div className="my-images-form-container">
        <h1 className="main-my-images-title">My Images</h1>

        {/* FILTER ROW */}
        <div
          className="filter-row"
          style={{
            display: "flex",
            gap: "8px",
            alignItems: "center",
            justifyContent: "center",
            marginBottom: "20px"
          }}
        >
          <input
            type="text"
            className="filter-input"
            placeholder="Name"
            value={filterName}
            onChange={e => setFilterName(e.target.value)}
          />
          <input
            type="text"
            className="filter-input"
            placeholder="Description"
            value={filterDescription}
            onChange={e => setFilterDescription(e.target.value)}
          />
          <button
            className="search-button"
            onClick={() =>
              setCriteria({ name: filterName, description: filterDescription, page: 0 })
            }
            style={{ padding: "8px 16px", cursor: "pointer" }}
          >
            Search
          </button>
        </div>

        {/* IMAGES GRID */}
        <div className="images-section">
          {Object.entries(imagesMap).length === 0 ? (
            <p>No images available.</p>
          ) : (
            Object.entries(imagesMap).map(([id, img]) => (
              <div
                key={id}
                className="image-item clickable"
                onClick={() => {
                  setSelectedImage(img);
                  setSelectedImageId(id);
                }}
              >
                <div className="image-thumbnail-text">
                  <strong>{img.name}</strong>
                </div>
              </div>
            ))
          )}
        </div>

        {/* PAGINATION */}
        <div className="pagination-row">
          <img
            src="/images/LeftArrowButton.png"
            alt="Prev"
            onClick={() =>
              hasPrevious && setCriteria(c => ({ ...c, page: c.page - 1 }))
            }
            className="prev-page-button"
            style={{
              opacity: hasPrevious ? 1 : 0.3,
              pointerEvents: hasPrevious ? "auto" : "none"
            }}
          />
          <span>Page {criteria.page + 1} out of {totalPages}</span>
          <img
            src="/images/RightArrowButton.png"
            alt="Next"
            onClick={() =>
              hasNext && setCriteria(c => ({ ...c, page: c.page + 1 }))
            }
            className="next-page-button"
            style={{
              opacity: hasNext ? 1 : 0.3,
              pointerEvents: hasNext ? "auto" : "none"
            }}
          />
        </div>

        {/* DETAIL MODAL */}
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
              onClick={e => e.stopPropagation()}
            >
              {/* Top-left Close/Back */}
              {viewSection === null ? (
                <img
                  src="/images/ExitButton2.png"
                  alt="Close"
                  className="modal-corner-button"
                  onClick={async () => {
                    await updateImageOnServer();
                    setSelectedImage(null);
                    setViewSection(null);
                  }}
                />
              ) : (
                <img
                  src="/images/ExitButton2.png"
                  alt="Back"
                  className="modal-corner-button"
                  onClick={() => setViewSection(null)}
                />
              )}

              {/* Top-right Actions */}
              <img
                src="/images/PublishButton.png"
                alt="Publish"
                className="modal-publish-button"
                onClick={handlePublishImage}
              />
              <img
                src="/images/Solve.png"
                alt="Solve"
                className="modal-solve-button"
                onClick={handleSolveImage}
              />
              <img
                src="/images/EditButton.png"
                alt="Edit"
                className="modal-edit-button"
                onClick={handleEditImage}
              />
              <img
                src="/images/CopyZPLButton.png"
                alt="Copy ZPL"
                className="modal-copy-button"
                onClick={handleCopyCode}
              />
              <img
                src="/images/delete.png"
                alt="Delete"
                className="modal-delete-button"
                onClick={handleDeleteImage}
              />

              {/* Modal Content */}
              {viewSection === null ? (
                <>
                  {/* Button row */}
                  <div className="modal-button-group">
                    <button className="fancy-button" onClick={() => setViewSection("sets")}>
                      Sets
                    </button>
                    <button className="fancy-button" onClick={() => setViewSection("params")}>
                      Parameters
                    </button>
                    <button className="fancy-button" onClick={() => setViewSection("constraints")}>
                      Constraints
                    </button>
                    <button className="fancy-button" onClick={() => setViewSection("preferences")}>
                      Preferences
                    </button>
                  </div>

                  {/* Description under the buttons */}
                  <div className="modal-desc">
                    <p>{selectedImage.description}</p>
                  </div>
                </>

              ) : viewSection === "sets" ? (
                <div className="modal-section-data sets-modal">
                  {/* Header with dropdown on left and button on right */}
                  <div
                    className="sets-header"
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "space-between",
                      marginBottom: "12px"
                    }}
                  >
                    <select
                      id="set-select"
                      className="sets-header__select"
                      value={selectedSetIndex}
                      onChange={e => setSelectedSetIndex(Number(e.target.value))}
                    >
                      {selectedImage.sets.map((set, idx) => (
                        <option key={idx} value={idx}>
                          {set.setDefinition.alias}
                        </option>
                      ))}
                    </select>

                    <button
                      className="add-row-button"
                      onClick={() => {
                        setEditingRow(-1);
                        setEditTupleValues(
                          selectedImage.sets[selectedSetIndex].setDefinition.structure.map(
                            () => ""
                          )
                        );
                      }}
                    >
                      Add New Value
                    </button>
                  </div>

                  {/* Tuple table */}
                  <table className="tuple-values-table">
                    <thead>
                      <tr>
                        {selectedImage.sets[selectedSetIndex].setDefinition.structure.map(
                          (col, ci) => (
                            <th key={ci}>
                              <div
                                style={{
                                  display: "flex",
                                  flexDirection: "column",
                                  alignItems: "center"
                                }}
                              >
                                <span>{col}</span>
                                <div style={{ marginTop: 4 }}>
                                  <button
                                    onClick={() =>
                                      setSortConfig({ colIndex: ci, direction: "asc" })
                                    }
                                    title="Sort Asc"
                                  >
                                    ▲
                                  </button>
                                  <button
                                    onClick={() =>
                                      setSortConfig({ colIndex: ci, direction: "desc" })
                                    }
                                    title="Sort Desc"
                                    style={{ marginLeft: 6 }}
                                  >
                                    ▼
                                  </button>
                                </div>
                              </div>
                            </th>
                          )
                        )}
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(() => {
                        const vals =
                          selectedImage.sets[selectedSetIndex].values || [];
                        let rows = vals.map(v =>
                          v
                            .slice(1, -1)
                            .split(",")
                            .map(c => c.trim())
                        );
                        const { colIndex, direction } = sortConfig;
                        if (colIndex !== null) {
                          rows = [...rows].sort((a, b) => {
                            const aVal = a[colIndex] ?? "";
                            const bVal = b[colIndex] ?? "";
                            if (aVal < bVal) return direction === "asc" ? -1 : 1;
                            if (aVal > bVal) return direction === "asc" ? 1 : -1;
                            return 0;
                          });
                        }

                        const toRender = [];
                        if (editingRow === -1) {
                          toRender.push(
                            <tr key="new">
                              {editTupleValues.map((val, ci) => (
                                <td key={ci}>
                                  <input
                                    className="tuple-edit-input"
                                    value={val}
                                    onChange={e => {
                                      const copy = [...editTupleValues];
                                      copy[ci] = e.target.value;
                                      setEditTupleValues(copy);
                                    }}
                                  />
                                </td>
                              ))}
                              <td>
                                <button
                                  onClick={() => {
                                    const joined = `<${editTupleValues.join(",")}>`;
                                    const img = { ...selectedImage };
                                    img.sets[selectedSetIndex].values.unshift(joined);
                                    setSelectedImage(img);
                                    setEditingRow(null);
                                  }}
                                >
                                  ✅
                                </button>
                                <button onClick={() => setEditingRow(null)}>✕</button>
                              </td>
                            </tr>
                          );
                        }

                        rows.forEach((row, ri) => {
                          const isEditing = editingRow === ri;
                          toRender.push(
                            <tr key={ri}>
                              {row.map((cell, ci) => (
                                <td key={ci}>
                                  {isEditing ? (
                                    <input
                                      className="tuple-edit-input"
                                      value={editTupleValues[ci] ?? row[ci]}
                                      onChange={e => {
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
                                    <button onClick={() => setEditingRow(null)}>✕</button>
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
                        });

                        return toRender;
                      })()}
                    </tbody>
                  </table>
                </div>
              ) : viewSection === "params" ? (
                <div className="modal-section-data parameters-modal">
                  {/* --- Parameters Section Start --- */}
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
                                    newImage.parameters[index].isEditing = false;
                                    delete newImage.parameters[index].tempValue;
                                    setSelectedImage(newImage);
                                  }}
                                >
                                  ✅
                                </button>
                                <button
                                  onClick={() => {
                                    const newImage = { ...selectedImage };
                                    newImage.parameters[index].isEditing = false;
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
                                  newImage.parameters[index].tempValue = param.value;
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
                                onChange={e => {
                                  const newImage = { ...selectedImage };
                                  newImage.parameters[index].tempValue = e.target.value;
                                  setSelectedImage(newImage);
                                }}
                                onKeyDown={e => {
                                  if (e.key === "Enter") {
                                    const newImage = { ...selectedImage };
                                    newImage.parameters[index].value =
                                      param.tempValue ?? param.value;
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
                  {/* --- Parameters Section End --- */}
                </div>
              ) : viewSection === "constraints" ? (
                <div className="modal-section-data constraints-modal">
                  {/* --- Constraints Section Start --- */}
                  {selectedImage.constraintModules.length === 0 ? (
                    <p>No constraints available.</p>
                  ) : (
                    selectedImage.constraintModules.map((mod, index) => (
                      <div key={index} className="module-box">
                        <div className="module-title">{mod.moduleName}</div>
                        <div className="module-description">{mod.description}</div>
                        <div className="module-checkbox">
                          <input
                            type="checkbox"
                            checked={mod.enabled ?? true}
                            onChange={() => {
                              const newImage = { ...selectedImage };
                              newImage.constraintModules[index].enabled = !(mod.enabled ?? true);
                              setSelectedImage(newImage);
                            }}
                          />
                          <label>Enabled</label>
                        </div>
                      </div>
                    ))
                  )}
                  {/* --- Constraints Section End --- */}
                </div>
              ) : (
                <div className="modal-section-data preferences-modal">
                  {/* --- Preferences Section Start --- */}
                  {selectedImage.preferenceModules.length === 0 ? (
                    <p>No preferences available.</p>
                  ) : (
                    selectedImage.preferenceModules.map((mod, index) => (
                      <div key={index} className="module-box">
                        <div className="module-title">{mod.moduleName}</div>
                        <div className="module-description">{mod.description}</div>
                        <div className="slider-container">
                          <label htmlFor={`slider-${index}`}>Value:</label>
                          <input
                            type="range"
                            id={`slider-${index}`}
                            min="0"
                            max="100"
                            value={mod.value ?? 50}
                            onChange={e => {
                              const newImage = { ...selectedImage };
                              newImage.preferenceModules[index].value = parseInt(e.target.value);
                              setSelectedImage(newImage);
                            }}
                          />
                          <span>{mod.value ?? 50}</span>
                        </div>
                      </div>
                    ))
                  )}
                  {/* --- Preferences Section End --- */}
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
