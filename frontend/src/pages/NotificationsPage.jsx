import React, { useState, useEffect } from 'react';
import { notificationAPI } from '../api/notificationAPI';
import { FaBell, FaCheckCircle, FaTrash } from 'react-icons/fa';
import { formatDistanceToNow } from 'date-fns';
import { fr } from 'date-fns/locale';
import LoadingSpinner from '../components/common/LoadingSpinner';
import toast from 'react-hot-toast';

const NotificationsPage = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const currentUserId = JSON.parse(localStorage.getItem('user'))?.id;

  useEffect(() => {
    if (currentUserId) loadNotifications();
    else setLoading(false);
  }, [currentUserId]);

  const loadNotifications = async () => {
    try {
      const response = await notificationAPI.getUserNotifications(currentUserId);
      setNotifications(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des notifications');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (notificationId) => {
    try {
      await notificationAPI.markAsRead(notificationId);
      setNotifications(prev =>
        prev.map(notif => (notif.id === notificationId ? { ...notif, lu: true } : notif))
      );
      toast.success('Notification marquée comme lue');
    } catch (error) {
      toast.error('Erreur lors de la mise à jour');
    }
  };

  const handleDelete = async (notificationId) => {
    if (window.confirm('Voulez-vous vraiment supprimer cette notification ?')) {
      try {
        await notificationAPI.deleteNotification(notificationId);
        setNotifications(prev => prev.filter(n => n.id !== notificationId));
        toast.success('Notification supprimée');
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const markAllAsRead = async () => {
    const unread = notifications.filter(n => !n.lu);
    for (const notif of unread) {
      await notificationAPI.markAsRead(notif.id);
    }
    setNotifications(prev => prev.map(n => ({ ...n, lu: true })));
    toast.success('Toutes les notifications ont été marquées comme lues');
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

  if (loading) return <LoadingSpinner />;

  const unreadCount = notifications.filter(n => !n.lu).length;

  return (
    <div className="page-shell">
      <div className="page-container">
        <header className="page-header flex items-start justify-between gap-4">
          <div>
            <h1 className="page-title">Notifications</h1>
            <p className="page-subtitle">
              {unreadCount > 0
                ? `${unreadCount} non lue${unreadCount > 1 ? 's' : ''}`
                : 'Tout est à jour'}
            </p>
          </div>
          {notifications.some(n => !n.lu) && (
            <button onClick={markAllAsRead} className="btn-accent-outline shrink-0">
              <FaCheckCircle />
              <span>Tout marquer comme lu</span>
            </button>
          )}
        </header>

        {notifications.length === 0 ? (
          <div className="card empty-state animate-fadeInUp">
            <div className="icon-box">
              <FaBell style={{ color: 'var(--color-primary)', fontSize: '1.375rem' }} />
            </div>
            <p className="empty-state-text">Aucune notification pour le moment</p>
          </div>
        ) : (
          <div className="flex flex-col gap-2.5">
            {notifications.map(notification => (
              <div
                key={notification.id}
                className={`notif-item animate-fadeInUp ${notification.lu ? 'notif-item-read' : 'notif-item-unread'}`}
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-start gap-3 flex-1 min-w-0">
                    <div className="notif-icon-box">
                      {getNotificationIcon(notification.type)}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm leading-relaxed m-0" style={{ color: 'var(--color-text)' }}>
                        {notification.contenu}
                      </p>
                      <p className="text-xs mt-1 m-0" style={{ color: 'var(--color-text-muted)' }}>
                        {formatDistanceToNow(new Date(notification.date), { addSuffix: true, locale: fr })}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-1 shrink-0">
                    {!notification.lu && (
                      <button
                        onClick={() => handleMarkAsRead(notification.id)}
                        title="Marquer comme lu"
                        className="btn-ghost p-1.5"
                        style={{ color: 'var(--color-primary)' }}
                      >
                        <FaCheckCircle />
                      </button>
                    )}
                    <button
                      onClick={() => handleDelete(notification.id)}
                      title="Supprimer"
                      className="btn-ghost p-1.5 text-red-500"
                    >
                      <FaTrash />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default NotificationsPage;
