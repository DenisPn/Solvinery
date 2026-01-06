import { useState, useEffect } from 'react';
import MaterialIcon from '../ui/MaterialIcon';

interface ConstraintItem {
  id: string; // ה-Identifier מה-ZPL
  name: string;
  type: string;
  desc: string;
}

interface ConstraintModuleModalProps {
  isOpen: boolean;
  onClose: () => void;
  availableLibrary: ConstraintItem[]; // <-- המידע מה-ZPL מגיע לכאן
  onSave: (moduleName: string, items: ConstraintItem[]) => void; // החזרת המודול החדש לאבא
}

export default function ConstraintModuleModal({ isOpen, onClose, availableLibrary, onSave }: ConstraintModuleModalProps) {
  // ניהול רשימות מקומיות למודאל
  const [availableConstraints, setAvailableConstraints] = useState<ConstraintItem[]>([]);
  const [selectedConstraints, setSelectedConstraints] = useState<ConstraintItem[]>([]);
  const [moduleName, setModuleName] = useState('');

  // בכל פעם שהמודאל נפתח מחדש, נאפס את הנתונים
  useEffect(() => {
    if (isOpen) {
      setAvailableConstraints(availableLibrary);
      setSelectedConstraints([]);
      setModuleName('');
    }
  }, [isOpen, availableLibrary]);

  const addToModule = (item: ConstraintItem) => {
    setAvailableConstraints(prev => prev.filter(i => i.id !== item.id));
    setSelectedConstraints(prev => [...prev, item]);
  };

  const removeFromModule = (item: ConstraintItem) => {
    setSelectedConstraints(prev => prev.filter(i => i.id !== item.id));
    setAvailableConstraints(prev => [...prev, item]);
  };

  const handleSave = () => {
    if (!moduleName) return alert("Please give the module a name");
    onSave(moduleName, selectedConstraints);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 animate-in fade-in duration-200">
      {/* ... (Style tag for scrollbar remains same) ... */}
      <style>{`.custom-scrollbar::-webkit-scrollbar { width: 6px; } .custom-scrollbar::-webkit-scrollbar-track { background: transparent; } .custom-scrollbar::-webkit-scrollbar-thumb { background-color: #e5e7eb; border-radius: 20px; } .dark .custom-scrollbar::-webkit-scrollbar-thumb { background-color: #374151; }`}</style>

      <div className="bg-white dark:bg-[#182830] w-full max-w-5xl rounded-2xl shadow-2xl border border-gray-200 dark:border-gray-700 max-h-[90vh] flex flex-col overflow-hidden animate-in zoom-in-95 duration-200">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200 dark:border-gray-700 shrink-0">
          <h2 className="text-xl font-bold text-gray-900 dark:text-white">Create Constraint Module</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors rounded-full p-1">
            <MaterialIcon icon="close" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6 custom-scrollbar">
          {/* Module Name Input */}
          <div className="mb-6">
            <label className="text-sm font-semibold text-gray-700 dark:text-gray-300 block mb-2">Module Name</label>
            <input 
              value={moduleName}
              onChange={(e) => setModuleName(e.target.value)}
              className="w-full rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 px-4 py-2.5 text-sm focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] outline-none transition-all dark:text-white" 
              placeholder="e.g. Senior Staff Rules" 
              type="text" 
            />
          </div>

          {/* Dual List */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 h-[400px]">
            
            {/* Left: Available (From ZPL) */}
            <div className="flex flex-col bg-[#f6f7f8] dark:bg-[#101c22] rounded-xl border border-[#e5e7eb] dark:border-gray-700 shadow-sm overflow-hidden h-full">
              <div className="p-4 border-b border-[#e5e7eb] dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50 flex justify-between items-center">
                <h3 className="font-bold text-gray-800 dark:text-white flex items-center gap-2">
                  <MaterialIcon icon="library_books" className="text-gray-500" />
                  ZPL Constraints
                </h3>
                <span className="text-xs font-medium bg-gray-200 dark:bg-gray-700 px-2 py-1 rounded-full text-gray-600 dark:text-gray-300">{availableConstraints.length}</span>
              </div>
              <div className="flex-1 overflow-y-auto p-2 space-y-1 custom-scrollbar">
                {availableConstraints.map((item) => (
                  <div key={item.id} className="group flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 border border-transparent hover:border-gray-100 dark:hover:border-gray-700 transition-all cursor-default">
                    <div className="flex flex-col gap-1 overflow-hidden">
                      <p className="text-sm font-semibold text-gray-800 dark:text-gray-200 truncate" title={item.name}>{item.name}</p>
                      <span className="text-[10px] uppercase font-bold text-orange-600 bg-orange-100 dark:bg-orange-900/30 dark:text-orange-400 px-1.5 py-0.5 rounded w-fit">Hard</span>
                    </div>
                    <button onClick={() => addToModule(item)} className="size-8 flex shrink-0 items-center justify-center rounded-lg text-gray-400 hover:text-[#13a4ec] hover:bg-[#13a4ec]/10 transition-colors">
                      <MaterialIcon icon="add_circle" />
                    </button>
                  </div>
                ))}
              </div>
            </div>

            {/* Right: Selected (New Module) */}
            <div className="flex flex-col bg-[#f6f7f8] dark:bg-[#101c22] rounded-xl border border-[#e5e7eb] dark:border-gray-700 shadow-sm overflow-hidden h-full">
              <div className="p-4 border-b border-[#e5e7eb] dark:border-gray-700 bg-[#13a4ec]/5 dark:bg-[#13a4ec]/10 flex justify-between items-center">
                <h3 className="font-bold text-[#13a4ec] dark:text-sky-400 flex items-center gap-2">
                  <MaterialIcon icon="check_circle" className="fill-current" />
                  Selected
                </h3>
                <span className="text-xs font-medium bg-white dark:bg-gray-800 px-2 py-1 rounded-full text-[#13a4ec] dark:text-sky-400 border border-[#13a4ec]/20">{selectedConstraints.length}</span>
              </div>
              <div className="flex-1 overflow-y-auto p-2 space-y-1 custom-scrollbar">
                {selectedConstraints.length === 0 && (
                    <div className="flex flex-col items-center justify-center h-full text-gray-400 text-sm">
                        <p>Drag or add items here</p>
                    </div>
                )}
                {selectedConstraints.map((item) => (
                   <div key={item.id} className="group flex items-center justify-between p-3 rounded-lg bg-white dark:bg-gray-900 border border-gray-100 dark:border-gray-700 hover:border-red-200 dark:hover:border-red-900/50 transition-all shadow-sm">
                   <p className="text-sm font-semibold text-gray-800 dark:text-gray-200 truncate">{item.name}</p>
                   <button onClick={() => removeFromModule(item)} className="size-8 flex shrink-0 items-center justify-center rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors">
                     <MaterialIcon icon="delete" />
                   </button>
                 </div>
                ))}
              </div>
            </div>

          </div>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-end gap-3 px-6 py-4 border-t border-gray-200 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50 shrink-0">
          <button onClick={onClose} className="px-5 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-200 font-medium hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
            Cancel
          </button>
          <button onClick={handleSave} className="px-5 py-2.5 rounded-lg bg-[#13a4ec] hover:bg-sky-600 text-white font-medium shadow-sm transition-colors flex items-center gap-2">
            <MaterialIcon icon="save" className="text-[20px]" />
            Save Module
          </button>
        </div>
      </div>
    </div>
  );
}