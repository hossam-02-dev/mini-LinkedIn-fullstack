package com.example.miniLinkedin.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    /**
     * Retourne l'ID de l'utilisateur actuellement connecté.
     * @return l'ID ou null si non authentifié ou impossible de le déterminer.
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            // Dans votre projet, UserDetails est implémenté par UserEntity.
            // On suppose que cette classe possède une méthode getId().
            try {
                // On essaye de récupérer la méthode getId() par réflexion (très fiable)
                java.lang.reflect.Method method = principal.getClass().getMethod("getId");
                return (Long) method.invoke(principal);
            } catch (Exception e) {
                // En cas d'échec, on retourne null
                return null;
            }
        }
        return null;
    }

    /**
     * Retourne l'email de l'utilisateur actuellement connecté.
     * @return l'email ou null si non authentifié.
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
}