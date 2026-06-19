import React, { useState, useEffect } from 'react';
import { connectionAPI } from '../../api/connectionAPI';
import { userAPI } from '../../api/userAPI';
import FriendCard from './FriendCard';
import LoadingSpinner from '../common/LoadingSpinner';
import { FaUserFriends, FaSearch } from 'react-icons/fa';
import toast from 'react-hot-toast';

const FriendList = ({ onConnectionRemoved }) => {
  const [connections, setConnections] = useState([]);
  const [filteredConnections, setFilteredConnections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadConnections();
  }, []);

  useEffect(() => {
    filterConnections();
  }, [searchTerm, connections]);

  const loadConnections = async () => {
    try {
      setLoading(true);
      const currentUser = JSON.parse(localStorage.getItem('user'));
      const response = await connectionAPI.getAcceptedConnections();
      const connectionsWithUsers = await Promise.all(
        response.data.map(async (connection) => {
          // Identify the OTHER user in the connection
          const otherUserId = connection.demandeurId === currentUser.id 
            ? connection.destinataireId 
            : connection.demandeurId;
            
          try {
            const userRes = await userAPI.getUser(otherUserId);
            return { ...connection, user: userRes.data };
          } catch (error) {
            return connection;
          }
        })
      );
      setConnections(connectionsWithUsers);
      setFilteredConnections(connectionsWithUsers);
    } catch (error) {
      toast.error('Erreur lors du chargement de vos connexions');
    } finally {
      setLoading(false);
    }
  };

  const filterConnections = () => {
    if (!searchTerm.trim()) {
      setFilteredConnections(connections);
    } else {
      const filtered = connections.filter(conn => 
        conn.user?.firstName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        conn.user?.lastName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        conn.user?.email?.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setFilteredConnections(filtered);
    }
  };

  const handleRemoveConnection = async (connectionId, userId) => {
    try {
      await connectionAPI.removeConnection(connectionId);
      toast.success('Connexion retirée');
      setConnections(connections.filter(c => c.id !== connectionId));
      setFilteredConnections(filteredConnections.filter(c => c.id !== connectionId));
      if (onConnectionRemoved) onConnectionRemoved(userId);
    } catch (error) {
      toast.error('Erreur lors de la suppression');
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 animate-fadeInUp">
      <div className="flex justify-between items-center mb-6">
        <div className="flex items-center space-x-2">
          <FaUserFriends className="text-blue-600 dark:text-blue-400 text-xl" />
          <h2 className="text-xl font-bold text-gray-800 dark:text-white">Mes connexions</h2>
          <span className="bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 px-2 py-1 rounded-full text-sm">
            {connections.length}
          </span>
        </div>
      </div>
      <div className="relative mb-6">
        <FaSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500" />
        <input
          type="text"
          placeholder="Rechercher une connexion..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full pl-10 pr-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
        />
      </div>
      {filteredConnections.length === 0 ? (
        <div className="text-center py-8 animate-fadeInUp">
          {searchTerm ? (
            <p className="text-gray-500 dark:text-gray-400">Aucune connexion trouvée pour "{searchTerm}"</p>
          ) : (
            <div>
              <p className="text-gray-500 dark:text-gray-400 mb-4">Vous n'avez pas encore de connexions</p>
              <button onClick={() => window.location.href = '/feed'} className="text-blue-600 dark:text-blue-400 hover:underline">
                Découvrir des professionnels
              </button>
            </div>
          )}
        </div>
      ) : (
        <div className="space-y-3 max-h-96 overflow-y-auto">
          {filteredConnections.map((connection) => (
            <div key={connection.id} className="animate-fadeInUp">
              <FriendCard
                connection={connection}
                onRemove={() => handleRemoveConnection(connection.id, connection.user?.id)}
              />
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default FriendList;