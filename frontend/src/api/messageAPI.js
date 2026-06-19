import axiosInstance from './axiosConfig';

export const messageAPI = {
  // Envoyer un message
  sendMessage: (destinataireId, contenu) => 
    axiosInstance.post(`/messages/${destinataireId}?contenu=${encodeURIComponent(contenu)}`),
  
  // Récupérer une conversation entre deux utilisateurs
  getConversation: (userId2) => axiosInstance.get(`/messages/conversation/${userId2}`),
  
  // Récupérer les messages non lus
  getUnreadMessages: () => axiosInstance.get('/messages/non-lus'),
  
  // Marquer un message comme lu
  markAsRead: (messageId) => axiosInstance.put(`/messages/${messageId}/lu`),
  
  // Supprimer un message
  deleteMessage: (messageId) => axiosInstance.delete(`/messages/${messageId}`),
};

export default messageAPI;