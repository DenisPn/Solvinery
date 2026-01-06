import MaterialIcon from '../ui/MaterialIcon';

// הגדרת המבנה של אובייקט Constraint Module
export interface ConstraintModuleData {
  id?: number | string;
  title: string;
  date: string;
  desc: string;
  count: number;
  icon: string;
  colorClasses: string; // מחרוזת ה-Tailwind לצבעים (רקע + טקסט)
}

interface ConstraintModuleCardProps {
  module: ConstraintModuleData;
  onEdit?: () => void;
  onDelete?: () => void;
}

export default function ConstraintModuleCard({ module, onEdit, onDelete }: ConstraintModuleCardProps) {
  return (
    <div className="group flex flex-col justify-between rounded-xl border border-[#dbe2e6] bg-white p-5 shadow-sm transition-all hover:border-[#13a4ec] hover:shadow-md dark:border-gray-700 dark:bg-[#233340] dark:hover:border-[#13a4ec]">
      <div>
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-3">
            {/* Icon Wrapper with Dynamic Colors */}
            <div className={`flex size-10 items-center justify-center rounded-lg ${module.colorClasses}`}>
              <MaterialIcon icon={module.icon} />
            </div>
            <div>
              <h3 className="text-base font-bold text-[#111618] dark:text-white group-hover:text-[#13a4ec] transition-colors">
                {module.title}
              </h3>
              <p className="text-xs text-gray-400 dark:text-gray-500">{module.date}</p>
            </div>
          </div>
        </div>
        
        <p className="mt-4 text-sm text-gray-600 dark:text-gray-300 line-clamp-2 min-h-[2.5em]">
          {module.desc}
        </p>
        
        <div className="mt-4 flex items-center gap-2">
          <span className="inline-flex items-center gap-1.5 rounded-full bg-gray-100 px-2.5 py-1 text-xs font-medium text-gray-700 dark:bg-gray-700 dark:text-gray-300">
            <MaterialIcon icon="format_list_bulleted" className="text-[16px]" />
            {module.count} Constraints
          </span>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="mt-6 flex gap-3 pt-4 border-t border-gray-100 dark:border-gray-600">
        <button 
          onClick={onEdit}
          className="flex flex-1 items-center justify-center gap-2 rounded-lg border border-gray-200 bg-white py-2 text-sm font-bold text-gray-700 hover:bg-gray-50 hover:text-[#13a4ec] dark:border-gray-600 dark:bg-[#2b3d4a] dark:text-gray-200 dark:hover:bg-gray-700 transition-colors"
        >
          <MaterialIcon icon="edit" className="text-[18px]" /> 
          Edit
        </button>
        <button 
          onClick={onDelete}
          className="flex flex-1 items-center justify-center gap-2 rounded-lg border border-gray-200 bg-white py-2 text-sm font-bold text-gray-700 hover:bg-red-50 hover:text-red-600 hover:border-red-200 dark:border-gray-600 dark:bg-[#2b3d4a] dark:text-gray-200 dark:hover:bg-gray-700 dark:hover:text-red-400 transition-colors"
        >
          <MaterialIcon icon="delete" className="text-[18px]" /> 
          Delete
        </button>
      </div>
    </div>
  );
}