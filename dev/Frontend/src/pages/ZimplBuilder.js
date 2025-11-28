import React, { useState } from 'react';
import './ZimplBuilder.css';

export default function ZimplBuilder() {
  
  // --- STATE ---

  // 1. Sets (Now supports Schema/Tuples)
  // schema: [{ name, type }]
  // items: [ { col1: val, col2: val } ] OR simple strings
  const [tables, setTables] = useState([
    { 
      id: 't1', 
      name: 'Workers', 
      schema: [{ name: 'Name', type: 'string' }], 
      items: [{ Name: 'Alice' }, { Name: 'Bob' }] 
    },
    { 
      id: 't2', 
      name: 'Shifts', 
      schema: [{ name: 'Name', type: 'string' }], 
      items: [{ Name: 'Morning' }, { Name: 'Evening' }] 
    }
  ]);

  // 2. Global Constants
  const [globals, setGlobals] = useState([
    { id: 'g1', name: 'MaxShiftsPerWorker', value: 5 },
    { id: 'g2', name: 'MinWorkersPerShift', value: 2 }
  ]);

  // 3. Matrix Variables
  const [variables, setVariables] = useState([
    { id: 'v1', name: 'Assign', dim1: 't1', dim2: 't2', type: 'binary' } 
  ]);

  // 4. Constraints
  const [constraints, setConstraints] = useState([
    { 
      id: 'c1', 
      targetVar: 'v1', 
      forEachDim: 't1', 
      operator: '<=', 
      limitType: 'global', 
      limitValue: 0, 
      limitRef: 'g1' 
    }
  ]);

  // 5. Objective
  const [objective, setObjective] = useState({
    direction: 'minimize',
    terms: [{ id: 'o1', weight: 1, variableId: 'v1' }],
    offsetType: 'none',
    offsetId: ''
  });

  // --- UI STATES ---
  const [showJson, setShowJson] = useState(false);
  const [editingSetId, setEditingSetId] = useState(null); 
  const [tempSetData, setTempSetData] = useState([]); // Holds data during edit

  // --- HELPER FUNCTIONS ---
  const getTable = (id) => tables.find(t => t.id === id);
  const getVar = (id) => variables.find(v => v.id === id);

  // --- JSON GENERATOR ---
  const generateJSON = () => {
    return {
      projectId: "generic_scheduling_v3_tuples",
      
      sets: tables.map(t => ({
        id: t.id,
        name: t.name,
        isTuple: t.schema.length > 1,
        schema: t.schema, // Defined columns
        elements: t.items.map(item => {
            // Convert object { Name: 'A', Age: 10 } to array based on schema order ['A', 10]
            if (t.schema.length === 1) return item[t.schema[0].name]; // Simple list
            return t.schema.map(col => item[col.name]); // Tuple list
        })
      })),

      parameters: globals.map(g => ({
        name: g.name,
        value: parseFloat(g.value) || 0
      })),

      variables: variables.map(v => ({
        id: v.id,
        name: v.name,
        type: v.type,
        dimensions: [{ sourceTableId: v.dim1 }, { sourceTableId: v.dim2 }]
      })),

      constraints: constraints.map((c, idx) => {
        const variable = getVar(c.targetVar);
        const loopDim = c.forEachDim; 
        const sumDim = (variable.dim1 === loopDim) ? variable.dim2 : variable.dim1;

        let limit = {};
        if (c.limitType === 'global' && c.limitRef) {
            const g = globals.find(x => x.id === c.limitRef);
            limit = { source: 'global', name: g ? g.name : '???' };
        } else {
            limit = { source: 'number', value: parseFloat(c.limitValue) };
        }

        return {
          id: c.id,
          name: `Rule_${idx + 1}`,
          scope: { forEach: loopDim, sumOver: sumDim },
          variableId: c.targetVar,
          operator: c.operator,
          limit: limit
        };
      }),

      objective: {
          direction: objective.direction,
          terms: objective.terms.map(t => ({
              weight: parseFloat(t.weight),
              variableId: t.variableId
          })),
          offset: objective.offsetType !== 'none' && objective.offsetId ? {
              operation: objective.offsetType,
              referenceName: globals.find(g => g.id === objective.offsetId)?.name
          } : null
      }
    };
  };

  // --- HANDLERS ---
  
  // Set Schema & Data Handling
  const addTable = () => setTables([...tables, { 
      id: `t${Date.now()}`, 
      name: `Set ${tables.length + 1}`, 
      schema: [{ name: 'Name', type: 'string' }], 
      items: [] 
  }]);
  
  const updateTable = (id, val) => setTables(tables.map(t => t.id === id ? { ...t, name: val } : t));

  const addColumn = (tableId) => {
      setTables(tables.map(t => {
          if (t.id !== tableId) return t;
          const newColName = `Col${t.schema.length + 1}`;
          return {
              ...t,
              schema: [...t.schema, { name: newColName, type: 'integer' }],
              // Update existing items to have this new key
              items: t.items.map(item => ({ ...item, [newColName]: '' }))
          };
      }));
  };

  const updateColumn = (tableId, idx, key, val) => {
      setTables(tables.map(t => {
          if(t.id !== tableId) return t;
          const newSchema = [...t.schema];
          // If renaming, we need to update data keys too (complex, skipping for demo simplicity or doing shallow)
          // For demo: just update schema definition
          newSchema[idx] = { ...newSchema[idx], [key]: val };
          return { ...t, schema: newSchema };
      }));
  };

  const removeColumn = (tableId, idx) => {
      setTables(tables.map(t => {
          if (t.id !== tableId || t.schema.length <= 1) return t;
          const colName = t.schema[idx].name;
          const newSchema = t.schema.filter((_, i) => i !== idx);
          // Remove key from items
          const newItems = t.items.map(item => {
              const { [colName]: _, ...rest } = item;
              return rest;
          });
          return { ...t, schema: newSchema, items: newItems };
      }));
  };

  // Set Item Editor
  const openSetEditor = (table) => {
      setEditingSetId(table.id);
      // Deep copy to avoid direct mutation
      setTempSetData(JSON.parse(JSON.stringify(table.items)));
  };

  const saveSetItems = () => {
      setTables(tables.map(t => t.id === editingSetId ? { ...t, items: tempSetData } : t));
      setEditingSetId(null);
  };

  const addTempRow = () => {
      const table = getTable(editingSetId);
      const newRow = {};
      table.schema.forEach(col => newRow[col.name] = "");
      setTempSetData([...tempSetData, newRow]);
  };

  const updateTempRow = (rowIdx, colName, val) => {
      const newData = [...tempSetData];
      newData[rowIdx][colName] = val;
      setTempSetData(newData);
  };

  const removeTempRow = (rowIdx) => {
      setTempSetData(tempSetData.filter((_, i) => i !== rowIdx));
  };

  // ... (Other CRUD handlers same as before)
  const addGlobal = () => setGlobals([...globals, { id: `g${Date.now()}`, name: 'NewParam', value: 0 }]);
  const updateGlobal = (id, key, val) => setGlobals(globals.map(g => g.id === id ? { ...g, [key]: val } : g));
  const removeGlobal = (id) => setGlobals(globals.filter(g => g.id !== id));

  const addVariable = () => {
      if(tables.length < 2) return alert("Need at least 2 sets");
      setVariables([...variables, { id: `v${Date.now()}`, name: 'NewAssign', dim1: tables[0].id, dim2: tables[1].id, type: 'binary' }]);
  };
  const updateVar = (id, key, val) => setVariables(variables.map(v => v.id === id ? { ...v, [key]: val } : v));
  const removeVar = (id) => setVariables(variables.filter(v => v.id !== id));

  const addConstraint = () => {
      if(variables.length === 0) return;
      setConstraints([...constraints, { id: `c${Date.now()}`, targetVar: variables[0].id, forEachDim: variables[0].dim1, operator: '<=', limitType: 'number', limitValue: 1 }]);
  };
  const updateConstraint = (id, key, val) => {
      setConstraints(constraints.map(c => {
          if (c.id !== id) return c;
          const updated = { ...c, [key]: val };
          if (key === 'targetVar') updated.forEachDim = getVar(val).dim1;
          return updated;
      }));
  };
  const removeConstraint = (id) => setConstraints(constraints.filter(c => c.id !== id));

  const addObjectiveTerm = () => {
      if(variables.length === 0) return;
      setObjective({
          ...objective,
          terms: [...objective.terms, { id: `o${Date.now()}`, weight: 1, variableId: variables[0].id }]
      });
  };
  const updateObjTerm = (id, key, val) => {
      setObjective({
          ...objective,
          terms: objective.terms.map(t => t.id === id ? { ...t, [key]: val } : t)
      });
  };
  const removeObjTerm = (id) => {
      setObjective({
          ...objective,
          terms: objective.terms.filter(t => t.id !== id)
      });
  };

  // --- RENDER ---
  const currentEditingTable = getTable(editingSetId);

  return (
    <div className="zb-container">
      <div className="zb-wrapper">
        
        <header>
            <h1 className="zb-header">Generic Scheduling Builder</h1>
            <p className="zb-desc">Define structured sets (tuples), matrices, and optimization logic.</p>
        </header>

        {/* 1. SETS DEFINITION */}
        <div className="zb-card zb-card-blue">
            <div className="zb-section-title">
                <span>1. Define Sets (Schema & Data)</span>
                <button onClick={addTable} className="zb-btn-add">+ Add Set</button>
            </div>
            <div className="zb-grid-2">
                {tables.map(t => (
                    <div key={t.id} style={{background: '#f8fafc', padding: '15px', borderRadius: '8px', border: '1px solid #e2e8f0'}}>
                        <div style={{display:'flex', justifyContent:'space-between'}}>
                            <label className="zb-logic-text">Set Name:</label>
                            <span className="zb-badge">{t.schema.length > 1 ? 'Tuple Table' : 'Simple List'}</span>
                        </div>
                        <input 
                            value={t.name} 
                            onChange={(e) => updateTable(t.id, e.target.value)} 
                            className="zb-input" 
                            style={{width: '100%', marginBottom: '10px', fontWeight: 'bold'}}
                        />
                        
                        {/* Schema Definition */}
                        <div className="zb-schema-container">
                            <span className="zb-logic-text" style={{fontSize:'12px'}}>Columns (Schema):</span>
                            {t.schema.map((col, idx) => (
                                <div key={idx} className="zb-schema-row">
                                    <input 
                                        value={col.name} 
                                        onChange={(e) => updateColumn(t.id, idx, 'name', e.target.value)}
                                        className="zb-input"
                                        style={{padding:'4px', fontSize:'12px', flex:1}}
                                        placeholder="Col Name"
                                    />
                                    <select 
                                        value={col.type} 
                                        onChange={(e) => updateColumn(t.id, idx, 'type', e.target.value)}
                                        className="zb-select"
                                        style={{padding:'4px', fontSize:'12px'}}
                                    >
                                        <option value="string">String</option>
                                        <option value="integer">Number</option>
                                    </select>
                                    {t.schema.length > 1 && (
                                        <button onClick={() => removeColumn(t.id, idx)} style={{color:'red', border:'none', cursor:'pointer'}}>&times;</button>
                                    )}
                                </div>
                            ))}
                            <button onClick={() => addColumn(t.id)} className="zb-btn-add" style={{fontSize:'12px', marginTop:'5px'}}>+ Add Column</button>
                        </div>

                        <div style={{marginTop: '15px', paddingTop:'10px', borderTop:'1px solid #eee', display:'flex', justifyContent:'space-between', alignItems:'center'}}>
                            <div style={{fontSize: '11px', color: '#94a3b8'}}>{t.items.length} items</div>
                            <button onClick={() => openSetEditor(t)} className="zb-btn-edit">Edit Data Items</button>
                        </div>
                    </div>
                ))}
            </div>
        </div>

        {/* 2. GLOBAL CONSTANTS */}
        <div className="zb-card zb-card-orange">
            <div className="zb-section-title">
                <span>2. Global Parameters</span>
                <button onClick={addGlobal} className="zb-btn-add">+ Add Parameter</button>
            </div>
            {globals.map(g => (
                <div key={g.id} className="zb-row">
                    <input value={g.name} onChange={(e) => updateGlobal(g.id, 'name', e.target.value)} className="zb-input" style={{color: '#ea580c', fontWeight: 'bold', flex: 1}} />
                    <span className="zb-logic-text">=</span>
                    <input type="number" value={g.value} onChange={(e) => updateGlobal(g.id, 'value', e.target.value)} className="zb-input" style={{width: '100px'}} />
                    <button onClick={() => removeGlobal(g.id)} className="zb-btn-remove">&times;</button>
                </div>
            ))}
        </div>

        {/* 3. MATRIX VARIABLES */}
        <div className="zb-card zb-card-indigo">
            <div className="zb-section-title">
                <span>3. Assignment Matrices</span>
                <button onClick={addVariable} className="zb-btn-add">+ Create Matrix</button>
            </div>
            {variables.map(v => (
                <div key={v.id} className="zb-row">
                    <span className="zb-logic-text">Matrix:</span>
                    <input value={v.name} onChange={(e) => updateVar(v.id, 'name', e.target.value)} className="zb-input" style={{fontWeight: 'bold', color: '#4f46e5', width: '150px'}} />
                    <span className="zb-logic-text">links</span>
                    <select value={v.dim1} onChange={(e) => updateVar(v.id, 'dim1', e.target.value)} className="zb-select">
                        {tables.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
                    </select>
                    <span className="zb-logic-text">to</span>
                    <select value={v.dim2} onChange={(e) => updateVar(v.id, 'dim2', e.target.value)} className="zb-select">
                        {tables.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
                    </select>
                    <select value={v.type} onChange={(e) => updateVar(v.id, 'type', e.target.value)} className="zb-select" style={{marginLeft: 'auto'}}>
                        <option value="binary">Binary</option>
                        <option value="integer">Integer</option>
                    </select>
                    <button onClick={() => removeVar(v.id)} className="zb-btn-remove">&times;</button>
                </div>
            ))}
        </div>

        {/* 4. CONSTRAINTS */}
        <div className="zb-card zb-card-purple">
            <div className="zb-section-title">
                <span>4. Business Rules</span>
                <button onClick={addConstraint} className="zb-btn-add">+ Add Rule</button>
            </div>
            {constraints.map(c => {
                const targetVar = getVar(c.targetVar);
                const dimOptions = targetVar ? [getTable(targetVar.dim1), getTable(targetVar.dim2)] : [];
                return (
                    <div key={c.id} className="zb-row">
                        <span className="zb-logic-text">For every</span>
                        <select value={c.forEachDim} onChange={(e) => updateConstraint(c.id, 'forEachDim', e.target.value)} className="zb-select" style={{fontWeight: 'bold', color: '#7e22ce'}}>
                            {dimOptions.map(t => t && <option key={t.id} value={t.id}>{t.name}</option>)}
                        </select>
                        <span className="zb-logic-text">, Sum(</span>
                        <select value={c.targetVar} onChange={(e) => updateConstraint(c.id, 'targetVar', e.target.value)} className="zb-select" style={{color: '#4f46e5', fontWeight: 'bold'}}>
                            {variables.map(v => <option key={v.id} value={v.id}>{v.name}</option>)}
                        </select>
                        <span className="zb-logic-text">)</span>
                        <select value={c.operator} onChange={(e) => updateConstraint(c.id, 'operator', e.target.value)} className="zb-select" style={{width: '60px'}}>
                            <option value="<=">&le;</option>
                            <option value=">=">&ge;</option>
                            <option value="==">=</option>
                        </select>
                        <select value={c.limitType} onChange={(e) => {
                            updateConstraint(c.id, 'limitType', e.target.value);
                            if(e.target.value === 'global' && globals.length) updateConstraint(c.id, 'limitRef', globals[0].id);
                        }} className="zb-select">
                            <option value="number">Number</option>
                            {globals.length > 0 && <option value="global">Global Param</option>}
                        </select>
                        {c.limitType === 'number' ? (
                            <input type="number" value={c.limitValue} onChange={(e) => updateConstraint(c.id, 'limitValue', e.target.value)} className="zb-input" style={{width: '80px'}} />
                        ) : (
                            <select value={c.limitRef} onChange={(e) => updateConstraint(c.id, 'limitRef', e.target.value)} className="zb-select">
                                {globals.map(g => <option key={g.id} value={g.id}>{g.name}</option>)}
                            </select>
                        )}
                        <button onClick={() => removeConstraint(c.id)} className="zb-btn-remove">&times;</button>
                    </div>
                );
            })}
        </div>

        {/* 5. OBJECTIVE */}
        <div className="zb-card zb-card-yellow">
            <div className="zb-section-title">
                <span>5. Objective Function</span>
                <select value={objective.direction} onChange={(e) => setObjective({...objective, direction: e.target.value})} className="zb-select" style={{width:'120px'}}>
                    <option value="minimize">Minimize</option>
                    <option value="maximize">Maximize</option>
                </select>
            </div>
            
            {objective.terms.map((term, idx) => (
                <div key={term.id} className="zb-row">
                    <span className="zb-logic-text" style={{color: '#888'}}>Term {idx + 1}:</span>
                    <input 
                        type="number" 
                        value={term.weight} 
                        onChange={(e) => updateObjTerm(term.id, 'weight', e.target.value)} 
                        className="zb-input" 
                        style={{width: '60px', textAlign: 'center'}}
                    />
                    <span className="zb-logic-text">&times; Sum of</span>
                    <select 
                        value={term.variableId} 
                        onChange={(e) => updateObjTerm(term.id, 'variableId', e.target.value)} 
                        className="zb-select"
                        style={{fontWeight: 'bold', color: '#4f46e5'}}
                    >
                        {variables.map(v => <option key={v.id} value={v.id}>{v.name}</option>)}
                    </select>
                    
                    {idx < objective.terms.length - 1 && <span className="zb-logic-text" style={{margin:'0 10px', fontSize:'18px'}}>+</span>}
                    <button onClick={() => removeObjTerm(term.id)} className="zb-btn-remove">&times;</button>
                </div>
            ))}
            <button onClick={addObjectiveTerm} className="zb-btn-add">+ Add Term</button>
        </div>

        <button className="zb-btn-generate" onClick={() => setShowJson(true)}>
            🚀 Generate ZIMPL Payload
        </button>

      </div>

      {/* JSON MODAL */}
      {showJson && (
          <div className="zb-modal-overlay">
              <div className="zb-modal">
                  <div className="zb-modal-header">
                      <span>Server Payload</span>
                      <button className="zb-modal-close" onClick={() => setShowJson(false)}>Close</button>
                  </div>
                  <div className="zb-modal-body zb-modal-body-code">
                      <pre>{JSON.stringify(generateJSON(), null, 2)}</pre>
                  </div>
              </div>
          </div>
      )}

      {/* SET ITEMS EDITOR (GRID OR TEXT) */}
      {editingSetId && (
          <div className="zb-modal-overlay">
              <div className="zb-modal zb-modal-light">
                  <div className="zb-modal-header zb-modal-header-light">
                      <span>Edit Items: {currentEditingTable?.name}</span>
                      <button className="zb-modal-close" style={{background:'#eee', color:'#333'}} onClick={() => setEditingSetId(null)}>&times;</button>
                  </div>
                  <div className="zb-modal-body">
                      
                      {/* Grid Editor */}
                      <table className="zb-grid-table">
                          <thead>
                              <tr>
                                  {currentEditingTable.schema.map((col, i) => (
                                      <th key={i}>{col.name} <span style={{fontSize:'9px', fontWeight:'normal'}}>({col.type})</span></th>
                                  ))}
                                  <th style={{width: '30px'}}></th>
                              </tr>
                          </thead>
                          <tbody>
                              {tempSetData.map((row, rIdx) => (
                                  <tr key={rIdx}>
                                      {currentEditingTable.schema.map((col, cIdx) => (
                                          <td key={cIdx}>
                                              <input 
                                                  className="zb-grid-input"
                                                  value={row[col.name] || ''}
                                                  onChange={(e) => updateTempRow(rIdx, col.name, e.target.value)}
                                              />
                                          </td>
                                      ))}
                                      <td>
                                          <button onClick={() => removeTempRow(rIdx)} style={{color:'red', border:'none', background:'none', cursor:'pointer'}}>&times;</button>
                                      </td>
                                  </tr>
                              ))}
                          </tbody>
                      </table>
                      <button onClick={addTempRow} className="zb-btn-add" style={{marginTop:'10px'}}>+ Add Row</button>

                  </div>
                  <div style={{padding:'20px', borderTop:'1px solid #eee', display:'flex', justifyContent:'flex-end'}}>
                      <button onClick={() => setEditingSetId(null)} className="zb-btn-secondary">Cancel</button>
                      <button onClick={saveSetItems} className="zb-btn-primary">Save Changes</button>
                  </div>
              </div>
          </div>
      )}

    </div>
  );
}