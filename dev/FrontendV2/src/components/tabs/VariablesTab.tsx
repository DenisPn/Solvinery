import { useState } from 'react';
import MaterialIcon from '../ui/MaterialIcon';
import VariableTableRow, { type VariableData } from '../variables/VariableTableRow';
import VariableModal, { type StructureItem } from '../modals/VariableModal';

interface VariablesTabProps {
  data: VariableData[];
  onUpdate: (updatedData: VariableData[]) => void; // <-- Prop חדש לעדכון האבא
}

export default function VariablesTab({ data, onUpdate }: VariablesTabProps) {
  // מחקנו את useState(localVariables) - אנחנו מסתמכים על data מהאבא!
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedVariable, setSelectedVariable] = useState<VariableData | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  // סינון עובד ישירות על ה-data שמגיע מהאבא
  const filteredData = data.filter(v => 
    v.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
    (v.alias && v.alias.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  const handleAdd = () => {
    setSelectedVariable(null);
    setIsModalOpen(true);
  };

  const handleEdit = (variable: VariableData) => {
    setSelectedVariable(variable);
    setIsModalOpen(true);
  };

  const handleDelete = (nameToDelete: string) => {
    if (confirm('Are you sure you want to delete this variable?')) {
        const updatedList = data.filter(v => v.name !== nameToDelete);
        onUpdate(updatedList); // עדכון האבא
    }
  };

  const handleSaveVariable = (newData: { name: string; alias: string; objectiveValueAlias?: string; structure: StructureItem[] }) => {
    
    // המרת מבנה המערך למחרוזת
    const structureString = `[${newData.structure.map(s => {
        const typeUpper = s.type.toUpperCase();
        if (typeUpper === 'INTEGER') return 'INT';
        if (typeUpper === 'BOOLEAN') return 'BOOL';
        if (typeUpper === 'STRING') return 'TEXT';
        return typeUpper;
    }).join(', ')}]`;

    const newVar: VariableData = {
        name: newData.name,
        type: structureString,
        alias: newData.alias,
        objectiveValueAlias: newData.objectiveValueAlias || '',
        desc: newData.alias ? `Alias: ${newData.alias}` : 'Decision Variable'
    };

    let updatedList;
    if (selectedVariable) {
        // עדכון קיים
        updatedList = data.map(v => v.name === selectedVariable.name ? newVar : v);
    } else {
        // יצירה חדשה
        updatedList = [...data, newVar];
    }

    onUpdate(updatedList); // שליחת הרשימה המעודכנת לאבא
    setIsModalOpen(false);
  };

  return (
    <>
      <div className="flex flex-col bg-white dark:bg-[#182830] rounded-xl shadow-sm border border-[#dbe2e6] dark:border-[#2a3b45] overflow-hidden min-h-[600px]">
        {/* ... (כל קוד ה-HTML של ה-Toolbar, Search והטבלה נשאר זהה לחלוטין) ... */}
        
        {/* Toolbar Section */}
        <div className="p-4 border-b border-[#dbe2e6] dark:border-[#2a3b45] flex flex-col md:flex-row gap-4 justify-between items-start md:items-center bg-gray-50/50 dark:bg-[#182830]">
          <div className="w-full md:w-auto md:flex-1 md:max-w-md">
            <div className="flex w-full items-center rounded-lg h-10 border border-[#dbe2e6] dark:border-[#2a3b45] bg-white dark:bg-[#101c22] focus-within:border-[#13a4ec] focus-within:ring-1 focus-within:ring-[#13a4ec] transition-all">
              <div className="text-[#617c89] dark:text-[#9aaeb5] flex items-center justify-center pl-3">
                <MaterialIcon icon="search" className="text-[20px]" />
              </div>
              <input 
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full min-w-0 flex-1 bg-transparent border-none text-[#111618] dark:text-[#e0e6e9] focus:ring-0 placeholder:text-[#617c89]/70 dark:placeholder:text-[#9aaeb5]/70 px-3 text-sm font-normal outline-none" 
                placeholder="Search variables by name..." 
                type="text"
              />
            </div>
          </div>
          <div className="flex gap-3 w-full md:w-auto">
             <button className="flex items-center gap-2 px-3 py-2 bg-white dark:bg-[#101c22] border border-[#dbe2e6] dark:border-[#2a3b45] rounded-lg text-sm font-medium text-[#617c89] dark:text-[#9aaeb5] hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
                <MaterialIcon icon="filter_list" className="text-[20px]" />
                Filter
             </button>
             <button onClick={handleAdd} className="flex-1 md:flex-none flex items-center justify-center gap-2 px-4 py-2 bg-[#13a4ec] hover:bg-[#0e8bc7] text-white rounded-lg text-sm font-bold shadow-sm transition-colors">
                <MaterialIcon icon="add" className="text-[20px]" />
                <span className="whitespace-nowrap">Add Variable</span>
             </button>
          </div>
        </div>

        {/* Data Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 dark:bg-[#182830] border-b border-[#dbe2e6] dark:border-[#2a3b45]">
                {['Variable Name', 'Structure', 'Alias', 'Objective Value Alias'].map((h) => (
                  <th key={h} className="py-3 px-6 text-xs font-semibold uppercase tracking-wider text-[#617c89] dark:text-[#9aaeb5]">
                    {h}
                  </th>
                ))}
                <th className="py-3 px-6 text-xs font-semibold uppercase tracking-wider text-[#617c89] dark:text-[#9aaeb5] text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#dbe2e6] dark:divide-[#2a3b45] bg-white dark:bg-[#182830]">
              {filteredData.length === 0 ? (
                  <tr><td colSpan={5} className="py-12 text-center text-[#617c89] dark:text-[#9aaeb5]">No variables found matching your search.</td></tr>
              ) : (
                  filteredData.map((variable, index) => (
                    <VariableTableRow 
                      key={index} 
                      variable={variable} 
                      onEdit={() => handleEdit(variable)} 
                      onDelete={() => handleDelete(variable.name)}
                    />
                  ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination & Info - נשאר זהה */}
        <div className="px-6 py-4 border-t border-[#dbe2e6] dark:border-[#2a3b45] bg-white dark:bg-[#182830] flex items-center justify-between mt-auto">
            <div className="text-sm text-[#617c89] dark:text-[#9aaeb5]">
                Showing <span className="font-medium text-[#111618] dark:text-[#e0e6e9]">1</span> to <span className="font-medium text-[#111618] dark:text-[#e0e6e9]">{filteredData.length}</span> of <span className="font-medium text-[#111618] dark:text-[#e0e6e9]">{data.length}</span> results
            </div>
            <div className="flex items-center gap-2">
                <button className="p-2 rounded-lg border border-[#dbe2e6] dark:border-[#2a3b45] text-[#617c89] dark:text-[#9aaeb5] hover:bg-gray-50 dark:hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed">
                    <MaterialIcon icon="chevron_left" className="text-[20px]" />
                </button>
                <button className="p-2 rounded-lg border border-[#dbe2e6] dark:border-[#2a3b45] text-[#617c89] dark:text-[#9aaeb5] hover:bg-gray-50 dark:hover:bg-gray-800">
                    <MaterialIcon icon="chevron_right" className="text-[20px]" />
                </button>
            </div>
        </div>
      </div>

      <div className="mx-4 mt-6 mb-8 p-4 bg-[#13a4ec]/10 dark:bg-[#13a4ec]/5 rounded-lg border border-[#13a4ec]/20 flex gap-4 items-start">
        <span className="text-[#13a4ec] mt-0.5"><MaterialIcon icon="info" /></span>
        <div className="flex flex-col gap-1">
            <h3 className="text-sm font-bold text-[#111618] dark:text-[#e0e6e9]">Defining Variables</h3>
            <p className="text-sm text-[#617c89] dark:text-[#9aaeb5]">Variables represent the decisions your model needs to make.</p>
        </div>
      </div>

      <VariableModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        initialData={selectedVariable}
        onSave={handleSaveVariable}
      />
    </>
  );
}