import React from 'react';
import MaterialIcon from '../ui/MaterialIcon';

export default function VariablesTab() {
  const variables = [
    { name: 'nurse_shift_A', type: 'Boolean', values: 'True / False', desc: 'Availability for morning shift block', color: 'purple' },
    { name: 'start_time_block', type: 'Integer', values: '0 - 24', desc: 'Starting hour for the primary task', color: 'blue' },
    { name: 'resource_allocation', type: 'Float', values: '0.0 - 1.0', desc: 'Percentage of total resources used', color: 'green' },
    { name: 'max_capacity_limit', type: 'Integer', values: '100 - 5000', desc: 'Hard limit on production units per day', color: 'blue' },
    { name: 'weekend_penalty_multiplier', type: 'Float', values: '1.0 - 2.5', desc: 'Cost multiplier for overtime on Sat/Sun', color: 'green' },
  ];

  return (
    <div className="flex flex-col bg-white dark:bg-[#1A2C38] rounded-xl shadow-sm border border-[#dbe2e6] dark:border-gray-700 overflow-hidden min-h-[600px]">
        {/* Toolbar */}
        <div className="p-4 border-b border-[#dbe2e6] dark:border-gray-700 flex flex-col md:flex-row gap-4 justify-between items-start md:items-center bg-gray-50/50 dark:bg-[#1A2C38]">
            <div className="w-full md:w-auto md:flex-1 md:max-w-md">
                <div className="flex w-full items-center rounded-lg h-10 border border-[#dbe2e6] dark:border-gray-600 bg-white dark:bg-[#233340] focus-within:ring-1 focus-within:ring-[#13a4ec] focus-within:border-[#13a4ec] transition-all">
                    <div className="text-[#617c89] dark:text-gray-400 flex items-center justify-center pl-3">
                        <MaterialIcon icon="search" className="text-[20px]" />
                    </div>
                    <input className="w-full min-w-0 flex-1 bg-transparent border-none text-[#111618] dark:text-white focus:outline-none placeholder:text-[#617c89]/70 dark:placeholder:text-gray-500 px-3 text-sm font-normal" placeholder="Search variables by name..." />
                </div>
            </div>
            <div className="flex gap-3 w-full md:w-auto">
                <button className="flex items-center gap-2 px-3 py-2 bg-white dark:bg-[#233340] border border-[#dbe2e6] dark:border-gray-600 rounded-lg text-sm font-medium text-[#617c89] dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                    <MaterialIcon icon="filter_list" className="text-[20px]" />
                    Filter
                </button>
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
                    {variables.map((v, i) => (
                      <tr key={i} className="group hover:bg-gray-50 dark:hover:bg-[#233340] transition-colors">
                          <td className="py-4 px-6">
                              <div className="font-mono text-sm font-medium text-[#111618] dark:text-white">{v.name}</div>
                          </td>
                          <td className="py-4 px-6">
                              <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium 
                                ${v.color === 'purple' ? 'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-300' : ''}
                                ${v.color === 'blue' ? 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300' : ''}
                                ${v.color === 'green' ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300' : ''}
                              `}>
                                  {v.type}
                              </span>
                          </td>
                          <td className="py-4 px-6">
                              <span className="text-sm text-[#111618] dark:text-white">{v.values}</span>
                          </td>
                          <td className="py-4 px-6">
                              <span className="text-sm text-[#617c89] dark:text-gray-400 line-clamp-1">{v.desc}</span>
                          </td>
                          <td className="py-4 px-6 text-right">
                              <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                  <button className="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-700 text-[#617c89] dark:text-gray-400 hover:text-[#13a4ec] transition-colors">
                                      <MaterialIcon icon="edit" className="text-[20px]" />
                                  </button>
                                  <button className="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-700 text-[#617c89] dark:text-gray-400 hover:text-red-500 transition-colors">
                                      <MaterialIcon icon="delete" className="text-[20px]" />
                                  </button>
                              </div>
                          </td>
                      </tr>
                    ))}
                </tbody>
            </table>
        </div>

        {/* Pagination & Info */}
        <div className="mt-auto">
            <div className="px-6 py-4 border-t border-[#dbe2e6] dark:border-gray-700 bg-white dark:bg-[#1A2C38] flex items-center justify-between">
                <div className="text-sm text-[#617c89] dark:text-gray-400">
                    Showing <span className="font-medium text-[#111618] dark:text-white">1</span> to <span className="font-medium text-[#111618] dark:text-white">5</span> of <span className="font-medium text-[#111618] dark:text-white">12</span> results
                </div>
                <div className="flex items-center gap-2">
                    <button className="p-2 rounded-lg border border-[#dbe2e6] dark:border-gray-600 text-[#617c89] dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50">
                        <MaterialIcon icon="chevron_left" className="text-[20px]" />
                    </button>
                    <button className="p-2 rounded-lg border border-[#dbe2e6] dark:border-gray-600 text-[#617c89] dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700">
                        <MaterialIcon icon="chevron_right" className="text-[20px]" />
                    </button>
                </div>
            </div>
            
            <div className="mx-4 mb-4 mt-2 p-4 bg-[#13a4ec]/10 dark:bg-[#13a4ec]/5 rounded-lg border border-[#13a4ec]/20 flex gap-4 items-start">
                <MaterialIcon icon="info" className="text-[#13a4ec] mt-0.5" />
                <div className="flex flex-col gap-1">
                    <h3 className="text-sm font-bold text-[#111618] dark:text-white">Defining Variables</h3>
                    <p className="text-sm text-[#617c89] dark:text-gray-400">
                        Variables represent the decisions your model needs to make. Ensure that variable names are unique and ranges are physically possible within your scheduling constraints.
                    </p>
                </div>
            </div>
        </div>
    </div>
  );
}