import { useState } from 'react';
import MaterialIcon from '../ui/MaterialIcon';
import ConstraintModuleModal from '../modals/ConstraintModuleModal';
import ConstraintModuleCard, { type ConstraintModuleData } from '../constraints/ConstraintModuleCard';

// הטיפוס של המידע הגולמי (ZPL)
interface RawConstraintData {
  identifier: string;
}

interface ConstraintsTabProps {
  libraryData: RawConstraintData[]; // <-- הספרייה הגולמית
}

export default function ConstraintsTab({ libraryData }: ConstraintsTabProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  // כאן אנחנו מנהלים את המודולים שהמשתמש *יצר*
  const [createdModules, setCreatedModules] = useState<ConstraintModuleData[]>([]);

  // פונקציה שנקראת כשהמודאל עושה "Save"
  const handleCreateModule = (name: string, items: any[]) => {
    const newModule: ConstraintModuleData = {
        title: name,
        date: 'Just now',
        desc: `Custom module containing ${items.length} constraints from ZPL.`,
        count: items.length,
        icon: 'extension',
        colorClasses: 'bg-blue-50 text-[#13a4ec] dark:bg-blue-900/20'
    };
    setCreatedModules([...createdModules, newModule]);
  };

  // המרה של המידע הגולמי לפורמט שהמודאל צריך
  const modalLibrary = libraryData.map(c => ({
      id: c.identifier,
      name: c.identifier,
      type: 'Constraint',
      desc: 'ZPL Constraint'
  }));

  return (
    <>
      <div className="bg-white dark:bg-[#1A2C38] rounded-xl border border-[#dbe2e6] dark:border-gray-700 shadow-sm flex flex-col min-h-[600px]">
          {/* Toolbar */}
          <div className="p-5 flex flex-col sm:flex-row justify-between items-center gap-4 border-b border-[#f0f3f4] dark:border-gray-700">
             <div className="flex items-center gap-3">
                 <div className="relative">
                      <MaterialIcon icon="search" className="absolute left-3 top-2.5 text-gray-400 text-[20px]" />
                      <input className="pl-10 pr-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-[#233340] dark:text-white text-sm focus:outline-none focus:border-[#13a4ec]" placeholder="Search modules..." />
                 </div>
             </div>
             <button onClick={() => setIsModalOpen(true)} className="flex items-center gap-2 rounded-lg h-10 px-5 bg-[#13a4ec] text-white text-sm font-bold shadow-sm hover:bg-[#0f8ecb] transition-colors">
                  <MaterialIcon icon="add" className="text-[20px]" />
                  <span>Create New Module</span>
              </button>
          </div>
  
          {/* Cards Grid */}
          <div className="p-6">
              {createdModules.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-20 text-gray-500 dark:text-gray-400">
                      <div className="w-16 h-16 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-4">
                        <MaterialIcon icon="dashboard_customize" className="text-3xl text-gray-400" />
                      </div>
                      <h3 className="text-lg font-bold mb-1">No Modules Created Yet</h3>
                      <p className="text-sm mb-6">Create modules to group your constraints together.</p>
                      <button onClick={() => setIsModalOpen(true)} className="text-[#13a4ec] font-semibold hover:underline">
                          Create your first module
                      </button>
                  </div>
              ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                      {createdModules.map((c, i) => (
                         <ConstraintModuleCard 
                            key={i} 
                            module={c}
                            onEdit={() => console.log('Edit', c.title)}
                            onDelete={() => console.log('Delete', c.title)}
                         />
                      ))}
                  </div>
              )}
          </div>
      </div>

      <ConstraintModuleModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)}
        availableLibrary={modalLibrary} // העברת הספרייה למודאל
        onSave={handleCreateModule}     // קבלת המודול החדש
      />
    </>
  );
}