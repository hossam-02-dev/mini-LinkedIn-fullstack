import React, { useState, useEffect } from 'react';
import { messageAPI } from '../api/messageAPI';
import { connectionAPI } from '../api/connectionAPI';
import { userAPI } from '../api/userAPI';
import ConversationList from '../components/messages/ConversationList';
import MessageThread from '../components/messages/MessageThread';
import LoadingSpinner from '../components/common/LoadingSpinner';
import toast from 'react-hot-toast';

const MessagesPage = () => {
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [connections, setConnections] = useState([]);

  useEffect(() => {
    loadConnections();
  }, []);

  const loadConnections = async () => {
    try {
      const response = await connectionAPI.getAcceptedConnections();
      const currentUserId = JSON.parse(localStorage.getItem('user')).id;
      const connectionsWithUsers = await Promise.all(
        response.data.map(async (conn) => {
          const otherUserId = conn.demandeurId === currentUserId ? conn.destinataireId : conn.demandeurId;
          try {
            const userRes = await userAPI.getUser(otherUserId);
            return { ...conn, user: userRes.data };
          } catch (error) {
            return { ...conn, user: null };
          }
        })
      );
      setConnections(connectionsWithUsers);
    } catch (error) {
      console.error('Erreur chargement connexions:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadConversation = async (userId) => {
    try {
      setLoading(true);
      const response = await messageAPI.getConversation(userId);
      setMessages(response.data);
      setSelectedConversation(userId);
      const currentUserId = JSON.parse(localStorage.getItem('user')).id;
      const unreadMessages = response.data.filter(m => !m.lu && m.destinataireId === currentUserId);
      for (const message of unreadMessages) {
        await messageAPI.markAsRead(message.id);
      }
    } catch (error) {
      toast.error('Erreur lors du chargement de la conversation');
    } finally {
      setLoading(false);
    }
  };

  const handleSendMessage = async (content) => {
    if (!selectedConversation || !content.trim()) return;
    try {
      const response = await messageAPI.sendMessage(selectedConversation, content);
      setMessages([...messages, response.data]);
    } catch (error) {
      toast.error('Erreur lors de l\'envoi du message');
    }
  };

  const handleMessageDeleted = (deletedMessageId) => {
    setMessages(prev => prev.filter(m => m.id !== deletedMessageId));
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="page-shell">
      <div className="page-container-wide">
        <header className="page-header">
          <h1 className="page-title">Messagerie</h1>
          <p className="page-subtitle">Vos conversations avec votre réseau</p>
        </header>

        <div className="panel flex h-[600px]">
          <div className="w-[280px] shrink-0 flex flex-col border-r overflow-hidden" style={{ borderColor: 'var(--color-border)' }}>
            <div className="px-5 py-4 flex items-center gap-2 border-b shrink-0" style={{ borderColor: 'var(--color-border)' }}>
              <div className="w-1.5 h-1.5 rounded-full" style={{ background: 'var(--gradient-brand)' }} />
              <h2 className="text-xs font-semibold uppercase tracking-wider" style={{ color: 'var(--color-text-muted)' }}>
                Conversations
              </h2>
            </div>
            <div className="flex-1 overflow-y-auto">
              <ConversationList
                connections={connections}
                onSelectConversation={loadConversation}
                selectedUserId={selectedConversation}
              />
            </div>
          </div>

          <div className="flex-1 flex flex-col min-w-0">
            {selectedConversation ? (
              <MessageThread
                messages={messages}
                onSendMessage={handleSendMessage}
                recipientId={selectedConversation}
                onMessageDeleted={handleMessageDeleted}
              />
            ) : (
              <div className="empty-state h-full">
                <div className="icon-box" aria-hidden="true">💬</div>
                <p className="empty-state-text">Sélectionnez une conversation pour commencer à discuter</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default MessagesPage;
