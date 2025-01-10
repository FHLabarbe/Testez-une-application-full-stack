package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String tokenForTestUser;

    @BeforeEach
    void setup() throws Exception {
        String loginPayload = "{\"email\":\"test.user@example.com\",\"password\":\"dummypassword\"}";
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
            .andReturn()
            .getResponse()
            .getContentAsString();

        tokenForTestUser = objectMapper.readTree(response).get("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void shouldReturnUserById() throws Exception {
        mockMvc.perform(get("/api/user/4")
        .header("Authorization", bearer(tokenForTestUser)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test.user@example.com")); 
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/user/999")
        .header("Authorization", bearer(tokenForTestUser)))
        .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/api/user/abc")
        .header("Authorization", bearer(tokenForTestUser)))
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteUserWhenAuthorized() throws Exception {
        mockMvc.perform(delete("/api/user/4")
        .header("Authorization", bearer(tokenForTestUser)))
        .andExpect(status().isOk());
        assertThat(userRepository.findById(4L)).isNotPresent();
    }

    @Test
    void shouldReturnUnauthorizedWhenDeletingAnotherUser() throws Exception {
        mockMvc.perform(delete("/api/user/2")
        .header("Authorization", bearer(tokenForTestUser)))
        .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/user/999")
        .header("Authorization", bearer(tokenForTestUser)))
        .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenIdIsNotNumericInDelete() throws Exception {
        mockMvc.perform(delete("/api/user/abc")
        .header("Authorization", bearer(tokenForTestUser)))
        .andExpect(status().isBadRequest());
    }
}
