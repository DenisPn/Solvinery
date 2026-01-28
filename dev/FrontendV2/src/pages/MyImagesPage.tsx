import React from 'react';
import { useNavigate } from 'react-router-dom';
import MyImageCard, { type MyImageCardProps } from '../components/MyImageCard';

const MyImagesPage: React.FC = () => {
  const navigate = useNavigate();

  const handleView = (id: string | number) => {
    console.log('Navigating to view:', id);
    navigate(`/problems/${id}`);
  };

  const handleEdit = (id: string | number) => {
    console.log('Navigating to edit:', id);
    navigate(`/problems/${id}/edit`);
  };

  // Mock Data
  const myProjects: MyImageCardProps[] = [
    {
      id: 1,
      title: "Q3 Logistics Optimization",
      status: "Solved",
      description: "Optimizing delivery routes and staff allocation for the third quarter peak season. Includes 50 drivers and 200 delivery points.",
      lastEdited: "Oct 24, 2023",
      icon: "event_note",
      iconColorClass: "bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400",
      statusColorClass: "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400",
      onView: handleView,
      onEdit: handleEdit,
    },
    {
      id: 2,
      title: "University Course Timetable",
      status: "Conflicts Found",
      description: "Scheduling for the Science department, Fall 2024 semester. Constraints include professor availability and lab room capacity.",
      lastEdited: "Oct 22, 2023",
      icon: "warning",
      iconColorClass: "bg-orange-100 dark:bg-orange-900/30 text-orange-600 dark:text-orange-400",
      statusColorClass: "bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400",
      onView: handleView,
      onEdit: handleEdit,
    },
    {
      id: 3,
      title: "Nurse Shift Rotation - Nov",
      status: "Processing",
      description: "Monthly shift planning for the ICU ward. Balancing night shifts and weekend leaves for 30 staff members.",
      lastEdited: "Oct 20, 2023",
      icon: "schedule",
      iconColorClass: "bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400",
      statusColorClass: "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400",
      onView: handleView,
      onEdit: handleEdit,
    },
    {
      id: 4,
      title: "Factory Line A Maintenance",
      status: "Draft",
      description: "Scheduling preventive maintenance downtime without disrupting the main production quotas for Week 45.",
      lastEdited: "Oct 18, 2023",
      icon: "factory",
      iconColorClass: "bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400",
      statusColorClass: "bg-gray-200 text-gray-700 dark:bg-gray-700 dark:text-gray-300",
      onView: handleView,
      onEdit: handleEdit,
    },
    {
      id: 5,
      title: "Regional Soccer Tournament",
      status: "Solved",
      description: "Match scheduling for 16 teams across 4 venues over a single weekend. Constraints: minimal travel time between venues.",
      lastEdited: "Oct 15, 2023",
      icon: "sports_soccer",
      iconColorClass: "bg-teal-100 dark:bg-teal-900/30 text-teal-600 dark:text-teal-400",
      statusColorClass: "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400",
      onView: handleView,
      onEdit: handleEdit,
    },
  ];

  return (
    <div className="bg-[#f6f7f8] dark:bg-[#101c22] text-[#111618] dark:text-white font-sans min-h-screen flex flex-col overflow-x-hidden">
      
      {/* Main Content Area */}
      <main className="flex-1 flex justify-center py-8 px-4 sm:px-8 lg:px-20">
        <div className="w-full max-w-[1200px] flex flex-col gap-6">
          
          {/* Page Title & Create Button */}
          <div className="flex flex-col md:flex-row flex-wrap justify-between items-start md:items-center gap-4">
            <div>
              <h1 className="text-[#111618] dark:text-white text-3xl md:text-4xl font-black leading-tight tracking-[-0.033em]">My Scheduling Problems</h1>
              <p className="text-gray-500 dark:text-gray-400 mt-1 text-sm">Manage and solve your scheduling challenges in one place</p>
            </div>
            <button 
              onClick={() => navigate('/new')}
              className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg h-10 px-5 text-sm font-bold leading-normal tracking-[0.015em] transition-all shadow-md hover:shadow-lg"
            >
              <span className="material-symbols-outlined text-[20px]">add_circle</span>
              <span className="truncate">Create New Problem</span>
            </button>
          </div>

          {/* Filters & Sort */}
          <div className="flex flex-wrap items-center gap-3">
            <button className="flex h-9 items-center justify-center gap-x-2 rounded-full bg-blue-600 text-white px-5 transition-colors shadow-sm">
              <span className="text-sm font-medium leading-normal">All</span>
            </button>
            <button className="flex h-9 items-center justify-center gap-x-2 rounded-full bg-white dark:bg-[#1a2c38] border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 text-[#111618] dark:text-gray-200 px-5 transition-colors">
              <span className="text-sm font-medium leading-normal">Solved</span>
            </button>
            <button className="flex h-9 items-center justify-center gap-x-2 rounded-full bg-white dark:bg-[#1a2c38] border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 text-[#111618] dark:text-gray-200 px-5 transition-colors">
              <span className="text-sm font-medium leading-normal">In Progress</span>
            </button>
            
            <div className="mr-auto ml-0 md:ml-auto md:mr-0 w-full md:w-auto mt-2 md:mt-0">
              <button className="flex items-center gap-2 text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-blue-600">
                <span className="material-symbols-outlined text-[20px]">sort</span>
                Sort by Date
              </button>
            </div>
          </div>

          {/* Projects List */}
          <div className="flex flex-col gap-4">
            {myProjects.map((project) => (
              <MyImageCard 
                key={project.id}
                {...project}
              />
            ))}
          </div>

          {/* Pagination */}
          <div className="flex items-center justify-center py-6 mt-4">
            <div className="flex items-center gap-1">
              <button className="flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
                <span className="material-symbols-outlined text-[20px]">chevron_left</span>
              </button>
              <button className="text-sm font-bold leading-normal flex size-9 items-center justify-center text-white rounded-lg bg-blue-600 shadow-md">1</button>
              <button className="text-sm font-normal leading-normal flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">2</button>
              <button className="text-sm font-normal leading-normal flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">3</button>
              <span className="flex items-center justify-center w-8 text-gray-400">...</span>
              <button className="text-sm font-normal leading-normal flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">5</button>
              <button className="flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
                <span className="material-symbols-outlined text-[20px]">chevron_right</span>
              </button>
            </div>
          </div>

        </div>
      </main>
    </div>
  );
};

export default MyImagesPage;