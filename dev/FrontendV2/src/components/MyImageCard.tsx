import React from 'react';

export interface MyImageCardProps {
  id: string | number;
  title: string;
  status: 'Solved' | 'Conflicts Found' | 'Processing' | 'Draft' | string;
  description: string;
  lastEdited: string;
  icon: string; // Material Symbol name (e.g., 'event_note')
  iconColorClass: string; // e.g., 'bg-blue-100 text-blue-600'
  statusColorClass: string; // e.g., 'bg-green-100 text-green-700'
  onView: (id: string | number) => void;
  onEdit: (id: string | number) => void;
}

const MyImageCard: React.FC<MyImageCardProps> = ({
  id,
  title,
  status,
  description,
  lastEdited,
  icon,
  iconColorClass,
  statusColorClass,
  onView,
  onEdit
}) => {
  return (
    <div className="group flex flex-col md:flex-row bg-surface-light dark:bg-[#1a2c38] rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-all duration-300 border border-transparent hover:border-primary/20 p-5 gap-4 md:gap-6 items-start md:items-center bg-white">
      
      {/* Icon Section */}
      <div className="flex-shrink-0">
        <div className={`size-12 md:size-14 rounded-full flex items-center justify-center ${iconColorClass}`}>
          <span className="material-symbols-outlined text-2xl md:text-3xl">{icon}</span>
        </div>
      </div>

      {/* Content Section */}
      <div className="flex-1 flex flex-col gap-1">
        <div className="flex items-center gap-2">
          <h3 className="text-[#111618] dark:text-white text-lg font-bold leading-tight">{title}</h3>
          <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${statusColorClass}`}>
            {status}
          </span>
        </div>
        <p className="text-gray-500 dark:text-gray-400 text-sm font-normal line-clamp-2">
          {description}
        </p>
        <p className="text-gray-400 dark:text-gray-500 text-xs mt-1">Last edited: {lastEdited}</p>
      </div>

      {/* Actions Section */}
      <div className="flex items-center gap-3 w-full md:w-auto mt-2 md:mt-0 border-t md:border-t-0 pt-4 md:pt-0 border-gray-100 dark:border-gray-700">
        <button 
          onClick={() => onView(id)}
          className="flex-1 md:flex-none flex items-center justify-center gap-2 h-10 px-4 rounded-lg bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 text-[#111618] dark:text-white text-sm font-medium transition-colors border border-gray-200 dark:border-gray-600"
        >
          <span className="material-symbols-outlined text-[18px]">visibility</span>
          <span className="whitespace-nowrap">View Details</span>
        </button>
        <button 
          onClick={() => onEdit(id)}
          className="flex-1 md:flex-none flex items-center justify-center gap-2 h-10 px-4 rounded-lg border border-blue-600 text-blue-600 hover:bg-blue-600 hover:text-white text-sm font-medium transition-colors"
        >
          <span className="material-symbols-outlined text-[18px]">edit</span>
          <span className="whitespace-nowrap">Edit Problem</span>
        </button>
      </div>
    </div>
  );
};

export default MyImageCard;