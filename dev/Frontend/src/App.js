import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';

import MainPage from './pages/MainPage';
import WorkAssignmentPage from './pages/WorkAssignmentPage';
import UploadZPLPage from './pages/UploadZPLPage';
import ConfigureVariablesPage from './pages/ConfigureVariablesPage';
import ConfigureConstraintsPage from './pages/ConfigureConstraintsPage';
import ConfigurePreferencesPage from './pages/ConfigurePreferencesPage';
import SolutionPreviewPage from './pages/SolutionPreviewPage';
import SolutionResultsPage from "./pages/SolutionResultsPage";
import LogInPage from "./pages/LogInPage";
import RegisterPage from "./pages/RegisterPage";
import ViewImagesPage from "./pages/ViewImagesPage";
import MyImagesPage from "./pages/MyImagesPage";
import ImageSettingSetAndParams from "./pages/ImageSettingSetAndParams";
import ImageSettingReview from "./pages/ImageSettingReview";
import RequireAuth from "./components/RequireAuth";


function App() {
    return (
        <Router>
            <DndProvider backend={HTML5Backend}>
               <Routes>
  {/* Public routes */}
  
  <Route path="/log-in" element={<LogInPage />} />
  <Route path="/register" element={<RegisterPage />} />

  {/* Protected routes */}
  <Route path="/" element={<RequireAuth><MainPage /></RequireAuth>} />
  <Route path="/work-assignment" element={<RequireAuth><WorkAssignmentPage /></RequireAuth>} />
  <Route path="/upload-zpl" element={<RequireAuth><UploadZPLPage /></RequireAuth>} />
  <Route path="/view-images" element={<RequireAuth><ViewImagesPage /></RequireAuth>} />
  <Route path="/my-images" element={<RequireAuth><MyImagesPage /></RequireAuth>} />
  <Route path="/configure-variables" element={<RequireAuth><ConfigureVariablesPage /></RequireAuth>} />
  <Route path="/configure-constraints" element={<RequireAuth><ConfigureConstraintsPage /></RequireAuth>} />
  <Route path="/configure-preferences" element={<RequireAuth><ConfigurePreferencesPage /></RequireAuth>} />
  <Route path="/solution-preview" element={<RequireAuth><SolutionPreviewPage /></RequireAuth>} />
  <Route path="/solution-results" element={<RequireAuth><SolutionResultsPage /></RequireAuth>} />
  <Route path="/image-setting-set-and-params" element={<RequireAuth><ImageSettingSetAndParams /></RequireAuth>} />
  <Route path="/image-setting-review" element={<RequireAuth><ImageSettingReview /></RequireAuth>} />
</Routes>

            </DndProvider>
        </Router>
    );
}

export default App;
