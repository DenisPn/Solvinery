import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';

// טיפוסים לנתונים
interface Employee {
  id: string;
  name: string;
  role: string;
  roleColor: string; // צבע התג של התפקיד
}

// נתונים דמה - עובדים
const employeesData: Employee[] = [
  { id: "EMP_001", name: "John Doe", role: "Shift Manager", roleColor: "bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-300" },
  { id: "EMP_002", name: "Ronnie Cohen", role: "Supervisor", roleColor: "bg-green-100 text-green-800 dark:bg-green-900/40 dark:text-green-300" },
  { id: "EMP_003", name: "Dana Levy", role: "General Staff", roleColor: "bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300" },
];

export default function EditImagePage() {
  const { id } = useParams();
  const [activeTab, setActiveTab] = useState('Sets');

  const tabs = [
    { name: 'Sets', icon: 'group_work' },
    { name: 'Variables', icon: 'data_object' },
    { name: 'Constraints', icon: 'gavel' },
    { name: 'Preferences', icon: 'tune' },
  ];

  return (
    <div className="min-h-screen bg-background-light dark:bg-background-dark text-text-main-light dark:text-white font-display flex flex-col">
      
      <main className="flex-1 flex flex-col max-w-[1400px] mx-auto w-full px-4 sm:px-6 lg:px-8 py-8">
        
        {/* Breadcrumbs */}
        <div className="flex flex-wrap gap-2 items-center text-sm mb-6">
          <Link to="/" className="text-text-secondary-light dark:text-gray-400 hover:text-primary transition-colors">Home</Link>
          <span className="material-symbols-outlined text-text-secondary-light dark:text-gray-400 text-base">chevron_right</span>
          <Link to="/" className="text-text-secondary-light dark:text-gray-400 hover:text-primary transition-colors">Scheduling Problems</Link>
          <span className="material-symbols-outlined text-text-secondary-light dark:text-gray-400 text-base">chevron_right</span>
          <span className="font-medium text-primary">Edit Problem #{id || '1234'}</span>
        </div>

        {/* Header Section */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8 bg-white dark:bg-surface-dark p-6 rounded-xl shadow-sm border border-border-light dark:border-gray-700">
          <div className="flex flex-col gap-2">
            <div className="flex items-center gap-3">
              <h1 className="text-2xl md:text-3xl font-black tracking-tight text-text-main-light dark:text-white">March 2024 Shifts</h1>
              <span className="px-2.5 py-0.5 rounded-full bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-200 text-xs font-semibold border border-yellow-200 dark:border-yellow-800">Draft</span>
            </div>
            <p className="text-text-secondary-light dark:text-gray-400 text-base">Manage constraints, variables, and settings to run the optimal solution.</p>
          </div>
          <div className="flex gap-3 w-full md:w-auto">
            <button className="flex-1 md:flex-none flex items-center justify-center gap-2 rounded-lg h-10 px-5 bg-white dark:bg-surface-dark border border-border-light dark:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-700 text-text-main-light dark:text-white text-sm font-bold transition-all shadow-sm">
              <span className="material-symbols-outlined text-lg">save</span>
              <span>Save Changes</span>
            </button>
            <button className="flex-1 md:flex-none flex items-center justify-center gap-2 rounded-lg h-10 px-5 bg-primary hover:bg-primary/90 text-white text-sm font-bold transition-all shadow-md shadow-primary/20">
              <span className="material-symbols-outlined text-lg">play_arrow</span>
              <span>Run Solution</span>
            </button>
          </div>
        </div>

        {/* Tabs Navigation */}
        <div className="mb-6">
          <nav aria-label="Tabs" className="flex gap-6 border-b border-border-light dark:border-gray-700 overflow-x-auto">
            {tabs.map((tab) => (
              <button
                key={tab.name}
                onClick={() => setActiveTab(tab.name)}
                className={`group flex items-center gap-2 border-b-[3px] pb-3 px-1 font-medium text-sm transition-all whitespace-nowrap ${
                  activeTab === tab.name
                    ? 'border-primary text-primary font-bold'
                    : 'border-transparent hover:border-gray-300 text-text-secondary-light dark:text-gray-400 hover:text-text-main-light dark:hover:text-white'
                }`}
              >
                <span className={`material-symbols-outlined ${activeTab === tab.name ? 'filled' : ''}`}>{tab.icon}</span>
                {tab.name}
              </button>
            ))}
          </nav>
        </div>

        {/* Main Grid Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          
          {/* Left Column (Main Content) */}
          <div className="lg:col-span-2 space-y-6">
            
            {activeTab === 'Sets' ? (
              <>
                {/* Header for Sets */}
                <div className="flex justify-between items-center pb-2">
                  <h2 className="text-xl font-bold flex items-center gap-2 text-text-main-light dark:text-white">
                    <span className="material-symbols-outlined text-primary">group_work</span>
                    Manage Sets
                  </h2>
                  <button className="flex items-center gap-1 text-sm font-semibold text-primary hover:text-primary/80 bg-primary/10 hover:bg-primary/20 px-3 py-1.5 rounded-lg transition-colors">
                    <span className="material-symbols-outlined text-lg">add</span>
                    Add New Set
                  </button>
                </div>

                {/* Employees Table Card */}
                <div className="bg-white dark:bg-surface-dark rounded-xl border border-border-light dark:border-gray-700 shadow-sm overflow-hidden group">
                  <div className="p-4 border-b border-border-light dark:border-gray-700 flex justify-between items-center bg-gray-50/50 dark:bg-[#202e36]">
                    <div className="flex items-center gap-3">
                      <div className="bg-blue-100 dark:bg-blue-900/40 p-2 rounded-lg text-blue-600 dark:text-blue-300">
                        <span className="material-symbols-outlined">groups</span>
                      </div>
                      <div>
                        <h3 className="font-bold text-text-main-light dark:text-white text-lg">Employees</h3>
                        <p className="text-xs text-text-secondary-light dark:text-gray-400">List of all employees available for shift scheduling</p>
                      </div>
                    </div>
                    <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button className="p-2 text-gray-500 hover:text-primary hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors" title="Edit Settings">
                        <span className="material-symbols-outlined text-xl">edit</span>
                      </button>
                      <button className="p-2 text-gray-500 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-full transition-colors" title="Delete">
                        <span className="material-symbols-outlined text-xl">delete</span>
                      </button>
                    </div>
                  </div>
                  
                  <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                      <thead className="text-xs text-text-secondary-light uppercase bg-[#f9fafb] dark:bg-[#152028] dark:text-gray-400">
                        <tr>
                          <th className="px-6 py-3 font-medium">ID</th>
                          <th className="px-6 py-3 font-medium">Name</th>
                          <th className="px-6 py-3 font-medium">Role</th>
                          <th className="px-6 py-3 font-medium text-right">Actions</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-border-light dark:divide-gray-700">
                        {employeesData.map((emp) => (
                          <tr key={emp.id} className="bg-white dark:bg-surface-dark hover:bg-gray-50 dark:hover:bg-[#202e36] transition-colors">
                            <td className="px-6 py-3 font-medium text-text-main-light dark:text-white">{emp.id}</td>
                            <td className="px-6 py-3">{emp.name}</td>
                            <td className="px-6 py-3">
                              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${emp.roleColor}`}>
                                {emp.role}
                              </span>
                            </td>
                            <td className="px-6 py-3 text-right">
                              <button className="text-gray-400 hover:text-primary transition-colors">
                                <span className="material-symbols-outlined text-lg">edit</span>
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                  
                  <div className="bg-gray-50 dark:bg-[#152028] p-3 border-t border-border-light dark:border-gray-700 flex justify-center">
                    <button className="text-xs font-bold text-text-secondary-light hover:text-primary uppercase tracking-wider flex items-center gap-1 transition-colors">
                      <span className="material-symbols-outlined text-sm">expand_more</span>
                      Show All (24 employees)
                    </button>
                  </div>
                </div>

                {/* Shifts Empty State Card */}
                <div className="bg-white dark:bg-surface-dark rounded-xl border border-border-light dark:border-gray-700 shadow-sm overflow-hidden group">
                  <div className="p-4 border-b border-border-light dark:border-gray-700 flex justify-between items-center bg-gray-50/50 dark:bg-[#202e36]">
                    <div className="flex items-center gap-3">
                      <div className="bg-purple-100 dark:bg-purple-900/40 p-2 rounded-lg text-purple-600 dark:text-purple-300">
                        <span className="material-symbols-outlined">schedule</span>
                      </div>
                      <div>
                        <h3 className="font-bold text-text-main-light dark:text-white text-lg">Shifts</h3>
                        <p className="text-xs text-text-secondary-light dark:text-gray-400">Required shift types</p>
                      </div>
                    </div>
                    <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button className="p-2 text-gray-500 hover:text-primary hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors" title="Edit Settings">
                        <span className="material-symbols-outlined text-xl">edit</span>
                      </button>
                      <button className="p-2 text-gray-500 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-full transition-colors" title="Delete">
                        <span className="material-symbols-outlined text-xl">delete</span>
                      </button>
                    </div>
                  </div>
                  
                  <div className="p-6 text-center text-text-secondary-light dark:text-gray-400 italic bg-white dark:bg-surface-dark">
                    <div className="flex flex-col items-center justify-center gap-3 py-4">
                      <div className="w-12 h-12 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center text-gray-400">
                        <span className="material-symbols-outlined text-2xl">list_alt</span>
                      </div>
                      <span>No items in this set yet.</span>
                      <button className="text-primary text-sm font-bold hover:underline">Add first item</button>
                    </div>
                  </div>
                </div>
              </>
            ) : (
              <div className="bg-white dark:bg-surface-dark rounded-xl border border-border-light dark:border-gray-700 p-10 text-center">
                <span className="material-symbols-outlined text-6xl text-gray-300 mb-4">construction</span>
                <h3 className="text-xl font-bold text-gray-600 dark:text-gray-300">Work in Progress</h3>
                <p className="text-gray-500">The <strong>{activeTab}</strong> editor is coming soon.</p>
              </div>
            )}
          </div>

          {/* Right Column (Sidebar) */}
          <div className="lg:col-span-1 space-y-6">
            
            {/* Problem Summary Widget */}
            <div className="bg-white dark:bg-surface-dark rounded-xl border border-border-light dark:border-gray-700 shadow-sm p-5">
              <h3 className="text-sm font-bold text-text-secondary-light uppercase tracking-wider mb-4">Problem Summary</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="size-8 rounded bg-primary/10 flex items-center justify-center text-primary">
                      <span className="material-symbols-outlined text-lg">group_work</span>
                    </div>
                    <span className="font-medium text-text-main-light dark:text-white">Sets</span>
                  </div>
                  <span className="font-bold text-lg">2</span>
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="size-8 rounded bg-primary/10 flex items-center justify-center text-primary">
                      <span className="material-symbols-outlined text-lg">data_object</span>
                    </div>
                    <span className="font-medium text-text-main-light dark:text-white">Variables</span>
                  </div>
                  <span className="font-bold text-lg">450</span>
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="size-8 rounded bg-primary/10 flex items-center justify-center text-primary">
                      <span className="material-symbols-outlined text-lg">gavel</span>
                    </div>
                    <span className="font-medium text-text-main-light dark:text-white">Constraints</span>
                  </div>
                  <span className="font-bold text-lg">12</span>
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="size-8 rounded bg-primary/10 flex items-center justify-center text-primary">
                      <span className="material-symbols-outlined text-lg">tune</span>
                    </div>
                    <span className="font-medium text-text-main-light dark:text-white">Preferences</span>
                  </div>
                  <span className="font-bold text-lg">5</span>
                </div>
              </div>
              
              <div className="mt-6 pt-6 border-t border-border-light dark:border-gray-700">
                <div className="bg-blue-50 dark:bg-blue-900/20 border border-blue-100 dark:border-blue-900/30 rounded-lg p-3 flex gap-3">
                  <span className="material-symbols-outlined text-blue-600 dark:text-blue-400">info</span>
                  <p className="text-xs text-blue-800 dark:text-blue-200 leading-relaxed">
                    Last change made 10 minutes ago by system.
                    <a className="underline font-semibold ml-1" href="#">Version history</a>
                  </p>
                </div>
              </div>
            </div>

            {/* Suggestions Widget */}
            <div className="bg-gradient-to-br from-indigo-50 to-white dark:from-[#1e2a35] dark:to-surface-dark rounded-xl border border-indigo-100 dark:border-gray-700 shadow-sm p-5 relative overflow-hidden">
              <div className="absolute -top-10 -left-10 w-32 h-32 bg-indigo-500/10 rounded-full blur-2xl"></div>
              <h3 className="text-sm font-bold text-indigo-900 dark:text-indigo-200 flex items-center gap-2 mb-3 relative z-10">
                <span className="material-symbols-outlined text-indigo-500">auto_awesome</span>
                Suggestions
              </h3>
              <p className="text-xs text-text-secondary-light dark:text-gray-400 mb-4 relative z-10">
                We detected missing minimum shift constraints for 3 employees. Would you like to add them automatically?
              </p>
              <button className="w-full py-2 bg-indigo-100 hover:bg-indigo-200 dark:bg-indigo-900/40 dark:hover:bg-indigo-900/60 text-indigo-700 dark:text-indigo-300 text-sm font-bold rounded-lg transition-colors relative z-10">
                Apply Suggestion
              </button>
            </div>

          </div>
        </div>
      </main>
    </div>
  );
}