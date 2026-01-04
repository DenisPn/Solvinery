import { BrowserRouter, Routes, Route } from 'react-router-dom';

// ייבוא הדפים
import AuthPage from './pages/AuthPage';
import MyImagesPage from './pages/MyImagesPage';
import NewImagePage from './pages/NewImagePage';
import ViewImagePage from './pages/ViewImagePage';
import EditImagePage from './pages/EditImagePage';
import SolutionViewPage from './pages/SolutionViewPage';

function App() {
  return (
    <BrowserRouter>
      
      <Routes>
        {/* דף הבית - מציג את רשימת התמונות שלי */}
        <Route path="/" element={<MyImagesPage />} />

        {/* דף התחברות/הרשמה */}
        <Route path="/auth" element={<AuthPage />} />

        {/* יצירת תמונה חדשה */}
        <Route path="/new" element={<NewImagePage />} />

        {/* נתיבים דינמיים (עם מזהה ייחודי) */}
        <Route path="/image/:id" element={<ViewImagePage />} />
        <Route path="/image/:id/edit" element={<EditImagePage />} />
        
        {/* צפייה בפתרון */}
        <Route path="/solution/:id" element={<SolutionViewPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;