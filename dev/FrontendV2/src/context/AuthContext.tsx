import { createContext, useContext, useState, useEffect, type ReactNode } from 'react'; // <-- התיקון כאן (type ReactNode)

// 1. הגדרת הטיפוסים למידע שנשמר ב-Context
interface AuthContextType {
  userId: string | null;
  isAuthenticated: boolean;
  login: (userId: string, token?: string) => void;
  logout: () => void;
}

// 2. יצירת ה-Context עם ערך ברירת מחדל
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// 3. ה-Provider: הרכיב שעוטף את האפליקציה ומספק את המידע
export function AuthProvider({ children }: { children: ReactNode }) {
  const [userId, setUserId] = useState<string | null>(null);

  // בעת טעינת האפליקציה, נבדוק אם יש מזהה שמור ב-localStorage
  useEffect(() => {
    const storedUserId = localStorage.getItem('userId');
    if (storedUserId) {
      setUserId(storedUserId);
    }
  }, []);

  // פונקציית התחברות
  const login = (newUserId: string, token?: string) => {
    setUserId(newUserId);
    localStorage.setItem('userId', newUserId);
    
    // אם השרת מחזיר טוקן, נשמור גם אותו
    if (token) {
      localStorage.setItem('token', token);
    }
  };

  // פונקציית התנתקות
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

// 4. Hook מותאם אישית לשימוש קל בקומפוננטות
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}