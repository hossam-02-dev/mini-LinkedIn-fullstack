import axiosInstance from './axiosConfig';

export const authAPI = {
  register: (userData) => axiosInstance.post('/auth/register', userData),
  login: (credentials) => axiosInstance.post('/auth/login', credentials),
  refreshToken: (refreshToken) => axiosInstance.post('/auth/refresh-token', { refreshToken }),
  activateAccount: (token) => axiosInstance.get(`/auth/activate?token=${token}`),
};

export default authAPI;