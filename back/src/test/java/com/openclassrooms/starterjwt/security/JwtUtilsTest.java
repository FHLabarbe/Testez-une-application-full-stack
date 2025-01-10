package com.openclassrooms.starterjwt.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {
    
    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "clefSecrete");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john@example.com");

        String token = jwtUtils.generateJwtToken(authentication);
        assertThat(token).isNotEmpty();

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isTrue();

        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertThat(username).isEqualTo("john@example.com");
    }

    @Test
    void shouldReturnFalseForInvalidSignatureToken() {
        boolean isValid = jwtUtils.validateJwtToken("abc.def.ghi");
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -10000); // -10s
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("john@example.com");
        String token = jwtUtils.generateJwtToken(authentication);

        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000);

        boolean valid = jwtUtils.validateJwtToken(token);
        assertThat(valid).isFalse();
    }

}
