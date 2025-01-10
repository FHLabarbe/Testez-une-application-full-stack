package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

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
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;

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
        SessionDto sessionDto = new SessionDto(1L, "Updated Session", new Date(), 1L, "Updated Description", null, null,
                null);
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

    @Test
    void shouldParticipateSuccessfully() throws Exception {
        Long sessionId = 1L;
        Long userToJoin = 3L;

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", sessionId, userToJoin)
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk());

        Session updatedSession = sessionRepository.findById(sessionId).orElse(null);
        assertThat(updatedSession).isNotNull();
        boolean isParticipant = updatedSession.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userToJoin));
        assertThat(isParticipant).isTrue();
    }

    @Test
    void shouldReturn404WhenSessionNotFoundInParticipate() throws Exception {
        mockMvc.perform(post("/api/session/999/participate/2")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenUserNotFoundInParticipate() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/999")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenAlreadyParticipating() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/2")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNoLongerParticipateSuccessfully() throws Exception {
        Long sessionId = 2L;
        Long userId = 3L;

        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", sessionId, userId)
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk());

        Session updatedSession = sessionRepository.findById(sessionId).orElse(null);
        assertThat(updatedSession).isNotNull();
        boolean stillParticipant = updatedSession.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));
        assertThat(stillParticipant).isFalse();
    }

    @Test
    void shouldReturnNotFoundWhenSessionDoesNotExistInNoLongerParticipate() throws Exception {
        mockMvc.perform(delete("/api/session/999/participate/2")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExistInNoLongerParticipate() throws Exception {
        mockMvc.perform(delete("/api/session/1/participate/3")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenIdsAreNonNumeric() throws Exception {
        mockMvc.perform(post("/api/session/abc/participate/abc")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnSessionWhenIdIsValid() throws Exception {
        mockMvc.perform(get("/api/session/1")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session 1"));
    }

    @Test
    void shouldReturnNotFoundWhenSessionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/session/999")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenIdIsNotNumericInFindById() throws Exception {
        mockMvc.perform(get("/api/session/abc")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDeletingNonNumericId() throws Exception {
        mockMvc.perform(delete("/api/session/abc")
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenIdIsNotNumericInUpdate() throws Exception {
        SessionDto dto = new SessionDto(null, "Title", new Date(), 1L, "Desc", null, null, null);
        mockMvc.perform(put("/api/session/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", getAuthorizationHeader()))
                .andExpect(status().isBadRequest());
    }

}
