package com.renewsim.backend.auth;

import com.renewsim.backend.config.SpringContext;
import com.renewsim.backend.exception.UnauthorizedException;
import com.renewsim.backend.security.UserDetailsImpl;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


public final class AuthUtils {

    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

    private AuthUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Attempt to access current user without authentication");
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            logger.debug("Current user retrieved from UserDetailsImpl: {}", userDetails.getUsername());
            return userDetails.getUser();
        } else if (principal instanceof Jwt jwt) {
            String username = jwt.getClaimAsString("sub");
            logger.debug("Current user retrieved from JWT: {}", username);

            return SpringContext.getBean(UserRepository.class)
                    .findByUsername(username)
                    .orElseThrow(() -> new UnauthorizedException("User not found"));
        } else {
            logger.warn("Invalid authentication principal: {}", principal.getClass().getName());
            throw new UnauthorizedException("Invalid authentication principal");
        }
    }
}

    