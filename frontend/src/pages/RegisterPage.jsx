import React from 'react';
import RegisterForm from '../components/auth/RegisterForm';

const RegisterPage = () => {
  return (
    <div className="auth-page">
      <div className="w-full max-w-md">
        <div className="auth-brand">
    
          <h1 className="auth-brand-title">Rejoignez-nous</h1>
          <p className="auth-brand-subtitle">Créez votre compte professionnel</p>
        </div>
        <RegisterForm />
      </div>
    </div>
  );
};

export default RegisterPage;
