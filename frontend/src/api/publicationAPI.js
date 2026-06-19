import axiosInstance from './axiosConfig';

export const publicationAPI = {
  // Récupérer le feed
  getFeed: () => axiosInstance.get('/publications/feed'),
  
  // Récupérer les publications d'un utilisateur
  getUserPublications: (userId) => axiosInstance.get(`/publications/user/${userId}`),
  
  // Récupérer une publication spécifique
  getPublication: (publicationId) => axiosInstance.get(`/publications/${publicationId}`),
  
  // Créer une publication
  createPublication: (data) => axiosInstance.post('/publications', data),
  
  // Modifier une publication
  updatePublication: (publicationId, data) => axiosInstance.put(`/publications/${publicationId}`, data),
  
  // Supprimer une publication
  deletePublication: (publicationId) => axiosInstance.delete(`/publications/${publicationId}`),
  
  // Likes
  likePublication: (publicationId) => axiosInstance.post(`/likes/publication/${publicationId}`),
  unlikePublication: (publicationId) => axiosInstance.delete(`/likes/publication/${publicationId}`),
  getLikeCount: (publicationId) => axiosInstance.get(`/likes/publication/${publicationId}/count`),
  
  // Commentaires
  getComments: (publicationId) => axiosInstance.get(`/commentaires/${publicationId}`),
  addComment: (publicationId, data) => axiosInstance.post(`/commentaires/publication/${publicationId}`, data),
  updateComment: (commentId, data) => axiosInstance.put(`/commentaires/${commentId}`, data),
  deleteComment: (commentId) => axiosInstance.delete(`/commentaires/${commentId}`),
  getUserComments: () => axiosInstance.get('/commentaires/mes-commentaires'),
};

export default publicationAPI;