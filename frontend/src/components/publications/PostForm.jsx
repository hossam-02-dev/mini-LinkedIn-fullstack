import React, { useState } from 'react';
import { publicationAPI } from '../../api/publicationAPI';
import ImageUploader from '../common/ImageUploader';
import { FaImage } from 'react-icons/fa';
import toast from 'react-hot-toast';
import LoadingSpinner from '../common/LoadingSpinner';

const PostForm = ({ onSuccess, initialContent = '', isEditing = false, postId = null, initialImageUrl = null }) => {
  const [content, setContent] = useState(initialContent);
  const [imageUrl, setImageUrl] = useState(initialImageUrl);
  const [showImageUploader, setShowImageUploader] = useState(!!initialImageUrl);
  const [loading, setLoading] = useState(false);

  const handleImageUploaded = (uploadedUrl) => {
    console.log('🔍 [PostForm] handleImageUploaded reçu:', uploadedUrl);
    setImageUrl(uploadedUrl);
    toast.success('Image ajoutée à votre publication');
  };

  const handleImageRemoved = () => {
    console.log('🔍 [PostForm] Image retirée');
    setImageUrl(null);
    if (!showImageUploader) {
      setShowImageUploader(false);
    }
    toast.info('Image retirée');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!content.trim() && !imageUrl) {
      toast.error('Veuillez ajouter du texte ou une image');
      return;
    }

    console.log('🔍 [PostForm] handleSubmit - contenu:', content);
    console.log('🔍 [PostForm] handleSubmit - imageUrl state:', imageUrl);

    setLoading(true);
    try {
      const publicationData = {
        contenu: content,
        imageUrl: imageUrl || null
      };
      console.log('🔍 [PostForm] Données envoyées au backend:', publicationData);
      
      if (isEditing && postId) {
        await publicationAPI.updatePublication(postId, publicationData);
        toast.success('Publication modifiée');
      } else {
        const response = await publicationAPI.createPublication(publicationData);
        console.log('🔍 [PostForm] Réponse backend:', response.data);
      }
      
      setContent('');
      setImageUrl(null);
      setShowImageUploader(false);
      
      if (onSuccess) onSuccess();
    } catch (error) {
      console.error('🔍 [PostForm] Erreur:', error);
      toast.error(error.response?.data?.message || 'Erreur lors de la publication');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder="Partagez quelque chose avec votre réseau..."
        className="textarea-field"
        rows="3"
      />
      
      {showImageUploader ? (
        <div className="mt-3">
          <ImageUploader
            onImageUploaded={handleImageUploaded}
            onRemove={handleImageRemoved}
            currentImageUrl={imageUrl}
          />
        </div>
      ) : (
        <button
          type="button"
          onClick={() => setShowImageUploader(true)}
          className="btn-ghost mt-3"
        >
          <FaImage />
          <span>Ajouter une image</span>
        </button>
      )}
      
      <div className="flex justify-end mt-3">
        <button type="submit" disabled={loading} className="btn-primary">
          {loading ? <LoadingSpinner size="sm" /> : (isEditing ? 'Modifier' : 'Publier')}
        </button>
      </div>
    </form>
  );
};

export default PostForm;
