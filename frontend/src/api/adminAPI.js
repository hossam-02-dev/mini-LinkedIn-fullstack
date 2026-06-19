import axiosInstance from './axiosConfig';

export const adminAPI = {
  // Gestion des utilisateurs
  getAllUsers: () => axiosInstance.get('/users'),
  getUserById: (id) => axiosInstance.get(`/users/${id}`),
  createUser: (userData) => axiosInstance.post('/users', userData),
  updateUser: (id, userData) => axiosInstance.put(`/users/${id}`, userData),
  deleteUser: (id) => axiosInstance.delete(`/users/${id}`),
  
  // Statistiques (à ajouter si nécessaire)
  getStats: () => axiosInstance.get('/admin/stats'),
};

export default adminAPI;