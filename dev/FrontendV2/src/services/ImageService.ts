import axios from 'axios';
import { API_BASE_URL } from '../config';
import type { ModelPayload } from '../types/apiTypes';

export const ImageService = {
  createImage: async (userId: string, payload: ModelPayload) => {
    try {
      const url = `${API_BASE_URL}/user/${userId}/image`;
      
      console.log("🚀 Service sending to:", url);
      // הדפסה של המידע שנשלח כדי שנוכל לבדוק אותו בעין
      console.log("📦 Payload Sent:", JSON.stringify(payload, null, 2));

      const response = await axios.post(url, payload, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      return response.data;
    } catch (error: any) {
      console.group("❌ Service Error Details");
      
      if (axios.isAxiosError(error) && error.response) {
        // זה החלק החשוב! כאן השרת כותב למה הוא כועס
        console.error("Status:", error.response.status);
        console.error("Server Message:", JSON.stringify(error.response.data, null, 2));
        console.error("Validation Errors:", error.response.data?.errors || "No specific validation errors found");
      } else {
        console.error("Error Message:", error.message);
      }
      
      console.groupEnd();
      throw error;
    }
  }
};