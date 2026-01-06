import MaterialIcon from '../ui/MaterialIcon';

// הגדרת המבנה של אובייקט Variable
export interface VariableData {
  id?: number | string; // אופציונלי, לעתיד
  name: string;
  type: string;
  values: string;
  desc: string;
  color: 'purple' | 'blue' | 'green' | 'red' | 'orange' | 'gray'; // הגדרת צבעים מותרים
}

interface VariableTableRowProps {
  variable: VariableData;
  onEdit?: () => void;
  onDelete?: () => void;
}

export default function VariableTableRow({ variable, onEdit, onDelete }: VariableTableRowProps) {
  
  // פונקציית עזר לבחירת עיצוב התגית לפי הצבע
  const getBadgeStyle = (color: string) => {
    switch (color) {
      case 'purple': return 'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-300';
      case 'blue': return 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300';
      case 'green': return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300';
      case 'red': return 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300';
      case 'orange': return 'bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400';
      default: return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300';
    }
  };

  return (
    <tr className="group hover:bg-slate-50 dark:hover:bg-[#1e2d3b] transition-colors border-b border-slate-200 dark:border-slate-700 last:border-0">
      
      {/* Variable Name */}
      <td className="py-4 px-6">
          <div className="font-mono text-sm font-medium text-[#111618] dark:text-white">{variable.name}</div>
      </td>

      {/* Data Type (Badge) */}
      <td className="py-4 px-6">
          <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${getBadgeStyle(variable.color)}`}>
              {variable.type}
          </span>
      </td>

      {/* Range / Values */}
      <td className="py-4 px-6">
          <span className="text-sm text-[#111618] dark:text-white">{variable.values}</span>
      </td>

      {/* Description */}
      <td className="py-4 px-6">
          <span className="text-sm text-[#617c89] dark:text-gray-400 line-clamp-1">{variable.desc}</span>
      </td>

      {/* Actions */}
      <td className="py-4 px-6 text-right">
          <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
              <button onClick={onEdit} className="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-700 text-[#617c89] dark:text-gray-400 hover:text-[#13a4ec] transition-colors">
                  <MaterialIcon icon="edit" className="text-[20px]" />
              </button>
              <button onClick={onDelete} className="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-700 text-[#617c89] dark:text-gray-400 hover:text-red-500 transition-colors">
                  <MaterialIcon icon="delete" className="text-[20px]" />
              </button>
          </div>
      </td>
    </tr>
  );
}