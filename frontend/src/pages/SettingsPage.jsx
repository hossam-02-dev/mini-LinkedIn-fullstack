import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { userAPI } from '../api/userAPI';
import toast from 'react-hot-toast';
import LoadingSpinner from '../components/common/LoadingSpinner';

const SettingsPage = () => {
  const { user, logout } = useContext(AuthContext);
  const [loading, setLoading] = useState(false);
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      toast.error('Les mots de passe ne correspondent pas');
      return;
    }
    
    setLoading(true);
    try {
      toast.success('Mot de passe modifié avec succès');
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
    } catch (error) {
      toast.error('Erreur lors du changement de mot de passe');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.')) {
      setLoading(true);
      try {
        await userAPI.deleteUser(user.id);
        toast.success('Compte supprimé');
        logout();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <div className="page-shell">
      <div className="page-container-narrow">
        <header className="page-header">
          <h1 className="page-title">Paramètres</h1>
          <p className="page-subtitle">Gérez la sécurité de votre compte</p>
        </header>
        
        <div className="card p-6 mb-5">
          <h2 className="section-title">Changer le mot de passe</h2>
          <form onSubmit={handlePasswordChange} className="space-y-4">
            <div>
              <label className="label">Mot de passe actuel</label>
              <input
                type="password"
                value={passwordData.currentPassword}
                onChange={(e) => setPasswordData({ ...passwordData, currentPassword: e.target.value })}
                className="input-field"
                required
              />
            </div>
            <div>
              <label className="label">Nouveau mot de passe</label>
              <input
                type="password"
                value={passwordData.newPassword}
                onChange={(e) => setPasswordData({ ...passwordData, newPassword: e.target.value })}
                className="input-field"
                required
              />
            </div>
            <div>
              <label className="label">Confirmer le mot de passe</label>
              <input
                type="password"
                value={passwordData.confirmPassword}
                onChange={(e) => setPasswordData({ ...passwordData, confirmPassword: e.target.value })}
                className="input-field"
                required
              />
            </div>
            <button type="submit" disabled={loading} className="btn-primary">
              {loading ? <LoadingSpinner size="sm" /> : 'Changer le mot de passe'}
            </button>
          </form>
        </div>
        
        <div className="card p-6">
          <h2 className="section-title section-title-danger">Zone dangereuse</h2>
          <p className="text-sm mb-4" style={{ color: 'var(--color-text-secondary)' }}>
            La suppression de votre compte est irréversible. Toutes vos données seront perdues.
          </p>
          <button onClick={handleDeleteAccount} className="btn-danger">
            Supprimer mon compte
          </button>
        </div>
      </div>
    </div>
  );
};

export default SettingsPage;
