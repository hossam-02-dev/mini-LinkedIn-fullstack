import axiosInstance from './axiosConfig';

export const connectionAPI = {
  // Envoyer une demande de connexion
  sendRequest: (destinataireId) => axiosInstance.post(`/connexions/${destinataireId}`),
  
  // Accepter une demande
  acceptRequest: (connexionId) => axiosInstance.put(`/connexions/${connexionId}/accepter`),
  
  // Refuser une demande
  refuseRequest: (connexionId) => axiosInstance.put(`/connexions/${connexionId}/refuser`),
  
  // Annuler une demande en attente
  cancelRequest: (connexionId) => axiosInstance.delete(`/connexions/${connexionId}/annuler`),
  
  // Supprimer une connexion acceptée (un ami)
  removeConnection: (connexionId) => axiosInstance.delete(`/connexions/${connexionId}/supprimer`),
  
  // Récupérer les demandes reçues
  getReceivedRequests: () => axiosInstance.get('/connexions/demandes-recues'),
  
  // Récupérer les connexions acceptées (mes relations)
  getAcceptedConnections: () => axiosInstance.get('/connexions/acceptees'),
  
  // Vérifier le statut de connexion avec un utilisateur
  getConnectionStatus: (userId) => axiosInstance.get(`/connexions/statut/${userId}`),
};

export default connectionAPI;