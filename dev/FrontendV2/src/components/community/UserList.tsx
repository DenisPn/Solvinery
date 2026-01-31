import React, { useState } from 'react';
import type { Collaborator } from '../../services/CommunityService';

interface UserListProps {
  users: Collaborator[];
  isOwner: boolean;
  onInvite: (username: string) => void;
}

const UserList: React.FC<UserListProps> = ({ users, isOwner, onInvite }) => {
  const [inviteName, setInviteName] = useState('');

  const handleInviteClick = () => {
    if (inviteName.trim()) {
      onInvite(inviteName);
      setInviteName('');
    }
  };

  return (
    <div className="bg-white dark:bg-[#1a2c38] rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm p-6 h-full flex flex-col">
      <div className="flex items-center gap-2 mb-6 border-b border-gray-100 dark:border-gray-700 pb-4">
        <span className="material-symbols-outlined text-[#13a4ec]">group</span>
        <h3 className="text-lg font-bold text-[#111618] dark:text-white">Team Members</h3>
        <span className="bg-gray-100 dark:bg-gray-700 text-xs px-2 py-0.5 rounded-full font-bold ml-auto">{users.length}</span>
      </div>

      {/* Owner Action: Invite */}
      {isOwner && (
        <div className="mb-6">
          <label className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-2 block">Invite New Member</label>
          <div className="flex gap-2">
            <input 
              type="text" 
              value={inviteName}
              onChange={(e) => setInviteName(e.target.value)}
              placeholder="Enter username..." 
              className="flex-1 bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-[#13a4ec] outline-none"
            />
            <button 
              onClick={handleInviteClick}
              disabled={!inviteName.trim()}
              className="bg-[#13a4ec] text-white p-2 rounded-lg hover:bg-[#0f8ecb] disabled:opacity-50 transition-colors"
            >
              <span className="material-symbols-outlined text-[20px]">person_add</span>
            </button>
          </div>
        </div>
      )}

      {/* Users List */}
      <div className="flex-1 overflow-y-auto custom-scrollbar space-y-3">
        {users.map((user) => (
          <div key={user.userId} className="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors group">
            <div className="flex items-center gap-3">
              <div className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold ${user.role === 'OWNER' ? 'bg-[#13a4ec]/20 text-[#13a4ec]' : 'bg-gray-200 text-gray-600'}`}>
                {user.username.substring(0, 2).toUpperCase()}
              </div>
              <div>
                <p className="text-sm font-bold text-[#111618] dark:text-white">{user.username}</p>
                <p className="text-xs text-gray-500">{user.role === 'OWNER' ? 'Owner' : 'Visitor'}</p>
              </div>
            </div>
            {user.role === 'OWNER' && <span className="material-symbols-outlined text-xs text-[#13a4ec]">verified</span>}
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserList;