import React, { useState } from 'react';
import MaterialIcon from '../ui/MaterialIcon'; // וודא שהנתיב תואם למיקום הקובץ

interface ConstraintModuleModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function ConstraintModuleModal({ isOpen, onClose }: ConstraintModuleModalProps) {
  // Mock Data: Constraints available to pick
  const [availableConstraints, setAvailableConstraints] = useState([
    { id: 1, name: 'Max Consecutive Shifts', type: 'Hard', desc: 'Limit: 5 shifts' },
    { id: 2, name: 'Weekend Balance', type: 'Soft', desc: 'Weight: 10' },
    { id: 3, name: 'Skill Match Required', type: 'Hard', desc: 'Mandatory' },
    { id: 4, name: 'Avoid Night Shift after Day', type: 'Soft', desc: 'Weight: 5' },
  ]);

  // Mock Data: Constraints already selected
  const [selectedConstraints, setSelectedConstraints] = useState([
    { id: 5, name: 'Min Rest Period (11h)', type: 'Hard', desc: 'EU Directive 2003/88' },
    { id: 6, name: 'Max Weekly Hours (48h)', type: 'Hard', desc: 'Rolling Avg 17 weeks' },
    { id: 7, name: 'Fair Distribution of Holidays', type: 'Soft', desc: 'Weight: 25' },
  ]);

  // Function to move item to "Selected"
  const addToModule = (item: any) => {
    setAvailableConstraints(availableConstraints.filter(i => i.id !== item.id));
    setSelectedConstraints([...selectedConstraints, item]);
  };

  // Function to move item back to "Available"
  const removeFromModule = (item: any) => {
    setSelectedConstraints(selectedConstraints.filter(i => i.id !== item.id));
    setAvailableConstraints([...availableConstraints, item]);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 animate-in fade-in duration-200">
      {/* Scrollbar Styles */}
      <style>{`
        .custom-scrollbar::-webkit-scrollbar { width: 6px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
        .custom-scrollbar::-webkit-scrollbar-thumb { background-color: #e5e7eb; border-radius: 20px; }
        .dark .custom-scrollbar::-webkit-scrollbar-thumb { background-color: #374151; }
      `}</style>

      <div className="bg-white dark:bg-[#182830] w-full max-w-5xl rounded-2xl shadow-2xl border border-gray-200 dark:border-gray-700 max-h-[90vh] flex flex-col overflow-hidden animate-in zoom-in-95 duration-200">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200 dark:border-gray-700 shrink-0">
          <h2 className="text-xl font-bold text-gray-900 dark:text-white">Edit Constraint Module</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors rounded-full p-1">
            <MaterialIcon icon="close" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6 custom-scrollbar">
          {/* Form Inputs */}
          <div className="mb-8">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="flex flex-col gap-2">
                <label className="text-sm font-semibold text-gray-700 dark:text-gray-300">Module Name</label>
                <input 
                  className="w-full rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 px-4 py-2.5 text-sm focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] outline-none transition-all dark:text-white" 
                  placeholder="e.g. Core Business Rules" 
                  defaultValue="Senior Staff Overtime Rules"
                  type="text" 
                />
              </div>
              <div className="flex flex-col gap-2 md:col-span-2">
                <label className="text-sm font-semibold text-gray-700 dark:text-gray-300">Description</label>
                <textarea 
                  className="w-full rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 px-4 py-3 text-sm focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] outline-none transition-all min-h-[80px] resize-y dark:text-white" 
                  placeholder="Describe the purpose of this module..."
                  defaultValue="Defines the maximum overtime limits and mandatory rest periods for all senior staff members to ensure compliance with labor laws."
                ></textarea>
              </div>
            </div>
          </div>

          {/* Dual List Interface */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 h-[500px]">
            
            {/* Left Column: Available */}
            <div className="flex flex-col bg-[#f6f7f8] dark:bg-[#101c22] rounded-xl border border-[#e5e7eb] dark:border-gray-700 shadow-sm overflow-hidden h-full">
              <div className="p-4 border-b border-[#e5e7eb] dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50">
                <div className="flex items-center justify-between mb-3">
                  <h3 className="font-bold text-gray-800 dark:text-white flex items-center gap-2">
                    <MaterialIcon icon="library_books" className="text-gray-500" />
                    Available Constraints
                  </h3>
                  <span className="text-xs font-medium bg-gray-200 dark:bg-gray-700 px-2 py-1 rounded-full text-gray-600 dark:text-gray-300">{availableConstraints.length} items</span>
                </div>
                <div className="relative">
                  <div className="absolute left-3 top-2.5 pointer-events-none">
                     <MaterialIcon icon="search" className="text-gray-400 text-[20px]" />
                  </div>
                  <input className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-sm focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] outline-none dark:text-white" placeholder="Search library..." type="text"/>
                </div>
              </div>
              <div className="flex-1 overflow-y-auto p-2 space-y-1 custom-scrollbar">
                {availableConstraints.map((item) => (
                  <div key={item.id} className="group flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 border border-transparent hover:border-gray-100 dark:hover:border-gray-700 transition-all cursor-default">
                    <div className="flex flex-col gap-1">
                      <p className="text-sm font-semibold text-gray-800 dark:text-gray-200">{item.name}</p>
                      <div className="flex items-center gap-2">
                        <span className={`text-[10px] uppercase font-bold px-1.5 py-0.5 rounded ${item.type === 'Hard' ? 'text-orange-600 bg-orange-100 dark:bg-orange-900/30 dark:text-orange-400' : 'text-blue-600 bg-blue-100 dark:bg-blue-900/30 dark:text-blue-400'}`}>{item.type}</span>
                        <span className="text-xs text-gray-500 dark:text-gray-400">{item.desc}</span>
                      </div>
                    </div>
                    <button onClick={() => addToModule(item)} className="size-8 flex items-center justify-center rounded-lg text-gray-400 hover:text-[#13a4ec] hover:bg-[#13a4ec]/10 transition-colors" title="Add to module">
                      <MaterialIcon icon="add_circle" />
                    </button>
                  </div>
                ))}
              </div>
            </div>

            {/* Right Column: Selected */}
            <div className="flex flex-col bg-[#f6f7f8] dark:bg-[#101c22] rounded-xl border border-[#e5e7eb] dark:border-gray-700 shadow-sm overflow-hidden h-full">
              <div className="p-4 border-b border-[#e5e7eb] dark:border-gray-700 bg-[#13a4ec]/5 dark:bg-[#13a4ec]/10">
                <div className="flex items-center justify-between mb-3">
                  <h3 className="font-bold text-[#13a4ec] dark:text-sky-400 flex items-center gap-2">
                    <MaterialIcon icon="check_circle" className="fill-current" />
                    Constraints in this Module
                  </h3>
                  <span className="text-xs font-medium bg-white dark:bg-gray-800 px-2 py-1 rounded-full text-[#13a4ec] dark:text-sky-400 border border-[#13a4ec]/20">{selectedConstraints.length} selected</span>
                </div>
                <div className="relative">
                  <div className="absolute left-3 top-2.5 pointer-events-none">
                     <MaterialIcon icon="search" className="text-gray-400 text-[20px]" />
                  </div>
                  <input className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-900 text-sm focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] outline-none dark:text-white" placeholder="Filter included..." type="text"/>
                </div>
              </div>
              <div className="flex-1 overflow-y-auto p-2 space-y-1 custom-scrollbar">
                {selectedConstraints.map((item) => (
                   <div key={item.id} className="group flex items-center justify-between p-3 rounded-lg bg-white dark:bg-gray-900 border border-gray-100 dark:border-gray-700 hover:border-red-200 dark:hover:border-red-900/50 transition-all shadow-sm">
                   <div className="flex flex-col gap-1">
                     <p className="text-sm font-semibold text-gray-800 dark:text-gray-200">{item.name}</p>
                     <div className="flex items-center gap-2">
                       <span className={`text-[10px] uppercase font-bold px-1.5 py-0.5 rounded ${item.type === 'Hard' ? 'text-orange-600 bg-orange-100 dark:bg-orange-900/30 dark:text-orange-400' : 'text-blue-600 bg-blue-100 dark:bg-blue-900/30 dark:text-blue-400'}`}>{item.type}</span>
                       <span className="text-xs text-gray-500 dark:text-gray-400">{item.desc}</span>
                     </div>
                   </div>
                   <button onClick={() => removeFromModule(item)} className="size-8 flex items-center justify-center rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors" title="Remove from module">
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
          <button onClick={onClose} className="px-5 py-2.5 rounded-lg bg-[#13a4ec] hover:bg-sky-600 text-white font-medium shadow-sm transition-colors flex items-center gap-2">
            <MaterialIcon icon="save" className="text-[20px]" />
            Save Module
          </button>
        </div>
      </div>
    </div>
  );
}