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
            String loginPayload = "{\"email\":\"test.user@example.com\",\"password\":\"password\"}";
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
            String loginPayload = "{\"email\":\"test.user@example.com\",\"password\":\"password\"}";

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginPayload))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldReturnAllSessions() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        sessionRepository.saveAndFlush(new Session(null, "Session 1", new Date(), "Description 1", teacher, null, null, null));
        sessionRepository.saveAndFlush(new Session(null, "Session 2", new Date(), "Description 2", teacher, null, null, null));

        mockMvc.perform(get("/api/session")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnEmptyListWhenNoSessions() throws Exception {
        mockMvc.perform(get("/api/session")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
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

        assertThat(sessionRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldReturnBadRequestForMissingFieldsInSessionCreation() throws Exception {
            SessionDto sessionDto = new SessionDto(null, "New Session", new Date(), 1L, "Description", null, null, null);

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto))
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateSession() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Session session = sessionRepository.saveAndFlush(
                new Session(null, "Old Session", null, "Old Description", teacher, null, null, null));
        SessionDto sessionDto = new SessionDto(null, "Updated Session", new Date(), 1L, "Updated Description", null, null, null);

        mockMvc.perform(put("/api/session/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto))
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Session"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        Session updatedSession = sessionRepository.findById(session.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getName()).isEqualTo("Updated Session");
    }

    @Test
    void shouldReturnBadRequestForMissingFieldsInSessionUpdate() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Session session = sessionRepository.saveAndFlush(
                new Session(null, "Old Session", null, "Old Description", teacher, null, null, null));
        SessionDto sessionDto = new SessionDto(null, "New Session", new Date(), 1L, "Description", null, null, null);

        mockMvc.perform(put("/api/session/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto))
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteSession() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Session session = sessionRepository.saveAndFlush(
                new Session(null, "Session to Delete", null, "Description", teacher, null, null, null));

        mockMvc.perform(delete("/api/session/" + session.getId())
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk());

        assertThat(sessionRepository.findById(session.getId())).isEmpty();
    }

    @Test
    void shouldReturnNotFoundForNonExistentSessionDeletion() throws Exception {
        mockMvc.perform(delete("/api/session/999")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isNotFound());
    }
}
