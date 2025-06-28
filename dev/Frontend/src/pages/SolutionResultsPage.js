import React, { useState, useEffect, useLayoutEffect, useMemo } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useZPL } from '../context/ZPLContext';
import * as XLSX from 'xlsx';
import svgPanZoom from 'svg-pan-zoom';
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  useSortable,
  horizontalListSortingStrategy,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import './SolutionResultsPage.css';

// Generic Draggable Header Component
function DraggableHeader({ id, sortId, label, onSort, sortKey, sortAsc, isPivotHeader = false }) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id, data: { type: isPivotHeader ? 'pivot-column' : 'column' } });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.8 : 1,
    cursor: 'move',
    zIndex: isDragging ? 10 : 1,
    position: 'relative',
  };

  return (
    <th ref={setNodeRef} style={style} {...attributes} className="has-tooltip" data-tooltip={label}>
      <div className="cell-content">
        <span {...listeners} style={{ flexGrow: 1, display: 'inline-block' }}>{label}</span>
        <button className="sort-btn" onClick={() => onSort(sortId)}>
          {sortKey === sortId ? (sortAsc ? '▲' : '▼') : '⇅'}
        </button>
      </div>
    </th>
  );
}

// Draggable Row Component
function DraggableRow({ row, columns }) {
    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
        isDragging,
    } = useSortable({ id: row.id, data: { type: 'row' } });

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
        opacity: isDragging ? 0.8 : 1,
        zIndex: isDragging ? 10 : 0,
        position: 'relative',
        backgroundColor: isDragging ? '#f0f8ff' : 'transparent',
    };
    
    return (
        <tr ref={setNodeRef} style={style} {...attributes}>
            <td className="drag-handle" {...listeners}>
                <span style={{cursor: 'grab'}}>⠿</span>
            </td>
            {columns.map(col => {
                const cellValue = col.id === 'objective' ? row.snappedObjective : row.values[col.originalIndex];
                return (
                    <td key={col.id} className="has-tooltip" data-tooltip={cellValue}>
                        <div className="cell-content">{cellValue}</div>
                    </td>
                );
            })}
        </tr>
    );
}

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
  const [pivotColumnSortMode, setPivotColumnSortMode] = useState('asc');
  
  const navigate = useNavigate();

  const [columns, setColumns] = useState([]);
  const [displayedRows, setDisplayedRows] = useState([]);
  const [pivotRows, setPivotRows] = useState([]);
  const [pivotCols, setPivotCols] = useState([]);
  const [pivotCellMap, setPivotCellMap] = useState({});
  const [pivotRows2D, setPivotRows2D] = useState([]);
  const [pivotCols2D, setPivotCols2D] = useState([]);
  const [pivotCellMap2D, setPivotCellMap2D] = useState({});

  const sensors = useSensors(useSensor(PointerSensor));

  const handleDragEnd = (event) => {
    const { active, over } = event;
    if (!over || active.id === over.id) return;
    const activeType = active.data.current?.type;
    
    if (activeType === 'column') {
      setColumns((items) => arrayMove(items, items.findIndex(i => i.id === active.id), items.findIndex(i => i.id === over.id)));
    } else if (activeType === 'row') {
      setDisplayedRows((items) => arrayMove(items, items.findIndex(i => i.id === active.id), items.findIndex(i => i.id === over.id)));
    } else if (activeType === 'pivot-column') {
      const colSetter = columnTypes.length === 3 ? setPivotCols : setPivotCols2D;
      colSetter((items) => arrayMove(items, items.findIndex(i => i === active.id), items.findIndex(i => i === over.id)));
      setPivotColumnSortMode('custom');
    }
  };

  const handleColumnSort = (key) => {
    if (sortKey === key) setSortAsc(a => !a);
    else { setSortKey(key); setSortAsc(true); }
  };

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
      const instance = svgPanZoom('#myGraph', { zoomEnabled: true, controlIconsEnabled: true, fit: true, center: true, minZoom: 0.5, maxZoom: 20, zoomScaleSensitivity: 0.2 });
      return () => instance.destroy();
    }
  }, [view, selectedVar, graphType]);

  const varData = solutionMap[selectedVar] || {};
  
  const solutions = useMemo(() => Array.from(varData.solutions || []), [varData.solutions]);
  const columnTypes = useMemo(() => varData.typeStructure || [], [varData.typeStructure]);
  
  const showObjective = useMemo(() => solutions.some(s => s.objectiveValue !== 1), [solutions]);
  const objectiveLabel = varData.objectiveValueAlias || 'Objective Value';

  const withSnapped = useMemo(() => solutions.map((s, index) => ({ 
    ...s, 
    id: `row-${index}-${s.values.join('-')}`,
    snappedObjective: snapInt(s.objectiveValue) 
  })), [solutions]);

  const sortedSolutions = useMemo(() => {
    let tempSolutions = [...withSnapped];
    if (sortKey !== null) {
      tempSolutions.sort((a, b) => {
        const va = sortKey === 'objective' ? a.snappedObjective : a.values[sortKey];
        const vb = sortKey === 'objective' ? b.snappedObjective : b.values[sortKey];
        const na = Number(va), nb = Number(vb);
        if (!isNaN(na) && !isNaN(nb)) return sortAsc ? na - nb : nb - na;
        const da = Date.parse(va), db = Date.parse(vb);
        if (!isNaN(da) && !isNaN(db)) return sortAsc ? da - db : db - da;
        return sortAsc ? String(va).localeCompare(String(vb)) : String(vb).localeCompare(String(va));
      });
    }
    return tempSolutions;
  }, [withSnapped, sortKey, sortAsc]);
  
  const filteredSolutions = useMemo(() => sortedSolutions.filter(sol =>
    Object.entries(filters).every(([key, filter]) => {
      if (!filter) return true;
      const cell = key === 'objective' ? sol.snappedObjective : sol.values[Number(key)];
      return String(cell).toLowerCase().includes(filter.toLowerCase());
    })
  ), [sortedSolutions, filters]);

  useEffect(() => {
    setDisplayedRows(filteredSolutions);
  }, [filteredSolutions]);

  useEffect(() => {
    const initialColumns = columnTypes.map((type, index) => ({ id: `col-${index}`, label: type, originalIndex: index }));
    if (showObjective) initialColumns.push({ id: 'objective', label: objectiveLabel, originalIndex: -1 });
    setColumns(initialColumns);
  }, [selectedVar, showObjective, objectiveLabel, columnTypes]);

  useEffect(() => {
    const init = {};
    columnTypes.forEach((_, i) => (init[i.toString()] = ''));
    if (showObjective) init['objective'] = '';
    setFilters(init);
  }, [columnTypes, showObjective]);

  const handleFilterChange = (key, value) => setFilters(f => ({ ...f, [key]: value }));

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

  const handlePivotSort = (colKey) => {
    setPivotSort(prev => {
      if (prev.key !== colKey) return { key: colKey, asc: true };
      if (prev.asc) return { ...prev, asc: false };
      return { key: null, asc: true };
    });
  };
  
  const handlePivotColumnSortToggle = () => {
    setPivotColumnSortMode(currentMode => {
        if(currentMode === 'custom') return 'asc';
        return currentMode === 'asc' ? 'desc' : 'asc';
    });
  };
  
  useEffect(() => {
    if (view !== 'Pivot') return;
  
    const getSortedCols = (cols) => {
        const sorted = [...cols];
        const areColsNumeric = sorted.every(c => !isNaN(Number(c)));
        if (areColsNumeric) sorted.sort((a,b) => Number(a) - Number(b));
        else sorted.sort((a,b) => String(a).localeCompare(String(b)));
        return sorted;
    };
  
    if (columnTypes.length === 3) {
      const newRows = [], tempCols = [], newCellMap = {};
      solutions.forEach(sol => {
        const r = sol.values[mapping.rowIndex], c = sol.values[mapping.colIndex], v = sol.values[mapping.cellIndex];
        if (!newRows.includes(r)) newRows.push(r);
        if (c !== null && c !== undefined && !tempCols.includes(c)) tempCols.push(c);
        const key = `${r}__${c}`;
        if (newCellMap[key]) { newCellMap[key] += `, ${v}`; } else { newCellMap[key] = v; }
      });
      setPivotRows(newRows);
      setPivotCellMap(newCellMap);
      
      const currentSorted = [...pivotCols].sort().join(',');
      const newSorted = [...tempCols].sort().join(',');
      if (currentSorted !== newSorted) {
        setPivotCols(getSortedCols(tempCols));
        setPivotColumnSortMode('asc');
      }
    } else if (columnTypes.length === 2) {
      const tempRows = [], tempCols = [], tempMap = {};
      const rowIndex = isTransposed ? 1 : 0;
      const colIndex = isTransposed ? 0 : 1;
      solutions.forEach(sol => {
        const r = sol.values[rowIndex], c = sol.values[colIndex], v = snapInt(sol.objectiveValue);
        if (!tempRows.includes(r)) tempRows.push(r);
        if (c !== null && c !== undefined && !tempCols.includes(c)) tempCols.push(c);
        tempMap[`${r}__${c}`] = v;
      });
      setPivotRows2D(tempRows);
      setPivotCellMap2D(tempMap);
      
      const currentSorted = [...pivotCols2D].sort().join(',');
      const newSorted = [...tempCols].sort().join(',');
      if(currentSorted !== newSorted) {
          setPivotCols2D(getSortedCols(tempCols));
          setPivotColumnSortMode('asc');
      }
    }
  }, [view, solutions, columnTypes, mapping, isTransposed, pivotCols, pivotCols2D]);

  useEffect(() => {
    if (pivotColumnSortMode === 'custom' || view !== 'Pivot') return;

    const [colsToProcess, colSetter] = columnTypes.length === 3 ? [pivotCols, setPivotCols] : [pivotCols2D, setPivotCols2D];
    if (colsToProcess.length === 0) return;

    colSetter(currentCols => {
        const sorted = [...currentCols];
        const areColsNumeric = sorted.every(c => !isNaN(Number(c)));
        sorted.sort((a,b) => {
            const valA = areColsNumeric ? Number(a) : String(a);
            const valB = areColsNumeric ? Number(b) : String(b);
            if (valA < valB) return -1;
            if (valA > valB) return 1;
            return 0;
        });
        if(pivotColumnSortMode === 'desc') sorted.reverse();
        return sorted;
    });

  }, [pivotColumnSortMode, columnTypes.length]);

  let sortedPivotRows = [...pivotRows];
  if (view === 'Pivot' && columnTypes.length === 3 && pivotSort.key !== null) {
    sortedPivotRows.sort((a, b) => {
      const valA = pivotCellMap[`${a}__${pivotSort.key}`] || '';
      const valB = pivotCellMap[`${b}__${pivotSort.key}`] || '';
      const numA = Number(valA), numB = Number(valB);
      let comparison = 0;
      if (!isNaN(numA) && !isNaN(numB)) comparison = numA - numB;
      else comparison = String(valA).localeCompare(String(valB));
      return pivotSort.asc ? comparison : -comparison;
    });
  }
  
  let sortedPivotRows2D = [...pivotRows2D];
  if (view === 'Pivot' && columnTypes.length === 2 && pivotSort.key !== null) {
    sortedPivotRows2D.sort((a, b) => {
      const valA = pivotCellMap2D[`${a}__${pivotSort.key}`] || -Infinity;
      const valB = pivotCellMap2D[`${b}__${pivotSort.key}`] || -Infinity;
      const comparison = valA - valB;
      return pivotSort.asc ? comparison : -comparison;
    });
  }

  const handleMappingChange = (roleToUpdate, selectedIndex) => {
    const newIndex = Number(selectedIndex);
    const currentMapping = { ...mapping };
    const roleToSwap = Object.keys(currentMapping).find((key) => currentMapping[key] === newIndex);
    const oldIndexOfRole = currentMapping[roleToUpdate];
    if (roleToSwap) currentMapping[roleToSwap] = oldIndexOfRole;
    currentMapping[roleToUpdate] = newIndex;
    setMapping(currentMapping);
    setPivotSort({ key: null, asc: true });
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
                  <td><select className="var-select" value={mapping.rowIndex} onChange={(e) => handleMappingChange('rowIndex', e.target.value)}>{columnTypes.map((type, i) => (<option key={`row-${i}`} value={i}>{type} (Column {i+1})</option>))}</select></td>
                </tr>
                <tr>
                  <td><strong>Columns</strong></td>
                  <td><select className="var-select" value={mapping.colIndex} onChange={(e) => handleMappingChange('colIndex', e.target.value)}>{columnTypes.map((type, i) => (<option key={`col-${i}`} value={i}>{type} (Column {i+1})</option>))}</select></td>
                </tr>
                <tr>
                  <td><strong>Cell Value</strong></td>
                  <td><select className="var-select" value={mapping.cellIndex} onChange={(e) => handleMappingChange('cellIndex', e.target.value)}>{columnTypes.map((type, i) => (<option key={`cell-${i}`} value={i}>{type} (Column {i+1})</option>))}</select></td>
                </tr>
              </tbody>
            </table>
            <button className="config-btn" onClick={() => setShowConfig(false)}>Done</button>
          </div>
        </div>
      )}

      <div className="top-controls">
        <Link to="/main-page" onClick={e => { e.preventDefault(); setSelectedImage(null); setSelectedImageId(null); navigate('/main-page'); }} className="nav-btn home-btn" style={{ backgroundImage: `url(${publicUrl}/images/HomeButton.png)` }} title="Home" />
        <Link to="/my-images" className="nav-btn images-btn" style={{ backgroundImage: `url(${publicUrl}/images/ExitButton2.png)` }} title="My Images" />
        <img src={`${publicUrl}/images/excel.png`} alt="Export to Excel" className="export-btn" onClick={handleExport} title="Export to Excel" />
        <label htmlFor="var-select" className="var-label">Variable:</label>
        <select id="var-select" className="var-select" value={selectedVar}
          onChange={e => {
            setSelectedVar(e.target.value);
            setView('Table');
            setIsTransposed(false);
            setPivotSort({ key: null, asc: true }); 
          }}
        >
          {variableNames.map(n => <option key={n} value={n}>{n}</option>)}
        </select>
        <label htmlFor="view-select" className="var-label">View:</label>
        <select id="view-select" className="var-select" value={view}
          onChange={e => {
            setView(e.target.value);
            setShowConfig(false);
            setIsTransposed(false);
            setPivotSort({ key: null, asc: true }); 
            setPivotColumnSortMode('asc');
          }}
        >
          <option value="Table">Table</option>
          {(columnTypes.length === 2 || columnTypes.length === 3) && <option value="Pivot">Pivot</option>}
          {columnTypes.length === 1 && <option value="Graph">Graph</option>}
          {columnTypes.includes('DATE') && <option value="Calendar">Calendar</option>}
        </select>
        {view === 'Pivot' && columnTypes.length === 3 && (
          <button className="config-btn" onClick={() => setShowConfig(true)}>Configure Pivot</button>
        )}
        {view === 'Pivot' && columnTypes.length === 2 && (
          <button onClick={() => setIsTransposed(t => !t)} className="config-btn">Switch Rows/Columns</button>
        )}
        {view === 'Pivot' && (columnTypes.length === 2 || columnTypes.length === 3) && (
            <button className="config-btn" onClick={handlePivotColumnSortToggle}>
                Sort Columns {pivotColumnSortMode === 'asc' ? '▲' : pivotColumnSortMode === 'desc' ? '▼' : '⇅'}
            </button>
        )}
        {view === 'Graph' && columnTypes.length === 1 && (
          <>
            <label htmlFor="chart-select" className="var-label">Chart:</label>
            <select id="chart-select" className="var-select" value={graphType} onChange={e => setGraphType(e.target.value)}>
              <option value="bar">Columns</option>
              <option value="point">Points</option>
              <option value="line">Line</option>
            </select>
          </>
        )}
      </div>

      <div className={`table-wrapper ${view.toLowerCase()}-view`}>
        {view === 'Table' && (
          <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
            <table className="solutions-table">
              <thead>
                <tr>
                  <th className="drag-handle-header"></th>
                  <SortableContext items={columns.map(c => c.id)} strategy={horizontalListSortingStrategy}>
                    {columns.map(col => (
                      <DraggableHeader
                        key={col.id} id={col.id} sortId={col.id === 'objective' ? 'objective' : col.originalIndex}
                        label={col.label} onSort={handleColumnSort} sortKey={sortKey} sortAsc={sortAsc}
                      />
                    ))}
                  </SortableContext>
                </tr>
                <tr>
                    <th className="drag-handle-header"></th>
                    {columns.map(col => {
                        const filterKey = col.id === 'objective' ? 'objective' : col.originalIndex.toString();
                        return (
                        <th key={`filter-${col.id}`}>
                            <input type="text" className="filter-input" placeholder="Search..."
                            value={filters[filterKey] || ''}
                            onChange={e => handleFilterChange(filterKey, e.target.value)}
                            />
                        </th>
                        );
                    })}
                </tr>
              </thead>
              <SortableContext items={displayedRows.map(r => r.id)} strategy={verticalListSortingStrategy}>
                <tbody>
                    {displayedRows.map((row) => (<DraggableRow key={row.id} row={row} columns={columns} />))}
                </tbody>
              </SortableContext>
            </table>
          </DndContext>
        )}

        {view === 'Pivot' && columnTypes.length === 3 && (
            <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
                <table className="pivot-table">
                    <thead>
                        <tr>
                            <th className="has-tooltip" data-tooltip={columnTypes[mapping.rowIndex]}>
                                <div className="cell-content">{columnTypes[mapping.rowIndex]}</div>
                            </th>
                            <SortableContext items={pivotCols} strategy={horizontalListSortingStrategy}>
                                {pivotCols.map(c => (
                                    <DraggableHeader key={c} id={c} sortId={c} label={c} onSort={handlePivotSort} sortKey={pivotSort.key} sortAsc={pivotSort.asc} isPivotHeader={true} />
                                ))}
                            </SortableContext>
                        </tr>
                    </thead>
                    <tbody>
                        {sortedPivotRows.map(r => (
                        <tr key={r}>
                            <td className="has-tooltip" data-tooltip={r}><div className="cell-content"><strong>{r}</strong></div></td>
                            {pivotCols.map(c => {
                                const cellValue = pivotCellMap[`${r}__${c}`] || '';
                                return (<td key={c} className="has-tooltip" data-tooltip={cellValue}><div className="cell-content">{cellValue}</div></td>)
                            })}
                        </tr>
                        ))}
                    </tbody>
                </table>
            </DndContext>
        )}
        
        {view === 'Pivot' && columnTypes.length === 2 && (
          <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
            <div className="pivot-container">
                <table className="pivot-table">
                <thead>
                    <tr>
                    <th className="has-tooltip" data-tooltip={`${isTransposed ? columnTypes[1] : columnTypes[0]} / ${isTransposed ? columnTypes[0] : columnTypes[1]}`}>
                        <div className="cell-content">{isTransposed ? columnTypes[1] : columnTypes[0]} / {isTransposed ? columnTypes[0] : columnTypes[1]}</div>
                    </th>
                    <SortableContext items={pivotCols2D} strategy={horizontalListSortingStrategy}>
                        {pivotCols2D.map(c => (
                            <DraggableHeader key={c} id={c} sortId={c} label={c} onSort={handlePivotSort} sortKey={pivotSort.key} sortAsc={pivotSort.asc} isPivotHeader={true} />
                        ))}
                    </SortableContext>
                    </tr>
                </thead>
                <tbody>
                    {sortedPivotRows2D.map(r => (
                        <tr key={r}>
                            <td className="has-tooltip" data-tooltip={r}><div className="cell-content"><strong>{r}</strong></div></td>
                            {pivotCols2D.map(c => {
                                const cellValue = pivotCellMap2D[`${r}__${c}`] ?? '';
                                return (<td key={c} className="has-tooltip" data-tooltip={cellValue}><div className="cell-content">{cellValue}</div></td>)
                            })}
                        </tr>
                    ))}
                </tbody>
                </table>
            </div>
          </DndContext>
        )}
        
        {view === 'Graph' && columnTypes.length === 1 && (
          <div className="graph-wrapper" key={`${selectedVar}-${graphType}`}>
            <svg id="myGraph" width="100%" height="100%" viewBox="0 0 700 520" preserveAspectRatio="xMidYMid meet" style={{ display: 'block' }}>
              {displayedRows.length > 0 && (
                <>
                  <text transform="rotate(-90)" x={-250} y={15} textAnchor="middle" fontSize="14" fill="#333" fontWeight="bold">{objectiveLabel}</text>
                  <text x={350} y={510} textAnchor="middle" fontSize="14" fill="#333" fontWeight="bold">{columnTypes[0]}</text>
                  {Array.from({ length: 6 }, (_, i) => {
                    const maxY = Math.max(1, ...displayedRows.map(s => snapInt(s.objectiveValue)));
                    const yVal = Math.round((i / 5) * maxY) || 0;
                    const yPos = 500 - 40 - (yVal / maxY * (500 - 60));
                    return (<g key={i} transform={`translate(0,${yPos})`}><line x1={50} x2={660} stroke="#eee" /><text x={42} dy="0.32em" textAnchor="end" fontSize="12" fill="#333">{yVal}</text></g>);
                  })}
                  {displayedRows.map((s, i) => {
                    const x = 50 + (i / (displayedRows.length - 1 || 1)) * (660 - 50);
                    return (<text key={i} x={x} y={480} textAnchor="middle" fontSize="12" fill="#333">{s.values[0]}</text>);
                  })}
                  {(() => {
                    const maxY = Math.max(1, ...displayedRows.map(s => snapInt(s.objectiveValue)));
                    const pts = displayedRows.map((s, i) => ({
                      x: 50 + (i / (displayedRows.length - 1 || 1)) * (660 - 50),
                      y: 500 - 40 - (snapInt(s.objectiveValue) / maxY * (500 - 60))
                    }));
                    if (graphType === 'bar') return pts.map((p, i) => <rect key={i} x={p.x - 10} y={p.y} width={20} height={500 - 40 - p.y} fill="#007BFF" />);
                    if (graphType === 'point') return pts.map((p, i) => <circle key={i} cx={p.x} cy={p.y} r={4} fill="#007BFF" />);
                    return <polyline fill="none" stroke="#007BFF" strokeWidth={2} points={pts.map(p => `${p.x},${p.y}`).join(' ')} />;
                  })()}
                </>
              )}
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