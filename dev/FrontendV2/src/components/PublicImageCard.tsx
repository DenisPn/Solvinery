import React from 'react';

// הגדרת טיפוס הנתונים שהכרטיס מקבל
interface PublicImageCardProps {
  id: number;
  title: string;
  description: string;
  industry: string;
  industryColor: string; // e.g., 'blue', 'green', 'gray'
  type: string;
  typeColor: string;    // e.g., 'purple', 'orange'
  author: string;
  date: string;
  complexity: string;
  complexityColor: string; // Tailwind class like 'text-[#13a4ec]'
  downloads: string;
  onViewDetails: (id: number) => void;
}

const PublicImageCard: React.FC<PublicImageCardProps> = ({
  id,
  title,
  description,
  industry,
  industryColor,
  type,
  typeColor,
  author,
  date,
  complexity,
  complexityColor,
  downloads,
  onViewDetails,
}) => {
  return (
    <div className="group bg-white dark:bg-gray-900 rounded-xl p-6 border border-[#f0f3f4] dark:border-gray-800 shadow-sm hover:shadow-md hover:border-[#13a4ec]/30 transition-all flex flex-col lg:flex-row items-start lg:items-center gap-6">
      <div className="flex-1 space-y-3">
        {/* Header: Title & Badges */}
        <div className="flex flex-wrap items-center gap-3">
          <h3 
            onClick={() => onViewDetails(id)}
            className="text-xl font-bold group-hover:text-[#13a4ec] transition-colors cursor-pointer"
          >
            {title}
          </h3>

          {/* Industry Badge */}
          {/* Note: In Tailwind, dynamic classes like `bg-${color}-50` need to be safe-listed or complete strings used. 
              For now keeping existing logic. */}
          <span className={`px-2 py-0.5 rounded bg-${industryColor}-50 text-${industryColor}-600 text-[10px] font-bold uppercase tracking-wider dark:bg-${industryColor}-900/30 dark:text-${industryColor}-400`}>
            {industry}
          </span>

          {/* Type Badge */}
          <span className={`px-2 py-0.5 rounded bg-${typeColor}-50 text-${typeColor}-600 text-[10px] font-bold uppercase tracking-wider dark:bg-${typeColor}-900/30 dark:text-${typeColor}-400`}>
            {type}
          </span>
        </div>

        {/* Description */}
        <p className="text-sm text-[#617c89] line-clamp-2 max-w-3xl">
          {description}
        </p>

        {/* Meta Data (Author, Date, Complexity) */}
        <div className="flex flex-wrap items-center gap-6 text-xs text-[#617c89]">
          <div className="flex items-center gap-1.5">
            <span className="material-symbols-outlined text-lg">person</span>
            <span>{author}</span>
          </div>
          <div className="flex items-center gap-1.5">
            <span className="material-symbols-outlined text-lg">calendar_today</span>
            <span>{date}</span>
          </div>
          <div className={`flex items-center gap-1.5 ${complexityColor}`}>
            <span className="material-symbols-outlined text-lg">bar_chart</span>
            <span className="font-bold">{complexity}</span>
          </div>
        </div>
      </div>

      {/* Action Section (Downloads & Button) */}
      <div className="flex items-center gap-4 w-full lg:w-auto pt-4 lg:pt-0 border-t lg:border-t-0 border-gray-100 dark:border-gray-800">
        <div className="flex flex-col items-center px-4 border-r border-gray-100 dark:border-gray-800">
          <span className="text-xs text-[#617c89] uppercase font-bold tracking-tighter">Downloads</span>
          <span className="text-lg font-bold">{downloads}</span>
        </div>
        <button 
          onClick={() => onViewDetails(id)}
          className="flex-1 lg:flex-none bg-[#13a4ec] text-white text-sm font-bold px-6 py-2.5 rounded-lg hover:bg-[#13a4ec]/90 transition-all shadow-sm shadow-[#13a4ec]/20 whitespace-nowrap"
        >
          View Details
        </button>
      </div>
    </div>
  );
};

export default PublicImageCard;