import { useState, useEffect } from 'react';
import { useZPL } from '../context/ZPLContext';
import './SolutionResultsPage.css';

export default function SolutionResultsPage() {
  // Retrieve the solutionResponse from ZPLContext using the useZPL hook
  const { solutionResponse } = useZPL();
  const solutionMap = solutionResponse?.solution || {};

  // Log the full solution response when it loads
  useEffect(() => {
    if (solutionResponse) {
      console.log('SolutionResponse:', solutionResponse);
    }
  }, [solutionResponse]);

  // List of variable names
  const variableNames = Object.keys(solutionMap);

  // State for the selected variable
  const [selectedVar, setSelectedVar] = useState('');

  // Initialize selection to the first variable if available
  useEffect(() => {
    if (variableNames.length > 0 && !selectedVar) {
      setSelectedVar(variableNames[0]);
    }
  }, [variableNames, selectedVar]);

  // Data for the selected variable
  const varData = solutionMap[selectedVar] || { solutions: [] };

  // Convert the Set to an array
  const solutionsArray = Array.from(varData.solutions || []);

  // Determine if we need to show the Objective Value column
  const showObjective = solutionsArray.some(sol => sol.objectiveValue !== 1);

  return (
    <div className="solution-container">
      {/* Dropdown for selecting variable */}
      <label htmlFor="var-select" className="var-label">
        Select Variable:
      </label>
      <select
        id="var-select"
        value={selectedVar}
        onChange={e => setSelectedVar(e.target.value)}
        className="var-select"
      >
        {variableNames.map(name => (
          <option key={name} value={name}>
            {name}
          </option>
        ))}
      </select>

      {/* Display message if no data */}
      {solutionsArray.length === 0 ? (
        <p className="no-data">No data found for the selected variable.</p>
      ) : (
        <div className="variable-details">
          <h2 className="var-title">{selectedVar}</h2>

          <div className="var-info">
            <span><strong>Sets:</strong> {varData.setStructure.join(', ')}</span>
            <span><strong>Types:</strong> {varData.typeStructure.join(', ')}</span>
          </div>

          {/* Table for displaying solutions */}
          <table className="solutions-table">
            <thead>
              <tr>
                <th>Values</th>
                {showObjective && <th>Objective Value</th>}
              </tr>
            </thead>
            <tbody>
              {solutionsArray.map(sol => (
                <tr key={sol.values.join('-')}>
                  <td>{sol.values.join(', ')}</td>
                  {showObjective && <td>{sol.objectiveValue}</td>}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
