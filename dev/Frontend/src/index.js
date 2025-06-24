import React from 'react';
import ReactDOM from 'react-dom/client'; // Use 'react-dom/client' instead
import App from './App';
import { ZPLProvider } from './context/ZPLContext'; // Import the provider
import "@fortawesome/fontawesome-free/css/all.min.css";
import axios from "axios";

axios.defaults.baseURL = "https://solvinery-production.up.railway.app";
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
axios.defaults.headers.common['Access-Control-Allow-Methods'] = 'GET,PUT,POST,DELETE,PATCH,OPTIONS';
axios.defaults.withCredentials = false;

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <ZPLProvider>
        <App />
    </ZPLProvider>
);
