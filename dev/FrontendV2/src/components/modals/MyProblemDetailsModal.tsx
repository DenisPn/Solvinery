import React, { useEffect } from 'react';
// ✅ תיקון: הוספת המילה type
import type { MyImageCardProps } from '../MyImageCard';

interface MyProblemDetailsModalProps {
  isOpen: boolean;
  onClose: () => void;
  data: MyImageCardProps | null;
  onEdit: (id: string | number) => void;
  onSolve: (id: string | number) => void;
}

const MyProblemDetailsModal: React.FC<MyProblemDetailsModalProps> = ({
  isOpen,
  onClose,
  data,
  onEdit,
  onSolve
}) => {

  // Prevent background scrolling when modal is open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => { document.body.style.overflow = 'unset'; };
  }, [isOpen]);

  if (!isOpen || !data) return null;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 md:p-8 font-sans">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity" 
        onClick={onClose}
      ></div>

      {/* Modal Container */}
      <div className="relative z-10 w-full max-w-5xl h-[90vh] flex flex-col bg-[#f6f7f8] dark:bg-[#101c22] rounded-xl shadow-2xl overflow-hidden border border-slate-200 dark:border-slate-800 animate-in fade-in zoom-in-95 duration-200">
        
        {/* Custom Scrollbar Styles */}
        <style>{`
          .custom-scrollbar::-webkit-scrollbar { width: 6px; }
          .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
          .custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }
          .dark .custom-scrollbar::-webkit-scrollbar-thumb { background: #374151; }
        `}</style>

        {/* Fixed Header */}
        <header className="flex items-center justify-between px-6 py-4 bg-white dark:bg-[#0f172a] border-b border-slate-200 dark:border-slate-800 shrink-0">
          <div className="flex items-center gap-3">
            <span className="material-symbols-outlined text-[#13a4ec] text-3xl">settings_suggest</span>
            <h1 className="text-[#111618] dark:text-white text-2xl font-bold leading-tight">My Problem Details</h1>
          </div>
          <button 
            onClick={onClose}
            className="flex items-center justify-center rounded-lg h-10 w-10 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-500 transition-colors"
          >
            <span className="material-symbols-outlined">close</span>
          </button>
        </header>

        {/* Fixed Tabs */}
        <nav className="bg-white dark:bg-[#0f172a] border-b border-slate-200 dark:border-slate-800 shrink-0">
          <div className="flex px-6 gap-8 overflow-x-auto">
            <button className="flex flex-col items-center justify-center border-b-[3px] border-[#13a4ec] text-[#13a4ec] pb-3 pt-4">
              <p className="text-sm font-bold leading-normal tracking-wide">Overview</p>
            </button>
            <button className="flex flex-col items-center justify-center border-b-[3px] border-transparent text-slate-500 dark:text-slate-400 hover:text-[#13a4ec] pb-3 pt-4 transition-colors">
              <p className="text-sm font-bold leading-normal tracking-wide">Constraints</p>
            </button>
            <button className="flex flex-col items-center justify-center border-b-[3px] border-transparent text-slate-500 dark:text-slate-400 hover:text-[#13a4ec] pb-3 pt-4 transition-colors">
              <p className="text-sm font-bold leading-normal tracking-wide">Preferences</p>
            </button>
            <button className="flex flex-col items-center justify-center border-b-[3px] border-transparent text-slate-500 dark:text-slate-400 hover:text-[#13a4ec] pb-3 pt-4 transition-colors">
              <p className="text-sm font-bold leading-normal tracking-wide">Variables</p>
            </button>
             <button className="flex flex-col items-center justify-center border-b-[3px] border-transparent text-slate-500 dark:text-slate-400 hover:text-[#13a4ec] pb-3 pt-4 transition-colors">
              <p className="text-sm font-bold leading-normal tracking-wide">Sets</p>
            </button>
          </div>
        </nav>

        {/* Scrollable Content */}
        <main className="flex-1 overflow-y-auto custom-scrollbar p-6 space-y-6 bg-[#f6f7f8] dark:bg-[#101c22]">
          
          {/* Section: Overview Summary */}
          <section className="bg-white dark:bg-[#0f172a] rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden">
            <div className="border-b border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/30 px-6 py-4">
              <h3 className="text-[#111618] dark:text-white text-lg font-bold">Problem Summary</h3>
            </div>
            <div className="p-6">
              <p className="text-slate-700 dark:text-slate-300 text-base leading-relaxed mb-6">
                 {data.description || "No description available for this problem."}
              </p>
              
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-y-6 gap-x-8 border-t border-slate-100 dark:border-slate-800 pt-6">
                <div className="flex flex-col gap-1">
                  <p className="text-slate-500 dark:text-slate-400 text-xs font-semibold uppercase tracking-wider">Problem ID</p>
                  <p className="text-[#111618] dark:text-white text-sm font-medium">{data.id}</p>
                </div>
                <div className="flex flex-col gap-1">
                  <p className="text-slate-500 dark:text-slate-400 text-xs font-semibold uppercase tracking-wider">Status</p>
                  <div className="flex items-center gap-2">
                    <span className={`w-2 h-2 rounded-full ${data.status === 'Solved' ? 'bg-emerald-500' : 'bg-amber-500'}`}></span>
                    <p className="text-[#111618] dark:text-white text-sm font-medium">{data.status}</p>
                  </div>
                </div>
                <div className="flex flex-col gap-1">
                  <p className="text-slate-500 dark:text-slate-400 text-xs font-semibold uppercase tracking-wider">Creation Date</p>
                  <p className="text-[#111618] dark:text-white text-sm font-medium">{data.lastEdited}</p>
                </div>
                <div className="flex flex-col gap-1">
                  <p className="text-slate-500 dark:text-slate-400 text-xs font-semibold uppercase tracking-wider">Complexity</p>
                  <p className="text-[#111618] dark:text-white text-sm font-medium">High (840 Variables)</p>
                </div>
              </div>
            </div>
          </section>

          {/* Section: Technical Metadata */}
          <section className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Optimization Model Card */}
            <div className="bg-white dark:bg-[#0f172a] rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
              <div className="flex items-center gap-2 mb-4">
                <span className="material-symbols-outlined text-[#13a4ec]">analytics</span>
                <h4 className="text-[#111618] dark:text-white font-bold">Optimization Model</h4>
              </div>
              <div className="space-y-4">
                <div className="flex justify-between items-center py-2 border-b border-slate-50 dark:border-slate-800">
                  <span className="text-slate-500 dark:text-slate-400 text-sm">Algorithm</span>
                  <span className="text-slate-900 dark:text-white text-sm font-medium">Constraint Programming</span>
                </div>
                <div className="flex justify-between items-center py-2 border-b border-slate-50 dark:border-slate-800">
                  <span className="text-slate-500 dark:text-slate-400 text-sm">Time Limit</span>
                  <span className="text-slate-900 dark:text-white text-sm font-medium">600 Seconds</span>
                </div>
                <div className="flex justify-between items-center py-2">
                  <span className="text-slate-500 dark:text-slate-400 text-sm">Target Objective</span>
                  <span className="text-slate-900 dark:text-white text-sm font-medium text-right leading-tight">Minimize Unassigned Shifts</span>
                </div>
              </div>
            </div>

            {/* Access & Sharing Card */}
            <div className="bg-white dark:bg-[#0f172a] rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
              <div className="flex items-center gap-2 mb-4">
                <span className="material-symbols-outlined text-[#13a4ec]">visibility</span>
                <h4 className="text-[#111618] dark:text-white font-bold">Access & Sharing</h4>
              </div>
              <div className="space-y-4">
                <div className="flex justify-between items-center py-2 border-b border-slate-50 dark:border-slate-800">
                  <span className="text-slate-500 dark:text-slate-400 text-sm">Visibility</span>
                  <span className="px-2 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 rounded text-xs font-bold uppercase tracking-tight">Private</span>
                </div>
                <div className="flex justify-between items-center py-2 border-b border-slate-50 dark:border-slate-800">
                  <span className="text-slate-500 dark:text-slate-400 text-sm">Last Modified</span>
                  <span className="text-slate-900 dark:text-white text-sm font-medium">{data.lastEdited}</span>
                </div>
                <div className="flex justify-between items-center py-2">
                  <span className="text-slate-500 dark:text-slate-400 text-sm">Owner</span>
                  <div className="flex items-center gap-2">
                    <div className="w-6 h-6 rounded-full bg-[#13a4ec]/20 flex items-center justify-center">
                      <span className="text-[10px] font-bold text-[#13a4ec]">ME</span>
                    </div>
                    <span className="text-slate-900 dark:text-white text-sm font-medium">Me</span>
                  </div>
                </div>
              </div>
            </div>
          </section>

          {/* Preview Data Section (Static from HTML) */}
          <section className="bg-white dark:bg-[#0f172a] rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm">
            <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-800 flex justify-between items-center">
              <h3 className="text-[#111618] dark:text-white text-lg font-bold">Constraint Preview</h3>
              <span className="text-xs text-slate-400">Showing first 3 of 12 total</span>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="bg-slate-50 dark:bg-slate-800/50 text-slate-500 dark:text-slate-400">
                  <tr>
                    <th className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px]">Constraint Name</th>
                    <th className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px]">Type</th>
                    <th className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px]">Weight</th>
                    <th className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px]">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                  <tr>
                    <td className="px-6 py-4 text-slate-900 dark:text-slate-200 font-medium">Maximum 5 Consecutive Days</td>
                    <td className="px-6 py-4 text-slate-600 dark:text-slate-400 italic">Hard Constraint</td>
                    <td className="px-6 py-4 text-slate-600 dark:text-slate-400">∞</td>
                    <td className="px-6 py-4">
                      <span className="text-emerald-600 dark:text-emerald-400 flex items-center gap-1 font-medium">
                        <span className="material-symbols-outlined text-sm">check_circle</span> Valid
                      </span>
                    </td>
                  </tr>
                  <tr>
                    <td className="px-6 py-4 text-slate-900 dark:text-slate-200 font-medium">Night Shift Recovery (24h)</td>
                    <td className="px-6 py-4 text-slate-600 dark:text-slate-400 italic">Hard Constraint</td>
                    <td className="px-6 py-4 text-slate-600 dark:text-slate-400">∞</td>
                    <td className="px-6 py-4">
                      <span className="text-emerald-600 dark:text-emerald-400 flex items-center gap-1 font-medium">
                        <span className="material-symbols-outlined text-sm">check_circle</span> Valid
                      </span>
                    </td>
                  </tr>
                  <tr>
                    <td className="px-6 py-4 text-slate-900 dark:text-slate-200 font-medium">Weekend Fairness Distribution</td>
                    <td className="px-6 py-4 text-slate-600 dark:text-slate-400 italic">Soft Preference</td>
                    <td className="px-6 py-4 text-slate-600 dark:text-slate-400">100.0</td>
                    <td className="px-6 py-4">
                      <span className="text-emerald-600 dark:text-emerald-400 flex items-center gap-1 font-medium">
                        <span className="material-symbols-outlined text-sm">check_circle</span> Valid
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        </main>

        {/* Fixed Footer Actions */}
        <footer className="bg-white dark:bg-[#0f172a] border-t border-slate-200 dark:border-slate-800 p-6 flex flex-wrap items-center justify-between gap-4 shrink-0">
          
          {/* Visibility Toggle (Visual Only) */}
          <div className="flex items-center gap-4">
            <div className="flex flex-col">
              <span className="text-xs font-bold text-slate-400 uppercase tracking-tighter">Visibility</span>
              <span className="text-sm font-medium text-slate-700 dark:text-slate-300">Private Mode</span>
            </div>
            <label className="relative inline-flex items-center cursor-pointer">
              <input className="sr-only peer" type="checkbox" />
              <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none rounded-full peer dark:bg-slate-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-[#13a4ec]"></div>
              <span className="ml-3 text-sm font-medium text-slate-700 dark:text-slate-300">Make Public</span>
            </label>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center gap-3">
            <button 
              onClick={onClose}
              className="px-6 py-2.5 rounded-lg text-sm font-bold text-slate-700 dark:text-slate-300 border border-slate-300 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
            >
              Close
            </button>
            <button 
              onClick={() => onEdit(data.id)}
              className="flex items-center gap-2 px-6 py-2.5 rounded-lg text-sm font-bold text-[#13a4ec] border border-[#13a4ec] hover:bg-[#13a4ec]/10 transition-colors"
            >
              <span className="material-symbols-outlined text-[18px]">edit</span>
              Edit Problem
            </button>
            <button 
              onClick={() => onSolve(data.id)}
              className="flex items-center gap-2 px-8 py-2.5 rounded-lg text-sm font-bold text-white bg-[#13a4ec] hover:bg-[#13a4ec]/90 shadow-lg shadow-[#13a4ec]/20 transition-all active:scale-95"
            >
              <span className="material-symbols-outlined text-[18px]">play_circle</span>
              Solve Problem
            </button>
          </div>
        </footer>
      </div>
    </div>
  );
};

export default MyProblemDetailsModal;