import React, { useState } from 'react';
import MaterialIcon from '../ui/MaterialIcon'; 
import type { ModelPayload } from '../../types/apiTypes';
import { ImageService } from '../../services/ImageService'; // הייבוא החדש

interface ReviewTabProps {
  userId: string;
  data: ModelPayload;
}

const ReviewTab: React.FC<ReviewTabProps> = ({ userId, data }) => {
  const [loading, setLoading] = useState(false);

  const handleSend = async () => {
    setLoading(true);
    try {
      // שימוש ב-Service הנקי
      await ImageService.createImage(userId, data);
      alert("Success! Image created successfully.");
    } catch (error) {
      alert("Error: Failed to submit data. Check console.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center w-full max-w-3xl mx-auto p-8 bg-white dark:bg-[#1A2C38] rounded-xl border border-[#dbe2e6] dark:border-gray-700 shadow-sm mt-6">
      
      <div className="mb-8 text-center">
        <h2 className="text-2xl font-bold text-[#111618] dark:text-white mb-3">Review & Submit</h2>
        <p className="text-[#617c89] dark:text-gray-400 max-w-md mx-auto">
          Please review the configuration summary below before creating the new image model.
        </p>
      </div>

      <div className="w-full bg-gray-50 dark:bg-gray-900 rounded-lg p-6 border border-gray-200 dark:border-gray-700 mb-8 font-mono text-sm shadow-inner">
        <div className="flex justify-between items-center border-b border-gray-200 dark:border-gray-700 pb-3 mb-3">
            <span className="font-bold text-gray-700 dark:text-gray-300 uppercase tracking-wide text-xs">Model Configuration</span>
            <span className="text-[#13a4ec] font-bold">{data.name}</span>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-y-3 gap-x-8 text-gray-600 dark:text-gray-400">
            <div className="flex justify-between"><span>Variables:</span> <span className="font-bold text-gray-800 dark:text-gray-200">{data.variables.length}</span></div>
            <div className="flex justify-between"><span>Constraints:</span> <span className="font-bold text-gray-800 dark:text-gray-200">{data.constraintModules.length}</span></div>
            <div className="flex justify-between"><span>Sets:</span> <span className="font-bold text-gray-800 dark:text-gray-200">{data.sets.length}</span></div>
            <div className="flex justify-between"><span>Parameters:</span> <span className="font-bold text-gray-800 dark:text-gray-200">{data.parameters.length}</span></div>
        </div>
      </div>

      <button
        onClick={handleSend}
        disabled={loading}
        className={`
            flex items-center justify-center gap-2 rounded-lg h-12 px-10 text-base font-bold text-white shadow-lg transition-all transform active:scale-95
            ${loading ? 'bg-gray-400 cursor-not-allowed opacity-70' : 'bg-[#4CAF50] hover:bg-[#43a047] shadow-green-500/30'}
        `}
      >
        {loading ? (
            <>
               <span className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></span>
               Processing...
            </>
        ) : (
            <>
                <MaterialIcon icon="cloud_upload" className="text-xl" />
                CREATE IMAGE
            </>
        )}
      </button>
    </div>
  );
};

export default ReviewTab;