import React from 'react';
import { FaUserPlus, FaUserCheck, FaClock } from 'react-icons/fa';

const ConnectionButton = ({ status, onSendRequest, onAccept, onRefuse, connexionId }) => {
  const getButton = () => {
    switch (status) {
      case 'connected':
        return (
          <button disabled className="btn-secondary opacity-80 cursor-default text-green-600">
            <FaUserCheck />
            <span>Connecté</span>
          </button>
        );
      
      case 'pending':
        return (
          <button disabled className="btn-secondary opacity-80 cursor-default text-amber-600">
            <FaClock />
            <span>En attente</span>
          </button>
        );
      
      case 'received':
        return (
          <div className="flex gap-2">
            <button onClick={onAccept} className="btn-primary">
              <FaUserCheck />
              <span>Accepter</span>
            </button>
            <button onClick={onRefuse} className="btn-danger">
              Refuser
            </button>
          </div>
        );
      
      default:
        return (
          <button onClick={onSendRequest} className="btn-primary">
            <FaUserPlus />
            <span>Se connecter</span>
          </button>
        );
    }
  };

  return getButton();
};

export default ConnectionButton;
