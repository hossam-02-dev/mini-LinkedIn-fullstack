export const ROLES = {
  ETUDIANT: 'ETUDIANT',
  PROFESSEUR: 'PROFESSEUR',
  CHERCHEUR: 'CHERCHEUR',
  ADMIN: 'ADMIN',
};

export const hasRole = (user, requiredRole) => {
  if (!user) return false;
  if (user.role === ROLES.ADMIN) return true;
  return user.role === requiredRole;
};

export const isAdmin = (user) => {
  return user?.role === ROLES.ADMIN;
};

export const getRoleLabel = (role) => {
  const labels = {
    [ROLES.ETUDIANT]: 'Étudiant',
    [ROLES.PROFESSEUR]: 'Professeur',
    [ROLES.CHERCHEUR]: 'Chercheur',
    [ROLES.ADMIN]: 'Administrateur',
  };
  return labels[role] || role;
};