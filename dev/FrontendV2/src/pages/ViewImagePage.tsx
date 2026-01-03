import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';

// הגדרת טיפוסים לנתונים
interface StatCard {
  title: string;
  value: string;
  subtext: string;
  icon: string;
  colorBg: string;
  colorText: string;
}

interface Constraint {
  id: string;
  name: string;
  type: 'Hard' | 'Soft';
  description: string;
  tag: string;
  weight?: number; // אופציונלי, רק עבור Soft
}

// נתונים דמה (סטטיסטיקות)
const stats: StatCard[] = [
  { title: "Constraints", value: "124", subtext: "112 Hard, 12 Soft", icon: "rule", colorBg: "bg-orange-50 hover:bg-orange-100", colorText: "text-orange-600" },
  { title: "Preferences", value: "15", subtext: "Weighted objectives", icon: "tune", colorBg: "bg-purple-50 hover:bg-purple-100", colorText: "text-purple-600" },
  { title: "Variables", value: "42", subtext: "Assignable entities", icon: "group", colorBg: "bg-blue-50 hover:bg-blue-100", colorText: "text-blue-600" },
  { title: "Sets", value: "8", subtext: "Defined groups", icon: "category", colorBg: "bg-emerald-50 hover:bg-emerald-100", colorText: "text-emerald-600" },
];

// נתונים דמה (טבלת אילוצים)
const constraintsData: Constraint[] = [
  { id: "#C-1024", name: "Max Shift Length", type: "Hard", description: "Employees cannot be scheduled for more than 8 hours continuously.", tag: "Legal" },
  { id: "#C-1025", name: "No Back-to-Back Nights", type: "Hard", description: "Night shifts must be followed by at least 24 hours off.", tag: "Health" },
  { id: "#P-2001", name: "Minimize Overtime", type: "Soft", weight: 10, description: "Prefer solutions with total overtime < 5%.", tag: "Cost" },
  { id: "#C-1044", name: "Certification Required", type: "Hard", description: "Only certified nurses can handle ICU shifts.", tag: "Skill" },
];

export default function ViewImagePage() {
  const { id } = useParams(); // שליפת המזהה מה-URL
  const [activeTab, setActiveTab] = useState('Constraints');

  const tabs = ['Constraints', 'Preferences', 'Variables', 'Sets', 'Results & Analysis'];

  return (
    <div className="min-h-screen bg-background-light dark:bg-background-dark text-text-main-light dark:text-white font-display flex flex-col">
      
      <main className="flex-grow">
        <div className="max-w-[1200px] mx-auto px-4 sm:px-6 lg:px-8 py-8">
          
          {/* Breadcrumbs */}
          <nav aria-label="Breadcrumb" className="flex mb-6">
            <ol className="flex items-center space-x-2">
              <li><Link to="/" className="text-gray-500 hover:text-primary text-sm font-medium">Home</Link></li>
              <li><span className="text-gray-300">/</span></li>
              <li><Link to="/" className="text-gray-500 hover:text-primary text-sm font-medium">Problems</Link></li>
              <li><span className="text-gray-300">/</span></li>
              <li aria-current="page" className="text-text-main-light dark:text-white text-sm font-medium">Problem #{id}</li>
            </ol>
          </nav>

          {/* Page Header */}
          <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-6 mb-8">
            <div className="flex-1">
              <div className="flex items-center gap-3 mb-2">
                <h2 className="text-3xl md:text-4xl font-black text-text-main-light dark:text-white tracking-tight">Shift Schedule - Oct 2023</h2>
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 border border-green-200">
                  Optimal Found
                </span>
              </div>
              <div className="flex flex-wrap gap-4 text-sm text-gray-500 dark:text-gray-400">
                <span className="flex items-center gap-1">
                  <span className="material-symbols-outlined text-[18px]">calendar_today</span>
                  Created Oct 10, 2023
                </span>
                <span className="flex items-center gap-1">
                  <span className="material-symbols-outlined text-[18px]">fingerprint</span>
                  ID: #{id || '8821'}
                </span>
                <span className="flex items-center gap-1">
                  <span className="material-symbols-outlined text-[18px]">schedule</span>
                  Last run: 2 hours ago
                </span>
              </div>
            </div>
            
            <div className="flex items-center gap-3">
              <Link to={`/image/${id}/edit`}>
                <button className="flex items-center justify-center gap-2 px-4 py-2 bg-white dark:bg-surface-dark border border-gray-300 dark:border-gray-600 rounded-lg text-sm font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-700 transition-all shadow-sm">
                  <span className="material-symbols-outlined text-[20px]">edit</span>
                  Edit Definition
                </button>
              </Link>
              
              {/* כפתור לפתרון - מוביל לדף SolutionViewPage */}
              <Link to={`/solution/${id}`}>
                <button className="flex items-center justify-center gap-2 px-6 py-2 bg-primary hover:bg-sky-600 rounded-lg text-sm font-bold text-white shadow-md shadow-primary/30 transition-all transform hover:scale-[1.02]">
                  <span className="material-symbols-outlined text-[20px]">play_arrow</span>
                  Re-Run Solver
                </button>
              </Link>
            </div>
          </div>

          {/* Stats Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
            {stats.map((stat) => (
              <div key={stat.title} className="bg-white dark:bg-surface-dark p-5 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex flex-col justify-between group hover:border-primary/50 transition-colors">
                <div className="flex justify-between items-start mb-2">
                  <p className="text-gray-500 dark:text-gray-400 text-sm font-medium">{stat.title}</p>
                  <div className={`p-2 rounded-lg transition-colors ${stat.colorBg} ${stat.colorText}`}>
                    <span className="material-symbols-outlined text-[20px]">{stat.icon}</span>
                  </div>
                </div>
                <div>
                  <p className="text-3xl font-bold text-text-main-light dark:text-white">{stat.value}</p>
                  <p className="text-xs text-gray-400 mt-1">{stat.subtext}</p>
                </div>
              </div>
            ))}
          </div>

          {/* Content Area with Tabs */}
          <div className="bg-white dark:bg-surface-dark rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm overflow-hidden min-h-[600px] flex flex-col">
            
            {/* Tabs Header */}
            <div className="border-b border-gray-200 dark:border-gray-700 px-6">
              <nav aria-label="Tabs" className="-mb-px flex gap-8 overflow-x-auto">
                {tabs.map((tab) => (
                  <button
                    key={tab}
                    onClick={() => setActiveTab(tab)}
                    className={`whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm flex items-center gap-2 transition-colors ${
                      activeTab === tab
                        ? 'border-primary text-primary font-semibold'
                        : 'border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 hover:border-gray-300'
                    }`}
                  >
                    {tab}
                  </button>
                ))}
              </nav>
            </div>

            {/* Tab Content */}
            <div className="p-6">
              {activeTab === 'Constraints' ? (
                <>
                  {/* Toolbar */}
                  <div className="flex flex-col sm:flex-row justify-between items-center gap-4 mb-6">
                    <div className="relative w-full sm:w-96">
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <span className="material-symbols-outlined text-gray-400">search</span>
                      </div>
                      <input 
                        className="block w-full pl-10 pr-3 py-2 border border-gray-200 dark:border-gray-600 rounded-lg text-sm bg-gray-50 dark:bg-gray-800 text-text-main-light dark:text-white focus:bg-white dark:focus:bg-surface-dark focus:ring-1 focus:ring-primary focus:border-primary transition-colors" 
                        placeholder="Filter constraints..." 
                        type="text"
                      />
                    </div>
                    <div className="flex items-center gap-2 w-full sm:w-auto">
                      <button className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-gray-600 dark:text-gray-300 bg-white dark:bg-surface-dark border border-gray-200 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700">
                        <span className="material-symbols-outlined text-[18px]">filter_list</span>
                        Filter
                      </button>
                      <button className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-white bg-primary rounded-lg hover:bg-sky-600">
                        <span className="material-symbols-outlined text-[18px]">add</span>
                        Add Constraint
                      </button>
                    </div>
                  </div>

                  {/* Constraints Table */}
                  <div className="overflow-x-auto rounded-lg border border-gray-200 dark:border-gray-700">
                    <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                      <thead className="bg-gray-50 dark:bg-gray-800">
                        <tr>
                          <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Name</th>
                          <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Type</th>
                          <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Description</th>
                          <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Tags</th>
                          <th className="px-6 py-3 text-right text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Actions</th>
                        </tr>
                      </thead>
                      <tbody className="bg-white dark:bg-surface-dark divide-y divide-gray-200 dark:divide-gray-700">
                        {constraintsData.map((row) => (
                          <tr key={row.id} className="hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors group">
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="flex items-center">
                                <div className={`flex-shrink-0 h-10 w-10 flex items-center justify-center rounded-lg ${row.type === 'Hard' ? 'bg-red-50 text-red-600' : 'bg-yellow-50 text-yellow-600'}`}>
                                  <span className="material-symbols-outlined">{row.type === 'Hard' ? 'gavel' : 'balance'}</span>
                                </div>
                                <div className="ml-4">
                                  <div className="text-sm font-semibold text-text-main-light dark:text-white">{row.name}</div>
                                  <div className="text-xs text-gray-500">{row.id}</div>
                                </div>
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full border ${row.type === 'Hard' ? 'bg-red-100 text-red-800 border-red-200' : 'bg-yellow-100 text-yellow-800 border-yellow-200'}`}>
                                {row.type} {row.weight ? `(W: ${row.weight})` : ''}
                              </span>
                            </td>
                            <td className="px-6 py-4">
                              <div className="text-sm text-gray-900 dark:text-gray-300 max-w-xs truncate">{row.description}</div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-300">
                                {row.tag}
                              </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                              <button className="text-gray-400 hover:text-primary transition-colors">
                                <span className="material-symbols-outlined">more_vert</span>
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {/* Pagination */}
                  <div className="flex items-center justify-between border-t border-gray-200 dark:border-gray-700 px-4 py-3 sm:px-6 mt-4">
                    <div className="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
                      <div>
                        <p className="text-sm text-gray-700 dark:text-gray-400">
                          Showing <span className="font-medium">1</span> to <span className="font-medium">{constraintsData.length}</span> of <span className="font-medium">124</span> results
                        </p>
                      </div>
                      <div>
                        <nav aria-label="Pagination" className="isolate inline-flex -space-x-px rounded-md shadow-sm">
                          <button className="relative inline-flex items-center rounded-l-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 dark:ring-gray-600 hover:bg-gray-50 dark:hover:bg-gray-700 focus:z-20 focus:outline-offset-0">
                            <span className="sr-only">Previous</span>
                            <span className="material-symbols-outlined text-[20px]">chevron_left</span>
                          </button>
                          <button aria-current="page" className="relative z-10 inline-flex items-center bg-primary px-4 py-2 text-sm font-semibold text-white focus:z-20 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary">1</button>
                          <button className="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-gray-900 dark:text-gray-300 ring-1 ring-inset ring-gray-300 dark:ring-gray-600 hover:bg-gray-50 dark:hover:bg-gray-700 focus:z-20 focus:outline-offset-0">2</button>
                          <button className="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-gray-900 dark:text-gray-300 ring-1 ring-inset ring-gray-300 dark:ring-gray-600 hover:bg-gray-50 dark:hover:bg-gray-700 focus:z-20 focus:outline-offset-0">3</button>
                          <button className="relative inline-flex items-center rounded-r-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 dark:ring-gray-600 hover:bg-gray-50 dark:hover:bg-gray-700 focus:z-20 focus:outline-offset-0">
                            <span className="sr-only">Next</span>
                            <span className="material-symbols-outlined text-[20px]">chevron_right</span>
                          </button>
                        </nav>
                      </div>
                    </div>
                  </div>
                </>
              ) : (
                <div className="flex flex-col items-center justify-center py-20 text-gray-500">
                  <span className="material-symbols-outlined text-6xl mb-4 text-gray-300">construction</span>
                  <p className="text-xl font-medium">Work in Progress</p>
                  <p className="text-sm">The {activeTab} view is currently under development.</p>
                </div>
              )}
            </div>
          </div>

          {/* Results Preview Section */}
          <div className="mt-8 bg-white dark:bg-surface-dark rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm p-6">
            <div className="flex flex-col md:flex-row justify-between items-center mb-6">
              <div>
                <h3 className="text-lg font-bold text-text-main-light dark:text-white">Latest Result Preview</h3>
                <p className="text-sm text-gray-500">Solved in 1.4 seconds with 0 violations.</p>
              </div>
              <Link to={`/solution/${id}`} className="text-primary font-medium text-sm flex items-center gap-1 hover:underline">
                View Full Schedule
                <span className="material-symbols-outlined text-[18px]">arrow_forward</span>
              </Link>
            </div>
            <div className="relative h-48 bg-gray-50 dark:bg-gray-800 rounded-lg border border-dashed border-gray-300 dark:border-gray-600 flex items-center justify-center overflow-hidden">
              <div className="absolute inset-0 opacity-50 flex flex-col gap-2 p-4">
                <div className="h-6 w-3/4 bg-blue-100 dark:bg-blue-900/30 rounded"></div>
                <div className="h-6 w-1/2 bg-blue-200 dark:bg-blue-800/30 rounded ml-10"></div>
                <div className="h-6 w-2/3 bg-blue-100 dark:bg-blue-900/30 rounded ml-4"></div>
                <div className="h-6 w-full bg-blue-50 dark:bg-blue-900/20 rounded"></div>
                <div className="h-6 w-4/5 bg-blue-100 dark:bg-blue-900/30 rounded ml-12"></div>
              </div>
              <div className="z-10 bg-white dark:bg-surface-dark px-4 py-2 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 text-sm font-medium text-gray-600 dark:text-gray-300">
                Visual schedule data is ready
              </div>
            </div>
          </div>

        </div>
      </main>
    </div>
  );
}