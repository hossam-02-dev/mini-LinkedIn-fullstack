import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from '../../api/authAPI';
import { FaUser, FaEnvelope, FaLock, FaEye, FaEyeSlash } from 'react-icons/fa';
import toast from 'react-hot-toast';
import LoadingSpinner from '../common/LoadingSpinner';

const RegisterForm = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'ETUDIANT',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const roles = [
    { value: 'ETUDIANT', label: 'Étudiant' },
    { value: 'PROFESSEUR', label: 'Professeur' },
    { value: 'CHERCHEUR', label: 'Chercheur' },
  ];

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      toast.error('Les mots de passe ne correspondent pas');
      return;
    }
    if (formData.password.length < 6) {
      toast.error('Le mot de passe doit contenir au moins 6 caractères');
      return;
    }
    setLoading(true);
    try {
      await authAPI.register(formData);
      toast.success('Inscription réussie ! Vérifiez votre email pour activer votre compte');
      navigate('/login');
    } catch (error) {
      const message = error.response?.data?.message || 'Erreur lors de l\'inscription';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-card">
      <h2 className="text-2xl font-bold text-center mb-6" style={{ color: 'var(--color-text)' }}>Inscription</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="label">Prénom</label>
            <div className="input-with-icon relative">
              <FaUser className="input-icon" />
              <input
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                className="input-field input-field-icon"
                required
              />
            </div>
          </div>
          <div>
            <label className="label">Nom</label>
            <input
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              className="input-field"
              required
            />
          </div>
        </div>

        <div>
          <label className="label">Email</label>
          <div className="input-with-icon relative">
            <FaEnvelope className="input-icon" />
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="input-field input-field-icon"
              required
            />
          </div>
        </div>

        <div>
          <label className="label">Mot de passe</label>
          <div className="input-with-icon relative">
            <FaLock className="input-icon" />
            <input
              type={showPassword ? 'text' : 'password'}
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="input-field input-field-icon pr-10"
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 btn-ghost p-1"
              style={{ color: 'var(--color-text-muted)' }}
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </button>
          </div>
          <p className="text-xs mt-1" style={{ color: 'var(--color-text-muted)' }}>Minimum 6 caractères</p>
        </div>

        <div>
          <label className="label">Confirmer le mot de passe</label>
          <div className="input-with-icon relative">
            <FaLock className="input-icon" />
            <input
              type={showPassword ? 'text' : 'password'}
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              className="input-field input-field-icon"
              required
            />
          </div>
        </div>

        <div>
          <label className="label">Rôle</label>
          <select
            name="role"
            value={formData.role}
            onChange={handleChange}
            className="input-field"
          >
            {roles.map(role => <option key={role.value} value={role.value}>{role.label}</option>)}
          </select>
        </div>

        <button type="submit" disabled={loading} className="btn-primary-full">
          {loading ? <LoadingSpinner size="sm" /> : "S'inscrire"}
        </button>
      </form>

      <div className="mt-5 text-center">
        <Link to="/login" className="link-accent">
          Déjà un compte ? Se connecter
        </Link>
      </div>
    </div>
  );
};

export default RegisterForm;
