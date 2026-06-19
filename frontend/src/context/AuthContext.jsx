import React, { createContext, useState, useEffect } from 'react';
import { authAPI } from '../api/authAPI';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      // Récupérer les infos utilisateur depuis le token ou une API
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      setUser(userData);
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const response = await authAPI.login({ email, password });
    const { id, token, refreshToken, firstName, lastName, email: userEmail, role } = response.data;
    localStorage.setItem('accessToken', token);
    localStorage.setItem('refreshToken', refreshToken);
    const userData = { id, firstName, lastName, email: userEmail, role };
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    return response;
  };

  const register = async (userData) => {
    const response = await authAPI.register(userData);
    return response;
  };

  const logout = () => {
    localStorage.clear();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};