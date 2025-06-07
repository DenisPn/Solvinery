import React from "react";
import { Navigate } from "react-router-dom";
import { useZPL } from "../context/ZPLContext"; // Adjust the path if needed

const RequireAuth = ({ children }) => {
  const { userId } = useZPL();

  if (!userId) {
    // If no user is logged in, redirect to login page
    return <Navigate to="/log-in" replace />;
  }

  // If user is logged in, render the protected content
  return children;
};

export default RequireAuth;
