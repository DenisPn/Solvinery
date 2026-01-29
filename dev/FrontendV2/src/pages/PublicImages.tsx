import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PublicImageCard from '../components/PublicImageCard';
import PublicImageDetailsModal from '../components/modals/PublicImageDetailsModal';
import { ImageService } from '../services/ImageService';
import type { ImageDto } from '../types/apiTypes';
// 1. ייבוא הקונטקסט (וודא שהנתיב תואם למיקום ששמרת את הקובץ)
import { useAuth } from '../context/AuthContext';

interface UIProjectData extends ImageDto {
  id: string;
  title: string;
  industry: string;
  industryColor: string;
  type: string;
  typeColor: string;
  author: string;
  date: string;
  complexity: string;
  complexityColor: string;
  downloads: string;
}

const PublicImages: React.FC = () => {
  const navigate = useNavigate();
  
  // 2. שימוש ב-AuthContext לקבלת המשתמש המחובר
  const { userId, isAuthenticated } = useAuth();

  // Modal & Selection State
  const [selectedProject, setSelectedProject] = useState<UIProjectData | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isCloning, setIsCloning] = useState(false);

  // Data State
  const [projects, setProjects] = useState<UIProjectData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  
  // Pagination State
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchImages = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await ImageService.getPublishedImages({
        page: currentPage,
        size: 10
      });

      const mappedProjects: UIProjectData[] = Object.entries(data.images).map(([id, dto]) => {
        return {
          ...dto,
          id: id,
          title: dto.name,
          author: dto.authorName,
          date: dto.creationDate, 
          
          // Hardcoded UI fields
          industry: "General", 
          industryColor: "blue",
          type: "Optimization",
          typeColor: "purple",
          complexity: "Intermediate",
          complexityColor: "text-orange-500",
          downloads: "N/A"
        };
      });

      setProjects(mappedProjects);
      setTotalPages(data.totalPages);
      
    } catch (err) {
      setError("Failed to load images.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchImages();
  }, [currentPage]);

  const handleViewDetails = (id: string | number) => {
    const project = projects.find(p => p.id === String(id));
    
    if (project) {
      // הדפסת המידע לקונסול כפי שביקשת
      console.group(`🔍 Opening Details for: ${project.title}`);
      console.log("🆔 Image ID:", project.id);
      console.log("📦 Full Data Object:", project);
      console.groupEnd();

      setSelectedProject(project);
      setIsModalOpen(true);
    } else {
      console.warn(`Project with id ${id} not found in current list`);
    }
  };

  const handleCloneProject = async (imageId: string) => {
    // 3. בדיקה מול ה-Context האם המשתמש מחובר
    if (!isAuthenticated || !userId) {
        alert("You must be logged in to clone problems.");
        // אופציונלי: הפניה לעמוד התחברות
        // navigate('/login');
        return;
    }

    setIsCloning(true);
    try {
        // שימוש ב-userId האמיתי מה-AuthContext
        await ImageService.cloneImage(userId, imageId);
        
        // Success actions
        alert("Problem cloned successfully to your collection!");
        setIsModalOpen(false);
        // Optional: navigate('/myimages');
        
    } catch (err) {
        console.error("Clone failed", err);
        alert("Failed to clone the problem. Please try again.");
    } finally {
        setIsCloning(false);
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) setCurrentPage(prev => prev + 1);
  };

  const handlePrevPage = () => {
    if (currentPage > 0) setCurrentPage(prev => prev - 1);
  };

  return (
    <div className="bg-[#f6f7f8] dark:bg-[#101c22] text-[#111618] dark:text-white min-h-screen font-sans flex flex-col">
      
      {/* Main Container */}
      <div className="flex flex-col md:flex-row max-w-[1440px] mx-auto w-full px-4 md:px-10 lg:px-20 py-8 gap-8 flex-1">
        
        {/* Filters Sidebar */}
        <aside className="w-full md:w-64 shrink-0">
          <div className="bg-white dark:bg-gray-900 rounded-xl p-6 border border-[#f0f3f4] dark:border-gray-800 shadow-sm sticky top-8">
            <div className="mb-6 flex items-center justify-between">
              <h3 className="text-lg font-bold">Filters</h3>
              <button className="text-xs text-[#13a4ec] font-semibold hover:underline">Clear all</button>
            </div>
            
            <div className="space-y-6">
              <div className="space-y-3">
                <div className="flex items-center gap-2 text-[#617c89] dark:text-gray-400">
                  <span className="material-symbols-outlined text-sm">tag</span>
                  <span className="text-xs font-bold uppercase tracking-wider">Problem Type</span>
                </div>
                <div className="space-y-2">
                  {['Staffing', 'Logistics', 'Production'].map((item) => (
                    <label key={item} className="flex items-center gap-3 cursor-pointer group">
                      <input type="checkbox" defaultChecked={item === 'Staffing'} className="rounded border-gray-300 text-[#13a4ec] focus:ring-[#13a4ec] w-4 h-4" />
                      <span className="text-sm font-medium text-gray-700 dark:text-gray-300 group-hover:text-[#13a4ec] transition-colors">{item}</span>
                    </label>
                  ))}
                </div>
              </div>

              <div className="pt-4 border-t border-[#f0f3f4] dark:border-gray-800">
                <button className="w-full bg-[#f0f3f4] dark:bg-gray-800 text-sm font-bold py-2 rounded-lg hover:bg-gray-200 transition-colors flex items-center justify-center gap-2">
                  <span className="material-symbols-outlined text-lg">star</span>
                  My Favorites
                </button>
              </div>
            </div>
          </div>
        </aside>

        {/* Main Content Area */}
        <main className="flex-1 flex flex-col gap-6">
          
          {/* Header */}
          <div className="space-y-4">
            <div className="flex items-center gap-2 text-sm">
              <span onClick={() => navigate('/')} className="text-[#617c89] hover:text-[#13a4ec] cursor-pointer">Home</span>
              <span className="text-[#617c89]">/</span>
              <span className="font-medium">Public Gallery</span>
            </div>
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div className="space-y-1">
                <h1 className="text-3xl font-black tracking-tight">Public Scheduling Problems</h1>
                <p className="text-[#617c89] text-base">Browse and contribute to our open-source scheduling database.</p>
              </div>
              <button 
                onClick={fetchImages}
                className="bg-[#f0f3f4] dark:bg-gray-800 text-sm font-bold px-4 py-2.5 rounded-lg hover:bg-gray-200 transition-colors flex items-center gap-2 self-start sm:self-center">
                <span className="material-symbols-outlined text-lg">refresh</span>
                Refresh List
              </button>
            </div>
          </div>

          {/* Sort/Filter Bar */}
          <div className="flex flex-wrap items-center justify-between gap-4 p-4 bg-white dark:bg-gray-900 rounded-xl border border-[#f0f3f4] dark:border-gray-800 shadow-sm">
            <div className="flex flex-wrap gap-2">
              <button className="h-8 px-3 rounded-full bg-[#13a4ec]/10 text-[#13a4ec] text-xs font-bold border border-[#13a4ec]/20 flex items-center gap-1">
                Staffing <span className="material-symbols-outlined text-sm">close</span>
              </button>
              <button className="h-8 px-3 rounded-full bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 text-xs font-bold flex items-center gap-1">
                + Add Filter
              </button>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-sm text-[#617c89]">Sort by:</span>
              <select className="bg-transparent border-none text-sm font-bold focus:ring-0 p-0 pr-6 cursor-pointer">
                <option>Newest First</option>
                <option>Most Downloaded</option>
              </select>
            </div>
          </div>

          {/* Cards List */}
          <div className="space-y-4">
            {loading && <div className="text-center py-10">Loading projects...</div>}
            
            {error && <div className="text-red-500 text-center py-10">{error}</div>}

            {!loading && !error && projects.length === 0 && (
              <div className="text-center py-10 text-gray-500">No public images found.</div>
            )}

            {!loading && !error && projects.map((project) => (
              <PublicImageCard 
                key={project.id}
                {...project}
                // @ts-ignore
                id={project.id} 
                onViewDetails={() => handleViewDetails(project.id)}
              />
            ))}
          </div>

          {/* Pagination */}
          {!loading && projects.length > 0 && (
            <div className="flex items-center justify-center gap-4 mt-4">
              <button 
                onClick={handlePrevPage}
                disabled={currentPage === 0}
                className="w-10 h-10 rounded-lg border border-gray-200 dark:border-gray-700 flex items-center justify-center hover:bg-white dark:hover:bg-gray-800 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                <span className="material-symbols-outlined">chevron_left</span>
              </button>
              
              <div className="flex items-center gap-2 font-medium">
                 Page {currentPage + 1} of {totalPages}
              </div>

              <button 
                 onClick={handleNextPage}
                 disabled={currentPage >= totalPages - 1}
                 className="w-10 h-10 rounded-lg border border-gray-200 dark:border-gray-700 flex items-center justify-center hover:bg-white dark:hover:bg-gray-800 transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                <span className="material-symbols-outlined">chevron_right</span>
              </button>
            </div>
          )}
          
        </main>
      </div>

      <footer className="bg-white dark:bg-[#101c22] border-t border-[#f0f3f4] dark:border-gray-800 py-8 px-4 md:px-10 lg:px-20 mt-auto">
        <div className="max-w-[1440px] mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 bg-[#13a4ec] rounded flex items-center justify-center text-white">
              <span className="material-symbols-outlined text-xs">grid_view</span>
            </div>
            <span className="font-bold text-sm">Scheduling Hub</span>
            <span className="text-xs text-[#617c89] ml-2">© 2023 Open Source Resource</span>
          </div>
          <div className="flex gap-8 text-sm text-[#617c89]">
            <a className="hover:text-[#13a4ec] transition-colors" href="#">Privacy Policy</a>
            <a className="hover:text-[#13a4ec] transition-colors" href="#">Terms of Service</a>
            <a className="hover:text-[#13a4ec] transition-colors" href="#">Contact</a>
          </div>
        </div>
      </footer>

      {/* Modal Component */}
      <PublicImageDetailsModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        data={selectedProject}
        onClone={handleCloneProject}
        isCloning={isCloning}
      />

    </div>
  );
};

export default PublicImages;