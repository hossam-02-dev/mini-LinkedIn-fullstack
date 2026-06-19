import React from 'react';
import { FaUser, FaCheck, FaTimes } from 'react-icons/fa';
import { formatDistanceToNow } from 'date-fns';
import { fr } from 'date-fns/locale';

const ConnectionRequestCard = ({ request, onAccept, onRefuse }) => {
  return (
    <div className="p-4 flex items-center justify-between gap-3">
      <div className="flex items-center gap-3 min-w-0">
        <div className="avatar-ring w-12 h-12">
          <FaUser style={{ color: 'var(--color-primary)' }} />
        </div>
        <div className="min-w-0">
          <h3 className="font-semibold truncate" style={{ color: 'var(--color-text)' }}>
            {request.user?.firstName} {request.user?.lastName}
          </h3>
          <p className="text-sm truncate" style={{ color: 'var(--color-text-muted)' }}>
            {request.user?.email}
          </p>
          <p className="text-xs mt-0.5" style={{ color: 'var(--color-text-muted)' }}>
            Demandé {formatDistanceToNow(new Date(request.dateEnvoi), { addSuffix: true, locale: fr })}
          </p>
        </div>
      </div>
      <div className="flex gap-2 shrink-0">
        <button onClick={onAccept} className="btn-primary p-2.5 rounded-full" title="Accepter">
          <FaCheck />
        </button>
        <button onClick={onRefuse} className="btn-danger p-2.5 rounded-full" title="Refuser">
          <FaTimes />
        </button>
      </div>
    </div>
  );
};

export default ConnectionRequestCard;
