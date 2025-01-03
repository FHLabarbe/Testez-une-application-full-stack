package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void setup() throws Exception {
            String loginPayload = "{\"email\":\"test.user@example.com\",\"password\":\"dummypassword\"}";
            String response = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginPayload))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            token = objectMapper.readTree(response).get("token").asText();
    }

    private String token;

    private String getAuthorizationHeader() {
            return "Bearer " + token;
    }
    
    @Test
    void shouldGenerateTokenForValidUser() throws Exception {
            String loginPayload = "{\"email\":\"test.user@example.com\",\"password\":\"dummypassword\"}";

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginPayload))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldReturnAllSessions() throws Exception {
        mockMvc.perform(get("/api/session")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldCreateSession() throws Exception {
        SessionDto sessionDto = new SessionDto(null, "New Session", new Date(), 1L, "Description", null, null, null);

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto))
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Session"))
                .andExpect(jsonPath("$.description").value("Description"));

        assertThat(sessionRepository.findAll()).hasSize(3);
    }

    @Test
    void shouldReturnBadRequestForMissingFieldsInSessionCreation() throws Exception {
        SessionDto sessionDto = new SessionDto(null, null, null, 1L, "Description", null, null, null);

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto))
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateSession() throws Exception {
        SessionDto sessionDto = new SessionDto(1L, "Updated Session", new Date(), 1L, "Updated Description", null, null, null);
        mockMvc.perform(put("/api/session/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto))
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Session"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        Session updatedSession = sessionRepository.findById(1L).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getName()).isEqualTo("Updated Session");
    }

    @Test
    void shouldReturnBadRequestForMissingFieldsInSessionUpdate() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        SessionDto sessionDto = new SessionDto(1L, "", new Date(), 1L, "Description", null, null, null);

        mockMvc.perform(put("/api/session/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto))
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteSession() throws Exception {
        mockMvc.perform(delete("/api/session/1")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk());

        assertThat(sessionRepository.findById(1L)).as("The session should be deleted").isNotPresent();
    }

    @Test
    void shouldReturnNotFoundForNonExistentSessionDeletion() throws Exception {
        mockMvc.perform(delete("/api/session/999")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isNotFound());
    }
}
