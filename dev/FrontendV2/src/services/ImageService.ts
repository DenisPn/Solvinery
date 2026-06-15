import axios from 'axios';
import { API_BASE_URL } from '../config';
import type { ModelPayload, PaginatedImagesResponse, ImageSearchParams } from '../types/apiTypes';

export const ImageService = {
  // --- Create Image ---
  createImage: async (userId: string, payload: ModelPayload) => {
    try {
      const url = `${API_BASE_URL}/user/${userId}/image`;

      console.log("🚀 Service sending to:", url);
      console.log("📦 Payload Sent:", JSON.stringify(payload, null, 2));

      const response = await axios.post(url, payload, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      return response.data;
    } catch (error: any) {
      console.group("❌ Service Error Details (Create)");
      if (axios.isAxiosError(error) && error.response) {
        console.error("Status:", error.response.status);
        console.error("Server Message:", JSON.stringify(error.response.data, null, 2));
      } else {
        console.error("Error Message:", error.message);
      }
      console.groupEnd();
      throw error;
    }
  },

  // --- Get Published Images ---
  getPublishedImages: async (params: ImageSearchParams = {}): Promise<PaginatedImagesResponse> => {
    try {
      const url = `${API_BASE_URL}/image/view`;

      const response = await axios.get<PaginatedImagesResponse>(url, {
        params: {
          page: 0,
          size: 10,
          ...params
        }
      });

      return response.data;
    } catch (error: any) {
      console.group("❌ Service Error Details (Fetch View)");
      if (axios.isAxiosError(error) && error.response) {
        console.error("Status:", error.response.status);
        console.error("Data:", error.response.data);
      } else {
        console.error("Error Message:", error.message);
      }
      console.groupEnd();
      throw error;
    }
  },

  // --- Get User Images ---
  getUserImages: async (userId: string, params: ImageSearchParams = {}): Promise<PaginatedImagesResponse> => {
    try {
      const url = `${API_BASE_URL}/user/${userId}/image/view`;

      const response = await axios.get<PaginatedImagesResponse>(url, {
        params: {
          page: 0,
          size: 10,
          ...params
        }
      });

      return response.data;
    } catch (error: any) {
      console.group("❌ Service Error Details (Fetch User Images)");
      if (axios.isAxiosError(error) && error.response) {
        console.error("Status:", error.response.status);
        console.error("Data:", error.response.data);
      } else {
        console.error("Error Message:", error.message);
      }
      console.groupEnd();
      throw error;
    }
  },

  // --- Clone Image ---
  cloneImage: async (userId: string, imageId: string) => {
    try {
      const url = `${API_BASE_URL}/user/${userId}/image/${imageId}/get`;

      console.log("🚀 Service cloning from:", url);

      const response = await axios.patch(url, {});

      return response.data;
    } catch (error: any) {
      console.group("❌ Service Error Details (Clone)");
      if (axios.isAxiosError(error) && error.response) {
        console.error("Status:", error.response.status);
        console.error("Server Message:", error.response.data);
      } else {
        console.error("Error Message:", error.message);
      }
      console.groupEnd();
      throw error;
    }
  },

  // --- Delete Image ---
  deleteImage: async (userId: string, imageId: string): Promise<void> => {
    try {
      const url = `${API_BASE_URL}/user/${userId}/image/${imageId}`;
      await axios.delete(url);
    } catch (error: any) {
      console.group("❌ Service Error Details (Delete)");
      if (axios.isAxiosError(error) && error.response) {
        console.error("Status:", error.response.status);
        console.error("Server Message:", error.response.data);
      } else {
        console.error("Error Message:", error.message);
      }
      console.groupEnd();
      throw error;
    }
  },

  // --- Publish Image ---
  publishImage: async (userId: string, imageId: string): Promise<void> => {
    try {
      const url = `${API_BASE_URL}/user/${userId}/image/${imageId}/publish`;
      await axios.patch(url);
    } catch (error: any) {
      console.group("❌ Service Error Details (Publish)");
      if (axios.isAxiosError(error) && error.response) {
        console.error("Status:", error.response.status);
        console.error("Server Message:", error.response.data);
      } else {
        console.error("Error Message:", error.message);
      }
      console.groupEnd();
      throw error;
    }
  },

};