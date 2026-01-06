import { useState } from 'react'; // שומרים רק את מה שצריך, או מוחקים לגמרי אם לא משתמשים ב-hooks
import MaterialIcon from '../ui/MaterialIcon';
import SetTableRow, { type SetData } from '../sets/SetTableRow'; // <-- הוספנו את המילה type

export default function SetsTab() {
  
  // נתונים לדוגמה
  const sets: SetData[] = [
    { 
      name: 'Doctors', 
      desc: 'Core Medical Staff', 
      members: ['Dr. House', 'Dr. Wilson'], 
      extraCount: 12,
      totalCount: 14 
    },
    { 
      name: 'Nurses', 
      desc: 'Support Staff', 
      members: ['Nurse Joy', 'Nurse Ratched'], 
      extraCount: 28,
      totalCount: 30 
    },
    { 
      name: 'Shifts', 
      desc: 'Time slots', 
      members: ['Morning', 'Afternoon', 'Night'], 
      extraCount: 0,
      totalCount: 3 
    },
    { 
      name: 'Locations', 
      desc: 'Departments', 
      members: ['ER', 'ICU', 'Pediatrics'], 
      extraCount: 2,
      totalCount: 5 
    },
  ];

  // פונקציות דמי
  const handleEdit = (name: string) => console.log('Edit', name);
  const handleDelete = (name: string) => console.log('Delete', name);

  return (
    <div className="bg-white dark:bg-[#15232d] rounded-xl shadow-sm border border-slate-100 dark:border-slate-800 p-6 sm:p-8 flex flex-col gap-6 min-h-[600px]">
      
      {/* Headline & Description */}
      <div>
        <h2 className="text-[#111618] dark:text-white text-2xl font-bold leading-tight mb-2">Define Sets</h2>
        <p className="text-slate-600 dark:text-slate-300 text-base font-normal leading-normal max-w-2xl">
          Create groups of entities (e.g., Doctors, Nurses, Night Shifts) to be used in your scheduling constraints. Define the set name and list its members.
        </p>
      </div>

      {/* Action Toolbar */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="relative w-full sm:w-72">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <MaterialIcon icon="search" className="text-slate-400 text-[20px]" />
          </div>
          <input 
            className="block w-full pl-10 pr-3 py-2 border border-slate-200 dark:border-slate-700 rounded-lg leading-5 bg-white dark:bg-[#1e2d3b] text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-[#13a4ec] focus:border-[#13a4ec] sm:text-sm" 
            placeholder="Search sets..." 
            type="text"
          />
        </div>
        <div className="flex gap-3 w-full sm:w-auto">
          <button className="flex-1 sm:flex-none items-center justify-center px-4 py-2 border border-slate-200 dark:border-slate-700 rounded-lg shadow-sm text-sm font-medium text-slate-700 dark:text-slate-200 bg-white dark:bg-[#1e2d3b] hover:bg-slate-50 dark:hover:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#13a4ec] transition-colors inline-flex gap-2">
            <MaterialIcon icon="file_upload" className="text-[18px]" />
            Import CSV
          </button>
          <button className="flex-1 sm:flex-none items-center justify-center px-4 py-2 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-[#13a4ec] hover:bg-[#13a4ec]/90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#13a4ec] transition-colors inline-flex gap-2">
            <MaterialIcon icon="add" className="text-[18px]" />
            Add New Set
          </button>
        </div>
      </div>

      {/* Data Table */}
      <div className="overflow-hidden border border-slate-200 dark:border-slate-700 rounded-lg">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200 dark:divide-slate-700">
            <thead className="bg-slate-50 dark:bg-[#1e2d3b]">
              <tr>
                <th className="px-6 py-3 text-left" scope="col">
                  <div className="flex items-center">
                    <input className="h-4 w-4 text-[#13a4ec] border-slate-300 rounded focus:ring-[#13a4ec] bg-white dark:bg-[#15232d] dark:border-slate-600" type="checkbox"/>
                  </div>
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider" scope="col">Set Name</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider" scope="col">Members</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider" scope="col">Count</th>
                <th className="px-6 py-3 text-right text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider" scope="col">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-[#15232d] divide-y divide-slate-200 dark:divide-slate-700">
              {sets.map((set, index) => (
                <SetTableRow 
                  key={index} 
                  set={set} 
                  onEdit={() => handleEdit(set.name)}
                  onDelete={() => handleDelete(set.name)}
                />
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pagination Footer */}
      <div className="bg-white dark:bg-[#15232d] px-4 py-3 flex items-center justify-between border-t border-slate-200 dark:border-slate-700 sm:px-6 mt-auto">
        <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p className="text-sm text-slate-700 dark:text-slate-300">
              Showing <span className="font-medium">1</span> to <span className="font-medium">4</span> of <span className="font-medium">12</span> sets
            </p>
          </div>
          <div>
            <nav aria-label="Pagination" className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
              <a className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-slate-300 dark:border-slate-600 bg-white dark:bg-[#1e2d3b] text-sm font-medium text-slate-500 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-700" href="#">
                <span className="sr-only">Previous</span>
                <MaterialIcon icon="chevron_left" className="text-[20px]" />
              </a>
              <a aria-current="page" className="z-10 bg-[#13a4ec]/10 border-[#13a4ec] text-[#13a4ec] relative inline-flex items-center px-4 py-2 border text-sm font-medium" href="#">1</a>
              <a className="bg-white dark:bg-[#1e2d3b] border-slate-300 dark:border-slate-600 text-slate-500 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-700 relative inline-flex items-center px-4 py-2 border text-sm font-medium" href="#">2</a>
              <a className="bg-white dark:bg-[#1e2d3b] border-slate-300 dark:border-slate-600 text-slate-500 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-700 relative inline-flex items-center px-4 py-2 border text-sm font-medium" href="#">3</a>
              <a className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-slate-300 dark:border-slate-600 bg-white dark:bg-[#1e2d3b] text-sm font-medium text-slate-500 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-700" href="#">
                <span className="sr-only">Next</span>
                <MaterialIcon icon="chevron_right" className="text-[20px]" />
              </a>
            </nav>
          </div>
        </div>
      </div>
    </div>
  );
}