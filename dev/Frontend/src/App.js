import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import UploadZPLPage from './pages/UploadZPLPage';
import ConfigureVariablePage from './pages/ConfigureVariablesPage';
import ConfigureConstraintsPage from './pages/ConfigureConstraintsPage';
import ConfigurePreferencesPage from './pages/ConfigurePreferencesPage';
import MainPage from './pages/MainPage';
import WorkAssignmentPage from './pages/WorkAssignmentPage';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/upload-zpl" element={<UploadZPLPage />} />
                <Route path="/configure-variables" element={<ConfigureVariablePage />} />
                <Route path="/configure-constraints" element={<ConfigureConstraintsPage />} />
                <Route path="/configure-preferences" element={<ConfigurePreferencesPage />} />
                <Route path="/work-assignment" element={<WorkAssignmentPage />} />
            </Routes>
        </Router>
    );
}

export default App;
