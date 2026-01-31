// import axios from 'axios';
// import { API_BASE_URL } from '../config';

export interface Collaborator {
  userId: string;
  username: string;
  role: 'OWNER' | 'VISITOR';
  joinedAt: string;
  avatarUrl?: string; 
}

export const CommunityService = {
  // קבלת רשימת המשתמשים המחוברים לתמונה
  getCollaborators: async (imageId: string): Promise<Collaborator[]> => {
    // נשתמש ב-imageId בעתיד לקריאת השרת. כרגע מודפס לקונסול כדי למנוע אזהרת "unused"
    console.log(`Fetching collaborators for ${imageId}`);
    
    // Mock Data
    return [
      { userId: "1", username: "Owner (You)", role: "OWNER", joinedAt: "2023-10-01" },
      { userId: "2", username: "Sarah Connors", role: "VISITOR", joinedAt: "2023-10-05" },
      { userId: "3", username: "Mike Ross", role: "VISITOR", joinedAt: "2023-10-06" }
    ];
  },

  // הזמנת משתמש חדש
  inviteUser: async (imageId: string, username: string) => {
    console.log(`📧 Inviting ${username} to image ${imageId}`);
    // Mock request
    await new Promise(resolve => setTimeout(resolve, 1000));
    return true;
  }
};