import React from 'react';
import LoginForm from '../components/auth/LoginForm';

const LoginPage = () => {
  return (
    <div className="auth-page">
      <div className="w-full max-w-md">
        <div className="auth-brand">
   
          <h1 className="auth-brand-title">LinkUp</h1>
          <p className="auth-brand-subtitle">Plateforme de réseautage professionnel</p>
        </div>
        <LoginForm />
      </div>
    </div>
  );
};

export default LoginPage;
