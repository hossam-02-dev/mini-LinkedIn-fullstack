import axiosInstance from './axiosConfig';

export const userAPI = {

  searchUsers: (query) => axiosInstance.get(`/users/search?q=${encodeURIComponent(query)}`),
  // Récupérer tous les utilisateurs (Admin)
  getAllUsers: () => axiosInstance.get('/users'),
  
  // Récupérer un utilisateur par ID
  getUser: (userId) => axiosInstance.get(`/users/${userId}`),
  
  // Créer un utilisateur (Admin)
  createUser: (userData) => axiosInstance.post('/users', userData),
  
  // Modifier un utilisateur (Admin)
  updateUser: (userId, userData) => axiosInstance.put(`/users/${userId}`, userData),
  
  // Désactiver un compte
  deleteUser: (userId) => axiosInstance.delete(`/users/${userId}`),
};

export default userAPI;