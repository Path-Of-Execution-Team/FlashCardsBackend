package com.flashcards.application.service;

import com.flashcards.application.mapper.UserMapper;
import com.flashcards.domain.model.User;
import com.flashcards.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void shouldLoadUserByEmail_whenUserExists() {
        // given
        User user = new User();
        user.setEmail("test@email.com");
        user.setUsername("testUser");
        user.setPasswordHash("hashedPassword");
        when(userRepository.findByEmail("test@email.com"))
            .thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@email.com");

        // then
        assertThat(userDetails.getUsername()).isEqualTo("testUser");
        assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");
        assertThat(userDetails.getAuthorities()).isEmpty();

        verify(userRepository).findByEmail("test@email.com");
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void shouldLoadUserByUsername_whenUserExists() {
        // given
        User user = new User();
        user.setEmail("test@email.com");
        user.setUsername("testUser");
        user.setPasswordHash("hashedPassword");

        when(userRepository.findByEmail("testUser")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testUser");

        // then
        assertThat(userDetails.getUsername()).isEqualTo("testUser");
        assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");
        assertThat(userDetails.getAuthorities()).isEmpty();

        verify(userRepository).findByEmail("testUser");
        verify(userRepository).findByUsername("testUser");
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        // given
        when(userRepository.findByEmail("ghost")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("ghost"))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("User not found:ghost");

        verify(userRepository).findByEmail("ghost");
        verify(userRepository).findByUsername("ghost");
    }
}
