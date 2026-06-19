import React, { useContext } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import LoadingSpinner from '../common/LoadingSpinner';

const ProtectedRoute = ({ requiredRole }) => {
  const { user, loading } = useContext(AuthContext);

  if (loading) {
    return <LoadingSpinner fullScreen />;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && user.role !== requiredRole && user.role !== 'ADMIN') {
    return <Navigate to="/feed" replace />;
  }

  return <Outlet />;
};

export default ProtectedRoute;