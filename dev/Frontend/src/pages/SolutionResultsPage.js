import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import './SolutionResultsPage.css';

export default function SolutionResultsPage() {
  const { solutionResponse } = useZPL();
  const solutionMap = solutionResponse?.solution || {};

  const variableNames = Object.keys(solutionMap);
  const [selectedVar, setSelectedVar] = useState('');
  const [viewMode, setViewMode] = useState(0); // 0 = table, 1 = pivot
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
  if (columnTypes.length === 3 && viewMode === 1) {
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
          {variableNames.map(n => (
            <option key={n} value={n}>{n}</option>
          ))}
        </select>

        {columnTypes.length === 3 && (
          <button
            className="view-toggle-btn"
            onClick={() => setViewMode(v => (v === 0 ? 1 : 0))}
          >
            {viewMode === 0 ? 'Pivot View' : 'Table View'}
          </button>
        )}

        {viewMode === 1 && columnTypes.length === 3 && (
          <button
            className="config-btn"
            onClick={() => setShowConfig(true)}
          >
            Configure Pivot
          </button>
        )}
      </div>

      {showConfig && (
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
        {viewMode === 0 ? (
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
        ) : (
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
      </div>
    </div>
  );
}
