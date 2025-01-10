package com.openclassrooms.starterjwt.Impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
class UserDetailsImplTest {

    @Test
    void shouldCreateUserDetailsAndCheckFields() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "yoga@studio.com",
                "Admin",
                "Admin",
                true,
                "dummypassword");

        assertThat(userDetails.getUsername()).isEqualTo("yoga@studio.com");
        assertThat(userDetails.getPassword()).isEqualTo("dummypassword");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    void shouldReturnEmptyAuthoritiesIfNotDefined() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                2L, "john@example.com", "John", "Doe", false, "pass");
        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    void shouldUseEquals() {
        UserDetailsImpl userA = new UserDetailsImpl(1L, "a@mail.com", "A", "A", true, "xxx");
        UserDetailsImpl userA2 = new UserDetailsImpl(1L, "a@mail.com", "A", "A", true, "xxx");
        UserDetailsImpl userB = new UserDetailsImpl(2L, "b@mail.com", "B", "B", false, "yyy");

        assertThat(userA).isEqualTo(userA2);
        assertThat(userA).isNotEqualTo(userB);

        assertThat(userA.getAdmin()).isTrue();
    }

}
