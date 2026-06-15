import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';

interface AuthContextType {
  userId: string | null;
  userName: string | null;
  email: string | null;
  isAuthenticated: boolean;
  login: (userId: string, userName: string, email: string, token?: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [userId, setUserId] = useState<string | null>(null);
  const [userName, setUserName] = useState<string | null>(null);
  const [email, setEmail] = useState<string | null>(null);

  useEffect(() => {
    const storedUserId = localStorage.getItem('userId');
    const storedUserName = localStorage.getItem('userName');
    const storedEmail = localStorage.getItem('email');
    if (storedUserId) setUserId(storedUserId);
    if (storedUserName) setUserName(storedUserName);
    if (storedEmail) setEmail(storedEmail);
  }, []);

  const login = (newUserId: string, newUserName: string, newEmail: string, token?: string) => {
    setUserId(newUserId);
    setUserName(newUserName);
    setEmail(newEmail);
    localStorage.setItem('userId', newUserId);
    localStorage.setItem('userName', newUserName);
    localStorage.setItem('email', newEmail);
    if (token) localStorage.setItem('token', token);
  };

  const logout = () => {
    setUserId(null);
    setUserName(null);
    setEmail(null);
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('email');
    localStorage.removeItem('token');
  };

  return (
    <AuthContext.Provider value={{ userId, userName, email, isAuthenticated: !!userId, login, logout }}>
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