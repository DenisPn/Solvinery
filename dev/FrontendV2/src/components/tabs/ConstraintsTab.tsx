import { useState } from 'react';
import MaterialIcon from '../ui/MaterialIcon';
import ConstraintModuleModal from '../modals/ConstraintModuleModal';
import ConstraintModuleCard from '../constraints/ConstraintModuleCard';

// Define the shape of data for both UI and API
export interface CombinedConstraintModuleData {
  // UI Fields
  title: string;
  date: string;
  desc: string;
  count: number;
  icon: string;
  colorClasses: string;

  // API Fields
  name: string;      // The module name for the API
  constraints: string[]; // The actual list of constraints
  description: string;
}

interface ConstraintsTabProps {
  data: CombinedConstraintModuleData[]; // Data passed from parent (NewImagePage)
  onUpdate: (data: CombinedConstraintModuleData[]) => void; // Callback to update parent state
  libraryData: any[]; // Raw ZPL data for the modal to select from
}

export default function ConstraintsTab({ data, onUpdate, libraryData }: ConstraintsTabProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  // Note: We removed the local 'createdModules' state. 
  // We now use the 'data' prop directly.

  const handleCreateModule = (name: string, items: any[]) => {
    const newModule: CombinedConstraintModuleData = {
        // --- UI Fields ---
        title: name,
        date: 'Just now',
        desc: `Custom module containing ${items.length} constraints from ZPL.`,
        count: items.length,
        icon: 'extension',
        colorClasses: 'bg-blue-50 text-[#13a4ec] dark:bg-blue-900/20',

        // --- API Fields ---
        name: name,
        description: `Custom module containing ${items.length} constraints.`,
        // Ensure we store strings (constraint identifiers or text)
        constraints: items.map(item => typeof item === 'string' ? item : item.identifier || item.name)
    };
    
    // Update the parent state
    onUpdate([...data, newModule]);
    setIsModalOpen(false);
  };

  const handleDeleteModule = (titleToDelete: string) => {
      const updatedList = data.filter(m => m.title !== titleToDelete);
      onUpdate(updatedList);
  };

  // Convert raw ZPL data for the modal
  const modalLibrary = libraryData.map(c => ({
      id: c.identifier || c.name,
      name: c.identifier || c.name,
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
              {data.length === 0 ? (
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
                      {data.map((c, i) => (
                         <ConstraintModuleCard 
                            key={i} 
                            // Cast as any if there are minor type mismatches with the card component
                            module={c as any} 
                            onEdit={() => console.log('Edit', c.title)}
                            onDelete={() => handleDeleteModule(c.title)}
                         />
                      ))}
                  </div>
              )}
          </div>
      </div>

      <ConstraintModuleModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)}
        availableLibrary={modalLibrary} 
        onSave={handleCreateModule}     
      />
    </>
  );
}