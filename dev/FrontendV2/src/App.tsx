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
// ✅ הייבוא החדש
import SolutionPage from './pages/SolutionPage';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />
        
        <Routes>
          {/* Home Page */}
          <Route path="/" element={<PublicImages />} />

          {/* Authentication */}
          <Route path="/auth" element={<AuthPage />} />

          {/* My Images */}
          <Route path="/myimages" element={<MyImagesPage />} />

          {/* Create New Image */}
          <Route path="/new" element={<NewImagePage />} />

          {/* Dynamic Routes */}
          {/* שים לב: הנתיבים כאן צריכים להתאים למה שכתבנו ב-navigate ב-MyImagesPage */}
          <Route path="/problems/:id" element={<ViewImagePage />} /> 
          <Route path="/problems/:id/edit" element={<EditImagePage />} />
          
          {/* ✅ הנתיב החדש לדף הפתרון */}
          <Route path="/problems/:id/solution" element={<SolutionPage />} />

          {/* Legacy/Other Routes */}
          <Route path="/image/:id" element={<ViewImagePage />} />
          <Route path="/image/:id/edit" element={<EditImagePage />} />
          <Route path="/solution/:id" element={<SolutionViewPage />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;