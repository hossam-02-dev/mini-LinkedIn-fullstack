import axiosInstance from './axiosConfig';

export const profileAPI = {
  // Profil
  getMyProfile: () => axiosInstance.get('/profils'),
  getProfile: (userId) => axiosInstance.get(`/profils/${userId}`),
  getMyStats: () => axiosInstance.get('/profils/me/stats'),
  createProfile: (data) => axiosInstance.post('/profils', data),
  updateProfile: (profilId, data) => axiosInstance.put(`/profils/${profilId}`, data),
  updatePhoto: (profilId, photoUrl) => axiosInstance.put(`/profils/${profilId}/photo?photoUrl=${photoUrl}`),
  
  // Formations
  getFormations: (profilId) => axiosInstance.get(`/formations/${profilId}`),
  addFormation: (profilId, data) => axiosInstance.post(`/formations/profil/${profilId}`, data),
  updateFormation: (formationId, data) => axiosInstance.put(`/formations/${formationId}`, data),
  deleteFormation: (formationId) => axiosInstance.delete(`/formations/${formationId}`),
  
  // Compétences
  getMyCompetences: () => axiosInstance.get('/competences/mes-competences'),
  addCompetence: (data) => axiosInstance.post('/competences', data),
  updateCompetence: (competenceId, data) => axiosInstance.put(`/competences/${competenceId}`, data),
  deleteCompetence: (competenceId) => axiosInstance.delete(`/competences/${competenceId}`),




//YLH ZEDTHA
getMyStats: () => axiosInstance.get('/profils/me/stats'),


};

export default profileAPI;