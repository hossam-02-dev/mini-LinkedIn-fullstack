import React, { useState, useEffect } from 'react';
import { profileAPI } from '../../api/profileAPI';
import { FaEye, FaUserCircle } from 'react-icons/fa';

const ProfileStats = () => {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await profileAPI.getMyStats();
        setStats(response.data);
      } catch (error) {
        console.error("Erreur lors du chargement des statistiques", error);
      }
    };
    fetchStats();
  }, []);

  if (!stats) return null;

  return (
    <div className="card p-4 bg-white shadow rounded-lg">
      <div className="flex items-center gap-3 mb-4 border-b pb-3">
        <FaEye className="text-blue-600 text-2xl" />
        <h3 className="font-bold text-lg text-gray-800">Vues de votre profil</h3>
      </div>
      
      <div className="text-center mb-6">
        <span className="text-4xl font-extrabold text-blue-600">{stats.totalViewsLast30Days}</span>
        <p className="text-gray-500 text-sm">vues ces 30 derniers jours</p>
      </div>

      {stats.recentViewers && stats.recentViewers.length > 0 && (
        <div>
          <p className="text-xs font-semibold text-gray-400 uppercase mb-3">Derniers visiteurs</p>
          <div className="flex flex-col gap-3">
            {stats.recentViewers.map((viewer, index) => (
              <div key={index} className="flex items-center gap-3">
                <FaUserCircle className="text-gray-300 text-3xl" />
                <div>
                  <p className="text-sm font-bold text-gray-800">{viewer.firstName} {viewer.lastName}</p>
                  <p className="text-xs text-gray-500 truncate w-40">{viewer.title}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfileStats;