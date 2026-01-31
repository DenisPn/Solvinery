import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MyImageCard, { type MyImageCardProps } from '../components/MyImageCard';
import { useAuth } from '../context/AuthContext';
import { ImageService } from '../services/ImageService';
import { SolverService } from '../services/SolverService';
import type { SolverConfig, ImageDto } from '../types/apiTypes';
import MyProblemDetailsModal from '../components/modals/MyProblemDetailsModal';

type ProjectData = Omit<MyImageCardProps, 'onView' | 'onEdit'>;

const MyImagesPage: React.FC = () => {
  const navigate = useNavigate();
  const { userId, isAuthenticated } = useAuth();

  // --- State ---
  const [projects, setProjects] = useState<ProjectData[]>([]);
  // ✅ State לשמירת המידע המלא שהגיע מהשרת
  const [rawModels, setRawModels] = useState<Record<string, ImageDto>>({});

  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [selectedProject, setSelectedProject] = useState<MyImageCardProps | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSolving, setIsSolving] = useState(false);

  // --- Handlers ---

  const handleView = (id: string | number) => {
    const project = projects.find(p => String(p.id) === String(id));
    
    if (project) {
        setSelectedProject({
            ...project,
            onView: handleView,
            onEdit: handleEdit
        });
        setIsModalOpen(true);
    } else {
        console.warn('Project not found for ID:', id);
    }
  };

  const handleEdit = (id: string | number) => {
    navigate(`/problems/${id}/edit`);
  };

  // --- פונקציית הפתרון המתוקנת ---
  const handleSolve = async (id: string | number) => {
    if (!userId) {
        alert("User ID missing. Please log in again.");
        return;
    }

    setIsSolving(true);
    try {
      console.log(`🚀 Preparing solver for problem ${id}...`);
      
      // 1. שליפת המידע המלא מהזיכרון המקומי (במקום מהשרת)
      const fullImage = rawModels[String(id)];
      
      if (!fullImage) {
          throw new Error("Could not find problem data in local cache. Please refresh the page.");
      }

      console.log("📦 Using cached model data:", fullImage);

      // 2. בניית הקונפיגורציה הדינמית
      const preferencesScalars: Record<string, number> = {};
      if (fullImage.preferenceModules) {
          fullImage.preferenceModules.forEach(module => {
              preferencesScalars[module.moduleName] = module.scalar || 1;
          });
      }

      const constraints: string[] = [];
      if (fullImage.constraintModules) {
          fullImage.constraintModules.forEach(module => {
              if (module.active) {
                  constraints.push(module.moduleName);
              }
          });
      }

      const dynamicConfig: SolverConfig = {
        preferenceModulesScalars: preferencesScalars,
        enabledConstraintModules: constraints,
        timeout: 30
      };

      console.log("⚙️ Sending Config:", dynamicConfig);

      // 3. שליחה ל-Solver
      const solutionData = await SolverService.solveImage(userId, String(id), dynamicConfig);
      
      console.log("✅ Solution received:", solutionData);
      
      setIsModalOpen(false);
      
      navigate(`/problems/${id}/solution`, { 
        state: { 
          solutionData: solutionData,
          problemTitle: fullImage.name || "Unknown Problem"
        } 
      });

    } catch (err) {
      console.error("Solver Error:", err);
      alert("Failed to solve. Please check the console for details.");
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

      // ✅ שמירת המידע המלא ב-State
      setRawModels(data.images);

      // המרה ל-UI (כרטיסיות)
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

  const handleNextPage = () => { if (currentPage < totalPages - 1) setCurrentPage(prev => prev + 1); };
  const handlePrevPage = () => { if (currentPage > 0) setCurrentPage(prev => prev - 1); };

  // --- Render ---

  if (!isAuthenticated) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#f6f7f8] dark:bg-[#101c22]">
        <div className="text-center">
          <h2 className="text-xl font-bold text-gray-700 dark:text-gray-200">Please log in.</h2>
          <button onClick={() => navigate('/auth')} className="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg">Go to Login</button>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-[#f6f7f8] dark:bg-[#101c22] text-[#111618] dark:text-white font-sans min-h-screen flex flex-col overflow-x-hidden">
      
      <main className="flex-1 flex justify-center py-8 px-4 sm:px-8 lg:px-20">
        <div className="w-full max-w-[1200px] flex flex-col gap-6">
          
          <div className="flex flex-col md:flex-row flex-wrap justify-between items-start md:items-center gap-4">
            <div>
              <h1 className="text-[#111618] dark:text-white text-3xl md:text-4xl font-black leading-tight tracking-[-0.033em]">My Scheduling Problems</h1>
              <p className="text-gray-500 dark:text-gray-400 mt-1 text-sm">Manage and solve your scheduling challenges in one place</p>
            </div>
            <button onClick={() => navigate('/new')} className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg h-10 px-5 text-sm font-bold leading-normal transition-all shadow-md">
              <span className="material-symbols-outlined text-[20px]">add_circle</span>
              <span className="truncate">Create New Problem</span>
            </button>
          </div>

          <div className="flex justify-end">
              <button onClick={fetchUserImages} className="flex items-center gap-2 text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-blue-600">
                <span className="material-symbols-outlined text-[20px]">refresh</span> Refresh
              </button>
          </div>

          <div className="flex flex-col gap-4">
            {!loading && !error && projects.map((project) => (
              <MyImageCard 
                key={project.id}
                {...project}
                onView={handleView}
                onEdit={handleEdit}
              />
            ))}
          </div>

          {!loading && projects.length > 0 && (
            <div className="flex items-center justify-center py-6 mt-4 gap-4">
               <button onClick={handlePrevPage} disabled={currentPage === 0} className="flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg disabled:opacity-50">
                <span className="material-symbols-outlined text-[20px]">chevron_left</span>
              </button>
              <span className="text-sm font-medium">Page {currentPage + 1} of {totalPages}</span>
              <button onClick={handleNextPage} disabled={currentPage >= totalPages - 1} className="flex size-9 items-center justify-center text-[#111618] dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg disabled:opacity-50">
                <span className="material-symbols-outlined text-[20px]">chevron_right</span>
              </button>
            </div>
          )}
        </div>
      </main>

      <MyProblemDetailsModal 
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        data={selectedProject}
        onEdit={handleEdit}
        onSolve={handleSolve}
      />

      {isSolving && (
        <div className="fixed inset-0 z-[200] bg-black/50 flex items-center justify-center backdrop-blur-sm">
          <div className="bg-white dark:bg-[#101c22] p-8 rounded-2xl flex flex-col items-center gap-4 shadow-2xl">
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