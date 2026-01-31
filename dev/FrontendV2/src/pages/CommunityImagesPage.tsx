import React from 'react';
import { useNavigate } from 'react-router-dom';
// ייבוא הקומפוננטה החדשה
import CommunityProjectCard from '../components/community/CommunityProjectCard';

const CommunityImagesPage: React.FC = () => {
  const navigate = useNavigate();

  // Mock Data
  const sharedProjects = [
    { 
      id: "101", 
      title: "Team Project Alpha", 
      owner: "Sarah Connors", 
      members: 4, 
      lastActive: "2h ago",
      role: "VISITOR" 
    },
    { 
      id: "102", 
      title: "Q4 Marketing Schedule", 
      owner: "You", 
      members: 8, 
      lastActive: "1d ago",
      role: "OWNER"
    },
    { 
      id: "103", 
      title: "Dev Ops Rotation", 
      owner: "You", 
      members: 3, 
      lastActive: "Just now",
      role: "OWNER"
    },
  ];

  return (
    <div className="bg-[#f6f7f8] dark:bg-[#101c22] min-h-screen text-[#111618] dark:text-white font-sans p-8">
      <div className="max-w-[1200px] mx-auto">
        <div className="mb-8 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h1 className="text-3xl font-black mb-2">Community Images</h1>
            <p className="text-gray-500 dark:text-gray-400">Collaborate on scheduling problems with your team.</p>
          </div>
          
          <button className="flex items-center gap-2 bg-[#13a4ec] hover:bg-[#0f8ecb] text-white px-5 py-2.5 rounded-lg font-bold shadow-md transition-colors">
             <span className="material-symbols-outlined">add</span>
             <span>New Shared Project</span>
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {sharedProjects.map((project) => (
            <CommunityProjectCard
              key={project.id}
              id={project.id}
              title={project.title}
              owner={project.owner}
              members={project.members}
              lastActive={project.lastActive}
              role={project.role}
              onEnter={(id) => navigate(`/community/${id}`)}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default CommunityImagesPage;