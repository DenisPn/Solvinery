import React, { useState, useEffect, useLayoutEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import * as XLSX from 'xlsx';
import svgPanZoom from 'svg-pan-zoom';
import './SolutionResultsPage.css';

export default function SolutionResultsPage() {
  const { solutionResponse, setSelectedImage, setSelectedImageId } = useZPL();
  const solutionMap = solutionResponse?.solution || {};

  const [sortKey, setSortKey] = useState(null);
  const [sortAsc, setSortAsc] = useState(true);
  const [filters, setFilters] = useState({});
  const [selectedVar, setSelectedVar] = useState('');
  const [view, setView] = useState('Table');
  const [graphType, setGraphType] = useState('line');
  const [showConfig, setShowConfig] = useState(false);
  const [mapping, setMapping] = useState({ rowIndex: 0, colIndex: 1, cellIndex: 2 });
  const [isTransposed, setIsTransposed] = useState(false);
  const [pivotSort, setPivotSort] = useState({ key: null, asc: true });

  const navigate = useNavigate();

  const snapInt = (x, tol = 1e-5) => {
    const f = Math.floor(x), c = Math.ceil(x);
    if (Math.abs(x - f) < tol) return f;
    if (Math.abs(x - c) < tol) return c;
    return x;
  };

  useEffect(() => {
    if (solutionResponse) console.log('SolutionResponse:', solutionResponse);
  }, [solutionResponse]);

  const variableNames = Object.keys(solutionMap);
  useEffect(() => {
    if (variableNames.length && !selectedVar) setSelectedVar(variableNames[0]);
  }, [variableNames, selectedVar]);

  useLayoutEffect(() => {
    if (view === 'Graph') {
      const instance = svgPanZoom('#myGraph', {
        zoomEnabled: true,
        controlIconsEnabled: true,
        fit: true,
        center: true,
        minZoom: 0.5,
        maxZoom: 20,
        zoomScaleSensitivity: 0.2,
      });
      return () => instance.destroy();
    }
  }, [view, selectedVar,graphType]);

  const varData = solutionMap[selectedVar] || {};
  const solutions = Array.from(varData.solutions || []);
  const columnTypes = varData.typeStructure || [];
  const showObjective = solutions.some(s => s.objectiveValue !== 1);
  const objectiveLabel = varData.objectiveValueAlias || 'Objective Value';

  useEffect(() => {
    const init = {};
    columnTypes.forEach((_, i) => (init[i] = ''));
    if (showObjective) init['objective'] = '';
    setFilters(init);
  }, [columnTypes, showObjective]);

  const handleFilterChange = (key, value) => setFilters(f => ({ ...f, [key]: value }));

  const withSnapped = solutions.map(s => ({ ...s, snappedObjective: snapInt(s.objectiveValue) }));

  let sortedSolutions = [...withSnapped];
  if (sortKey !== null) {
    sortedSolutions.sort((a, b) => {
      const va = sortKey === 'objective' ? a.snappedObjective : a.values[sortKey];
      const vb = sortKey === 'objective' ? b.snappedObjective : b.values[sortKey];
      const na = Number(va), nb = Number(vb);
      if (!isNaN(na) && !isNaN(nb)) return sortAsc ? na - nb : nb - na;
      const da = Date.parse(va), db = Date.parse(vb);
      if (!isNaN(da) && !isNaN(db)) return sortAsc ? da - db : db - da;
      return sortAsc ? String(va).localeCompare(String(vb)) : String(vb).localeCompare(String(va));
    });
  }

  const filteredSolutions = sortedSolutions.filter(sol =>
    Object.entries(filters).every(([key, filter]) => {
      if (!filter) return true;
      const cell = key === 'objective' ? sol.snappedObjective : sol.values[Number(key)];
      return String(cell).toLowerCase().includes(filter.toLowerCase());
    })
  );

  const handleExport = () => {
    const wb = XLSX.utils.book_new();
    variableNames.forEach(varName => {
      const d = solutionMap[varName];
      const cols = d.typeStructure || [];
      const sols = Array.from(d.solutions || []);
      const hasObj = sols.some(s => s.objectiveValue !== 1);
      const hdr = [...cols];
      if (hasObj) hdr.push(d.objectiveValueAlias || 'Objective Value');
      const aoa = [hdr, ...sols.map(s => [...s.values, ...(hasObj ? [Math.floor(s.objectiveValue)] : [])])];
      const ws = XLSX.utils.aoa_to_sheet(aoa);
      XLSX.utils.book_append_sheet(wb, ws, varName.substring(0, 31));
    });
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([wbout], { type: 'application/octet-stream' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'solutions.xlsx';
    a.click();
  };

  // Logic for 3-column pivot
  const rows = [], cols = [], cellMap = {};
  if (view === 'Pivot' && columnTypes.length === 3) {
    solutions.forEach(sol => {
      const r = sol.values[mapping.rowIndex], c = sol.values[mapping.colIndex], v = sol.values[mapping.cellIndex];
      if (!rows.includes(r)) rows.push(r);
      if (!cols.includes(c)) cols.push(c);
      cellMap[`${r}__${c}`] = v;
    });
    if (columnTypes[mapping.colIndex] === 'INT') cols.sort((a, b) => a - b);
  }

  // Sorting logic for 3-column pivot
  let sortedRows = [...rows];
  if (view === 'Pivot' && columnTypes.length === 3 && pivotSort.key !== null) {
    sortedRows.sort((a, b) => {
      const valA = cellMap[`${a}__${pivotSort.key}`] || '';
      const valB = cellMap[`${b}__${pivotSort.key}`] || '';
      
      const numA = Number(valA);
      const numB = Number(valB);

      let comparison = 0;
      if (!isNaN(numA) && !isNaN(numB)) {
        comparison = numA - numB;
      } else {
        comparison = String(valA).localeCompare(String(valB));
      }

      return pivotSort.asc ? comparison : -comparison;
    });
  }

  // Logic for 2-column pivot
  const rows2D = [], cols2D = [], cellMap2D = {};
  if (view === 'Pivot' && columnTypes.length === 2) {
    const rowIndex = isTransposed ? 1 : 0;
    const colIndex = isTransposed ? 0 : 1;

    solutions.forEach(sol => {
      const r = sol.values[rowIndex];
      const c = sol.values[colIndex];
      const v = snapInt(sol.objectiveValue);

      if (!rows2D.includes(r)) rows2D.push(r);
      if (!cols2D.includes(c)) cols2D.push(c);
      cellMap2D[`${r}__${c}`] = v;
    });
  }

  // Sorting logic for 2-column pivot
  let sortedRows2D = [...rows2D];
  if (view === 'Pivot' && columnTypes.length === 2 && pivotSort.key !== null) {
    sortedRows2D.sort((a, b) => {
      const valA = cellMap2D[`${a}__${pivotSort.key}`] || -Infinity;
      const valB = cellMap2D[`${b}__${pivotSort.key}`] || -Infinity;
      
      const comparison = valA - valB;
      
      return pivotSort.asc ? comparison : -comparison;
    });
  }

  const handleMappingChange = (roleToUpdate, selectedIndex) => {
    const newIndex = Number(selectedIndex);
    const currentMapping = { ...mapping };

    const roleToSwap = Object.keys(currentMapping).find(
      (key) => currentMapping[key] === newIndex
    );
    
    const oldIndexOfRole = currentMapping[roleToUpdate];

    if (roleToSwap) {
      currentMapping[roleToSwap] = oldIndexOfRole;
    }
    currentMapping[roleToUpdate] = newIndex;

    setMapping(currentMapping);
    setPivotSort({ key: null, asc: true });
  };
  
  const handlePivotSort = (colKey) => {
    setPivotSort(prev => {
      if (prev.key !== colKey) return { key: colKey, asc: true };
      if (prev.asc) return { ...prev, asc: false };
      return { key: null, asc: true };
    });
  };

  const publicUrl = process.env.PUBLIC_URL;

  return (
    <div className="solution-container">
      {showConfig && (
        <div className="modal-overlay" onClick={() => setShowConfig(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3 style={{ marginTop: 0 }}>Configure Pivot Table</h3>
            <p>Assign a data column to each role in the pivot table.</p>
            <table className="pivot-config-table">
              <tbody>
                <tr>
                  <td><strong>Rows</strong></td>
                  <td>
                    <select
                      className="var-select"
                      value={mapping.rowIndex}
                      onChange={(e) => handleMappingChange('rowIndex', e.target.value)}
                    >
                      {columnTypes.map((type, i) => (
                        <option key={`row-${i}`} value={i}>{type} (Column {i+1})</option>
                      ))}
                    </select>
                  </td>
                </tr>
                <tr>
                  <td><strong>Columns</strong></td>
                  <td>
                    <select
                      className="var-select"
                      value={mapping.colIndex}
                      onChange={(e) => handleMappingChange('colIndex', e.target.value)}
                    >
                      {columnTypes.map((type, i) => (
                        <option key={`col-${i}`} value={i}>{type} (Column {i+1})</option>
                      ))}
                    </select>
                  </td>
                </tr>
                <tr>
                  <td><strong>Cell Value</strong></td>
                  <td>
                    <select
                      className="var-select"
                      value={mapping.cellIndex}
                      onChange={(e) => handleMappingChange('cellIndex', e.target.value)}
                    >
                      {columnTypes.map((type, i) => (
                        <option key={`cell-${i}`} value={i}>{type} (Column {i+1})</option>
                      ))}
                    </select>
                  </td>
                </tr>
              </tbody>
            </table>
            <button className="config-btn" onClick={() => setShowConfig(false)}>Done</button>
          </div>
        </div>
      )}

      <div className="top-controls">
        <Link to="/main-page"
          onClick={e => { e.preventDefault(); setSelectedImage(null); setSelectedImageId(null); navigate('/main-page'); }}
          className="nav-btn home-btn"
          style={{ backgroundImage: `url(${publicUrl}/images/HomeButton.png)` }}
          title="Home"
        />
        <Link to="/my-images"
          className="nav-btn images-btn"
          style={{ backgroundImage: `url(${publicUrl}/images/ExitButton2.png)` }}
          title="My Images"
        />
        <img
          src={`${publicUrl}/images/excel.png`}
          alt="Export to Excel"
          className="export-btn"
          onClick={handleExport}
          title="Export to Excel"
        />
        <label htmlFor="var-select" className="var-label">Variable:</label>
        <select
          id="var-select"
          className="var-select"
          value={selectedVar}
          // ==================== START: MODIFIED CODE ====================
          onChange={e => {
            setSelectedVar(e.target.value);
            setView('Table'); // Reset the view to the default
            setIsTransposed(false);
            setPivotSort({ key: null, asc: true }); 
          }}
          // ===================== END: MODIFIED CODE =====================
        >
          {variableNames.map(n => <option key={n} value={n}>{n}</option>)}
        </select>
        <label htmlFor="view-select" className="var-label">View:</label>
        <select
          id="view-select"
          className="var-select"
          value={view}
          onChange={e => {
            setView(e.target.value);
            setShowConfig(false);
            setIsTransposed(false);
            setPivotSort({ key: null, asc: true }); 
          }}
        >
          <option value="Table">Table</option>
          {(columnTypes.length === 2 || columnTypes.length === 3) && <option value="Pivot">Pivot</option>}
          {columnTypes.length === 1 && <option value="Graph">Graph</option>}
          {columnTypes.includes('DATE') && <option value="Calendar">Calendar</option>}
        </select>
        {view === 'Pivot' && columnTypes.length === 3 && (
          <button className="config-btn" onClick={() => setShowConfig(true)}>
            Configure Pivot
          </button>
        )}
        {view === 'Graph' && columnTypes.length === 1 && (
          <>
            <label htmlFor="chart-select" className="var-label">Chart:</label>
            <select
              id="chart-select"
              className="var-select"
              value={graphType}
              onChange={e => setGraphType(e.target.value)}
            >
              <option value="bar">Columns</option>
              <option value="point">Points</option>
              <option value="line">Line</option>
            </select>
          </>
        )}
      </div>

      <div className={`table-wrapper ${view.toLowerCase()}-view`}>
        {view === 'Table' && (
          <table className="solutions-table">
            <thead>
              <tr>
                {columnTypes.map((t, i) => (
                  <th key={i} className="has-tooltip" data-tooltip={t}>
                    <div className="cell-content">
                      {t}{' '}
                      <button className="sort-btn" onClick={() => {
                        if (sortKey === i) setSortAsc(a => !a);
                        else { setSortKey(i); setSortAsc(true); }
                      }}>⇅</button>
                    </div>
                  </th>
                ))}
                {showObjective && (
                  <th className="has-tooltip" data-tooltip={objectiveLabel}>
                    <div className="cell-content">
                      {objectiveLabel}{' '}
                      <button className="sort-btn" onClick={() => {
                        if (sortKey === 'objective') setSortAsc(a => !a);
                        else { setSortKey('objective'); setSortAsc(true); }
                      }}>⇅</button>
                    </div>
                  </th>
                )}
              </tr>
              <tr>
                {columnTypes.map((_, i) => (
                  <th key={`filter-${i}`}>
                    <input
                      type="text"
                      className="filter-input"
                      placeholder="Search..."
                      value={filters[i] || ''}
                      onChange={e => handleFilterChange(i.toString(), e.target.value)}
                    />
                  </th>
                ))}
                {showObjective && (
                  <th>
                    <input
                      type="text"
                      className="filter-input"
                      placeholder="Search..."
                      value={filters['objective'] || ''}
                      onChange={e => handleFilterChange('objective', e.target.value)}
                    />
                  </th>
                )}
              </tr>
            </thead>
            <tbody>
              {filteredSolutions.map(sol => (
                <tr key={sol.values.join('-')}>
                  {sol.values.map((v, idx) => (
                    <td key={idx} className="has-tooltip" data-tooltip={v}>
                      <div className="cell-content">{v}</div>
                    </td>
                  ))}
                  {showObjective && (
                    <td className="has-tooltip" data-tooltip={sol.snappedObjective}>
                      <div className="cell-content">{sol.snappedObjective}</div>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {view === 'Pivot' && columnTypes.length === 3 && (
          <table className="pivot-table">
            <thead>
              <tr>
                <th className="has-tooltip" data-tooltip={columnTypes[mapping.rowIndex]}>
                  <div className="cell-content">{columnTypes[mapping.rowIndex]}</div>
                </th>
                {cols.map(c => (
                  <th key={c} className="has-tooltip" data-tooltip={c}>
                    <div className="cell-content">
                      {c}
                      <button className="sort-btn" onClick={() => handlePivotSort(c)}>
                        {pivotSort.key === c ? (pivotSort.asc ? '▲' : '▼') : '⇅'}
                      </button>
                    </div>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {sortedRows.map(r => (
                <tr key={r}>
                  <td className="has-tooltip" data-tooltip={r}>
                    <div className="cell-content"><strong>{r}</strong></div>
                  </td>
                  {cols.map(c => {
                    const cellValue = cellMap[`${r}__${c}`] || '';
                    return (
                      <td key={c} className="has-tooltip" data-tooltip={cellValue}>
                        <div className="cell-content">{cellValue}</div>
                      </td>
                    )
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {view === 'Pivot' && columnTypes.length === 2 && (
          <div className="pivot-container">
            <button onClick={() => setIsTransposed(t => !t)} className="config-btn" style={{marginBottom: '10px'}}>
              Switch Rows/Columns
            </button>
            <table className="pivot-table">
              <thead>
                <tr>
                  <th className="has-tooltip" data-tooltip={`${isTransposed ? columnTypes[1] : columnTypes[0]} / ${isTransposed ? columnTypes[0] : columnTypes[1]}`}>
                    <div className="cell-content">
                      {isTransposed ? columnTypes[1] : columnTypes[0]} / {isTransposed ? columnTypes[0] : columnTypes[1]}
                    </div>
                  </th>
                  {cols2D.map(c => (
                    <th key={c} className="has-tooltip" data-tooltip={c}>
                      <div className="cell-content">
                        {c}
                        <button className="sort-btn" onClick={() => handlePivotSort(c)}>
                          {pivotSort.key === c ? (pivotSort.asc ? '▲' : '▼') : '⇅'}
                        </button>
                      </div>
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {sortedRows2D.map(r => (
                  <tr key={r}>
                    <td className="has-tooltip" data-tooltip={r}>
                      <div className="cell-content"><strong>{r}</strong></div>
                    </td>
                    {cols2D.map(c => {
                      const cellValue = cellMap2D[`${r}__${c}`] ?? '';
                      return (
                        <td key={c} className="has-tooltip" data-tooltip={cellValue}>
                          <div className="cell-content">{cellValue}</div>
                        </td>
                      )
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {view === 'Graph' && columnTypes.length === 1 && (
          <div
            className="graph-wrapper"
            key={`${selectedVar}-${graphType}`}
          >
            <svg
              id="myGraph"
              width="100%"
              height="100%"
              viewBox="0 0 700 500"
              preserveAspectRatio="xMidYMid meet"
              style={{ display: 'block' }}
            >
              {Array.from({ length: 6 }, (_, i) => {
                const maxY = Math.max(...solutions.map(s => snapInt(s.objectiveValue)));
                const yVal = Math.round((i / 5) * maxY);
                const yPos = 500 - 40 - (yVal / maxY * (500 - 60));
                return (
                  <g key={i} transform={`translate(0,${yPos})`}>
                    <line x1={40} x2={660} stroke="#eee" />
                    <text x={32} dy="0.32em" textAnchor="end" fontSize="12" fill="#333">
                      {yVal}
                    </text>
                  </g>
                );
              })}
              {solutions.map((s, i) => {
                const x = 40 + (i / (solutions.length - 1 || 1)) * (660 - 40);
                return (
                  <text key={i} x={x} y={480} textAnchor="middle" fontSize="12" fill="#333">
                    {s.values[0]}
                  </text>
                );
              })}
              {(() => {
                const pts = solutions.map((s, i) => ({
                  x: 40 + (i / (solutions.length - 1 || 1)) * (660 - 40),
                  y: 500 - 40 - (snapInt(s.objectiveValue) / Math.max(...solutions.map(s => snapInt(s.objectiveValue))) * (500 - 60))
                }));
                if (graphType === 'bar') {
                  return pts.map((p, i) => (
                    <rect key={i} x={p.x - 10} y={p.y} width={20} height={500 - 40 - p.y} fill="#007BFF" />
                  ));
                }
                if (graphType === 'point') {
                  return pts.map((p, i) => (
                    <circle key={i} cx={p.x} cy={p.y} r={4} fill="#007BFF" />
                  ));
                }
                return (
                  <polyline
                    fill="none"
                    stroke="#007BFF"
                    strokeWidth={2}
                    points={pts.map(p => `${p.x},${p.y}`).join(' ')}
                  />
                );
              })()}
            </svg>
          </div>
        )}

        {view === 'Calendar' && (
          <div style={{ padding: 20, textAlign: 'center', color: '#666' }}>
            <p>Calendar view not implemented yet.</p>
          </div>
        )}
      </div>
    </div>
  );
}