import React, { useState, useRef } from 'react';
import { profileAPI } from '../../api/profileAPI';
import { uploadAPI } from '../../api/uploadAPI';
import { FaEdit, FaCamera, FaMapMarkerAlt, FaBuilding, FaGlobe, FaCalendarAlt, FaSpinner } from 'react-icons/fa';
import toast from 'react-hot-toast';
import LoadingSpinner from '../common/LoadingSpinner';

const ProfileHeader = ({ profile, onUpdate, onReload }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [uploadingPhoto, setUploadingPhoto] = useState(false);
  const fileInputRef = useRef(null);
  
  const [formData, setFormData] = useState({
    name: profile.name || '',
    ville: profile.ville || '',
    etablissement: profile.etablissement || '',
    bio: profile.bio || '',
    siteWeb: profile.siteWeb || '',
    dateNaissance: profile.dateNaissance || '',
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await profileAPI.updateProfile(profile.id, formData);
      onUpdate(response.data);
      setIsEditing(false);
      toast.success('Profil mis à jour');
    } catch (error) {
      toast.error('Erreur lors de la mise à jour');
    } finally {
      setLoading(false);
    }
  };

  const handlePhotoUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      toast.error('Veuillez sélectionner une image');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      toast.error('L\'image ne doit pas dépasser 5MB');
      return;
    }
    setIsEditing(false);
    setUploadingPhoto(true);
    try {
      const uploadResponse = await uploadAPI.uploadImage(file);
      const photoUrl = uploadResponse.data.imageUrl;
      await profileAPI.updatePhoto(profile.id, photoUrl);
      if (onReload) {
        await onReload();
      } else if (onUpdate) {
        onUpdate({ ...profile, photoUrl });
      }
      toast.success('Photo de profil mise à jour');
    } catch (error) {
      toast.error('Erreur lors de l\'upload');
    } finally {
      setUploadingPhoto(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  const triggerFileInput = () => {
    fileInputRef.current.click();
  };

  const getPhotoUrl = (url) => {
    if (!url) return '/default-avatar.png';
    if (url.startsWith('http')) return url;
    return `http://localhost:8080${url}`;
  };

  return (
    <div className="overflow-hidden">
      <div className="profile-banner">
        <button
          onClick={triggerFileInput}
          className="absolute bottom-4 right-4 btn-outline p-2.5 bg-white/90 dark:bg-gray-800/90"
          disabled={uploadingPhoto}
        >
          {uploadingPhoto ? <FaSpinner className="animate-spin" /> : <FaCamera />}
        </button>
        <input type="file" ref={fileInputRef} onChange={handlePhotoUpload} accept="image/*" className="hidden" />
      </div>

      <div className="relative px-6">
        <img
          src={getPhotoUrl(profile.photoUrl)}
          alt="Profile"
          className="avatar-lg absolute -top-16 object-cover shadow-lg"
        />
      </div>

      <div className="px-6 pt-20 pb-6">
        <div className="flex justify-between items-start gap-4">
          <div className="flex-1 min-w-0">
            {isEditing ? (
              <form onSubmit={handleSubmit} className="space-y-3">
                <input type="text" name="name" value={formData.name} onChange={handleChange} placeholder="Nom complet" className="input-field" />
                <input type="text" name="ville" value={formData.ville} onChange={handleChange} placeholder="Ville" className="input-field" />
                <input type="text" name="etablissement" value={formData.etablissement} onChange={handleChange} placeholder="Établissement" className="input-field" />
                <textarea name="bio" value={formData.bio} onChange={handleChange} placeholder="Bio" rows="3" className="textarea-field" />
                <input type="url" name="siteWeb" value={formData.siteWeb} onChange={handleChange} placeholder="Site web" className="input-field" />
                <input type="date" name="dateNaissance" value={formData.dateNaissance} onChange={handleChange} className="input-field" />
                <div className="flex gap-2">
                  <button type="submit" disabled={loading} className="btn-primary">
                    {loading ? <LoadingSpinner size="sm" /> : 'Enregistrer'}
                  </button>
                  <button type="button" onClick={() => setIsEditing(false)} className="btn-secondary">Annuler</button>
                </div>
              </form>
            ) : (
              <>
                <h1 className="text-2xl font-bold" style={{ color: 'var(--color-text)' }}>{profile.name}</h1>
                {profile.bio && <p className="mt-2 text-sm leading-relaxed" style={{ color: 'var(--color-text-secondary)' }}>{profile.bio}</p>}
              </>
            )}
          </div>
          {!isEditing && (
            <button onClick={() => setIsEditing(true)} className="btn-outline shrink-0">
              <FaEdit />
              <span>Modifier</span>
            </button>
          )}
        </div>

        {!isEditing && (
          <div className="mt-4 space-y-2 text-sm" style={{ color: 'var(--color-text-secondary)' }}>
            {profile.ville && (
              <div className="flex items-center gap-2">
                <FaMapMarkerAlt style={{ color: 'var(--color-primary)' }} />
                <span>{profile.ville}</span>
              </div>
            )}
            {profile.etablissement && (
              <div className="flex items-center gap-2">
                <FaBuilding style={{ color: 'var(--color-primary)' }} />
                <span>{profile.etablissement}</span>
              </div>
            )}
            {profile.siteWeb && (
              <div className="flex items-center gap-2">
                <FaGlobe style={{ color: 'var(--color-primary)' }} />
                <a href={profile.siteWeb} target="_blank" rel="noopener noreferrer" className="link-accent">
                  {profile.siteWeb}
                </a>
              </div>
            )}
            {profile.dateNaissance && (
              <div className="flex items-center gap-2">
                <FaCalendarAlt style={{ color: 'var(--color-primary)' }} />
                <span>Né(e) le {new Date(profile.dateNaissance).toLocaleDateString()}</span>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfileHeader;
