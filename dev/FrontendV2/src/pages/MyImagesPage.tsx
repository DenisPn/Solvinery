import React from 'react';
import { useNavigate } from 'react-router-dom';

const MyImagesPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 dark:bg-gray-900 p-6 text-center">
      <div className="max-w-md w-full bg-white dark:bg-gray-800 shadow-lg rounded-xl p-8">
        {/* Icon */}
        <div className="text-6xl mb-6">
          🚧
        </div>

        {/* Title */}
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
          Under Construction
        </h1>

        {/* Description */}
        <p className="text-gray-600 dark:text-gray-300 mb-8">
          We are currently working on the <strong>My Images</strong> gallery. 
          Soon you will be able to view and manage your personal uploads here.
        </p>

        {/* Back to Home Button */}
        <div className="flex justify-center">
          <button 
            onClick={() => navigate('/')}
            className="flex min-w-[120px] cursor-pointer items-center justify-center overflow-hidden rounded-lg h-10 px-4 bg-transparent border border-gray-300 dark:border-gray-600 text-[#111618] dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-800 text-sm font-semibold leading-normal transition-colors"
          >
            Back to Home
          </button>
        </div>
      </div>
    </div>
  );
};

export default MyImagesPage;