import React, { useState } from 'react';
import { projectAPI } from '../../api/projectAPI';
import { FaGithub, FaExternalLinkAlt, FaEdit, FaTrash, FaCode } from 'react-icons/fa';
import { formatDistanceToNow } from 'date-fns';
import { fr } from 'date-fns/locale';
import toast from 'react-hot-toast';
import ProjectForm from './ProjectForm';

const ProjectCard = ({ project, onUpdate, isOwner = false }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [showDetails, setShowDetails] = useState(false);

  const handleDelete = async () => {
    if (window.confirm('Voulez-vous vraiment supprimer ce projet ?')) {
      try {
        await projectAPI.deleteProject(project.id);
        toast.success('Projet supprimé');
        if (onUpdate) onUpdate();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const handleUpdate = () => {
    setIsEditing(false);
    if (onUpdate) onUpdate();
  };

  if (isEditing) {
    return (
      <div className="col-span-full">
        <ProjectForm
          initialData={project}
          onSuccess={handleUpdate}
          onCancel={() => setIsEditing(false)}
          isEditing
        />
      </div>
    );
  }

  // Construction de l'URL de l'image
  const getImageUrl = (url) => {
    if (!url) return null;
    if (url.startsWith('http')) return url;
    return `http://localhost:8080${url}`;
  };

  return (
    <div className="card card-interactive overflow-hidden h-full flex flex-col">
      {/* Image du projet */}
      {project.imageUrl && (
        <img
          src={getImageUrl(project.imageUrl)}
          alt={project.titre}
          className="w-full h-48 object-cover"
          onError={(e) => {
            e.target.style.display = 'none';
          }}
        />
      )}
      {/* Optionnel : un placeholder si pas d'image */}
      {!project.imageUrl && (
        <div className="w-full h-48 flex items-center justify-center" style={{ background: 'var(--color-surface-muted)', color: 'var(--color-text-muted)' }}>
          <FaCode className="text-4xl" />
        </div>
      )}
      <div className="p-5">
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-lg font-bold" style={{ color: 'var(--color-text)' }}>{project.titre}</h3>
          {isOwner && (
            <div className="flex gap-2">
              <button onClick={() => setIsEditing(true)} className="btn-ghost p-1.5" style={{ color: 'var(--color-primary)' }}>
                <FaEdit />
              </button>
              <button onClick={handleDelete} className="btn-ghost p-1.5 text-red-500">
                <FaTrash />
              </button>
            </div>
          )}
        </div>
        <p className="text-sm mb-2" style={{ color: 'var(--color-text-muted)' }}>
          Par {project.nomAuteur} • {formatDistanceToNow(new Date(project.dateCreation), { addSuffix: true, locale: fr })}
        </p>
        <p className="mb-3 line-clamp-2 text-sm" style={{ color: 'var(--color-text-secondary)' }}>{project.description}</p>
        {project.technologies && (
          <div className="flex flex-wrap gap-1 mb-3">
            {project.technologies.split(',').map((tech, idx) => (
              <span key={idx} className="text-xs px-2 py-1 rounded-md font-medium" style={{ background: 'rgba(99,102,241,0.1)', color: 'var(--color-primary)' }}>
                {tech.trim()}
              </span>
            ))}
          </div>
        )}
        <div className="flex space-x-3">
          {project.lienGithub && (
            <a href={project.lienGithub} target="_blank" rel="noopener noreferrer" className="btn-ghost gap-1 py-1 px-2">
              <FaGithub /><span className="text-sm">GitHub</span>
            </a>
          )}
          {project.lienDemo && (
            <a href={project.lienDemo} target="_blank" rel="noopener noreferrer" className="btn-ghost gap-1 py-1 px-2">
              <FaExternalLinkAlt /><span className="text-sm">Démo</span>
            </a>
          )}
          <button onClick={() => setShowDetails(!showDetails)} className="btn-ghost gap-1 py-1 px-2">
            <FaCode /><span className="text-sm">{showDetails ? 'Moins' : 'Plus'}</span>
          </button>
        </div>
        {showDetails && (
          <div className="mt-4 pt-3 border-t" style={{ borderColor: 'var(--color-border)' }}>
            <p className="text-sm whitespace-pre-wrap" style={{ color: 'var(--color-text-secondary)' }}>{project.description}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectCard;