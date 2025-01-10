package com.openclassrooms.starterjwt.Impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        User user = User.builder()
                .id(10L)
                .email("john@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("encodedPass")
                .admin(false)
                .build();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("john@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john@example.com");
        assertThat(result.getPassword()).isEqualTo("encodedPass");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userDetailsService.loadUserByUsername("unknown@example.com"))
            .isInstanceOf(UsernameNotFoundException.class);
    }
}
