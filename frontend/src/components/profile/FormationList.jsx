import React, { useState, useEffect } from 'react';
import { profileAPI } from '../../api/profileAPI';
import { FaPlus, FaEdit, FaTrash } from 'react-icons/fa';
import toast from 'react-hot-toast';
import { format } from 'date-fns';

const FormationList = ({ profilId, onUpdate }) => {
  const [formations, setFormations] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingFormation, setEditingFormation] = useState(null);
  const [formData, setFormData] = useState({
    diplome: '',
    etablissement: '',
    domaine: '',
    enCours: false,
    dateDebut: '',
    dateFin: '',
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadFormations();
  }, [profilId]);

  const loadFormations = async () => {
    try {
      const response = await profileAPI.getFormations(profilId);
      setFormations(response.data);
    } catch (error) {
      toast.error('Erreur lors du chargement des formations');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingFormation) {
        await profileAPI.updateFormation(editingFormation.id, formData);
        toast.success('Formation mise à jour');
      } else {
        await profileAPI.addFormation(profilId, formData);
        toast.success('Formation ajoutée');
      }
      resetForm();
      loadFormations();
      if (onUpdate) onUpdate();
    } catch (error) {
      toast.error('Erreur lors de l\'enregistrement');
    }
  };

  const handleDelete = async (formationId) => {
    if (window.confirm('Voulez-vous vraiment supprimer cette formation ?')) {
      try {
        await profileAPI.deleteFormation(formationId);
        toast.success('Formation supprimée');
        loadFormations();
        if (onUpdate) onUpdate();
      } catch (error) {
        toast.error('Erreur lors de la suppression');
      }
    }
  };

  const resetForm = () => {
    setFormData({ diplome: '', etablissement: '', domaine: '', enCours: false, dateDebut: '', dateFin: '' });
    setEditingFormation(null);
    setShowForm(false);
  };

  const startEdit = (formation) => {
    setEditingFormation(formation);
    setFormData({
      diplome: formation.diplome,
      etablissement: formation.etablissement,
      domaine: formation.domaine,
      enCours: formation.enCours,
      dateDebut: formation.dateDebut || '',
      dateFin: formation.dateFin || '',
    });
    setShowForm(true);
  };

  if (loading) return <div className="text-center text-gray-500 dark:text-gray-400">Chargement...</div>;

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold text-gray-800 dark:text-white">Formations</h2>
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
            <input type="text" placeholder="Diplôme*" value={formData.diplome} onChange={(e) => setFormData({ ...formData, diplome: e.target.value })} className="p-2 border rounded-lg dark:bg-gray-600 dark:text-white" required />
            <input type="text" placeholder="Établissement*" value={formData.etablissement} onChange={(e) => setFormData({ ...formData, etablissement: e.target.value })} className="p-2 border rounded-lg dark:bg-gray-600 dark:text-white" required />
            <input type="text" placeholder="Domaine*" value={formData.domaine} onChange={(e) => setFormData({ ...formData, domaine: e.target.value })} className="p-2 border rounded-lg dark:bg-gray-600 dark:text-white" required />
            <div className="flex items-center space-x-2">
              <input type="checkbox" checked={formData.enCours} onChange={(e) => setFormData({ ...formData, enCours: e.target.checked })} className="w-4 h-4" />
              <label className="text-gray-700 dark:text-gray-300">En cours</label>
            </div>
            <input type="date" placeholder="Date début" value={formData.dateDebut} onChange={(e) => setFormData({ ...formData, dateDebut: e.target.value })} className="p-2 border rounded-lg dark:bg-gray-600 dark:text-white" />
            <input type="date" placeholder="Date fin" value={formData.dateFin} onChange={(e) => setFormData({ ...formData, dateFin: e.target.value })} className="p-2 border rounded-lg dark:bg-gray-600 dark:text-white" disabled={formData.enCours} />
          </div>
          <div className="flex justify-end space-x-2 mt-4">
            <button type="button" onClick={resetForm} className="px-4 py-2 bg-gray-300 dark:bg-gray-600 text-gray-700 dark:text-gray-200 rounded hover:bg-gray-400">Annuler</button>
            <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
              {editingFormation ? 'Mettre à jour' : 'Ajouter'}
            </button>
          </div>
        </form>
      )}

      {formations.length === 0 ? (
        <div className="text-center text-gray-500 dark:text-gray-400 py-8">Aucune formation ajoutée</div>
      ) : (
        <div className="space-y-4">
          {formations.map(formation => (
            <div key={formation.id} className="border-b pb-4" style={{ borderColor: 'var(--color-border)' }}>
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="font-semibold text-lg text-gray-800 dark:text-white">{formation.diplome}</h3>
                  <p className="text-gray-600 dark:text-gray-300">{formation.etablissement}</p>
                  <p className="text-sm text-gray-500 dark:text-gray-400">{formation.domaine}</p>
                  {(formation.dateDebut || formation.dateFin) && (
                    <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                      {formation.dateDebut && format(new Date(formation.dateDebut), 'MMM yyyy')}
                      {formation.dateDebut && formation.dateFin && ' - '}
                      {formation.dateFin && !formation.enCours && format(new Date(formation.dateFin), 'MMM yyyy')}
                      {formation.enCours && 'Présent'}
                    </p>
                  )}
                </div>
                <div className="flex space-x-2">
                  <button onClick={() => startEdit(formation)} className="text-blue-600 dark:text-blue-400 hover:text-blue-800">
                    <FaEdit />
                  </button>
                  <button onClick={() => handleDelete(formation.id)} className="text-red-600 dark:text-red-400 hover:text-red-800">
                    <FaTrash />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default FormationList;