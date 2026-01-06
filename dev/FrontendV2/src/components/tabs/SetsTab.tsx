import MaterialIcon from '../ui/MaterialIcon';
import SetTableRow, { type SetData } from '../sets/SetTableRow';

interface SetsTabProps {
  data: SetData[];
}

export default function SetsTab({ data }: SetsTabProps) {
  return (
    <div className="bg-white dark:bg-[#15232d] rounded-xl shadow-sm border border-slate-100 dark:border-slate-800 p-6 sm:p-8 flex flex-col gap-6 min-h-[600px]">
      
      {/* Headline & Description */}
      <div>
        <h2 className="text-[#111618] dark:text-white text-2xl font-bold leading-tight mb-2">Define Sets</h2>
        <p className="text-slate-600 dark:text-slate-300 text-base font-normal leading-normal max-w-2xl">
          Groups of entities extracted from your ZPL model.
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
              {data.length === 0 ? (
                  <tr>
                      <td colSpan={5} className="py-12 text-center text-slate-500 dark:text-slate-400">
                          No sets found. Upload a ZPL file to see data.
                      </td>
                  </tr>
              ) : (
                  data.map((set, index) => (
                    <SetTableRow 
                      key={index} 
                      set={set} 
                      onEdit={() => console.log('Edit', set.name)}
                      onDelete={() => console.log('Delete', set.name)}
                    />
                  ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}