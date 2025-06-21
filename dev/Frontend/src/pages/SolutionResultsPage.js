import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import './SolutionResultsPage.css';

export default function SolutionResultsPage() {
  const { solutionResponse } = useZPL();
  const solutionMap = solutionResponse?.solution || {};

  // Log the full solutionResponse as soon as it arrives
  useEffect(() => {
    if (solutionResponse) {
      console.log('SolutionResponse:', solutionResponse);
    }
  }, [solutionResponse]);

  const variableNames = Object.keys(solutionMap);
  const [selectedVar, setSelectedVar] = useState('');
  const [view, setView] = useState('Table'); // 'Table' | 'Pivot' | 'Graph' | 'Calendar'
  const [graphType, setGraphType] = useState('line'); // 'bar' | 'point' | 'line'
  const [showConfig, setShowConfig] = useState(false);

  // Auto‐select first variable
  useEffect(() => {
    if (variableNames.length > 0 && !selectedVar) {
      setSelectedVar(variableNames[0]);
    }
  }, [variableNames, selectedVar]);

  const varData = solutionMap[selectedVar] || { solutions: [], typeStructure: [] };
  const solutionsArray = Array.from(varData.solutions || []);
  const columnTypes = varData.typeStructure || [];
  const showObjective = solutionsArray.some(sol => sol.objectiveValue !== 1);

  // Pivot mapping
  const [mapping, setMapping] = useState({ rowIndex: 0, colIndex: 1, cellIndex: 2 });
  const order = ['rowIndex', 'colIndex', 'cellIndex'];

  const moveUp = key => {
    const idx = order.indexOf(key);
    if (idx > 0) {
      const prev = order[idx - 1];
      setMapping(m => ({ ...m, [key]: m[prev], [prev]: m[key] }));
    }
  };

  const moveDown = key => {
    const idx = order.indexOf(key);
    if (idx < order.length - 1) {
      const next = order[idx + 1];
      setMapping(m => ({ ...m, [key]: m[next], [next]: m[key] }));
    }
  };

  // Build pivot data
  const rows = [];
  const cols = [];
  const cellMap = {};
  if (columnTypes.length === 3 && view === 'Pivot') {
    solutionsArray.forEach(sol => {
      const r = sol.values[mapping.rowIndex];
      const c = sol.values[mapping.colIndex];
      const v = sol.values[mapping.cellIndex];
      if (!rows.includes(r)) rows.push(r);
      if (!cols.includes(c)) cols.push(c);
      cellMap[`${r}__${c}`] = v;
    });
    if (columnTypes[mapping.rowIndex] === 'INT') rows.sort((a, b) => Number(a) - Number(b));
    if (columnTypes[mapping.colIndex] === 'INT') cols.sort((a, b) => Number(a) - Number(b));
  }

  const publicUrl = process.env.PUBLIC_URL;

  return (
    <div className="solution-container">
      <div className="top-controls">
        <Link
          to="/"
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

        <label htmlFor="var-select" className="var-label">Variable:</label>
        <select
          id="var-select"
          className="var-select"
          value={selectedVar}
          onChange={e => setSelectedVar(e.target.value)}
        >
          {variableNames.map(n => <option key={n} value={n}>{n}</option>)}
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
          <button
            className="config-btn"
            onClick={() => setShowConfig(true)}
          >
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

      {showConfig && view === 'Pivot' && (
        <div className="modal-overlay" onClick={() => setShowConfig(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>Configure Pivot</h3>
            <table className="pivot-config-table">
              <tbody>
                {order.map((key, i) => (
                  <tr key={key}>
                    <td>
                      {key === 'rowIndex' ? 'Rows' : key === 'colIndex' ? 'Columns' : 'Cells'}
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
                {columnTypes.map((t, i) => <th key={i}>{t}</th>)}
                {showObjective && <th>Objective Value</th>}
              </tr>
            </thead>
            <tbody>
              {solutionsArray.map(sol => (
                <tr key={sol.values.join('-')}>
                  {sol.values.map((v, idx) => <td key={idx}>{v}</td>)}
                  {showObjective && <td>{sol.objectiveValue}</td>}
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
          const xs = solutionsArray.map(sol => sol.values[0]);
          const ys = solutionsArray.map(sol => sol.objectiveValue);
          const margin = { top: 20, right: 20, bottom: 40, left: 40 };
          const width = 600;
          const height = 300;
          const maxY = Math.max(...ys, 0);

          const xScale = (d, i) =>
            margin.left + (i / (xs.length - 1 || 1)) * (width - margin.left - margin.right);
          const yScale = y =>
            height - margin.bottom - (y / (maxY || 1)) * (height - margin.top - margin.bottom);

          const pts = xs.map((x, i) => ({ x: xScale(x, i), y: yScale(ys[i]) }));

          return (
            <svg width={width} height={height}>
              {/* Y‐axis integer ticks */}
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

              {/* X‐axis labels */}
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

              {/* Chart rendering */}
              {graphType === 'bar' &&
                pts.map((p, i) => (
                  <rect
                    key={i}
                    x={p.x - 10}
                    y={p.y}
                    width={20}
                    height={height - margin.bottom - p.y}
                    fill="#007BFF"
                  />
                ))}

              {graphType === 'point' &&
                pts.map((p, i) => (
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
          );
        })()}

        {/* Calendar View */}
        {view === 'Calendar' && (
          <div style={{ padding: 20, textAlign: 'center', color: '#666' }}>
            {/* placeholder */}
            <p>Calendar view not implemented yet.</p>
          </div>
        )}
      </div>
    </div>
  );
}
