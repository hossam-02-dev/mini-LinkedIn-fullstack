import React, { useState, useEffect } from 'react';
import { projectAPI } from '../api/projectAPI';
import ProjectCard from '../components/profile/ProjectCard';
import ProjectForm from '../components/profile/ProjectForm';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { FaSearch, FaPlus } from 'react-icons/fa';
import toast from 'react-hot-toast';

const ProjectsPage = () => {
  const [projects, setProjects] = useState([]);
  const [filteredProjects, setFilteredProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [showMyProjects, setShowMyProjects] = useState(false);

  useEffect(() => {
    loadProjects();
  }, [showMyProjects]);

  useEffect(() => {
    if (searchTerm) {
      filterProjects();
    } else {
      setFilteredProjects(projects);
    }
  }, [searchTerm, projects]);

  const loadProjects = async () => {
    try {
      setLoading(true);
      let response;
      if (showMyProjects) {
        response = await projectAPI.getMyProjects();
      } else {
        response = await projectAPI.getAllProjects();
      }
      setProjects(response.data);
      setFilteredProjects(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des projets');
    } finally {
      setLoading(false);
    }
  };

  const filterProjects = async () => {
    if (searchTerm.trim()) {
      try {
        const response = await projectAPI.searchProjects(searchTerm);
        setFilteredProjects(response.data);
      } catch (error) {
        toast.error('Erreur lors de la recherche');
      }
    } else {
      setFilteredProjects(projects);
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
            <h1 className="page-title">Projets</h1>
            <p className="page-subtitle">Explorez et partagez vos réalisations</p>
          </div>
          <button onClick={() => setShowForm(!showForm)} className="btn-primary">
            <FaPlus />
            <span>Nouveau projet</span>
          </button>
        </header>

        {showForm && (
          <div className="card p-5 mb-6">
            <ProjectForm onSuccess={handleProjectCreated} onCancel={() => setShowForm(false)} />
          </div>
        )}

        <div className="flex flex-col md:flex-row justify-between gap-4 mb-6">
          <div className="flex gap-2">
            <button
              onClick={() => setShowMyProjects(false)}
              className={`tab-pill ${!showMyProjects ? 'tab-pill-active' : 'tab-pill-inactive'}`}
            >
              Tous les projets
            </button>
            <button
              onClick={() => setShowMyProjects(true)}
              className={`tab-pill ${showMyProjects ? 'tab-pill-active' : 'tab-pill-inactive'}`}
            >
              Mes projets
            </button>
          </div>

          <div className="input-with-icon relative flex-1 max-w-md">
            <FaSearch className="input-icon" />
            <input
              type="text"
              placeholder="Rechercher un projet..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="input-field input-field-icon"
            />
          </div>
        </div>

        {filteredProjects.length === 0 ? (
          <div className="card empty-state animate-fadeInUp">
            <p className="empty-state-text">Aucun projet trouvé</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {filteredProjects.map(project => (
              <div key={project.id} className="animate-fadeInUp">
                <ProjectCard
                  project={project}
                  onUpdate={loadProjects}
                  isOwner={showMyProjects}
                />
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectsPage;
