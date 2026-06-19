import React, { useState, useEffect } from 'react';
import { FaTimes } from 'react-icons/fa';
import LoadingSpinner from '../common/LoadingSpinner';

const UserFormModal = ({ isOpen, onClose, onSave, user }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    roleName: 'ETUDIANT',
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      setFormData({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
        password: '',
        roleName: user.roleName || 'ETUDIANT',
      });
    } else {
      setFormData({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        roleName: 'ETUDIANT',
      });
    }
  }, [user]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await onSave(formData);
      onClose();
    } catch (error) {
      console.error('Erreur:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex items-center justify-center min-h-screen px-4">
        <div className="fixed inset-0 bg-black opacity-50" onClick={onClose}></div>
        
        <div className="relative bg-white rounded-lg shadow-xl max-w-md w-full p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold">
              {user ? 'Modifier l\'utilisateur' : 'Ajouter un utilisateur'}
            </h2>
            <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
              <FaTimes />
            </button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Prénom *
                </label>
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  required
                  className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nom *
                </label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  required
                  className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Email *
                </label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              {!user && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Mot de passe *
                  </label>
                  <input
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    required={!user}
                    minLength={6}
                    className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              )}

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Rôle *
                </label>
                <select
                  name="roleName"
                  value={formData.roleName}
                  onChange={handleChange}
                  required
                  className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="ETUDIANT">Étudiant</option>
                  <option value="PROFESSEUR">Professeur</option>
                  <option value="CHERCHEUR">Chercheur</option>
                  <option value="ADMIN">Administrateur</option>
                </select>
              </div>
            </div>

            <div className="flex justify-end space-x-3 mt-6">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400"
              >
                Annuler
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
              >
                {loading ? <LoadingSpinner size="sm" /> : (user ? 'Mettre à jour' : 'Créer')}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default UserFormModal;