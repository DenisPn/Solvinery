import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import * as XLSX from 'xlsx';
import './SolutionResultsPage.css';

export default function SolutionResultsPage() {
  const { solutionResponse, setSelectedImage, setSelectedImageId } = useZPL();
  const solutionMap = solutionResponse?.solution || {};

  // Sorting state
  const [sortKey, setSortKey] = useState(null);       // numeric index or 'objective'
  const [sortAsc, setSortAsc] = useState(true);

  // Filters state
  const [filters, setFilters] = useState({});

  // View state
  const [selectedVar, setSelectedVar] = useState('');
  const [view, setView] = useState('Table');    // 'Table' | 'Pivot' | 'Graph' | 'Calendar'
  const [graphType, setGraphType] = useState('line');
  const [showConfig, setShowConfig] = useState(false);

  const navigate = useNavigate();

  const snapInt = (x, tol = 1e-5) => {
    const f = Math.floor(x), c = Math.ceil(x);
    if (Math.abs(x - f) < tol) return f;
    if (Math.abs(x - c) < tol) return c;
    return x;
  };

  // Log when we get a new response
  useEffect(() => {
    if (solutionResponse) console.log('SolutionResponse:', solutionResponse);
  }, [solutionResponse]);

  const variableNames = Object.keys(solutionMap);

  // Auto-select first variable
  useEffect(() => {
    if (variableNames.length > 0 && !selectedVar) {
      setSelectedVar(variableNames[0]);
    }
  }, [variableNames, selectedVar]);

  // Pull out data for the currently selected variable
  const varData = solutionMap[selectedVar] || {};
  const solutions = Array.from(varData.solutions || []);
  const columnTypes = varData.typeStructure || [];
  const showObjective = solutions.some(s => s.objectiveValue !== 1);
  const objectiveLabel = varData.objectiveValueAlias || 'Objective Value';

  // Initialize filters whenever columns change
  useEffect(() => {
    const init = {};
    columnTypes.forEach((_, i) => { init[i] = ''; });
    if (showObjective) init['objective'] = '';
    setFilters(init);
  }, [columnTypes, showObjective]);

  const handleFilterChange = (key, value) => {
    setFilters(f => ({ ...f, [key]: value }));
  };

  // Prepare sorted + snapped solutions
  const withSnapped = solutions.map(s => ({
    ...s,
    snappedObjective: snapInt(s.objectiveValue)
  }));
  let sortedSolutions = [...withSnapped];
  if (sortKey !== null) {
    sortedSolutions.sort((a, b) => {
      const va = sortKey === 'objective' ? a.snappedObjective : a.values[sortKey];
      const vb = sortKey === 'objective' ? b.snappedObjective : b.values[sortKey];
      const na = Number(va), nb = Number(vb);
      if (!isNaN(na) && !isNaN(nb)) {
        return sortAsc ? na - nb : nb - na;
      }
      const da = Date.parse(va), db = Date.parse(vb);
      if (!isNaN(da) && !isNaN(db)) {
        return sortAsc ? da - db : db - da;
      }
      return sortAsc
        ? String(va).localeCompare(String(vb))
        : String(vb).localeCompare(String(va));
    });
  }

  // Apply filters
  const filteredSolutions = sortedSolutions.filter(sol =>
    Object.entries(filters).every(([key, filter]) => {
      if (!filter) return true;
      const cell = key === 'objective'
        ? sol.snappedObjective
        : sol.values[Number(key)];
      return String(cell).toLowerCase().includes(filter.toLowerCase());
    })
  );

  // Export to Excel
  const handleExport = () => {
    const wb = XLSX.utils.book_new();
    variableNames.forEach(varName => {
      const d = solutionMap[varName];
      const cols = d.typeStructure || [];
      const sols = Array.from(d.solutions || []);
      const hasObj = sols.some(s => s.objectiveValue !== 1);
      const hdr = [...cols];
      if (hasObj) hdr.push(d.objectiveValueAlias || 'Objective Value');
      const aoa = [
        hdr,
        ...sols.map(s => [
          ...s.values,
          ...(hasObj ? [Math.floor(s.objectiveValue)] : [])
        ])
      ];
      const ws = XLSX.utils.aoa_to_sheet(aoa);
      XLSX.utils.book_append_sheet(wb, ws, varName.substring(0, 31));
    });
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([wbout], { type: 'application/octet-stream' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'solutions.xlsx';
    a.click();
    URL.revokeObjectURL(url);
  };

  // Pivot mapping
  const [mapping, setMapping] = useState({ rowIndex: 0, colIndex: 1, cellIndex: 2 });
  const order = ['rowIndex', 'colIndex', 'cellIndex'];
  const moveUp = key => {
    const i = order.indexOf(key);
    if (i > 0) {
      const p = order[i - 1];
      setMapping(m => ({ ...m, [key]: m[p], [p]: m[key] }));
    }
  };
  const moveDown = key => {
    const i = order.indexOf(key);
    if (i < order.length - 1) {
      const n = order[i + 1];
      setMapping(m => ({ ...m, [key]: m[n], [n]: m[key] }));
    }
  };

  // Build pivot data
  const rows = [];
  const cols = [];
  const cellMap = {};
  if (columnTypes.length === 3 && view === 'Pivot') {
    solutions.forEach(sol => {
      const r = sol.values[mapping.rowIndex];
      const c = sol.values[mapping.colIndex];
      const v = sol.values[mapping.cellIndex];
      if (!rows.includes(r)) rows.push(r);
      if (!cols.includes(c)) cols.push(c);
      cellMap[`${r}__${c}`] = v;
    });
    if (columnTypes[mapping.rowIndex] === 'INT') rows.sort((a, b) => a - b);
    if (columnTypes[mapping.colIndex] === 'INT') cols.sort((a, b) => a - b);
  }

  const publicUrl = process.env.PUBLIC_URL;

  return (
    <div className="solution-container">
      <div className="top-controls">
        <Link
          to="/main-page"
          onClick={e => {
            e.preventDefault();
            setSelectedImage(null);
            setSelectedImageId(null);
            navigate('/main-page');
          }}
          className="nav-btn home-btn"
          style={{ backgroundImage: `url(${publicUrl}/Images/HomeButton.png)` }}
          title="Home"
        />
        <Link
          to="/my-images"
          className="nav-btn images-btn"
          style={{ backgroundImage: `url(${publicUrl}/Images/ExitButton2.png)` }}
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
          onChange={e => setSelectedVar(e.target.value)}
        >
          {variableNames.map(n => (
            <option key={n} value={n}>{n}</option>
          ))}
        </select>
        <label htmlFor="view-select" className="var-label">View:</label>
        <select
          id="view-select"
          className="var-select"
          value={view}
          onChange={e => { setView(e.target.value); setShowConfig(false); }}
        >
          <option value="Table">Table</option>
          {columnTypes.length === 3 && <option value="Pivot">Pivot</option>}
          {columnTypes.length === 1 && <option value="Graph">Graph</option>}
          {columnTypes.includes('DATE') && <option value="Calendar">Calendar</option>}
        </select>
        {view === 'Pivot' && (
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

      {/* Pivot Configuration Modal */}
      {showConfig && view === 'Pivot' && (
        <div className="modal-overlay" onClick={() => setShowConfig(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>Configure Pivot</h3>
            <table className="pivot-config-table">
              <tbody>
                {order.map((key, i) => (
                  <tr key={key}>
                    <td>
                      {key === 'rowIndex'
                        ? 'Rows'
                        : key === 'colIndex'
                          ? 'Columns'
                          : 'Cells'
                      }
                    </td>
                    <td className="pivot-config-cell">
                      <button onClick={() => moveUp(key)} disabled={i === 0}>↑</button>
                      <span className="pivot-type">{columnTypes[mapping[key]]}</span>
                      <button onClick={() => moveDown(key)} disabled={i === order.length - 1}>↓</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <button onClick={() => setShowConfig(false)}>Close</button>
          </div>
        </div>
      )}

      <div className="table-wrapper">
        {/* Table View */}
        {view === 'Table' && (
          <table className="solutions-table">
            <thead>
              <tr>
                {columnTypes.map((t, i) => (
                  <th key={i}>
                    {t}{' '}
                    <button
                      className="sort-btn"
                      onClick={() => {
                        if (sortKey === i) setSortAsc(a => !a);
                        else { setSortKey(i); setSortAsc(true); }
                      }}
                    >⇅</button>
                  </th>
                ))}
                {showObjective && (
                  <th>
                    {objectiveLabel}{' '}
                    <button
                      className="sort-btn"
                      onClick={() => {
                        if (sortKey === 'objective') setSortAsc(a => !a);
                        else { setSortKey('objective'); setSortAsc(true); }
                      }}
                    >⇅</button>
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
                  {sol.values.map((v, idx) => <td key={idx}>{v}</td>)}
                  {showObjective && <td>{sol.snappedObjective}</td>}
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {/* Pivot View */}
        {view === 'Pivot' && (
          <table className="pivot-table">
            <thead>
              <tr>
                <th>{columnTypes[mapping.rowIndex]}</th>
                {cols.map(c => <th key={c}>{c}</th>)}
              </tr>
            </thead>
            <tbody>
              {rows.map(r => (
                <tr key={r}>
                  <td><strong>{r}</strong></td>
                  {cols.map(c => <td key={c}>{cellMap[`${r}__${c}`] || ''}</td>)}
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {/* Graph View */}
        {view === 'Graph' && columnTypes.length === 1 && (() => {
          const xs = solutions.map(s => s.values[0]);
          const ys = solutions.map(s => snapInt(s.objectiveValue));
          const margin = { top: 20, right: 20, bottom: 40, left: 40 };
          const width = 600, height = 300;
          const maxY = Math.max(...ys, 0);

          const xScale = (d, i) =>
            margin.left + (i / (xs.length - 1 || 1)) * (width - margin.left - margin.right);
          const yScale = y =>
            height - margin.bottom - (y / (maxY || 1)) * (height - margin.top - margin.bottom);

          const pts = xs.map((x, i) => ({ x: xScale(x, i), y: yScale(ys[i]) }));

          return (
            <div className="graph-wrapper">
              <svg width={width} height={height}>
                {Array.from({ length: maxY + 1 }, (_, y) => y).map(y => (
                  <g key={y} transform={`translate(0,${yScale(y)})`}>
                    <line x1={margin.left} x2={width - margin.right} stroke="#eee" />
                    <text
                      x={margin.left - 8}
                      y={0}
                      dy="0.32em"
                      textAnchor="end"
                      fontSize="12"
                      fill="#333"
                    >
                      {y}
                    </text>
                  </g>
                ))}
                {xs.map((x, i) => (
                  <text
                    key={i}
                    x={xScale(x, i)}
                    y={height - margin.bottom + 18}
                    textAnchor="middle"
                    fontSize="12"
                    fill="#333"
                  >
                    {x}
                  </text>
                ))}
                {graphType === 'bar' && pts.map((p, i) => (
                  <rect
                    key={i}
                    x={p.x - 10}
                    y={p.y}
                    width={20}
                    height={height - margin.bottom - p.y}
                    fill="#007BFF"
                  />
                ))}
                {graphType === 'point' && pts.map((p, i) => (
                  <circle key={i} cx={p.x} cy={p.y} r={4} fill="#007BFF" />
                ))}
                {graphType === 'line' && (
                  <polyline
                    fill="none"
                    stroke="#007BFF"
                    strokeWidth={2}
                    points={pts.map(p => `${p.x},${p.y}`).join(' ')}
                  />
                )}
              </svg>
            </div>
          );
        })()}

        {/* Calendar View */}
        {view === 'Calendar' && (
          <div style={{ padding: 20, textAlign: 'center', color: '#666' }}>
            <p>Calendar view not implemented yet.</p>
          </div>
        )}
      </div>
    </div>
  );
}
