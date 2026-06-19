import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { authAPI } from '../api/authAPI';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { FaCheckCircle, FaTimesCircle } from 'react-icons/fa';

const ActivateAccountPage = () => {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState('loading');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const token = searchParams.get('token');
    if (token) {
      activateAccount(token);
    } else {
      setStatus('error');
      setMessage('Token d\'activation manquant');
    }
  }, [searchParams]);

  const activateAccount = async (token) => {
    try {
      await authAPI.activateAccount(token);
      setStatus('success');
      setMessage('Votre compte a été activé avec succès !');
    } catch (error) {
      setStatus('error');
      setMessage(error.response?.data?.message || 'Erreur lors de l\'activation du compte');
    }
  };

  if (status === 'loading') {
    return (
      <div className="auth-page">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="auth-page">
      <div className="auth-card text-center max-w-md w-full">
        {status === 'success' ? (
          <>
            <FaCheckCircle className="text-green-500 text-6xl mx-auto mb-4" />
            <h2 className="text-2xl font-bold mb-2" style={{ color: 'var(--color-text)' }}>Compte activé !</h2>
            <p className="mb-6" style={{ color: 'var(--color-text-secondary)' }}>{message}</p>
            <Link to="/login" className="btn-primary inline-flex">
              Se connecter
            </Link>
          </>
        ) : (
          <>
            <FaTimesCircle className="text-red-500 text-6xl mx-auto mb-4" />
            <h2 className="text-2xl font-bold mb-2" style={{ color: 'var(--color-text)' }}>Erreur</h2>
            <p className="mb-6" style={{ color: 'var(--color-text-secondary)' }}>{message}</p>
            <Link to="/login" className="btn-primary inline-flex">
              Retour à la connexion
            </Link>
          </>
        )}
      </div>
    </div>
  );
};

export default ActivateAccountPage;
