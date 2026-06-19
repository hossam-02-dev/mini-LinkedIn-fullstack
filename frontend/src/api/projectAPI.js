import axiosInstance from './axiosConfig';

export const projectAPI = {
  // Récupérer tous les projets
  getAllProjects: () => axiosInstance.get('/projets'),
  
  // Récupérer mes projets
  getMyProjects: () => axiosInstance.get('/projets/mes-projets'),
  
  // Rechercher par titre
  searchProjects: (titre) => axiosInstance.get(`/projets/search?titre=${titre}`),
  
  // Récupérer un projet spécifique
  getProject: (projetId) => axiosInstance.get(`/projets/${projetId}`),
  
  // Créer un projet
  createProject: (data) => axiosInstance.post('/projets', data),
  
  // Modifier un projet
  updateProject: (projetId, data) => axiosInstance.put(`/projets/${projetId}`, data),
  
  // Supprimer un projet
  deleteProject: (projetId) => axiosInstance.delete(`/projets/${projetId}`),
};

export default projectAPI;