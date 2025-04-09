package com.renewsim.backend.security;

import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.renewsim.backend.config.SpringContext;
import com.renewsim.backend.exception.UnauthorizedException;
import org.springframework.security.oauth2.jwt.Jwt;



public class AuthUtils {

    private AuthUtils() {

    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }
    
        Object principal = authentication.getPrincipal();
    
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser();
        } else if (principal instanceof Jwt jwt) {
            String username = jwt.getClaimAsString("sub"); // <-- Asegúrate que tu claim sea "sub" o el que uses
            // Aquí busca el usuario en la base de datos con el username
            return SpringContext.getBean(UserRepository.class)
                    .findByUsername(username)
                    .orElseThrow(() -> new UnauthorizedException("User not found"));
        } else {
            throw new UnauthorizedException("Invalid authentication principal");
        }
    }   

}
    