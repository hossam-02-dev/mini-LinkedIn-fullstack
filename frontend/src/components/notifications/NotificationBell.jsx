import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { notificationAPI } from '../../api/notificationAPI';
import { FaBell } from 'react-icons/fa';
import { formatDistanceToNow } from 'date-fns';
import { fr } from 'date-fns/locale';

const NotificationBell = () => {
  const [unreadCount, setUnreadCount] = useState(0);
  const [notifications, setNotifications] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  const currentUserId = JSON.parse(localStorage.getItem('user'))?.id;

  useEffect(() => {
    if (currentUserId) {
      loadUnreadCount();
      loadRecentNotifications();

      const interval = setInterval(() => {
        if (currentUserId) loadUnreadCount();
      }, 30000);

      return () => clearInterval(interval);
    }
  }, [currentUserId]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const loadUnreadCount = async () => {
    try {
      const response = await notificationAPI.getUnreadCount();
      setUnreadCount(response.data);
    } catch (error) {
      console.error('Erreur chargement compteur:', error);
    }
  };

  const loadRecentNotifications = async () => {
    if (!currentUserId) return;
    try {
      const response = await notificationAPI.getUserNotifications(currentUserId);
      setNotifications(response.data.slice(0, 5));
    } catch (error) {
      console.error('Erreur chargement notifications:', error);
    }
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'CONNECTION_REQUEST': return '👥';
      case 'CONNECTION_ACCEPTED': return '✅';
      case 'LIKE': return '❤️';
      case 'COMMENT': return '💬';
      case 'MESSAGE': return '✉️';
      default: return '🔔';
    }
  };

  return (
    <div className="relative" ref={dropdownRef}>
      <button onClick={() => setIsOpen(!isOpen)} className="nav-icon-btn relative">
        <FaBell className="text-lg" />
        {unreadCount > 0 && (
          <span className="badge-danger">
            {unreadCount > 9 ? '9+' : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="dropdown-menu">
          <div className="dropdown-header">
            <div className="flex justify-between items-center">
              <h3 className="font-semibold text-sm" style={{ color: 'var(--color-text)' }}>Notifications</h3>
              <Link
                to="/notifications"
                className="link-accent text-sm"
                onClick={() => setIsOpen(false)}
              >
                Voir tout
              </Link>
            </div>
          </div>

          <div className="max-h-96 overflow-y-auto">
            {notifications.length === 0 ? (
              <div className="p-4 text-center text-sm" style={{ color: 'var(--color-text-muted)' }}>
                Aucune notification
              </div>
            ) : (
              notifications.map((notif) => (
                <div
                  key={notif.id}
                  className={`p-3 border-b last:border-b-0 ${!notif.lu ? 'dropdown-item-unread' : ''}`}
                  style={{ borderColor: 'var(--color-border)' }}
                >
                  <div className="flex items-start gap-2">
                    <span className="text-lg">{getNotificationIcon(notif.type)}</span>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm leading-snug" style={{ color: 'var(--color-text)' }}>{notif.contenu}</p>
                      <p className="text-xs mt-1" style={{ color: 'var(--color-text-muted)' }}>
                        {formatDistanceToNow(new Date(notif.date), { addSuffix: true, locale: fr })}
                      </p>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
