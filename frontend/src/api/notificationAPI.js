import axiosInstance from './axiosConfig';

export const notificationAPI = {
  // Récupérer toutes les notifications d'un utilisateur
  getUserNotifications: (userId) => axiosInstance.get(`/notifications/user/${userId}`),
  
  // Récupérer une notification spécifique
  getNotification: (id) => axiosInstance.get(`/notifications/${id}`),
  
  // Marquer comme lue
  markAsRead: (notificationId) => axiosInstance.put(`/notifications/${notificationId}/lu`),
  
  // Compter les notifications non lues
  getUnreadCount: () => axiosInstance.get('/notifications/non-lues/count'),
  
  // Supprimer une notification
  deleteNotification: (notificationId) => axiosInstance.delete(`/notifications/${notificationId}`),
};

export default notificationAPI;