import React from 'react';

export interface CommunityProjectCardProps {
  id: string;
  title: string;
  owner: string;
  members: number;
  lastActive: string;
  role: 'OWNER' | 'VISITOR' | string;
  onEnter: (id: string) => void;
}

const CommunityProjectCard: React.FC<CommunityProjectCardProps> = ({
  id,
  title,
  owner,
  members,
  lastActive,
  role,
  onEnter
}) => {
  const isOwner = role === 'OWNER';

  // הגדרת צבעים דינמית לפי התפקיד
  const theme = isOwner 
    ? {
        border: 'bg-[#13a4ec]',
        iconBg: 'bg-blue-100',
        iconText: 'text-blue-600',
        badgeText: 'text-blue-600'
      }
    : {
        border: 'bg-purple-500',
        iconBg: 'bg-purple-100',
        iconText: 'text-purple-600',
        badgeText: 'text-purple-600'
      };

  return (
    <div className="bg-white dark:bg-[#1a2c38] p-6 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
      
      {/* פס צבעוני בצד */}
      <div className={`absolute left-0 top-0 bottom-0 w-1 ${theme.border}`}></div>

      <div className="flex justify-between items-start mb-4 pl-3">
        {/* אייקון האות הראשונה */}
        <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold ${theme.iconBg} ${theme.iconText}`}>
          {title.charAt(0).toUpperCase()}
        </div>
        
        {/* תגית כמות חברים */}
        <span className="bg-gray-100 dark:bg-gray-700 text-xs px-2 py-1 rounded-full font-medium text-gray-600 dark:text-gray-300">
          {members} Members
        </span>
      </div>
      
      <div className="pl-3">
        <h3 className="text-xl font-bold mb-1 text-[#111618] dark:text-white group-hover:text-[#13a4ec] transition-colors">
          {title}
        </h3>
        
        <p className="text-sm text-gray-500 mb-1">
          Owner: <span className="font-medium text-gray-700 dark:text-gray-300">{owner}</span>
        </p>
        
        <p className="text-xs text-gray-400 mb-4">
          Active {lastActive}
        </p>
        
        <div className="flex items-center justify-between mt-4 pt-4 border-t border-gray-100 dark:border-gray-700">
            <span className={`text-xs font-bold uppercase tracking-wider ${theme.badgeText}`}>
                {role} View
            </span>
            
            <button 
                onClick={() => onEnter(id)}
                className="px-4 py-2 bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 text-gray-700 dark:text-gray-200 text-sm font-bold rounded-lg transition-colors border border-gray-200 dark:border-gray-600 flex items-center gap-2"
            >
                <span>Open</span>
                <span className="material-symbols-outlined text-[16px]">arrow_forward</span>
            </button>
        </div>
      </div>
    </div>
  );
};

export default CommunityProjectCard;