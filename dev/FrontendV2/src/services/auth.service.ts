// src/services/auth.service.ts

const BASE_URL = "/api"; 

export interface LoginRequest {
    userName: string;
    password: string;
}

export interface RegisterRequest {
    userName: string;
    nickname: string;
    password: string;
    email: string;
}

export interface LoginResponse {
    userId: string;
}

export const authService = {
    login: async (data: LoginRequest): Promise<LoginResponse> => {
        const response = await fetch(`${BASE_URL}/users/session`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            let errorMessage = 'Login failed';
            try {
                const errorData = await response.json();
                console.error("Server Error Response:", errorData); // לוג לדיבאג
                
                // התיקון: בדיקה ספציפית של השדה 'msg'
                errorMessage = errorData.msg || errorData.message || errorData.error || 'Unknown error occurred';
                
            } catch (e) {
                errorMessage = `Server error: ${response.status} ${response.statusText}`;
            }
            throw new Error(errorMessage);
        }

        return await response.json();
    },

    register: async (data: RegisterRequest): Promise<void> => {
        const response = await fetch(`${BASE_URL}/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            let errorMessage = 'Registration failed';
            try {
                const errorData = await response.json();
                console.error("Server Error Response:", errorData);
                
                // התיקון: בדיקה ספציפית של השדה 'msg'
                errorMessage = errorData.msg || errorData.message || errorData.error || 'Unknown error occurred';
                
            } catch (e) {
                errorMessage = `Server error: ${response.status} ${response.statusText}`;
            }
            throw new Error(errorMessage);
        }
    }
};