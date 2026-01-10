import { useState, useRef, useEffect } from 'react';
import MaterialIcon from '../ui/MaterialIcon';

export interface StructureItem {
  id: string;
  type: 'Integer' | 'Boolean' | 'String' | 'Float' | 'Array' | 'Set' | 'Decision' | 'Date';
  label: string;
  subLabel: string;
}

interface VariableModalProps {
  isOpen: boolean;
  onClose: () => void;
  // תיקון 1: הוספת objectiveValueAlias להגדרת הטיפוס
  onSave: (data: { 
    name: string; 
    alias: string; 
    objectiveValueAlias: string; 
    structure: StructureItem[] 
  }) => void;
  initialData?: any;
}

export default function VariableModal({ isOpen, onClose, onSave, initialData }: VariableModalProps) {
  const [name, setName] = useState('');
  const [alias, setAlias] = useState('');
  const [objAlias, setObjAlias] = useState('');
  
  const [structure, setStructure] = useState<StructureItem[]>([
    { id: '1', type: 'Set', label: 'Set', subLabel: 'Employees (Index)' },
    { id: '2', type: 'Array', label: 'Array', subLabel: 'Dimension: 7 (Days)' },
    { id: '3', type: 'Integer', label: 'Integer', subLabel: 'Binary (0 or 1)' }
  ]);

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [draggedItemIndex, setDraggedItemIndex] = useState<number | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    if (isOpen) {
      if (initialData) {
        setName(initialData.name || '');
        setAlias(initialData.alias !== '-' ? initialData.alias : '');
        setObjAlias(initialData.objectiveValueAlias !== '-' ? initialData.objectiveValueAlias : '');
        
        if (initialData.type && typeof initialData.type === 'string') {
            const cleanType = initialData.type.replace('[', '').replace(']', '');
            const types = cleanType.split(',').map((t: string) => t.trim());
            
            if (types.length > 0 && types[0] !== '') {
                const newStructure: StructureItem[] = types.map((t: string, index: number) => {
                    let mappedType: StructureItem['type'] = 'Integer';
                    const upperT = t.toUpperCase();
                    if (upperT === 'INT' || upperT === 'INTEGER') mappedType = 'Integer';
                    else if (upperT === 'TEXT' || upperT === 'STRING') mappedType = 'String';
                    else if (upperT === 'BOOL' || upperT === 'BOOLEAN') mappedType = 'Boolean';
                    else if (upperT === 'FLOAT') mappedType = 'Float';
                    else if (upperT.includes('ARRAY') || upperT === 'ARR') mappedType = 'Array';
                    else if (upperT.includes('SET')) mappedType = 'Set';
                    
                    return {
                        id: `loaded-${index}`,
                        type: mappedType,
                        label: mappedType,
                        subLabel: 'Imported'
                    };
                });
                setStructure(newStructure);
            }
        }
      } else {
        setName('');
        setAlias('');
        setObjAlias('');
        setStructure([
            { id: '1', type: 'Set', label: 'Set', subLabel: 'Employees (Index)' },
            { id: '2', type: 'Array', label: 'Array', subLabel: 'Dimension: 7 (Days)' },
            { id: '3', type: 'Integer', label: 'Integer', subLabel: 'Binary (0 or 1)' }
        ]);
      }
    }
  }, [isOpen, initialData]);

  if (!isOpen) return null;

  const moveItem = (index: number, direction: -1 | 1) => {
    const newStructure = [...structure];
    const targetIndex = index + direction;
    if (targetIndex < 0 || targetIndex >= newStructure.length) return;
    [newStructure[index], newStructure[targetIndex]] = [newStructure[targetIndex], newStructure[index]];
    setStructure(newStructure);
  };

  const handleDragStart = (index: number) => {
    setDraggedItemIndex(index);
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
  };

  const handleDrop = (targetIndex: number) => {
    if (draggedItemIndex === null || draggedItemIndex === targetIndex) return;
    const newStructure = [...structure];
    const [draggedItem] = newStructure.splice(draggedItemIndex, 1);
    newStructure.splice(targetIndex, 0, draggedItem);
    setStructure(newStructure);
    setDraggedItemIndex(null);
  };

  const getTypeConfig = (type: string) => {
    switch (type) {
      case 'Integer': return { color: 'text-emerald-500', bg: 'bg-white dark:bg-[#1a2c35]', border: 'border-gray-100 dark:border-gray-700 hover:border-emerald-500', icon: '123' };
      case 'Boolean': return { color: 'text-orange-500', bg: 'bg-white dark:bg-[#1a2c35]', border: 'border-gray-100 dark:border-gray-700 hover:border-orange-500', icon: 'toggle_on' };
      case 'String': return { color: 'text-blue-500', bg: 'bg-white dark:bg-[#1a2c35]', border: 'border-gray-100 dark:border-gray-700 hover:border-blue-500', icon: 'abc' };
      case 'Array': return { color: 'text-purple-500', bg: 'bg-white dark:bg-[#1a2c35]', border: 'border-gray-100 dark:border-gray-700 hover:border-purple-500', icon: 'data_array' };
      case 'Set': return { color: 'text-[#13a4ec]', bg: 'bg-white dark:bg-[#1a2c35]', border: 'border-gray-100 dark:border-gray-700 hover:border-primary', icon: 'category' };
      case 'Float': return { color: 'text-teal-500', bg: 'bg-white dark:bg-[#1a2c35]', border: 'border-gray-100 dark:border-gray-700 hover:border-teal-500', icon: 'decimal_increase' };
      case 'Decision': return { color: 'text-slate-500', bg: 'bg-white dark:bg-[#1a2c35]', border: 'border-gray-100 dark:border-gray-700 hover:border-slate-500', icon: 'functions' };
      case 'Date': return { color: 'text-pink-500', bg: 'bg-white dark:bg-[#1a2c35]', border: 'border-gray-100 dark:border-gray-700 hover:border-pink-500', icon: 'calendar_today' };
      default: return { color: 'text-gray-500', bg: 'bg-white', border: 'border-gray-200', icon: 'circle' };
    }
  };
  
  const getBadgeStyle = (type: string) => {
      switch (type) {
          case 'Set': return 'bg-blue-50 dark:bg-blue-900/20 text-primary border-blue-100 dark:border-blue-900/50';
          case 'Array': return 'bg-purple-50 dark:bg-purple-900/20 text-purple-600 dark:text-purple-400 border-purple-100 dark:border-purple-900/50';
          case 'Integer': return 'bg-emerald-50 dark:bg-emerald-900/20 text-emerald-600 dark:text-emerald-400 border-emerald-100 dark:border-emerald-900/50';
          case 'Boolean': return 'bg-orange-50 dark:bg-orange-900/20 text-orange-600 dark:text-orange-400 border-orange-100 dark:border-orange-900/50';
          default: return 'bg-gray-50 dark:bg-gray-800 text-gray-600 dark:text-gray-300 border-gray-200';
      }
  };

  const addType = (type: StructureItem['type']) => {
    const newItem: StructureItem = {
      id: Math.random().toString(36).substr(2, 9),
      type,
      label: type,
      subLabel: 'New Element'
    };
    setStructure([...structure, newItem]);
    setIsDropdownOpen(false);
  };

  const removeType = (id: string) => {
    setStructure(structure.filter(item => item.id !== id));
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 sm:p-6 transition-opacity duration-300 animate-in fade-in">
      <style>{`.custom-scrollbar::-webkit-scrollbar { width: 6px; height: 6px; } .custom-scrollbar::-webkit-scrollbar-track { background: transparent; } .custom-scrollbar::-webkit-scrollbar-thumb { background-color: #d1d5db; border-radius: 9999px; } .dark .custom-scrollbar::-webkit-scrollbar-thumb { background-color: #4b5563; } .custom-scrollbar::-webkit-scrollbar-thumb:hover { background-color: #9ca3af; }`}</style>

      <div className="w-full max-w-[720px] flex flex-col bg-white dark:bg-[#1a2c35] rounded-xl shadow-[0_8px_30px_rgb(0,0,0,0.12)] max-h-[90vh] overflow-hidden transform transition-all animate-in zoom-in-95 duration-200">
        
        {/* Header */}
        <div className="flex items-start justify-between p-6 pb-4 border-b border-[#dbe2e6] dark:border-gray-700 bg-white dark:bg-[#1a2c35] sticky top-0 z-10 shrink-0">
          <div className="flex flex-col gap-1">
            <h2 className="text-[#111618] dark:text-white text-2xl font-bold leading-tight tracking-tight">
                {initialData ? 'Edit Variable' : 'Create New Variable'}
            </h2>
            <p className="text-[#617c89] dark:text-gray-400 text-sm font-normal">Define the properties, aliases, and structure of your variable below.</p>
          </div>
          <button onClick={onClose} className="text-[#617c89] hover:text-[#111618] dark:text-gray-400 dark:hover:text-white transition-colors p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center justify-center">
            <MaterialIcon icon="close" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6 space-y-8 custom-scrollbar">
          
          <div className="flex flex-col gap-2">
            <label className="text-[#111618] dark:text-gray-200 text-sm font-medium leading-normal">Variable Name</label>
            <input 
              value={name} 
              onChange={(e) => setName(e.target.value)}
              className="w-full rounded-lg border border-[#dbe2e6] dark:border-gray-600 bg-white dark:bg-[#101c22] px-4 py-3 text-[#111618] dark:text-white placeholder-[#617c89] dark:placeholder-gray-500 focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] focus:outline-none text-base transition-shadow" 
              placeholder="e.g., variable_name" type="text" 
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="flex flex-col gap-2">
              <label className="text-[#111618] dark:text-gray-200 text-sm font-medium leading-normal">Alias</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-[#617c89]">
                  <MaterialIcon icon="short_text" className="text-[20px]" />
                </span>
                <input 
                  value={alias} 
                  onChange={(e) => setAlias(e.target.value)}
                  className="w-full rounded-lg border border-[#dbe2e6] dark:border-gray-600 bg-white dark:bg-[#101c22] pl-10 pr-4 py-3 text-[#111618] dark:text-white placeholder-[#617c89] dark:placeholder-gray-500 focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] focus:outline-none text-base transition-shadow" 
                  placeholder="e.g., S_Start" type="text" 
                />
              </div>
            </div>
            <div className="flex flex-col gap-2">
              <label className="text-[#111618] dark:text-gray-200 text-sm font-medium leading-normal flex items-center justify-between">
                Objective Value Alias 
                <span className="text-[#617c89] dark:text-gray-500 font-normal text-xs bg-gray-100 dark:bg-gray-800 px-2 py-0.5 rounded">Optional</span>
              </label>
              <input 
                value={objAlias} 
                onChange={(e) => setObjAlias(e.target.value)}
                className="w-full rounded-lg border border-[#dbe2e6] dark:border-gray-600 bg-white dark:bg-[#101c22] px-4 py-3 text-[#111618] dark:text-white placeholder-[#617c89] dark:placeholder-gray-500 focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] focus:outline-none text-base transition-shadow" 
                placeholder="e.g., Obj_S_Start" type="text" 
              />
            </div>
          </div>

          <div className="flex flex-col gap-4">
            <div className="flex items-center gap-2 border-b border-[#dbe2e6] dark:border-gray-700 pb-2">
              <MaterialIcon icon="schema" className="text-[#13a4ec] text-xl" />
              <h3 className="text-[#111618] dark:text-white text-lg font-bold leading-tight">Structure Definition</h3>
            </div>
            
            <div className="bg-[#f6f7f8] dark:bg-[#101c22] rounded-lg p-5 border border-[#dbe2e6] dark:border-gray-700 shadow-sm flex flex-col min-h-[400px]">
              <p className="text-[#617c89] dark:text-gray-400 text-sm mb-4">Define the ordered composition of your variable structure. Elements are processed sequentially.</p>
              
              <div className="relative mb-4 shrink-0">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-[#617c89]">
                  <MaterialIcon icon="search" className="text-[20px]" />
                </span>
                <input className="w-full rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-[#1a2c35] py-2 pl-10 pr-4 text-sm text-[#111618] dark:text-white focus:border-[#13a4ec] focus:outline-none focus:ring-1 focus:ring-[#13a4ec] transition-all shadow-sm" placeholder="Search data types in structure..." type="text"/>
              </div>

              <div className="relative mb-4 shrink-0" ref={dropdownRef}>
                <button 
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  className={`w-full flex items-center justify-center gap-2 py-3 px-4 rounded-lg border-2 border-dashed transition-all
                    ${isDropdownOpen ? 'border-[#13a4ec] bg-blue-50/50 dark:bg-blue-900/20 text-[#13a4ec]' : 'border-[#dbe2e6] dark:border-gray-600 bg-white/50 dark:bg-[#1a2c35]/50 text-[#617c89] dark:text-gray-400 hover:border-[#13a4ec] hover:text-[#13a4ec]'}
                  `}
                >
                  <MaterialIcon icon="add_circle" />
                  Add Data Type
                </button>

                {isDropdownOpen && (
                  <div className="absolute top-full left-0 right-0 mt-2 bg-white dark:bg-[#1a2c35] shadow-[0_10px_40px_-10px_rgba(0,0,0,0.2)] rounded-xl border border-gray-200 dark:border-gray-700 block z-50 overflow-hidden ring-1 ring-black/5 animate-in fade-in zoom-in-95 duration-100 origin-top">
                    <div className="px-4 py-2 border-b border-gray-100 dark:border-gray-700 bg-gray-50/50 dark:bg-[#15232b] flex justify-between items-center">
                      <span className="text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Select Type to Append</span>
                      <button onClick={() => setIsDropdownOpen(false)} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors"><MaterialIcon icon="close" className="text-[18px]"/></button>
                    </div>
                    <div className="grid grid-cols-4 gap-3 p-4">
                      {['Integer', 'Boolean', 'String', 'Float', 'Array', 'Set', 'Decision', 'Date'].map((type) => {
                         const conf = getTypeConfig(type);
                         return (
                          <button key={type} onClick={() => addType(type as any)} className={`flex flex-col items-center justify-center gap-1.5 h-20 rounded-lg border ${conf.border} ${conf.bg} hover:shadow-sm transition-all group`}>
                            <MaterialIcon icon={conf.icon} className={`text-2xl ${conf.color} group-hover:scale-110 transition-transform`} />
                            <span className={`text-xs font-semibold text-gray-600 dark:text-gray-300 group-hover:${conf.color}`}>{type}</span>
                          </button>
                         )
                      })}
                    </div>
                  </div>
                )}
              </div>

              <div className="flex-1 flex flex-col min-h-0 relative">
                <div className="absolute left-6 top-0 bottom-0 w-0.5 bg-gray-300 dark:bg-gray-700 -z-0"></div>
                <div className="overflow-y-auto pr-2 custom-scrollbar -mr-2 flex-1 pb-2 pl-1">
                  <ul className="space-y-3 relative z-10">
                    {structure.map((item, index) => {
                      const conf = getTypeConfig(item.type);
                      const badgeClass = getBadgeStyle(item.type);
                      const isDragging = draggedItemIndex === index;

                      return (
                        <li 
                          key={item.id} 
                          draggable
                          onDragStart={() => handleDragStart(index)}
                          onDragOver={handleDragOver}
                          onDrop={() => handleDrop(index)}
                          className={`group flex items-center gap-3 p-3 bg-white dark:bg-[#1a2c35] border border-gray-200 dark:border-gray-600 rounded-lg shadow-sm hover:border-[#13a4ec]/50 dark:hover:border-[#13a4ec]/50 transition-all
                            ${isDragging ? 'opacity-50 border-dashed border-[#13a4ec]' : ''}
                          `}
                        >
                          <div className="cursor-grab text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300 active:cursor-grabbing">
                            <MaterialIcon icon="drag_indicator" className="text-[20px]" />
                          </div>
                          <div className={`w-9 h-9 rounded ${badgeClass} flex items-center justify-center shrink-0 border`}>
                            <MaterialIcon icon={conf.icon} className="text-[20px]" />
                          </div>
                          <div className="flex-1 flex flex-col">
                            <div className="flex items-center gap-2">
                              <span className="text-[#111618] dark:text-white text-sm font-semibold">{item.label}</span>
                              <span className="px-2 py-0.5 rounded text-[10px] font-medium bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300 uppercase tracking-wider">Type</span>
                            </div>
                            <span className="text-[#617c89] dark:text-gray-400 text-xs">{item.subLabel}</span>
                          </div>
                          <div className="flex items-center gap-1 opacity-100 sm:opacity-0 sm:group-hover:opacity-100 transition-opacity">
                            <button 
                              onClick={() => moveItem(index, -1)} 
                              disabled={index === 0}
                              className={`p-1.5 rounded transition-colors ${index === 0 ? 'opacity-20 cursor-not-allowed' : 'text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700'}`}
                            >
                              <MaterialIcon icon="arrow_upward" className="text-[18px]" />
                            </button>
                            <button 
                              onClick={() => moveItem(index, 1)} 
                              disabled={index === structure.length - 1}
                              className={`p-1.5 rounded transition-colors ${index === structure.length - 1 ? 'opacity-20 cursor-not-allowed' : 'text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700'}`}
                            >
                              <MaterialIcon icon="arrow_downward" className="text-[18px]" />
                            </button>
                            <button className="p-1.5 text-blue-500 hover:text-blue-600 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded transition-colors"><MaterialIcon icon="edit" className="text-[18px]" /></button>
                            <button onClick={() => removeType(item.id)} className="p-1.5 text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 rounded transition-colors"><MaterialIcon icon="delete" className="text-[18px]" /></button>
                          </div>
                        </li>
                      );
                    })}
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-6 border-t border-[#dbe2e6] dark:border-gray-700 bg-gray-50 dark:bg-[#101c22] flex flex-col sm:flex-row justify-end gap-3 rounded-b-xl shrink-0">
          <button onClick={onClose} className="px-6 py-3 sm:py-2.5 rounded-lg border border-[#dbe2e6] dark:border-gray-600 bg-white dark:bg-transparent text-[#111618] dark:text-white font-medium hover:bg-gray-50 dark:hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-200 dark:focus:ring-gray-700 transition-colors w-full sm:w-auto">
            Cancel
          </button>
          
          {/* תיקון 2: הוספת objectiveValueAlias: objAlias לפונקציית השמירה */}
          <button onClick={() => onSave({ name, alias, objectiveValueAlias: objAlias, structure })} className="px-6 py-3 sm:py-2.5 rounded-lg bg-[#13a4ec] text-white font-medium hover:bg-[#1195d8] focus:outline-none focus:ring-2 focus:ring-[#13a4ec] focus:ring-offset-2 dark:focus:ring-offset-[#1a2c35] shadow-sm transition-colors flex items-center justify-center gap-2 w-full sm:w-auto">
            <MaterialIcon icon="save" className="text-[20px]" />
            Save Changes
          </button>
        </div>

      </div>
    </div>
  );
}