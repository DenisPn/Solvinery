import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';

interface AuthContextType {
  userId: string | null;
  isAuthenticated: boolean;
  login: (userId: string, token?: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [userId, setUserId] = useState<string | null>(null);

  useEffect(() => {
    const storedUserId = localStorage.getItem('userId');
    if (storedUserId) {
      setUserId(storedUserId);
    }
  }, []);

  const login = (newUserId: string, token?: string) => {
    setUserId(newUserId);
    localStorage.setItem('userId', newUserId);
    if (token) {
      localStorage.setItem('token', token);
    }
  };

  const logout = () => {
    setUserId(null);
    localStorage.removeItem('userId');
    localStorage.removeItem('token');
  };

  return (
    <AuthContext.Provider value={{ userId, isAuthenticated: !!userId, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}