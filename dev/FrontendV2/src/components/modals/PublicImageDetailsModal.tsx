import React, { useEffect } from 'react';

interface PublicImageDetailsModalProps {
  isOpen: boolean;
  onClose: () => void;
  onClone: (id: string) => void; 
  isCloning: boolean;
  data: {
    id: string;
    title: string;
    author: string;
    description: string;
    date: string;
  } | null;
}

const PublicImageDetailsModal: React.FC<PublicImageDetailsModalProps> = ({ 
  isOpen, 
  onClose, 
  onClone,
  isCloning,
  data 
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
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity" 
        onClick={onClose}
      ></div>

      {/* Modal Container */}
      <div className="relative z-10 w-full max-w-5xl bg-[#f6f7f8] dark:bg-[#101c22] rounded-xl shadow-2xl flex flex-col h-[85vh] overflow-hidden border border-slate-200 dark:border-slate-800 animate-in fade-in zoom-in-95 duration-200">
        
        {/* CSS for custom scrollbar */}
        <style>{`
          .custom-scrollbar::-webkit-scrollbar { width: 8px; }
          .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
          .custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
          .dark .custom-scrollbar::-webkit-scrollbar-thumb { background: #334155; }
          .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
        `}</style>

        {/* Header Section */}
        <div className="flex-none bg-[#f6f7f8] dark:bg-[#101c22] z-20">
          <div className="flex items-start justify-between p-6 border-b border-slate-200 dark:border-slate-800">
            <div className="flex flex-col gap-1">
              <div className="flex items-center gap-3">
                <span className="material-symbols-outlined text-[#13a4ec] text-3xl">account_tree</span>
                <div className="flex flex-col">
                  <h1 className="text-[#111618] dark:text-white text-2xl font-black leading-tight tracking-tight">
                    {data.title}
                  </h1>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="text-[#617c89] dark:text-slate-400 text-xs font-medium">
                      Created by {data.author}
                    </span>
                    <span className="bg-[#13a4ec]/10 text-[#13a4ec] text-[10px] font-bold uppercase tracking-widest px-2 py-0.5 rounded flex items-center gap-1">
                      <span className="material-symbols-outlined text-[12px]">verified</span> Verified Expert
                    </span>
                  </div>
                </div>
              </div>
            </div>
            <button 
              onClick={onClose}
              className="flex h-10 w-10 items-center justify-center rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors cursor-pointer"
            >
              <span className="material-symbols-outlined">close</span>
            </button>
          </div>

          {/* Navigation Tabs */}
          <div className="bg-white dark:bg-[#101c22]/50 border-b border-slate-200 dark:border-slate-800 px-6">
            <div className="flex gap-8 overflow-x-auto no-scrollbar">
              <button className="flex flex-col items-center justify-center border-b-[3px] border-[#13a4ec] text-[#13a4ec] pb-3 pt-4 px-1">
                <p className="text-sm font-bold leading-normal tracking-wide">Overview</p>
              </button>
              <button className="flex flex-col items-center justify-center border-b-[3px] border-transparent text-[#617c89] dark:text-slate-400 pb-3 pt-4 px-1 hover:text-[#13a4ec] transition-colors">
                <p className="text-sm font-bold leading-normal tracking-wide">Constraints</p>
              </button>
              <button className="flex flex-col items-center justify-center border-b-[3px] border-transparent text-[#617c89] dark:text-slate-400 pb-3 pt-4 px-1 hover:text-[#13a4ec] transition-colors">
                <p className="text-sm font-bold leading-normal tracking-wide">Preferences</p>
              </button>
              <button className="flex flex-col items-center justify-center border-b-[3px] border-transparent text-[#617c89] dark:text-slate-400 pb-3 pt-4 px-1 hover:text-[#13a4ec] transition-colors">
                <p className="text-sm font-bold leading-normal tracking-wide">Variables</p>
              </button>
            </div>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto p-6 space-y-8 custom-scrollbar bg-slate-50/50 dark:bg-transparent">
          
          {/* Description Section */}
          <div className="space-y-4">
            <div className="flex items-center gap-2 px-2">
              <span className="material-symbols-outlined text-[#13a4ec]">description</span>
              <h3 className="text-[#111618] dark:text-white text-xl font-bold leading-tight">Problem Description</h3>
            </div>
            <div className="bg-white dark:bg-slate-800/20 p-6 rounded-xl border border-slate-200 dark:border-slate-800">
              <p className="text-[#111618] dark:text-slate-300 text-base font-normal leading-relaxed">
                {data.description || "No description available."}
              </p>
              <div className="mt-4 pt-4 border-t border-slate-100 dark:border-slate-800 flex gap-6">
                <div className="flex items-center gap-2 text-[#617c89] dark:text-slate-400 text-xs">
                  <span className="material-symbols-outlined text-sm">calendar_today</span>
                  <span>Published {data.date}</span>
                </div>
                <div className="flex items-center gap-2 text-[#617c89] dark:text-slate-400 text-xs">
                  <span className="material-symbols-outlined text-sm">category</span>
                  <span>Healthcare Scheduling</span>
                </div>
              </div>
            </div>
          </div>

          {/* Stats & Tags Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="flex flex-col gap-3">
              <h4 className="text-sm font-bold text-slate-400 uppercase tracking-widest px-2">System Stats</h4>
              <div className="grid grid-cols-2 gap-3">
                <div className="bg-white dark:bg-slate-800/40 p-4 rounded-lg border border-slate-200 dark:border-slate-700 flex flex-col">
                  <span className="text-2xl font-black text-[#13a4ec]">24</span>
                  <span className="text-xs font-medium text-slate-500 dark:text-slate-400">Total Constraints</span>
                </div>
                <div className="bg-white dark:bg-slate-800/40 p-4 rounded-lg border border-slate-200 dark:border-slate-700 flex flex-col">
                  <span className="text-2xl font-black text-[#13a4ec]">156</span>
                  <span className="text-xs font-medium text-slate-500 dark:text-slate-400">Variables</span>
                </div>
              </div>
            </div>
            <div className="flex flex-col gap-3">
              <h4 className="text-sm font-bold text-slate-400 uppercase tracking-widest px-2">Tags</h4>
              <div className="flex flex-wrap gap-2 pt-2">
                {["Nurse Rostering", "Optimization", "24/7 Operations", "Heuristics"].map((tag) => (
                   <span key={tag} className="px-3 py-1 bg-white dark:bg-slate-800/40 text-slate-600 dark:text-slate-300 text-xs font-bold rounded-full border border-slate-200 dark:border-slate-700">
                     {tag}
                   </span>
                ))}
              </div>
            </div>
          </div>

          {/* Constraints Section (Static) */}
          <div className="space-y-4">
            <div className="flex items-center justify-between px-2">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-[#13a4ec]">gavel</span>
                <h3 className="text-[#111618] dark:text-white text-xl font-bold">Structural Constraints</h3>
              </div>
            </div>
            <div className="space-y-3">
              <div className="flex items-start gap-4 p-4 bg-white dark:bg-slate-800/40 rounded-lg border border-slate-200 dark:border-slate-700">
                <span className="material-symbols-outlined text-amber-500 mt-0.5">priority_high</span>
                <div className="flex-1">
                  <p className="text-sm font-bold text-[#111618] dark:text-white">Maximum Consecutive Days</p>
                  <p className="text-xs text-slate-500 mt-1">Safety regulation: No staff member can work more than 6 consecutive days without a full 24-hour rest period.</p>
                </div>
                <span className="px-2 py-0.5 bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400 text-[10px] font-bold rounded whitespace-nowrap">HARD CONSTRAINT</span>
              </div>
              
              <div className="flex items-start gap-4 p-4 bg-white dark:bg-slate-800/40 rounded-lg border border-slate-200 dark:border-slate-700">
                <span className="material-symbols-outlined text-blue-500 mt-0.5">info</span>
                <div className="flex-1">
                  <p className="text-sm font-bold text-[#111618] dark:text-white">Minimum Rest Period</p>
                  <p className="text-xs text-slate-500 mt-1">Mandatory 11-hour rest between shifts for all employees.</p>
                </div>
                <span className="px-2 py-0.5 bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400 text-[10px] font-bold rounded whitespace-nowrap">HARD CONSTRAINT</span>
              </div>
            </div>
          </div>

          {/* Preferences Section (Static) */}
          <div className="space-y-4">
             <div className="flex items-center gap-2 px-2">
                <span className="material-symbols-outlined text-[#13a4ec]">star</span>
                <h3 className="text-[#111618] dark:text-white text-xl font-bold">Optimization Preferences</h3>
             </div>
             <div className="space-y-3 pb-4">
                <div className="flex items-start gap-4 p-4 bg-white dark:bg-slate-800/40 rounded-lg border border-slate-200 dark:border-slate-700">
                   <span className="material-symbols-outlined text-emerald-500 mt-0.5">trending_down</span>
                   <div className="flex-1">
                      <p className="text-sm font-bold text-[#111618] dark:text-white">Minimize Agency Staffing</p>
                      <p className="text-xs text-slate-500 mt-1">Heavy weight on avoiding external contractors to reduce operational costs.</p>
                   </div>
                   <span className="px-2 py-0.5 bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 dark:text-emerald-400 text-[10px] font-bold rounded whitespace-nowrap">SOFT</span>
                </div>
             </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="flex-none p-6 border-t border-slate-200 dark:border-slate-800 bg-white dark:bg-[#101c22] flex flex-col-reverse sm:flex-row items-center justify-end gap-3 z-20">
          <button 
            onClick={onClose}
            className="w-full sm:w-auto flex min-w-[100px] cursor-pointer items-center justify-center overflow-hidden rounded-lg h-12 px-6 bg-slate-100 dark:bg-slate-800 text-[#111618] dark:text-white text-sm font-bold leading-normal tracking-wide hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
          >
            <span className="truncate">Close</span>
          </button>

          {/* Clone Button */}
          <button 
            onClick={() => onClone(data.id)}
            disabled={isCloning}
            className={`w-full sm:w-auto flex min-w-[200px] cursor-pointer items-center justify-center gap-2 overflow-hidden rounded-lg h-12 px-6 text-white text-sm font-bold leading-normal tracking-wide transition-all shadow-lg shadow-[#13a4ec]/20 
              ${isCloning ? 'bg-gray-400 cursor-not-allowed' : 'bg-[#13a4ec] hover:opacity-90'}`}
          >
            {isCloning ? (
               <>
                 <span className="material-symbols-outlined text-lg animate-spin">progress_activity</span>
                 <span className="truncate">Cloning...</span>
               </>
            ) : (
               <>
                 <span className="material-symbols-outlined text-lg">content_copy</span>
                 <span className="truncate">Clone to My Problems</span>
               </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default PublicImageDetailsModal;