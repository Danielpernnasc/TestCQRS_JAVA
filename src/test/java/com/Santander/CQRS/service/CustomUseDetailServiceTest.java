package com.Santander.CQRS.service;
import com.Santander.CQRS.model.User;
import com.Santander.CQRS.repository.UserRepository;
import com.Santander.CQRS.service.CustomUseDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CustomUseDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUseDetailsService service;

    @Test
    @DisplayName("loadUserByUsername - when user exists, returns valid UserDetails")
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        // arrange
        User userMock = mock(User.class);
        when(userMock.getLogin()).thenReturn("maria");
        when(userMock.getPasswordHash()).thenReturn("$2a$10$somehashedpassword"); // exemplo hash bcrypt

        when(userRepository.findByLogin("maria")).thenReturn(Optional.of(userMock));

        // act
        UserDetails details = service.loadUserByUsername("maria");

        // assert
        assertThat(details).isNotNull();
        assertThat(details.getUsername()).isEqualTo("maria");
        assertThat(details.getPassword()).isEqualTo("$2a$10$somehashedpassword");
        assertThat(details.getAuthorities()).isNotEmpty();
        assertThat(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER"))).isTrue();

        // verify repository interaction
        verify(userRepository, times(1)).findByLogin("maria");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("loadUserByUsername - when user not found, throws UsernameNotFoundException")
    void loadUserByUsername_whenUserNotFound_throwsException() {
        // arrange
        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        // act + assert
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unknown"));

        verify(userRepository, times(1)).findByLogin("unknown");
        verifyNoMoreInteractions(userRepository);
    }
}