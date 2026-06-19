import React, { useContext, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import { useTheme } from '../../context/ThemeContext';
import { FaHome, FaUser, FaProjectDiagram, FaEnvelope, FaUsers, FaSignOutAlt, FaCrown, FaBars, FaTimes, FaMoon, FaSun } from 'react-icons/fa';
import NotificationBell from '../notifications/NotificationBell';
import UserSearch from './UserSearch';

const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const { theme, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItems = [
    { path: '/feed', icon: FaHome, label: 'Accueil' },
    { path: '/profile', icon: FaUser, label: 'Profil' },
    { path: '/projects', icon: FaProjectDiagram, label: 'Projets' },
    { path: '/messages', icon: FaEnvelope, label: 'Messages' },
    { path: '/connections', icon: FaUsers, label: 'Réseau' },
  ];

  if (user?.role === 'ADMIN') {
    navItems.push({ path: '/admin', icon: FaCrown, label: 'Admin' });
  }

  return (
    <nav className="navbar">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-14">

          <Link to="/feed" className="nav-brand no-underline">
            LinkUp
          </Link>

          <div className="hidden md:flex items-center gap-1">
            {navItems.map((item) => (
              <Link key={item.path} to={item.path} className="nav-link">
                <item.icon className="text-sm" />
                <span>{item.label}</span>
              </Link>
            ))}

            <div className="ml-2">
              <UserSearch />
            </div>

            <div className="ml-1">
              <NotificationBell />
            </div>

            <button
              onClick={toggleTheme}
              title={theme === 'light' ? 'Mode sombre' : 'Mode clair'}
              className="nav-icon-btn"
            >
              {theme === 'light' ? <FaMoon /> : <FaSun />}
            </button>

            <div className="nav-divider flex items-center gap-2.5">
              <div className="nav-avatar">
                {user?.firstName?.[0]}{user?.lastName?.[0]}
              </div>
              <span className="nav-user-name hidden lg:inline">
                {user?.firstName} {user?.lastName}
              </span>
              <button onClick={handleLogout} className="nav-logout">
                <FaSignOutAlt />
                <span className="hidden lg:inline">Déconnexion</span>
              </button>
            </div>
          </div>

          <div className="md:hidden flex items-center gap-2.5">
            <NotificationBell />
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="nav-icon-btn"
            >
              {isMobileMenuOpen ? <FaTimes size={20} /> : <FaBars size={20} />}
            </button>
          </div>

        </div>
      </div>

      {isMobileMenuOpen && (
        <div className="md:hidden mobile-nav-panel">
          <div className="px-3 py-3 flex flex-col gap-0.5">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className="nav-link py-2.5"
                onClick={() => setIsMobileMenuOpen(false)}
              >
                <item.icon />
                <span>{item.label}</span>
              </Link>
            ))}
            <button
              onClick={() => { handleLogout(); setIsMobileMenuOpen(false); }}
              className="nav-logout w-full text-left py-2.5 px-3"
            >
              <FaSignOutAlt />
              <span>Déconnexion</span>
            </button>
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
