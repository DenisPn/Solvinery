import React, { useEffect, useState } from 'react';
import type { MyImageCardProps } from '../MyImageCard';
import type { ImageDto } from '../../types/apiTypes';

interface MyProblemDetailsModalProps {
  isOpen: boolean;
  onClose: () => void;
  data: MyImageCardProps | null;
  imageDto: ImageDto | null;
  onEdit: (id: string | number) => void;
  onSolve: (id: string | number) => void;
  onDelete: (id: string | number) => void;
  onPublish: (id: string | number) => void;
}

const MyProblemDetailsModal: React.FC<MyProblemDetailsModalProps> = ({
  isOpen,
  onClose,
  data,
  imageDto,
  onEdit,
  onSolve,
  onDelete,
  onPublish,
}) => {
  const [activeTab, setActiveTab] = useState('overview');

  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      setActiveTab('overview');
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => { document.body.style.overflow = 'unset'; };
  }, [isOpen]);

  if (!isOpen || !data) return null;

  const tabs = [
    { id: 'overview', label: 'Overview' },
    { id: 'constraints', label: 'Constraints' },
    { id: 'preferences', label: 'Preferences' },
    { id: 'variables', label: 'Variables' },
    { id: 'sets', label: 'Sets' },
  ];

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 md:p-8 font-sans">
      <div className="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity" onClick={onClose}></div>

      <div className="relative z-10 w-full max-w-5xl h-[90vh] flex flex-col bg-[#f6f7f8] dark:bg-[#101c22] rounded-xl shadow-2xl overflow-hidden border border-slate-200 dark:border-slate-800">

        <style>{`
          .custom-scrollbar::-webkit-scrollbar { width: 6px; }
          .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
          .custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }
          .dark .custom-scrollbar::-webkit-scrollbar-thumb { background: #374151; }
        `}</style>

        {/* Header */}
        <header className="flex items-center justify-between px-6 py-4 bg-white dark:bg-[#0f172a] border-b border-slate-200 dark:border-slate-800 shrink-0">
          <div className="flex items-center gap-3">
            <span className="material-symbols-outlined text-[#13a4ec] text-3xl">settings_suggest</span>
            <h1 className="text-[#111618] dark:text-white text-2xl font-bold leading-tight">My Problem Details</h1>
          </div>
          <button onClick={onClose} className="flex items-center justify-center rounded-lg h-10 w-10 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-500 transition-colors">
            <span className="material-symbols-outlined">close</span>
          </button>
        </header>

        {/* Tabs */}
        <nav className="bg-white dark:bg-[#0f172a] border-b border-slate-200 dark:border-slate-800 shrink-0">
          <div className="flex px-6 gap-8 overflow-x-auto">
            {tabs.map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex flex-col items-center justify-center border-b-[3px] pb-3 pt-4 transition-colors ${activeTab === tab.id
                    ? 'border-[#13a4ec] text-[#13a4ec]'
                    : 'border-transparent text-slate-500 dark:text-slate-400 hover:text-[#13a4ec]'
                  }`}
              >
                <p className="text-sm font-bold leading-normal tracking-wide">{tab.label}</p>
              </button>
            ))}
          </div>
        </nav>

        {/* Content */}
        <main className="flex-1 overflow-y-auto custom-scrollbar p-6 space-y-6 bg-[#f6f7f8] dark:bg-[#101c22]">

          {/* Overview Tab */}
          {activeTab === 'overview' && (
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
                    <p className="text-[#111618] dark:text-white text-sm font-medium">
                      {imageDto?.variables?.length ? `${imageDto.variables.length} Variables` : 'N/A'}
                    </p>
                  </div>
                </div>
              </div>
            </section>
          )}

          {/* Constraints Tab */}
          {activeTab === 'constraints' && (
            <section className="bg-white dark:bg-[#0f172a] rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm">
              <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-800">
                <h3 className="text-[#111618] dark:text-white text-lg font-bold">Constraints</h3>
              </div>
              <div className="p-6">
                {imageDto?.constraintModules?.length ? (
                  <div className="space-y-4">
                    {imageDto.constraintModules.map((module, i) => (
                      <div key={i} className="border border-slate-200 dark:border-slate-700 rounded-lg p-4">
                        <div className="flex items-center justify-between mb-2">
                          <h4 className="font-bold text-[#111618] dark:text-white">{module.moduleName}</h4>
                          <span className={`px-2 py-0.5 rounded text-xs font-bold ${module.active ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'}`}>
                            {module.active ? 'Active' : 'Inactive'}
                          </span>
                        </div>
                        <p className="text-sm text-slate-500 mb-2">{module.description}</p>
                        <ul className="list-disc list-inside text-sm text-slate-600 dark:text-slate-400 space-y-1">
                          {module.constraints.map((c, j) => <li key={j}>{c}</li>)}
                        </ul>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-slate-500">No constraint data available.</p>
                )}
              </div>
            </section>
          )}

          {/* Preferences Tab */}
          {activeTab === 'preferences' && (
            <section className="bg-white dark:bg-[#0f172a] rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm">
              <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-800">
                <h3 className="text-[#111618] dark:text-white text-lg font-bold">Preferences</h3>
              </div>
              <div className="p-6">
                {imageDto?.preferenceModules?.length ? (
                  <div className="space-y-4">
                    {imageDto.preferenceModules.map((module, i) => (
                      <div key={i} className="border border-slate-200 dark:border-slate-700 rounded-lg p-4">
                        <div className="flex items-center justify-between mb-2">
                          <h4 className="font-bold text-[#111618] dark:text-white">{module.moduleName}</h4>
                          <span className="px-2 py-0.5 rounded text-xs font-bold bg-blue-100 text-blue-700">
                            Scalar: {module.scalar}
                          </span>
                        </div>
                        <p className="text-sm text-slate-500 mb-2">{module.description}</p>
                        <ul className="list-disc list-inside text-sm text-slate-600 dark:text-slate-400 space-y-1">
                          {module.preferences.map((p, j) => <li key={j}>{p}</li>)}
                        </ul>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-slate-500">No preference data available.</p>
                )}
              </div>
            </section>
          )}

          {/* Variables Tab */}
          {activeTab === 'variables' && (
            <section className="bg-white dark:bg-[#0f172a] rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm">
              <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-800">
                <h3 className="text-[#111618] dark:text-white text-lg font-bold">Variables</h3>
              </div>
              <div className="overflow-x-auto">
                {imageDto?.variables?.length ? (
                  <table className="w-full text-left text-sm">
                    <thead className="bg-slate-50 dark:bg-slate-800/50 text-slate-500 dark:text-slate-400">
                      <tr>
                        <th className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px]">Name</th>
                        <th className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px]">Structure</th>
                        <th className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px]">Alias</th>
                        <th className="px-6 py-3 font-semibold uppercase tracking-wider text-[11px]">Objective Alias</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                      {imageDto.variables.map((v, i) => (
                        <tr key={i}>
                          <td className="px-6 py-4 font-medium text-[#111618] dark:text-white">{v.identifier}</td>
                          <td className="px-6 py-4 text-slate-600 dark:text-slate-400">{v.structure.join(', ')}</td>
                          <td className="px-6 py-4 text-slate-600 dark:text-slate-400">{v.alias || '-'}</td>
                          <td className="px-6 py-4 text-slate-600 dark:text-slate-400">{v.objectiveValueAlias || '-'}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                ) : (
                  <p className="p-6 text-slate-500">No variable data available.</p>
                )}
              </div>
            </section>
          )}

          {/* Sets Tab */}
          {activeTab === 'sets' && (
            <section className="bg-white dark:bg-[#0f172a] rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm">
              <div className="px-6 py-4 border-b border-slate-100 dark:border-slate-800">
                <h3 className="text-[#111618] dark:text-white text-lg font-bold">Sets</h3>
              </div>
              <div className="p-6">
                {imageDto?.sets?.length ? (
                  <div className="space-y-6">
                    {imageDto.sets.map((set, i) => {
                      const parseValue = (v: string): string[] => {
                        const inner = v.replace(/^<|>$/g, '');
                        return inner.split(',').map(s => s.trim().replace(/^"|"$/g, ''));
                      };
                      const parsedValues = set.values.map(parseValue);
                      const numCols = parsedValues[0]?.length ?? 1;
                      const structure = set.setDefinition.structure;

                      return (
                        <div key={i} className="border border-slate-200 dark:border-slate-700 rounded-lg overflow-hidden">
                          <div className="px-4 py-3 bg-slate-50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-700 flex items-center justify-between">
                            <h4 className="font-bold text-[#111618] dark:text-white">{set.setDefinition.name}</h4>
                            <span className="text-xs text-slate-400">{set.values.length} values</span>
                          </div>
                          <div className="overflow-x-auto">
                            <table className="w-full text-left text-sm">
                              <thead className="bg-slate-50 dark:bg-slate-800/50 text-slate-500 dark:text-slate-400">
                                <tr>
                                  <th className="px-4 py-2 font-semibold uppercase tracking-wider text-[11px]">#</th>
                                  {Array.from({ length: numCols }).map((_, ci) => (
                                    <th key={ci} className="px-4 py-2 font-semibold uppercase tracking-wider text-[11px]">
                                      {structure[ci] ?? `Col ${ci + 1}`}
                                    </th>
                                  ))}
                                </tr>
                              </thead>
                              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                                {parsedValues.map((row, j) => (
                                  <tr key={j} className="hover:bg-slate-50 dark:hover:bg-slate-800/30">
                                    <td className="px-4 py-2 text-slate-400 text-sm">{j + 1}</td>
                                    {row.map((cell, ci) => (
                                      <td key={ci} className="px-4 py-2 text-slate-700 dark:text-slate-300 text-sm">{cell}</td>
                                    ))}
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                ) : (
                  <p className="text-slate-500">No set data available.</p>
                )}
              </div>
            </section>
          )}

        </main>

        {/* Footer */}
        <footer className="bg-white dark:bg-[#0f172a] border-t border-slate-200 dark:border-slate-800 p-6 flex flex-wrap items-center justify-between gap-4 shrink-0">
          <div className="flex items-center gap-3">
            <button
              onClick={() => {
                if (window.confirm('Publish this problem to the community?')) {
                  onPublish(data.id);
                }
              }}
              className="flex items-center gap-2 px-6 py-2.5 rounded-lg text-sm font-bold text-emerald-600 border border-emerald-300 hover:bg-emerald-50 transition-colors"
            >
              <span className="material-symbols-outlined text-[18px]">public</span>
              Make Public
            </button>
          </div>
          <div className="flex items-center gap-3">
            <button onClick={onClose} className="px-6 py-2.5 rounded-lg text-sm font-bold text-slate-700 dark:text-slate-300 border border-slate-300 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors">
              Close
            </button>
            <button
              onClick={() => {
                if (window.confirm('Are you sure you want to delete this problem?')) {
                  onDelete(data.id);
                }
              }}
              className="flex items-center gap-2 px-6 py-2.5 rounded-lg text-sm font-bold text-red-500 border border-red-300 hover:bg-red-50 transition-colors"
            >
              <span className="material-symbols-outlined text-[18px]">delete</span>
              Delete
            </button>
            <button onClick={() => onEdit(data.id)} className="flex items-center gap-2 px-6 py-2.5 rounded-lg text-sm font-bold text-[#13a4ec] border border-[#13a4ec] hover:bg-[#13a4ec]/10 transition-colors">
              <span className="material-symbols-outlined text-[18px]">edit</span>
              Edit Problem
            </button>
            <button onClick={() => onSolve(data.id)} className="flex items-center gap-2 px-8 py-2.5 rounded-lg text-sm font-bold text-white bg-[#13a4ec] hover:bg-[#13a4ec]/90 shadow-lg shadow-[#13a4ec]/20 transition-all active:scale-95">
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