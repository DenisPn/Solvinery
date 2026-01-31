import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar.tsx';
import { AuthProvider } from './context/AuthContext';

// Page Imports
import AuthPage from './pages/AuthPage';
import MyImagesPage from './pages/MyImagesPage';
import NewImagePage from './pages/NewImagePage';
import ViewImagePage from './pages/ViewImagePage';
import EditImagePage from './pages/EditImagePage';
import SolutionViewPage from './pages/SolutionViewPage';
import PublicImages from './pages/PublicImages.tsx';
import SolutionPage from './pages/SolutionPage';
import CommunityWorkspacePage from './pages/CommunityWorkspacePage';
// ✅ ייבוא העמוד החדש
import CommunityImagesPage from './pages/CommunityImagesPage'; 

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />
        
        <Routes>
          <Route path="/" element={<PublicImages />} />
          <Route path="/auth" element={<AuthPage />} />
          <Route path="/myimages" element={<MyImagesPage />} />
          <Route path="/new" element={<NewImagePage />} />

          <Route path="/problems/:id" element={<ViewImagePage />} /> 
          <Route path="/problems/:id/edit" element={<EditImagePage />} />
          <Route path="/problems/:id/solution" element={<SolutionPage />} />

          {/* ✅ הוספת הנתיב לרשימת הקהילה */}
          <Route path="/community" element={<CommunityImagesPage />} />
          
          {/* הנתיב הקיים ל-Workspace ספציפי */}
          <Route path="/community/:id" element={<CommunityWorkspacePage />} />

          <Route path="/image/:id" element={<ViewImagePage />} />
          <Route path="/image/:id/edit" element={<EditImagePage />} />
          <Route path="/solution/:id" element={<SolutionViewPage />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;