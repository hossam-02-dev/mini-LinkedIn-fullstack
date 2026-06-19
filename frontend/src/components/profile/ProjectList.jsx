import React, { useState, useEffect } from 'react';
import { projectAPI } from '../../api/projectAPI';
import ProjectCard from './ProjectCard';
import LoadingSpinner from '../common/LoadingSpinner';
import toast from 'react-hot-toast';

const ProjectList = ({ userId, isOwner = false }) => {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProjects();
  }, [userId]);

  const loadProjects = async () => {
    try {
      if (isOwner) {
        const response = await projectAPI.getMyProjects();
        setProjects(response.data);
      } else {
        const allProjects = await projectAPI.getAllProjects();
        const userProjects = allProjects.data.filter(p => p.auteurId === parseInt(userId));
        setProjects(userProjects);
      }
    } catch (error) {
      toast.error('Erreur lors du chargement des projets');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner size="sm" />;

  return (
    <div className="animate-fadeInUp">
      {projects.length === 0 ? (
        <div className="empty-state p-8">
          Aucun projet à afficher
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {projects.map(project => (
            <div key={project.id} className="animate-fadeInUp">
              <ProjectCard
                project={project}
                onUpdate={loadProjects}
                isOwner={isOwner}
              />
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ProjectList;