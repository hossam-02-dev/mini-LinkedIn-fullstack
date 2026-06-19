import React, { useState, useEffect } from 'react';
import { publicationAPI } from '../api/publicationAPI';
import PostCard from '../components/publications/PostCard';
import PostForm from '../components/publications/PostForm';
import LoadingSpinner from '../components/common/LoadingSpinner';
import toast from 'react-hot-toast';

const HomePage = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadFeed();
  }, []);

  const loadFeed = async () => {
    try {
      setLoading(true);
      const response = await publicationAPI.getFeed();
      setPosts(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement du feed');
    } finally {
      setLoading(false);
    }
  };

  const handlePostCreated = () => {
    loadFeed();
    toast.success('Publication créée avec succès !');
  };

  if (loading) return <LoadingSpinner />;

  const currentUserId = JSON.parse(localStorage.getItem('user')).id;

  return (
    <div className="page-shell">
      <div className="page-glow" aria-hidden="true" />

      <div className="page-container">
        <header className="page-header">
          <h1 className="page-title">Fil d&apos;actualité</h1>
          <p className="page-subtitle">Découvrez ce que partage votre réseau</p>
        </header>

        <div className="card p-5 mb-5">
          <PostForm onSuccess={handlePostCreated} />
        </div>

        <div className="flex flex-col gap-3.5">
          {posts.length === 0 ? (
            <div className="card empty-state animate-fadeInUp">
              <div className="icon-box" aria-hidden="true">📢</div>
              <p className="empty-state-text">Aucune publication pour le moment.</p>
              <p className="empty-state-subtext">Soyez le premier à partager quelque chose !</p>
            </div>
          ) : (
            posts.map((post) => (
              <div key={post.id} className="card card-interactive animate-fadeInUp overflow-hidden">
                <PostCard
                  post={post}
                  isOwner={post.auteurId === currentUserId}
                  onDelete={() => loadFeed()}
                  onUpdate={() => loadFeed()}
                />
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default HomePage;
