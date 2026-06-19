// src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import { ThemeProvider } from './context/ThemeContext';   // ← AJOUT
import ProtectedRoute from './components/auth/ProtectedRoute';
import Navbar from './components/common/Navbar';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ActivateAccountPage from './pages/ActivateAccountPage';
import HomePage from './pages/HomePage';
import ProfilePage from './pages/ProfilePage';
import UserProfilePage from './pages/UserProfilePage';
import ProjectsPage from './pages/ProjectsPage';
import MyProjectsPage from './pages/MyProjectsPage';
import MessagesPage from './pages/MessagesPage';
import ConnectionsPage from './pages/ConnectionsPage';
import NotificationsPage from './pages/NotificationsPage';
import SettingsPage from './pages/SettingsPage';
import AdminPage from './pages/AdminPage';

function App() {
  return (
    <ThemeProvider>   {/* ← ENVELOPPER TOUT */}
      <Router>
        <AuthProvider>
          <NotificationProvider>
            <Toaster 
              position="top-right"
              toastOptions={{
                duration: 4000,
                style: {
                  background: 'var(--color-surface-elevated)',
                  color: 'var(--color-text)',
                  border: '1px solid var(--color-border)',
                  boxShadow: 'var(--shadow-panel)',
                  borderRadius: '0.75rem',
                  fontSize: '0.875rem',
                },
                success: {
                  duration: 3000,
                  iconTheme: {
                    primary: '#6366f1',
                    secondary: '#fff',
                  },
                },
                error: {
                  duration: 4000,
                  iconTheme: {
                    primary: '#ef4444',
                    secondary: '#fff',
                  },
                },
              }}
            />
            <div className="app-shell">
              <Navbar />
              <main className="app-main">
                <Routes>
                  {/* Routes publiques */}
                  <Route path="/login" element={<LoginPage />} />
                  <Route path="/register" element={<RegisterPage />} />
                  <Route path="/activate" element={<ActivateAccountPage />} />
                  
                  {/* Routes protégées */}
                  <Route element={<ProtectedRoute />}>
                    <Route path="/" element={<Navigate to="/feed" replace />} />
                    <Route path="/feed" element={<HomePage />} />
                    <Route path="/profile" element={<ProfilePage />} />
                    <Route path="/profile/:userId" element={<UserProfilePage />} />
                    <Route path="/projects" element={<ProjectsPage />} />
                    <Route path="/my-projects" element={<MyProjectsPage />} />
                    <Route path="/messages" element={<MessagesPage />} />
                    <Route path="/connections" element={<ConnectionsPage />} />
                    <Route path="/notifications" element={<NotificationsPage />} />
                    <Route path="/settings" element={<SettingsPage />} />
                  </Route>
                  
                  {/* Routes Admin */}
                  <Route element={<ProtectedRoute requiredRole="ADMIN" />}>
                    <Route path="/admin" element={<AdminPage />} />
                    <Route path="/admin/users" element={<AdminPage />} />
                  </Route>
                  
                  {/* 404 */}
                  <Route path="*" element={<Navigate to="/feed" replace />} />
                </Routes>
              </main>
            </div>
          </NotificationProvider>
        </AuthProvider>
      </Router>
    </ThemeProvider>
  );
}

export default App;