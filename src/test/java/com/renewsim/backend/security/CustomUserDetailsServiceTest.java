package com.renewsim.backend.security;

import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    private static final String TEST_USERNAME = "testuser";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("should return UserDetailsImpl when user is found")
    void shouldReturnUserDetailsWhenUserIsFound() {
        User mockUser = new User();
        mockUser.setUsername(TEST_USERNAME);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(TEST_USERNAME);

        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals(TEST_USERNAME, userDetails.getUsername(), "Username should match");
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    @DisplayName("should throw UsernameNotFoundException when user is not found")
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(TEST_USERNAME),
                "Expected UsernameNotFoundException");

        assertEquals("User not found with username: " + TEST_USERNAME, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }
}
