import React, { useState } from 'react';
import MaterialIcon from '../ui/MaterialIcon';
import PreferenceModuleModal from '../modals/PreferenceModuleModal'; // <-- ייבוא המודאל החדש

export default function PreferencesTab() {
  const [isModalOpen, setIsModalOpen] = useState(false); // State לפתיחת המודאל

  const prefs = [
    { title: 'Staff Shift Requests', date: 'Just now', desc: 'Maximize the granting of specific shift requests and time-off wishes submitted by employees.', count: 12, icon: 'favorite', color: 'pink' },
    { title: 'Minimize Overtime', date: 'Yesterday', desc: 'Aim to keep employee working hours within standard limits to reduce extra costs.', count: 3, icon: 'timelapse', color: 'emerald' },
    { title: 'Maximize Weekends Off', date: 'Oct 24, 2023', desc: 'Prioritize giving full weekends off to staff whenever coverage allows.', count: 1, icon: 'weekend', color: 'violet' },
    { title: 'Seniority Priorities', date: 'Oct 20, 2023', desc: 'Give scheduling priority for preferred shifts to senior team members.', count: 5, icon: 'stars', color: 'amber' },
  ];

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
                  {prefs.map((p, i) => (
                     <div key={i} className="group flex flex-col justify-between rounded-xl border border-[#dbe2e6] bg-white p-5 shadow-sm transition-all hover:border-[#13a4ec] hover:shadow-md dark:border-gray-700 dark:bg-[#233340] dark:hover:border-[#13a4ec]">
                         <div>
                             <div className="flex items-start justify-between">
                                 <div className="flex items-center gap-3">
                                     <div className={`flex size-10 items-center justify-center rounded-lg 
                                         ${p.color === 'pink' ? 'bg-pink-50 text-pink-600 dark:bg-pink-900/20 dark:text-pink-400' : ''}
                                         ${p.color === 'emerald' ? 'bg-emerald-50 text-emerald-600 dark:bg-emerald-900/20 dark:text-emerald-400' : ''}
                                         ${p.color === 'violet' ? 'bg-violet-50 text-violet-600 dark:bg-violet-900/20 dark:text-violet-400' : ''}
                                         ${p.color === 'amber' ? 'bg-amber-50 text-amber-600 dark:bg-amber-900/20 dark:text-amber-400' : ''}
                                     `}>
                                         <MaterialIcon icon={p.icon} />
                                     </div>
                                     <div>
                                         <h3 className="text-base font-bold text-[#111618] dark:text-white group-hover:text-[#13a4ec] transition-colors">{p.title}</h3>
                                         <p className="text-xs text-gray-400 dark:text-gray-500">{p.date}</p>
                                     </div>
                                 </div>
                             </div>
                             <p className="mt-4 text-sm text-gray-600 dark:text-gray-300 line-clamp-2 min-h-[2.5em]">{p.desc}</p>
                             <div className="mt-4 flex items-center gap-2">
                                 <span className="inline-flex items-center gap-1.5 rounded-full bg-gray-100 px-2.5 py-1 text-xs font-medium text-gray-700 dark:bg-gray-700 dark:text-gray-300">
                                     <MaterialIcon icon="thumb_up" className="text-[16px]" />
                                     {p.count} Preferences
                                 </span>
                             </div>
                         </div>
                         <div className="mt-6 flex gap-3 pt-4 border-t border-gray-100 dark:border-gray-600">
                             <button className="flex flex-1 items-center justify-center gap-2 rounded-lg border border-gray-200 bg-white py-2 text-sm font-bold text-gray-700 hover:bg-gray-50 hover:text-[#13a4ec] dark:border-gray-600 dark:bg-[#2b3d4a] dark:text-gray-200 dark:hover:bg-gray-700 transition-colors">
                                 <MaterialIcon icon="edit" className="text-[18px]" /> Edit
                             </button>
                             <button className="flex flex-1 items-center justify-center gap-2 rounded-lg border border-gray-200 bg-white py-2 text-sm font-bold text-gray-700 hover:bg-red-50 hover:text-red-600 hover:border-red-200 dark:border-gray-600 dark:bg-[#2b3d4a] dark:text-gray-200 dark:hover:bg-gray-700 dark:hover:text-red-400 transition-colors">
                                 <MaterialIcon icon="delete" className="text-[18px]" /> Delete
                             </button>
                         </div>
                     </div>
                  ))}
              </div>
          </div>
          
          {/* Pagination */}
          <div className="mt-auto px-6 py-4 border-t border-gray-200 dark:border-gray-700 flex items-center justify-between">
              <p className="text-sm text-gray-500 dark:text-gray-400">
                  Showing <span className="font-medium text-gray-900 dark:text-white">1</span> to <span className="font-medium text-gray-900 dark:text-white">4</span> of <span className="font-medium text-gray-900 dark:text-white">4</span> modules
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

      {/* חיבור המודאל החדש */}
      <PreferenceModuleModal 
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
      />
    </>
  );
}