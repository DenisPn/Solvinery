import { useState } from 'react';
import { Link } from 'react-router-dom';

// --- טיפוסים ---
interface ConstraintToggle {
  id: string;
  label: string;
  subtext: string;
  isActive: boolean;
}

interface PreferenceSlider {
  id: string;
  label: string;
  value: number;
  tagLabel: string;
  tagColor: string; // Tailwind classes
}

// --- קומפוננטה ראשית ---
export default function NewImagePage() {
  // State עבור הטופס הראשי
  const [problemName, setProblemName] = useState('');
  const [description, setDescription] = useState('');

  // State עבור האילוצים (Constraints)
  const [constraints, setConstraints] = useState<ConstraintToggle[]>([
    { id: '1', label: 'Max Hours Per Week', subtext: 'Limit to 40h per employee', isActive: true },
    { id: '2', label: 'No Overlapping Shifts', subtext: 'Prevent double booking', isActive: true },
    { id: '3', label: 'Min Rest Period', subtext: '12h between shifts', isActive: false },
  ]);

  // State עבור ההעדפות (Preferences)
  const [preferences, setPreferences] = useState<PreferenceSlider[]>([
    { id: 'p1', label: 'Minimize Cost', value: 80, tagLabel: 'High', tagColor: 'bg-primary/10 text-primary' },
    { id: 'p2', label: 'Employee Fairness', value: 50, tagLabel: 'Med', tagColor: 'bg-gray-100 dark:bg-gray-700 text-gray-500 dark:text-gray-300' },
    { id: 'p3', label: 'Shift Consistency', value: 20, tagLabel: 'Low', tagColor: 'bg-gray-100 dark:bg-gray-700 text-gray-500 dark:text-gray-300' },
  ]);

  // פונקציה לשינוי מצב אילוץ
  const toggleConstraint = (id: string) => {
    setConstraints(prev => prev.map(c => c.id === id ? { ...c, isActive: !c.isActive } : c));
  };

  // פונקציה לשינוי ערך סליידר
  const updatePreference = (id: string, newValue: number) => {
    setPreferences(prev => prev.map(p => p.id === id ? { ...p, value: newValue } : p));
  };

  return (
    <div className="min-h-screen flex flex-col bg-background-light dark:bg-background-dark text-text-main-light dark:text-white font-display overflow-x-hidden">
      
      {/* Top Navigation - Header ספציפי לדף זה כפי שהופיע בעיצוב */}
      <header className="flex items-center justify-between whitespace-nowrap border-b border-solid border-[#f0f3f4] dark:border-border-dark bg-white dark:bg-surface-dark px-10 py-3 sticky top-0 z-50">
        <div className="flex items-center gap-4 text-text-main-light dark:text-white">
          <div className="size-8 flex items-center justify-center bg-primary/10 rounded-lg text-primary">
            <span className="material-symbols-outlined text-2xl">calendar_month</span>
          </div>
          <h2 className="text-text-main-light dark:text-white text-lg font-bold leading-tight tracking-[-0.015em]">Scheduling Solver</h2>
        </div>
        <div className="flex flex-1 justify-end gap-8">
          <div className="hidden md:flex items-center gap-9">
            <Link to="/" className="text-text-main-light dark:text-gray-300 text-sm font-medium leading-normal hover:text-primary transition-colors">Dashboard</Link>
            <Link to="/" className="text-text-main-light dark:text-gray-300 text-sm font-medium leading-normal hover:text-primary transition-colors">Problems</Link>
            <a className="text-text-main-light dark:text-gray-300 text-sm font-medium leading-normal hover:text-primary transition-colors" href="#">Solvers</a>
            <a className="text-text-main-light dark:text-gray-300 text-sm font-medium leading-normal hover:text-primary transition-colors" href="#">Settings</a>
          </div>
          <div className="flex items-center gap-4">
            <button className="flex items-center justify-center rounded-full size-10 bg-background-light dark:bg-background-dark text-text-main-light dark:text-white hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors">
              <span className="material-symbols-outlined">notifications</span>
            </button>
            <div 
              className="bg-center bg-no-repeat aspect-square bg-cover rounded-full size-10 border-2 border-white dark:border-surface-dark shadow-sm" 
              style={{ backgroundImage: 'url("https://i.pravatar.cc/150?img=10")' }}
            ></div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 flex flex-col items-center py-10 px-4 md:px-10 lg:px-40">
        <div className="w-full max-w-[1024px] flex flex-col gap-8">
          
          {/* Breadcrumbs */}
          <nav className="flex flex-wrap gap-2 items-center">
            <Link to="/" className="text-text-secondary-light dark:text-gray-400 text-sm font-medium leading-normal hover:text-primary transition-colors">Home</Link>
            <span className="material-symbols-outlined text-text-secondary-light dark:text-gray-400 text-sm">chevron_right</span>
            <Link to="/" className="text-text-secondary-light dark:text-gray-400 text-sm font-medium leading-normal hover:text-primary transition-colors">Problems</Link>
            <span className="material-symbols-outlined text-text-secondary-light dark:text-gray-400 text-sm">chevron_right</span>
            <span className="text-text-main-light dark:text-white text-sm font-medium leading-normal">New</span>
          </nav>

          {/* Page Heading */}
          <div className="flex flex-col gap-3">
            <h1 className="text-text-main-light dark:text-white text-4xl font-black leading-tight tracking-[-0.033em]">Create New Scheduling Problem</h1>
            <p className="text-text-secondary-light dark:text-gray-400 text-lg font-normal leading-normal max-w-2xl">Define the scope, constraints, and objectives for your new scheduling task. Use this tool to set up the initial parameters before running the solver.</p>
          </div>

          {/* Main Form Content */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            
            {/* Left Column: General Info & Sets */}
            <div className="lg:col-span-8 flex flex-col gap-6">
              
              {/* General Information Card */}
              <div className="bg-white dark:bg-surface-dark rounded-xl border border-border-light dark:border-border-dark p-6 shadow-sm">
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-2 bg-primary/10 rounded-lg text-primary">
                    <span className="material-symbols-outlined">description</span>
                  </div>
                  <h3 className="text-xl font-bold text-text-main-light dark:text-white">General Information</h3>
                </div>
                <div className="flex flex-col gap-6">
                  <label className="flex flex-col w-full">
                    <p className="text-text-main-light dark:text-gray-200 text-base font-medium leading-normal pb-2">Problem Name <span className="text-red-500">*</span></p>
                    <input 
                      value={problemName}
                      onChange={(e) => setProblemName(e.target.value)}
                      className="form-input flex w-full rounded-lg text-text-main-light dark:text-white focus:outline-0 focus:ring-2 focus:ring-primary/50 border border-border-light dark:border-border-dark bg-white dark:bg-background-dark h-12 px-4 text-base placeholder:text-text-secondary-light dark:placeholder:text-gray-500 transition-all" 
                      placeholder="e.g., Q4 Nurse Shifts 2023"
                    />
                  </label>
                  <label className="flex flex-col w-full">
                    <p className="text-text-main-light dark:text-gray-200 text-base font-medium leading-normal pb-2">Description <span className="text-text-secondary-light dark:text-gray-500 text-sm font-normal">(Optional)</span></p>
                    <textarea 
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      className="form-input flex w-full rounded-lg text-text-main-light dark:text-white focus:outline-0 focus:ring-2 focus:ring-primary/50 border border-border-light dark:border-border-dark bg-white dark:bg-background-dark min-h-[120px] p-4 text-base placeholder:text-text-secondary-light dark:placeholder:text-gray-500 resize-y transition-all" 
                      placeholder="Enter a brief description of the scheduling context, goals, and any specific notes..."
                    ></textarea>
                  </label>
                </div>
              </div>

              {/* Sets & Variables Card */}
              <div className="bg-white dark:bg-surface-dark rounded-xl border border-border-light dark:border-border-dark p-6 shadow-sm">
                <div className="flex items-center justify-between mb-6">
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-green-100 dark:bg-green-900/30 rounded-lg text-green-600 dark:text-green-400">
                      <span className="material-symbols-outlined">dataset</span>
                    </div>
                    <div>
                      <h3 className="text-xl font-bold text-text-main-light dark:text-white">Sets & Variables</h3>
                      <p className="text-sm text-text-secondary-light dark:text-gray-400">Define the resources and items involved.</p>
                    </div>
                  </div>
                  <button className="text-primary text-sm font-medium hover:underline flex items-center gap-1">
                    <span className="material-symbols-outlined text-lg">add</span> Add Set
                  </button>
                </div>
                
                <div className="flex flex-col gap-4">
                  {/* Set Item 1 */}
                  <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 p-4 rounded-lg border border-[#f0f3f4] dark:border-border-dark bg-background-light dark:bg-background-dark/50">
                    <div className="flex items-center gap-4">
                      <div className="size-10 rounded-full bg-white dark:bg-surface-dark flex items-center justify-center text-text-secondary-light border border-gray-100 dark:border-border-dark shadow-sm">
                        <span className="material-symbols-outlined">group</span>
                      </div>
                      <div>
                        <p className="font-bold text-text-main-light dark:text-white">Employees</p>
                        <p className="text-sm text-text-secondary-light dark:text-gray-400">14 items defined</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2 w-full sm:w-auto">
                      <button className="flex-1 sm:flex-none px-4 py-2 bg-white dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-lg text-sm font-medium text-text-main-light dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">Edit List</button>
                      <button className="p-2 text-text-secondary-light hover:text-red-500 transition-colors">
                        <span className="material-symbols-outlined">delete</span>
                      </button>
                    </div>
                  </div>

                  {/* Set Item 2 */}
                  <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 p-4 rounded-lg border border-[#f0f3f4] dark:border-border-dark bg-background-light dark:bg-background-dark/50">
                    <div className="flex items-center gap-4">
                      <div className="size-10 rounded-full bg-white dark:bg-surface-dark flex items-center justify-center text-text-secondary-light border border-gray-100 dark:border-border-dark shadow-sm">
                        <span className="material-symbols-outlined">schedule</span>
                      </div>
                      <div>
                        <p className="font-bold text-text-main-light dark:text-white">Shift Types</p>
                        <p className="text-sm text-text-secondary-light dark:text-gray-400">3 items (Morning, Evening, Night)</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2 w-full sm:w-auto">
                      <button className="flex-1 sm:flex-none px-4 py-2 bg-white dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-lg text-sm font-medium text-text-main-light dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">Edit List</button>
                      <button className="p-2 text-text-secondary-light hover:text-red-500 transition-colors">
                        <span className="material-symbols-outlined">delete</span>
                      </button>
                    </div>
                  </div>

                  {/* Add Placeholder */}
                  <button className="flex items-center justify-center gap-2 p-4 rounded-lg border border-dashed border-border-light dark:border-gray-600 bg-transparent hover:bg-background-light dark:hover:bg-white/5 transition-colors group">
                    <span className="material-symbols-outlined text-text-secondary-light group-hover:text-primary transition-colors">add_circle</span>
                    <span className="text-text-secondary-light group-hover:text-primary font-medium transition-colors">Define new variable set</span>
                  </button>
                </div>
              </div>
            </div>

            {/* Right Column: Constraints & Preferences */}
            <div className="lg:col-span-4 flex flex-col gap-6">
              
              {/* Constraints Card */}
              <div className="bg-white dark:bg-surface-dark rounded-xl border border-border-light dark:border-border-dark p-6 shadow-sm">
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-2 bg-orange-100 dark:bg-orange-900/30 rounded-lg text-orange-600 dark:text-orange-400">
                    <span className="material-symbols-outlined">lock</span>
                  </div>
                  <h3 className="text-xl font-bold text-text-main-light dark:text-white">Constraints</h3>
                </div>
                
                <div className="flex flex-col gap-5">
                  {constraints.map((constraint, idx) => (
                    <div key={constraint.id}>
                      <label className="flex items-start justify-between gap-4 cursor-pointer group">
                        <div className="flex flex-col">
                          <span className="text-text-main-light dark:text-gray-200 font-medium text-sm">{constraint.label}</span>
                          <span className="text-text-secondary-light dark:text-gray-400 text-xs">{constraint.subtext}</span>
                        </div>
                        <div className="relative flex items-center">
                          <input 
                            checked={constraint.isActive}
                            onChange={() => toggleConstraint(constraint.id)}
                            className="peer sr-only" 
                            type="checkbox"
                          />
                          <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-primary"></div>
                        </div>
                      </label>
                      {idx < constraints.length - 1 && <hr className="border-[#f0f3f4] dark:border-border-dark mt-5" />}
                    </div>
                  ))}
                  
                  <button className="mt-2 w-full py-2 flex items-center justify-center gap-2 text-primary font-medium text-sm border border-transparent hover:bg-primary/5 rounded-lg transition-colors">
                    <span className="material-symbols-outlined text-lg">add</span> Add Constraint
                  </button>
                </div>
              </div>

              {/* Preferences Card */}
              <div className="bg-white dark:bg-surface-dark rounded-xl border border-border-light dark:border-border-dark p-6 shadow-sm">
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-2 bg-purple-100 dark:bg-purple-900/30 rounded-lg text-purple-600 dark:text-purple-400">
                    <span className="material-symbols-outlined">tune</span>
                  </div>
                  <h3 className="text-xl font-bold text-text-main-light dark:text-white">Preferences</h3>
                </div>
                
                <div className="flex flex-col gap-6">
                  {preferences.map((pref) => (
                    <div key={pref.id} className="flex flex-col gap-2">
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium text-text-main-light dark:text-gray-200">{pref.label}</span>
                        <div className="flex items-center gap-2">
                          <span className="text-xs text-gray-400">{pref.value}%</span>
                          <span className={`text-xs font-bold px-2 py-0.5 rounded ${pref.tagColor}`}>
                            {pref.tagLabel}
                          </span>
                        </div>
                      </div>
                      <input 
                        type="range" 
                        min="0" 
                        max="100" 
                        value={pref.value}
                        onChange={(e) => updatePreference(pref.id, parseInt(e.target.value))}
                        className="w-full h-2 bg-gray-200 dark:bg-gray-700 rounded-lg appearance-none cursor-pointer accent-primary"
                      />
                    </div>
                  ))}
                </div>
              </div>

            </div>
          </div>

          {/* Action Bar (Sticky Footer) */}
          <div className="sticky bottom-4 z-40 mt-4">
            <div className="bg-white/80 dark:bg-surface-dark/90 backdrop-blur-md border border-border-light dark:border-border-dark rounded-2xl p-4 shadow-lg flex items-center justify-between">
              <div className="hidden sm:block pl-2">
                <span className="text-sm text-text-secondary-light dark:text-gray-400">Ready to solve? Save your configuration first.</span>
              </div>
              <div className="flex gap-3 w-full sm:w-auto justify-end">
                <Link to="/" className="flex-1 sm:flex-none">
                  <button className="w-full h-12 px-6 rounded-lg bg-transparent border border-border-light dark:border-gray-600 text-text-main-light dark:text-white font-medium hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                    Cancel
                  </button>
                </Link>
                <button className="flex-1 sm:flex-none h-12 px-8 rounded-lg bg-primary hover:bg-primary/90 text-white font-bold shadow-md shadow-primary/20 transition-all transform hover:-translate-y-0.5 flex items-center justify-center gap-2">
                  <span>Create Problem</span>
                  <span className="material-symbols-outlined text-lg">arrow_forward</span>
                </button>
              </div>
            </div>
          </div>

        </div>
      </main>
    </div>
  );
}