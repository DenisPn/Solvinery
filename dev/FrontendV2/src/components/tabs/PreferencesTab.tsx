import { useState } from 'react';
import MaterialIcon from '../ui/MaterialIcon';
import PreferenceModuleModal from '../modals/PreferenceModuleModal';
import PreferenceModuleCard, { type PreferenceModuleData } from '../preferences/PreferenceModuleCard'; // <-- ייבוא החדש

export default function PreferencesTab() {
  const [isModalOpen, setIsModalOpen] = useState(false);

  // נתונים (State)
  const [preferences, setPreferences] = useState<PreferenceModuleData[]>([
    { 
      title: 'Staff Shift Requests', 
      date: 'Just now', 
      desc: 'Maximize the granting of specific shift requests and time-off wishes submitted by employees.', 
      count: 12, 
      icon: 'favorite', 
      color: 'pink' 
    },
    { 
      title: 'Minimize Overtime', 
      date: 'Yesterday', 
      desc: 'Aim to keep employee working hours within standard limits to reduce extra costs.', 
      count: 3, 
      icon: 'timelapse', 
      color: 'emerald' 
    },
    { 
      title: 'Maximize Weekends Off', 
      date: 'Oct 24, 2023', 
      desc: 'Prioritize giving full weekends off to staff whenever coverage allows.', 
      count: 1, 
      icon: 'weekend', 
      color: 'violet' 
    },
    { 
      title: 'Seniority Priorities', 
      date: 'Oct 20, 2023', 
      desc: 'Give scheduling priority for preferred shifts to senior team members.', 
      count: 5, 
      icon: 'stars', 
      color: 'amber' 
    },
  ]);

  // פונקציות דמי
  const handleEdit = (title: string) => {
    console.log('Edit preference:', title);
    setIsModalOpen(true);
  };
  
  const handleDelete = (title: string) => console.log('Delete preference:', title);

  return (
    <>
      <div className="bg-white dark:bg-[#1A2C38] rounded-xl border border-[#dbe2e6] dark:border-gray-700 shadow-sm flex flex-col min-h-[600px]">
          {/* Toolbar */}
          <div className="p-5 flex flex-col sm:flex-row justify-between items-center gap-4 border-b border-[#f0f3f4] dark:border-gray-700">
              <div className="flex items-center gap-3 w-full sm:w-auto flex-1">
                  <div className="relative w-full sm:max-w-xs">
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <MaterialIcon icon="search" className="text-gray-400 text-[20px]" />
                      </div>
                      <input className="block w-full pl-10 pr-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg leading-5 bg-white dark:bg-[#233340] dark:text-white placeholder-gray-500 focus:outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] sm:text-sm transition duration-150 ease-in-out" placeholder="Search preferences..." type="text"/>
                  </div>
                  <div className="relative">
                      <select className="appearance-none bg-white dark:bg-[#233340] border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-200 py-2 pl-3 pr-8 rounded-lg leading-tight focus:outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] sm:text-sm">
                          <option>All Modules</option>
                          <option>Recently Created</option>
                          <option>Alphabetical</option>
                      </select>
                      <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-500">
                          <MaterialIcon icon="expand_more" className="text-[18px]" />
                      </div>
                  </div>
              </div>

              {/* הכפתור שפותח את המודאל */}
              <button 
                onClick={() => setIsModalOpen(true)}
                className="flex w-full sm:w-auto items-center justify-center gap-2 rounded-lg h-10 px-5 bg-[#13a4ec] hover:bg-[#0f8ecb] text-white text-sm font-bold leading-normal tracking-[0.015em] transition-colors shadow-sm"
              >
                  <MaterialIcon icon="add" className="text-[20px]" />
                  <span className="truncate">Create New Module</span>
              </button>
          </div>
  
          {/* Cards Grid */}
          <div className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                  {preferences.map((p, i) => (
                     <PreferenceModuleCard 
                        key={i} 
                        module={p}
                        onEdit={() => handleEdit(p.title)}
                        onDelete={() => handleDelete(p.title)}
                     />
                  ))}
              </div>
          </div>
          
          {/* Pagination */}
          <div className="mt-auto px-6 py-4 border-t border-gray-200 dark:border-gray-700 flex items-center justify-between">
              <p className="text-sm text-gray-500 dark:text-gray-400">
                  Showing <span className="font-medium text-gray-900 dark:text-white">1</span> to <span className="font-medium text-gray-900 dark:text-white">{preferences.length}</span> of <span className="font-medium text-gray-900 dark:text-white">4</span> modules
              </p>
              <div className="flex gap-2">
                  <button className="relative inline-flex items-center px-4 py-2 border border-gray-300 dark:border-gray-600 text-sm font-medium rounded-lg text-gray-700 dark:text-gray-200 bg-white dark:bg-[#233340] hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed" disabled>
                      Previous
                  </button>
                  <button className="relative inline-flex items-center px-4 py-2 border border-gray-300 dark:border-gray-600 text-sm font-medium rounded-lg text-gray-700 dark:text-gray-200 bg-white dark:bg-[#233340] hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed" disabled>
                      Next
                  </button>
              </div>
          </div>
      </div>

      <PreferenceModuleModal 
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
      />
    </>
  );
}