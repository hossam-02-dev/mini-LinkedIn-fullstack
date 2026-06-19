import React, { createContext, useState, useContext, useEffect } from 'react';
import { notificationAPI } from '../api/notificationAPI';
import { AuthContext } from './AuthContext';

const NotificationContext = createContext();

export const useNotifications = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotifications must be used within NotificationProvider');
  }
  return context;
};

export const NotificationProvider = ({ children }) => {
  const { user } = useContext(AuthContext);
  const [unreadCount, setUnreadCount] = useState(0);
  const [notifications, setNotifications] = useState([]);
  const [socket, setSocket] = useState(null);

  const loadUnreadCount = async () => {
    if (!user) return;
    try {
      const response = await notificationAPI.getUnreadCount();
      setUnreadCount(response.data);
    } catch (error) {
      console.error('Erreur chargement compteur:', error);
    }
  };

  const loadNotifications = async () => {
    if (!user) return;
    try {
      const response = await notificationAPI.getUserNotifications(user.id);
      setNotifications(response.data);
    } catch (error) {
      console.error('Erreur chargement notifications:', error);
    }
  };

  const markAsRead = async (notificationId) => {
    try {
      await notificationAPI.markAsRead(notificationId);
      setUnreadCount(Math.max(0, unreadCount - 1));
      setNotifications(notifications.map(n => 
        n.id === notificationId ? { ...n, lu: true } : n
      ));
    } catch (error) {
      console.error('Erreur marquage lu:', error);
    }
  };

  const addNotification = (notification) => {
    setNotifications(prev => [notification, ...prev]);
    if (!notification.lu) {
      setUnreadCount(prev => prev + 1);
    }
  };

  useEffect(() => {
    if (user) {
      loadUnreadCount();
      loadNotifications();
    }
  }, [user]);

  return (
    <NotificationContext.Provider value={{
      unreadCount,
      notifications,
      loadUnreadCount,
      loadNotifications,
      markAsRead,
      addNotification,
    }}>
      {children}
    </NotificationContext.Provider>
  );
};