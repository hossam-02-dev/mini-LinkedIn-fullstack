import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { FaHome, FaUser, FaProjectDiagram, FaEnvelope, FaUsers, FaBell, FaCog } from 'react-icons/fa';

const Sidebar = () => {
  const location = useLocation();

  const menuItems = [
    { path: '/feed', icon: FaHome, label: 'Fil d\'actualité' },
    { path: '/profile', icon: FaUser, label: 'Mon profil' },
    { path: '/projects', icon: FaProjectDiagram, label: 'Projets' },
    { path: '/messages', icon: FaEnvelope, label: 'Messages' },
    { path: '/connections', icon: FaUsers, label: 'Connexions' },
    { path: '/notifications', icon: FaBell, label: 'Notifications' },
    { path: '/settings', icon: FaCog, label: 'Paramètres' },
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <aside className="sidebar">
      <nav className="p-4">
        <ul className="space-y-1">
          {menuItems.map((item) => (
            <li key={item.path}>
              <Link
                to={item.path}
                className={`sidebar-link ${isActive(item.path) ? 'sidebar-link-active' : ''}`}
              >
                <item.icon />
                <span>{item.label}</span>
              </Link>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
};

export default Sidebar;
