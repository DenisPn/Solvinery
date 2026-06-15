import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

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
import CommunityImagesPage from './pages/CommunityImagesPage';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* עמוד התחברות - בלי Navbar */}
          <Route path="/auth" element={<AuthPage />} />

          {/* כל שאר העמודים - עם Navbar ומוגנים */}
          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<PublicImages />} />
            <Route path="/myimages" element={<MyImagesPage />} />
            <Route path="/new" element={<NewImagePage />} />
            <Route path="/problems/:id" element={<ViewImagePage />} />
            <Route path="/problems/:id/edit" element={<EditImagePage />} />
            <Route path="/problems/:id/solution" element={<SolutionPage />} />
            <Route path="/community" element={<CommunityImagesPage />} />
            <Route path="/community/:id" element={<CommunityWorkspacePage />} />
            <Route path="/image/:id" element={<ViewImagePage />} />
            <Route path="/image/:id/edit" element={<EditImagePage />} />
            <Route path="/solution/:id" element={<SolutionViewPage />} />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;