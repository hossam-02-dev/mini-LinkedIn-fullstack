import React, { useState, useEffect } from 'react';
import { publicationAPI } from '../../api/publicationAPI';
import { FaUser, FaEdit, FaTrash } from 'react-icons/fa';
import { formatDistanceToNow } from 'date-fns';
import { fr } from 'date-fns/locale';
import toast from 'react-hot-toast';
import LoadingSpinner from '../common/LoadingSpinner';

const CommentSection = ({ publicationId }) => {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [editingComment, setEditingComment] = useState(null);
  // ✅ Correction : récupérer l'id depuis l'objet user stocké
  const currentUserId = JSON.parse(localStorage.getItem('user')).id;

  useEffect(() => {
    loadComments();
  }, [publicationId]);

  const loadComments = async () => {
    try {
      const response = await publicationAPI.getComments(publicationId);
      setComments(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des commentaires');
    } finally {
      setLoading(false);
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    try {
      await publicationAPI.addComment(publicationId, {
        texte: newComment,
        publicationId,
        auteurId: currentUserId,
      });
      setNewComment('');
      loadComments();
      toast.success('Commentaire ajouté');
    } catch (error) {
      console.error(error);
      toast.error('Erreur lors de l\'ajout du commentaire');
    }
  };

  const handleUpdateComment = async (commentId, newText) => {
    try {
      await publicationAPI.updateComment(commentId, { texte: newText });
      setEditingComment(null);
      loadComments();
      toast.success('Commentaire modifié');
    } catch (error) {
      toast.error('Erreur lors de la modification');
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (window.confirm('Voulez-vous vraiment supprimer ce commentaire ?')) {
      try {
        await publicationAPI.deleteComment(commentId);
        loadComments();
        toast.success('Commentaire supprimé');
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  if (loading) return <LoadingSpinner size="sm" />;

  return (
    <div className="mt-4 pt-4 border-t px-4 pb-4" style={{ borderColor: 'var(--color-border)' }}>
      <h4 className="font-semibold mb-3">Commentaires ({comments.length})</h4>
      
      <form onSubmit={handleAddComment} className="mb-4">
        <div className="flex space-x-2">
          <input
            type="text"
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="Écrire un commentaire..."
            className="input-field flex-1"
          />
          <button
            type="submit"
            className="btn-primary"
          >
            Envoyer
          </button>
        </div>
      </form>

      <div className="space-y-3">
        {comments.map(comment => (
          <div key={comment.id} className="bg-gray-50 rounded-lg p-3">
            {editingComment === comment.id ? (
              <div className="space-y-2">
                <textarea
                  defaultValue={comment.texte}
                  className="w-full p-2 border rounded-lg"
                  rows="2"
                  id={`edit-comment-${comment.id}`}
                />
                <div className="flex space-x-2">
                  <button
                    onClick={() => {
                      const textarea = document.getElementById(`edit-comment-${comment.id}`);
                      handleUpdateComment(comment.id, textarea.value);
                    }}
                    className="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700"
                  >
                    Sauvegarder
                  </button>
                  <button
                    onClick={() => setEditingComment(null)}
                    className="px-3 py-1 bg-gray-300 rounded hover:bg-gray-400"
                  >
                    Annuler
                  </button>
                </div>
              </div>
            ) : (
              <>
                <div className="flex justify-between items-start">
                  <div className="flex items-center space-x-2">
                    <FaUser className="text-gray-400" />
                    <span className="font-medium">{comment.nomAuteur}</span>
                    <span className="text-xs text-gray-500">
                      {formatDistanceToNow(new Date(comment.date), { addSuffix: true, locale: fr })}
                    </span>
                  </div>
                  {comment.auteurId === currentUserId && (
                    <div className="flex space-x-2">
                      <button
                        onClick={() => setEditingComment(comment.id)}
                        className="text-blue-600 hover:text-blue-800"
                      >
                        <FaEdit size={14} />
                      </button>
                      <button
                        onClick={() => handleDeleteComment(comment.id)}
                        className="text-red-600 hover:text-red-800"
                      >
                        <FaTrash size={14} />
                      </button>
                    </div>
                  )}
                </div>
                <p className="text-gray-700 mt-1">{comment.texte}</p>
              </>
            )}
          </div>
        ))}
        
        {comments.length === 0 && (
          <p className="text-gray-500 text-center py-4">Aucun commentaire pour le moment</p>
        )}
      </div>
    </div>
  );
};

export default CommentSection;