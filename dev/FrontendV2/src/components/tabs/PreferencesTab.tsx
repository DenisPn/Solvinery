import { useState } from 'react';
import MaterialIcon from '../ui/MaterialIcon';
import PreferenceModuleModal from '../modals/PreferenceModuleModal';
import PreferenceModuleCard from '../preferences/PreferenceModuleCard';

// הגדרת הצבעים המותרים בדיוק כפי שהכרטיסייה מצפה להם
type ModuleColor = 'emerald' | 'pink' | 'violet' | 'amber' | 'blue';

export interface CombinedPreferenceModuleData {
  // שדות ל-UI
  title: string;
  date: string;
  desc: string;
  count: number;
  icon: string;
  color: ModuleColor; // 👇 תיקון: שימוש בטיפוס הספציפי במקום string
  
  // שדות ל-API
  name: string;
  preferences: string[]; 
  description: string;
}

interface PreferencesTabProps {
  data: CombinedPreferenceModuleData[];
  onUpdate: (data: CombinedPreferenceModuleData[]) => void;
  libraryData: any[];
}

export default function PreferencesTab({ data, onUpdate, libraryData }: PreferencesTabProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleCreateModule = (name: string, items: any[]) => {
    const newModule: CombinedPreferenceModuleData = {
        // --- UI Fields ---
        title: name,
        date: 'Just now',
        desc: `Optimization module with ${items.length} preferences.`,
        count: items.length,
        icon: 'tune',
        color: 'emerald', // זה תקין כי 'emerald' נמצא ברשימת הצבעים המותרים

        // --- API Fields ---
        name: name,
        description: `Optimization module with ${items.length} preferences.`,
        preferences: items.map(item => typeof item === 'string' ? item : item.identifier || item.name)
    };

    onUpdate([...data, newModule]);
    setIsModalOpen(false);
  };

  const handleDeleteModule = (titleToDelete: string) => {
      const updatedList = data.filter(m => m.title !== titleToDelete);
      onUpdate(updatedList);
  };

  const modalLibrary = libraryData.map(p => ({
      id: p.identifier || p.name,
      name: p.identifier || p.name,
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
              {data.length === 0 ? (
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
                      {data.map((p, i) => (
                         <PreferenceModuleCard 
                            key={i} 
                            // אנו עושים כאן casting קטן (as any) אם הטיפוסים עדיין מתנגשים בגלל הגדרות פנימיות של הכרטיסייה,
                            // אבל התיקון למעלה ב-interface אמור לפתור את זה באופן נקי.
                            module={p as any} 
                            onEdit={() => console.log('Edit', p.title)}
                            onDelete={() => handleDeleteModule(p.title)}
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