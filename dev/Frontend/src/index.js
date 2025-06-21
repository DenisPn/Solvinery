import React from 'react';
import ReactDOM from 'react-dom/client'; // Use 'react-dom/client' instead
import App from './App';
import { ZPLProvider } from './context/ZPLContext'; // Import the provider
import "@fortawesome/fontawesome-free/css/all.min.css";


const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <ZPLProvider>
        <App />
    </ZPLProvider>
);
