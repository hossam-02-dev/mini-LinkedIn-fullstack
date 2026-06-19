import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import { FaEnvelope, FaLock, FaEye, FaEyeSlash } from 'react-icons/fa';
import toast from 'react-hot-toast';
import LoadingSpinner from '../common/LoadingSpinner';

const LoginForm = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      toast.error('Veuillez remplir tous les champs');
      return;
    }
    setLoading(true);
    try {
      await login(email, password);
      toast.success('Connexion réussie !');
      navigate('/feed');
    } catch (error) {
      const message = error.response?.data?.message || 'Email ou mot de passe incorrect';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-card">
      <h2 className="text-2xl font-bold text-center mb-6" style={{ color: 'var(--color-text)' }}>Connexion</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="label">Email</label>
          <div className="input-with-icon">
            <FaEnvelope className="input-icon" />
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input-field input-field-icon"
              placeholder="exemple@email.com"
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
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input-field input-field-icon pr-10"
              placeholder="••••••••"
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
        </div>

        <button type="submit" disabled={loading} className="btn-primary-full">
          {loading ? <LoadingSpinner size="sm" /> : 'Se connecter'}
        </button>
      </form>

      <div className="mt-5 text-center">
        <Link to="/register" className="link-accent">
          Pas encore de compte ? S&apos;inscrire
        </Link>
      </div>
    </div>
  );
};

export default LoginForm;
