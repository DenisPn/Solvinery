import React from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import type { SolverResponse } from '../types/apiTypes';

interface LocationState {
  solutionData: SolverResponse;
  problemTitle: string;
}

const SolutionPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  
  // שליפת המידע שהועבר מהדף הקודם
  const state = location.state as LocationState;
  const { solutionData, problemTitle } = state || {};

  // אם הגענו לדף בטעות בלי מידע (למשל רענון הדף), נחזור אחורה או נציג שגיאה
  if (!solutionData) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen">
        <h2 className="text-xl font-bold mb-4">No solution data found.</h2>
        <button onClick={() => navigate('/myimages')} className="text-blue-500 underline">Back to My Problems</button>
      </div>
    );
  }

  return (
    <div className="bg-background-light dark:bg-background-dark font-sans text-[#111618] dark:text-white min-h-screen flex flex-col">
      <style>{`
        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }
        .custom-scrollbar::-webkit-scrollbar { width: 8px; height: 8px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: #f1f1f1; }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 4px; }
        .dark .custom-scrollbar::-webkit-scrollbar-track { background: #1f2937; }
        .dark .custom-scrollbar::-webkit-scrollbar-thumb { background: #4b5563; }
      `}</style>

      <main className="flex-1 px-4 sm:px-10 py-8 max-w-[1440px] mx-auto w-full">
        
        {/* Breadcrumbs */}
        <div className="flex flex-wrap gap-2 mb-4">
          <button onClick={() => navigate('/myimages')} className="text-[#617c89] text-sm font-medium leading-normal hover:text-primary">My Problems</button>
          <span className="text-[#617c89] text-sm font-medium leading-normal">/</span>
          <span className="text-[#111618] dark:text-white text-sm font-medium leading-normal">Solution Results</span>
        </div>

        {/* Page Heading - Dynamic Data */}
        <div className="flex flex-wrap justify-between items-end gap-3 mb-8">
          <div className="flex min-w-72 flex-col gap-2">
            <div className="flex items-center gap-3">
              <h1 className="text-[#111618] dark:text-white text-4xl font-black leading-tight tracking-[-0.033em]">
                {problemTitle}
              </h1>
              {solutionData.solved ? (
                <span className="bg-[#e7f6ed] text-[#078836] px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">Solved Successfully</span>
              ) : (
                <span className="bg-red-100 text-red-600 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">Unsolved</span>
              )}
            </div>
            <p className="text-[#617c89] text-base font-normal leading-normal">
              Problem ID: {id} • Time taken: {solutionData.solvingTime}ms
            </p>
          </div>
          <div className="flex gap-3">
            <button onClick={() => navigate(`/problems/${id}`)} className="flex min-w-[84px] cursor-pointer items-center justify-center rounded-lg h-10 px-4 bg-[#f0f3f4] dark:bg-gray-700 text-[#111618] dark:text-white text-sm font-bold leading-normal hover:bg-[#e0e3e4] transition-all">
              <span className="truncate text-primary">Back to Problem Details</span>
            </button>
          </div>
        </div>

        {/* Stats Cards - Dynamic Data */}
        <div className="flex flex-wrap gap-4 mb-8">
          {/* Optimization Score (Objective Value) */}
          <div className="flex min-w-[200px] flex-1 flex-col gap-2 rounded-xl p-6 bg-white dark:bg-gray-800 border border-[#dbe2e6] dark:border-gray-700 shadow-sm">
            <div className="flex justify-between items-start">
              <p className="text-[#617c89] text-sm font-medium uppercase tracking-wider">Objective Value</p>
              <span className="material-symbols-outlined text-primary">analytics</span>
            </div>
            <div className="flex items-baseline gap-2">
              <p className="text-[#111618] dark:text-white tracking-tight text-3xl font-bold">
                {solutionData.objectiveValue.toFixed(2)}
              </p>
            </div>
            <div className="w-full bg-[#f0f3f4] dark:bg-gray-700 h-1.5 rounded-full mt-2">
              <div className="bg-primary h-1.5 rounded-full" style={{ width: '100%' }}></div>
            </div>
          </div>
          
          {/* Status Card */}
          <div className="flex min-w-[200px] flex-1 flex-col gap-2 rounded-xl p-6 bg-white dark:bg-gray-800 border border-[#dbe2e6] dark:border-gray-700 shadow-sm">
            <div className="flex justify-between items-start">
              <p className="text-[#617c89] text-sm font-medium uppercase tracking-wider">Solver Status</p>
              <span className="material-symbols-outlined text-primary">task_alt</span>
            </div>
            <p className="text-[#111618] dark:text-white tracking-tight text-3xl font-bold">
              {solutionData.solved ? "Optimal" : "Failed"}
            </p>
            <p className="text-[#617c89] text-sm font-medium">Solver execution complete</p>
          </div>
        </div>

        {/* JSON View (Temporary until we have a real table mapping) */}
        <div className="bg-white dark:bg-gray-800 rounded-xl border border-[#dbe2e6] dark:border-gray-700 shadow-md overflow-hidden flex flex-col p-6">
            <h3 className="text-lg font-bold mb-4">Raw Solution Data</h3>
            <div className="bg-gray-50 dark:bg-gray-900 p-4 rounded-lg overflow-auto max-h-[500px] custom-scrollbar">
                <pre className="text-sm font-mono text-gray-700 dark:text-gray-300">
                    {JSON.stringify(solutionData, null, 2)}
                </pre>
            </div>
        </div>

      </main>
    </div>
  );
};

export default SolutionPage;