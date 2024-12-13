package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void setup() {
        sessionRepository.deleteAll();
    }

    @Test
    void shouldReturnAllSessions() throws Exception {
        sessionRepository.save(new Session(null, "Session 1", null, "Description 1", null, null, null, null));
        sessionRepository.save(new Session(null, "Session 2", null, "Description 2", null, null, null, null));

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnEmptyListWhenNoSessions() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldCreateSession() throws Exception {
        SessionDto sessionDto = new SessionDto(null, "New Session", null, null, "Description", null, null, null);

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Session"))
                .andExpect(jsonPath("$.description").value("Description"));

        assertThat(sessionRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldReturnBadRequestForMissingFieldsInSessionCreation() throws Exception {
        SessionDto sessionDto = new SessionDto(null, null, null, null, "Description", null, null, null);

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateSession() throws Exception {
        Session session = sessionRepository.save(
                new Session(null, "Old Session", null, "Old Description", null, null, null, null));
        SessionDto sessionDto = new SessionDto(null, "Updated Session", null, null, "Updated Description", null, null, null);

        mockMvc.perform(put("/api/session/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Session"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        Session updatedSession = sessionRepository.findById(session.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getName()).isEqualTo("Updated Session");
    }

    @Test
    void shouldReturnBadRequestForMissingFieldsInSessionUpdate() throws Exception {
        Session session = sessionRepository.save(
                new Session(null, "Old Session", null, "Old Description", null, null, null, null));
        SessionDto sessionDto = new SessionDto(null, null, null, null, "Updated Description", null, null, null);

        mockMvc.perform(put("/api/session/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteSession() throws Exception {
        Session session = sessionRepository.save(
                new Session(null, "Session to Delete", null, "Description", null, null, null, null));

        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());

        assertThat(sessionRepository.findById(session.getId())).isEmpty();
    }

    @Test
    void shouldReturnNotFoundForNonExistentSessionDeletion() throws Exception {
        mockMvc.perform(delete("/api/session/999"))
                .andExpect(status().isNotFound());
    }
}
