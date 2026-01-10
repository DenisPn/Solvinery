import MaterialIcon from '../ui/MaterialIcon';

export interface ParameterData {
  name: string;
  type: string;
}

interface ParametersTabProps {
  data: ParameterData[];
}

export default function ParametersTab({ data }: ParametersTabProps) {
  return (
    <div className="bg-white dark:bg-[#1A2C38] rounded-xl border border-[#dbe2e6] dark:border-gray-700 shadow-sm flex flex-col min-h-[600px] overflow-hidden">
        {/* Toolbar */}
        <div className="p-5 border-b border-[#f0f3f4] dark:border-gray-700 bg-gray-50/50 dark:bg-[#1A2C38]">
             <div className="flex items-center gap-2 mb-1">
                <MaterialIcon icon="tune" className="text-[#13a4ec]" />
                <h3 className="text-lg font-bold text-[#111618] dark:text-white">Global Parameters</h3>
             </div>
             <p className="text-sm text-gray-500 dark:text-gray-400">System-wide constants defined in your ZPL model.</p>
        </div>

        {/* Table */}
        <div className="overflow-x-auto flex-1">
            <table className="w-full text-left border-collapse">
                <thead>
                    <tr className="bg-gray-50 dark:bg-[#233340] border-b border-[#dbe2e6] dark:border-gray-700">
                        <th className="py-3 px-6 text-xs font-semibold uppercase tracking-wider text-[#617c89] dark:text-gray-400">Parameter Name</th>
                        <th className="py-3 px-6 text-xs font-semibold uppercase tracking-wider text-[#617c89] dark:text-gray-400">Type</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-[#dbe2e6] dark:divide-gray-700 bg-white dark:bg-[#1A2C38]">
                    {data.length === 0 ? (
                        <tr>
                            <td colSpan={2} className="py-12 text-center text-gray-500 dark:text-gray-400">
                                <div className="flex flex-col items-center gap-2">
                                    <MaterialIcon icon="data_array" className="text-4xl text-gray-300" />
                                    <p>No parameters found in the model.</p>
                                </div>
                            </td>
                        </tr>
                    ) : (
                        data.map((param, index) => (
                            <tr key={index} className="hover:bg-gray-50 dark:hover:bg-[#1e2d3b] transition-colors">
                                <td className="py-4 px-6 font-mono text-sm font-medium text-[#111618] dark:text-white">{param.name}</td>
                                <td className="py-4 px-6">
                                    <span className="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300 border border-gray-200 dark:border-gray-600">
                                        {param.type}
                                    </span>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    </div>
  );
}