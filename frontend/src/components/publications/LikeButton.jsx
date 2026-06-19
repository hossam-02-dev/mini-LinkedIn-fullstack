import React, { useState, useEffect } from 'react';
import { publicationAPI } from '../../api/publicationAPI';
import { FaHeart, FaRegHeart } from 'react-icons/fa';
import toast from 'react-hot-toast';

const LikeButton = ({ publicationId }) => {
  const [likeCount, setLikeCount] = useState(0);
  const [isLiked, setIsLiked] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadLikeCount();
    checkIfLiked();
  }, [publicationId]);

  const loadLikeCount = async () => {
    try {
      const response = await publicationAPI.getLikeCount(publicationId);
      setLikeCount(response.data);
    } catch (error) {
      console.error('Erreur chargement likes:', error);
    }
  };

  const checkIfLiked = async () => {
    // À implémenter avec un endpoint qui vérifie si l'utilisateur a liké
    // Pour l'instant, on part du principe que non
    setIsLiked(false);
  };

  const handleLike = async () => {
    if (loading) return;
    
    setLoading(true);
    try {
      if (isLiked) {
        await publicationAPI.unlikePublication(publicationId);
        setLikeCount(likeCount - 1);
        setIsLiked(false);
      } else {
        await publicationAPI.likePublication(publicationId);
        setLikeCount(likeCount + 1);
        setIsLiked(true);
      }
    } catch (error) {
      toast.error('Erreur lors de l\'interaction');
    } finally {
      setLoading(false);
    }
  };

  return (
    <button
      onClick={handleLike}
      disabled={loading}
      className={`btn-ghost gap-2 ${isLiked ? 'text-red-500' : ''}`}
    >
      {isLiked ? <FaHeart className="text-red-600" /> : <FaRegHeart />}
      <span>{likeCount > 0 && likeCount}</span>
    </button>
  );
};

export default LikeButton;