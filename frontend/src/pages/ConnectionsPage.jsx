import React, { useState, useEffect } from 'react';
import { connectionAPI } from '../api/connectionAPI';
import { userAPI } from '../api/userAPI';
import ConnectionRequestCard from "../components/connections/ConnectionRequest";
import FriendList from '../components/connections/FriendList';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { FaUserFriends, FaUserPlus } from 'react-icons/fa';
import toast from 'react-hot-toast';

const ConnectionsPage = () => {
  const [receivedRequests, setReceivedRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('connections');

  useEffect(() => {
    loadReceivedRequests();
  }, []);

  const loadReceivedRequests = async () => {
    try {
      const response = await connectionAPI.getReceivedRequests();
      const requestsWithUsers = await Promise.all(
        response.data.map(async (request) => {
          try {
            const userRes = await userAPI.getUser(request.demandeurId);
            return { ...request, user: userRes.data };
          } catch (error) {
            return request;
          }
        })
      );
      setReceivedRequests(requestsWithUsers);
    } catch (error) {
      toast.error('Erreur lors du chargement des demandes');
    } finally {
      setLoading(false);
    }
  };

  const handleAccept = async (connexionId) => {
    try {
      await connectionAPI.acceptRequest(connexionId);
      toast.success('Demande acceptée');
      loadReceivedRequests();
    } catch (error) {
      toast.error('Erreur lors de l\'acceptation');
    }
  };

  const handleRefuse = async (connexionId) => {
    try {
      await connectionAPI.refuseRequest(connexionId);
      toast.success('Demande refusée');
      loadReceivedRequests();
    } catch (error) {
      toast.error('Erreur lors du refus');
    }
  };

  const handleConnectionRemoved = (userId) => {
    console.log('Connexion retirée pour l\'utilisateur:', userId);
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="page-shell">
      <div className="page-container-md">
        <header className="page-header">
          <h1 className="page-title">Mon Réseau</h1>
          <p className="page-subtitle">Gérez vos connexions et demandes en attente</p>
        </header>

        <div className="panel">
          <div className="tabs-bar">
            <button
              onClick={() => setActiveTab('connections')}
              className={`tab-btn ${activeTab === 'connections' ? 'tab-btn-active' : ''}`}
            >
              <FaUserFriends />
              <span>Mes connexions</span>
            </button>
            <button
              onClick={() => setActiveTab('requests')}
              className={`tab-btn ${activeTab === 'requests' ? 'tab-btn-active' : ''}`}
            >
              <FaUserPlus />
              <span>Demandes reçues</span>
              {receivedRequests.length > 0 && (
                <span className="badge">{receivedRequests.length}</span>
              )}
            </button>
          </div>

          <div className="p-6">
            {activeTab === 'connections' ? (
              <FriendList onConnectionRemoved={handleConnectionRemoved} />
            ) : (
              <div className="flex flex-col gap-3">
                {receivedRequests.length === 0 ? (
                  <div className="empty-state animate-fadeInUp">
                    <div className="icon-box" aria-hidden="true">🤝</div>
                    <p className="empty-state-text">Aucune demande de connexion en attente</p>
                  </div>
                ) : (
                  receivedRequests.map(request => (
                    <div key={request.id} className="card card-interactive animate-fadeInUp overflow-hidden">
                      <ConnectionRequestCard
                        request={request}
                        onAccept={() => handleAccept(request.id)}
                        onRefuse={() => handleRefuse(request.id)}
                      />
                    </div>
                  ))
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConnectionsPage;
