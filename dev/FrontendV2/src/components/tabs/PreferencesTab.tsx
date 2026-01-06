import { useState } from 'react';
import MaterialIcon from '../ui/MaterialIcon';
import PreferenceModuleModal from '../modals/PreferenceModuleModal';
import PreferenceModuleCard, { type PreferenceModuleData } from '../preferences/PreferenceModuleCard';

interface RawPreferenceData {
  identifier: string;
}

interface PreferencesTabProps {
  libraryData: RawPreferenceData[];
}

export default function PreferencesTab({ libraryData }: PreferencesTabProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [createdModules, setCreatedModules] = useState<PreferenceModuleData[]>([]);

  const handleCreateModule = (name: string, items: any[]) => {
    const newModule: PreferenceModuleData = {
        title: name,
        date: 'Just now',
        desc: `Optimization module with ${items.length} preferences.`,
        count: items.length,
        icon: 'tune',
        color: 'emerald'
    };
    setCreatedModules([...createdModules, newModule]);
  };

  const modalLibrary = libraryData.map(p => ({
      id: p.identifier,
      name: p.identifier,
      type: 'Preference',
      desc: 'ZPL Objective'
  }));

  return (
    <>
      <div className="bg-white dark:bg-[#1A2C38] rounded-xl border border-[#dbe2e6] dark:border-gray-700 shadow-sm flex flex-col min-h-[600px]">
          {/* Toolbar */}
          <div className="p-5 flex flex-col sm:flex-row justify-between items-center gap-4 border-b border-[#f0f3f4] dark:border-gray-700">
              <div className="flex items-center gap-3">
                 <div className="relative">
                      <MaterialIcon icon="search" className="absolute left-3 top-2.5 text-gray-400 text-[20px]" />
                      <input className="pl-10 pr-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-[#233340] dark:text-white text-sm focus:outline-none focus:border-[#13a4ec]" placeholder="Search preferences..." />
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
                        <MaterialIcon icon="tune" className="text-3xl text-gray-400" />
                      </div>
                      <h3 className="text-lg font-bold mb-1">No Preferences Modules</h3>
                      <p className="text-sm mb-6">Group your objectives into modules.</p>
                      <button onClick={() => setIsModalOpen(true)} className="text-[#13a4ec] font-semibold hover:underline">
                          Create module
                      </button>
                   </div>
              ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                      {createdModules.map((p, i) => (
                         <PreferenceModuleCard 
                            key={i} 
                            module={p}
                            onEdit={() => console.log('Edit', p.title)}
                            onDelete={() => console.log('Delete', p.title)}
                         />
                      ))}
                  </div>
              )}
          </div>
      </div>

      <PreferenceModuleModal 
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        availableLibrary={modalLibrary}
        onSave={handleCreateModule}
      />
    </>
  );
}