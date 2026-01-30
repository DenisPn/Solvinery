import axios from 'axios';
import { API_BASE_URL } from '../config';
import type { SolverConfig, SolverResponse } from '../types/apiTypes';

export const SolverService = {
  /**
   * Triggers the solver for a specific image.
   * POST /user/{userId}/image/{imageId}/solver
   */
  solveImage: async (userId: string, imageId: string, config: SolverConfig): Promise<SolverResponse> => {
    try {
      const url = `${API_BASE_URL}/user/${userId}/image/${imageId}/solver`;
      
      console.log("🧠 Solver Service sending to:", url);
      console.log("⚙️ Config:", JSON.stringify(config, null, 2));

      const response = await axios.post<SolverResponse>(url, config, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      return response.data;
    } catch (error: any) {
      console.group("❌ Solver Service Error");
      if (axios.isAxiosError(error) && error.response) {
        console.error("Status:", error.response.status);
        // שינוי כאן: המרת האובייקט לטקסט קריא כדי שתראה בקונסול מה הבעיה
        console.error("Server Message:", JSON.stringify(error.response.data, null, 2));
      } else {
        console.error("Error Message:", error.message);
      }
      console.groupEnd();
      throw error;
    }
  }
};