import MaterialIcon from '../ui/MaterialIcon';

// הגדרת המבנה של אובייקט Set
export interface SetData {
  name: string;
  desc: string;
  members: string[];
  extraCount: number;
  totalCount: number;
}

interface SetTableRowProps {
  set: SetData;
  onEdit?: () => void;
  onDelete?: () => void;
}

export default function SetTableRow({ set, onEdit, onDelete }: SetTableRowProps) {
  return (
    <tr className="group hover:bg-slate-50 dark:hover:bg-[#1e2d3b] transition-colors border-b border-slate-200 dark:border-slate-700 last:border-0">
      
      {/* Checkbox Column */}
      <td className="px-6 py-4 whitespace-nowrap">
        <div className="flex items-center">
          <input 
            className="h-4 w-4 text-[#13a4ec] border-slate-300 rounded focus:ring-[#13a4ec] bg-white dark:bg-[#15232d] dark:border-slate-600" 
            type="checkbox"
          />
        </div>
      </td>

      {/* Name & Description Column */}
      <td className="px-6 py-4 whitespace-nowrap">
        <div className="text-sm font-medium text-slate-900 dark:text-white">{set.name}</div>
        <div className="text-xs text-slate-500 dark:text-slate-400">{set.desc}</div>
      </td>

      {/* Members Pills Column */}
      <td className="px-6 py-4">
        <div className="flex flex-wrap gap-2">
          {set.members.map((member, i) => (
            <span key={i} className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-[#13a4ec]/10 text-[#13a4ec] border border-[#13a4ec]/20">
              {member}
            </span>
          ))}
          {set.extraCount > 0 && (
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300">
              +{set.extraCount} others
            </span>
          )}
        </div>
      </td>

      {/* Count Column */}
      <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500 dark:text-slate-400">
        {set.totalCount}
      </td>

      {/* Actions Column */}
      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
        <button onClick={onEdit} className="text-slate-400 hover:text-[#13a4ec] transition-colors p-1">
          <MaterialIcon icon="edit" className="text-[20px]" />
        </button>
        <button onClick={onDelete} className="text-slate-400 hover:text-red-500 transition-colors p-1 ml-2">
          <MaterialIcon icon="delete" className="text-[20px]" />
        </button>
      </td>
    </tr>
  );
}