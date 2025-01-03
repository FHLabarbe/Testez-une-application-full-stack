package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnUserById() {
        Long userId = 2L;
        User user = User.builder()
                .email("john.doe@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("dummypassword")
                .admin(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findById(userId);

        assertThat(result).isEqualTo(user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldReturnNullWhenUserDoesNotExist() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        User result = userService.findById(userId);
        assertThat(result).isNull();
        verify(userRepository, times(1)).findById(userId);
    }

}
