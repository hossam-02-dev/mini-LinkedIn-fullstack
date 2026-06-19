import React, { useState } from 'react';
import { formatDistanceToNow } from 'date-fns';
import { fr } from 'date-fns/locale';
import { FaComment, FaTrash, FaEdit, FaUser } from 'react-icons/fa';
import { publicationAPI } from '../../api/publicationAPI';
import CommentSection from './CommentSection';
import LikeButton from './LikeButton';
import toast from 'react-hot-toast';

const PostCard = ({ post, isOwner, onDelete, onUpdate }) => {
  const [showComments, setShowComments] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editedContent, setEditedContent] = useState(post.contenu);
  const [editedImageUrl, setEditedImageUrl] = useState(post.imageUrl);

  const handleDelete = async () => {
    if (window.confirm('Voulez-vous vraiment supprimer cette publication ?')) {
      try {
        await publicationAPI.deletePublication(post.id);
        toast.success('Publication supprimée');
        onDelete(post.id);
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const handleUpdate = async () => {
    try {
      await publicationAPI.updatePublication(post.id, { 
        contenu: editedContent, 
        imageUrl: editedImageUrl || null
      });
      toast.success('Publication mise à jour');
      setIsEditing(false);
      onUpdate();
    } catch (error) {
      toast.error('Erreur lors de la mise à jour');
    }
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    setEditedContent(post.contenu);
    setEditedImageUrl(post.imageUrl);
  };

  const getImageUrl = (url) => {
    if (!url) return null;
    if (url.startsWith('http')) return url;
    return `http://localhost:8080${url}`;
  };

  const getPhotoUrl = (url) => {
    if (!url) return null;
    if (url.startsWith('http')) return url;
    return `http://localhost:8080${url}`;
  };

  return (
    <div className="post-card">
      <div className="flex items-center justify-between p-4">
        <div className="flex items-center gap-3">
          <div className="avatar-ring">
            {post.photoProfil ? (
              <img src={getPhotoUrl(post.photoProfil)} alt="Avatar" className="w-full h-full object-cover" />
            ) : (
              <FaUser className="text-primary" style={{ color: 'var(--color-primary)' }} />
            )}
          </div>
          <div>
            <h3 className="font-semibold text-sm" style={{ color: 'var(--color-text)' }}>
              {post.nomAuteur || `Utilisateur ${post.auteurId}`}
            </h3>
            <p className="text-xs" style={{ color: 'var(--color-text-muted)' }}>
              {formatDistanceToNow(new Date(post.datePublication), { addSuffix: true, locale: fr })}
              {post.dateMaj && post.dateMaj !== post.datePublication && (
                <span className="ml-1">(modifié)</span>
              )}
            </p>
          </div>
        </div>
        {isOwner && !isEditing && (
          <div className="flex gap-2">
            <button onClick={() => setIsEditing(true)} className="btn-ghost p-2" style={{ color: 'var(--color-primary)' }}>
              <FaEdit />
            </button>
            <button onClick={handleDelete} className="btn-ghost p-2 text-red-500">
              <FaTrash />
            </button>
          </div>
        )}
      </div>

      {isEditing ? (
        <div className="px-4 pb-4">
          <textarea
            value={editedContent}
            onChange={(e) => setEditedContent(e.target.value)}
            className="textarea-field"
            rows="4"
          />
          {editedImageUrl && (
            <div className="relative mt-3 inline-block">
              <img src={getImageUrl(editedImageUrl)} alt="Aperçu" className="h-32 w-auto rounded-lg object-cover border" style={{ borderColor: 'var(--color-border)' }} />
              <button onClick={() => setEditedImageUrl(null)} className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1">
                <FaTrash size={12} />
              </button>
            </div>
          )}
          <div className="flex justify-end gap-2 mt-3">
            <button onClick={handleCancelEdit} className="btn-secondary">Annuler</button>
            <button onClick={handleUpdate} className="btn-primary">Enregistrer</button>
          </div>
        </div>
      ) : (
        <>
          <p className="px-4 pb-2 whitespace-pre-wrap text-sm leading-relaxed" style={{ color: 'var(--color-text-secondary)' }}>{post.contenu}</p>
          {post.imageUrl && (
            <div className="px-4 pb-3">
              <img src={getImageUrl(post.imageUrl)} alt="Image" className="rounded-xl max-h-96 w-full object-cover" />
            </div>
          )}
        </>
      )}

      {!isEditing && (
        <>
          <div className="flex items-center gap-6 px-4 py-2.5 border-t" style={{ borderColor: 'var(--color-border)' }}>
            <LikeButton publicationId={post.id} />
            <button
              onClick={() => setShowComments(!showComments)}
              className="btn-ghost gap-2"
            >
              <FaComment />
              <span>Commenter</span>
            </button>
          </div>
          {showComments && <CommentSection publicationId={post.id} />}
        </>
      )}
    </div>
  );
};

export default PostCard;
