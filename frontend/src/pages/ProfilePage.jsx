import React, { useState, useEffect } from 'react';
import { profileAPI } from '../api/profileAPI';
import ProfileHeader from '../components/profile/ProfileHeader';
import FormationList from '../components/profile/FormationList';
import CompetenceList from '../components/profile/CompetenceList';
import ProjectList from '../components/profile/ProjectList';
import LoadingSpinner from '../components/common/LoadingSpinner';
import toast from 'react-hot-toast';

import ProfileStats from '../components/profile/ProfileStats';

const ProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const response = await profileAPI.getMyProfile();
      setProfile(response.data);
    } catch (error) {
      toast.error('Impossible de charger le profil');
    } finally {
      setLoading(false);
    }
  };

  const handleProfileUpdate = (updatedProfile) => {
    setProfile(updatedProfile);
    toast.success('Profil mis à jour');
  };

  const reloadProfile = async () => {
    await loadProfile();
  };

  if (loading) return <LoadingSpinner />;

  if (!profile) return (
    <div className="page-shell flex items-center justify-center">
      <p className="empty-state-text">Aucun profil trouvé</p>
    </div>
  );

  return (
    <div className="page-shell">
      <div className="page-glow" aria-hidden="true" />

      <div className="page-container-md">
        <div className="card overflow-hidden mb-5">
          <ProfileHeader
            profile={profile}
            onUpdate={handleProfileUpdate}
            onReload={reloadProfile}
          />
        </div>

        <div className="flex flex-col gap-4">
          <div className="card card-interactive overflow-hidden">
            <ProfileStats />
          </div>
          <div className="card card-interactive overflow-hidden">
            <FormationList profilId={profile.id} onUpdate={loadProfile} />
          </div>
          <div className="card card-interactive overflow-hidden">
            <CompetenceList onUpdate={loadProfile} />
          </div>
          <div className="card card-interactive overflow-hidden">
            <ProjectList userId={profile.userId} isOwner={true} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
