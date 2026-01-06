import MaterialIcon from '../ui/MaterialIcon';
import VariableTableRow, { type VariableData } from '../variables/VariableTableRow';

interface VariablesTabProps {
  data: VariableData[];
}

export default function VariablesTab({ data }: VariablesTabProps) {
  
  return (
    <div className="flex flex-col bg-white dark:bg-[#1A2C38] rounded-xl shadow-sm border border-[#dbe2e6] dark:border-gray-700 overflow-hidden min-h-[600px]">
        {/* Toolbar */}
        <div className="p-4 border-b border-[#dbe2e6] dark:border-gray-700 flex flex-col md:flex-row gap-4 justify-between items-start md:items-center bg-gray-50/50 dark:bg-[#1A2C38]">
            <div className="w-full md:w-auto md:flex-1 md:max-w-md">
                <div className="flex w-full items-center rounded-lg h-10 border border-[#dbe2e6] dark:border-gray-600 bg-white dark:bg-[#233340] focus-within:ring-1 focus-within:ring-[#13a4ec] focus-within:border-[#13a4ec] transition-all">
                    <div className="text-[#617c89] dark:text-gray-400 flex items-center justify-center pl-3">
                        <MaterialIcon icon="search" className="text-[20px]" />
                    </div>
                    <input className="w-full min-w-0 flex-1 bg-transparent border-none text-[#111618] dark:text-white focus:outline-none placeholder:text-[#617c89]/70 dark:placeholder:text-gray-500 px-3 text-sm font-normal" placeholder="Search variables..." />
                </div>
            </div>
            <div className="flex gap-3 w-full md:w-auto">
                <button className="flex-1 md:flex-none flex items-center justify-center gap-2 px-4 py-2 bg-[#13a4ec] hover:bg-[#0e8bc7] text-white rounded-lg text-sm font-bold shadow-sm transition-colors">
                    <MaterialIcon icon="add" className="text-[20px]" />
                    <span className="whitespace-nowrap">Add Variable</span>
                </button>
            </div>
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
                <thead>
                    <tr className="bg-gray-50 dark:bg-[#1A2C38] border-b border-[#dbe2e6] dark:border-gray-700">
                        {['Variable Name', 'Data Type', 'Range / Values', 'Description'].map(h => (
                          <th key={h} className="py-3 px-6 text-xs font-semibold uppercase tracking-wider text-[#617c89] dark:text-gray-400">{h}</th>
                        ))}
                        <th className="py-3 px-6 text-xs font-semibold uppercase tracking-wider text-[#617c89] dark:text-gray-400 text-right">Actions</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-[#dbe2e6] dark:divide-gray-700 bg-white dark:bg-[#1A2C38]">
                    {data.length === 0 ? (
                         <tr><td colSpan={5} className="p-12 text-center text-gray-500">No variables found in the model.</td></tr>
                    ) : (
                        data.map((v, i) => (
                          <VariableTableRow 
                            key={i} 
                            variable={v}
                            onEdit={() => console.log('Edit', v.name)}
                            onDelete={() => console.log('Delete', v.name)}
                          />
                        ))
                    )}
                </tbody>
            </table>
        </div>
    </div>
  );
}