import React, { useState, useEffect } from 'react';
import { adminAPI } from '../api/adminAPI';
import UserManagement from '../components/admin/UserManagement';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { FaUsers, FaChartLine, FaCrown } from 'react-icons/fa';
import toast from 'react-hot-toast';

const AdminPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('users');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [usersRes] = await Promise.all([
        adminAPI.getAllUsers(),
      ]);
      setUsers(usersRes.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  };

  const handleUserCreate = (newUser) => {
    setUsers([...users, newUser]);
    toast.success('Utilisateur créé avec succès');
  };

  const handleUserUpdate = (updatedUser) => {
    setUsers(users.map(u => u.id === updatedUser.id ? updatedUser : u));
    toast.success('Utilisateur mis à jour');
  };

  const handleUserDelete = (userId) => {
    setUsers(users.filter(u => u.id !== userId));
    toast.success('Utilisateur désactivé');
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="page-shell">
      <div className="page-container-wide">
        <header className="page-header">
          <h1 className="page-title">Administration</h1>
          <p className="page-subtitle">Tableau de bord et gestion des utilisateurs</p>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5 mb-8">
          <div className="stat-card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm" style={{ color: 'var(--color-text-muted)' }}>Total utilisateurs</p>
                <p className="text-2xl font-bold mt-1" style={{ color: 'var(--color-text)' }}>{users.length}</p>
              </div>
              <FaUsers className="text-3xl" style={{ color: 'var(--color-primary)' }} />
            </div>
          </div>
          
          <div className="stat-card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm" style={{ color: 'var(--color-text-muted)' }}>Utilisateurs actifs</p>
                <p className="text-2xl font-bold mt-1" style={{ color: 'var(--color-text)' }}>{users.filter(u => u.isActive).length}</p>
              </div>
              <FaChartLine className="text-3xl text-green-500" />
            </div>
          </div>
          
          <div className="stat-card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm" style={{ color: 'var(--color-text-muted)' }}>Administrateurs</p>
                <p className="text-2xl font-bold mt-1" style={{ color: 'var(--color-text)' }}>{users.filter(u => u.roleName === 'ADMIN').length}</p>
              </div>
              <FaCrown className="text-3xl text-amber-500" />
            </div>
          </div>
        </div>

        <div className="panel">
          <div className="tabs-bar">
            <button
              onClick={() => setActiveTab('users')}
              className={`tab-btn ${activeTab === 'users' ? 'tab-btn-active' : ''}`}
            >
              Gestion des utilisateurs
            </button>
          </div>

          <div className="p-6">
            {activeTab === 'users' && (
              <UserManagement
                users={users}
                onCreateUser={handleUserCreate}
                onUpdateUser={handleUserUpdate}
                onDeleteUser={handleUserDelete}
              />
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminPage;
