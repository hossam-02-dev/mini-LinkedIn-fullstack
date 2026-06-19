import React from 'react';
import { Link } from 'react-router-dom';
import { FaUser, FaEnvelope, FaTrash } from 'react-icons/fa';

const FriendCard = ({ connection, onRemove }) => {
  const user = connection.user;
  return (
    <div className="card-flat p-4 flex items-center justify-between gap-3">
      <div className="flex items-center gap-3 min-w-0">
        <div className="avatar-ring w-12 h-12">
          <FaUser style={{ color: 'var(--color-primary)' }} />
        </div>
        <div className="min-w-0">
          <Link to={`/profile/${user.id}`} className="font-semibold link-accent block truncate">
            {user.firstName} {user.lastName}
          </Link>
          <p className="text-sm truncate" style={{ color: 'var(--color-text-muted)' }}>{user.email}</p>
        </div>
      </div>
      <div className="flex gap-2 shrink-0">
        <Link
          to={`/messages?user=${user.id}`}
          className="btn-primary p-2.5 rounded-full"
          title="Envoyer un message"
        >
          <FaEnvelope />
        </Link>
        <button
          onClick={onRemove}
          className="btn-danger p-2.5 rounded-full"
          title="Retirer la connexion"
        >
          <FaTrash />
        </button>
      </div>
    </div>
  );
};

export default FriendCard;
