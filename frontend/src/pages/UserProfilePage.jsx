import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { userAPI } from '../api/userAPI';
import { publicationAPI } from '../api/publicationAPI';
import { connectionAPI } from '../api/connectionAPI';
import { profileAPI } from '../api/profileAPI';
import ProfileHeader from '../components/profile/ProfileHeader';
import PostCard from '../components/publications/PostCard';
import ConnectionButton from '../components/connections/ConnectionButton';
import LoadingSpinner from '../components/common/LoadingSpinner';
import toast from 'react-hot-toast';

const UserProfilePage = () => {
  const { userId } = useParams();
  const [user, setUser] = useState(null);
  const [posts, setPosts] = useState([]);
  const [connectionStatus, setConnectionStatus] = useState('default');
  const [connexionId, setConnexionId] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadUserProfile();
    loadUserPosts();
    checkConnectionStatus();
  }, [userId]);

  const loadUserProfile = async () => {
    try {
      const response = await userAPI.getUser(userId);
      setUser(response.data);
      // Déclencher la comptabilisation de la vue sur le backend
      await profileAPI.getProfile(userId);
    } catch (error) {
      toast.error('Erreur lors du chargement du profil');
    }
  };

  const loadUserPosts = async () => {
    try {
      const response = await publicationAPI.getUserPublications(userId);
      setPosts(response.data);
    } catch (error) {
      console.error('Erreur chargement posts:', error);
    } finally {
      setLoading(false);
    }
  };

  const checkConnectionStatus = async () => {
    try {
      const response = await connectionAPI.getConnectionStatus(userId);
      setConnectionStatus(response.data.status);
      if (response.data.connexionId) {
        setConnexionId(response.data.connexionId);
      }
    } catch (error) {
      console.error('Erreur vérification connexion:', error);
    }
  };

  const handleSendRequest = async () => {
    try {
      const response = await connectionAPI.sendRequest(userId);
      setConnectionStatus('pending');
      toast.success('Demande de connexion envoyée');
    } catch (error) {
      toast.error('Erreur lors de l\'envoi de la demande');
    }
  };

  const handleAcceptRequest = async () => {
    try {
      await connectionAPI.acceptRequest(connexionId);
      setConnectionStatus('connected');
      toast.success('Demande acceptée');
    } catch (error) {
      toast.error('Erreur lors de l\'acceptation');
    }
  };

  const handleRefuseRequest = async () => {
    try {
      await connectionAPI.refuseRequest(connexionId);
      setConnectionStatus('default');
      toast.success('Demande refusée');
    } catch (error) {
      toast.error('Erreur lors du refus');
    }
  };

  if (loading) return <LoadingSpinner />;
  if (!user) return (
    <div className="page-shell flex items-center justify-center">
      <p className="empty-state-text">Utilisateur non trouvé</p>
    </div>
  );

  return (
    <div className="page-shell">
      <div className="page-glow" aria-hidden="true" />
      <div className="page-container-md">
        <div className="card p-6 mb-6">
          <div className="flex justify-between items-start gap-4">
            <div className="flex-1 min-w-0">
              <h1 className="page-title">
                {user.firstName} {user.lastName}
              </h1>
              <p className="mt-1" style={{ color: 'var(--color-text-secondary)' }}>{user.email}</p>
              <p className="text-sm mt-1" style={{ color: 'var(--color-text-muted)' }}>
                Membre depuis {new Date(user.createdAt).toLocaleDateString()}
              </p>
            </div>
            <ConnectionButton
              status={connectionStatus}
              onSendRequest={handleSendRequest}
              onAccept={handleAcceptRequest}
              onRefuse={handleRefuseRequest}
              connexionId={connexionId}
            />
          </div>
        </div>

        <div className="space-y-4">
          <h2 className="section-title">Publications</h2>
          {posts.length === 0 ? (
            <div className="card empty-state">
              <p className="empty-state-text">Aucune publication</p>
            </div>
          ) : (
            posts.map(post => (
              <div key={post.id} className="card card-interactive overflow-hidden">
                <PostCard
                  post={post}
                  isOwner={false}
                  onDelete={() => loadUserPosts()}
                  onUpdate={() => loadUserPosts()}
                />
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default UserProfilePage;