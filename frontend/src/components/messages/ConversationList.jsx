import React from 'react';

const ConversationList = ({ connections, onSelectConversation, selectedUserId }) => {
  return (
    <div className="overflow-y-auto">
      {connections.length === 0 ? (
        <div className="p-4 text-center text-sm animate-fadeInUp" style={{ color: 'var(--color-text-muted)' }}>
          Aucune conversation
        </div>
      ) : (
        connections.map(connection => {
          const user = connection.user;
          if (!user) return null;
          const isSelected = selectedUserId === user.id;
          return (
            <div key={user.id} className="animate-fadeInUp">
              <button
                onClick={() => onSelectConversation(user.id)}
                className="w-full p-4 text-left transition-colors border-b"
                style={{
                  borderColor: 'var(--color-border)',
                  background: isSelected ? 'rgba(99, 102, 241, 0.08)' : 'transparent',
                }}
              >
                <div className="flex items-center gap-3">
                  <div className="nav-avatar w-10 h-10 text-sm">
                    {user.firstName?.[0]}{user.lastName?.[0]}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-medium text-sm truncate" style={{ color: 'var(--color-text)' }}>
                      {user.firstName} {user.lastName}
                    </p>
                    <p className="text-xs truncate" style={{ color: 'var(--color-text-muted)' }}>
                      Cliquez pour discuter
                    </p>
                  </div>
                </div>
              </button>
            </div>
          );
        })
      )}
    </div>
  );
};

export default ConversationList;
