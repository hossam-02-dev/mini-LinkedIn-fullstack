import React, { useState, useEffect } from 'react';
import { profileAPI } from '../../api/profileAPI';
import { FaPlus, FaEdit, FaTrash } from 'react-icons/fa';
import toast from 'react-hot-toast';
import LoadingSpinner from '../common/LoadingSpinner';

const CompetenceList = ({ onUpdate }) => {
  const [competences, setCompetences] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingCompetence, setEditingCompetence] = useState(null);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({ nom: '', niveau: 'DEBUTANT' });

  const niveaux = ['DEBUTANT', 'INTERMEDIAIRE', 'AVANCE', 'EXPERT'];

  useEffect(() => {
    loadCompetences();
  }, []);

  const loadCompetences = async () => {
    try {
      const response = await profileAPI.getMyCompetences();
      setCompetences(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des compétences');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingCompetence) {
        await profileAPI.updateCompetence(editingCompetence.id, formData);
        toast.success('Compétence mise à jour');
      } else {
        await profileAPI.addCompetence(formData);
        toast.success('Compétence ajoutée');
      }
      resetForm();
      loadCompetences();
      if (onUpdate) onUpdate();
    } catch (error) {
      toast.error('Erreur lors de l\'enregistrement');
    }
  };

  const handleDelete = async (competenceId) => {
    if (window.confirm('Voulez-vous vraiment supprimer cette compétence ?')) {
      try {
        await profileAPI.deleteCompetence(competenceId);
        toast.success('Compétence supprimée');
        loadCompetences();
        if (onUpdate) onUpdate();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const resetForm = () => {
    setFormData({ nom: '', niveau: 'DEBUTANT' });
    setEditingCompetence(null);
    setShowForm(false);
  };

  const startEdit = (competence) => {
    setEditingCompetence(competence);
    setFormData({ nom: competence.nom, niveau: competence.niveau });
    setShowForm(true);
  };

  const getNiveauColor = (niveau) => {
    const colors = {
      DEBUTANT: 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300',
      INTERMEDIAIRE: 'bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300',
      AVANCE: 'bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300',
      EXPERT: 'bg-purple-100 dark:bg-purple-900 text-purple-700 dark:text-purple-300',
    };
    return colors[niveau] || colors.DEBUTANT;
  };

  if (loading) return <LoadingSpinner size="sm" />;

  return (
    <div className="p-6 animate-fadeInUp">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold text-gray-800 dark:text-white">Compétences</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="btn-primary"
        >
          <FaPlus />
          <span>Ajouter</span>
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="mb-6 p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <input
              type="text"
              placeholder="Nom de la compétence*"
              value={formData.nom}
              onChange={(e) => setFormData({ ...formData, nom: e.target.value })}
              className="p-2 border rounded-lg dark:bg-gray-600 dark:text-white"
              required
            />
            <select
              value={formData.niveau}
              onChange={(e) => setFormData({ ...formData, niveau: e.target.value })}
              className="p-2 border rounded-lg dark:bg-gray-600 dark:text-white"
            >
              {niveaux.map(niveau => <option key={niveau} value={niveau}>{niveau}</option>)}
            </select>
          </div>
          <div className="flex justify-end space-x-2 mt-4">
            <button type="button" onClick={resetForm} className="px-4 py-2 bg-gray-300 dark:bg-gray-600 text-gray-700 dark:text-gray-200 rounded hover:bg-gray-400">Annuler</button>
            <button type="submit" className="btn-primary">
              {editingCompetence ? 'Mettre à jour' : 'Ajouter'}
            </button>
          </div>
        </form>
      )}

      <div className="flex flex-wrap gap-2">
        {competences.length === 0 ? (
          <div className="text-center text-gray-500 dark:text-gray-400 py-8 w-full">Aucune compétence ajoutée</div>
        ) : (
          competences.map(competence => (
            <div key={competence.id} className={`flex items-center space-x-2 px-3 py-2 rounded-full ${getNiveauColor(competence.niveau)} animate-fadeInUp`}>
              <span className="font-medium">{competence.nom}</span>
              <span className="text-xs">{competence.niveau}</span>
              <div className="flex space-x-1 ml-2">
                <button onClick={() => startEdit(competence)} className="hover:opacity-70"><FaEdit size={12} /></button>
                <button onClick={() => handleDelete(competence.id)} className="hover:opacity-70"><FaTrash size={12} /></button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default CompetenceList;