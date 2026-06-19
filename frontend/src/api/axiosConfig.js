

import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: '/api',

 // baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Log pour vérifier la baseURL (supprimer après test)
console.log('🔧 Axios configuré avec baseURL:', axiosInstance.defaults.baseURL);

// Intercepteur pour ajouter le token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // Log de l'URL appelée (utile pour debug)
    console.log('📡 Requête sortante:', config.method.toUpperCase(), config.baseURL + config.url);
    return config;
  },
  (error) => Promise.reject(error)
);

// Intercepteur pour rafraîchir le token
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(token => {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return axiosInstance(originalRequest);
        }).catch(err => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          throw new Error('No refresh token');
        }

        const response = await axios.post('http://localhost:8080/api/auth/refresh-token', {
          refreshToken,
        });
        
        const { token, refreshToken: newRefreshToken } = response.data;
        localStorage.setItem('accessToken', token);
        localStorage.setItem('refreshToken', newRefreshToken);
        
        axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        processQueue(null, token);
        
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance;