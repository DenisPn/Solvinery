import React, {
  useState,
  useEffect,
  useCallback,
} from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useZPL } from "../context/ZPLContext";
import "../Themes/MainTheme.css";
import "./MyImagesPage.css";

const PAGE_SIZE = 5;

const MyImagesPage = () => {
  /* ─────────────────────────────  Local filters & paging  ───────────────────────────── */
  const [filterName, setFilterName] = useState("");
  const [filterDescription, setFilterDescription] = useState("");
  const [criteria, setCriteria] = useState({ name: "", description: "", page: 0 });

  const [imagesMap, setImagesMap] = useState({});
  const [loading, setLoading] = useState(false);
  const [hasNext, setHasNext] = useState(false);
  const [hasPrev, setHasPrev] = useState(false);
  const [totalPages, setTotalPages] = useState(1);

  /* ─────────────────────────────  Modal / editing state  ───────────────────────────── */
  const [viewSection, setViewSection] = useState(null);          // null | "sets" | "params" | "constraints" | "preferences"
  const [selectedSetIndex, setSelectedSetIndex] = useState(0);

  const [sortConfig, setSortConfig] = useState({ colIndex: null, direction: "asc" });
  const [editingRow, setEditingRow] = useState(null);            // -1 =new, number = row index
  const [editTupleValues, setEditTupleValues] = useState([]);
  const [newTupleValues, setNewTupleValues] = useState([]);

  /* ─────────────────────────────  Context  ───────────────────────────── */
  const {
    userId,
    /* dozens of setters ↓ */
    SolutionResponse,setSolutionResponse,
    selectedImage, setSelectedImage,
    selectedImageId, setSelectedImageId,
    setVariables, setConstraints, setPreferences,
    setSelectedVars, setConstraintsModules, setPreferenceModules,
    setSetTypes, setSetAliases,
    setParamTypes, setParamAliases,
    setImageId, setImageName, setImageDescription, setZplCode,
    setIsEditMode,
  } = useZPL();
  const navigate = useNavigate();

  /* ─────────────────────────────  Fetch list  ───────────────────────────── */
  const fetchImages = useCallback(async () => {
    if (!userId) return;
    setLoading(true);
    try {
      const { data } = await axios.get(`/user/${userId}/image/view`, {
        params: {
          name: criteria.name || undefined,
          description: criteria.description || undefined,
          page: criteria.page,
          size: PAGE_SIZE,
        },
      });
      setImagesMap(data.images || {});
      setHasNext(Boolean(data.hasNext));
      setHasPrev(Boolean(data.hasPrevious));
      setTotalPages(data.totalPages || 1);
      console.log("Update3");
    } catch (err) {
      alert(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, [userId, criteria]);

  useEffect(() => { fetchImages(); }, [fetchImages]);

  /* ─────────────────────────────  Keep selectedImage sync  ───────────────────────────── */
  useEffect(() => {
    if (selectedImageId && imagesMap[selectedImageId]) {
      setSelectedImage(imagesMap[selectedImageId]);
    }
  }, [imagesMap, selectedImageId, setSelectedImage]);

  /* ─────────────────────────────  Initialise tuple inputs  ───────────────────────────── */
  useEffect(() => {
    if (!selectedImage) return;
    const struct = selectedImage.sets?.[selectedSetIndex]?.setDefinition?.structure || [];
    setNewTupleValues(Array(struct.length).fill(""));
    setEditingRow(null);
    setEditTupleValues([]);
  }, [selectedImage, selectedSetIndex]);

  /* ─────────────────────────────  Server-helpers  ───────────────────────────── */
  const updateImageOnServer = async () => {
    if (!selectedImage || !selectedImageId || !userId) return;
    setLoading(true);
    try {
      const payload = {
        variables: (selectedImage.variables || []).map((v) => ({
          identifier: v.identifier,
          structure: Array.isArray(v.structure) ? v.structure : [],
          alias: v.alias || v.identifier,
          objectiveValueAlias: v.objectiveValueAlias || "",
        })),
        constraintModules: (selectedImage.constraintModules || []).map((m) => ({
          moduleName: m.moduleName,
          description: m.description,
          constraints: m.constraints || [],
        })),
        preferenceModules: (selectedImage.preferenceModules || []).map((m) => ({
          moduleName: m.moduleName,
          description: m.description,
          preferences: m.preferences || [],
        })),
        sets: (selectedImage.sets || []).map((s) => ({
          setDefinition: {
            name: s.setDefinition.name,
            structure: s.setDefinition.structure || [],
            alias: s.setDefinition.alias,
          },
          values: s.values || [],
        })),
        parameters: (selectedImage.parameters || []).map((p) => ({
          parameterDefinition: {
            name: p.parameterDefinition.name,
            structure: p.parameterDefinition.structure,
            alias: p.parameterDefinition.alias,
          },
          value: p.value != null ? String(p.value) : "",
        })),
        name: selectedImage.name,
        description: selectedImage.description,
        code: selectedImage.code,
      };
      await axios.patch(
        `/user/${userId}/image/${selectedImageId}`,
        payload,
        { headers: { "Content-Type": "application/json" } }
      );
    } catch (err) {
      alert(`Update failed: ${err.response?.data?.message || err.message}`);
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  /* ─────────────────────────────  Actions  ───────────────────────────── */
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
    setLoading(true);

    const preferenceModulesScalars = {};
    selectedImage.preferenceModules.forEach((m) => {
      const raw = Number(m.value ?? 50);
      preferenceModulesScalars[m.moduleName] = Math.min(Math.max(raw / 100, 0), 1);
    });
    const enabledConstraintModules = selectedImage.constraintModules
      .filter((m) => m.enabled ?? true)
      .map((m) => m.moduleName);

      console.log("Solving with:", {
        preferenceModulesScalars,
        enabledConstraintModules
      });
    try {
      const resp = await axios.post(
        `/user/${userId}/image/${selectedImageId}/solver`,
        { preferenceModulesScalars, enabledConstraintModules, timeout: 20 }
      );
      
      setSolutionResponse(resp.data);
      setViewSection(null);



      if(resp.data && resp.data.solved === true && resp.data.solution !== null ) {
        navigate("/solution-results");
      }
      else {
        alert("No solution found or the problem is infeasible.");
        setSolutionResponse(null);
      }
      
    } catch (err) {
      alert(`Solve error: ${err.message}`);
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteImage = async () => {
    if (!selectedImageId || !userId) return;
    try {
      await axios.delete(`/user/${userId}/image/${selectedImageId}`);
      alert("Deleted");
      setSelectedImage(null);
      setSelectedImageId(null);
      setViewSection(null);
      fetchImages();
    } catch (err) {
      alert(`Delete failed: ${err.message}`);
    }
  };

  const handleEditImage = async () => {
    if (!selectedImageId || !selectedImage) return;
    if (!window.confirm("Editing image requires understanding of the ZPL model. Continue?")) return;

    await updateImageOnServer();

    /* 1) basic fields */
    setImageId(selectedImageId);
    setImageName(selectedImage.name);
    setImageDescription(selectedImage.description);
    setZplCode(selectedImage.code);

    /* 2) variables picked by user */
    setSelectedVars(selectedImage.variables || []);

    /* 3) fetch full model */
    try {
      const { data } = await axios.post(
        `/user/${userId}/image/model`,
        { code: selectedImage.code },
        { headers: { "Content-Type": "application/json" } }
      );
      setVariables(data.variables || []);
      setConstraints(data.constraints || []);
      setPreferences(data.preferences || []);
    } catch (err) {
      alert(`Failed to load model: ${err.response?.data?.msg || err.message}`);
    }

    /* 4) constraint modules */
    setConstraintsModules(
      (selectedImage.constraintModules || []).map((m) => ({
        name: m.moduleName,
        description: m.description,
        constraints: m.constraints.map((c) => ({ identifier: c })),
      }))
    );

    /* 5) preference modules */
    setPreferenceModules(
      (selectedImage.preferenceModules || []).map((m) => ({
        name: m.moduleName,
        description: m.description,
        preferences: m.preferences.map((p) => ({ identifier: p })),
      }))
    );

    /* 6) sets + aliases */
    const st = {};
    const sa = {};
    (selectedImage.sets || []).forEach((s) => {
      st[s.setDefinition.name] = s.setDefinition.structure;
      sa[s.setDefinition.name] = { alias: s.setDefinition.alias, typeAlias: s.setDefinition.structure };
    });
    setSetTypes(st);
    setSetAliases(sa);

    /* 7) params → maps */
    const pt = {};
    const pa = {};
    (selectedImage.parameters || []).forEach((p) => {
      pt[p.parameterDefinition.name] = p.parameterDefinition.structure;
      pa[p.parameterDefinition.name] = {
        alias: p.parameterDefinition.alias,
        typeAlias: p.parameterDefinition.typeAlias,
      };
    });
    setParamTypes(pt);
    setParamAliases(pa);

    /* 8) go edit */
    setIsEditMode(true);
    navigate("/configure-variables");
  };

  const handleCopyCode = () => {
    if (!selectedImage?.code) return;
    navigator.clipboard.writeText(selectedImage.code).then(
      () => alert("Copied"),
      () => alert("Copy failed")
    );
  };

  const ActionBtn = ({ src, alt, onClick }) => (
    <span className="mi-action-wrapper" data-tip={alt}>
      <img src={src} alt={alt} className="mi-icon-button" onClick={onClick} />
    </span>
  );

  /* ─────────────────────────────  JSX  ───────────────────────────── */
  return (
    <div className="mi-bg">
      {/* ───────── Loader ───────── */}
      {loading && (
        <div className="mi-modal-overlay" style={{ zIndex: 9999 }}>
          <div className="mi-spinner-modal">
            <div className="mi-spinner" />
            <p className="mi-loading">Loading…</p>
          </div>
        </div>
      )}

      {/* ───────── Home button ───────── */}
      {!selectedImage && (
        <img
          src={`${process.env.PUBLIC_URL}/images/HomeButton.png`}
          alt="Home"
          className="mi-home-btn"
          onClick={() => navigate("/main-page")}
        />
      )}

      {/* ───────── Page shell ───────── */}
      <div className="mi-container">
        <h1 className="mi-title">My Images</h1>

        {/* ===== Filter row ===== */}
<div className="filters-card">
  <div className="filter-grid">
    <input
      className="mi-filter-input"
      placeholder="Name"
      value={filterName}
      onChange={(e) => setFilterName(e.target.value)}
    />
    <input
      className="mi-filter-input"
      placeholder="Description"
      value={filterDescription}
      onChange={(e) => setFilterDescription(e.target.value)}
    />
   
  </div>

  <div className="search-button-container">
    <button
      className="search-button"
      onClick={() =>
        setCriteria({ name: filterName, description: filterDescription, page: 0 })
      }
    >
      Search
    </button>
  </div>
</div>

        {/* ===== Thumbnails grid ===== */}
        <div className="mi-images-section">
          {Object.keys(imagesMap).length === 0 ? (
            <p>No images available.</p>
          ) : (
            Object.entries(imagesMap).map(([id, img]) => (
              <div key={id} className="tooltip">
                <div
                  className="mi-image-item"
                  onClick={() => {
                    setSelectedImage(img);
                    setSelectedImageId(id);
                  }}
                >
                  <div className="mi-thumbnail-text">
                    <h4>{img.name}</h4>
                  </div>
                </div>
                {img.description && (
                  <div className="tooltip-bubble">{img.description}</div>
                )}
              </div>
            ))
          )}
        </div>

        {/* ===== Pagination ===== */}
        <div className="mi-pagination">
          <img
            src={`${process.env.PUBLIC_URL}/images/LeftArrowButton.png`}
            alt="Prev"
            className="mi-prev-btn"
            style={{ opacity: hasPrev ? 1 : 0.3 }}
            onClick={() => hasPrev && setCriteria((c) => ({ ...c, page: c.page - 1 }))}
          />
          <span>Page {criteria.page + 1} / {totalPages}</span>
          <img
            src={`${process.env.PUBLIC_URL}/images/RightArrowButton.png`}
            alt="Next"
            className="mi-next-btn"
            style={{ opacity: hasNext ? 1 : 0.3 }}
            onClick={() => hasNext && setCriteria((c) => ({ ...c, page: c.page + 1 }))}
          />
        </div>

        {/* ════════════════════════  Modal  ════════════════════════ */}
        {selectedImage && (
          <div
            className="mi-modal-overlay"
            onClick={() => {
              setSelectedImage(null);
              setSelectedImageId(null);
              setViewSection(null);
            }}
          >
            <div
              className="mi-modal"
              onClick={(e) => e.stopPropagation()}
            >
              {/* top-left close / back */}
              {viewSection === null ? (
                <span
                  className="mi-action-wrapper"
                  data-tip={viewSection === null ? 'Close' : 'Back'}
                >
                  <img
                    src={`${process.env.PUBLIC_URL}/images/ExitButton2.png`}
                    alt={viewSection === null ? 'Close' : 'Back'}
                    className="mi-modal-close-btn"
                    onClick={async () => {
                      if (viewSection === null) {
                        await updateImageOnServer();
                        setSelectedImage(null);
                        setSelectedImageId(null);
                      }
                      setViewSection(null);
                    }}
                  />
                </span>
              ) : (
                <img
                  src={`${process.env.PUBLIC_URL}/images/ExitButton2.png`}
                  alt="Back"
                  className="mi-modal-close-btn"
                  onClick={() => setViewSection(null)}
                />
              )}

              {/* top-right actions */}
              <div className="mi-action-bar">
                <span className="tooltip tooltip-down mi-action-wrapper">
                  <img
                    src={`${process.env.PUBLIC_URL}/images/PublishButton.png`}
                    alt="Publish"
                    className="mi-icon-button"
                    onClick={handlePublishImage}
                  />
                  <div className="tooltip-bubble">Publish</div>
                </span>

                <span className="tooltip tooltip-down mi-action-wrapper">
                  <img
                    src={`${process.env.PUBLIC_URL}/images/Solve.png`}
                    alt="Solve"
                    className="mi-icon-button"
                    onClick={handleSolveImage}
                  />
                  <div className="tooltip-bubble">Solve</div>
                </span>

                <span className="tooltip tooltip-down mi-action-wrapper">
                  <img
                    src={`${process.env.PUBLIC_URL}/images/EditButton.png`}
                    alt="Edit"
                    className="mi-icon-button"
                    onClick={handleEditImage}
                  />
                  <div className="tooltip-bubble">Edit</div>
                </span>

                <span className="tooltip tooltip-down mi-action-wrapper">
                  <img
                    src={`${process.env.PUBLIC_URL}/images/CopyZPLButton.png`}
                    alt="Copy code"
                    className="mi-icon-button"
                    onClick={handleCopyCode}
                  />
                  <div className="tooltip-bubble">Copy code</div>
                </span>

                <span className="tooltip tooltip-down mi-action-wrapper">
                  <img
                    src={`${process.env.PUBLIC_URL}/images/delete.png`}
                    alt="Delete"
                    className="mi-icon-button"
                    onClick={handleDeleteImage}
                  />
                  <div className="tooltip-bubble">Delete</div>
                </span>
              </div>

              {/* ====== Tabs ====== */}
              {viewSection === null ? (
                <>
                  <div className="mi-tab-group">
                    <button className="mi-tab-btn" onClick={() => setViewSection("sets")}>
                      Sets
                    </button>
                    <button className="mi-tab-btn" onClick={() => setViewSection("params")}>
                      Parameters
                    </button>
                    <button className="mi-tab-btn" onClick={() => setViewSection("constraints")}>
                      Constraints
                    </button>
                    <button className="mi-tab-btn" onClick={() => setViewSection("preferences")}>
                      Preferences
                    </button>
                  </div>

                  <div className="mi-modal-desc">
                    <h2 className="mi-modal-title">{selectedImage.name}</h2>
                    <p>{selectedImage.description}</p>
                  </div>
                </>
              ) : viewSection === "sets" ? (
                /* ===============================================  SETS PANEL  =============================================== */
                <div className="mi-sets-panel">
                  {/* header: dropdown + add-new */}
                  {/* Move dropdown + button BELOW top row */}
                  <div className="mi-sets-controls">
                    <select
                      className="mi-sets-dropdown"
                      value={selectedSetIndex}
                      onChange={(e) => setSelectedSetIndex(Number(e.target.value))}
                    >
                      {selectedImage.sets.map((s, i) => (
                        <option key={i} value={i}>
                          {s.setDefinition.alias}
                        </option>
                      ))}
                    </select>

                    <button
                      className="mi-sets-add-btn"
                      onClick={() => {
                        setEditingRow(-1);
                        setEditTupleValues(
                          selectedImage.sets[selectedSetIndex].setDefinition.structure.map(() => "")
                        );
                      }}
                    >
                      Add New Value
                    </button>
                  </div>


                  {/* tuple table */}
                  <table className="mi-tuple-table">
                    <thead>
                      <tr>
                        {selectedImage.sets[selectedSetIndex].setDefinition.structure.map((col, ci) => (
                          <th key={ci}>
                            <span className="mi-col-title">{col}</span>
                            <button
                              className="mi-sort-btn"
                              onClick={() => {
                                setSortConfig((prev) => {
                                  if (prev.colIndex !== ci) return { colIndex: ci, direction: "asc" };
                                  if (prev.direction === "asc") return { colIndex: ci, direction: "desc" };
                                  return { colIndex: null, direction: "asc" };   // חזרה ל־unsorted
                                });
                              }}
                            >
                              {sortConfig.colIndex === ci
                                ? sortConfig.direction === "asc"
                                  ? "▲"
                                  : "▼"
                                : "⇅"}
                            </button>

                          </th>
                        ))}
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(() => {
                        const vals = selectedImage.sets[selectedSetIndex].values || [];
                        let rows = vals.map((v) =>
                          v.slice(1, -1).split(",").map((c) => c.trim().replace(/^"|"$/g, ""))
                        );

                        const { colIndex, direction } = sortConfig;

                        if (colIndex !== null) {
                          rows = [...rows].sort((a, b) => {
                            /* הערכים לפני ניקוי */
                            const rawA = a[colIndex] ?? "";
                            const rawB = b[colIndex] ?? "";

                            /* נסה להפוך למספר */
                            const numA = Number(rawA);
                            const numB = Number(rawB);

                            let cmp;

                            // אם שני הערכים מספריים – השווה מספרית
                            if (!isNaN(numA) && !isNaN(numB)) {
                              cmp = numA - numB;          //  120-9 = חיובי => a>b
                            } else {
                              // אחרת – השוואה אלפאביתית (Locale)
                              cmp = rawA.localeCompare(rawB);
                            }

                            // כיוון המיון
                            return direction === "asc" ? cmp : -cmp;
                          });
                        }

                        const renderRows = [];

                        /* new-row editor */
                        if (editingRow === -1) {
                          renderRows.push(
                            <tr key="new">
                              {editTupleValues.map((val, ci) => (
                                <td key={ci}>
                                  <input
                                    className="mi-tuple-input"
                                    value={val}
                                    onChange={(e) => {
                                      const c = [...editTupleValues];
                                      c[ci] = e.target.value;
                                      setEditTupleValues(c);
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

                        /* existing rows */
                        rows.forEach((row, ri) => {
                          const isEditing = editingRow === ri;
                          renderRows.push(
                            <tr key={ri}>
                              {row.map((cell, ci) => (
                                <td key={ci}>
                                  {isEditing ? (
                                    <input
                                      className="mi-tuple-input"
                                      value={editTupleValues[ci] ?? row[ci]}
                                      onChange={(e) => {
                                        const c = [...editTupleValues];
                                        c[ci] = e.target.value;
                                        setEditTupleValues(c);
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
                                        const newString = `<${editTupleValues.join(",")}>`;
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

                        return renderRows;
                      })()}
                    </tbody>
                  </table>
                </div>
              ) : viewSection === "params" ? (
                /* ===============================================  PARAMETERS PANEL  =============================================== */
                <div className="mi-params-panel">
                  <table className="mi-params-table">
                    <thead>
                      <tr>
                        <th>Edit</th>
                        <th>Parameter Name</th>
                        <th>Value</th>
                      </tr>
                    </thead>
                    <tbody>
                      {selectedImage.parameters.map((p, i) => (
                        <tr key={i}>
                          <td className="mi-param-cell-edit">
                            {p.isEditing ? (
                              <>
                                <button
                                  className="mi-param-btn"
                                  onClick={() => {
                                    const img = { ...selectedImage };
                                    img.parameters[i].value = p.tempValue ?? p.value;
                                    img.parameters[i].isEditing = false;
                                    delete img.parameters[i].tempValue;
                                    setSelectedImage(img);
                                  }}
                                >
                                  ✅
                                </button>
                                <button
                                  className="mi-param-btn"
                                  onClick={() => {
                                    const img = { ...selectedImage };
                                    img.parameters[i].isEditing = false;
                                    delete img.parameters[i].tempValue;
                                    setSelectedImage(img);
                                  }}
                                >
                                  ✕
                                </button>
                              </>
                            ) : (
                              <button
                                className="mi-param-btn"
                                onClick={() => {
                                  const img = { ...selectedImage };
                                  img.parameters[i].isEditing = true;
                                  img.parameters[i].tempValue = p.value;
                                  setSelectedImage(img);
                                }}
                              >
                                ✎
                              </button>
                            )}
                          </td>
                          <td>{p.parameterDefinition.alias || "—"}</td>
                          <td className="mi-param-cell-value">
                            {p.isEditing ? (
                              <input
                                type="text"
                                className="mi-param-input"
                                value={p.tempValue}
                                onChange={(e) => {
                                  const img = { ...selectedImage };
                                  img.parameters[i].tempValue = e.target.value;
                                  setSelectedImage(img);
                                }}
                                onKeyDown={(e) => {
                                  if (e.key === "Enter") {
                                    const img = { ...selectedImage };
                                    img.parameters[i].value = p.tempValue ?? p.value;
                                    img.parameters[i].isEditing = false;
                                    delete img.parameters[i].tempValue;
                                    setSelectedImage(img);
                                  }
                                }}
                              />
                            ) : (
                              p.value
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : viewSection === "constraints" ? (
                /* ===============================================  CONSTRAINTS PANEL  =============================================== */
                <div className="mi-constraints-panel">
                  {selectedImage.constraintModules.map((m, idx) => (
                    <div key={idx} className="mi-module-box">
                      <div className="mi-header-row">
                        <div className="mi-module-title">{m.moduleName}</div>
                        <label className="mi-module-checkbox">
                          <input
                            type="checkbox"
                            checked={m.enabled ?? true}
                            onChange={(e) => {
                              const img = { ...selectedImage };
                              img.constraintModules[idx].enabled = e.target.checked;
                              setSelectedImage(img);
                            }}
                          />
                        </label>
                      </div>
                      <div className="mi-module-desc">{m.description}</div>
                    </div>
                  ))}
                </div>
              ) : (
                /* ===============================================  PREFERENCES PANEL  =============================================== */
                <div className="mi-prefs-panel">
                  {selectedImage.preferenceModules.length === 0 ? (
                    <p>No preferences available.</p>
                  ) : (
                    selectedImage.preferenceModules.map((m, idx) => (
                      <div key={idx} className="mi-module-box">
                        <div className="mi-header-row">
                          <div className="mi-module-title">{m.moduleName}</div>
                          <div className="mi-slider-info">
                            <span className="mi-info-icon">
                              ?
                              <div className="mi-info-tooltip">
                                Adjusts how strongly this preference is applied (0–100).
                              </div>
                            </span>
                            <label htmlFor={`slider-${idx}`}>Value:</label>
                            <input
                              id={`slider-${idx}`}
                              type="range"
                              min="0"
                              max="100"
                              value={m.value ?? 100}
                              onChange={(e) => {
                                const img = { ...selectedImage };
                                img.preferenceModules[idx].value = Number(e.target.value);
                                setSelectedImage(img);
                              }}
                            />
                            <span>{m.value ?? 50}</span>
                          </div>
                        </div>
                        <div className="mi-module-desc">{m.description}</div>
                      </div>
                    ))
                  )}
                </div>
              )}
              {/* ====== /panel ====== */}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MyImagesPage;
