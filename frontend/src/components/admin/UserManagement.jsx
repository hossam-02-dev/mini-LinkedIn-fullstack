import React, { useState } from 'react';
import { FaEdit, FaTrash, FaUserPlus, FaBan, FaCheckCircle } from 'react-icons/fa';
import UserFormModal from './UserFormModal';
import toast from 'react-hot-toast';

const UserManagement = ({ users, onCreateUser, onUpdateUser, onDeleteUser }) => {
  const [showModal, setShowModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState('');

  const filteredUsers = users.filter(user => {
    const matchesSearch = user.firstName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         user.lastName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         user.email?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesRole = !roleFilter || user.roleName === roleFilter;
    return matchesSearch && matchesRole;
  });

  const handleEdit = (user) => {
    setEditingUser(user);
    setShowModal(true);
  };

  const handleDelete = (user) => {
    if (window.confirm(`Voulez-vous vraiment désactiver le compte de ${user.firstName} ${user.lastName} ?`)) {
      onDeleteUser(user.id);
    }
  };

  const handleModalClose = () => {
    setShowModal(false);
    setEditingUser(null);
  };

  const handleSave = (userData) => {
    if (editingUser) {
      onUpdateUser({ ...userData, id: editingUser.id });
    } else {
      onCreateUser(userData);
    }
    handleModalClose();
  };

  const getRoleBadgeColor = (role) => {
    const colors = {
      ADMIN: 'bg-purple-100 text-purple-800',
      PROFESSEUR: 'bg-blue-100 text-blue-800',
      CHERCHEUR: 'bg-green-100 text-green-800',
      ETUDIANT: 'bg-yellow-100 text-yellow-800',
    };
    return colors[role] || 'bg-gray-100 text-gray-800';
  };

  return (
    <div>
      {/* Barre d'outils */}
      <div className="flex flex-wrap justify-between gap-4 mb-6">
        <div className="flex flex-wrap gap-2">
          <input
            type="text"
            placeholder="Rechercher..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="px-4 py-2 border rounded-lg w-64"
          />
          <select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            className="px-4 py-2 border rounded-lg"
          >
            <option value="">Tous les rôles</option>
            <option value="ETUDIANT">Étudiants</option>
            <option value="PROFESSEUR">Professeurs</option>
            <option value="CHERCHEUR">Chercheurs</option>
            <option value="ADMIN">Administrateurs</option>
          </select>
        </div>
        
        <button
          onClick={() => setShowModal(true)}
          className="flex items-center space-x-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          <FaUserPlus />
          <span>Ajouter un utilisateur</span>
        </button>
      </div>

      {/* Tableau des utilisateurs */}
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white rounded-lg overflow-hidden">
          <thead className="bg-gray-100">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Utilisateur</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Rôle</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date d'inscription</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {filteredUsers.map(user => (
              <tr key={user.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="flex items-center">
                    <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                      <span className="text-blue-600 font-medium">
                        {user.firstName?.[0]}{user.lastName?.[0]}
                      </span>
                    </div>
                    <div className="ml-4">
                      <div className="text-sm font-medium text-gray-900">
                        {user.firstName} {user.lastName}
                      </div>
                    </div>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {user.email}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-1 text-xs rounded-full ${getRoleBadgeColor(user.roleName)}`}>
                    {user.roleName}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  {user.isActive ? (
                    <span className="flex items-center text-green-600 text-sm">
                      <FaCheckCircle className="mr-1" /> Actif
                    </span>
                  ) : (
                    <span className="flex items-center text-red-600 text-sm">
                      <FaBan className="mr-1" /> Inactif
                    </span>
                  )}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {new Date(user.createdAt).toLocaleDateString()}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button
                    onClick={() => handleEdit(user)}
                    className="text-blue-600 hover:text-blue-900 mr-3"
                  >
                    <FaEdit />
                  </button>
                  <button
                    onClick={() => handleDelete(user)}
                    className="text-red-600 hover:text-red-900"
                  >
                    <FaTrash />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Modal d'ajout/édition */}
      <UserFormModal
        isOpen={showModal}
        onClose={handleModalClose}
        onSave={handleSave}
        user={editingUser}
      />
    </div>
  );
};

export default UserManagement;