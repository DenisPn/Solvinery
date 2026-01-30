import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MyImageCard, { type MyImageCardProps } from '../components/MyImageCard';
import { useAuth } from '../context/AuthContext';
import { ImageService } from '../services/ImageService';
import { SolverService } from '../services/SolverService';
import type { SolverConfig } from '../types/apiTypes';
import MyProblemDetailsModal from '../components/modals/MyProblemDetailsModal';

// אנו שומרים ב-State רק את המידע היבש, ללא הפונקציות
type ProjectData = Omit<MyImageCardProps, 'onView' | 'onEdit'>;

const MyImagesPage: React.FC = () => {
  const navigate = useNavigate();
  const { userId, isAuthenticated } = useAuth();

  // --- State ---
  const [projects, setProjects] = useState<ProjectData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Pagination
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Modal State
  const [selectedProject, setSelectedProject] = useState<MyImageCardProps | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  // Solver Loading State
  const [isSolving, setIsSolving] = useState(false);

  // --- Handlers (מוגדרים מחדש בכל רינדור) ---

  const handleView = (id: string | number) => {
    // המרת ID ל-String לצורך השוואה בטוחה
    const project = projects.find(p => String(p.id) === String(id));
    
    if (project) {
        console.log('✅ Found project, opening modal:', project.title);
        // שחזור האובייקט המלא עבור המודל (כולל הפונקציות)
        setSelectedProject({
            ...project,
            onView: handleView,
            onEdit: handleEdit
        });
        setIsModalOpen(true);
    } else {
        console.warn('❌ Project not found for ID:', id);
    }
  };

  const handleEdit = (id: string | number) => {
    console.log('Navigating to edit:', id);
    navigate(`/problems/${id}/edit`);
  };

  const handleSolve = async (id: string | number) => {
    if (!userId) {
        alert("User ID missing. Please log in again.");
        return;
    }

    // ✅ תיקון: שליחת קונפיגורציה ריקה כדי למנוע שגיאות על שמות מודולים שלא קיימים בשרת
    // בעתיד: נצטרך לשלוף את שמות המודולים האמיתיים של הבעיה לפני השליחה
    const safeConfig: SolverConfig = {
      preferenceModulesScalars: {}, // שליחת אובייקט ריק
      enabledConstraintModules: [], // שליחת מערך ריק
      timeout: 30
    };

    setIsSolving(true);
    try {
      console.log(`🚀 Starting solver for problem ${id}...`);
      
      // שליחת הבקשה לשרת
      const solutionData = await SolverService.solveImage(userId, String(id), safeConfig);
      
      console.log("✅ Solution received:", solutionData);
      
      // סגירת המודל
      setIsModalOpen(false);
      
      // ניווט לדף הפתרון עם המידע שהתקבל
      navigate(`/problems/${id}/solution`, { 
        state: { 
          solutionData: solutionData,
          problemTitle: selectedProject?.title || "Unknown Problem"
        } 
      });

    } catch (err) {
      console.error("Failed to solve:", err);
      // ההודעה תופיע עכשיו ברור יותר בקונסול בזכות התיקון בסרביס
      alert("Failed to solve. Please check the console log for the specific server error.");
    } finally {
      setIsSolving(false);
    }
  };

  // --- Fetch Data ---

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

      // המרת המידע מהשרת (ללא הפונקציות)
      const mappedProjects: ProjectData[] = Object.entries(data.images).map(([id, dto]) => {
        return {
          id: id,
          title: dto.name || "Untitled Problem",
          description: dto.description || "No description provided.",
          lastEdited: dto.creationDate || "Unknown date",
          status: "Saved", 
          icon: "description",
          iconColorClass: "bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400",
          statusColorClass: "bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300",
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

  useEffect(() => {
    fetchUserImages();
  }, [userId, currentPage]);

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) setCurrentPage(prev => prev + 1);
  };

  const handlePrevPage = () => {
    if (currentPage > 0) setCurrentPage(prev => prev - 1);
  };

  // --- Render ---

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
          
          {/* Header */}
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

          {/* Filters & Refresh */}
          <div className="flex flex-wrap items-center gap-3">
            <button className="flex h-9 items-center justify-center gap-x-2 rounded-full bg-blue-600 text-white px-5 transition-colors shadow-sm">
              <span className="text-sm font-medium leading-normal">All</span>
            </button>
            <div className="mr-auto ml-0 md:ml-auto md:mr-0 w-full md:w-auto mt-2 md:mt-0">
              <button 
                onClick={fetchUserImages}
                className="flex items-center gap-2 text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-blue-600"
              >
                <span className="material-symbols-outlined text-[20px]">refresh</span>
                Refresh
              </button>
            </div>
          </div>

          {/* Cards List */}
          <div className="flex flex-col gap-4">
            {loading && <div className="text-center py-10 text-gray-500">Loading your problems...</div>}
            
            {error && <div className="text-center py-10 text-red-500">{error}</div>}

            {!loading && !error && projects.length === 0 && (
                <div className="text-center py-12 bg-white dark:bg-[#1a2c38] rounded-xl border border-dashed border-gray-300 dark:border-gray-700">
                    <p className="text-gray-500 dark:text-gray-400 mb-4">You haven't created any problems yet.</p>
                    <button onClick={() => navigate('/new')} className="text-blue-600 font-bold hover:underline">
                        Create your first problem
                    </button>
                </div>
            )}

            {!loading && !error && projects.map((project) => (
              <MyImageCard 
                key={project.id}
                {...project}
                // מעבירים את הפונקציות המעודכנות ישירות ב-Render
                onView={handleView}
                onEdit={handleEdit}
              />
            ))}
          </div>

          {/* Pagination */}
          {!loading && projects.length > 0 && (
            <div className="flex items-center justify-center py-6 mt-4 gap-4">
               <button onClick={handlePrevPage} disabled={currentPage === 0} className="flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                <span className="material-symbols-outlined text-[20px]">chevron_left</span>
              </button>
              <span className="text-sm font-medium">Page {currentPage + 1} of {totalPages}</span>
              <button onClick={handleNextPage} disabled={currentPage >= totalPages - 1} className="flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                <span className="material-symbols-outlined text-[20px]">chevron_right</span>
              </button>
            </div>
          )}

        </div>
      </main>

      {/* Details Modal */}
      <MyProblemDetailsModal 
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        data={selectedProject}
        onEdit={handleEdit}
        onSolve={handleSolve} // חיבור פונקציית הפתרון
      />

      {/* Solver Loading Overlay */}
      {isSolving && (
        <div className="fixed inset-0 z-[200] bg-black/50 flex items-center justify-center backdrop-blur-sm">
          <div className="bg-white dark:bg-[#101c22] p-8 rounded-2xl flex flex-col items-center gap-4 shadow-2xl border border-gray-200 dark:border-gray-700">
            <span className="material-symbols-outlined text-4xl text-[#13a4ec] animate-spin">settings</span>
            <div className="text-center">
              <h3 className="text-xl font-bold text-[#111618] dark:text-white">Solving Problem...</h3>
              <p className="text-gray-500 dark:text-gray-400">Please wait while our AI crunches the numbers.</p>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};

export default MyImagesPage;