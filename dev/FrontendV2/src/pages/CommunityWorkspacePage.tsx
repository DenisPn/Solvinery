import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { CommunityService, type Collaborator } from '../services/CommunityService';
import { SolverService } from '../services/SolverService';
import { ImageService } from '../services/ImageService';
import UserList from '../components/community/UserList';
import type { ModelPayload, SolverConfig } from '../types/apiTypes';

const CommunityWorkspacePage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { userId } = useAuth();
  
  // State
  const [image, setImage] = useState<ModelPayload | null>(null);
  const [collaborators, setCollaborators] = useState<Collaborator[]>([]);
  const [isOwner, setIsOwner] = useState(false);
  const [loading, setLoading] = useState(true);
  const [solving, setSolving] = useState(false);
  
  // --- לוגיקה לקביעת בעלות (Mock) ---
  useEffect(() => {
    if (!userId || !id) return;

    // הדגמה: אם נכנסים לפרויקט 101 -> אתה Visitor (לא המנהל)
    // בכל שאר הפרויקטים -> אתה Owner
    if (id === '101') {
        setIsOwner(false);
    } else {
        setIsOwner(true);
    }
  }, [userId, id]);

  // Load Data
  useEffect(() => {
    const loadData = async () => {
      if (!id || !userId) return;
      try {
        setLoading(true);
        
        // Mock Image Loading
        // אנחנו מדמים טעינה של תמונה שיתופית
        let foundImage: ModelPayload = {
            name: id === '101' ? "Team Project Alpha" : "My Shared Project",
            description: id === '101' 
                ? "Scheduling for the external Alpha team. managed by Sarah." 
                : "A project where you are the admin.",
            variables: [],
            constraintModules: [],
            preferenceModules: [],
            sets: [],
            parameters: [],
            code: ""
        };

        // ניסיון לשלוף מידע אמיתי אם קיים (אופציונלי)
        try {
            const response = await ImageService.getUserImages(userId, { size: 100 });
            if (response.images[id]) {
                foundImage = response.images[id];
            }
        } catch (e) { /* ignore */ }

        setImage(foundImage);

        // Mock Users Loading
        // אם זה פרויקט 101, נשים מישהו אחר כ-Owner ברשימה
        const users = await CommunityService.getCollaborators(id);
        if (id === '101') {
            setCollaborators([
                { userId: "999", username: "Sarah Connors", role: "OWNER", joinedAt: "2023-01-01" }, // היא המנהלת
                { userId: userId, username: "You", role: "VISITOR", joinedAt: "2023-10-25" } // אתה אורח
            ]);
        } else {
            setCollaborators(users);
        }

      } catch (error) {
        console.error("Failed to load community workspace", error);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [id, userId]);

  // Actions
  const handleInvite = async (username: string) => {
    if (!id) return;
    await CommunityService.inviteUser(id, username);
    setCollaborators([...collaborators, { 
      userId: Math.random().toString(), 
      username, 
      role: 'VISITOR', 
      joinedAt: new Date().toISOString() 
    }]);
  };

  const handleRunSolution = async () => {
    if (!id || !userId || !image) return;
    setSolving(true);
    try {
      const dynamicConfig: SolverConfig = {
        preferenceModulesScalars: {}, 
        enabledConstraintModules: [],
        timeout: 30
      };
      
      const solution = await SolverService.solveImage(userId, id, dynamicConfig);
      navigate(`/problems/${id}/solution`, { 
        state: { solutionData: solution, problemTitle: image.name } 
      });
    } catch (error) {
      alert("Failed to run solution. (Mock mode might lack server data)");
    } finally {
      setSolving(false);
    }
  };

  const handleEditImage = () => {
    navigate(`/problems/${id}/edit`);
  };

  if (loading) return <div className="p-10 text-center">Loading Workspace...</div>;

  return (
    <div className="bg-[#f6f7f8] dark:bg-[#101c22] min-h-screen text-[#111618] dark:text-white font-sans">
      <main className="max-w-[1440px] mx-auto p-4 sm:p-8 flex flex-col lg:flex-row gap-6 h-[calc(100vh-64px)]">
        
        {/* Left Column */}
        <div className="flex-1 flex flex-col gap-6">
          
          {/* Header Card */}
          <div className="bg-white dark:bg-[#1a2c38] p-8 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm">
            <div className="flex justify-between items-start">
              <div>
                <div className="flex items-center gap-3 mb-2">
                  <span className="material-symbols-outlined text-[#13a4ec] text-3xl">hub</span>
                  <h1 className="text-3xl font-black">{image?.name}</h1>
                </div>
                <p className="text-gray-500 dark:text-gray-400 max-w-2xl">{image?.description}</p>
              </div>
              <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider ${isOwner ? 'bg-blue-100 text-blue-700' : 'bg-purple-100 text-purple-700'}`}>
                {isOwner ? 'You are Owner' : 'You are Visitor'}
              </span>
            </div>
            
            <div className="flex gap-6 mt-6 pt-6 border-t border-gray-100 dark:border-gray-700">
                <div className="flex flex-col gap-1">
                    <span className="text-xs font-bold text-gray-400 uppercase">Access Level</span>
                    <span className="font-medium">{isOwner ? "Full Control" : "Read Only"}</span>
                </div>
                <div className="flex flex-col gap-1">
                    <span className="text-xs font-bold text-gray-400 uppercase">Managed By</span>
                    <span className="font-medium">{id === '101' ? "Sarah Connors" : "You"}</span>
                </div>
            </div>
          </div>

          {/* Action Panel */}
          <div className="bg-white dark:bg-[#1a2c38] p-8 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex-1">
            <h2 className="text-xl font-bold mb-6 flex items-center gap-2">
                <span className="material-symbols-outlined">construction</span>
                Workspace Actions
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* 1. Edit (Disabled for Visitor) */}
                <button 
                    onClick={handleEditImage}
                    disabled={!isOwner}
                    className={`flex items-center gap-4 p-4 rounded-xl border-2 text-left transition-all
                    ${isOwner 
                        ? 'border-gray-100 hover:border-[#13a4ec] hover:bg-[#13a4ec]/5 cursor-pointer' 
                        : 'border-gray-100 opacity-40 cursor-not-allowed bg-gray-50'}`}
                >
                    <div className={`w-12 h-12 rounded-full flex items-center justify-center shrink-0 ${isOwner ? 'bg-blue-100 text-blue-600' : 'bg-gray-200 text-gray-400'}`}>
                        <span className="material-symbols-outlined">{isOwner ? 'edit' : 'lock'}</span>
                    </div>
                    <div>
                        <h3 className="font-bold text-lg">Edit Definition</h3>
                        <p className="text-sm text-gray-500">{isOwner ? "Modify constraints." : "Only owner can edit."}</p>
                    </div>
                </button>

                {/* 2. Run Solution (Disabled for Visitor) */}
                <button 
                    onClick={handleRunSolution}
                    disabled={!isOwner || solving}
                    className={`flex items-center gap-4 p-4 rounded-xl border-2 text-left transition-all
                    ${isOwner 
                        ? 'border-gray-100 hover:border-emerald-500 hover:bg-emerald-50 cursor-pointer' 
                        : 'border-gray-100 opacity-40 cursor-not-allowed bg-gray-50'}`}
                >
                    <div className={`w-12 h-12 rounded-full flex items-center justify-center shrink-0 ${solving ? 'bg-emerald-100 text-emerald-600 animate-spin' : (isOwner ? 'bg-emerald-100 text-emerald-600' : 'bg-gray-200 text-gray-400')}`}>
                        <span className="material-symbols-outlined">{solving ? 'settings' : (isOwner ? 'play_arrow' : 'lock')}</span>
                    </div>
                    <div>
                        <h3 className="font-bold text-lg">{solving ? 'Solving...' : 'Run Solution'}</h3>
                        <p className="text-sm text-gray-500">{isOwner ? "Calculate schedule." : "Only owner can run."}</p>
                    </div>
                </button>

                {/* 3. View Solution (Available to Everyone) */}
                <button 
                    onClick={() => navigate(`/problems/${id}/solution`)}
                    className="flex items-center gap-4 p-4 rounded-xl border-2 border-gray-100 hover:border-purple-500 hover:bg-purple-50 cursor-pointer text-left transition-all md:col-span-2"
                >
                    <div className="w-12 h-12 rounded-full bg-purple-100 text-purple-600 flex items-center justify-center shrink-0">
                        <span className="material-symbols-outlined">visibility</span>
                    </div>
                    <div>
                        <h3 className="font-bold text-lg">View Latest Solution</h3>
                        <p className="text-sm text-gray-500">View results shared by the owner.</p>
                    </div>
                </button>
            </div>
          </div>
        </div>

        {/* Right Column: User List */}
        <div className="w-full lg:w-80 shrink-0">
          <UserList 
            users={collaborators} 
            isOwner={isOwner} 
            onInvite={handleInvite} 
          />
        </div>

      </main>
    </div>
  );
};

export default CommunityWorkspacePage;