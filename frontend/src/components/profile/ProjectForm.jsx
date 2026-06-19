import React, { useState } from 'react';
import { projectAPI } from '../../api/projectAPI';
import ImageUploader from '../common/ImageUploader';
import { FaTimes } from 'react-icons/fa';
import toast from 'react-hot-toast';
import LoadingSpinner from '../common/LoadingSpinner';

const ProjectForm = ({ onSuccess, onCancel, initialData = null, isEditing = false }) => {
  const [formData, setFormData] = useState({
    titre: initialData?.titre || '',
    description: initialData?.description || '',
    technologies: initialData?.technologies || '',
    lienGithub: initialData?.lienGithub || '',
    lienDemo: initialData?.lienDemo || '',
    imageUrl: initialData?.imageUrl || '',
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleImageUploaded = (url) => {
    setFormData({ ...formData, imageUrl: url });
    toast.success('Image ajoutée au projet');
  };

  const handleImageRemoved = () => {
    setFormData({ ...formData, imageUrl: '' });
    toast.info('Image retirée');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.titre || !formData.description) {
      toast.error('Le titre et la description sont requis');
      return;
    }
    setLoading(true);
    try {
      if (isEditing && initialData) {
        await projectAPI.updateProject(initialData.id, formData);
        toast.success('Projet mis à jour');
      } else {
        await projectAPI.createProject(formData);
        toast.success('Projet créé avec succès');
      }
      if (onSuccess) onSuccess();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Erreur lors de l\'enregistrement');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-1">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold text-gray-800 dark:text-white">
          {isEditing ? 'Modifier le projet' : 'Nouveau projet'}
        </h2>
        {onCancel && (
          <button onClick={onCancel} className="text-gray-500 hover:text-gray-700 dark:text-gray-400">
            <FaTimes size={20} />
          </button>
        )}
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Titre du projet *</label>
          <input
            type="text"
            name="titre"
            value={formData.titre}
            onChange={handleChange}
            className="input-field"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Description *</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="4"
            className="input-field"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Technologies (séparées par des virgules)</label>
          <input
            type="text"
            name="technologies"
            value={formData.technologies}
            onChange={handleChange}
            placeholder="React, Spring Boot, MySQL"
            className="input-field"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Lien GitHub</label>
          <input
            type="url"
            name="lienGithub"
            value={formData.lienGithub}
            onChange={handleChange}
            placeholder="https://github.com/..."
            className="input-field"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Lien de démonstration</label>
          <input
            type="url"
            name="lienDemo"
            value={formData.lienDemo}
            onChange={handleChange}
            placeholder="https://..."
            className="input-field"
          />
        </div>

        {/* Composant d'upload d'image */}
        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Image du projet</label>
          <ImageUploader
            onImageUploaded={handleImageUploaded}
            onRemove={handleImageRemoved}
            currentImageUrl={formData.imageUrl}
          />
        </div>

        <div className="flex justify-end space-x-3 pt-4">
          {onCancel && (
            <button type="button" onClick={onCancel} className="px-4 py-2 bg-gray-300 dark:bg-gray-600 text-gray-700 dark:text-gray-200 rounded-lg hover:bg-gray-400 dark:hover:bg-gray-500">
              Annuler
            </button>
          )}
          <button type="submit" disabled={loading} className="btn-primary">
            {loading ? <LoadingSpinner size="sm" /> : (isEditing ? 'Mettre à jour' : 'Publier')}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProjectForm;