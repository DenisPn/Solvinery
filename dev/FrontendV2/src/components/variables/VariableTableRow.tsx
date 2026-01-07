import MaterialIcon from '../ui/MaterialIcon';

export interface VariableData {
  name: string;
  type: string;
  alias?: string;
  objectiveValueAlias?: string;
  desc?: string;
}

interface VariableTableRowProps {
  variable: VariableData;
  onEdit?: () => void;
  onDelete?: () => void;
}

export default function VariableTableRow({ variable, onEdit, onDelete }: VariableTableRowProps) {
  
  const renderStructureBadges = (typeStr: string) => {
    // ניקוי המחרוזת: הסרת סוגריים מרובעים וחלוקה
    const parts = typeStr.replace(/[\[\]]/g, '').split(',').map(s => s.trim()).filter(s => s);

    return (
      <div className="flex flex-wrap gap-2">
        {parts.map((part, index) => {
          let label = part;
          // צבע ברירת מחדל (אפור)
          let colorClass = "bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300 border border-gray-200 dark:border-gray-700"; 

          const upperPart = part.toUpperCase();
          
          // --- התאמת הצבעים לתמונה ---
          
          // 1. Integer -> Green
          if (upperPart === 'INT' || upperPart === 'INTEGER') {
            label = 'Integer';
            colorClass = "bg-emerald-50 text-emerald-700 border border-emerald-100 dark:bg-emerald-500/10 dark:text-emerald-400 dark:border-emerald-500/20";
          } 
          // 2. String -> Blue
          else if (upperPart === 'TEXT' || upperPart === 'STRING') {
            label = 'String';
            colorClass = "bg-blue-50 text-blue-700 border border-blue-100 dark:bg-blue-500/10 dark:text-blue-400 dark:border-blue-500/20";
          } 
          // 3. Boolean -> Orange
          else if (upperPart === 'BOOL' || upperPart === 'BOOLEAN') {
            label = 'Boolean';
            colorClass = "bg-orange-50 text-orange-700 border border-orange-100 dark:bg-orange-500/10 dark:text-orange-400 dark:border-orange-500/20";
          }
          // 4. Float -> Teal
          else if (upperPart === 'FLOAT') {
            label = 'Float';
            colorClass = "bg-teal-50 text-teal-700 border border-teal-100 dark:bg-teal-500/10 dark:text-teal-400 dark:border-teal-500/20";
          }
          // 5. Array -> Purple
          else if (upperPart.includes('ARRAY') || upperPart === 'ARR') {
            label = 'Array';
            colorClass = "bg-purple-50 text-purple-700 border border-purple-100 dark:bg-purple-500/10 dark:text-purple-400 dark:border-purple-500/20";
          }
          // 6. Set -> Sky (Primary)
          else if (upperPart.includes('SET')) {
            label = 'Set';
            colorClass = "bg-[#13a4ec]/10 text-[#13a4ec] border border-[#13a4ec]/20";
          }
          // 7. Decision -> Slate
          else if (upperPart.includes('DECISION')) {
            label = 'Decision';
            colorClass = "bg-slate-100 text-slate-700 border border-slate-200 dark:bg-slate-700/50 dark:text-slate-300 dark:border-slate-600";
          }
          // 8. Date -> Pink
          else if (upperPart.includes('DATE')) {
            label = 'Date';
            colorClass = "bg-pink-50 text-pink-700 border border-pink-100 dark:bg-pink-500/10 dark:text-pink-400 dark:border-pink-500/20";
          }

          return (
            <span key={index} className={`inline-flex items-center rounded-md px-2.5 py-1 text-xs font-medium ${colorClass}`}>
              {label}
            </span>
          );
        })}
      </div>
    );
  };

  return (
    <tr className="group hover:bg-gray-50 dark:hover:bg-[#1e2d3b]/50 transition-colors border-b border-[#dbe2e6] dark:border-[#2a3b45]">
      
      {/* Name */}
      <td className="py-4 px-6">
        <div className="font-mono text-sm font-medium text-[#111618] dark:text-[#e0e6e9]">
          {variable.name}
        </div>
      </td>

      {/* Structure (Badges) */}
      <td className="py-4 px-6">
        <div className="flex items-center gap-2">
          {renderStructureBadges(variable.type)}
        </div>
      </td>

      {/* Alias */}
      <td className="py-4 px-6">
        <span className="text-sm text-[#111618] dark:text-[#e0e6e9]">
          {variable.alias || '-'}
        </span>
      </td>

      {/* Objective Value Alias */}
      <td className="py-4 px-6">
        <span className="font-mono text-sm text-[#617c89] dark:text-[#9aaeb5]">
          {variable.objectiveValueAlias ? `"${variable.objectiveValueAlias}"` : '-'}
        </span>
      </td>

      {/* Actions */}
      <td className="py-4 px-6 text-right">
        <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
          <button 
            onClick={onEdit} 
            className="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-700 text-[#617c89] dark:text-[#9aaeb5] hover:text-[#13a4ec] transition-colors"
          >
            <MaterialIcon icon="edit" className="text-[20px]" />
          </button>
          <button 
            onClick={onDelete} 
            className="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-700 text-[#617c89] dark:text-[#9aaeb5] hover:text-red-500 transition-colors"
          >
            <MaterialIcon icon="delete" className="text-[20px]" />
          </button>
        </div>
      </td>
    </tr>
  );
}