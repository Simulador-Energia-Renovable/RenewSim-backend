package com.renewsim.backend.security;

import com.renewsim.backend.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.renewsim.backend.exception.UnauthorizedException;

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
        } else {
            throw new UnauthorizedException("Invalid authentication principal");
        }
    }
}
