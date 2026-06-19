import React, { useState, useRef, useEffect } from 'react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { FaPaperPlane, FaTrash } from 'react-icons/fa';
import { messageAPI } from '../../api/messageAPI';
import toast from 'react-hot-toast';

const MessageThread = ({ messages, onSendMessage, recipientId, onMessageDeleted }) => {
  const [newMessage, setNewMessage] = useState('');
  const messagesEndRef = useRef(null);
  const currentUserId = JSON.parse(localStorage.getItem('user')).id;

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (newMessage.trim()) {
      onSendMessage(newMessage);
      setNewMessage('');
    }
  };

  const formatTime = (date) => {
    return format(new Date(date), 'HH:mm', { locale: fr });
  };

  const handleDelete = async (messageId) => {
    if (window.confirm('Voulez-vous vraiment supprimer ce message ?')) {
      try {
        await messageAPI.deleteMessage(messageId);
        if (onMessageDeleted) onMessageDeleted(messageId);
        toast.success('Message supprimé');
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  return (
    <>
      <div className="messages-area flex-1">
        {messages.map((message, index) => {
          const isOwn = message.expediteurId === currentUserId;
          return (
            <div key={index} className={`flex ${isOwn ? 'justify-end' : 'justify-start'}`}>
              <div className={isOwn ? 'message-bubble-own' : 'message-bubble-other'}>
                <p className="text-sm leading-relaxed">{message.contenu}</p>
                <div className={`text-xs mt-1 flex items-center gap-2 ${isOwn ? 'text-white/70' : ''}`} style={!isOwn ? { color: 'var(--color-text-muted)' } : undefined}>
                  <span>{formatTime(message.dateEnvoi)}</span>
                  {isOwn && message.lu && <span>✓✓</span>}
                  {isOwn && (
                    <button onClick={() => handleDelete(message.id)} className="opacity-70 hover:opacity-100 hover:text-red-300 transition-colors" title="Supprimer">
                      <FaTrash size={12} />
                    </button>
                  )}
                </div>
              </div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </div>
      <form onSubmit={handleSubmit} className="p-4 border-t flex gap-2" style={{ borderColor: 'var(--color-border)', background: 'var(--color-surface)' }}>
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Écrivez votre message..."
          className="input-field flex-1"
        />
        <button type="submit" className="btn-primary px-4">
          <FaPaperPlane />
        </button>
      </form>
    </>
  );
};

export default MessageThread;
