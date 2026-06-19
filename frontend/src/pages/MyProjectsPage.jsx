import React, { useState, useEffect } from 'react';
import { projectAPI } from '../api/projectAPI';
import ProjectCard from '../components/profile/ProjectCard';
import ProjectForm from '../components/profile/ProjectForm';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { FaPlus } from 'react-icons/fa';
import toast from 'react-hot-toast';

const MyProjectsPage = () => {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    try {
      const response = await projectAPI.getMyProjects();
      setProjects(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement de vos projets');
    } finally {
      setLoading(false);
    }
  };

  const handleProjectCreated = () => {
    loadProjects();
    setShowForm(false);
    toast.success('Projet créé avec succès');
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="page-shell">
      <div className="page-container-wide">
        <header className="page-header flex flex-wrap items-center justify-between gap-4">
          <div>
            <h1 className="page-title">Mes Projets</h1>
            <p className="page-subtitle">Vos réalisations professionnelles</p>
          </div>
          <button onClick={() => setShowForm(!showForm)} className="btn-primary">
            <FaPlus />
            <span>Ajouter un projet</span>
          </button>
        </header>

        {showForm && (
          <div className="card p-5 mb-6">
            <ProjectForm onSuccess={handleProjectCreated} onCancel={() => setShowForm(false)} />
          </div>
        )}

        {projects.length === 0 ? (
          <div className="card empty-state">
            <p className="empty-state-text">Vous n&apos;avez pas encore de projets.</p>
            <p className="empty-state-subtext">Commencez à partager vos réalisations !</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {projects.map(project => (
              <ProjectCard key={project.id} project={project} onUpdate={loadProjects} isOwner />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default MyProjectsPage;