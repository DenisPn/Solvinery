import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MyImageCard, { type MyImageCardProps } from '../components/MyImageCard';
import { useAuth } from '../context/AuthContext';
import { ImageService } from '../services/ImageService';

const MyImagesPage: React.FC = () => {
  const navigate = useNavigate();
  const { userId, isAuthenticated } = useAuth();

  // State management
  const [projects, setProjects] = useState<MyImageCardProps[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Pagination state
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const handleView = (id: string | number) => {
    console.log('Navigating to view:', id);
    navigate(`/problems/${id}`);
  };

  const handleEdit = (id: string | number) => {
    console.log('Navigating to edit:', id);
    navigate(`/problems/${id}/edit`);
  };

  const fetchUserImages = async () => {
    if (!isAuthenticated || !userId) {
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const data = await ImageService.getUserImages(userId, {
        page: currentPage,
        size: 10
      });

      // המרת המידע מהשרת למבנה שהכרטיס מצפה לקבל
      const mappedProjects: MyImageCardProps[] = Object.entries(data.images).map(([id, dto]) => {
        return {
          id: id,
          title: dto.name || "Untitled Problem",
          description: dto.description || "No description provided.",
          lastEdited: dto.creationDate || "Unknown date",
          
          // שדות UI שאינם מגיעים מהשרת (Hardcoded / Generated)
          status: "Saved", 
          icon: "description", // אייקון ברירת מחדל
          
          // צבעים אקראיים או קבועים כדי שהעיצוב יראה חי
          iconColorClass: "bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400",
          statusColorClass: "bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300",
          
          onView: handleView,
          onEdit: handleEdit,
        };
      });

      setProjects(mappedProjects);
      setTotalPages(data.totalPages);

    } catch (err) {
      console.error(err);
      setError("Failed to load your problems.");
    } finally {
      setLoading(false);
    }
  };

  // שליפת הנתונים בטעינה ראשונית או כשעוברים עמוד
  useEffect(() => {
    fetchUserImages();
  }, [userId, currentPage]); // ירוץ מחדש אם המשתמש משתנה או העמוד

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) setCurrentPage(prev => prev + 1);
  };

  const handlePrevPage = () => {
    if (currentPage > 0) setCurrentPage(prev => prev - 1);
  };

  if (!isAuthenticated) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#f6f7f8] dark:bg-[#101c22]">
        <div className="text-center">
          <h2 className="text-xl font-bold text-gray-700 dark:text-gray-200">Please log in to view your problems.</h2>
          <button 
            onClick={() => navigate('/auth')}
            className="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Go to Login
          </button>
        </div>
      </div>
    );
  }

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
            
            <div className="mr-auto ml-0 md:ml-auto md:mr-0 w-full md:w-auto mt-2 md:mt-0">
              <button 
                onClick={fetchUserImages} // כפתור רענון זמני
                className="flex items-center gap-2 text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-blue-600"
              >
                <span className="material-symbols-outlined text-[20px]">refresh</span>
                Refresh
              </button>
            </div>
          </div>

          {/* Projects List */}
          <div className="flex flex-col gap-4">
            {loading && <div className="text-center py-10 text-gray-500">Loading your problems...</div>}
            
            {error && <div className="text-center py-10 text-red-500">{error}</div>}

            {!loading && !error && projects.length === 0 && (
                <div className="text-center py-12 bg-white dark:bg-[#1a2c38] rounded-xl border border-dashed border-gray-300 dark:border-gray-700">
                    <p className="text-gray-500 dark:text-gray-400 mb-4">You haven't created any problems yet.</p>
                    <button 
                        onClick={() => navigate('/new')}
                        className="text-blue-600 font-bold hover:underline"
                    >
                        Create your first problem
                    </button>
                </div>
            )}

            {!loading && !error && projects.map((project) => (
              <MyImageCard 
                key={project.id}
                {...project}
              />
            ))}
          </div>

          {/* Pagination */}
          {!loading && projects.length > 0 && (
            <div className="flex items-center justify-center py-6 mt-4 gap-4">
               <button 
                onClick={handlePrevPage}
                disabled={currentPage === 0}
                className="flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                <span className="material-symbols-outlined text-[20px]">chevron_left</span>
              </button>
              
              <span className="text-sm font-medium">Page {currentPage + 1} of {totalPages}</span>

              <button 
                onClick={handleNextPage}
                disabled={currentPage >= totalPages - 1}
                className="flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                <span className="material-symbols-outlined text-[20px]">chevron_right</span>
              </button>
            </div>
          )}

        </div>
      </main>
    </div>
  );
};

export default MyImagesPage;